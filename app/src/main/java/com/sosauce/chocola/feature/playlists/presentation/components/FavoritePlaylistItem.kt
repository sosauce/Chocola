@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.data.local.Playlist
import com.sosauce.chocola.core.designsystem.components.FavoriteBubble
import com.sosauce.chocola.core.designsystem.components.FavoriteItem
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.nekobites.animations.rememberAnimatedShape
import com.sosauce.nekobites.components.AnimatedSelectedIcon

@Composable
fun FavoritePlaylistItem(
    modifier: Modifier = Modifier,
    playlist: Playlist,
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
            playlist.musics.size,
            playlist.musics.size
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
    playlist: Playlist,
    isPressed: Boolean
) {
    if (playlist.emoji.isNotBlank()) {
        Text(
            text = playlist.emoji,
            fontSize = 55.sp
        )
        return
    }

    val accent = if (playlist.color != -1) {
        Color(playlist.color)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
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
