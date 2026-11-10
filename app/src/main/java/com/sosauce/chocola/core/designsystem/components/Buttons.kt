@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.core.designsystem.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.preferences.rememberSeekButtonsDuration
import com.sosauce.nekobites.animations.AnimatedDrawable
import com.sosauce.nekobites.animations.AnimatedDrawableFile
import com.sosauce.nekobites.helpers.rememberInteractionSource

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f
    )


    IconButton(
        onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
        shapes = IconButtonDefaults.shapes(),
        modifier = buttonModifier,
        interactionSource = interactionSource
    ) {
        AnimatedDrawable(
            drawable = AnimatedDrawableFile.PLAY,
            atEnd = isPlaying,
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun ActionButtonsRow(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    hiddenButtons: Set<NowPlayingButton> = emptySet()
) {

    val interactionSources = List(5) { rememberInteractionSource() }
    val seekButtonsDuration by rememberSeekButtonsDuration()
    val seekDurationInSeconds = seekButtonsDuration * 1000L
    val items = rememberAnimatedGroupItems(keys = actionButtonsGroup, hidden = hiddenButtons)

    AnimatedButtonGroup(
        items = items,
        spacing = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) { item ->
        val index = actionButtonsGroup.indexOf(item.key)
        val buttonModifier =
            animatedWeight(item, if (item.key == NowPlayingButton.PLAY_PAUSE) 1.5f else 1f)
                .height(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide).height)
                .animateWidth(interactionSource = interactionSources[index])

        when (item.key) {
            NowPlayingButton.PREVIOUS -> FilledIconButton(
                onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.tertiary)
                ),
                interactionSource = interactionSources[index],
                modifier = buttonModifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.skip_previous),
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_to_previous_description)
                )
            }

            NowPlayingButton.REWIND -> IconButton(
                onClick = { onHandlePlayerActions(PlayerActions.RewindTo(seekDurationInSeconds)) },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                ),
                interactionSource = interactionSources[index],
                modifier = buttonModifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.fast_rewind),
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_back_description)
                )
            }

            NowPlayingButton.PLAY_PAUSE -> FilledIconButton(
                onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
                ),
                interactionSource = interactionSources[index],
                modifier = buttonModifier
            ) {
                AnimatedDrawable(
                    drawable = AnimatedDrawableFile.PLAY,
                    atEnd = musicState.isPlaying
                )
            }

            NowPlayingButton.FORWARD -> IconButton(
                onClick = { onHandlePlayerActions(PlayerActions.SeekTo(seekDurationInSeconds)) },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                ),
                interactionSource = interactionSources[index],
                modifier = buttonModifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.fast_forward),
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                )
            }

            else -> FilledIconButton(
                onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.tertiary)
                ),
                interactionSource = interactionSources[index],
                modifier = buttonModifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.skip_next),
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_to_next_description)
                )
            }
        }
    }
}

private val actionButtonsGroup = listOf(
    NowPlayingButton.PREVIOUS,
    NowPlayingButton.REWIND,
    NowPlayingButton.PLAY_PAUSE,
    NowPlayingButton.FORWARD,
    NowPlayingButton.NEXT
)
