@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)

package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils
import com.sosauce.nekobites.animations.AnimatedFab
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@Composable
fun SharedTransitionScope.AlbumHeader(
    album: Album,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    Box(
        modifier = Modifier
            .height(300.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        AsyncImage(
            model = album.art,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.headlineLargeEmphasized,
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(album.name + album.id),
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current
                        )
                        .basicMarquee()
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(album.artist + album.id),
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current
                        )
                        .basicMarquee()
                )
            }

            IconButton(onClick = { onHandlePlayerActions(PlayerActions.ToggleAlbumFavorite(album.id)) }) {
                Icon(
                    painter = painterResource(R.drawable.favorite_filled),
                    contentDescription = stringResource(if (album.isFavorite) R.string.remove_favorite else R.string.add_favorite),
                    tint = if (album.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedFab(
                onClick = { onHandlePlayerActions(PlayerActions.AddToQueue(album.tracks)) },
                icon = R.drawable.add_to_queue,
                modifier = Modifier.align(Alignment.Bottom).size(45.dp)
            )

            AnimatedFab(
                onClick = {
                    onHandlePlayerActions(
                        PlayerActions.StartPlaylist(
                            source = PlaySource.Album(album.name)
                        )
                    )
                },
                icon = R.drawable.widget_play,
                minSize = 90.dp
            )
        }
    }
}
