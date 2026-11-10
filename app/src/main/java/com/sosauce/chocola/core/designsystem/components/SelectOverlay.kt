package com.sosauce.chocola.core.designsystem.components

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.skydoves.cloudy.cloudy
import com.sosauce.nekobites.animations.bouncySpec
import kotlin.math.roundToInt

/**
 * An action shown in the menu of a [SelectOverlayItem].
 * @param isDestructive tints the action with the error color
 */
@Immutable
data class SelectOverlayAction(
    val label: String,
    @DrawableRes val icon: Int,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun rememberSelectOverlayState(): SelectOverlayState = remember { SelectOverlayState() }

@Stable
class SelectOverlayState internal constructor() {

    /** Item currently lifted, kept until the exit animation has finished. */
    internal var entry by mutableStateOf<SelectOverlayEntry?>(null)
        private set

    internal var isOpen by mutableStateOf(false)
        private set

    private var pendingAction: (() -> Unit)? = null

    internal fun show(entry: SelectOverlayEntry) {
        this.entry = entry
        isOpen = true
    }

    /**
     * @param then runs once it is fully closed.
     */
    fun dismiss(then: (() -> Unit)? = null) {
        if (!isOpen) return
        pendingAction = then
        isOpen = false
    }

    internal fun onClosed() {
        entry = null
        pendingAction?.invoke()
        pendingAction = null
    }

    internal fun isLifted(key: Any): Boolean = entry?.key === key
}

/**
 * Snapshot of a [SelectOverlayItem] taken when it is lifted. The item never changes while lifted,
 * so this is exactly what the overlay shows.
 * @param key identifies the item, also used as its shared element key
 * @param onSelect starts multi selection with the item
 * @param locals composition locals at the item's position, re-provided around [content] in the overlay
 */
@Immutable
internal class SelectOverlayEntry(
    val key: Any,
    val bounds: Rect,
    val actions: List<SelectOverlayAction>,
    val onSelect: () -> Unit,
    val locals: CompositionLocalContext,
    val content: @Composable () -> Unit
)

/** What the host hands down to every [SelectOverlayItem]. */
@Stable
private class SelectOverlayHostScope(
    val state: SelectOverlayState,
    val sharedTransitionScope: SharedTransitionScope,
    val liftScale: State<Float>
)

private val LocalSelectOverlayHost = staticCompositionLocalOf<SelectOverlayHostScope?> { null }

/**
 * Wraps the app content. When an item is lifted, blurs and dims [content] and draws the lifted item and its menu above it.
 * The overlay lives in the same window as [content] (no Dialog/Popup) so Cloudy can blur it.
 *
 * The lifted copy is composed with the composition locals of the original item, and moves between the item
 * and the overlay with a shared element transition.
 */
@Composable
fun SelectOverlayHost(
    state: SelectOverlayState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = state.isOpen, label = "select overlay")
    val enterEffects = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val progress = transition.animateFloat(
        transitionSpec = { if (targetState) enterEffects else SelectOverlayDefaults.exitSpec() },
        label = "progress"
    ) { open -> if (open) 1f else 0f }
    val liftScale = transition.animateFloat(
        transitionSpec = { if (targetState) bouncySpec() else SelectOverlayDefaults.exitSpec() },
        label = "lift"
    ) { open -> if (open) SelectOverlayDefaults.LiftedScale else 1f }

    // isRunning covers an open interrupted halfway, where current and target are both already false
    val isActive = transition.currentState || transition.targetState || transition.isRunning
    LaunchedEffect(isActive) {
        if (!isActive) state.onClosed()
    }

    var hostOffset by remember { mutableStateOf(Offset.Zero) }

    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        val hostScope = remember(state, this, liftScale) {
            SelectOverlayHostScope(state, this, liftScale)
        }
        CompositionLocalProvider(LocalSelectOverlayHost provides hostScope) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { hostOffset = it.positionInWindow() }
            ) {
                BlurredContent(
                    progress = { progress.value },
                    content = content
                )
                val entry = state.entry
                if (isActive && entry != null) {
                    SelectOverlayLayer(
                        state = state,
                        entry = entry,
                        transition = transition,
                        progress = progress,
                        liftScale = liftScale,
                        hostOffset = hostOffset
                    )
                }
            }
        }
    }
}

/**
 * Wraps an item that can be lifted into the [SelectOverlayHost].
 * [content] receives a `showOverlay` trigger to call from the item's own long press.
 * While lifted the item is hidden and the host draws a copy of [content], the two are linked by a shared element.
 */
