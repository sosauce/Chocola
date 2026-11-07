@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.feature.library.presentation.artist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.core.presentation.components.DefaultMusicListItemTrailingContent
import com.sosauce.chocola.core.presentation.components.MusicListItem
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.designsystem.components.SelectOverlayItem
import com.sosauce.chocola.core.presentation.components.trackSelectActions
import com.sosauce.chocola.core.presentation.components.TracksSelectedBar
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.designsystem.components.NumberOfTracks
import com.sosauce.chocola.feature.library.presentation.artist.components.ArtistHeader
import com.sosauce.chocola.core.designsystem.components.NumberOfAlbums
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.feature.library.presentation.album.components.AlbumCard
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.sweetselect.rememberSweetSelectState

@Composable
fun SharedTransitionScope.ArtistDetailsScreen(
    state: ArtistDetailsState,
    onNavigate: (Screen) -> Unit,
    onNavigateBack: () -> Unit,
    textFieldState: TextFieldState,
    musicState: MusicState,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {
    val lazyState = rememberLazyListState()
    val multiSelectState = rememberSweetSelectState<CuteTrack>()
    val activeTrackId = remember(musicState.track) { musicState.track.mediaId }



    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            AnimatedContent(
                targetState = multiSelectState.isInSelectionMode,
                transitionSpec = { barsContentTransform }
            ) {
                if (it) {
                    TracksSelectedBar(
                        modifier = Modifier.selfAlignHorizontally(),
                        tracks = state.tracks,
                        multiSelectState = multiSelectState,
                        onHandlePlayerActions = onHandlePlayerAction
                    )
                } else {
                    CuteSearchbar(
                        modifier = Modifier.selfAlignHorizontally(),
                        musicState = musicState,
                        textFieldState = textFieldState,
                        onHandlePlayerActions = onHandlePlayerAction,
                        onNavigate = onNavigate,
                        backButton = { CuteSearchbarDefaults.BackButton(onNavigateBack) },
                        fab = {
                            AnimatedFab(
                                onClick = {
                                    onHandlePlayerAction(
                                        PlayerActions.PlayFromSource(
                                            mediaId = null,
                                            source = PlaySource.Artist(state.artist.name)
                                        )
                                    )
                                },
                                icon = R.drawable.shuffle
                            )
                        },
                        sortMenu = {
                            CuteSearchbarDefaults.TrackSortPopupContent()
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        LoadingBox(
            isLoading = state.isLoading
        ) {
            LazyColumn(
                state = lazyState,
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
            ) {

                item(
                    key = "Header"
                ) {
                    ArtistHeader(
                        artist = state.artist,
                        tracks = state.tracks,
                        onHandlePlayerActions = onHandlePlayerAction
                    )
                }

                if (state.albums.isNotEmpty()) {
                    item(
                        key = "Albums"
                    ) {
                        NumberOfAlbums(state.albums.size)


                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { state.albums.size },
                            preferredItemWidth = 186.dp,
                            itemSpacing = 5.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) { index ->
                            val album = state.albums[index]

                            AlbumCard(
                                album = album,
                                onClick = { onNavigate(Screen.AlbumsDetails(name = album.name)) },
                                onToggleFavorite = { onHandlePlayerAction(PlayerActions.ToggleAlbumFavorite(album.id)) },
                                modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge)
                            )
                        }
                    }
                }


                // Don't check "is searching" considering if we're in an artist's details it means it needs to have at least 1 track
                if (state.tracks.isEmpty()) {
                    item(
                        key = "empty"
                    ) {
                        NoResult(Modifier.animateItem())
                    }
                }


                item(
                    key = "NbTracks"
                ) { NumberOfTracks(size = state.tracks.size) }

                items(
                    items = state.tracks,
                    key = { it.mediaId }
                ) { track ->

                    val isSelected by multiSelectState.isSelectedAsState(track)

                    SelectOverlayItem(
                        modifier = Modifier.animateItem(),
                        enabled = !multiSelectState.isInSelectionMode,
                        actions = trackSelectActions(
                            track = track,
                            onNavigate = onNavigate,
                            onHandlePlayerAction = onHandlePlayerAction
                        )
                    ) { showOverlay ->
                        MusicListItem(
                            track = track,
                            onShortClick = {
                                if (multiSelectState.isInSelectionMode) {
                                    multiSelectState.toggle(track)
                                } else {
                                    onHandlePlayerAction(
                                        PlayerActions.PlayFromSource(
                                            mediaId = track.mediaId,
                                            source = PlaySource.Artist(state.artist.name)
                                        )
                                    )
                                }
                            },
                            onLongClick = {
                                if (multiSelectState.isInSelectionMode) {
                                    multiSelectState.toggle(track)
                                } else {
                                    showOverlay()
                                }
                            },
                            isSelected = isSelected,
                            isActive = track.mediaId == activeTrackId,
                            trailingContent = {
                                DefaultMusicListItemTrailingContent(
                                    track = track,
                                    onNavigate = onNavigate,
                                    onHandlePlayerActions = onHandlePlayerAction
                                )
                            }
                        )
                    }
                }
            }
        }

    }

}
