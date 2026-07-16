@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.presentation.screens.playlists

import android.app.Application
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Playlist
import com.sosauce.chocola.data.playlist.PlaylistDao
import com.sosauce.chocola.domain.actions.PlaylistActions
import com.sosauce.chocola.utils.combine
import com.sosauce.chocola.utils.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class PlaylistViewModel(
    private val application: Application,
    private val dao: PlaylistDao,
    private val userPreferences: UserPreferences
) : AndroidViewModel(application) {

    val textFieldState = TextFieldState()
    private val userQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)

    private val _state = MutableStateFlow(PlaylistsState(isLoading = true))
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dao.getPlaylists(),
                userPreferences.getPlaylistsSort,
                userPreferences.getRegexFilter,
                userPreferences.getMatchCaseFilter,
                userPreferences.sortPlaylistsAscending,
                userQuery
            ) { playlists, sort, regex, matchCase, ascending, query ->

                val sortedPlaylists = playlists.ordered(sort, regex, matchCase, ascending, query.toString())

                PlaylistsState(
                    playlists = sortedPlaylists,
                    isLoading = false,
                    textFieldState = textFieldState,
                    isSearching = query.isNotEmpty()
                )

            }.collectLatest { newState -> _state.update { newState } }
        }
    }


    fun handlePlaylistActions(action: PlaylistActions) {
        when (action) {
            is PlaylistActions.DeletePlaylists -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.deletePlaylist(action.playlists)
                }
            }

            is PlaylistActions.CreatePlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist)
                }
            }

            is PlaylistActions.UpsertPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist)
                }
            }

            is PlaylistActions.ImportM3uPlaylist -> {

                val tracksFromFile = mutableListOf<String>()

                viewModelScope.launch(Dispatchers.IO) {
                    ensureActive()
                    application.contentResolver.openInputStream(action.uri)?.bufferedReader()
                        ?.useLines { lines ->
                            for (line in lines) {
                                if (!line.startsWith('/')) continue
                                tracksFromFile.add(getMediaIdFromFilePath(line))
                            }

                        }

                    val playlist = Playlist(
                        emoji = "",
                        name = action.uri.path?.substringAfterLast('/')?.substringBeforeLast('.')
                            ?: "Imported playlist",
                        musics = tracksFromFile,
                        color = -1,
                        tags = emptyList()
                    )

                    dao.upsertPlaylist(playlist)
                }
            }

            is PlaylistActions.ExportM3uPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    ensureActive()
                    application.contentResolver.openOutputStream(action.uri)?.bufferedWriter()
                        ?.use { writer ->
                            action.tracks.fastForEach { track ->
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

data class PlaylistsState(
    val isLoading: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val textFieldState: TextFieldState = TextFieldState(),
    val isSearching: Boolean = false
)