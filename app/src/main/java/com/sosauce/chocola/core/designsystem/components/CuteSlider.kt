@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.designsystem.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.ThumbStyle
import com.sosauce.chocola.core.designsystem.TrackStyle
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.preferences.rememberDynamicDuration
import com.sosauce.chocola.core.presentation.preferences.rememberThumbStyle
import com.sosauce.chocola.core.presentation.preferences.rememberTrackStyle

@Composable
fun CuteSlider(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val thumbStyle by rememberThumbStyle()
    val trackStyle by rememberTrackStyle()
    val dynamicDuration by rememberDynamicDuration()

    var tempSliderValue by remember { mutableStateOf<Float?>(null) }
    var lastHapticSecond by remember { mutableLongStateOf(-1L) }

    val currentPosition = musicState.position.toFloat()
    val animatedPosition by animateFloatAsState(
        targetValue = tempSliderValue ?: currentPosition
    )
    // key needed cuz else track range doesn't update when changing tracks
    val sliderState = key(musicState.duration) {
        rememberSliderState(
            value = currentPosition,
            trackRange = 0f..musicState.duration.toFloat()
        )
    }


    LaunchedEffect(animatedPosition) {
        sliderState.value = animatedPosition
    }

    val multiplier = if (dynamicDuration) musicState.speed else 1f

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = DateUtils.formatElapsedTime(((musicState.position / multiplier) / 1000).toLong()),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(6.dp))

            AnimatedVisibility(
                visible = tempSliderValue != null
            ) {
                val draggingPos = tempSliderValue?.toLong() ?: 0L
                val isForward = draggingPos > musicState.position
                val iconRes = if (isForward) R.drawable.fast_forward else R.drawable.fast_rewind

                val infiniteTransition = rememberInfiniteTransition(label = "SeekIconFlash")
                val iconAlpha by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    )
                )


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.graphicsLayer { alpha = iconAlpha }
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = DateUtils.formatElapsedTime(((draggingPos / multiplier) / 1000).toLong()),
                        style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = DateUtils.formatElapsedTime(((musicState.duration / multiplier) / 1000).toLong()),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            state = sliderState,
            onValueChange = { newValue ->
                tempSliderValue = newValue
                val currentSecond = (newValue / 1000).toLong()
                if (currentSecond != lastHapticSecond) {
                    lastHapticSecond = currentSecond
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }
            },
            onValueChangeFinished = {
                tempSliderValue?.let { finalValue ->
                    val seekPos = finalValue.toLong()
                    onHandlePlayerActions(PlayerActions.UpdateCurrentPosition(seekPos))
                    onHandlePlayerActions(PlayerActions.SeekToSlider(seekPos))
                }
                tempSliderValue = null
                lastHapticSecond = -1L
            },
            thumb = {
                when (thumbStyle) {
                    ThumbStyle.STRAIGHT -> StraightThumb(it.isDragging)
                    ThumbStyle.BALL -> ClassicThumb(it.isDragging)
                    ThumbStyle.MORPHING -> MorphingThumb()
                }
            },
            track = { trackState ->
                when (trackStyle) {
                    TrackStyle.WAVY -> WavyTrack(
                        isPlaying = musicState.isPlaying,
                        sliderState = trackState
                    )

                    TrackStyle.STRAIGHT -> StraightTrack(trackState)
                }
            }
        )
    }
}