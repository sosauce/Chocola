@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.screens.playing.components.SpeedCardContent
import com.sosauce.chocola.presentation.screens.playing.components.WavySlider

@Composable
fun SpeedAndPitchDialog(
    musicState: MusicState,
    onDismissRequest: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    shouldSnap: Boolean,
    onChangeSnap: () -> Unit,
    onSetSpeedContent: (SpeedCardContent) -> Unit
) {


    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()

            ) {
                Text(text = stringResource(id = R.string.okay))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.speed_rounded),
                contentDescription = null
            )
        },
        title = { Text(stringResource(id = R.string.playback_speed)) },
        text = {
            Column {
                if (!shouldSnap) {
                    RateSliderCard(
                        value = musicState.speed,
                        onValueChange = { onHandlePlayerAction(PlayerActions.SetSpeed(it)) },
                        text = stringResource(R.string.speed),
                        onReset = { onHandlePlayerAction(PlayerActions.SetSpeed(1.0f)) },
                        onOpenManualEdit = { onSetSpeedContent(SpeedCardContent.SPEED) }
                    )
                    Spacer(Modifier.height(10.dp))
                    RateSliderCard(
                        value = musicState.pitch,
                        onValueChange = { onHandlePlayerAction(PlayerActions.SetPitch(it)) },
                        text = stringResource(R.string.pitch),
                        onReset = { onHandlePlayerAction(PlayerActions.SetPitch(1.0f)) },
                        onOpenManualEdit = { onSetSpeedContent(SpeedCardContent.PITCH) }
                    )
                } else {
                    RateSliderCard(
                        value = musicState.speed,
                        onValueChange = {
                            onHandlePlayerAction(PlayerActions.SetSpeed(it))
                            onHandlePlayerAction(PlayerActions.SetPitch(it))
                        },
                        text = stringResource(R.string.rate),
                        onReset = {
                            onHandlePlayerAction(PlayerActions.SetSpeed(1.0f))
                            onHandlePlayerAction(PlayerActions.SetPitch(1.0f))
                        },
                        onOpenManualEdit = { onSetSpeedContent(SpeedCardContent.RATE) }
                    )
                }

                val snapColor by animateColorAsState(
                    if (shouldSnap) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest
                )
                val snapRadius by animateDpAsState(
                    if (shouldSnap) 50.dp else 12.dp
                )

                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onChangeSnap,
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(snapRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = snapColor,
                        contentColor = contentColorFor(snapColor)
                    )
                ) {
                    AnimatedVisibility(shouldSnap) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(5.dp))
                    }
                    Text(
                        text = stringResource(id = R.string.snap)
                    )
                }
            }
        }
    )
}

@Composable
private fun RateSliderCard(
    value: Float,
    text: String,
    onValueChange: (Float) -> Unit,
    onReset: () -> Unit,
    onOpenManualEdit: () -> Unit
) {

    val animatedValue by animateFloatAsState(value)
    val sliderState = rememberSliderState(
        value = animatedValue,
        valueRange = 0.5f..3.0f,
    )
    sliderState.onValueChange = { onValueChange(it) }

    LaunchedEffect(animatedValue) {
        sliderState.value = animatedValue
    }

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHighest),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text)
                }
                Text(text = "%.2f".format(animatedValue))
            }
            WavySlider(state = sliderState)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onOpenManualEdit,
                    shapes = ButtonDefaults.shapes(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit_filled),
                        contentDescription = null
                    )
                }
                AnimatedVisibility(
                    visible = value != 1.0f,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = onReset,
                        shapes = ButtonDefaults.shapes(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.reset),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
