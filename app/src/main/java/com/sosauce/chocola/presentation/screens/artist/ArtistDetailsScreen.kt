@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.artist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sosauce.chocola.data.datastore.rememberSortTracksAscending
import com.sosauce.chocola.data.datastore.rememberTrackSort
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.CuteSearchbar
import com.sosauce.chocola.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.presentation.components.DefaultMusicListItemTrailingContent
import com.sosauce.chocola.presentation.components.LoadingBox
import com.sosauce.chocola.presentation.components.MusicListItem
import com.sosauce.chocola.presentation.components.NoResult
import com.sosauce.chocola.presentation.components.NoXFound
import com.sosauce.chocola.presentation.components.TracksSelectedBar
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.album.components.NumberOfTracks
import com.sosauce.chocola.presentation.screens.artist.components.ArtistHeader
import com.sosauce.chocola.presentation.screens.artist.components.NumberOfAlbums
import com.sosauce.chocola.utils.ImageUtils
import com.sosauce.chocola.utils.barsContentTransform
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.sweetselect.rememberSweetSelectState

@Composable
fun SharedTransitionScope.ArtistDetailsScreen(
    state: ArtistDetailsState,
    onNavigate: (Screen) -> Unit,
    textFieldState: TextFieldState,
    musicState: MusicState,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {
    val lazyState = rememberLazyListState()
    val multiSelectState = rememberSweetSelectState<CuteTrack>()


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
                        fab = {
                            AnimatedFab(
                                onClick = {
                                    onHandlePlayerAction(
                                        PlayerActions.Play(
                                            index = 0,
                                            tracks = state.tracks,
                                            random = true
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
                        NumberOfAlbums(state.artist.numberAlbums)

                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { state.albums.count() },
                            preferredItemWidth = 186.dp,
                            itemSpacing = 5.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 16.dp)
                        ) { index ->
                            val album = state.albums[index]

                            Box(
                                modifier = Modifier
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .background(MaterialTheme.colorScheme.surfaceContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.album_filled),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                                AsyncImage(
                                    model = ImageUtils.getAlbumArt(album.id),
                                    contentDescription = stringResource(id = R.string.artwork),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                                            )
                                        )
                                        .padding(15.dp)
                                ) {
                                    Text(
                                        text = album.name,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.titleMediumEmphasized,
                                        modifier = Modifier
                                            .sharedBounds(
                                                sharedContentState = rememberSharedContentState(album.name + album.id),
                                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                            )
                                            .basicMarquee()
                                    )
                                    Text(
                                        text = album.artist,
                                        style = MaterialTheme.typography.bodyLargeEmphasized,
                                        modifier = Modifier
                                            .sharedElement(
                                                sharedContentState = rememberSharedContentState(album.artist + album.id),
                                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                            )
                                            .basicMarquee()

                                    )
                                }
                            }
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

                    val isSelected by remember {
                        derivedStateOf { multiSelectState.isSelected(track) }
                    }

                    MusicListItem(
                        modifier = Modifier.animateItem(),
                        track = track,
                        musicState = musicState,
                        onShortClick = {
                            if (multiSelectState.isInSelectionMode) {
                                multiSelectState.toggle(track)
                            } else {
                                onHandlePlayerAction(
                                    PlayerActions.Play(
                                        index = state.tracks.indexOf(track),
                                        tracks = state.tracks
                                    )
                                )
                            }
                        },
                        onLongClick = { multiSelectState.toggle(track) },
                        isSelected = isSelected,
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