package com.sosauce.chocola.feature.playlists.presentation

import android.net.Uri
import com.sosauce.chocola.feature.playlists.presentation.model.PlaylistUi

sealed interface PlaylistActions {
    data class SetFavorite(val playlistId: Int, val isFavorite: Boolean) : PlaylistActions

    data class CreatePlaylist(val playlist: PlaylistUi) : PlaylistActions
    data class DeletePlaylists(val playlists: List<PlaylistUi>) : PlaylistActions
    data class UpsertPlaylist(val playlist: PlaylistUi) :
        PlaylistActions // Modify a playlist basically

    data class AddTracksToPlaylist(
        val playlist: PlaylistUi,
        val mediaIds: List<String>
    ) : PlaylistActions

    data class ImportM3uPlaylist(val uri: Uri) : PlaylistActions
    data class ExportM3uPlaylist(
        val uri: Uri,
        val tracks: Set<String>
    ) : PlaylistActions
}
