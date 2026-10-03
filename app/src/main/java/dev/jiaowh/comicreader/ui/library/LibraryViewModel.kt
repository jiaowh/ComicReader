package dev.jiaowh.comicreader.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jiaowh.comicreader.data.ComicRepository
import dev.jiaowh.comicreader.data.ReadingOrderState
import dev.jiaowh.comicreader.data.SeedStatus
import dev.jiaowh.comicreader.data.SeriesChoice
import dev.jiaowh.comicreader.data.StarterSeriesSeeder
import dev.jiaowh.comicreader.data.db.LibraryItem
import dev.jiaowh.comicreader.data.db.SeriesEntity
import dev.jiaowh.comicreader.ui.common.appViewModelFactory
import dev.jiaowh.comicreader.ui.common.pluralize
import dev.jiaowh.comicreader.ui.common.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface LibraryMessage {
    data class Info(val text: String) : LibraryMessage
    /** Update check was stopped by the site's bot protection. */
    data object NeedsVerification : LibraryMessage
}

data class SeriesGroup(
    val series: SeriesEntity,
    val items: List<LibraryItem>,
    /** Present when the series has a reading order. */
    val order: ReadingOrderState? = null,
) {
    val newChapters get() = items.sumOf { it.comic.newChapterCount }
}

data class LibraryContent(
    val groups: List<SeriesGroup>,
    /** Comics not in any series. */
    val ungrouped: List<LibraryItem>,
) {
    val isEmpty get() = groups.isEmpty() && ungrouped.isEmpty()
}

class LibraryViewModel(
    private val repository: ComicRepository,
    private val seeder: StarterSeriesSeeder,
    siteVerified: SharedFlow<Unit>,
) : ViewModel() {

    /** Null while the first load from the database is pending. */
    val content: StateFlow<LibraryContent?> = combine(
        repository.library,
        repository.series,
        repository.readingOrders,
    ) { items, series, orders ->
        val bySeries = items.groupBy { it.comic.seriesId }
        val knownIds = series.mapTo(HashSet()) { it.id }
        LibraryContent(
            groups = series.map { entry ->
                val order = orders[entry.id]
                var groupItems = bySeries[entry.id].orEmpty()
                if (order != null) {
                    // Show the comics in the order they're read.
                    val firstStep = HashMap<String, Int>()
                    order.steps.forEachIndexed { index, step -> step.step.comicUrl?.let { firstStep.putIfAbsent(it, index) } }
                    groupItems = groupItems.sortedBy { firstStep[it.comic.url] ?: Int.MAX_VALUE }
                }
                SeriesGroup(entry, groupItems, order)
            },
            // A dangling seriesId (shouldn't happen) falls back to ungrouped rather than vanishing.
            ungrouped = items.filter { it.comic.seriesId == null || it.comic.seriesId !in knownIds },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val series = repository.series.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _message = MutableStateFlow<LibraryMessage?>(null)
    val message = _message.asStateFlow()

    private val _seedStatus = MutableStateFlow<SeedStatus>(SeedStatus.Idle)
    val seedStatus = _seedStatus.asStateFlow()

    init {
        seedStarterSeries()
        viewModelScope.launch {
            siteVerified.collect { if (_seedStatus.value == SeedStatus.BlockedByProtection) seedStarterSeries() }
        }
    }

    private fun seedStarterSeries() {
        viewModelScope.launch {
            seeder.seedIfNeeded { status ->
                _seedStatus.value = status
                if (status == SeedStatus.BlockedByProtection) _message.value = LibraryMessage.NeedsVerification
            }
        }
    }

    fun refresh() {
        if (_refreshing.value) return
        viewModelScope.launch {
            _refreshing.value = true
            try {
                val result = repository.checkForUpdates()
                _message.value = when {
                    result.blockedByProtection -> LibraryMessage.NeedsVerification
                    result.updated.isNotEmpty() -> LibraryMessage.Info(
                        "New chapters in ${pluralize(result.updated.size, "comic")}",
                    )
                    result.failed > 0 -> LibraryMessage.Info("Couldn't check ${pluralize(result.failed, "comic")}")
                    else -> LibraryMessage.Info("No new chapters")
                }
            } catch (e: Exception) {
                _message.value = LibraryMessage.Info(e.userMessage())
            } finally {
                _refreshing.value = false
            }
        }
    }

    fun messageShown() {
        _message.value = null
    }

    fun removeComic(item: LibraryItem) {
        viewModelScope.launch {
            repository.removeFromLibrary(item.comic.url)
            _message.value = LibraryMessage.Info("Removed ${item.comic.title}")
        }
    }

    fun moveToSeries(item: LibraryItem, choice: SeriesChoice) {
        viewModelScope.launch { repository.setComicSeries(item.comic.url, repository.resolveSeries(choice)) }
    }

    fun createSeries(name: String) {
        viewModelScope.launch { repository.createSeries(name) }
    }

    fun renameSeries(series: SeriesEntity, name: String) {
        viewModelScope.launch { repository.renameSeries(series.id, name) }
    }

    fun toggleCollapsed(series: SeriesEntity) {
        viewModelScope.launch { repository.setSeriesCollapsed(series.id, !series.collapsed) }
    }

    fun deleteSeries(series: SeriesEntity, deleteComics: Boolean) {
        viewModelScope.launch {
            repository.deleteSeries(series.id, deleteComics)
            _message.value = LibraryMessage.Info("Deleted ${series.name}")
        }
    }

    companion object {
        val Factory = appViewModelFactory { container, _ ->
            LibraryViewModel(container.repository, container.starterSeriesSeeder, container.siteVerified)
        }
    }
}
