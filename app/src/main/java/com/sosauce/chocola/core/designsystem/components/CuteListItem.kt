package com.sosauce.chocola.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.nekobites.helpers.rememberInteractionSource

/**
 * A surface for how all list items should look like
 */
@Composable
fun CuteListItem(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color.Transparent,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)? = null,
    leadingContent: @Composable () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {

    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = bouncySpec()
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .padding(horizontal = 5.dp, vertical = 1.dp)
            .clip(shape)
            .background(backgroundColor)
            .combinedClickable(
                enabled = onClick != null || onLongClick != null,
                onClick = { onClick?.invoke() },
                onLongClick = onLongClick,
                interactionSource = interactionSource
            )
    ) {
        Row(
            modifier = modifier
                .padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent()
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            ) { content() }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) { trailingContent?.invoke() }
        }
    }
}