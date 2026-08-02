@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings

import android.media.audiofx.Equalizer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalSlider
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp.Companion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberEnableEqualizer
import com.sosauce.chocola.data.datastore.rememberPauseOnMute
import com.sosauce.chocola.data.datastore.rememberSeekButtonsDuration
import com.sosauce.chocola.presentation.screens.settings.compenents.EqualizerPresetSelector
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSwitch
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.presentation.screens.settings.compenents.SliderSettingsCards
import com.sosauce.chocola.presentation.screens.settings.compenents.SquareSelector
import com.sosauce.chocola.presentation.shared_components.LazyRowWithScrollButton
import com.sosauce.chocola.utils.bouncySpec
import kotlin.math.sqrt

@Composable
fun SettingsPlayback(
    state: PlaybackSettingsState,
    onHandlePlaybackSettingsActions: (PlaybackSettingsActions) -> Unit
) {

    var pauseOnMute by rememberPauseOnMute()
    var seekButtonsDuration by rememberSeekButtonsDuration()
    var enableEqualizer by rememberEnableEqualizer()

    Column {
        SettingsWithTitle(
            title = R.string.playback
        ) {
            SliderSettingsCards(
                value = seekButtonsDuration,
                onValueChange = { seekButtonsDuration = it },
                topDp = 24.dp,
                bottomDp = 24.dp,
                unit = "s",
                text = stringResource(R.string.seek_buttons_duration)
            )
        }

        SettingsWithTitle(
            title = R.string.audio
        ) {
            SettingsSwitch(
                checked = pauseOnMute,
                onCheckedChange = { pauseOnMute = !pauseOnMute },
                topDp = 24.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.pause_on_mute),
                optionalDescription = R.string.pause_on_mute_desc
            )
        }

        SettingsWithTitle(
            title = R.string.equalizer
        ) {

            SettingsSwitch(
                checked = enableEqualizer,
                onCheckedChange = {
                    onHandlePlaybackSettingsActions(PlaybackSettingsActions.ToggleEqualizer(!enableEqualizer))
                    enableEqualizer = !enableEqualizer
                },
                topDp = 50.dp,
                bottomDp = 50.dp,
                text = stringResource(R.string.enable_equalizer)
            )
            Spacer(Modifier.height(10.dp))
            AnimatedVisibility(
                visible = enableEqualizer
            ) {
                Column {
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    ) {
                        LazyRowWithScrollButton(
                            items = state.eqPresets
                        ) { preset ->
                            val emoji = when {
                                preset.name.contains("Normal", true) -> "🎧"
                                preset.name.contains("Classic", true) || preset.name.contains("Classical", true) -> "🎹"
                                preset.name.contains("Dance", true) -> "🕺"
                                preset.name.contains("Flat", true) -> "📏"
                                preset.name.contains("Folk", true) -> "🪕"
                                preset.name.contains("Heavy Metal", true) || preset.name.contains("Metal", true) -> "🤘"
                                preset.name.contains("Hip Hop", true) -> "🪩"
                                preset.name.contains("Jazz", true) -> "🎷"
                                preset.name.contains("Pop", true) -> "🎤"
                                preset.name.contains("Rock", true) -> "🎸"
                                preset.name.contains("Acoustic", true) -> "🎻"
                                preset.name.contains("Bass", true) -> "🔊"
                                preset.name.contains("Loudness", true) || preset.name.contains("Boost", true) -> "📢"
                                preset.name.contains("Electronic", true) || preset.name.contains("Techno", true) -> "🎛️"
                                preset.name.contains("Latin", true) -> "💃"
                                preset.name.contains("Country", true) -> "🤠"
                                preset.name.contains("Piano", true) -> "🎼"
                                preset.name.contains("Vocal", true) -> "🗣️"
                                else -> "🎵"
                            }

                            EqualizerPresetSelector(
                                emoji = emoji,
                                name = preset.name,
                                onClick = { onHandlePlaybackSettingsActions(PlaybackSettingsActions.UsePreset(preset.band)) }
                            )
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 24.dp,
                            bottomEnd = 24.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            state.eqBands.fastForEach { (centerFrequency, milliBel) ->
                                EqualizerBandSlider(
                                    centerFrequency = centerFrequency,
                                    milliBel = milliBel,
                                    onBandGainChanged = { freq, gain ->
                                        onHandlePlaybackSettingsActions(PlaybackSettingsActions.SetBandGain(freq, gain))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EqualizerBandSlider(
    centerFrequency: Int,
    milliBel: Short,
    onBandGainChanged: (Int, Short) -> Unit
) {
    val targetValue = remember(milliBel) { milliBel / 100f }

    val sliderState = rememberSliderState(
        value = targetValue,
        valueRange = -15f..15f
    )

    sliderState.onValueChangeFinished = {
        val finalMilliBel = (sliderState.value * 100).toInt().toShort()
        onBandGainChanged(centerFrequency, finalMilliBel)
    }

    Column(
        modifier = Modifier.padding(horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val label = if (centerFrequency >= 1000) "${centerFrequency / 1000}kHz" else "${centerFrequency}Hz"
        Text(
            text = label,
            style = MaterialTheme.typography.labelMediumEmphasized
        )

        VerticalSlider(
            modifier = Modifier.height(300.dp),
            topToBottom = false,
            state = sliderState,
            thumb = { state ->
                val rotation by animateFloatAsState(
                    targetValue = state.value * 360,
                    animationSpec = bouncySpec()
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialShapes.Circle.toShape()
                        )
                        .graphicsLayer {
                            rotationZ = rotation
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = MaterialShapes.Cookie12Sided.toShape()
                            )
                    )
                }
            },
            track = { state ->
                SliderDefaults.Track(
                    modifier = Modifier.width(34.dp),
                    sliderState = state,
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp
                )
            }
        )
        Text(
            text = "${sliderState.value.toInt()}dB",
            style = MaterialTheme.typography.labelMediumEmphasized
        )
    }
}