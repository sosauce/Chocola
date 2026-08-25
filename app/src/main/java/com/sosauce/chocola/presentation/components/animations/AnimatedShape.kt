package com.sosauce.chocola.presentation.components.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import com.sosauce.chocola.utils.bouncySpec

@Composable
fun rememberAnimatedShape(
    condition: Boolean,
    shapeA: RoundedPolygon,
    shapeB: RoundedPolygon
): MorphPolygonShape {

    val morph = remember(shapeA, shapeB) {
        Morph(
            shapeA,
            shapeB
        )
    }
    val animatedProgress by animateFloatAsState(
        targetValue = if (condition) 1f else 0f,
        animationSpec = bouncySpec()
    )
    return remember(morph, animatedProgress) {
        MorphPolygonShape(morph, animatedProgress)
    }
}