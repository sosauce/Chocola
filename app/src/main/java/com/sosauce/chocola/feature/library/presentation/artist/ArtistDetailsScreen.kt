@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.feature.library.presentation.artist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.core.presentation.components.TracksSelectedBar
import com.sosauce.chocola.core.presentation.components.trackItems
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.feature.library.presentation.album.components.AlbumCard
import com.sosauce.chocola.feature.library.presentation.artist.components.ArtistHeader
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
                        albumCount = state.albums.size,
                        onHandlePlayerActions = onHandlePlayerAction
                    )
                    Spacer(Modifier.height(5.dp))
                }

                if (state.albums.isNotEmpty()) {
                    item(
                        key = "Albums"
                    ) {
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
                                onHandlePlayerActions = onHandlePlayerAction,
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


                trackItems(
                    tracks = state.tracks,
                    multiSelectState = multiSelectState,
                    activeTrackId = activeTrackId,
                    source = PlaySource.Artist(state.artist.name),
                    onNavigate = onNavigate,
                    onHandlePlayerAction = onHandlePlayerAction
                )
            }
        }

    }

}
