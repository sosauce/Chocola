@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.chocola.R
import com.sosauce.chocola.core.presentation.preferences.rememberAppTheme
import com.sosauce.chocola.core.presentation.preferences.rememberPaletteStyle
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.core.presentation.components.DefaultMusicListItemTrailingContent
import com.sosauce.chocola.core.presentation.components.MoreOptions
import com.sosauce.chocola.core.presentation.components.MusicListItem
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.presentation.components.TracksSelectedBar
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.designsystem.components.NumberOfTracks
import com.sosauce.chocola.feature.playlists.presentation.components.EmptyPlaylist
import com.sosauce.chocola.feature.playlists.presentation.components.PlaylistHeader
import com.sosauce.chocola.core.designsystem.CuteTheme
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.chocola.core.domain.util.copyMutate
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.core.presentation.util.toPaletteStyle
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.utils.ColorUtils.toColor
import com.sosauce.sweetselect.rememberSweetSelectState
import kotlin.random.Random

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
            seedColor = state.playlist.color.toColor(MaterialTheme.colorScheme.primary),
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
                            fab = {
                                AnimatedFab(
                                    onClick = {
                                        onHandlePlayerAction(
                                            PlayerActions.PlayFromSource(
                                                mediaId = null,
                                                source = PlaySource.ExplicitTracks(state.playlistTracks)
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
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    state = listState
                ) {


                    if (state.tracks.isEmpty()) {
                        item(
                            key = "empty"
                        ) {
                            if (textFieldState.text.isEmpty()) {
                                EmptyPlaylist(state.playlist.emoji)
                            } else {
                                NoResult(Modifier.animateItem())
                            }

                        }
                    } else {
                        item("header") {
                            PlaylistHeader(
                                playlist = state.playlist,
                                tracks = state.playlistTracks,
                                onHandlePlayerActions = onHandlePlayerAction
                            )
                            NumberOfTracks(size = state.tracks.size)
                        }
                    }

                    items(
                        items = state.tracks,
                        key = { it.mediaId }
                    ) { track ->

                        val isSelected by multiSelectState.isSelectedAsState(track)

                        MusicListItem(
                            modifier = Modifier
                                .animateItem(),
                            onShortClick = {
                                if (multiSelectState.isInSelectionMode) {
                                    multiSelectState.toggle(track)
                                } else {
                                    onHandlePlayerAction(
                                        PlayerActions.PlayFromSource(
                                            mediaId = track.mediaId,
                                            source = PlaySource.ExplicitTracks(state.playlistTracks)
                                        )
                                    )
                                }
                            },
                            isSelected = isSelected,
                            onLongClick = { multiSelectState.toggle(track) },
                            track = track,
                            isActive = track.mediaId == activeTrackId,
                            trailingContent = {
                                DefaultMusicListItemTrailingContent(
                                    track = track,
                                    onNavigate = onNavigate,
                                    onHandlePlayerActions = onHandlePlayerAction,
                                    extraOptions = listOf(
                                        MoreOptions(
                                            text = { stringResource(R.string.remove_from_playlist) },
                                            icon = R.drawable.playlist_remove,
                                            onClick = {
                                                onHandlePlaylistAction(
                                                    PlaylistActions.UpsertPlaylist(
                                                        state.playlist.copy(
                                                            musics = state.playlist.musics.copyMutate {
                                                                remove(
                                                                    track.mediaId
                                                                )
                                                            }
                                                        )
                                                    )
                                                )
                                            }
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


}