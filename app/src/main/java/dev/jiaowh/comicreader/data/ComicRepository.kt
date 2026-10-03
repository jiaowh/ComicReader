package dev.jiaowh.comicreader.data

import androidx.room.withTransaction
import dev.jiaowh.comicreader.data.db.AppDatabase
import dev.jiaowh.comicreader.data.db.ComicEntity
import dev.jiaowh.comicreader.data.db.SeriesEntity
import dev.jiaowh.comicreader.data.db.toChapter
import dev.jiaowh.comicreader.data.db.toEntities
import dev.jiaowh.comicreader.data.net.SiteProtectionException
import dev.jiaowh.comicreader.data.remote.BatcaveApi
import dev.jiaowh.comicreader.data.remote.Chapter
import dev.jiaowh.comicreader.data.remote.ComicDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

/** Which series to put a comic in: an existing one (null = none) or a new one by name. */
sealed interface SeriesChoice {
    data class Existing(val id: Long?) : SeriesChoice
    data class New(val name: String) : SeriesChoice
}

data class UpdateResult(
    /** Title to number of new chapters, only for comics that got some. */
    val updated: List<Pair<String, Int>>,
    val failed: Int,
    val blockedByProtection: Boolean,
)

class ComicRepository(
    private val api: BatcaveApi,
    private val db: AppDatabase,
) {
    private val dao = db.comicDao()
    private val detailsCache = ConcurrentHashMap<String, ComicDetails>()

    val library = dao.libraryItems()
    val libraryUrls = dao.libraryUrls().map { it.toSet() }

    fun comic(url: String) = dao.comicFlow(url)
    fun chapters(url: String) = dao.chaptersFlow(url).map { list -> list.map { it.toChapter() } }

    suspend fun libraryComic(url: String): ComicEntity? = dao.comic(url)
    suspend fun libraryChapters(url: String): List<Chapter> = dao.chapters(url).map { it.toChapter() }

    suspend fun latest(page: Int) = api.latest(page)
    suspend fun search(query: String, page: Int) = api.search(query, page)
    suspend fun chapterImages(newsId: Int, chapterId: Int) = api.chapterImages(newsId, chapterId)

    suspend fun details(url: String, refresh: Boolean = false): ComicDetails {
        if (!refresh) detailsCache[url]?.let { return it }
        return api.comicDetails(url).also { detailsCache[url] = it }
    }

    suspend fun addToLibrary(details: ComicDetails, seriesId: Long? = null) {
        db.withTransaction {
            dao.insertComic(
                ComicEntity(
                    url = details.url,
                    title = details.title,
                    coverUrl = details.coverUrl,
                    publisher = details.publisher,
                    status = details.status,
                    newsId = details.newsId,
                    addedAt = System.currentTimeMillis(),
                    lastCheckedAt = System.currentTimeMillis(),
                    seriesId = seriesId,
                ),
            )
            dao.replaceChapters(details.url, details.chapters.toEntities(details.url))
        }
    }

    suspend fun removeFromLibrary(url: String) = dao.deleteComic(url)

    // --- Series ---

    val series = dao.seriesFlow()

    /** Returns the chosen series id, creating the series first if needed. */
    suspend fun resolveSeries(choice: SeriesChoice): Long? = when (choice) {
        is SeriesChoice.Existing -> choice.id
        is SeriesChoice.New -> createSeries(choice.name)
    }

    /** Creates a series, or returns the existing one with the same name. */
    suspend fun createSeries(name: String): Long {
        val trimmed = name.trim()
        dao.seriesByName(trimmed)?.let { return it.id }
        return dao.insertSeries(SeriesEntity(name = trimmed))
    }

    suspend fun renameSeries(id: Long, name: String) = dao.renameSeries(id, name.trim())
    suspend fun setSeriesCollapsed(id: Long, collapsed: Boolean) = dao.setSeriesCollapsed(id, collapsed)
    suspend fun deleteSeries(id: Long, deleteComics: Boolean) = dao.deleteSeries(id, deleteComics)
    suspend fun setComicSeries(url: String, seriesId: Long?) = dao.setComicSeries(url, seriesId)

    /**
     * Stores freshly fetched details for a comic that's in the library.
     * Returns how many chapters weren't known before (0 if not in library).
     */
    suspend fun applyDetails(details: ComicDetails): Int = db.withTransaction {
        val existing = dao.comic(details.url) ?: return@withTransaction 0
        val known = dao.chapters(details.url).mapTo(HashSet()) { it.id }
        val newCount = if (known.isEmpty()) 0 else details.chapters.count { it.id !in known }
        dao.updateComic(
            existing.copy(
                title = details.title,
                coverUrl = details.coverUrl ?: existing.coverUrl,
                publisher = details.publisher,
                status = details.status,
                newsId = details.newsId,
                lastCheckedAt = System.currentTimeMillis(),
                newChapterCount = existing.newChapterCount + newCount,
            ),
        )
        dao.replaceChapters(details.url, details.chapters.toEntities(details.url))
        newCount
    }

    /** Checks every library comic for new chapters, politely one at a time. */
    suspend fun checkForUpdates(): UpdateResult {
        val updated = mutableListOf<Pair<String, Int>>()
        var failed = 0
        var blocked = false
        for ((index, comic) in dao.allComics().withIndex()) {
            if (index > 0) delay(REQUEST_SPACING_MS)
            try {
                val added = applyDetails(details(comic.url, refresh = true))
                if (added > 0) updated += comic.title to added
            } catch (e: CancellationException) {
                throw e
            } catch (e: SiteProtectionException) {
                blocked = true
                failed++
                break // every further request would hit the same wall
            } catch (e: Exception) {
                failed++
            }
        }
        return UpdateResult(updated, failed, blocked)
    }

    suspend fun markSeen(url: String) = dao.clearNewChapters(url)

    suspend fun saveProgress(url: String, chapterId: Int?, page: Int, pageCount: Int, finished: Boolean) =
        dao.updateProgress(url, chapterId, page, pageCount, finished, System.currentTimeMillis())

    private companion object {
        const val REQUEST_SPACING_MS = 750L
    }
}
