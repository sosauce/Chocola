@file:OptIn(FlowPreview::class)

package com.sosauce.chocola.feature.library.presentation.main

import android.app.Application
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.data.library.AbstractTracksScanner
import com.sosauce.chocola.core.data.local.FavoritesDao
import com.sosauce.chocola.core.domain.library.search
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.model.SearchSettings
import com.sosauce.chocola.core.domain.util.combine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainViewModel(
    private val application: Application,
    private val abstractTracksScanner: AbstractTracksScanner,
    private val userPreferences: UserPreferences,
    private val favoritesDao: FavoritesDao
) : AndroidViewModel(application) {

    val textFieldState = TextFieldState()
    private val searchQuery = snapshotFlow { textFieldState.text }.debounce(250.milliseconds)

    val state: StateFlow<MainState> = combine(
        abstractTracksScanner.latestTracks(),
        userPreferences.searchSettings(),
        searchQuery,
        userPreferences.getHiddenFolders,
        userPreferences.getGroupByFolders,
        favoritesDao.observeTrackIds()
    ) { tracks: List<CuteTrack>, searchSettings: SearchSettings, query: CharSequence, hiddenFolders: Set<String>, groupByFolders: Boolean, favoriteIds: List<Long> ->
        val prioritizedTracks =
            tracks.map { it.copy(isFavorite = it.mediaId.toLongOrNull() in favoriteIds) }
                .sortedByDescending { it.isFavorite }
        val filteredTracks = prioritizedTracks.search(query.toString(), searchSettings)
        val (favoriteTracks, nonFavoriteTracks) = filteredTracks.partition { it.isFavorite }
        MainState(
            isLoading = false,
            tracks = filteredTracks,
            favoriteTracks = favoriteTracks,
            nonFavoriteTracks = nonFavoriteTracks,
            categories = if (groupByFolders) {
                nonFavoriteTracks
                    .groupBy { it.folder }
                    .toSortedMap()
                    .map { (folder, folderTracks) ->
                        Category(
                            path = folder,
                            name = folder.substringAfterLast('/'),
                            tracks = folderTracks,
                            isHidden = folder in hiddenFolders
                        )
                    }
            } else {
                emptyList()
            },
            groupByFolders = groupByFolders
        )
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainState()
    )

    fun toggleHiddenFolder(folder: String) {
        viewModelScope.launch {
            userPreferences.toggleHiddenFolder(folder)
        }
    }
}

@Stable
data class MainState(
    val isLoading: Boolean = true,
    val tracks: List<CuteTrack> = emptyList(),
    val favoriteTracks: List<CuteTrack> = emptyList(),
    val nonFavoriteTracks: List<CuteTrack> = emptyList(),
    val categories: List<Category> = emptyList(),
    val groupByFolders: Boolean = false
)

data class Category(
    val path: String,
    val name: String,
    val tracks: List<CuteTrack>,
    val isHidden: Boolean
)
