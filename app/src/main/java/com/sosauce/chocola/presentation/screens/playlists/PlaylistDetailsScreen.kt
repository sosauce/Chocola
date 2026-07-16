@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playlists

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAppTheme
import com.sosauce.chocola.data.datastore.rememberPaletteStyle
import com.sosauce.chocola.data.datastore.rememberSortTracksAscending
import com.sosauce.chocola.data.datastore.rememberTrackSort
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.domain.actions.PlaylistActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.album.components.NumberOfTracks
import com.sosauce.chocola.presentation.screens.playlists.components.EmptyPlaylist
import com.sosauce.chocola.presentation.screens.playlists.components.PlaylistHeader
import com.sosauce.chocola.presentation.shared_components.CuteSearchbar
import com.sosauce.chocola.presentation.shared_components.DefaultMusicListItemTrailingContent
import com.sosauce.chocola.presentation.shared_components.MoreOptions
import com.sosauce.chocola.presentation.shared_components.MusicListItem
import com.sosauce.chocola.presentation.shared_components.SortingDropdownMenu
import com.sosauce.chocola.presentation.shared_components.TracksSelectedBar
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedFab
import com.sosauce.chocola.utils.CuteTheme
import com.sosauce.chocola.utils.barsContentTransform
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.chocola.utils.toPaletteStyle
import com.sosauce.sweetselect.rememberSweetSelectState

@Composable
fun SharedTransitionScope.PlaylistDetailsScreen(
    state: PlaylistDetailsState,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandlePlaylistAction: (PlaylistActions) -> Unit,
    onNavigateUp: () -> Unit
) {

    val listState = rememberLazyListState()
    val theme by rememberAppTheme()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val paletteStyle by rememberPaletteStyle()
    var sortTracksAsc by rememberSortTracksAscending()
    var trackSort by rememberTrackSort()
    val multiSelectState = rememberSweetSelectState<CuteTrack>()




    DynamicMaterialExpressiveTheme(
        state = rememberDynamicMaterialThemeState(
            seedColor = state.playlist.color.takeIf { it != -1 }?.let { Color(it) }
                ?: MaterialTheme.colorScheme.primary,
            isDark = if (theme == CuteTheme.SYSTEM) isSystemInDarkTheme else if (theme == CuteTheme.AMOLED) true else theme == CuteTheme.DARK,
            isAmoled = theme == CuteTheme.AMOLED,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
            style = paletteStyle.toPaletteStyle()
        )
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ContainedLoadingIndicator()
            }
        } else {
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
                                showSearchField = false,
                                onNavigate = onNavigate,
                                onNavigateUp = onNavigateUp,
                                modifier = Modifier.selfAlignHorizontally(),
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
                                }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    state = listState
                ) {
                    if (state.tracks.isEmpty()) {
                        item("empty header") { EmptyPlaylist(state.playlist.emoji) }
                    } else {
                        item("header") {
                            PlaylistHeader(
                                playlist = state.playlist,
                                tracks = state.tracks,
                                onHandlePlayerActions = onHandlePlayerAction
                            )
                        }

                        item("track number") {
                            NumberOfTracks(
                                size = state.tracks.size,
                                sortMenu = {
                                    SortingDropdownMenu(
                                        isSortedAscending = sortTracksAsc,
                                        onChangeSorting = { sortTracksAsc = it },
                                        isMatchCaseFilter = null,
                                        isRegexFilter = null,
                                        onChangeMatchCaseFilter = null,
                                        onChangeRegexFilter = null,
                                    ) {
                                        repeat(6) { i ->
                                            val text = when (i) {
                                                0 -> R.string.title
                                                1 -> R.string.artist
                                                2 -> R.string.album
                                                3 -> R.string.year
                                                4 -> R.string.date_modified
                                                5 -> R.string.as_added
                                                else -> throw IndexOutOfBoundsException()
                                            }
                                            DropdownMenuItem(
                                                selected = trackSort == i,
                                                onClick = { trackSort = i },
                                                shapes = MenuDefaults.itemShape(i, 6),
                                                colors = MenuDefaults.selectableItemColors(),
                                                text = { Text(stringResource(text)) },
                                                trailingIcon = {
                                                    if (trackSort == i) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.check),
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        items(
                            items = state.tracks,
                            key = { it.mediaId }
                        ) { track ->

                            val isSelected by remember {
                                derivedStateOf { multiSelectState.isSelected(track) }
                            }

                            MusicListItem(
                                modifier = Modifier
                                    .animateItem(),
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
                                isSelected = isSelected,
                                onLongClick = { multiSelectState.toggle(track) },
                                track = track,
                                musicState = musicState,
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


}