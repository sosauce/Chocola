@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAlbumGrids
import com.sosauce.chocola.data.datastore.rememberAlbumSort
import com.sosauce.chocola.data.datastore.rememberMatchCaseFilter
import com.sosauce.chocola.data.datastore.rememberRegexFilter
import com.sosauce.chocola.data.datastore.rememberSortAlbumsAscending
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.CuteSearchbar
import com.sosauce.chocola.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.presentation.components.LoadingBox
import com.sosauce.chocola.presentation.components.NoResult
import com.sosauce.chocola.presentation.components.NoXFound
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.album.components.AlbumCard
import com.sosauce.chocola.utils.selfAlignHorizontally

@Composable
fun SharedTransitionScope.AlbumsScreen(
    state: AlbumsState,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val lazyState = rememberLazyGridState()
    var numberOfAlbumGrids by rememberAlbumGrids()


    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            CuteSearchbar(
                modifier = Modifier.selfAlignHorizontally(),
                musicState = musicState,
                textFieldState = textFieldState,
                onHandlePlayerActions = onHandlePlayerActions,
                onNavigate = onNavigate,
                sortMenu = {
                    CuteSearchbarDefaults.AlbumSortPopupContent()
                }
            )
        }
    ) { paddingValues ->
        LoadingBox(
            isLoading = state.isLoading
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(numberOfAlbumGrids),
                contentPadding = paddingValues + PaddingValues(horizontal = 5.dp),
                state = lazyState,
            ) {

                if (state.albums.isEmpty()) {
                    item(
                        key = "empty",
                        span = { GridItemSpan(maxLineSpan) }
                    ) {

                        if (textFieldState.text.isEmpty()) {
                            NoXFound(
                                modifier = Modifier.animateItem(),
                                headlineText = R.string.no_albums_found,
                                bodyText = R.string.no_album_desc,
                                icon = androidx.media3.session.R.drawable.media3_icon_album
                            )
                        } else { NoResult(modifier = Modifier.animateItem()) }

                    }
                }

                items(
                    items = state.albums,
                    key = { it.id }
                ) { album ->
                    AlbumCard(
                        modifier = Modifier.animateItem(),
                        album = album,
                        onClick = { onNavigate(Screen.AlbumsDetails(album.name)) }
                    )
                }
            }
        }
    }
}

