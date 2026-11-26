@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.rectangle
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.nekobites.animations.rememberAnimatedShape

@Composable
fun BoxScope.FavoriteArtwork(
    model: Any?,
    @DrawableRes placeholderIcon: Int,
    isPressed: Boolean
) {
    val shapeRotation by animateFloatAsState(
        targetValue = if (isPressed) 40f else 0f,
        animationSpec = bouncySpec()
    )
    val rounded = RoundedPolygon.rectangle(
        rounding = CornerRounding(
            radius = 0.4f
        )
    ).normalized()
    val shape = rememberAnimatedShape(
        condition = isPressed,
        shapeA = rounded,
        shapeB = MaterialShapes.Circle
    )

    Box(
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer { rotationZ = shapeRotation }
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(placeholderIcon),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        AsyncImage(
            model = model,
            contentDescription = stringResource(R.string.artwork),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = -shapeRotation }
        )
    }
}
