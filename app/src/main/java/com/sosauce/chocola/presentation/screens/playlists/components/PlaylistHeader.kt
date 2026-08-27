@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playlists.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.models.Playlist
import com.sosauce.chocola.domain.actions.PlaySource
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.nekobites.animations.AnimatedFab

@Composable
fun PlaylistHeader(
    playlist: Playlist,
    tracks: List<CuteTrack>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .statusBarsPadding()
            .padding(20.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {

            if (playlist.emoji.isNotEmpty()) {
                Text(
                    text = playlist.emoji,
                    style = MaterialTheme.typography.displayLargeEmphasized
                )
            }

            Text(
                text = playlist.name,
                style = MaterialTheme.typography.headlineLargeEmphasized,
                modifier = Modifier.basicMarquee()
            )
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                playlist.tags.forEach { tag ->
                    Button(
                        onClick = {},
                        shapes = ButtonDefaults.shapes(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                        )
                    ) { Text(tag) }
                }
            }
        }

        AnimatedFab(
            onClick = {
                onHandlePlayerActions(
                    PlayerActions.StartPlaylist(
                        source = PlaySource.ExplicitTracks(tracks)
                    )
                )
            },
            icon = R.drawable.widget_play,
            minSize = 90.dp
        )
    }
}