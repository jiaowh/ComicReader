package dev.jiaowh.comicreader.ui.comic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.jiaowh.comicreader.data.ComicRepository
import dev.jiaowh.comicreader.data.SeriesChoice
import dev.jiaowh.comicreader.data.db.ComicEntity
import dev.jiaowh.comicreader.data.remote.Chapter
import dev.jiaowh.comicreader.data.remote.ComicDetails
import dev.jiaowh.comicreader.ui.ComicRoute
import dev.jiaowh.comicreader.ui.common.appViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Where the reader should open, and how to label the button. */
data class ReadTarget(val chapter: Chapter, val page: Int, val label: String)

data class ComicUiState(
    val url: String,
    val title: String = "",
    val coverUrl: String? = null,
    val publisher: String? = null,
    val year: String? = null,
    val status: String? = null,
    val description: String? = null,
    /** Oldest first. */
    val chapters: List<Chapter> = emptyList(),
    val progress: ComicEntity? = null,
    val inLibrary: Boolean = false,
    val canAdd: Boolean = false,
    val loading: Boolean = true,
    val error: Throwable? = null,
) {
    private val currentIndex get() = chapters.indexOfFirst { it.id == progress?.lastReadChapterId }

    /** Null when caught up (or nothing to read). */
    val readTarget: ReadTarget?
        get() {
            if (chapters.isEmpty()) return null
            val index = currentIndex
            val entry = progress
            return when {
                index == -1 || entry == null -> ReadTarget(chapters.first(), 0, "Start reading")
                !entry.lastChapterFinished -> ReadTarget(chapters[index], entry.lastReadPage, "Continue ${chapters[index].title}")
                index < chapters.lastIndex -> ReadTarget(chapters[index + 1], 0, "Read ${chapters[index + 1].title}")
                else -> null
            }
        }

    fun isRead(chapter: Chapter): Boolean {
        val index = currentIndex
        if (index == -1) return false
        val position = chapters.indexOf(chapter)
        return position < index || (position == index && progress?.lastChapterFinished == true)
    }

    fun isCurrent(chapter: Chapter) =
        progress?.let { it.lastReadChapterId == chapter.id && !it.lastChapterFinished } == true
}

class ComicViewModel(
    private val repository: ComicRepository,
    private val url: String,
    siteVerified: SharedFlow<Unit>,
) : ViewModel() {

    private val remote = MutableStateFlow<ComicDetails?>(null)
    private val loading = MutableStateFlow(true)
    private val error = MutableStateFlow<Throwable?>(null)

    val state: StateFlow<ComicUiState> = combine(
        repository.comic(url),
        repository.chapters(url),
        remote,
        loading,
        error,
    ) { entry, savedChapters, details, isLoading, failure ->
        ComicUiState(
            url = url,
            title = details?.title ?: entry?.title.orEmpty(),
            coverUrl = details?.coverUrl ?: entry?.coverUrl,
            publisher = details?.publisher ?: entry?.publisher,
            year = details?.year,
            status = details?.status ?: entry?.status,
            description = details?.description,
            chapters = if (entry != null && savedChapters.isNotEmpty()) savedChapters else details?.chapters.orEmpty(),
            progress = entry,
            inLibrary = entry != null,
            canAdd = details != null,
            loading = isLoading,
            error = failure,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ComicUiState(url))

    init {
        load()
        viewModelScope.launch { siteVerified.collect { if (error.value != null) load() } }
    }

    fun load() {
        viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                val details = repository.details(url, refresh = true)
                remote.value = details
                repository.applyDetails(details)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = e
            } finally {
                loading.value = false
                // The user is looking at the chapter list, so nothing is "new" any more.
                repository.markSeen(url)
            }
        }
    }

    val series = repository.series.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addToLibrary(choice: SeriesChoice = SeriesChoice.Existing(null)) {
        val details = remote.value ?: return
        viewModelScope.launch { repository.addToLibrary(details, repository.resolveSeries(choice)) }
    }

    fun setSeries(choice: SeriesChoice) {
        viewModelScope.launch { repository.setComicSeries(url, repository.resolveSeries(choice)) }
    }

    fun removeFromLibrary() {
        viewModelScope.launch { repository.removeFromLibrary(url) }
    }

    /** Marks [chapter] and everything before it as read. */
    fun markReadUpTo(chapter: Chapter) {
        viewModelScope.launch { repository.saveProgress(url, chapter.id, 0, 0, finished = true) }
    }

    /** Marks [chapter] and everything after it as unread. */
    fun markUnreadFrom(chapter: Chapter) {
        val chapters = state.value.chapters
        val previous = chapters.getOrNull(chapters.indexOf(chapter) - 1)
        viewModelScope.launch { repository.saveProgress(url, previous?.id, 0, 0, finished = previous != null) }
    }

    companion object {
        val Factory = appViewModelFactory { container, handle ->
            ComicViewModel(container.repository, handle.toRoute<ComicRoute>().url, container.siteVerified)
        }
    }
}
