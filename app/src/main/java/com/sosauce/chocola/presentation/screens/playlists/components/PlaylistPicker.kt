@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playlists.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.domain.actions.PlaylistActions
import com.sosauce.chocola.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.chocola.utils.ICON_TEXT_SPACING
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.selfAlignHorizontally
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistPicker(
    mediaId: List<String>,
    onDismissRequest: () -> Unit,
    onAddingFinished: () -> Unit = {}
) {
    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val state by playlistViewModel.state.collectAsStateWithLifecycle()
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }


    if (showPlaylistCreatorDialog) {
        CreatePlaylistDialog { showPlaylistCreatorDialog = false }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.secondary) }
    ) {
        LazyColumn {
            item {
                Button(
                    onClick = { showPlaylistCreatorDialog = true },
                    modifier = Modifier.selfAlignHorizontally(),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                        Text(stringResource(R.string.create_playlist))
                    }
                }
            }

            items(
                items = state.playlists,
                key = { it.id }
            ) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClickPlaylist = {
                        playlistViewModel.handlePlaylistActions(
                            PlaylistActions.UpsertPlaylist(
                                playlist.copy(
                                    musics = playlist.musics.copyMutate {
                                        mediaId.fastForEach { id ->
                                            if (!contains(id)) {
                                                add(id)
                                            }
                                        }
                                    }
                                )
                            )
                        )
                        onAddingFinished()
                    },
                    onHandlePlaylistActions = playlistViewModel::handlePlaylistActions,
                    enabled = mediaId.fastAny { it !in playlist.musics }
                )
            }
        }
    }
}