@Composable
fun SelectOverlayItem(
    actions: List<SelectOverlayAction>,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable (showOverlay: () -> Unit) -> Unit
) {
    val host = LocalSelectOverlayHost.current
    if (host == null) {
        Box(modifier) { content {} }
        return
    }
    val state = host.state
    val haptics = LocalHapticFeedback.current
    val locals = currentCompositionLocalContext
    val key = remember { Any() }
    val coordinates = remember { CoordinatesHolder() }

    val showOverlay: () -> Unit = show@{
        val coords = coordinates.value?.takeIf { it.isAttached } ?: return@show
        if (!enabled) return@show
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        state.show(
            SelectOverlayEntry(
                key = key,
                bounds = coords.boundsInWindow(),
                actions = actions,
                onSelect = onSelect,
                locals = locals,
                content = { content {} }
            )
        )
    }

    with(host.sharedTransitionScope) {
        Box(
            modifier = modifier
                .onGloballyPositioned { coordinates.value = it }
                // keeps the item above the scrim and blur while the overlay animates out
                .renderInSharedTransitionScopeOverlay(
                    zIndexInOverlay = 1f,
                    renderInOverlay = { state.isLifted(key) }
                )
                .sharedElementWithCallerManagedVisibility(
                    sharedContentState = rememberSharedContentState(key),
                    visible = !(state.isOpen && state.isLifted(key)),
                    boundsTransform = SelectOverlayDefaults.ExitBoundsTransform
                )
                .graphicsLayer {
                    val scale = if (state.isLifted(key)) host.liftScale.value else 1f
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            content(showOverlay)
        }
    }
}

private class CoordinatesHolder {
    var value: LayoutCoordinates? = null
}

@Composable
private fun BlurredContent(
    progress: () -> Float,
    content: @Composable () -> Unit
) {
    val value = progress()
    // RenderEffect (API 31+) is cheap enough to animate per frame, the legacy CPU blur is not,
    // so below 31 the blur is applied at full radius for the whole time the overlay is shown and only the scrim fades.
    val radius = when {
        value <= 0f -> 0
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> (value * SelectOverlayDefaults.BlurRadius).roundToInt()
        else -> SelectOverlayDefaults.BlurRadius
    }
    Box(
        modifier = Modifier.cloudy(
            radius = radius,
            enabled = radius > 0
        )
    ) {
        content()
    }
}

@Composable
private fun SharedTransitionScope.SelectOverlayLayer(
    state: SelectOverlayState,
    entry: SelectOverlayEntry,
    transition: Transition<Boolean>,
    progress: State<Float>,
    liftScale: State<Float>,
    hostOffset: Offset
) {
    val scrimColor = MaterialTheme.colorScheme.scrim
    val enterBounds = MaterialTheme.motionScheme.defaultSpatialSpec<Rect>()
    val dismissOnTap = Modifier.pointerInput(state) { detectTapGestures { state.dismiss() } }

    BackHandler(enabled = state.isOpen) { state.dismiss() }

    Box(Modifier.fillMaxSize()) {
        // scrim, also eats every touch while open so nothing leaks to the content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        scrimColor,
                        alpha = progress.value * SelectOverlayDefaults.ScrimAlpha
                    )
                }
                .then(if (state.isOpen) dismissOnTap else Modifier)
        )
        SelectOverlayContentLayout(
            // window bounds -> host bounds, the host is normally at the window origin
            itemBounds = entry.bounds.translate(-hostOffset),
            transition = transition,
            actions = entry.actions,
            onActionClick = { action -> state.dismiss(then = action.onClick) },
            onSelect = entry.onSelect,
            liftedContent = {
                Box(
                    modifier = Modifier.sharedElementWithCallerManagedVisibility(
                        sharedContentState = rememberSharedContentState(entry.key),
                        visible = state.isOpen,
                        boundsTransform = { _, _ -> enterBounds }
                    )
                ) {
                    Box(
                        modifier = Modifier.graphicsLayer {
                            scaleX = liftScale.value
                            scaleY = liftScale.value
                        }
                    ) {
                        CompositionLocalProvider(entry.locals) {
                            entry.content()
                        }
                    }
                    // the copy is a preview, tapping it dismisses like iOS instead of reaching the copy's own click handlers
                    Box(
                        Modifier
                            .matchParentSize()
                            .then(if (state.isOpen) dismissOnTap else Modifier)
                    )
                }
            }
        )
    }
}

/**
 * Places the lifted copy and its menu. The copy stays where the item is unless the menu fits neither below nor above it,
 * in which case the copy is moved up so the menu fits below. The shared element animates that move.
 */
