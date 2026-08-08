@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.presentation.screens.album

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.domain.repository.AlbumsRepository
import com.sosauce.chocola.utils.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AlbumsViewModel(
    private val albumsRepository: AlbumsRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {


    val textFieldState = TextFieldState()
    private val userQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)
    private val _state = MutableStateFlow(AlbumsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {

            val albums = albumsRepository.fetchAlbums()

            combine(
                userPreferences.getAlbumsSort,
                userPreferences.getRegexFilter,
                userPreferences.getMatchCaseFilter,
                userPreferences.sortAlbumsAscending,
                userQuery
            ) { sort, regex, matchCase, ascending, query ->
                val sortedAlbums = albums.ordered(sort, regex, matchCase, ascending, query.toString())

                AlbumsState(
                    isLoading = false,
                    albums = sortedAlbums,
                    textFieldState = textFieldState,
                    isSearching = query.isNotEmpty()
                )
            }.collectLatest { newState -> _state.update { newState } }
        }

    }


}


data class AlbumsState(
    val isLoading: Boolean = false,
    val albums: List<Album> = emptyList(),
    val textFieldState: TextFieldState = TextFieldState(),
    val isSearching: Boolean = false
)