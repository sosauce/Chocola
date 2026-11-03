@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.feature.library.presentation.artist

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.local.FavoritesDao
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.util.combine
import com.sosauce.chocola.core.domain.library.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class ArtistsViewModel(
    private val userPreferences: UserPreferences,
    private val abstractTracksScanner: AbstractTracksScanner,
    private val favoritesDao: FavoritesDao
) : ViewModel() {

    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        kotlinx.coroutines.flow.combine(abstractTracksScanner.latestTracks(), favoritesDao.observeArtistIds()) { tracks, ids -> tracks to ids },
        userPreferences.getArtistsSort,
        userPreferences.getRegexFilter,
        userPreferences.getMatchCaseFilter,
        userPreferences.sortArtistsAscending,
        searchQuery
    ) { (tracks, favoriteIds), sort, regex, matchCase, ascending, query ->
        val artists = tracks.groupBy { it.artist }
            .map { (artist, tracks) ->
                Artist(
                    id = tracks.firstOrNull()?.artistId ?: -1L,
                    tracks = tracks,
                    name = artist,
                    isFavorite = tracks.firstOrNull()?.artistId in favoriteIds
                )
            }
            .ordered(sort, regex, matchCase, ascending, query.toString())
            .sortedByDescending { it.isFavorite }

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


@Stable
data class ArtistsState(
    val isLoading: Boolean = true,
    val artists: List<Artist> = emptyList()
)
