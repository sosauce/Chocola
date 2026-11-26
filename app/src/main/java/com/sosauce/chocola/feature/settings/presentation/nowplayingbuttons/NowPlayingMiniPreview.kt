package com.sosauce.chocola.feature.settings.presentation.nowplayingbuttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.feature.playing.presentation.NowPlayingLayout
import kotlin.math.min
import kotlin.math.roundToInt


@Composable
fun NowPlayingMiniPreview(
    hiddenButtons: Set<NowPlayingButton>,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    val previewLabel = stringResource(R.string.now_playing_preview)
    val fakeMusicState = rememberFakeMusicState()
    val windowSize = rememberWindowSize()

    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clearAndSetSemantics { contentDescription = previewLabel }
            // Keyboard/D-pad focus never enters the preview
            .focusProperties { onEnter = { cancelFocusChange() } }
            .focusGroup()
    ) {
        ScaledToFit(fullSize = windowSize) {
            Box(Modifier.background(MaterialTheme.colorScheme.background)) {
                NowPlayingLayout(
                    musicState = fakeMusicState,
                    hiddenButtons = hiddenButtons,
                    onHandlePlayerActions = {},
                    onNavigate = {}
                )
            }
        }
        // blocks inputs
        Box(
            Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent().changes.forEach { it.consume() }
                        }
                    }
                }
        )
    }
}

/** Measures [content] at [fullSize] and draws it scaled down to fit the incoming constraints. */
@Composable
private fun ScaledToFit(
    fullSize: IntSize,
    content: @Composable () -> Unit
) {
    Layout(content = content) { measurables, constraints ->
        val scale = min(
            constraints.maxWidth / fullSize.width.toFloat(),
            if (constraints.hasBoundedHeight) constraints.maxHeight / fullSize.height.toFloat() else Float.MAX_VALUE
        ).coerceAtMost(1f)
        val placeable =
            measurables.single().measure(Constraints.fixed(fullSize.width, fullSize.height))
        val width = (fullSize.width * scale).roundToInt()
        val height = (fullSize.height * scale).roundToInt()

        layout(width, height) {
            placeable.placeWithLayer(0, 0) {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0f, 0f)
            }
        }
    }
}

@Composable
private fun rememberWindowSize(): IntSize {
    val containerSize = LocalWindowInfo.current.containerSize
    if (containerSize.width > 0 && containerSize.height > 0) return containerSize

    val density = LocalDensity.current
    return with(density) {
        IntSize(containerSize.width.dp.roundToPx(), containerSize.height.dp.roundToPx())
    }
}

@Composable
private fun rememberFakeMusicState(): MusicState {
    val title = stringResource(R.string.title)
    val artist = stringResource(R.string.artist)
    val album = stringResource(R.string.album)

    return remember(title, artist, album) {
        val track = CuteTrack(
            mediaId = "now_playing_preview",
            title = title,
            artist = artist,
            album = album
        )
        MusicState(
            track = track,
            isPlaying = true,
            duration = 200_000L,
            position = 80_000L,
            isPlayerReady = true,
            loadedMedias = listOf(track)
        )
    }
}
