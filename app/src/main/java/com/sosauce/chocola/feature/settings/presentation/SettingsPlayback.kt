@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalSlider
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.WavySlider
import com.sosauce.chocola.core.presentation.preferences.rememberEnableEqualizer
import com.sosauce.chocola.core.presentation.preferences.rememberKeepAlive
import com.sosauce.chocola.core.presentation.preferences.rememberPauseOnMute
import com.sosauce.chocola.core.presentation.preferences.rememberSeekButtonsDuration
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.nekobites.components.settings.SelectableTile
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsItem
import com.sosauce.nekobites.components.settings.SettingsPicker
import com.sosauce.nekobites.components.settings.SettingsSlider
import com.sosauce.nekobites.components.settings.SettingsSwitch

@Composable
fun ColumnScope.SettingsPlayback(
    state: PlaybackSettingsState,
    onHandlePlaybackSettingsActions: (PlaybackSettingsActions) -> Unit
) {
    var pauseOnMute by rememberPauseOnMute()
    var keepAlive by rememberKeepAlive()
    var seekButtonsDuration by rememberSeekButtonsDuration()
    var enableEqualizer by rememberEnableEqualizer()

    SettingsGroup(title = stringResource(R.string.playback)) {
        SettingsSlider(
            title = stringResource(R.string.seek_buttons_duration),
            value = seekButtonsDuration,
            onValueChange = { seekButtonsDuration = it },
            unit = "s",
            slider = { state, onChange -> WavySlider(state, onChange) }
        )
        SettingsSwitch(
            title = stringResource(R.string.pause_on_mute),
            description = stringResource(R.string.pause_on_mute_desc),
            checked = pauseOnMute,
            onCheckedChange = { pauseOnMute = !pauseOnMute }
        )
        SettingsSwitch(
            title = stringResource(R.string.keep_alive),
            description = stringResource(R.string.keep_alive_desc),
            checked = keepAlive,
            onCheckedChange = { keepAlive = !keepAlive }
        )
    }

    SettingsGroup(title = stringResource(R.string.equalizer)) {
        SettingsSwitch(
            title = stringResource(R.string.enable_equalizer),
            icon = R.drawable.headphones,
            checked = enableEqualizer,
            onCheckedChange = {
                onHandlePlaybackSettingsActions(PlaybackSettingsActions.ToggleEqualizer(!enableEqualizer))
                enableEqualizer = !enableEqualizer
            }
        )
        AnimatedVisibility(visible = enableEqualizer) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SettingsPicker(title = stringResource(R.string.settings_eq_presets)) {
                    items(state.eqPresets) { preset ->
                        SelectableTile(
                            selected = false,
                            onClick = {
                                onHandlePlaybackSettingsActions(
                                    PlaybackSettingsActions.UsePreset(
                                        preset.gains
                                    )
                                )
                            },
                            label = preset.name
                        ) {
                            Text(
                                text = preset.emoji,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
                SettingsItem {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.eqBands) { (frequency, gain) ->
                            EqualizerBand(
                                frequency = frequency,
                                gain = gain,
                                onBandGainChanged = { freq, newGain ->
                                    onHandlePlaybackSettingsActions(
                                        PlaybackSettingsActions.SetBandGain(freq, newGain)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EqualizerBand(
    frequency: Float,
    gain: Float,
    onBandGainChanged: (Float, Float) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val sliderState = rememberSliderState(
        value = gain,
        trackRange = -15f..15f
    )
    val label =
        if (frequency >= 1000) "${frequency.toInt() / 1000}kHz" else "${frequency.toInt()}Hz"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = colors.secondaryContainer,
            contentColor = colors.onSecondaryContainer
        ) {
            Text(
                text = "${sliderState.value.toInt()}dB",
                style = MaterialTheme.typography.labelMediumEmphasized,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        VerticalSlider(
            state = sliderState,
            topToBottom = false,
            onValueChange = { sliderState.value = it },
            onValueChangeFinished = { onBandGainChanged(frequency, sliderState.value) },
            modifier = Modifier
                .padding(vertical = 10.dp)
                .height(260.dp),
            thumb = { state ->
                val rotation by animateFloatAsState(
                    targetValue = state.value * 24f,
                    animationSpec = bouncySpec(),
                    label = "bandThumb"
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .background(colors.primary, MaterialShapes.Cookie9Sided.toShape())
                )
            },
            track = { state ->
                SliderDefaults.Track(
                    sliderState = state,
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    modifier = Modifier.width(40.dp)
                )
            }
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMediumEmphasized,
            color = colors.onSurfaceVariant
        )
    }
}
