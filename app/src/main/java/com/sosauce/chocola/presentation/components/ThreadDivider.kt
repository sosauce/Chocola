package com.sosauce.chocola.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Basically HorizontalDivider + VerticalDivider with a curved edge
@Composable
fun ThreadDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = MaterialTheme.colorScheme.onBackground,
    curveSize: Dp = 10.dp
) {
    Canvas(
        modifier = modifier
            .width(20.dp)
            .height(50.dp)
    ) {
        val strokeWidth = thickness.toPx()
        val midX = strokeWidth / 2
        val midY = size.height / 2
        val curvePx = curveSize.toPx()

        val path = Path().apply {
            moveTo(midX, 0f)
            lineTo(midX, midY - curvePx)
            cubicTo(
                midX, midY,
                midX + curvePx, midY,
                midX + curvePx, midY
            )
            lineTo(size.width, midY)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}