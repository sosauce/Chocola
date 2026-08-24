@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.rectangle
import com.sosauce.chocola.R
import com.sosauce.chocola.presentation.screens.playing.components.ClassicThumb
import com.sosauce.chocola.presentation.screens.playing.components.MorphingThumb
import com.sosauce.chocola.presentation.screens.playing.components.StraightThumb
import com.sosauce.chocola.presentation.screens.playing.components.StraightTrack
import com.sosauce.chocola.presentation.screens.playing.components.WavyTrack
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

const val CUTE_MUSIC_ID = "CUTE_MUSIC_ID"
const val ICON_TEXT_SPACING = 5
const val GITHUB_RELEASES = "https://github.com/sosauce/Chocola/releases"
const val SUPPORT_PAGE = "https://sosauce.github.io/support/"


object SharedTransitionKeys {
    const val CURRENTLY_PLAYING = "CURRENTLY_PLAYING"
    const val ARTIST = "ARTIST"
    const val PLAY_PAUSE_BUTTON = "PLAY_PAUSE_BUTTON"
    const val SKIP_NEXT_BUTTON = "SKIP_NEXT_BUTTON"
    const val SKIP_PREVIOUS_BUTTON = "SKIP_PREVIOUS_BUTTON"
    const val SEARCHBAR = "SEARCHBAR"
}


object CuteTheme {
    const val SYSTEM = "SYSTEM"
    const val DARK = "DARK"
    const val LIGHT = "LIGHT"
    const val AMOLED = "AMOLED"
}

object ArtworkShape {
    const val ROUNDED = "rounded"
    const val CIRCLE = "circle"
    const val COOKIE_4 = "cookie4"
    const val COOKIE_9 = "cookie9"
    const val COOKIE_12 = "cookie12"
    const val CLOVER_8 = "clover8"
    const val SUNNY = "sunny"
    const val ARROW = "arrow"
    const val DIAMOND = "diamond"
    const val BUN = "bun"
    const val HEART = "heart"

    @Composable
    fun toShape(shape: String): Shape = when (shape) {
        ROUNDED -> RoundedCornerShape(10)
        CIRCLE -> MaterialShapes.Circle.toShape()
        COOKIE_4 -> MaterialShapes.Cookie4Sided.toShape()
        COOKIE_9 -> MaterialShapes.Cookie9Sided.toShape()
        COOKIE_12 -> MaterialShapes.Cookie12Sided.toShape()
        CLOVER_8 -> MaterialShapes.Clover8Leaf.toShape()
        SUNNY -> MaterialShapes.Sunny.toShape()
        ARROW -> MaterialShapes.Arrow.toShape()
        DIAMOND -> MaterialShapes.Diamond.toShape()
        BUN -> MaterialShapes.Bun.toShape()
        HEART -> MaterialShapes.Heart.toShape()
        else -> RoundedCornerShape(10)
    }

    fun toRoundedPolygon(shape: String): RoundedPolygon {


        val rounded = RoundedPolygon.rectangle(
            rounding = CornerRounding(
                radius = 0.2f
            )
        ).normalized()

        println("artwork shape: $shape")

        return when (shape) {
            ROUNDED -> rounded
            CIRCLE -> MaterialShapes.Circle
            COOKIE_4 -> MaterialShapes.Cookie4Sided
            COOKIE_9 -> MaterialShapes.Cookie9Sided
            COOKIE_12 -> MaterialShapes.Cookie12Sided
            CLOVER_8 -> MaterialShapes.Clover8Leaf
            SUNNY -> MaterialShapes.Sunny
            ARROW -> MaterialShapes.Arrow
            DIAMOND -> MaterialShapes.Diamond
            BUN -> MaterialShapes.Bun
            HEART -> MaterialShapes.Heart
            else -> rounded
        }
    }
}

object CutePaletteStyle {
    const val EXPRESSIVE = "Expressive"
    const val FIDELITY = "Fidelity"
    const val TONAL_SPOT = "Tonal spot"
    const val NEUTRAL = "Neutral"
    const val VIBRANT = "Vibrant"
    const val MONOCHROME = "Monochrome"
    const val FRUIT_SALAD = "Fruit salad"
}

object LyricsAlignment {
    const val START = "Start"
    const val CENTERED = "Centered"
    const val END = "End"
}

object ThumbStyle {
    const val STRAIGHT = "Straight"
    const val BALL = "Ball"
    const val MORPHING = "Morphing"


    @Composable
    fun toThumb(
        style: String,
        isDragging: Boolean
    ) {
        when(style) {
            STRAIGHT -> StraightThumb(isDragging)
            BALL -> ClassicThumb(isDragging)
            MORPHING -> MorphingThumb()
        }
    }
}

object TrackStyle {
    const val WAVY = "Wavy"
    const val STRAIGHT = "Straight"


    @Composable
    fun toTrack(
        style: String,
        isPlaying: Boolean,
        sliderState: SliderState
    ) {
        when(style) {
            WAVY -> WavyTrack(isPlaying, sliderState)
            STRAIGHT -> StraightTrack(sliderState)
        }
    }

}
