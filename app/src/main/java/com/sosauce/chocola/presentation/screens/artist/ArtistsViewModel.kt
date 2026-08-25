@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.presentation.screens.artist

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.utils.combine
import com.sosauce.chocola.utils.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class ArtistsViewModel(
    private val userPreferences: UserPreferences,
    private val abstractTracksScanner: AbstractTracksScanner
) : ViewModel() {

    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        abstractTracksScanner.latestTracks,
        userPreferences.getArtistsSort,
        userPreferences.getRegexFilter,
        userPreferences.getMatchCaseFilter,
        userPreferences.sortArtistsAscending,
        searchQuery
    ) { tracks, sort, regex, matchCase, ascending, query ->
        val artists = tracks.groupBy { it.artist }
            .map { (artist, tracks) ->
                Artist(
                    id = Random.nextLong(),
                    tracks = tracks,
                    name = artist
                )
            }
            .ordered(sort, regex, matchCase, ascending, query.toString())

        ArtistsState(
            isLoading = false,
            artists = artists
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ArtistsState()
    )

}


data class ArtistsState(
    val isLoading: Boolean = true,
    val artists: List<Artist> = emptyList()
)