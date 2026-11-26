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
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderDefaults
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderPlaybackActions
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions

@Composable
fun SharedTransitionScope.ArtistHeader(
    artist: Artist,
    tracks: List<CuteTrack>,
    albumCount: Int,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    DetailsHeader(
        title = {
            DetailsHeaderDefaults.Title(
                text = artist.name,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(artist.name + artist.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
            )
        },
        artwork = null,
        info = {
            DetailsHeaderDefaults.InfoChip(
                text = pluralStringResource(R.plurals.tracks_plural, tracks.size, tracks.size),
                icon = R.drawable.music_note
            )
            if (albumCount > 0) {
                DetailsHeaderDefaults.InfoChip(
                    text = pluralStringResource(R.plurals.albums_plural, albumCount, albumCount),
                    icon = R.drawable.album_filled
                )
            }
        },
        actions = {
            DetailsHeaderPlaybackActions(
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
                }
            )
        }
    )
}
