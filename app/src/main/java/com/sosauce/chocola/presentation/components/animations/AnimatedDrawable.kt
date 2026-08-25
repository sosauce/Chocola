package com.sosauce.chocola.presentation.components.animations


import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedDrawable(
    modifier: Modifier = Modifier,
    drawable: Int,
    atEnd: Boolean
) {


    val animated = rememberAnimatedVectorPainter(
        animatedImageVector = AnimatedImageVector.animatedVectorResource(drawable),
        atEnd = atEnd
    )

    Icon(
        painter = animated,
        contentDescription = null,
        modifier = modifier.size(24.dp)
    )
}