@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.designsystem.components.SelectOverlayAction
import com.sosauce.chocola.core.designsystem.components.SelectOverlayItem
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.SelectedBarSurface
import com.sosauce.chocola.core.presentation.components.dialogs.PlaylistDeletionDialog
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.feature.playlists.presentation.components.CreatePlaylistDialog
import com.sosauce.chocola.feature.playlists.presentation.components.EditPlaylist
import com.sosauce.chocola.feature.playlists.presentation.components.FavoritePlaylistItem
import com.sosauce.chocola.feature.playlists.presentation.components.PlaylistItem
import com.sosauce.chocola.feature.playlists.presentation.model.PlaylistUi
import com.sosauce.nekobites.animations.ToggleAnimatedFab
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.components.NoXFound
import com.sosauce.sweetselect.rememberSweetSelectState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsScreen(
    state: PlaylistsState,
    textFieldState: TextFieldState,
    onHandlePlaylistAction: (PlaylistActions) -> Unit,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }
    val lazyState = rememberLazyListState()
    var fabMenuExpanded by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    val multiSelectState = rememberSweetSelectState<PlaylistUi>()


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
                        musicState = musicState,
                        textFieldState = textFieldState,
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

                        },
                        sortMenu = {

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
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
                state = lazyState
            ) {
                if (state.favoritePlaylists.isNotEmpty()) {
                    item(key = "favorites") {
                        LazyRow(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.favoritePlaylists,
                                key = { it.id }
                            ) { playlist ->
                                val isSelected by multiSelectState.isSelectedAsState(playlist)

                                SelectOverlayItem(
                                    modifier = Modifier.animateItem(),
                                    enabled = !multiSelectState.isInSelectionMode,
                                    actions = playlistSelectActions(
                                        playlist = playlist,
                                        onHandlePlaylistAction = onHandlePlaylistAction
                                    ),
                                    onSelect = { multiSelectState.addAll(listOf(playlist)) }
                                ) { showOverlay ->
                                    FavoritePlaylistItem(
                                        playlist = playlist,
                                        isSelected = isSelected,
                                        isInSelectionMode = multiSelectState.isInSelectionMode,
                                        onClick = {
                                            if (multiSelectState.isInSelectionMode) {
                                                multiSelectState.toggle(playlist)
                                            } else {
                                                onNavigate(Screen.PlaylistDetails(playlist.id))
                                            }
                                        },
                                        onLongClick = {
                                            if (multiSelectState.isInSelectionMode) {
                                                multiSelectState.toggle(playlist)
                                            } else {
                                                showOverlay()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (state.playlists.isEmpty()) {
                    item(
                        key = "empty"
                    ) {

                        if (textFieldState.text.isEmpty()) {
                            NoXFound(
                                modifier = Modifier.animateItem(),
                                headlineText = R.string.no_playlists,
                                bodyText = R.string.no_playlist_desc,
                                icon = R.drawable.playlist
                            )
                        } else {
                            NoResult(modifier = Modifier.animateItem())
                        }

                    }
                }
                items(
                    items = state.nonFavoritePlaylists,
                    key = { it.id }
                ) { playlist ->

                    val isSelected by multiSelectState.isSelectedAsState(playlist)

                    SelectOverlayItem(
                        modifier = Modifier.animateItem(),
                        enabled = !multiSelectState.isInSelectionMode,
                        actions = playlistSelectActions(
                            playlist = playlist,
                            onHandlePlaylistAction = onHandlePlaylistAction
                        ),
                        onSelect = { multiSelectState.addAll(listOf(playlist)) }
                    ) { showOverlay ->
                        PlaylistItem(
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
                            onLongClick = {
                                if (multiSelectState.isInSelectionMode) {
                                    multiSelectState.toggle(playlist)
                                } else {
                                    showOverlay()
                                }
                            }
                        )
                    }
                }
            }
        }

    }
}

/**
 * Actions shown when [playlist] is lifted into the select overlay, together with the dialogs they open.
 * Mirrors the options in [PlaylistItem], so both ways of reaching a playlist's actions stay in sync.
 */
@Composable
private fun playlistSelectActions(
    playlist: PlaylistUi,
    onHandlePlaylistAction: (PlaylistActions) -> Unit
): List<SelectOverlayAction> {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    val exportPlaylistLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
            uri?.let {
                onHandlePlaylistAction(
                    PlaylistActions.ExportM3uPlaylist(
                        uri = it,
                        tracks = playlist.tracks.mapTo(mutableSetOf()) { it.mediaId }
                    )
                )
            }
        }

    if (showEditDialog) {
        EditPlaylist(
            playlist = playlist,
            onDismissRequest = { showEditDialog = false },
            onHandlePlaylistActions = onHandlePlaylistAction
        )
    }

    if (showDeletionDialog) {
        PlaylistDeletionDialog(
            playlists = listOf(playlist),
            onHandlePlaylistAction = onHandlePlaylistAction,
            onDismissRequest = { showDeletionDialog = false }
        )
    }

    return listOf(
        SelectOverlayAction(
            label = stringResource(if (playlist.isFavorite) R.string.remove_favorite else R.string.add_favorite),
            icon = if (playlist.isFavorite) R.drawable.favorite_filled else R.drawable.favorite,
            onClick = {
                onHandlePlaylistAction(
                    PlaylistActions.SetFavorite(
                        playlist.id,
                        !playlist.isFavorite
                    )
                )
            }
        ),
        SelectOverlayAction(
            label = stringResource(R.string.edit_playlist),
            icon = R.drawable.edit_rounded,
            onClick = { showEditDialog = true }
        ),
        SelectOverlayAction(
            label = stringResource(R.string.export_playlist),
            icon = R.drawable.export,
            onClick = { exportPlaylistLauncher.launch("${playlist.name.ifEmpty { "Playlist" }}.m3u") }
        ),
        SelectOverlayAction(
            label = stringResource(R.string.delete),
            icon = R.drawable.trash_rounded,
            isDestructive = true,
            onClick = { showDeletionDialog = true }
        )
    )
}
