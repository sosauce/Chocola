package com.sosauce.chocola.presentation.screens.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.domain.repository.AlbumsRepository
import com.sosauce.chocola.utils.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumDetailsViewModel(
    private val albumName: String,
    private val albumsRepository: AlbumsRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AlbumDetailsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val album = albumsRepository.fetchAlbumDetails(albumName)
            _state.update { it.copy(album = album) }
        }

//        viewModelScope.launch(Dispatchers.IO) {
//            combine(
//                albumsRepository.fetchLatestAlbumTracks(albumName),
//                userPreferences.getTrackSort,
//                userPreferences.sortTracksAscending
//            ) { tracks, sort, ascending ->
//                tracks.ordered(sort, ascending).sortedWith(
//                    compareBy(
//                        { it.trackNumber == 0 },
//                        { it.trackNumber }
//                    )
//                )
//            }.flowOn(Dispatchers.Default).collectLatest { sortedTracks ->
//                _state.update {
//                    it.copy(
//                        tracks = sortedTracks,
//                        isLoading = false
//                    )
//                }
//            }
//        }
    }
}

data class AlbumDetailsState(
    val isLoading: Boolean = false,
    val album: Album = Album(),
    val tracks: List<CuteTrack> = emptyList()
)