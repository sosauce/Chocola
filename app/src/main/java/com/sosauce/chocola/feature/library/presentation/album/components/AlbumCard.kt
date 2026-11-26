@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.SelectionCheckbox
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.CollectionMoreButton
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.nekobites.helpers.rememberInteractionSource

@Composable
fun SharedTransitionScope.AlbumCard(
    modifier: Modifier = Modifier,
    album: Album,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    isInSelectionMode: Boolean = false,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed || isSelected) 0.95f else 1f,
        animationSpec = bouncySpec()
    )

    Box(
        modifier = modifier
            .padding(horizontal = 5.dp, vertical = 10.dp)
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                interactionSource = interactionSource,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.album_filled),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        AsyncImage(
            model = album.art,
            contentDescription = stringResource(id = R.string.artwork),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        AnimatedVisibility(
            visible = !isInSelectionMode,
            modifier = Modifier.align(Alignment.TopEnd),
            enter = scaleIn(bouncySpec()),
            exit = scaleOut(bouncySpec())
        ) {
            CollectionMoreButton(
                tracks = album.tracks,
                source = PlaySource.Album(album.name),
                isFavorite = album.isFavorite,
                onToggleFavorite = { onHandlePlayerActions(PlayerActions.ToggleAlbumFavorite(album.id)) },
                onHandlePlayerActions = onHandlePlayerActions
            )
        }
        SelectionCheckbox(
            visible = isInSelectionMode,
            checked = isSelected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
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
                    .basicMarquee()
            )
            Text(
                text = album.artist,
                style = MaterialTheme.typography.bodyMediumEmphasized,
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
