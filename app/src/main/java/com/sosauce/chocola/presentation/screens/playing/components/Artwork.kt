@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class
)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberArtLyrics
import com.sosauce.chocola.data.datastore.rememberArtworkShape
import com.sosauce.chocola.data.datastore.rememberCarousel
import com.sosauce.chocola.data.datastore.rememberIsLandscape
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.screens.lyrics.LyricsList
import com.sosauce.chocola.utils.ArtworkShape
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun Artwork(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val useCarousel by rememberCarousel()
    var artworkShape by rememberArtworkShape()
    val isLandscape = rememberIsLandscape()
    var showLyrics by remember { mutableStateOf(false) }
    val blur by animateIntAsState(
        targetValue = if (showLyrics) 100 else 0,
        animationSpec = tween(1000)
    )
    val artLyrics by rememberArtLyrics()

    Box(
        modifier = Modifier
            .fillMaxWidth(if (isLandscape) 0.4f else 1f)
            .aspectRatio(1f)
            .clickable(
                enabled = artLyrics,
                indication = null,
                interactionSource = null,
                onClick = { showLyrics = !showLyrics }
            )
    ) {
        if (useCarousel) {
            val carouselState =
                rememberCarouselState(initialItem = musicState.mediaIndex) { musicState.loadedMedias.size }

            var isProgrammaticScroll by remember { mutableStateOf(false) }

            // Sync carousel position when playback changes track externally
            LaunchedEffect(musicState.mediaIndex) {
                if (!carouselState.isScrollInProgress &&
                    carouselState.currentItem != musicState.mediaIndex
                ) {
                    isProgrammaticScroll = true
                    carouselState.animateScrollToItem(musicState.mediaIndex)
                    isProgrammaticScroll = false
                }
            }

            // Use rememberUpdatedState to always read the latest values inside the long-lived LaunchedEffect
            val currentMediaIndex by rememberUpdatedState(musicState.mediaIndex)
            val currentShuffle by rememberUpdatedState(musicState.shuffle)
            val currentTrackCount by rememberUpdatedState(musicState.loadedMedias.size)

            // Dispatch track change when user finishes swiping
            LaunchedEffect(carouselState) {
                snapshotFlow { carouselState.isScrollInProgress }
                    .filter { !it }
                    .map { carouselState.currentItem }
                    .distinctUntilChanged()
                    .collectLatest { settledItem ->
                        if (currentTrackCount == 0) return@collectLatest
                        val safeIndex = settledItem.coerceIn(0, currentTrackCount - 1)
                        if (isProgrammaticScroll) return@collectLatest
                        if (safeIndex != currentMediaIndex) {
                            if (currentShuffle) {
                                if (safeIndex > currentMediaIndex) {
                                    onHandlePlayerActions(PlayerActions.SeekToNextMusic)
                                } else {
                                    onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
                                }
                            } else {
                                onHandlePlayerActions(PlayerActions.SeekToMusicIndex(safeIndex))
                            }
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
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .cloudy(
                            radius = blur,
                            enabled = carouselState.currentItem == page && blur > 0
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.music_note),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.4f),
                        tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)

                    )
                    AsyncImage(
                        model = musicState.loadedMedias[page].artUri,
                        contentDescription = stringResource(R.string.artwork),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

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
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh, offset = Offset(10f, 5f), blurRadius = 10f
                                    )
                                )
                            )
                        }
                    )
                }
            }

        } else {

            Box(
                modifier = Modifier
                    .clip(ArtworkShape.toShape(artworkShape))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.4f),
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
                                text = stringResource(R.string.no_lyrics_note)
                            )
                        }
                    )
                }
            }
        }

    }

}


