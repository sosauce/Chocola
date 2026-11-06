@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)

package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.SharedTransitionKeys
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.components.Spacer
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

// Header metrics: poster bleeds under the status bar, the contact icon sits centered
// on the poster/card seam (clear of the status bar), and the content card overlaps
// the poster's bottom edge.
private val PosterHeight = 220.dp
private val IconSize = 170.dp
private val CardOverlap = 25.dp
private val SeamY = PosterHeight - CardOverlap

@Composable
fun SharedTransitionScope.AlbumHeader(
    album: Album,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    modifier: Modifier = Modifier
) {
    val cookie9 = MaterialShapes.Cookie9Sided.toShape()
    val highest = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PosterHeight + 20.dp) // keep the 20.dp pls
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
            AsyncImage(
                model = album.art,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .cloudy(15),
                contentScale = ContentScale.Crop
            )
        }

        Surface(
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = SeamY)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = IconSize / 2 + 12.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = album.name,
                    modifier = Modifier
                        .basicMarquee(),
                    style = MaterialTheme.typography.headlineLargeEmphasized
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = SeamY - IconSize / 2)
        ) {
            Box(
                modifier = modifier
                    .drawWithCache {
                        val path = cookie9.createOutline(size, layoutDirection, this)
                            .let { outline ->
                                Path().apply {
                                    when (outline) {
                                        is Outline.Generic -> addPath(outline.path)
                                        is Outline.Rounded -> addRoundRect(outline.roundRect)
                                        is Outline.Rectangle -> addRect(outline.rect)
                                    }
                                }
                            }
                        onDrawBehind {
                            withTransform(
                                {
                                    scale(
                                        scaleX = 1.08f,
                                        scaleY = 1.08f,
                                        pivot = center
                                    )
                                }
                            ) {
                                drawPath(
                                    path = path,
                                    color = highest
                                )
                            }
                        }
                    }
                    .size(IconSize)
                    .clip(MaterialShapes.Cookie9Sided.toShape())
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialShapes.Cookie9Sided.toShape()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.album_filled),
                    contentDescription = null,
                    tint = contentColorFor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(IconSize / 2)
                )
                AsyncImage(
                    model = album.art,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialShapes.Cookie9Sided.toShape()),
                    contentScale = ContentScale.Crop
                )
            }


            val favoriteButtonColor by animateColorAsState(
                if (album.isFavorite) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.surfaceContainerHigh
            )

            FilledIconButton(
                onClick = { onHandlePlayerActions(PlayerActions.ToggleAlbumFavorite(album.id)) },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = favoriteButtonColor,
                    contentColor = contentColorFor(favoriteButtonColor)
                ),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(
                        if (album.isFavorite) R.drawable.favorite_filled else R.drawable.favorite
                    ),
                    contentDescription = null
                )
            }
        }
    }
}
