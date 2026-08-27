package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.repositories.FoldersRepository
import com.sosauce.chocola.data.repositories.SafManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsLibraryViewModel(
    private val safManager: SafManager,
    private val abstractTracksScanner: AbstractTracksScanner,
    private val userPreferences: UserPreferences,
    private val foldersRepository: FoldersRepository
) : ViewModel() {


    private val _events = Channel<LibraryEvents>()
    val events = _events.receiveAsFlow()

    val safTracks = safManager.fetchLatestSafTracks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

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

    val folders = foldersRepository.fetchLatestMusicFolders().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun handleLibraryAction(action: LibraryActions) {
        when (action) {
            is LibraryActions.UnhideTrack -> {
                viewModelScope.launch {
                    userPreferences.unhideTrack(action.mediaId)
                }
            }

            is LibraryActions.ForceRescan -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        abstractTracksScanner.forceScanDevice()
                        _events.trySend(LibraryEvents.RescanSuccessful)
                    } catch (e: Exception) {
                        ensureActive()
                        _events.trySend(
                            LibraryEvents.RescanError(e.message ?: "Unknown error")
                        )
                    }
                }
            }
        }
    }

}


sealed interface LibraryActions {

    data class UnhideTrack(
        val mediaId: String
    ) : LibraryActions

    data object ForceRescan : LibraryActions
}

sealed interface LibraryEvents {


    data object RescanSuccessful : LibraryEvents
    data class RescanError(
        val errorMessage: String
    ) : LibraryEvents
}


