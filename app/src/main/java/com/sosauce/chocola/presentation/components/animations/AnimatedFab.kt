@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.components.animations

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import com.sosauce.chocola.utils.bouncySpec
import com.sosauce.chocola.utils.rememberInteractionSource


private data class FabAnimation(
    val rotation: Float,
    val scale: Float,
    val shape: Shape
)

@Composable
private fun rememberFabAnimations(isPressed: Boolean): FabAnimation {
    val morph = remember {
        Morph(
            MaterialShapes.Cookie9Sided,
            MaterialShapes.Circle
        )
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        label = "scale",
        animationSpec = bouncySpec()
    )

    val animatedRotation by animateFloatAsState(
        targetValue = if (isPressed) 180f else 0f,
        label = "rotation",
        animationSpec = bouncySpec()
    )

    val animatedProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        label = "progress",
        animationSpec = bouncySpec()
    )

    val shape = remember(morph, animatedProgress) {
        MorphPolygonShape(morph, animatedProgress)
    }

    return FabAnimation(
        rotation = animatedRotation,
        scale = animatedScale,
        shape = shape
    )
}

@Composable
fun AnimatedFab(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    minSize: Dp = 56.dp,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    enabled: Boolean = true
) {
    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val fabAnimation = rememberFabAnimations(isPressed)
    val color by animateColorAsState(
        if (enabled) containerColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
    val contentColor by animateColorAsState(
        targetValue = contentColorFor(color).copy(
            alpha = if (enabled) 1f else 0.1f
        )
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = fabAnimation.scale
                scaleY = fabAnimation.scale
            }
            .defaultMinSize(minWidth = minSize, minHeight = minSize)
            .clip(fabAnimation.shape)
            .background(color)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    rotationZ = fabAnimation.rotation
                }
        )
    }
}

@Composable
fun ToggleAnimatedFab(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    minSize: Dp = 56.dp,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    icon: (checkedProgress: Float) -> Int
) {

    val checkedProgress by
        animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = bouncySpec(),
        )
    val fabAnimation = rememberFabAnimations(checkedProgress > .5f)

    Box(
        modifier = modifier
            .scale(fabAnimation.scale)
            .defaultMinSize(minWidth = minSize, minHeight = minSize)
            .clip(fabAnimation.shape)
            .background(containerColor)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                interactionSource = null,
                indication = null,
            )
    ) {
        Icon(
            painter = painterResource(icon(checkedProgress)),
            contentDescription = null,
            tint = contentColorFor(containerColor),
            modifier = Modifier
                .align(Alignment.Center)
                .rotate(fabAnimation.rotation)
        )
    }
}


class MorphPolygonShape(
    private val morph: Morph,
    private val percentage: Float
) : Shape {

    private val matrix = Matrix()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        matrix.scale(size.width, size.height)
        val path = morph.toPath(progress = percentage)
        path.transform(matrix)
        return Outline.Generic(path)
    }
}
