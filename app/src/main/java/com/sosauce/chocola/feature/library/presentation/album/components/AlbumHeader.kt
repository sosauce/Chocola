@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.DetailsHeader
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderArtwork
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderPoster
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions

@Composable
fun AlbumHeader(
    album: Album,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    modifier: Modifier = Modifier
) {
    DetailsHeader(
        title = album.name,
        subtitle = album.artist,
        trackCount = album.tracks.size,
        isFavorite = album.isFavorite,
        onShuffle = {
            onHandlePlayerActions(
                PlayerActions.PlayFromSource(
                    mediaId = null,
                    source = PlaySource.Album(album.name)
                )
            )
        },
        onPlay = {
            onHandlePlayerActions(
                PlayerActions.StartPlaylist(PlaySource.Album(album.name))
            )
        },
        onToggleFavorite = {
            onHandlePlayerActions(PlayerActions.ToggleAlbumFavorite(album.id))
        },
        poster = { DetailsHeaderPoster(model = album.art) },
        artwork = {
            DetailsHeaderArtwork(
                model = album.art,
                placeholderIcon = R.drawable.album_filled
            )
        },
        modifier = modifier
    )
}
