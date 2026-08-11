@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions

@Composable
fun CuteSlider(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    var tempSliderValue by remember { mutableStateOf<Float?>(null) }
    val value by animateFloatAsState(
        targetValue = tempSliderValue ?: musicState.position.toFloat()
    )
    val sliderState = rememberCuteSliderState(
        value = value,
        onValueChange = { tempSliderValue = it },
        onValueChangeFinished = {
            tempSliderValue?.let {
                onHandlePlayerActions(
                    PlayerActions.UpdateCurrentPosition(it.toLong())
                )
                onHandlePlayerActions(
                    PlayerActions.SeekToSlider(it.toLong())
                )
            }
            tempSliderValue = null
        },
        valueRange = 0f..musicState.track.durationMs.toFloat(),
        enabled = true
    )


    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = DateUtils.formatElapsedTime(musicState.position / 1000),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(5.dp))
            AnimatedVisibility(
                visible = tempSliderValue != null
            ) {

                val time = tempSliderValue?.toLong() ?: 0

                val icon = if (time > musicState.position) R.drawable.fast_forward else R.drawable.fast_rewind
                val infinite = rememberInfiniteTransition()
                val iconFlash by infinite.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = iconFlash
                            }
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = DateUtils.formatElapsedTime(time / 1000),
                        style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = DateUtils.formatElapsedTime(musicState.track.durationMs / 1000),
                color = MaterialTheme.colorScheme.primary
            )
        }
        NowPlayingSlider(
            state = sliderState,
            isPlaying = musicState.isPlaying
        )

    }
}