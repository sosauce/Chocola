@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroup
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
import com.sosauce.chocola.data.datastore.rememberSeekButtonsDuration
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.animations.AnimatedDrawable
import com.sosauce.chocola.utils.rememberInteractionSource

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
            drawable = R.drawable.play_to_pause,
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
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSources = List(5) { rememberInteractionSource() }
    val seekButtonsDuration by rememberSeekButtonsDuration()
    val seekDurationInSeconds = seekButtonsDuration * 1000L


    ButtonGroup(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        overflowIndicator = {}
    ) {
        customItem(
            buttonGroupContent = {
                FilledIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                    shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.tertiary)
                    ),
                    interactionSource = interactionSources[0],
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[0])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_previous),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                    )
                }
            },
            menuContent = {}
        )

        customItem(
            buttonGroupContent = {
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.RewindTo(seekDurationInSeconds)) },
                    shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    interactionSource = interactionSources[1],
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[1])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fast_rewind),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                    )
                }
            },
            menuContent = {}
        )

        customItem(
            buttonGroupContent = {
                FilledIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
                    shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
                    ),
                    interactionSource = interactionSources[2],
                    modifier = Modifier
                        .weight(1.5f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[2])
                ) {
                    AnimatedDrawable(
                        drawable = R.drawable.play_to_pause,
                        atEnd = musicState.isPlaying
                    )
                }
            },
            menuContent = {}
        )

        customItem(
            buttonGroupContent = {
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekTo(seekDurationInSeconds)) },
                    shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    interactionSource = interactionSources[3],
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[3])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fast_forward),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                    )
                }
            },
            menuContent = {}
        )

        customItem(
            buttonGroupContent = {
                FilledIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                    shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.tertiary)
                    ),
                    interactionSource = interactionSources[4],
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[4])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                    )
                }
            },
            menuContent = {}
        )
    }
}