@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playing.presentation.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import com.sosauce.chocola.core.designsystem.components.AnimatedButtonGroup
import com.sosauce.chocola.core.designsystem.components.animatedGroupItemShape
import com.sosauce.chocola.core.designsystem.components.collapseHorizontally
import com.sosauce.chocola.core.designsystem.components.rememberAnimatedGroupItems
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.nekobites.helpers.rememberInteractionSource


@Composable
fun QuickActionsRow(
    musicState: MusicState,
    onShowSpeedCard: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    hiddenButtons: Set<NowPlayingButton> = emptySet()
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


    val items = rememberAnimatedGroupItems(keys = quickActionsGroup, hidden = hiddenButtons)
    val interactionSources = List(3) { rememberInteractionSource() }

    val shuffleColor by animateColorAsState(
        if (musicState.shuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    )
    val isRepeating =
        musicState.repeatMode == Player.REPEAT_MODE_ONE || musicState.repeatMode == Player.REPEAT_MODE_ALL
    val repeatColor by animateColorAsState(
        if (isRepeating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    )
    val isRateChanged = musicState.speed != 1.0f || musicState.pitch != 1.0f
    val rateColor by animateColorAsState(
        if (isRateChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    )
    val shapes = items.associate { item ->
        item.key to animatedGroupItemShape(
            position = item.position,
            roundAll = when (item.key) {
                NowPlayingButton.SHUFFLE -> musicState.shuffle
                NowPlayingButton.REPEAT -> isRepeating
                else -> isRateChanged
            }
        )
    }

    Row(
        modifier = Modifier.padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedButtonGroup(
            items = items,
            spacing = 1.dp
        ) { item ->
            val index = quickActionsGroup.indexOf(item.key)
            val buttonModifier = Modifier
                .collapseHorizontally(item)
                .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                .animateWidth(interactionSources[index])

            when (item.key) {
                NowPlayingButton.SHUFFLE -> IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.Shuffle) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = shuffleColor,
                        contentColor = contentColorFor(shuffleColor)
                    ),
                    interactionSource = interactionSources[index],
                    shape = shapes.getValue(item.key),
                    modifier = buttonModifier
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = null
                    )
                }

                NowPlayingButton.REPEAT -> IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.ChangeRepeatMode) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = repeatColor,
                        contentColor = contentColorFor(repeatColor)
                    ),
                    interactionSource = interactionSources[index],
                    shape = shapes.getValue(item.key),
                    modifier = buttonModifier
                ) {
                    val icon =
                        if (musicState.repeatMode == Player.REPEAT_MODE_ONE) R.drawable.repeat_one else R.drawable.repeat

                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null
                    )
                }

                else -> IconButton(
                    onClick = onShowSpeedCard,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = rateColor,
                        contentColor = contentColorFor(rateColor)
                    ),
                    interactionSource = interactionSources[index],
                    shape = shapes.getValue(item.key),
                    modifier = buttonModifier
                ) {
                    Icon(
                        painter = painterResource(R.drawable.speed_filled),
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
        val timerColor by animateColorAsState(
            if (musicState.sleepTimerRemainingDuration > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
        )

        AnimatedVisibility(
            visible = NowPlayingButton.SLEEP_TIMER !in hiddenButtons,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
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

}

private val quickActionsGroup = listOf(
    NowPlayingButton.SHUFFLE,
    NowPlayingButton.REPEAT,
    NowPlayingButton.SPEED
)

/** Whether [QuickActionsRow] has anything to show with these hidden buttons. */
fun hasVisibleQuickActions(hiddenButtons: Set<NowPlayingButton>) =
    (quickActionsGroup + NowPlayingButton.FAVORITE + NowPlayingButton.SLEEP_TIMER).any { it !in hiddenButtons }
