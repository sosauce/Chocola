package com.sosauce.chocola.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.repositories.FoldersRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class FoldersViewModel(
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    val folders = foldersRepository.fetchLatestMusicFolders().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

}