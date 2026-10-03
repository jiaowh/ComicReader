package dev.jiaowh.comicreader.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jiaowh.comicreader.data.ComicRepository
import androidx.navigation.toRoute
import dev.jiaowh.comicreader.data.remote.ComicSummary
import dev.jiaowh.comicreader.ui.SearchRoute
import dev.jiaowh.comicreader.ui.common.appViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    /** The query the results belong to; blank means "latest releases". */
    val activeQuery: String = "",
    val results: List<ComicSummary> = emptyList(),
    val page: Int = 0,
    val hasNextPage: Boolean = true,
    val loading: Boolean = false,
    val error: Throwable? = null,
)

class SearchViewModel(
    private val repository: ComicRepository,
    private val route: SearchRoute,
    siteVerified: SharedFlow<Unit>,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState(activeQuery = route.query.trim()))
    val state = _state.asStateFlow()

    /** Set when choosing a comic for a reading-order step. */
    val linkLabel: String? = route.linkLabel.takeIf { route.linkStepId != null }

    private val _linking = MutableStateFlow(false)
    val linking = _linking.asStateFlow()

    private val _linked = MutableStateFlow(false)
    val linked = _linked.asStateFlow()

    private val _linkError = MutableStateFlow<Throwable?>(null)
    val linkError = _linkError.asStateFlow()

    fun link(url: String) {
        val stepId = route.linkStepId ?: return
        if (_linking.value) return
        viewModelScope.launch {
            _linking.value = true
            _linkError.value = null
            try {
                repository.linkStep(stepId, url)
                _linked.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _linkError.value = e
            } finally {
                _linking.value = false
            }
        }
    }

    fun linkErrorShown() {
        _linkError.value = null
    }

    val libraryUrls = repository.libraryUrls.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private var job: Job? = null

    init {
        loadMore()
        viewModelScope.launch { siteVerified.collect { if (_state.value.error != null) loadMore() } }
    }

    fun search(query: String) {
        job?.cancel()
        _state.value = SearchUiState(activeQuery = query.trim())
        loadMore()
    }

    fun loadMore() {
        val current = _state.value
        if (current.loading || (!current.hasNextPage && current.page > 0)) return
        job = viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val next = current.page + 1
                val result = if (current.activeQuery.isBlank()) {
                    repository.latest(next)
                } else {
                    repository.search(current.activeQuery, next)
                }
                _state.update {
                    it.copy(
                        results = (it.results + result.comics).distinctBy { comic -> comic.url },
                        page = next,
                        hasNextPage = result.hasNextPage && result.comics.isNotEmpty(),
                        loading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { container, handle ->
            SearchViewModel(container.repository, handle.toRoute<SearchRoute>(), container.siteVerified)
        }
    }
}
