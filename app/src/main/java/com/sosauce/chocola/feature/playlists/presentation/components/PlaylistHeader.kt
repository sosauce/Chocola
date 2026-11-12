@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.sp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.DetailsHeader
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderDefaults
import com.sosauce.chocola.core.designsystem.components.DetailsHeaderPlaybackActions
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.util.millisToDuration
import com.sosauce.chocola.feature.playlists.presentation.model.PlaylistUi

@Composable
fun PlaylistHeader(
    playlist: PlaylistUi,
    tracks: List<CuteTrack>,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val emoji = playlist.emoji

    DetailsHeader(
        title = { DetailsHeaderDefaults.Title(playlist.name) },
        artwork = {
            if (emoji != null) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.displayLargeEmphasized.copy(fontSize = 82.sp)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.queue_music_rounded),
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.fillMaxSize(0.45f)
                )
            }
        },
        artworkShape = MaterialShapes.Cookie9Sided.toShape(),
        artworkContainerColor = if (emoji != null) colors.surfaceContainerHigh else colors.primary,
        backdrop = { DetailsHeaderDefaults.GradientBackdrop() },
        info = {
            DetailsHeaderDefaults.InfoChip(
                text = pluralStringResource(R.plurals.tracks_plural, tracks.size, tracks.size),
                icon = R.drawable.music_note
            )
            DetailsHeaderDefaults.InfoChip(
                text = playlist.tracks.sumOf { it.durationMillis }.millisToDuration(),
                icon = R.drawable.time_filled
            )
            playlist.tags.forEach { tag ->
                DetailsHeaderDefaults.InfoChip(
                    text = tag,
                    containerColor = colors.tertiaryContainer,
                    contentColor = colors.onTertiaryContainer
                )
            }
        },
        actions = {
            DetailsHeaderPlaybackActions(
                isFavorite = playlist.isFavorite,
                onShuffle = {
                    onHandlePlayerActions(
                        PlayerActions.PlayFromSource(
                            mediaId = null,
                            source = PlaySource.ExplicitTracks(tracks)
                        )
                    )
                },
                onPlay = {
                    onHandlePlayerActions(
                        PlayerActions.StartPlaylist(PlaySource.ExplicitTracks(tracks))
                    )
                },
                onToggleFavorite = onToggleFavorite
            )
        }
    )
}
