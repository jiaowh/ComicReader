package dev.jiaowh.comicreader.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.jiaowh.comicreader.data.ComicRepository
import dev.jiaowh.comicreader.data.ReadingOrderState
import dev.jiaowh.comicreader.data.StepState
import dev.jiaowh.comicreader.ui.ReadingOrderRoute
import dev.jiaowh.comicreader.ui.common.appViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReadingOrderViewModel(
    private val repository: ComicRepository,
    private val seriesId: Long,
    siteVerified: SharedFlow<Unit>,
) : ViewModel() {

    val order: StateFlow<ReadingOrderState?> = repository.readingOrder(seriesId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val seriesName: StateFlow<String> = repository.series
        .map { list -> list.firstOrNull { it.id == seriesId }?.name.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    /** (done, total) while matching steps to BatCave comics. */
    private val _matching = MutableStateFlow<Pair<Int, Int>?>(null)
    val matching = _matching.asStateFlow()

    private val _error = MutableStateFlow<Throwable?>(null)
    val error = _error.asStateFlow()

    init {
        // Match any books not linked yet, top of the order first.
        match(retryNotFound = false)
        viewModelScope.launch { siteVerified.collect { if (_error.value != null) match(retryNotFound = false) } }
    }

    /** Also retries books that weren't found before. */
    fun rematch() = match(retryNotFound = true)

    private fun match(retryNotFound: Boolean) {
        if (_matching.value != null) return
        viewModelScope.launch {
            _matching.value = 0 to 0
            _error.value = null
            try {
                val onProgress = { done: Int, total: Int -> _matching.value = done to total }
                if (retryNotFound) {
                    repository.rematchReadingOrder(seriesId, onProgress)
                } else {
                    repository.resolveReadingOrder(seriesId, onProgress)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e
            } finally {
                _matching.value = null
            }
        }
    }

    fun markRead(step: StepState) {
        viewModelScope.launch { repository.markStepRead(step) }
    }

    fun markUnread(step: StepState) {
        viewModelScope.launch { repository.markStepUnread(step) }
    }

    companion object {
        val Factory = appViewModelFactory { container, handle ->
            ReadingOrderViewModel(container.repository, handle.toRoute<ReadingOrderRoute>().seriesId, container.siteVerified)
        }
    }
}
