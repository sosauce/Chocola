@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.sosauce.chocola.feature.library.presentation.album

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.local.FavoritesDao
import com.sosauce.chocola.core.domain.library.orderAlbumTrackNumber
import com.sosauce.chocola.core.domain.library.search
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils
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
    private val favoritesDao: FavoritesDao
) : ViewModel() {


    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    val state = combine(
        combine(
            abstractTracksScanner.latestTracks(),
            favoritesDao.observeAlbumIds()
        ) { tracks, ids -> tracks to ids },
        userPreferences.searchSettings(),
        searchQuery
    ) { (tracks, favoriteIds), settings, query ->
        val filtered = tracks
            .fastFilter { it.album == albumName }
            .orderAlbumTrackNumber()


        val lastTrack = filtered.lastOrNull() ?: return@combine AlbumDetailsState(isLoading = false)
        val albumId = lastTrack.albumId

        val album = Album(
            id = albumId,
            name = albumName,
            artist = lastTrack.artist,
            tracks = filtered.search(query.toString(), settings),
            art = ImageUtils.getAlbumArt(albumId),
            isFavorite = albumId in favoriteIds
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

@Stable
data class AlbumDetailsState(
    val isLoading: Boolean = true,
    val album: Album = Album()
)
