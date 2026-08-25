@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.presentation.screens.album

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.repositories.IDRepositories
import com.sosauce.chocola.utils.combine
import com.sosauce.chocola.utils.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class AlbumsViewModel(
    private val abstractTracksScanner: AbstractTracksScanner,
    private val userPreferences: UserPreferences,
    private val idRepository: IDRepositories
) : ViewModel() {


    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)


    private val ids = idRepository.getAllAlbumIds()

    val state = combine(
        abstractTracksScanner.latestTracks,
        userPreferences.getAlbumsSort,
        userPreferences.getRegexFilter,
        userPreferences.getMatchCaseFilter,
        userPreferences.sortAlbumsAscending,
        searchQuery
    ) { tracks, sort, regex, matchCase, ascending, query ->
        val albums = tracks
            .groupBy { it.album }
            .map { (album, tracks) ->

                val lastTrack = tracks.lastOrNull()

                Album(
                    id = ids.getOrElse(album) { Random.nextLong() },
                    name = album,
                    artist = lastTrack?.artist ?: "",
                    tracks = tracks
                )
            }
            .ordered(sort, regex, matchCase, ascending, query.toString())

        AlbumsState(
            isLoading = false,
            albums = albums
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AlbumsState(isLoading = true)
    )


//    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            val albums = albumsRepository.fetchAlbums()
//
//            combine(
//                userPreferences.getAlbumsSort,
//                userPreferences.getRegexFilter,
//                userPreferences.getMatchCaseFilter,
//                userPreferences.sortAlbumsAscending,
//                searchQuery
//            ) { sort, regex, matchCase, ascending, query ->
//                val sortedAlbums = albums.ordered(sort, regex, matchCase, ascending, query.toString())
//
//                AlbumsState(
//                    isLoading = false,
//                    albums = sortedAlbums,
//                    isSearching = query.isNotEmpty()
//                )
//            }.collectLatest { newState -> _state.update { newState } }
//        }
//
//    }


}


data class AlbumsState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList()
)