@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.feature.playlists.presentation

import android.app.Application
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.R
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.local.Playlist
import com.sosauce.chocola.core.data.local.PlaylistDao
import com.sosauce.chocola.core.data.local.ordered
import com.sosauce.chocola.core.domain.util.combine
import com.sosauce.chocola.feature.playlists.presentation.model.PlaylistUi
import com.sosauce.chocola.feature.playlists.presentation.model.toEntity
import com.sosauce.chocola.feature.playlists.presentation.model.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class PlaylistViewModel(
    private val application: Application,
    private val dao: PlaylistDao,
    private val userPreferences: UserPreferences,
    private val abstractTracksScanner: AbstractTracksScanner
) : AndroidViewModel(application) {

    val textFieldState = TextFieldState()
    private val userQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)

    private val _state = MutableStateFlow(PlaylistsState(isLoading = true))
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dao.getPlaylists(),
                abstractTracksScanner.latestTracks(),
                userPreferences.getPlaylistsSort,
                userPreferences.getRegexFilter,
                userPreferences.getMatchCaseFilter,
                userPreferences.sortPlaylistsAscending,
                userQuery
            ) { playlists, tracks, sort, regex, matchCase, ascending, query ->

                val sortedPlaylists =
                    playlists.ordered(sort, regex, matchCase, ascending, query.toString())
                        .sortedByDescending { it.isFavorite }
                        .map { it.toUi(tracks) }
                val (favoritePlaylists, nonFavoritePlaylists) = sortedPlaylists.partition { it.isFavorite }

                PlaylistsState(
                    playlists = sortedPlaylists,
                    favoritePlaylists = favoritePlaylists,
                    nonFavoritePlaylists = nonFavoritePlaylists,
                    isLoading = false
                )

            }.collectLatest { newState -> _state.update { newState } }
        }
    }


    fun handlePlaylistActions(action: PlaylistActions) {
        when (action) {
            is PlaylistActions.SetFavorite -> viewModelScope.launch(Dispatchers.IO) {
                dao.setFavorite(action.playlistId, action.isFavorite)
            }

            is PlaylistActions.DeletePlaylists -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.deletePlaylist(action.playlists.map { it.toEntity() })
                }
            }

            is PlaylistActions.CreatePlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist.toEntity())
                }
            }

            is PlaylistActions.UpsertPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist.toEntity())
                }
            }

            is PlaylistActions.AddTracksToPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val playlist = action.playlist.toEntity()
                    dao.upsertPlaylist(playlist.copy(musics = playlist.musics + action.mediaIds))
                }
            }

            is PlaylistActions.ImportM3uPlaylist -> {

                val tracksFromFile = mutableSetOf<String>()

                viewModelScope.launch(Dispatchers.IO) {
                    val playlistFolder = File(
                        Environment.getExternalStorageDirectory(),
                        DocumentsContract.getDocumentId(action.uri).substringAfter(':').substringBeforeLast('/', "")
                    )
                    application.contentResolver.openInputStream(action.uri)?.bufferedReader()
                        ?.useLines { lines ->
                            for (line in lines) {
                                if (line.isBlank() || line.startsWith('#')) continue
                                val path = if (line.startsWith('/')) line else playlistFolder.resolve(line).normalize().path
                                tracksFromFile.add(getMediaIdFromFilePath(path))
                            }

                        }

                    val playlist = Playlist(
                        emoji = "",
                        name = action.uri.path?.substringAfterLast('/')?.substringBeforeLast('.')
                            ?: "Imported playlist",
                        musics = tracksFromFile,
                        color = -1,
                        tags = emptySet()
                    )

                    dao.upsertPlaylist(playlist)
                }
            }

            is PlaylistActions.ExportM3uPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    application.contentResolver.openOutputStream(action.uri)?.bufferedWriter()
                        ?.use { writer ->
                            action.tracks.forEach { track ->
                                writer.write("${getFilePathFromMediaId(track)}\n")
                            }
                        }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            application,
                            application.getString(R.string.m3u_saved_good),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getMediaIdFromFilePath(filePath: String): String {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(filePath)

        application.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val id = cursor.getLong(idColumn)
                return id.toString()
            }
        }

        return ""
    }

    private fun getFilePathFromMediaId(mediaId: String): String {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(mediaId)

        application.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val path = cursor.getString(pathColumn)
                return path
            }
        }

        return ""
    }
}

@Stable
data class PlaylistsState(
    val isLoading: Boolean = false,
    val playlists: List<PlaylistUi> = emptyList(),
    val favoritePlaylists: List<PlaylistUi> = emptyList(),
    val nonFavoritePlaylists: List<PlaylistUi> = emptyList()
)
