package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.DetailsHeader
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderArtwork
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderDefaults
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderPlaybackActions
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.util.millisToDuration

@Composable
fun AlbumHeader(
    album: Album,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    modifier: Modifier = Modifier
) {
    DetailsHeader(
        modifier = modifier,
        title = { DetailsHeaderDefaults.Title(album.name) },
        subtitle = { DetailsHeaderDefaults.Subtitle(album.artist) },
        artwork = {
            DetailsHeaderArtwork(
                model = album.art,
                placeholderIcon = R.drawable.album_filled
            )
        },
        backdrop = { DetailsHeaderDefaults.Backdrop(album.art) },
        info = {
            DetailsHeaderDefaults.InfoChip(
                text = pluralStringResource(
                    R.plurals.tracks_plural,
                    album.tracks.size,
                    album.tracks.size
                ),
                icon = R.drawable.music_note
            )
            DetailsHeaderDefaults.InfoChip(
                text = album.durationMillis.millisToDuration(),
                icon = R.drawable.time_filled
            )
        },
        actions = {
            DetailsHeaderPlaybackActions(
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
                }
            )
        }
    )
}

@Preview
@Composable
private fun AlbumHeaderPreview() {
    ChocolaTheme {
        AlbumHeader(
            album = Album(
                name = "A really long album name that keeps going",
                artist = "Artist",
                isFavorite = true
            ),
            onHandlePlayerActions = {}
        )
    }
}
