package com.sosauce.chocola.feature.playlists.presentation

import android.net.Uri
import com.sosauce.chocola.core.data.local.Playlist

sealed interface PlaylistActions {
    data class SetFavorite(val playlistId: Int, val isFavorite: Boolean) : PlaylistActions

    data class CreatePlaylist(val playlist: Playlist) : PlaylistActions
    data class DeletePlaylists(val playlists: List<Playlist>) : PlaylistActions
    data class UpsertPlaylist(val playlist: Playlist) :
        PlaylistActions // Modify a playlist basically

    data class ImportM3uPlaylist(val uri: Uri) : PlaylistActions
    data class ExportM3uPlaylist(
        val uri: Uri,
        val tracks: Set<String>
    ) : PlaylistActions
}
