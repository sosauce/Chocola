@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.feature.library.presentation.artist

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.local.FavoritesDao
import com.sosauce.chocola.core.domain.library.search
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils
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
    private val abstractTracksScanner: AbstractTracksScanner,
    private val favoritesDao: FavoritesDao
) : ViewModel() {

    val textFieldState = TextFieldState()

    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        combine(
            abstractTracksScanner.latestTracks(),
            favoritesDao.observeArtistIds()
        ) { tracks, ids -> tracks to ids },
        userPreferences.searchSettings(),
        userPreferences.getHideSingleAlbums(),
        searchQuery
    ) { (tracks, favoriteIds), settings, hideSingleAlbums, query ->
        val filtered = tracks
            .fastFilter { it.artist == artistName }

        val searched = filtered
            .search(query.toString(), settings)

        var albums = searched
            .groupBy { it.album }
            .map { (albumName, tracks) ->
                val lastTrack = tracks.last()

                Album(
                    id = lastTrack.albumId,
                    name = albumName,
                    artist = artistName,
                    tracks = tracks,
                    art = ImageUtils.getAlbumArt(lastTrack.albumId)
                )
            }
        if (hideSingleAlbums) {
            albums = albums.filter { it.tracks.size > 1 }
        }
        val artistId = filtered.last().artistId



        ArtistDetailsState(
            isLoading = false,
            artist = Artist(
                name = artistName,
                id = artistId,
                isFavorite = artistId in favoriteIds
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

@Stable
data class ArtistDetailsState(
    val isLoading: Boolean = true,
    val artist: Artist = Artist(),
    val tracks: List<CuteTrack> = emptyList(),
    val albums: List<Album> = emptyList()
)