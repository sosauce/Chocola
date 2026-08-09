package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.datastore.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HiddenTracksViewModel(
    private val abstractTracksScanner: AbstractTracksScanner,
    private val userPreferences: UserPreferences
): ViewModel() {


    val hiddenTracks = combine(
        abstractTracksScanner.latestTracks,
        userPreferences.getHiddenTracks()
    ) { tracks, hidden ->
        tracks.fastFilter { track ->
            hidden.contains(track.mediaId)
        }
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    fun unhideTrack(mediaId: String) {
        viewModelScope.launch {
            userPreferences.unhideTrack(mediaId)
        }
    }

}