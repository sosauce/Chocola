@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.CuteTheme
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.designsystem.components.SelectOverlayAction
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.core.presentation.components.MoreOptions
import com.sosauce.chocola.core.presentation.components.TracksSelectedBar
import com.sosauce.chocola.core.presentation.components.trackItems
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.preferences.rememberAppTheme
import com.sosauce.chocola.core.presentation.preferences.rememberPaletteStyle
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.core.presentation.util.toPaletteStyle
import com.sosauce.chocola.feature.playlists.presentation.components.EmptyPlaylist
import com.sosauce.chocola.feature.playlists.presentation.components.PlaylistHeader
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.sweetselect.rememberSweetSelectState

@Composable
fun SharedTransitionScope.PlaylistDetailsScreen(
    state: PlaylistDetailsState,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onNavigate: (Screen) -> Unit,
    onNavigateBack: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandlePlaylistAction: (PlaylistActions) -> Unit
) {

    val listState = rememberLazyListState()
    val theme by rememberAppTheme()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val paletteStyle by rememberPaletteStyle()
    val multiSelectState = rememberSweetSelectState<CuteTrack>()
    val activeTrackId = remember(musicState.track) { musicState.track.mediaId }





    DynamicMaterialExpressiveTheme(
        state = rememberDynamicMaterialThemeState(
            seedColor = state.playlist.color ?: MaterialTheme.colorScheme.primary,
            isDark = if (theme == CuteTheme.SYSTEM) isSystemInDarkTheme else if (theme == CuteTheme.AMOLED) true else theme == CuteTheme.DARK,
            isAmoled = theme == CuteTheme.AMOLED,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
            style = paletteStyle.toPaletteStyle()
        )
    ) {
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
                            onHandlePlayerActions = onHandlePlayerAction,
                            musicState = musicState,
                            textFieldState = textFieldState,
                            onNavigate = onNavigate,
                            modifier = Modifier.selfAlignHorizontally(),
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
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    state = listState
                ) {
                    if (state.tracks.isEmpty()) {
                        item(
                            key = "empty"
                        ) {
                            if (textFieldState.text.isEmpty()) {
                                EmptyPlaylist(state.playlist.emoji.orEmpty())
                            } else {
                                NoResult(Modifier.animateItem())
                            }

                        }
                    } else {
                        item("header") {
                            PlaylistHeader(
                                playlist = state.playlist,
                                tracks = state.playlistTracks,
                                onHandlePlayerActions = onHandlePlayerAction,
                                onToggleFavorite = {
                                    onHandlePlaylistAction(
                                        PlaylistActions.SetFavorite(
                                            playlistId = state.playlist.id,
                                            isFavorite = !state.playlist.isFavorite
                                        )
                                    )
                                }
                            )
                            Spacer(Modifier.height(5.dp))
                        }
                    }

                    trackItems(
                        tracks = state.tracks,
                        multiSelectState = multiSelectState,
                        activeTrackId = activeTrackId,
                        source = PlaySource.ExplicitTracks(state.playlistTracks),
                        onNavigate = onNavigate,
                        onHandlePlayerAction = onHandlePlayerAction,
                        extraOptions = { track ->
                            listOf(
                                MoreOptions(
                                    text = { stringResource(R.string.remove_from_playlist) },
                                    icon = R.drawable.playlist_remove,
                                    onClick = {
                                        onHandlePlaylistAction(
                                            PlaylistActions.UpsertPlaylist(
                                                state.playlist.copy(
                                                    tracks = state.playlist.tracks.filterNot { it.mediaId == track.mediaId }
                                                )
                                            )
                                        )
                                    }
                                )
                            )
                        },
                        extraSelectActions = { track ->
                            listOf(
                                SelectOverlayAction(
                                    label = stringResource(R.string.remove_from_playlist),
                                    icon = R.drawable.playlist_remove,
                                    onClick = {
                                        onHandlePlaylistAction(
                                            PlaylistActions.UpsertPlaylist(
                                                state.playlist.copy(
                                                    tracks = state.playlist.tracks.filterNot { it.mediaId == track.mediaId }
                                                )
                                            )
                                        )
                                    }
                                )
                            )
                        }
                    )

                }
            }
        }
    }


}