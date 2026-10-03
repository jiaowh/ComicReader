package dev.jiaowh.comicreader.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.jiaowh.comicreader.data.ComicRepository
import dev.jiaowh.comicreader.data.remote.Chapter
import dev.jiaowh.comicreader.ui.ReaderRoute
import dev.jiaowh.comicreader.ui.common.appViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class ReaderUiState(
    val comicTitle: String = "",
    val chapter: Chapter? = null,
    val pages: List<String> = emptyList(),
    val startPage: Int = 0,
    val previous: Chapter? = null,
    val next: Chapter? = null,
    val loading: Boolean = true,
    val error: Throwable? = null,
    /** False when reading a comic that isn't in the library, so progress isn't saved. */
    val tracking: Boolean = false,
    /** Set when this chapter ends a step of its series' reading order. */
    val orderNext: OrderNext? = null,
)

/** The next step in the reading order. [url]/[chapterId] are null if that step isn't linked to a comic yet. */
data class OrderNext(
    val seriesId: Long,
    val seriesName: String,
    val label: String,
    val url: String?,
    val chapterId: Int?,
    val page: Int,
)

class ReaderViewModel(
    private val repository: ComicRepository,
    route: ReaderRoute,
    siteVerified: SharedFlow<Unit>,
) : ViewModel() {

    private val url = route.url
    private val _state = MutableStateFlow(ReaderUiState())
    val state = _state.asStateFlow()

    private var chapters: List<Chapter> = emptyList()
    private var newsId = 0
    private var inLibrary = false
    private var comicTitle = ""

    private var requestedChapterId = route.chapterId
    private var requestedPage = route.page
    private var loadJob: Job? = null

    init {
        open(route.chapterId, route.page)
        viewModelScope.launch { siteVerified.collect { if (_state.value.error != null) retry() } }
    }

    fun retry() = open(requestedChapterId, requestedPage)

    /** The URL of the comic being read, to tell same-comic and cross-comic jumps apart. */
    val comicUrl: String get() = url

    fun open(chapterId: Int, page: Int = 0) {
        requestedChapterId = chapterId
        requestedPage = page
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                loadComic()
                val index = chapters.indexOfFirst { it.id == chapterId }
                if (index == -1) throw IOException("Chapter not found. Try refreshing the comic.")
                val chapter = chapters[index]
                val pages = repository.chapterImages(newsId, chapter.id)
                if (pages.isEmpty()) throw IOException("This chapter has no pages.")
                _state.value = ReaderUiState(
                    comicTitle = comicTitle,
                    chapter = chapter,
                    pages = pages,
                    startPage = page.coerceIn(0, pages.lastIndex),
                    previous = chapters.getOrNull(index - 1),
                    next = chapters.getOrNull(index + 1),
                    loading = false,
                    tracking = inLibrary,
                    orderNext = orderNextAfter(chapter),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e) }
            }
        }
    }

    /** If [chapter] is the last one in its reading-order step, the step that follows it. */
    private suspend fun orderNextAfter(chapter: Chapter): OrderNext? {
        val seriesId = repository.libraryComic(url)?.seriesId ?: return null
        val order = repository.readingOrderSnapshot(seriesId) ?: return null
        val step = order.stepContaining(url, chapter.id) ?: return null
        if (step.chapters.last().id != chapter.id) return null
        val next = order.stepAfter(step.step.id) ?: return null
        val target = next.openTarget.takeIf { next.isLinked }
        return OrderNext(
            seriesId = seriesId,
            seriesName = repository.series.first().firstOrNull { it.id == seriesId }?.name.orEmpty(),
            label = next.step.label,
            url = next.comic?.url.takeIf { target != null },
            chapterId = target?.first,
            page = target?.second ?: 0,
        )
    }

    private suspend fun loadComic() {
        if (chapters.isNotEmpty() && newsId != 0) return
        val saved = repository.libraryComic(url)
        inLibrary = saved != null
        if (saved != null) {
            newsId = saved.newsId
            comicTitle = saved.title
            chapters = repository.libraryChapters(url)
        }
        if (chapters.isEmpty() || newsId == 0) {
            val details = repository.details(url)
            newsId = details.newsId
            comicTitle = details.title
            chapters = details.chapters
        }
    }

    /** [page] may equal pages.size for the end-of-chapter screen. */
    fun onPageChanged(page: Int) {
        val current = _state.value
        val chapter = current.chapter ?: return
        if (!inLibrary || current.pages.isEmpty()) return
        val lastIndex = current.pages.lastIndex
        viewModelScope.launch {
            repository.saveProgress(
                url = url,
                chapterId = chapter.id,
                page = page.coerceAtMost(lastIndex),
                pageCount = current.pages.size,
                finished = page >= lastIndex,
            )
        }
    }

    companion object {
        val Factory = appViewModelFactory { container, handle ->
            ReaderViewModel(container.repository, handle.toRoute<ReaderRoute>(), container.siteVerified)
        }
    }
}
