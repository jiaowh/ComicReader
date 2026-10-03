package dev.jiaowh.comicreader.data

import android.content.Context
import dev.jiaowh.comicreader.data.net.SiteProtectionException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A series the app sets up on first launch. Each entry in [titles] is searched on BatCave, and
 * every result whose title starts with it is added (so "Absolute Batman" also picks up its
 * annuals and specials).
 */
data class StarterSeries(val key: String, val name: String, val titles: List<String>)

val STARTER_SERIES = listOf(
    StarterSeries(
        key = "dc_absolute_v1",
        name = "DC Absolute",
        titles = listOf(
            "Absolute Batman",
            "Absolute Superman",
            "Absolute Wonder Woman",
            "Absolute Flash",
            "Absolute Green Lantern",
            "Absolute Martian Manhunter",
            "Absolute Evil",
        ),
    ),
)

sealed interface SeedStatus {
    data object Idle : SeedStatus
    data class Running(val seriesName: String, val done: Int, val total: Int) : SeedStatus
    data object BlockedByProtection : SeedStatus
}

/** Populates [STARTER_SERIES] once. Retries on later launches until a run finishes without errors. */
class StarterSeriesSeeder(context: Context, private val repository: ComicRepository) {
    private val prefs = context.getSharedPreferences("starter_series", Context.MODE_PRIVATE)
    private val mutex = Mutex()

    suspend fun seedIfNeeded(onStatus: (SeedStatus) -> Unit) = mutex.withLock {
        for (starter in STARTER_SERIES) {
            if (prefs.getBoolean(starter.key, false)) continue
            val complete = try {
                seed(starter, onStatus)
            } catch (e: SiteProtectionException) {
                onStatus(SeedStatus.BlockedByProtection)
                return@withLock
            }
            if (complete) prefs.edit().putBoolean(starter.key, true).apply()
        }
        onStatus(SeedStatus.Idle)
    }

    /** Returns true if every title was searched successfully. */
    private suspend fun seed(starter: StarterSeries, onStatus: (SeedStatus) -> Unit): Boolean {
        val seriesId = repository.createSeries(starter.name)
        var allOk = true
        starter.titles.forEachIndexed { index, title ->
            onStatus(SeedStatus.Running(starter.name, index, starter.titles.size))
            try {
                val wanted = title.normalized()
                val matches = repository.search(title, 1).comics.filter { it.title.normalized().startsWith(wanted) }
                for (match in matches) {
                    val existing = repository.libraryComic(match.url)
                    when {
                        existing == null -> {
                            delay(REQUEST_SPACING_MS)
                            repository.addToLibrary(repository.details(match.url), seriesId)
                        }
                        existing.seriesId == null -> repository.setComicSeries(match.url, seriesId)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: SiteProtectionException) {
                throw e
            } catch (e: Exception) {
                allOk = false
            }
            delay(REQUEST_SPACING_MS)
        }
        return allOk
    }

    private fun String.normalized() =
        lowercase().replace(Regex("[^a-z0-9 ]"), " ").replace(Regex("\\s+"), " ").trim()

    private companion object {
        const val REQUEST_SPACING_MS = 500L
    }
}
