package dev.jiaowh.comicreader.data

import androidx.room.withTransaction
import dev.jiaowh.comicreader.data.db.AppDatabase
import dev.jiaowh.comicreader.data.db.ComicEntity
import dev.jiaowh.comicreader.data.db.ReadingStepEntity
import dev.jiaowh.comicreader.data.db.SeriesEntity
import dev.jiaowh.comicreader.data.db.toChapter
import dev.jiaowh.comicreader.data.db.toEntities
import dev.jiaowh.comicreader.data.net.SiteProtectionException
import dev.jiaowh.comicreader.data.remote.BatcaveApi
import dev.jiaowh.comicreader.data.remote.Chapter
import dev.jiaowh.comicreader.data.remote.ComicDetails
import dev.jiaowh.comicreader.data.remote.ComicSummary
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    private val resolveMutex = Mutex()

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
    suspend fun createSeries(name: String, collapsed: Boolean = false): Long {
        val trimmed = name.trim()
        dao.seriesByName(trimmed)?.let { return it.id }
        return dao.insertSeries(SeriesEntity(name = trimmed, collapsed = collapsed))
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

    // --- Reading orders ---

    /** Every series' reading order, keyed by series id. Series without steps are absent. */
    val readingOrders: Flow<Map<Long, ReadingOrderState>> = combine(
        dao.allStepsFlow(),
        dao.stepComicsFlow(),
        dao.stepChaptersFlow(),
    ) { steps, comics, chapters ->
        val comicsByUrl = comics.associateBy { it.url }
        val chaptersByComic = chapters.groupBy { it.comicUrl }.mapValues { (_, list) -> list.sortedBy { it.sortIndex } }
        steps.groupBy { it.seriesId }.mapValues { (seriesId, list) ->
            buildReadingOrder(seriesId, list, comicsByUrl, chaptersByComic)
        }
    }

    fun readingOrder(seriesId: Long): Flow<ReadingOrderState?> = readingOrders.map { it[seriesId] }

    suspend fun readingOrderSnapshot(seriesId: Long): ReadingOrderState? = readingOrders.first()[seriesId]

    suspend fun insertStepsIfEmpty(seriesId: Long, seeds: List<StepSeed>) {
        if (dao.steps(seriesId).isNotEmpty()) return
        dao.insertSteps(seeds.mapIndexed { index, seed -> seed.toEntity(seriesId, index) })
    }

    suspend fun seriesIdByName(name: String): Long? = dao.seriesByName(name)?.id

    /**
     * Brings a built-in order's steps in line with [seeds]: adds new steps, updates moved or
     * relabelled ones, and drops removed ones. Steps are matched on what they point at (title, year,
     * issues), so links and the progress they carry survive.
     */
    suspend fun syncSteps(seriesId: Long, seeds: List<StepSeed>) = db.withTransaction {
        if (dao.seriesById(seriesId) == null) return@withTransaction
        val plan = planStepSync(seriesId, dao.steps(seriesId), seeds)
        plan.updates.forEach { dao.updateStep(it) }
        if (plan.inserts.isNotEmpty()) dao.insertSteps(plan.inserts)
        if (plan.deletes.isNotEmpty()) dao.deleteSteps(plan.deletes)
    }

    /**
     * Searches BatCave for every unlinked step and links the best match, adding it to the library
     * under the series. Returns true if every step was attempted without errors.
     */
    suspend fun resolveReadingOrder(
        seriesId: Long,
        onProgress: (done: Int, total: Int) -> Unit = { _, _ -> },
    ): Boolean = resolveMutex.withLock {
        // Under the lock, so a second screen on the same order sees the first one's links.
        val pending = dao.steps(seriesId).filter { it.comicUrl == null && !it.unresolved }
        // Several steps point at the same comic (Metal #1, #2, ...); only look each one up once.
        val found = HashMap<String, String?>()
        var allOk = true
        pending.forEachIndexed { index, step ->
            onProgress(index, pending.size)
            try {
                val key = "${TitleMatch.normalize(step.title)}|${step.year}"
                val url = if (key in found) found[key] else findComic(step).also { found[key] = it }
                if (url == null) {
                    dao.setStepComic(step.id, null, unresolved = true)
                } else {
                    ensureInLibrary(url, seriesId)
                    dao.setStepComic(step.id, url, unresolved = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: SiteProtectionException) {
                throw e
            } catch (e: Exception) {
                allOk = false // leave it pending so the next run retries
            }
        }
        onProgress(pending.size, pending.size)
        allOk
    }

    /** Retries matching for steps that were not found or whose comic was removed. */
    suspend fun rematchReadingOrder(seriesId: Long, onProgress: (Int, Int) -> Unit): Boolean {
        dao.resetUnmatchedSteps(seriesId)
        return resolveReadingOrder(seriesId, onProgress)
    }

    /** Manually links a step to a comic the user picked. */
    suspend fun linkStep(stepId: Long, url: String) {
        val step = dao.step(stepId) ?: return
        ensureInLibrary(url, step.seriesId)
        dao.setStepComic(stepId, url, unresolved = false)
    }

    suspend fun markStepRead(state: StepState) {
        val comic = state.comic ?: return
        val last = state.chapters.lastOrNull() ?: return
        if (state.status == StepStatus.Done) return // don't move progress backwards
        saveProgress(comic.url, last.id, 0, 0, finished = true)
    }

    suspend fun markStepUnread(state: StepState) {
        val comic = state.comic ?: return
        val first = state.chapters.firstOrNull() ?: return
        val previous = dao.chapters(comic.url).lastOrNull { it.sortIndex < first.sortIndex }
        saveProgress(comic.url, previous?.id, 0, 0, finished = previous != null)
    }

    private suspend fun findComic(step: ReadingStepEntity): String? {
        val candidates = searchCandidates(step)
        if (candidates.size == 1) return candidates.single().url

        var best: String? = null
        var bestScore = -1
        for (candidate in candidates.take(MAX_CANDIDATES)) {
            delay(REQUEST_SPACING_MS)
            val chapters = details(candidate.url).chapters
            if (selectIssues(chapters, step.firstIssue, step.lastIssue) { it.number }.isEmpty()) continue
            var score = 0
            if (step.year != null && TitleMatch.year(candidate.title) == step.year) score += 4
            if (step.firstIssue == null && chapters.size == 1) score += 2 // looks like a one-shot
            if (step.lastIssue != null && chapters.any { it.number == step.lastIssue }) score += 2
            if (score > bestScore) {
                best = candidate.url
                bestScore = score
            }
        }
        return best
    }

    /**
     * Search results whose title matches the step, trying each search phrasing until one hits.
     * Exact title matches win; otherwise titles that start with it (e.g. "... The Omega Act Special").
     */
    private suspend fun searchCandidates(step: ReadingStepEntity): List<ComicSummary> {
        val wanted = TitleMatch.normalize(step.title)
        for ((attempt, query) in TitleMatch.queries(step.title).withIndex()) {
            if (attempt > 0) delay(REQUEST_SPACING_MS)
            val sameVolume = search(query, 1).comics
                // A listing with a different explicit year is a different volume.
                .filter { step.year == null || TitleMatch.year(it.title).let { year -> year == null || year == step.year } }
            val exact = sameVolume.filter { TitleMatch.normalize(it.title) == wanted }
            val matches = exact.ifEmpty {
                sameVolume.filter {
                    TitleMatch.normalize(it.title).startsWith("$wanted ") &&
                        // A generic title like "Superman" prefixes too much; insist on the year then.
                        (step.year == null || TitleMatch.year(it.title) == step.year)
                }
            }
            if (matches.isNotEmpty()) {
                return matches.sortedByDescending { step.year != null && TitleMatch.year(it.title) == step.year }
            }
        }
        return emptyList()
    }

    private suspend fun ensureInLibrary(url: String, seriesId: Long) {
        val existing = dao.comic(url)
        when {
            existing == null -> addToLibrary(details(url), seriesId)
            existing.seriesId == null -> dao.setComicSeries(url, seriesId)
        }
    }

    suspend fun markSeen(url: String) = dao.clearNewChapters(url)

    suspend fun saveProgress(url: String, chapterId: Int?, page: Int, pageCount: Int, finished: Boolean) =
        dao.updateProgress(url, chapterId, page, pageCount, finished, System.currentTimeMillis())

    private companion object {
        const val REQUEST_SPACING_MS = 750L
        const val MAX_CANDIDATES = 4
    }
}
