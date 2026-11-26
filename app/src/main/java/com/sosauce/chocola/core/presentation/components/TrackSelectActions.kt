package com.sosauce.chocola.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.SelectOverlayAction
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.dialogs.DeletionDialog
import com.sosauce.chocola.core.presentation.navigation.Screen

/**
 * Actions shown when [track] is lifted into the select overlay, together with the dialogs they open.
 * Mirrors [TrackDropdownMenu], so both ways of reaching a track's actions stay in sync.
 * @param extraActions inserted before the destructive delete action, for screen specific actions
 */
@Composable
fun trackSelectActions(
    track: CuteTrack,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    extraActions: List<SelectOverlayAction> = emptyList()
): List<SelectOverlayAction> {
    var showPlaylistPicker by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }

    if (showPlaylistPicker) {
        PlaylistPicker(
            mediaId = listOf(track.mediaId),
            onDismissRequest = { showPlaylistPicker = false }
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = listOf(track),
            onDismissRequest = { showDeletionDialog = false }
        )
    }

    return buildList {
        add(
            SelectOverlayAction(
                label = stringResource(R.string.play_next),
                icon = R.drawable.fast_forward,
                onClick = { onHandlePlayerAction(PlayerActions.PlayNext(track)) }
            )
        )
        add(
            SelectOverlayAction(
                label = stringResource(R.string.add_queue),
                icon = R.drawable.add_to_queue,
                onClick = { onHandlePlayerAction(PlayerActions.AddToQueue(listOf(track))) }
            )
        )
        add(
            SelectOverlayAction(
                label = stringResource(R.string.add_to_playlist),
                icon = R.drawable.playlist_add,
                onClick = { showPlaylistPicker = true }
            )
        )
        add(
            SelectOverlayAction(
                label = stringResource(R.string.go_to, track.album),
                icon = androidx.media3.session.R.drawable.media3_icon_album,
                onClick = { onNavigate(Screen.AlbumsDetails(track.album)) }
            )
        )
        add(
            SelectOverlayAction(
                label = stringResource(R.string.go_to, track.artist),
                icon = R.drawable.artist_rounded,
                onClick = { onNavigate(Screen.ArtistsDetails(track.artist)) }
            )
        )
        add(
            SelectOverlayAction(
                label = stringResource(
                    if (track.isFavorite) R.string.remove_favorite else R.string.add_favorite
                ),
                icon = if (track.isFavorite) R.drawable.favorite_filled else R.drawable.favorite,
                onClick = {
                    track.mediaId.toLongOrNull()?.let {
                        onHandlePlayerAction(PlayerActions.ToggleFavorite(it))
                    }
                }
            )
        )
        addAll(extraActions)
        add(
            SelectOverlayAction(
                label = stringResource(R.string.delete),
                icon = R.drawable.trash_rounded_filled,
                isDestructive = true,
                onClick = { showDeletionDialog = true }
            )
        )
    }
}
