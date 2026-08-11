@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)

package com.sosauce.chocola.presentation.screens.album.components

import android.net.Uri
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.utils.ImageUtils
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@Composable
fun SharedTransitionScope.AlbumHeader(
    album: Album,
    tracks: List<CuteTrack>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    Box(
        modifier = Modifier
            .height(300.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        AsyncImage(
            model = ImageUtils.getAlbumArt(album.id),
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

            AnimatedFab(
                onClick = { onHandlePlayerActions(PlayerActions.Play(0, tracks)) },
                icon = R.drawable.widget_play,
                minSize = 90.dp
            )
        }
    }
}

