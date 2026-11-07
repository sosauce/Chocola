@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.presentation.preferences.rememberAlbumGrids
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.preferences.rememberHideSingleAlbums
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.chocola.feature.library.presentation.album.components.AlbumCard
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.components.NoXFound
import com.sosauce.chocola.feature.library.presentation.album.components.FavoriteAlbumItem
import com.sosauce.chocola.feature.library.presentation.album.components.SelectedAlbums
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import com.sosauce.sweetselect.rememberSweetSelectState

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
    val multiSelectState = rememberSweetSelectState<Album>()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            AnimatedContent(
                targetState = multiSelectState.isInSelectionMode,
                transitionSpec = { barsContentTransform }
            ) { isInSelectionMode ->
                if (isInSelectionMode) {
                    SelectedAlbums(
                        modifier = Modifier.selfAlignHorizontally(),
                        albums = state.albums,
                        multiSelectState = multiSelectState,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                } else {
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
            }
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
                if (state.favoriteAlbums.isNotEmpty()) {
                    item(
                        key = "favorites",
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.favoriteAlbums,
                                key = { it.id }
                            ) { album ->
                                val isSelected by multiSelectState.isSelectedAsState(album)

                                FavoriteAlbumItem(
                                    modifier = Modifier.animateItem(),
                                    album = album,
                                    isSelected = isSelected,
                                    isInSelectionMode = multiSelectState.isInSelectionMode,
                                    onClick = {
                                        if (multiSelectState.isInSelectionMode) {
                                            multiSelectState.toggle(album)
                                        } else {
                                            onNavigate(Screen.AlbumsDetails(album.name))
                                        }
                                    },
                                    onLongClick = { multiSelectState.toggle(album) }
                                )
                            }
                        }
                    }
                }

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
                        } else {
                            NoResult(modifier = Modifier.animateItem())
                        }

                    }
                }

                items(
                    items = state.nonFavoriteAlbums,
                    key = { it.id }
                ) { album ->
                    val isSelected by multiSelectState.isSelectedAsState(album)

                    AlbumCard(
                        modifier = Modifier.animateItem(),
                        album = album,
                        isSelected = isSelected,
                        isInSelectionMode = multiSelectState.isInSelectionMode,
                        onClick = {
                            if (multiSelectState.isInSelectionMode) {
                                multiSelectState.toggle(album)
                            } else {
                                onNavigate(Screen.AlbumsDetails(album.name))
                            }
                        },
                        onLongClick = { multiSelectState.toggle(album) },
                        onToggleFavorite = { onHandlePlayerActions(PlayerActions.ToggleAlbumFavorite(album.id)) }
                    )
                }
            }
        }
    }
}
