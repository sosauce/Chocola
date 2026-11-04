@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.sosauce.chocola.feature.playlists.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.data.local.Playlist
import com.sosauce.chocola.core.data.local.PlaylistDao
import com.sosauce.chocola.core.domain.library.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PlaylistDetailsViewModel(
    private val id: Int,
    private val userPreferences: UserPreferences,
    private val dao: PlaylistDao,
    private val abstractTracksScanner: AbstractTracksScanner
) : ViewModel() {


    val textFieldState = TextFieldState()
    val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        dao.getPlaylistDetails(id),
        abstractTracksScanner.latestTracks(),
        userPreferences.searchSettings(),
        searchQuery
    ) { playlist, tracks, settings, query ->
        val filtered = tracks
            .fastFilter { playlist.musics.contains(it.mediaId) }


        PlaylistDetailsState(
            isLoading = false,
            tracks = filtered.search(query.toString(), settings),
            playlistTracks = filtered,
            playlist = playlist
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistDetailsState()
    )

    fun handlePlaylistActions(action: PlaylistActions) {
        when (action) {
            is PlaylistActions.UpsertPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist)
                }
            }

            else -> Unit
        }
    }

}

@Stable
data class PlaylistDetailsState(
    val isLoading: Boolean = true,
    val playlist: Playlist = Playlist(),
    val tracks: List<CuteTrack> = emptyList(),
    val playlistTracks: List<CuteTrack> = emptyList()
)