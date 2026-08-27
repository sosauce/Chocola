@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.utils.rememberInteractionSource


@Composable
fun QuickActionsRow(
    musicState: MusicState,
    onShowSpeedCard: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        CuteTimePicker(
            currentTimerTime = musicState.sleepTimerRemainingDuration,
            onDismissRequest = { showTimePicker = false },
            onSetTimer = { hours, minutes ->
                showTimePicker = false
                onHandlePlayerActions(PlayerActions.SetSleepTimer(hours, minutes))
            },
            onCancelTimer = { onHandlePlayerActions(PlayerActions.CancelSleepTimer) }
        )
    }



    Row(
        modifier = Modifier.padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val interactionSources = List(3) { rememberInteractionSource() }

        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            overflowIndicator = {}
        ) {

            customItem(
                buttonGroupContent = {

                    val shuffleColor by animateColorAsState(
                        if (musicState.shuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
                    )
                    val shuffleEnd by animateDpAsState(
                        if (musicState.shuffle) 50.dp else 4.dp
                    )

                    IconButton(
                        onClick = { onHandlePlayerActions(PlayerActions.Shuffle) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = shuffleColor,
                            contentColor = contentColorFor(shuffleColor)
                        ),
                        interactionSource = interactionSources[0],
                        shape = RoundedCornerShape(
                            topStart = 50.dp,
                            topEnd = shuffleEnd,
                            bottomStart = 50.dp,
                            bottomEnd = shuffleEnd
                        ),
                        modifier = Modifier
                            .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                            .animateWidth(interactionSources[0])
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = null
                        )
                    }
                },
                menuContent = {}
            )

            customItem(
                buttonGroupContent = {

                    val repeatColor by animateColorAsState(
                        if (musicState.repeatMode == Player.REPEAT_MODE_ONE || musicState.repeatMode == Player.REPEAT_MODE_ALL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
                    )

                    val repeatShape by animateDpAsState(
                        if (musicState.repeatMode == Player.REPEAT_MODE_ONE || musicState.repeatMode == Player.REPEAT_MODE_ALL) 50.dp else 4.dp
                    )

                    IconButton(
                        onClick = { onHandlePlayerActions(PlayerActions.ChangeRepeatMode) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = repeatColor,
                            contentColor = contentColorFor(repeatColor)
                        ),
                        interactionSource = interactionSources[1],
                        shape = RoundedCornerShape(repeatShape),
                        modifier = Modifier
                            .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                            .animateWidth(interactionSources[1])
                    ) {
                        val icon =
                            if (musicState.repeatMode == Player.REPEAT_MODE_ONE) R.drawable.repeat_one else R.drawable.repeat

                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    }
                },
                menuContent = {}
            )

            customItem(
                buttonGroupContent = {

                    val rateColor by animateColorAsState(
                        if (musicState.speed != 1.0f || musicState.pitch != 1.0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
                    )

                    val rateStart by animateDpAsState(
                        if (musicState.speed != 1.0f || musicState.pitch != 1.0f) 50.dp else 4.dp
                    )

                    IconButton(
                        onClick = onShowSpeedCard,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = rateColor,
                            contentColor = contentColorFor(rateColor)
                        ),
                        interactionSource = interactionSources[2],
                        shape = RoundedCornerShape(
                            topStart = rateStart,
                            topEnd = 50.dp,
                            bottomStart = rateStart,
                            bottomEnd = 50.dp
                        ),
                        modifier = Modifier
                            .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                            .animateWidth(interactionSources[2])
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.speed_filled),
                            contentDescription = null
                        )
                    }
                },
                menuContent = {}
            )
        }

        Spacer(Modifier.weight(1f))
        val timerColor by animateColorAsState(
            if (musicState.sleepTimerRemainingDuration > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
        )

        Button(
            onClick = { showTimePicker = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = timerColor,
                contentColor = contentColorFor(timerColor)
            ),
            shapes = ButtonDefaults.shapes()
        ) {
            val icon =
                if (musicState.sleepTimerRemainingDuration > 0) R.drawable.sleep_timer_active_filled else R.drawable.sleep_timer_filled
            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
            AnimatedVisibility(visible = musicState.sleepTimerRemainingDuration > 0) {
                Text(
                    text = DateUtils.formatElapsedTime(musicState.sleepTimerRemainingDuration / 1000),
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
    }

}
