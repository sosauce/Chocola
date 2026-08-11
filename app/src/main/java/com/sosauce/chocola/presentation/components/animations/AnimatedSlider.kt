@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.chocola.presentation.components.animations

import androidx.annotation.IntRange
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.utils.rememberInteractionSource

@Composable
fun AnimatedSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = rememberInteractionSource(),
    @IntRange(from = 0) steps: Int = 0,
    hideThumb: Boolean = false,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    val isDragging by interactionSource.collectIsDraggedAsState()
    val animatedValue by animateFloatAsState(value)

    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        modifier = modifier,
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                drawStopIndicator = null,
                thumbTrackGapSize = 4.dp,
                modifier = Modifier.height(5.dp),
                trackInsideCornerSize = 3.dp
            )
        },
        thumb = {
            val animatedHeight by animateDpAsState(
                if (it.isDragging) 40.dp else 35.dp
            )

            val animatedWidth by animateDpAsState(
                if (it.isDragging) 10.dp else 6.dp
            )

            SliderDefaults.Thumb(
                interactionSource = rememberInteractionSource(),
                thumbSize = DpSize(animatedWidth, animatedHeight)
            )
        },
        valueRange = valueRange,
        enabled = enabled,
        interactionSource = interactionSource,
        steps = steps,
        colors = colors
    )
}