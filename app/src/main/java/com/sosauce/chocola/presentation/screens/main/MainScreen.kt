@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class
)

package com.sosauce.chocola.presentation.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberGroupByFolders
import com.sosauce.chocola.data.datastore.rememberHiddenFolders
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlaySource
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.CuteSearchbar
import com.sosauce.chocola.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.presentation.components.DefaultMusicListItemTrailingContent
import com.sosauce.chocola.presentation.components.MusicListItem
import com.sosauce.chocola.presentation.components.NoResult
import com.sosauce.chocola.presentation.components.TracksSelectedBar
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.main.components.FolderHeader
import com.sosauce.chocola.utils.addOrRemove
import com.sosauce.chocola.utils.barsContentTransform
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.components.NoXFound
import com.sosauce.nekobites.components.Spacer
import com.sosauce.sweetselect.rememberSweetSelectState
import kotlinx.coroutines.FlowPreview

// https://medium.com/@gregkorossy/hacking-lazylist-in-android-jetpack-compose-38afacb3df67
@Composable
fun SharedTransitionScope.MainScreen(
    state: MainState,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {

    val lazyState = rememberLazyListState()
    var hiddenFolders by rememberHiddenFolders()
    var groupByFolders by rememberGroupByFolders()
    val multiSelectState = rememberSweetSelectState<CuteTrack>()
    val activeTrackId = remember(musicState.track) { musicState.track.mediaId }

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
                    val categories = state.tracks
                        .groupBy { it.folder }
                        .toSortedMap()
                        .map { (folder, tracks) ->
                            Category(
                                path = folder,
                                name = folder.substringAfterLast('/'),
                                tracks = tracks
                            )
                        }
                    categories.fastForEach { category ->
                        item {
                            FolderHeader(
                                modifier = Modifier.animateItem(),
                                category = category,
                                isHidden = category.path in hiddenFolders,
                                onToggleVisibility = {
                                    hiddenFolders =
                                        hiddenFolders.copyMutate { addOrRemove(category.path) }
                                },
                                onHandlePlayerAction = onHandlePlayerAction
                            )
                        }
                        if (category.path !in hiddenFolders) {
                            items(
                                items = category.tracks,
                                key = { it.mediaId }
                            ) { track ->
                                val isSelected by multiSelectState.isSelectedAsState(track)

                                MusicListItem(
                                    modifier = Modifier.animateItem(),
                                    onShortClick = {
                                        if (multiSelectState.isInSelectionMode) {
                                            multiSelectState.toggle(track)
                                        } else {
                                            onHandlePlayerAction(
                                                PlayerActions.PlayFromSource(
                                                    mediaId = track.mediaId,
                                                    source = PlaySource.All
                                                )
                                            )
                                        }
                                    },
                                    onLongClick = { multiSelectState.toggle(track) },
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

                } else {
                    items(
                        items = state.tracks,
                        key = { it.mediaId },
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
                                            source = PlaySource.All
                                        )
                                    )
                                }
                            },
                            onLongClick = { multiSelectState.toggle(track) },
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

data class Category(
    val path: String,
    val name: String,
    val tracks: List<CuteTrack>
)




