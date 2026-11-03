@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.feature.library.presentation.album

import android.content.ContentUris
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.local.FavoritesDao
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.util.combine
import com.sosauce.chocola.core.domain.library.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds
import androidx.core.net.toUri
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils

class AlbumsViewModel(
    private val abstractTracksScanner: AbstractTracksScanner,
    private val userPreferences: UserPreferences,
    private val favoritesDao: FavoritesDao
) : ViewModel() {


    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        combine(abstractTracksScanner.latestTracks(), favoritesDao.observeAlbumIds()) { tracks, favAlbumIds -> tracks to favAlbumIds },
        userPreferences.getAlbumsSort,
        userPreferences.getRegexFilter,
        userPreferences.getMatchCaseFilter,
        userPreferences.sortAlbumsAscending,
        searchQuery
    ) { (tracks, favAlbumIds), sort, regex, matchCase, ascending, query ->
        val albums = tracks
            .groupBy { it.album }
            .map { (album, albumTracks) ->

                val lastTrack = albumTracks.last()
                val albumId = lastTrack.albumId

                Album(
                    id = albumId,
                    name = album,
                    artist = lastTrack.artist,
                    tracks = albumTracks,
                    art = ImageUtils.getAlbumArt(albumId),
                    isFavorite = albumId in favAlbumIds
                )
            }
            .ordered(sort, regex, matchCase, ascending, query.toString())
            .sortedByDescending { it.isFavorite }

        AlbumsState(
            isLoading = false,
            albums = albums
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AlbumsState(isLoading = true)
    )



}


@Stable
data class AlbumsState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList()
)
