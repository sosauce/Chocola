@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.data.datastore.rememberThumbStyle
import com.sosauce.chocola.data.datastore.rememberTrackStyle
import com.sosauce.chocola.utils.ThumbStyle
import com.sosauce.chocola.utils.TrackStyle
import com.sosauce.chocola.utils.rememberInteractionSource



@Composable
fun StraightThumb(isDragging: Boolean) {
    val animatedHeight by animateDpAsState(
        if (isDragging) 40.dp else 35.dp
    )

    val animatedWidth by animateDpAsState(
        if (isDragging) 10.dp else 6.dp
    )

    SliderDefaults.Thumb(
        interactionSource = rememberInteractionSource(),
        thumbSize = DpSize(animatedWidth, animatedHeight)
    )
}

@Composable
fun MorphingThumb() = LoadingIndicator(modifier = Modifier.size(35.dp))



@Composable
fun ClassicThumb(isDragging: Boolean) {
    val width by animateDpAsState(
        targetValue = if (isDragging) 28.dp else 20.dp
    )
    SliderDefaults.Thumb(
        interactionSource = rememberInteractionSource(),
        thumbSize = DpSize(
            width = width,
            height = 20.dp
        )
    )
}

@Composable
fun WavySlider(
    state: SliderState
) {
    Slider(
        state = state,
        thumb = { StraightThumb(it.isDragging) },
        track = { WavyTrack(true, state) }
    )
}

@Composable
fun WavyTrack(
    isPlaying: Boolean,
    sliderState: SliderState
) {
    val animatedHeight by animateDpAsState(
        if (sliderState.isDragging) 7.dp else 4.dp
    )
    val trackStroke = Stroke(
        width =
            with(LocalDensity.current) {
                animatedHeight.toPx()
            },
        cap = StrokeCap.Round,
    )

    LinearWavyProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        progress = {
            val rangeLength = sliderState.valueRange.endInclusive - sliderState.valueRange.start
            if (rangeLength > 0f) {
                (sliderState.value - sliderState.valueRange.start) / rangeLength
            } else 0f
        },
        stopSize = 0.dp,
        trackStroke = trackStroke,
        amplitude = { if (isPlaying && !sliderState.isDragging) 1f else 0f }
    )
}

@Composable
fun StraightTrack(sliderState: SliderState) {
    SliderDefaults.Track(
        sliderState = sliderState,
        drawStopIndicator = null,
        thumbTrackGapSize = 0.dp,
        modifier = Modifier.height(4.dp)
    )
}
