package com.sosauce.chocola.feature.settings.presentation.nowplayingbuttons

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class NowPlayingButtonsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val state = userPreferences.getHiddenNowPlayingButtons
        .map { NowPlayingButtonsState(isLoading = false, hiddenButtons = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NowPlayingButtonsState()
        )

    private val _events = Channel<NowPlayingButtonsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: NowPlayingButtonsAction) {
        when (action) {
            is NowPlayingButtonsAction.SetButtonVisible -> {
                viewModelScope.launch {
                    try {
                        userPreferences.setNowPlayingButtonHidden(
                            button = action.button,
                            hidden = !action.visible
                        )
                    } catch (_: IOException) {
                        _events.send(NowPlayingButtonsEvent.SaveFailed)
                    }
                }
            }
        }
    }
}

@Immutable
data class NowPlayingButtonsState(
    val isLoading: Boolean = true,
    val hiddenButtons: Set<NowPlayingButton> = emptySet()
)

sealed interface NowPlayingButtonsAction {
    data class SetButtonVisible(
        val button: NowPlayingButton,
        val visible: Boolean
    ) : NowPlayingButtonsAction
}

sealed interface NowPlayingButtonsEvent {
    data object SaveFailed : NowPlayingButtonsEvent
}
