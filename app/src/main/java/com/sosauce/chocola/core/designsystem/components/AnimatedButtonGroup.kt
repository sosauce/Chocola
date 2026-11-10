@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Shared logic for button groups whose buttons can be hidden (now playing screen and its preview
 * in settings): position/shape computed from the *visible* buttons only, plus animated show/hide.
 */

@Immutable
enum class GroupPosition { SINGLE, FIRST, MIDDLE, LAST }

fun groupPosition(index: Int, count: Int): GroupPosition = when {
    count <= 1 -> GroupPosition.SINGLE
    index == 0 -> GroupPosition.FIRST
    index == count - 1 -> GroupPosition.LAST
    else -> GroupPosition.MIDDLE
}

@Stable
class AnimatedGroupItem<T>(
    val key: T,
    val isVisible: Boolean,
    /** Position among the visible items only. Hidden items are treated as [GroupPosition.SINGLE]. */
    val position: GroupPosition,
    private val fractionState: State<Float>
) {
    /** 1f when fully shown, 0f when fully hidden. */
    val fraction: Float get() = fractionState.value

    /** Whether the item still needs to be laid out (visible, or still animating out). */
    val isPresent: Boolean get() = isVisible || fraction > 0f
}

/**
 * @param keys every item of the group, in on-screen order. Must be the same list for a given call site.
 * @param hidden keys that should be hidden.
 */
@Composable
fun <T> rememberAnimatedGroupItems(
    keys: List<T>,
    hidden: Set<T>
): List<AnimatedGroupItem<T>> {
    val visibleKeys = keys.filter { it !in hidden }
    return keys.map { itemKey ->
        key(itemKey) {
            val isVisible = itemKey !in hidden
            val fraction = animateFloatAsState(if (isVisible) 1f else 0f)
            AnimatedGroupItem(
                key = itemKey,
                isVisible = isVisible,
                position = if (isVisible) {
                    groupPosition(visibleKeys.indexOf(itemKey), visibleKeys.size)
                } else GroupPosition.SINGLE,
                fractionState = fraction
            )
        }
    }
}

/**
 * Connected-group shape: outer corners on the first/last visible item, inner corners in between,
 * a single visible item is fully rounded. Corners animate on change.
 *
 * @param roundAll forces every corner to [outerRadius] (e.g. a toggled-on button).
 */
@Composable
fun animatedGroupItemShape(
    position: GroupPosition,
    roundAll: Boolean = false,
    outerRadius: Dp = 50.dp,
    innerRadius: Dp = 2.dp
): Shape {
    val roundStart = roundAll || position == GroupPosition.SINGLE || position == GroupPosition.FIRST
    val roundEnd = roundAll || position == GroupPosition.SINGLE || position == GroupPosition.LAST
    val start by animateDpAsState(if (roundStart) outerRadius else innerRadius)
    val end by animateDpAsState(if (roundEnd) outerRadius else innerRadius)

    return RoundedCornerShape(
        topStart = start,
        bottomStart = start,
        topEnd = end,
        bottomEnd = end
    )
}

/**
 * A [ButtonGroup] whose items can be hidden with an animation.
 *
 * At rest it is a plain [ButtonGroup] of the visible items with [spacing], so it behaves exactly like
 * one. While items animate, the gaps become explicit spacers that shrink/grow along with the item,
 * so neighbours move smoothly. When no item is present, nothing (not even [modifier]) is emitted.
 *
 * Items must apply [collapseHorizontally] (fixed-width items) or [animatedWeight] (weighted items).
 */
@Composable
fun <T> AnimatedButtonGroup(
    items: List<AnimatedGroupItem<T>>,
    spacing: Dp,
    modifier: Modifier = Modifier,
    itemContent: @Composable ButtonGroupScope.(AnimatedGroupItem<T>) -> Unit
) {
    if (items.none { it.isPresent }) return

    val isAnimating = items.any { it.fraction > 0f && it.fraction < 1f }

    ButtonGroup(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (isAnimating) 0.dp else spacing),
        overflowIndicator = {}
    ) {
        var maxFractionBefore = 0f
        var isFirst = true
        items.forEach { item ->
            if (!item.isPresent) return@forEach
            val fraction = item.fraction.coerceIn(0f, 1f)
            if (isAnimating && !isFirst) {
                // A gap disappears with the item that owns it, and the leading gap of the
                // first item vanishes as the items before it collapse.
                val gap = spacing * (fraction * maxFractionBefore)
                customItem(
                    buttonGroupContent = { Spacer(Modifier.width(gap)) },
                    menuContent = {}
                )
            }
            maxFractionBefore = max(maxFractionBefore, fraction)
            isFirst = false
            customItem(
                buttonGroupContent = { itemContent(item) },
                menuContent = {}
            )
        }
    }
}

/** For fixed-width items: shrinks the reported width with the item's visibility, clipped and faded. */
fun Modifier.collapseHorizontally(item: AnimatedGroupItem<*>): Modifier = this
    .graphicsLayer { alpha = item.fraction.coerceIn(0f, 1f) }
    .clipToBounds()
    .layout { measurable, constraints ->
        val fraction = item.fraction.coerceIn(0f, 1f)
        if (fraction >= 1f) {
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) { placeable.place(0, 0) }
        } else {
            val naturalWidth = measurable.maxIntrinsicWidth(constraints.maxHeight)
            val placeable = measurable.measure(
                constraints.copy(minWidth = naturalWidth, maxWidth = naturalWidth)
            )
            val width = constraints.constrainWidth((naturalWidth * fraction).roundToInt())
            layout(width, placeable.height) {
                placeable.place((width - placeable.width) / 2, 0)
            }
        }
    }

/** For weighted items: scales [weight] with the item's visibility, clipped and faded. */
fun ButtonGroupScope.animatedWeight(item: AnimatedGroupItem<*>, weight: Float): Modifier =
    Modifier
        .weight(weight * item.fraction.coerceIn(0.001f, 1f))
        .graphicsLayer { alpha = item.fraction.coerceIn(0f, 1f) }
        .clipToBounds()
