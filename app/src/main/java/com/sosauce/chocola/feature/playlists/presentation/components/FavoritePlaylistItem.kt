@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.FavoriteItem
import com.sosauce.chocola.feature.playlists.presentation.model.PlaylistUi
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.nekobites.animations.rememberAnimatedShape

@Composable
fun FavoritePlaylistItem(
    modifier: Modifier = Modifier,
    playlist: PlaylistUi,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    FavoriteItem(
        modifier = modifier,
        title = playlist.name,
        subtitle = pluralStringResource(
            R.plurals.tracks_plural,
            playlist.tracks.size,
            playlist.tracks.size
        ),
        isSelected = isSelected,
        isInSelectionMode = isInSelectionMode,
        onClick = onClick,
        onLongClick = onLongClick
    ) { isPressed ->
        PlaylistVisual(playlist = playlist, isPressed = isPressed)
    }
}

@Composable
private fun BoxScope.PlaylistVisual(
    playlist: PlaylistUi,
    isPressed: Boolean
) {
    if (!playlist.emoji.isNullOrBlank()) {
        Text(
            text = playlist.emoji,
            fontSize = 55.sp
        )
        return
    }

    val accent = playlist.color ?: MaterialTheme.colorScheme.surfaceContainerHigh
    val shapeRotation by animateFloatAsState(
        targetValue = if (isPressed) 40f else 0f,
        animationSpec = bouncySpec()
    )
    val shape = rememberAnimatedShape(
        condition = isPressed,
        shapeA = MaterialShapes.Cookie9Sided,
        shapeB = MaterialShapes.Cookie12Sided
    )

    Box(
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer { rotationZ = shapeRotation }
            .clip(shape)
            .background(accent)
    )
    Icon(
        painter = painterResource(R.drawable.queue_music_rounded),
        contentDescription = null,
        tint = contentColorFor(accent),
        modifier = Modifier.size(40.dp)
    )
}
