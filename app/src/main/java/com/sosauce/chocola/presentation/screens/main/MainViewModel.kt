@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.presentation.screens.main

import android.app.Application
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.utils.combine
import com.sosauce.chocola.utils.ordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainViewModel(
    private val application: Application,
    private val abstractTracksScanner: AbstractTracksScanner,
    private val userPreferences: UserPreferences
) : AndroidViewModel(application) {

    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)

    val state = combine(
        abstractTracksScanner.latestTracks,
        userPreferences.tracksSettings(),
        searchQuery,
    ) { tracks, settings, query ->
        val ordered = tracks.ordered(settings, query.toString())
        MainState(
            isLoading = false,
            tracks = ordered
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainState()
    )
}


data class MainState(
    val isLoading: Boolean = true,
    val tracks: List<CuteTrack> = emptyList()
)