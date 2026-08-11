package com.sosauce.chocola.presentation.components.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.sosauce.chocola.utils.rememberInteractionSource

@Composable
fun AnimatedIconButton(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Int,
    contentDescription: String?
) {
    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f
    )


    IconButton(
        onClick = onClick,
        shapes = IconButtonDefaults.shapes(),
        modifier = buttonModifier,
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}