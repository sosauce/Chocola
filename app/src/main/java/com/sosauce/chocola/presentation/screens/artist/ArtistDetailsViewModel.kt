package com.sosauce.chocola.presentation.screens.artist

import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.domain.repository.AlbumsRepository
import com.sosauce.chocola.domain.repository.ArtistsRepository
import com.sosauce.chocola.utils.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistDetailsViewModel(
    private val artistName: String,
    private val artistsRepository: ArtistsRepository,
    private val albumsRepository: AlbumsRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(ArtistDetailsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {

            val artist = artistsRepository.fetchArtistDetails(artistName)
            val albums = albumsRepository.fetchAlbums(
                selection = "${MediaStore.Audio.Albums.ARTIST} = ?",
                selectionArgs = arrayOf(artistName)
            )
            _state.update {
                it.copy(
                    artist = artist,
                    albums = albums
                )
            }
        }

//        viewModelScope.launch(Dispatchers.IO) {
//            combine(
//                artistsRepository.fetchLatestArtistTracks(artistName),
//                userPreferences.getTrackSort,
//                userPreferences.sortTracksAscending
//            ) { tracks, sort, ascending ->
//                tracks.ordered(sort, ascending)
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

data class ArtistDetailsState(
    val isLoading: Boolean = false,
    val artist: Artist = Artist(),
    val tracks: List<CuteTrack> = emptyList(),
    val albums: List<Album> = emptyList()
)