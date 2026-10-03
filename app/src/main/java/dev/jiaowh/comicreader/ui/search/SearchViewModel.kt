package dev.jiaowh.comicreader.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jiaowh.comicreader.data.ComicRepository
import dev.jiaowh.comicreader.data.remote.ComicSummary
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
    siteVerified: SharedFlow<Unit>,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

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
        val Factory = appViewModelFactory { container, _ ->
            SearchViewModel(container.repository, container.siteVerified)
        }
    }
}
