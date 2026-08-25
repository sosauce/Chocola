@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.presentation.screens.artist

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.utils.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class ArtistDetailsViewModel(
    private val artistName: String,
    private val userPreferences: UserPreferences,
    private val abstractTracksScanner: AbstractTracksScanner
) : ViewModel() {

    val textFieldState = TextFieldState()

    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        abstractTracksScanner.latestTracks,
        userPreferences.searchSettings(),
        searchQuery
    ) { tracks, settings, query ->
        val searched = tracks
            .fastFilter { it.artist == artistName }
            .search(query.toString(), settings)

        val albums = searched.groupBy { it.album }
            .map { (albumName, tracks) ->
                Album(
                    name = albumName,
                    artist = artistName,
                    tracks = tracks
                )
            }



        ArtistDetailsState(
            isLoading = false,
            artist = Artist(
                name = artistName
            ),
            tracks = searched,
            albums = albums
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ArtistDetailsState()
    )


}

data class ArtistDetailsState(
    val isLoading: Boolean = true,
    val artist: Artist = Artist(),
    val tracks: List<CuteTrack> = emptyList(),
    val albums: List<Album> = emptyList()
)