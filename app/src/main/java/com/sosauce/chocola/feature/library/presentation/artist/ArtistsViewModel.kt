@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.feature.library.presentation.artist

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.local.FavoritesDao
import com.sosauce.chocola.core.domain.library.ordered
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.util.combine
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
        kotlinx.coroutines.flow.combine(
            abstractTracksScanner.latestTracks(),
            favoritesDao.observeArtistIds()
        ) { tracks, ids -> tracks to ids },
        userPreferences.getArtistsSort,
        userPreferences.getRegexFilter,
        userPreferences.getMatchCaseFilter,
        userPreferences.sortArtistsAscending,
        userPreferences.getHideSingleAlbums(),
        searchQuery
    ) { (tracks, favoriteIds), sort, regex, matchCase, ascending, hideSingleAlbums, query ->


        val artists = tracks.groupBy { it.artist }
            .map { (artist, artistTracks) ->
                val artistId = artistTracks.last().artistId
                val albumCounts = artistTracks.groupingBy { it.album }.eachCount()
                val numberAlbums =
                    if (hideSingleAlbums) albumCounts.count { it.value > 1 } else albumCounts.size

                Artist(
                    id = artistId,
                    tracks = artistTracks,
                    numberAlbums = numberAlbums,
                    name = artist,
                    isFavorite = artistId in favoriteIds
                )
            }
            .ordered(sort, regex, matchCase, ascending, query.toString())
            .sortedByDescending { it.isFavorite }
        val (favoriteArtists, nonFavoriteArtists) = artists.partition { it.isFavorite }

        ArtistsState(
            isLoading = false,
            artists = artists,
            favoriteArtists = favoriteArtists,
            nonFavoriteArtists = nonFavoriteArtists
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
    val artists: List<Artist> = emptyList(),
    val favoriteArtists: List<Artist> = emptyList(),
    val nonFavoriteArtists: List<Artist> = emptyList()
)