@Composable
private fun SelectOverlayContentLayout(
    itemBounds: Rect,
    transition: Transition<Boolean>,
    actions: List<SelectOverlayAction>,
    onActionClick: (SelectOverlayAction) -> Unit,
    onSelect: () -> Unit,
    liftedContent: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val insets = WindowInsets.safeDrawing
    var origin by remember { mutableStateOf(TransformOrigin(0f, 0f)) }

    val enterSpatial = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val enterEffects = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val menuScale by transition.animateFloat(
        transitionSpec = { if (targetState) enterSpatial else SelectOverlayDefaults.exitSpec() },
        label = "menu scale"
    ) { open -> if (open) 1f else SelectOverlayDefaults.MenuHiddenScale }
    val menuAlpha by transition.animateFloat(
        transitionSpec = { if (targetState) enterEffects else SelectOverlayDefaults.exitSpec() },
        label = "menu alpha"
    ) { open -> if (open) 1f else 0f }

    // rows fade in one after the other on enter, on exit the menu fades out as a whole.
    // +1 for the mandatory select action the menu always shows first
    val rowAlphas = List(actions.size + 1) { index ->
        transition.animateFloat(
            transitionSpec = {
                if (targetState) {
                    tween(
                        durationMillis = SelectOverlayDefaults.RowFadeMillis,
                        delayMillis = index * SelectOverlayDefaults.RowStaggerMillis
                    )
                } else {
                    snap()
                }
            },
            label = "row alpha"
        ) { open -> if (open) 1f else 0f }
    }

    Layout(
        content = {
            liftedContent()
            SelectOverlayMenu(
                actions = actions,
                onActionClick = onActionClick,
                onSelect = onSelect,
                modifier = Modifier.graphicsLayer {
                    transformOrigin = origin
                    scaleX = menuScale
                    scaleY = menuScale
                    alpha = menuAlpha
                },
                itemModifier = { index ->
                    Modifier.graphicsLayer {
                        val rowAlpha = if (transition.targetState) rowAlphas[index].value else 1f
                        alpha = rowAlpha
                        translationY = (1f - rowAlpha) * SelectOverlayDefaults.RowOffset.toPx()
                    }
                }
            )
        }
    ) { measurables, constraints ->
        val margin = SelectOverlayDefaults.ScreenMargin.roundToPx()
        val gap = SelectOverlayDefaults.MenuGap.roundToPx()
        val top = insets.getTop(density) + margin
        val bottom = constraints.maxHeight - insets.getBottom(density) - margin
        val start = insets.getLeft(density, LayoutDirection.Ltr) + margin
        val end = constraints.maxWidth - insets.getRight(density, LayoutDirection.Ltr) - margin

        val itemWidth = itemBounds.width.roundToInt().coerceAtLeast(0)
        val itemHeight = itemBounds.height.roundToInt().coerceAtLeast(0)
        val lifted = measurables[0].measure(Constraints.fixed(itemWidth, itemHeight))
        val menu = measurables[1].measure(
            Constraints(
                maxWidth = (end - start).coerceAtLeast(0),
                maxHeight = (bottom - top).coerceAtLeast(0)
            )
        )

        // the lifted copy grows from its center, keep the menu clear of its scaled bounds
        val grow = (itemHeight * (SelectOverlayDefaults.LiftedScale - 1f) / 2f).roundToInt()
        val itemTop = itemBounds.top.roundToInt()
        val fitsBelow = itemTop + itemHeight + grow + gap + menu.height <= bottom
        val fitsAbove = itemTop - grow - gap - menu.height >= top
        val showBelow = fitsBelow || !fitsAbove

        val liftedY = if (fitsBelow || fitsAbove) {
            itemTop
        } else {
            (bottom - menu.height - gap - grow - itemHeight).coerceAtLeast(top + grow)
        }
        val menuY = if (showBelow) {
            (liftedY + itemHeight + grow + gap).coerceAtMost(bottom - menu.height)
        } else {
            liftedY - grow - gap - menu.height
        }
        val liftedX = itemBounds.left.roundToInt()
        val menuX = liftedX
            .coerceAtMost(end - menu.width)
            .coerceAtLeast(start)

        origin = TransformOrigin(
            pivotFractionX = if (menu.width == 0) 0f else
                ((liftedX + itemWidth / 2f - menuX) / menu.width).coerceIn(0f, 1f),
            pivotFractionY = if (showBelow) 0f else 1f
        )

        layout(constraints.maxWidth, constraints.maxHeight) {
            lifted.place(liftedX, liftedY)
            menu.place(menuX, menuY)
        }
    }
}

object SelectOverlayDefaults {
    const val BlurRadius = 24
    const val ScrimAlpha = 0.32f
    const val LiftedScale = 1.05f
    internal const val MenuHiddenScale = 0.8f
    internal const val RowFadeMillis = 250
    internal const val RowStaggerMillis = 50
    internal val RowOffset = 8.dp
    internal val MenuGap = 12.dp
    internal val ScreenMargin = 16.dp

    /** Shared by every exit animation so they all settle together, no bounce so nothing lingers. */
    internal fun exitSpec() = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    internal val ExitBoundsTransform = BoundsTransform { _, _ ->
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
            visibilityThreshold = Rect.VisibilityThreshold
        )
    }
}
