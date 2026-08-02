@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playlists

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberMatchCaseFilter
import com.sosauce.chocola.data.datastore.rememberPlaylistSort
import com.sosauce.chocola.data.datastore.rememberRegexFilter
import com.sosauce.chocola.data.datastore.rememberSortPlaylistsAscending
import com.sosauce.chocola.data.models.Playlist
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.domain.actions.PlaylistActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playlists.components.CreatePlaylistDialog
import com.sosauce.chocola.presentation.screens.playlists.components.PlaylistItem
import com.sosauce.chocola.presentation.shared_components.CuteSearchbar
import com.sosauce.chocola.presentation.shared_components.NoResult
import com.sosauce.chocola.presentation.shared_components.NoXFound
import com.sosauce.chocola.presentation.shared_components.SelectedBarSurface
import com.sosauce.chocola.presentation.shared_components.SortingDropdownMenu
import com.sosauce.chocola.presentation.shared_components.animations.ToggleAnimatedFab
import com.sosauce.chocola.presentation.shared_components.dialogs.PlaylistDeletionDialog
import com.sosauce.chocola.utils.barsContentTransform
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.sweetselect.rememberSweetSelectState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsScreen(
    state: PlaylistsState,
    onHandlePlaylistAction: (PlaylistActions) -> Unit,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }
    val lazyState = rememberLazyListState()
    var isSortedByASC by rememberSortPlaylistsAscending()
    var regexFilter by rememberRegexFilter()
    var matchCaseFilter by rememberMatchCaseFilter()
    var fabMenuExpanded by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    var playlistSort by rememberPlaylistSort()
    val multiSelectState = rememberSweetSelectState<Playlist>()


    val importPlaylistLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                if (!it.toString().endsWith(".m3u")) {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.not_m3u_file),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onHandlePlaylistAction(PlaylistActions.ImportM3uPlaylist(it))
                }
            }

        }

    if (showPlaylistCreatorDialog) {
        CreatePlaylistDialog { showPlaylistCreatorDialog = false }
    }

    if (showDeletionDialog) {
        PlaylistDeletionDialog(
            playlists = multiSelectState.selectedItems.toList(),
            onHandlePlaylistAction = onHandlePlaylistAction,
            onDismissRequest = { showDeletionDialog = false }
        )
    }

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
                ) { isInSelectionMode ->
                    if (isInSelectionMode) {
                        SelectedBarSurface(
                            modifier = Modifier.selfAlignHorizontally(),
                            items = state.playlists,
                            multiSelectState = multiSelectState
                        ) {
                            Button(
                                onClick = { showDeletionDialog = true },
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.trash_rounded_filled),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    } else {
                        CuteSearchbar(
                            modifier = Modifier.selfAlignHorizontally(),
                            textFieldState = state.textFieldState,
                            musicState = musicState,
                            sortingMenu = {
                                SortingDropdownMenu(
                                    isSortedAscending = isSortedByASC,
                                    onChangeSorting = { isSortedByASC = it },
                                    isMatchCaseFilter = matchCaseFilter,
                                    onChangeMatchCaseFilter = { matchCaseFilter = it },
                                    isRegexFilter = regexFilter,
                                    onChangeRegexFilter = { regexFilter = it },
                                ) {
                                    repeat(4) { i ->
                                        val text = when (i) {
                                            0 -> R.string.name
                                            1 -> R.string.number_of_tracks
                                            2 -> R.string.tags
                                            3 -> R.string.color
                                            else -> throw IndexOutOfBoundsException()
                                        }
                                        DropdownMenuItem(
                                            selected = playlistSort == i,
                                            onClick = { playlistSort = i },
                                            shapes = MenuDefaults.itemShape(i, 4),
                                            colors = MenuDefaults.selectableItemColors(),
                                            text = { Text(stringResource(text)) },
                                            trailingContent = {
                                                if (playlistSort == i) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.check),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        )

                                    }
                                }
                            },
                            onHandlePlayerActions = onHandlePlayerAction,
                            onNavigate = onNavigate,
                            fab = {
                                FloatingActionButtonMenu(
                                    modifier = Modifier.offset(
                                        12.dp,
                                        12.dp
                                    ),
                                    expanded = fabMenuExpanded,
                                    button = {
                                        ToggleAnimatedFab(
                                            checked = fabMenuExpanded,
                                            onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                                        ) { checkedProgress ->
                                            if (checkedProgress > 0.5f) R.drawable.close else R.drawable.add
                                        }
                                    }
                                ) {
                                    FloatingActionButtonMenuItem(
                                        onClick = {
                                            importPlaylistLauncher.launch(arrayOf("*/*"))
                                            fabMenuExpanded = false
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.resource_import),
                                                contentDescription = null
                                            )
                                        },
                                        text = { Text(stringResource(R.string.import_playlist)) },
                                    )
                                    FloatingActionButtonMenuItem(
                                        onClick = {
                                            showPlaylistCreatorDialog = true
                                            fabMenuExpanded = false
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.playlist_add),
                                                contentDescription = null
                                            )
                                        },
                                        text = { Text(stringResource(R.string.create_playlist)) },
                                    )
                                }

                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
                state = lazyState
            ) {
                if (state.playlists.isEmpty() && !state.isSearching) {
                    item {
                        NoXFound(
                            headlineText = R.string.no_playlists,
                            bodyText = R.string.no_playlist_desc,
                            icon = R.drawable.playlist
                        )
                    }
                } else {
                    if (state.playlists.isEmpty()) {
                        item { NoResult() }
                    } else {
                        items(
                            items = state.playlists,
                            key = { it.id }
                        ) { playlist ->

                            val isSelected by remember {
                                derivedStateOf { multiSelectState.isSelected(playlist) }
                            }

                            PlaylistItem(
                                modifier = Modifier.animateItem(),
                                playlist = playlist,
                                isSelected = isSelected,
                                onHandlePlaylistActions = onHandlePlaylistAction,
                                onClickPlaylist = {
                                    if (multiSelectState.isInSelectionMode) {
                                        multiSelectState.toggle(playlist)
                                    } else {
                                        onNavigate(Screen.PlaylistDetails(playlist.id))
                                    }
                                },
                                onLongClick = { multiSelectState.toggle(playlist) }
                            )
                        }
                    }
                }
            }
        }
    }
}