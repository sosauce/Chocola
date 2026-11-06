package com.sosauce.chocola.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Corner radius used by grouped cards and list items (settings cards, folder items, grouped tracks...).
 * The outer corners of a group are [Outer], the corners between two items of the same group are [Inner].
 */
object GroupedShapeDefaults {
    val Outer = 24.dp
    val Inner = 2.dp
}

/**
 * Shape of an item inside a vertical group, with [top] radius on both top corners and [bottom] radius on both bottom corners.
 */
fun groupedShape(
    top: Dp,
    bottom: Dp
): Shape = RoundedCornerShape(
    topStart = top,
    topEnd = top,
    bottomStart = bottom,
    bottomEnd = bottom
)

/**
 * Shape of an item inside a vertical group, rounded on the outside of the group only.
 */
fun groupedShape(
    isFirst: Boolean,
    isLast: Boolean
): Shape = groupedShape(
    top = if (isFirst) GroupedShapeDefaults.Outer else GroupedShapeDefaults.Inner,
    bottom = if (isLast) GroupedShapeDefaults.Outer else GroupedShapeDefaults.Inner
)
