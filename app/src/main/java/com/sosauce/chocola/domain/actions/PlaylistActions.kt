package com.sosauce.chocola.domain.actions

import android.net.Uri
import com.sosauce.chocola.data.models.Playlist

sealed interface PlaylistActions {

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