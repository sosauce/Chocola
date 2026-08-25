@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.album.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.utils.ImageUtils

@Composable
fun SharedTransitionScope.AlbumCard(
    modifier: Modifier = Modifier,
    album: Album,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .padding(horizontal = 5.dp, vertical = 10.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.album_filled),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        AsyncImage(
            model = ImageUtils.getAlbumArt(album.id),
            contentDescription = stringResource(id = R.string.artwork),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
                .padding(15.dp)
        ) {
            Text(
                text = album.name,
                maxLines = 1,
                style = MaterialTheme.typography.titleMediumEmphasized,
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
    }
}