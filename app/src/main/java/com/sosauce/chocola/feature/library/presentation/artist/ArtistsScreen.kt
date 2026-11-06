@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.artist

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.designsystem.components.CuteListItem
import com.sosauce.chocola.core.presentation.components.CuteSearchbar
import com.sosauce.chocola.core.presentation.components.CuteSearchbarDefaults
import com.sosauce.chocola.core.designsystem.components.NoResult
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.components.NoXFound
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape
import com.sosauce.chocola.feature.library.presentation.artist.components.FavoriteArtistItem
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement

@Composable
fun SharedTransitionScope.ArtistsScreen(
    state: ArtistsState,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {

    val lazyState = rememberLazyListState()

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
                    CuteSearchbarDefaults.ArtistSortPopupContent()
                },
            )
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
                                FavoriteArtistItem(
                                    modifier = Modifier.animateItem(),
                                    artist = artist,
                                    onClick = { onNavigate(Screen.ArtistsDetails(artist.name)) }
                                )
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
                    ArtistItem(
                        modifier = Modifier.animateItem(),
                        artist = artist,
                        onClick = { onNavigate(Screen.ArtistsDetails(artist.name)) },
                        onToggleFavorite = { onHandlePlayerActions(PlayerActions.ToggleArtistFavorite(artist.id)) }
                    )
                }
            }
        }

    }

}


@Composable
fun SharedTransitionScope.ArtistItem(
    modifier: Modifier = Modifier,
    artist: Artist,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {

    CuteListItem(
        modifier = modifier,
        onClick = onClick,
        leadingContent = {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(50.dp)
                    .clip(SquircleShape(smoothing = CornerSmoothing.Full))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.artists_filled),
                    contentDescription = null
                )
                AsyncImage(
                    model = ImageUtils.getAlbumArt(artist.albumId),
                    contentDescription = stringResource(R.string.artwork),
                    contentScale = ContentScale.Crop,
                )
            }
        },
        trailingContent = {
            IconButton(
                onClick = onToggleFavorite,
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(R.drawable.favorite_filled),
                    contentDescription = stringResource(if (artist.isFavorite) R.string.remove_favorite else R.string.add_favorite),
                    tint = if (artist.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
