package com.sosauce.chocola.presentation.screens.settings

import androidx.collection.FloatList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.EqualizerBand
import com.sosauce.chocola.data.models.EqualizerPreset
import com.sosauce.chocola.domain.EqualizerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaybackSettingsViewModel(
    private val equalizerManager: EqualizerManager,
    private val userPreferences: UserPreferences
): ViewModel() {

    private val _state = MutableStateFlow(PlaybackSettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            equalizerManager.eqBands.collectLatest { bands ->
                _state.update {
                    it.copy(
                        eqBands = bands,
                        eqPresets = equalizerManager.getPresets()
                    )
                }
            }
        }
    }

    fun handlePlaybackSettingsActions(action: PlaybackSettingsActions) {
        when(action) {
            is PlaybackSettingsActions.ToggleEqualizer -> { equalizerManager.toggleEqualizer(action.enable) }
            is PlaybackSettingsActions.SetBandGain -> {
                viewModelScope.launch {
                    equalizerManager.setBandGain(action.frequency, action.gain)
                }
            }
            is PlaybackSettingsActions.UsePreset -> {
                viewModelScope.launch {
                    equalizerManager.usePreset(action.preset)
                }
            }
        }
    }


}

data class PlaybackSettingsState(
    val eqBands: List<EqualizerBand> = emptyList(),
    val eqPresets: List<EqualizerPreset> = emptyList()
)

sealed interface PlaybackSettingsActions {
    data class ToggleEqualizer(val enable: Boolean): PlaybackSettingsActions
    data class UsePreset(val preset: FloatList): PlaybackSettingsActions
    data class SetBandGain(
        val frequency: Float,
        val gain: Float
    ): PlaybackSettingsActions
}


