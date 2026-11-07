package com.sosauce.chocola.feature.library.presentation.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarArrangement
import androidx.compose.material3.ShortNavigationBarDefaults
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
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
import com.sosauce.chocola.core.designsystem.components.PlayPauseButton
import com.sosauce.chocola.core.presentation.components.TracksSelectedBar
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.feature.library.presentation.main.components.FolderHeader
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.components.NoXFound
import com.sosauce.nekobites.components.Spacer
import com.sosauce.sweetselect.rememberSweetSelectState
import kotlinx.coroutines.FlowPreview
import com.sosauce.chocola.feature.library.presentation.main.components.FavoriteTrackItem
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.PaddingValues

// https://medium.com/@gregkorossy/hacking-lazylist-in-android-jetpack-compose-38afacb3df67
@Composable
fun SharedTransitionScope.MainScreen(
    state: MainState,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onFolderToggle: (String) -> Unit
) {

    val lazyState = rememberLazyListState()
    val multiSelectState = rememberSweetSelectState<CuteTrack>()
    val activeTrackId = remember(musicState.track) { musicState.track.mediaId }

    val groupByFolders = state.groupByFolders

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            AnimatedContent(
                targetState = multiSelectState.isInSelectionMode,
                transitionSpec = { barsContentTransform },
            ) { isInSelectionMode ->
                if (isInSelectionMode) {
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
                            AnimatedVisibility(
                                visible = !groupByFolders,
                                enter = scaleIn(bouncySpec()),
                                exit = scaleOut(bouncySpec())
                            ) {
                                AnimatedFab(
                                    onClick = {
                                        onHandlePlayerAction(
                                            PlayerActions.PlayFromSource(
                                                mediaId = null,
                                                source = PlaySource.All
                                            )
                                        )
                                    },
                                    icon = R.drawable.shuffle
                                )
                            }
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
                contentPadding = paddingValues
            ) {
                if (state.favoriteTracks.isNotEmpty()) {
                    item(key = "favorites") {
                        LazyRow(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.favoriteTracks,
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
                                    FavoriteTrackItem(
                                        track = track,
                                        isSelected = isSelected,
                                        isInSelectionMode = multiSelectState.isInSelectionMode,
                                        onClick = {
                                            if (multiSelectState.isInSelectionMode) {
                                                multiSelectState.toggle(track)
                                            } else {
                                                onHandlePlayerAction(
                                                    PlayerActions.PlayFromSource(
                                                        mediaId = track.mediaId,
                                                        source = PlaySource.ExplicitTracks(state.favoriteTracks)
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
                                        onUnfavorite = {
                                            track.mediaId.toLongOrNull()?.let { onHandlePlayerAction(PlayerActions.ToggleFavorite(it)) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (state.tracks.isEmpty()) {
                    item(
                        key = "empty"
                    ) {
                        if (textFieldState.text.isEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NoXFound(
                                    modifier = Modifier.animateItem(),
                                    headlineText = R.string.no_music_title,
                                    bodyText = R.string.no_music_desc,
                                    icon = R.drawable.music_note
                                )
                                Spacer(15.dp)
                                Text(
                                    text = stringResource(R.string.no_music_tip),
                                    style = MaterialTheme.typography.bodySmallEmphasized.copy(
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        } else {
                            NoResult(modifier = Modifier.animateItem())
                        }

                    }
                }


                if (groupByFolders) {
                    state.categories.fastForEach { category ->
                        item(category.path) {
                            FolderHeader(
                                modifier = Modifier.animateItem(),
                                category = category,
                                isHidden = category.isHidden,
                                onToggleVisibility = {
                                    onFolderToggle(category.path)
                                },
                                onHandlePlayerAction = onHandlePlayerAction
                            )
                        }
                        if (!category.isHidden) {
                            items(
                                items = category.tracks,
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
                                        onShortClick = {
                                            if (multiSelectState.isInSelectionMode) {
                                                multiSelectState.toggle(track)
                                            } else {
                                                onHandlePlayerAction(
                                                    PlayerActions.PlayFromSource(
                                                        mediaId = track.mediaId,
                                                        source = PlaySource.ExplicitTracks(category.tracks)
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
                                        track = track,
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
                } else {
                    items(
                        items = state.nonFavoriteTracks,
                        key = { it.mediaId },
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
                                onShortClick = {
                                    if (multiSelectState.isInSelectionMode) {
                                        multiSelectState.toggle(track)
                                    } else {
                                        onHandlePlayerAction(
                                            PlayerActions.PlayFromSource(
                                                mediaId = track.mediaId,
                                                source = PlaySource.ExplicitTracks(state.nonFavoriteTracks)
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
                                track = track,
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
}
