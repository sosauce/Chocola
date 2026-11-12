package com.sosauce.chocola.feature.setup.presentation

import androidx.compose.runtime.Stable
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.data.library.FoldersRepository
import com.sosauce.chocola.core.data.mapper.toFolders
import com.sosauce.chocola.core.domain.model.Folder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupViewModel(
    private val userPreferences: UserPreferences,
    private val foldersRepository: FoldersRepository
) : ViewModel() {


    private val _state = MutableStateFlow(SetupState())
    val state = combine(
        _state,
        userPreferences.getWhitelistedFolders(),
        userPreferences.getBlacklistedFolders()
    ) { state, white, black ->
        state.copy(
            whitelistedFolders = white.toFolders(),
            blacklistedFolders = black.toFolders()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SetupState()
    )


    fun handleSetupAction(action: SetupActions) {
        when (action) {
            is SetupActions.BlacklistFolders -> {
                viewModelScope.launch {
                    userPreferences.addBlacklistedFolders(action.paths.toSet())
                }
            }

            is SetupActions.WhitelistFolders -> {
                viewModelScope.launch {
                    userPreferences.addWhitelistedFolders(action.paths.toSet())
                }
            }
        }
    }

    init {
        initFolders()
    }

    private fun initFolders() {
        viewModelScope.launch {
            if (userPreferences.hasFolderLists()) return@launch

            _state.update { it.copy(isLoadingFolders = true) }
            val folders = foldersRepository.fetchLatestMusicFolders().first()
            userPreferences.seedInitialFoldersIfAbsent(folders.fastMap { it.path }.toSet())
            _state.update { it.copy(isLoadingFolders = false) }

        }
    }

}

@Stable
data class SetupState(
    val isLoadingFolders: Boolean = false,
    val whitelistedFolders: List<Folder> = emptyList(),
    val blacklistedFolders: List<Folder> = emptyList()
)


sealed interface SetupActions {

    data class BlacklistFolders(
        val paths: List<String>
    ) : SetupActions

    data class WhitelistFolders(
        val paths: List<String>
    ) : SetupActions
}
