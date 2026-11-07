@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.data.local.Playlist
import com.sosauce.chocola.core.designsystem.components.DetailsHeader
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions

@Composable
fun PlaylistHeader(
    playlist: Playlist,
    tracks: List<CuteTrack>,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onToggleFavorite: () -> Unit
) {
    DetailsHeader(
        title = playlist.name,
        subtitle = playlist.tags.joinToString(", ").ifEmpty { null },
        trackCount = tracks.size,
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
        onToggleFavorite = onToggleFavorite,
        artworkColor = if (playlist.emoji.isNotEmpty()) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.primary,
        poster = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )
        },
        artwork = {
            if (playlist.emoji.isNotEmpty()) {
                Text(
                    text = playlist.emoji,
                    style = MaterialTheme.typography.displayLargeEmphasized.copy(
                        fontSize = 82.sp
                    )
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.queue_music_rounded),
                    contentDescription = null,
                    tint = contentColorFor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize(0.5f)
                )
            }
        }
    )
}
