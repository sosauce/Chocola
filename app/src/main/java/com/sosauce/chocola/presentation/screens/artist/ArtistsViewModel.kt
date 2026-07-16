@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.presentation.screens.artist

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.domain.repository.ArtistsRepository
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
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds

class ArtistsViewModel(
    private val artistsRepository: ArtistsRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val textFieldState = TextFieldState()
    private val userQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)
    private val _state = MutableStateFlow(ArtistsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {

            measureTimeMillis {
                artistsRepository.fetchArtists()
            }.also {
                println("Time for artists: ${it}ms")
            }
            val artists = artistsRepository.fetchArtists()

            combine(
                userPreferences.getArtistsSort,
                userPreferences.getRegexFilter,
                userPreferences.getMatchCaseFilter,
                userPreferences.sortArtistsAscending,
                userQuery
            ) { sort, regex, matchCase, ascending, query ->

                val sortedArtists = artists.ordered(sort, regex, matchCase, ascending, query.toString())

                ArtistsState(
                    isLoading = false,
                    artists = sortedArtists,
                    isSearching = query.isNotEmpty(),
                    textFieldState = textFieldState
                )

            }.collectLatest { newState -> _state.update { newState } }
        }

    }

}


data class ArtistsState(
    val isLoading: Boolean = false,
    val artists: List<Artist> = emptyList(),
    val textFieldState: TextFieldState = TextFieldState(),
    val isSearching: Boolean = false
)