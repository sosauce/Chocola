package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.library.SafManager
import com.sosauce.chocola.core.data.mapper.toFolders
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.model.Folder
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
    private val userPreferences: UserPreferences
) : ViewModel() {


    private val _events = Channel<LibraryEvents>()
    val events = _events.receiveAsFlow()

    val state = combine(
        safManager.fetchLatestSafTracks(),
        userPreferences.getWhitelistedFolders(),
        userPreferences.getBlacklistedFolders(),
        abstractTracksScanner.fetchHiddenTracks()
    ) { saf, white, black, hidden ->
        LibraryState(
            safTracks = saf,
            whitelistedFolders = white.toFolders(),
            blacklistedFolders = black.toFolders(),
            hiddenTracks = hidden
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LibraryState()
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

            is LibraryActions.BlacklistFolders -> {
                viewModelScope.launch {
                    userPreferences.addBlacklistedFolders(action.paths.toSet())
                }
            }

            is LibraryActions.WhitelistFolders -> {
                viewModelScope.launch {
                    userPreferences.addWhitelistedFolders(action.paths.toSet())
                }
            }

            is LibraryActions.ChangeSafTracks -> {
                viewModelScope.launch {
                    userPreferences.saveSafTracks(action.tracks.toSet(), action.add)
                }
            }
        }
    }

}

@Stable
data class LibraryState(
    val safTracks: List<CuteTrack> = emptyList(),
    val hiddenTracks: List<CuteTrack> = emptyList(),
    val whitelistedFolders: List<Folder> = emptyList(),
    val blacklistedFolders: List<Folder> = emptyList()
)


sealed interface LibraryActions {

    data class UnhideTrack(
        val mediaId: String
    ) : LibraryActions

    data class WhitelistFolders(
        val paths: List<String>
    ) : LibraryActions

    data class BlacklistFolders(
        val paths: List<String>
    ) : LibraryActions

    data class ChangeSafTracks(
        val tracks: List<String>,
        val add: Boolean
    ) : LibraryActions

    data object ForceRescan : LibraryActions
}

sealed interface LibraryEvents {


    data object RescanSuccessful : LibraryEvents
    data class RescanError(
        val errorMessage: String
    ) : LibraryEvents
}


