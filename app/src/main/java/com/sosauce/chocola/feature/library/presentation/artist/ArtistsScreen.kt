@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.artist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.CuteListItem
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.designsystem.components.SelectOverlayItem
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CollectionMoreButton
import com.sosauce.chocola.core.presentation.components.CollectionSelectedBar
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.core.presentation.components.collectionSelectActions
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.util.barsContentTransform
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.feature.library.presentation.artist.components.FavoriteArtistItem
import com.sosauce.nekobites.components.AnimatedSelectedIcon
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.components.NoXFound
import com.sosauce.sweetselect.rememberSweetSelectState
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun SharedTransitionScope.ArtistsScreen(
    state: ArtistsState,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {

    val lazyState = rememberLazyListState()
    val multiSelectState = rememberSweetSelectState<Artist>()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            AnimatedContent(
                targetState = multiSelectState.isInSelectionMode,
                transitionSpec = { barsContentTransform }
            ) { isInSelectionMode ->
                if (isInSelectionMode) {
                    CollectionSelectedBar(
                        modifier = Modifier.selfAlignHorizontally(),
                        items = state.artists,
                        multiSelectState = multiSelectState,
                        tracksOf = { it.tracks },
                        isFavoriteOf = { it.isFavorite },
                        onToggleFavorite = {
                            onHandlePlayerActions(
                                PlayerActions.ToggleArtistFavorite(
                                    it.id
                                )
                            )
                        },
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
                            CuteSearchbarDefaults.ArtistSortPopupContent()
                        },
                    )
                }
            }
        }
    ) { paddingValues ->

        LoadingBox(
            isLoading = state.isLoading
        ) {
            LazyColumn(
                contentPadding = paddingValues,
                state = lazyState
            ) {
                if (state.favoriteArtists.isNotEmpty()) {
                    item(key = "favorites") {
                        LazyRow(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.favoriteArtists,
                                key = { it.id }
                            ) { artist ->
                                val isSelected by multiSelectState.isSelectedAsState(artist)

                                SelectOverlayItem(
                                    modifier = Modifier.animateItem(),
                                    enabled = !multiSelectState.isInSelectionMode,
                                    actions = collectionSelectActions(
                                        tracks = artist.tracks,
                                        source = PlaySource.Artist(artist.name),
                                        isFavorite = artist.isFavorite,
                                        onToggleFavorite = {
                                            onHandlePlayerActions(
                                                PlayerActions.ToggleArtistFavorite(
                                                    artist.id
                                                )
                                            )
                                        },
                                        onHandlePlayerActions = onHandlePlayerActions
                                    ),
                                    onSelect = { multiSelectState.addAll(listOf(artist)) }
                                ) { showOverlay ->
                                    FavoriteArtistItem(
                                        artist = artist,
                                        isSelected = isSelected,
                                        isInSelectionMode = multiSelectState.isInSelectionMode,
                                        onClick = {
                                            if (multiSelectState.isInSelectionMode) {
                                                multiSelectState.toggle(artist)
                                            } else {
                                                onNavigate(Screen.ArtistsDetails(artist.name))
                                            }
                                        },
                                        onLongClick = {
                                            if (multiSelectState.isInSelectionMode) {
                                                multiSelectState.toggle(artist)
                                            } else {
                                                showOverlay()
                                            }
                                        },
                                        onUnfavorite = {
                                            onHandlePlayerActions(
                                                PlayerActions.ToggleArtistFavorite(
                                                    artist.id
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (state.artists.isEmpty()) {
                    item(
                        key = "empty"
                    ) {
                        if (textFieldState.text.isEmpty()) {
                            NoXFound(
                                modifier = Modifier.animateItem(),
                                headlineText = R.string.no_artists_found,
                                bodyText = R.string.no_artist_desc,
                                icon = R.drawable.artist_rounded
                            )
                        } else {
                            NoResult(modifier = Modifier.animateItem())
                        }

                    }
                }
                items(
                    items = state.nonFavoriteArtists,
                    key = { it.id }
                ) { artist ->
                    val isSelected by multiSelectState.isSelectedAsState(artist)

                    SelectOverlayItem(
                        modifier = Modifier.animateItem(),
                        enabled = !multiSelectState.isInSelectionMode,
                        actions = collectionSelectActions(
                            tracks = artist.tracks,
                            source = PlaySource.Artist(artist.name),
                            isFavorite = artist.isFavorite,
                            onToggleFavorite = {
                                onHandlePlayerActions(
                                    PlayerActions.ToggleArtistFavorite(
                                        artist.id
                                    )
                                )
                            },
                            onHandlePlayerActions = onHandlePlayerActions
                        ),
                        onSelect = { multiSelectState.addAll(listOf(artist)) }
                    ) { showOverlay ->
                        ArtistItem(
                            artist = artist,
                            isSelected = isSelected,
                            onClick = {
                                if (multiSelectState.isInSelectionMode) {
                                    multiSelectState.toggle(artist)
                                } else {
                                    onNavigate(Screen.ArtistsDetails(artist.name))
                                }
                            },
                            onLongClick = {
                                if (multiSelectState.isInSelectionMode) {
                                    multiSelectState.toggle(artist)
                                } else {
                                    showOverlay()
                                }
                            },
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    }
                }
            }
        }

    }

}


@Composable
fun SharedTransitionScope.ArtistItem(
    modifier: Modifier = Modifier,
    artist: Artist,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.95f else 1f
    )

    CuteListItem(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        onClick = onClick,
        onLongClick = onLongClick,
        backgroundColor = MaterialTheme.colorScheme.background,
        leadingContent = {
            AnimatedSelectedIcon(
                isSelected = isSelected
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(SquircleShape(smoothing = CornerSmoothing.Full))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.artists_filled),
                        contentDescription = null
                    )
                }
            }
        },
        trailingContent = {
            CollectionMoreButton(
                tracks = artist.tracks,
                source = PlaySource.Artist(artist.name),
                isFavorite = artist.isFavorite,
                onToggleFavorite = { onHandlePlayerActions(PlayerActions.ToggleArtistFavorite(artist.id)) },
                onHandlePlayerActions = onHandlePlayerActions
            )
        }
    ) {
        Text(
            text = artist.name,
            maxLines = 1,
            style = MaterialTheme.typography.titleMediumEmphasized,
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(artist.name + artist.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
                .basicMarquee()
        )
        Text(
            text = buildString {
                append(
                    pluralStringResource(
                        R.plurals.tracks_plural,
                        artist.tracks.size,
                        artist.tracks.size
                    )
                )
                if (artist.numberAlbums > 0) {
                    append(" & ")
                    append(
                        pluralStringResource(
                            R.plurals.albums_plural,
                            artist.numberAlbums,
                            artist.numberAlbums
                        )
                    )
                }
            },
            maxLines = 1,
            style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier.basicMarquee()
        )
    }
}
