@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.sosauce.chocola.presentation.screens.album

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.repositories.IDRepositories
import com.sosauce.chocola.utils.orderAlbumTrackNumber
import com.sosauce.chocola.utils.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class AlbumDetailsViewModel(
    private val albumName: String,
    private val userPreferences: UserPreferences,
    private val abstractTracksScanner: AbstractTracksScanner,
    private val idRepositories: IDRepositories
) : ViewModel() {


    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        abstractTracksScanner.latestTracks,
        userPreferences.searchSettings(),
        searchQuery
    ) { tracks, settings, query ->
        val searched = tracks
            .fastFilter { it.album == albumName }
            .search(query.toString(), settings)
            .orderAlbumTrackNumber()


        val lastTrack = tracks.lastOrNull()
        val artist = lastTrack?.artist ?: ""

        val album = Album(
            id = idRepositories.getAlbumId(albumName),
            name = albumName,
            artist = artist,
            tracks = searched
        )

        AlbumDetailsState(
            isLoading = false,
            album = album
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AlbumDetailsState()
    )

}

data class AlbumDetailsState(
    val isLoading: Boolean = true,
    val album: Album = Album()
)
