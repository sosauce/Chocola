@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.artist.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.DetailsHeader
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderArtwork
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderPoster
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils

@Composable
fun SharedTransitionScope.ArtistHeader(
    artist: Artist,
    tracks: List<CuteTrack>,
    albumCount: Int,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    val artUri = ImageUtils.getAlbumArt(artist.albumId)

    DetailsHeader(
        title = artist.name,
        subtitle = null,
        trackCount = tracks.size,
        isFavorite = artist.isFavorite,
        onShuffle = {
            onHandlePlayerActions(
                PlayerActions.PlayFromSource(
                    mediaId = null,
                    source = PlaySource.Artist(artist.name)
                )
            )
        },
        onPlay = {
            onHandlePlayerActions(
                PlayerActions.StartPlaylist(PlaySource.Artist(artist.name))
            )
        },
        onToggleFavorite = {
            onHandlePlayerActions(PlayerActions.ToggleArtistFavorite(artist.id))
        },
        titleModifier = Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(artist.name + artist.id),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current
        ),
        poster = { DetailsHeaderPoster(model = artUri) },
        artwork = {
            DetailsHeaderArtwork(
                model = artUri,
                placeholderIcon = R.drawable.artists_filled
            )
        }
    )
}
