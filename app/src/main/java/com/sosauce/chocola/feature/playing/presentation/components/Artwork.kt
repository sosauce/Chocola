@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class
)

package com.sosauce.chocola.feature.playing.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.ArtworkShape
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.preferences.rememberArtLyrics
import com.sosauce.chocola.core.presentation.preferences.rememberArtworkShape
import com.sosauce.chocola.core.presentation.preferences.rememberCarousel
import com.sosauce.chocola.core.presentation.preferences.rememberIsLandscape
import com.sosauce.chocola.core.presentation.preferences.rememberNowPlayingShapeMorph
import com.sosauce.chocola.core.presentation.preferences.rememberUseTurntable
import com.sosauce.chocola.feature.lyrics.presentation.LyricsList
import com.sosauce.nekobites.animations.rememberAnimatedShape
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest

@Composable
private fun TurntableArtwork(
    musicState: MusicState,
    blur: Int,
    showLyrics: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(musicState.isPlaying) {
        if (musicState.isPlaying) {
            while (true) {
                rotation.animateTo(
                    targetValue = rotation.value + 360f,
                    animationSpec = tween(7000, easing = LinearEasing)
                )
            }
        } else {
            rotation.stop()
        }
    }

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(12.dp, CircleShape)
                .background(Color(0xFF171719), CircleShape)
                .rotate(rotation.value),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxRadius = size.minDimension / 2f
                for (index in 1..12) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = maxRadius * index / 13f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(0.52f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.35f),
                    tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )
                AsyncImage(
                    model = musicState.track.artUri,
                    contentDescription = stringResource(R.string.artwork),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .cloudy(radius = blur, enabled = blur > 0)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color(0xFFB8B8BC),
                start = Offset(size.width * 0.84f, size.height * 0.12f),
                end = Offset(size.width * 0.68f, size.height * 0.28f),
                strokeWidth = 5.dp.toPx()
            )
            drawLine(
                color = Color(0xFFB8B8BC),
                start = Offset(size.width * 0.68f, size.height * 0.28f),
                end = Offset(size.width * 0.56f, size.height * 0.44f),
                strokeWidth = 3.dp.toPx()
            )
        }
        AnimatedVisibility(
            visible = showLyrics,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            LyricsList(
                textShadow = true,
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions,
                emptyLyrics = {
                    Text(
                        text = stringResource(R.string.no_lyrics_note),
                        style = MaterialTheme.typography.titleLargeEmphasized.copy(
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                offset = Offset(10f, 5f),
                                blurRadius = 10f
                            )
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun Artwork(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val useCarousel by rememberCarousel()
    var artworkShape by rememberArtworkShape()
    val artLyrics by rememberArtLyrics()
    val shapeMorph by rememberNowPlayingShapeMorph()
    val useTurntable by rememberUseTurntable()

    val isLandscape = rememberIsLandscape()
    var showLyrics by remember { mutableStateOf(false) }
    val blur by animateIntAsState(
        targetValue = if (showLyrics) 100 else 0,
        animationSpec = tween(1000)
    )
    val scale by animateFloatAsState(
        targetValue = if (musicState.isPlaying) 1f else 0.9f
    )
    val animatedShape = if (shapeMorph) {
        rememberAnimatedShape(
            condition = musicState.isPlaying,
            shapeA = MaterialShapes.Circle,
            shapeB = ArtworkShape.toRoundedPolygon(artworkShape)
        )
    } else ArtworkShape.toShape(artworkShape)

    Box(
        modifier = if (isLandscape) {
            modifier
                .fillMaxWidth(0.4f)
                .aspectRatio(1f)
        } else {
            modifier
                // Shrink to fit
                .layout { measurable, constraints ->
                    val side = minOf(
                        constraints.maxWidth,
                        if (constraints.hasBoundedHeight) constraints.maxHeight
                        else constraints.maxWidth
                    ).coerceAtLeast(0)
                    val placeable = measurable.measure(Constraints.fixed(side, side))
                    layout(side, side) { placeable.place(0, 0) }
                }
        }
            .clickable(
                enabled = artLyrics,
                indication = null,
                interactionSource = null,
                onClick = { showLyrics = !showLyrics }
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        if (useTurntable) {
            TurntableArtwork(
                musicState = musicState,
                blur = blur,
                showLyrics = showLyrics,
                onHandlePlayerActions = onHandlePlayerActions
            )
        } else if (useCarousel) {

            val carouselState = rememberCarouselState(
                initialItem = musicState.mediaIndex
            ) { musicState.loadedMedias.size }

            val currentTrack by rememberUpdatedState(musicState.track)
            val loadedMedias by rememberUpdatedState(musicState.loadedMedias)

            LaunchedEffect(musicState.loadedMedias, musicState.track.mediaId) {
                val target =
                    musicState.loadedMedias.indexOfFirst { it.mediaId == musicState.track.mediaId }
                if (target != -1 && carouselState.currentItem != target) {
                    carouselState.animateScrollToItem(target)
                }
            }

            LaunchedEffect(carouselState) {
                snapshotFlow { carouselState.isScrollInProgress }
                    .filter { !it }
                    .mapLatest { carouselState.currentItem }
                    .distinctUntilChanged()
                    .collectLatest { settled ->
                        val track = loadedMedias.getOrNull(settled) ?: return@collectLatest
                        if (track.mediaId != currentTrack.mediaId) {
                            onHandlePlayerActions(PlayerActions.PlayTrack(track))
                        }
                    }
            }

            HorizontalCenteredHeroCarousel(
                state = carouselState,
                itemSpacing = 5.dp,
            ) { page ->
                Box(
                    modifier = Modifier
                        .maskClip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.music_note),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(0.4f)
                            .cloudy(
                                radius = blur,
                                enabled = carouselState.currentItem == page && blur > 0
                            ),
                        tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)

                    )
                    AsyncImage(
                        model = musicState.loadedMedias[page].artUri,
                        contentDescription = stringResource(R.string.artwork),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .cloudy(
                                radius = blur,
                                enabled = carouselState.currentItem == page && blur > 0
                            )
                    )
                    AnimatedVisibility(
                        visible = carouselState.currentItem == page && showLyrics,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut(),
                    ) {
                        LyricsList(
                            textShadow = true,
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerActions,
                            emptyLyrics = {
                                Text(
                                    text = stringResource(R.string.no_lyrics_note),
                                    style = MaterialTheme.typography.titleLargeEmphasized.copy(
                                        shadow = Shadow(
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            offset = Offset(10f, 5f),
                                            blurRadius = 10f
                                        )
                                    )
                                )
                            }
                        )
                    }
                }

            }

        } else {

            Box(
                modifier = Modifier
                    .clip(animatedShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(0.4f)
                        .cloudy(
                            radius = blur,
                            enabled = blur > 0
                        ),
                    tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)

                )
                AsyncImage(
                    model = musicState.track.artUri,
                    contentDescription = stringResource(R.string.artwork),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .cloudy(
                            radius = blur,
                            enabled = blur > 0
                        )
                )
                AnimatedVisibility(
                    visible = showLyrics,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    LyricsList(
                        textShadow = true,
                        musicState = musicState,
                        onHandlePlayerActions = onHandlePlayerActions,
                        emptyLyrics = {
                            Text(
                                text = stringResource(R.string.no_lyrics_note),
                                style = MaterialTheme.typography.titleLargeEmphasized.copy(
                                    shadow = Shadow(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        offset = Offset(10f, 5f),
                                        blurRadius = 10f
                                    )
                                )
                            )
                        }
                    )
                }
            }
        }

    }

}
