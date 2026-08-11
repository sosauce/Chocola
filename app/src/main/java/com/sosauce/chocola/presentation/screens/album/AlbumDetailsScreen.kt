@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberIsLandscape
import com.sosauce.chocola.data.datastore.rememberMatchCaseFilter
import com.sosauce.chocola.data.datastore.rememberRegexFilter
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
import com.sosauce.chocola.presentation.screens.album.components.AlbumHeader
import com.sosauce.chocola.presentation.screens.album.components.NumberOfTracks
import com.sosauce.chocola.utils.barsContentTransform
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.sweetselect.rememberSweetSelectState

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    state: AlbumDetailsState,
    textFieldState: TextFieldState,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val lazyState = rememberLazyListState()
    val multiSelectState = rememberSweetSelectState<CuteTrack>()


    Scaffold(
        bottomBar = {
            AnimatedContent(
                targetState = multiSelectState.isInSelectionMode,
                transitionSpec = { barsContentTransform }
            ) {
                if (it) {
                    TracksSelectedBar(
                        modifier = Modifier.selfAlignHorizontally(),
                        tracks = state.album.tracks,
                        multiSelectState = multiSelectState,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                } else {
                    CuteSearchbar(
                        modifier = Modifier.selfAlignHorizontally(),
                        musicState = musicState,
                        textFieldState = textFieldState,
                        onHandlePlayerActions = onHandlePlayerActions,
                        onNavigate = onNavigate,
                        backButton = { CuteSearchbarDefaults.BackButton(onNavigateUp) },
                        fab = {
                            AnimatedFab(
                                onClick = {
                                    onHandlePlayerActions(
                                        PlayerActions.Play(
                                            index = 0,
                                            tracks = state.album.tracks,
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
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
            ) {
                item(
                    key = "Header"
                ) {
                    AlbumHeader(
                        album = state.album,
                        tracks = state.album.tracks,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                    if (state.album.tracks.isNotEmpty()) {
                        NumberOfTracks(size = state.album.tracks.size)
                    }
                }

                // Don't check "is searching" considering if we're in an album's details it means it needs to have at least 1 track
                if (state.album.tracks.isEmpty()) {
                    item(
                        key = "empty"
                    ) {
                        NoResult(Modifier.animateItem())
                    }
                }

                items(
                    items = state.album.tracks,
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
                                onHandlePlayerActions(
                                    PlayerActions.Play(
                                        index = state.album.tracks.indexOf(track),
                                        tracks = state.album.tracks
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
                                onHandlePlayerActions = onHandlePlayerActions
                            )
                        }
                    )
                }
            }
        }

    }

}
