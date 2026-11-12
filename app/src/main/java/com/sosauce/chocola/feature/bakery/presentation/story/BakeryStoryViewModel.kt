package com.sosauce.chocola.feature.bakery.presentation.story

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.feature.bakery.data.BakeryRepository
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class BakeryStoryViewModel(
    private val repository: BakeryRepository,
    private val period: PeriodId
) : ViewModel() {

    private val _state = MutableStateFlow(
        BakeryStoryState(
            isLoading = true,
            period = period,
            pages = emptyList()
        )
    )

    val state = _state.asStateFlow()

    private val _events = Channel<BakeryStoryEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.observeStory(period).collect { pages ->
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        pages = pages,
                        currentPage = current.currentPage.coerceIn(
                            minimumValue = 0,
                            maximumValue = pages.lastIndex.coerceAtLeast(0)
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            while (isActive) {
                delay(PROGRESS_TICK_MILLIS.milliseconds)
                advanceProgress()
            }
        }
    }

    fun onAction(action: BakeryStoryAction) {
        when (action) {
            BakeryStoryAction.NextPage -> {
                _state.update { current ->
                    if (current.pages.isEmpty() || current.currentPage >= current.pages.lastIndex) {
                        current
                    } else {
                        current.copy(
                            currentPage = current.currentPage + 1,
                            pageProgress = 0f
                        )
                    }
                }
            }

            BakeryStoryAction.PreviousPage -> {
                _state.update { current ->
                    if (current.pages.isEmpty()) {
                        current
                    } else {
                        current.copy(
                            currentPage = (current.currentPage - 1).coerceAtLeast(0),
                            pageProgress = 0f
                        )
                    }
                }
            }

            BakeryStoryAction.Pause -> {
                _state.update { it.copy(isPaused = true) }
            }

            BakeryStoryAction.Resume -> {
                _state.update { it.copy(isPaused = false) }
            }

            BakeryStoryAction.Close -> {
                _events.trySend(BakeryStoryEvent.Close)
            }

            is BakeryStoryAction.SaveSummary -> saveSummary(action.bitmap)
        }
    }

    private fun advanceProgress() {
        _state.update { current ->
            when {
                current.isPaused || current.isLoading || current.pages.isEmpty() -> current

                current.currentPage >= current.pages.lastIndex -> {
                    current.copy(pageProgress = 1f)
                }

                else -> {
                    val nextProgress = current.pageProgress + PROGRESS_STEP
                    if (nextProgress >= 1f) {
                        current.copy(
                            currentPage = current.currentPage + 1,
                            pageProgress = 0f
                        )
                    } else {
                        current.copy(pageProgress = nextProgress)
                    }
                }
            }
        }
    }

    private fun saveSummary(bitmap: Bitmap) {
        if (_state.value.isSaving) return

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val saved = runCatching { repository.saveSummaryImage(bitmap) }
                .getOrDefault(false)
            _state.update { it.copy(isSaving = false) }
            _events.trySend(
                if (saved) BakeryStoryEvent.SummarySaved else BakeryStoryEvent.SaveFailed
            )
        }
    }

    private companion object {
        const val PAGE_DURATION_MILLIS = 6_000L
        const val PROGRESS_TICK_MILLIS = 50L
        const val PROGRESS_STEP = PROGRESS_TICK_MILLIS.toFloat() / PAGE_DURATION_MILLIS
    }
}
