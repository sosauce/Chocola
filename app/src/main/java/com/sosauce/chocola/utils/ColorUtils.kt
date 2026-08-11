package com.sosauce.chocola.utils

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

// https://github.com/Elnix90/Dragon-Launcher/blob/3031fa8f86b912dbd2882df68324944bdaf24822/core/base/src/main/kotlin/org/elnix/dragonlauncher/base/ColorUtils.kt

object ColorUtils {

    /**
     * Returns this [androidx.compose.ui.graphics.Color] only if it is explicitly defined.
     *
     * If the receiver is `null` or equal to [androidx.compose.ui.graphics.Color.Companion.Unspecified], this returns `null`.
     * Otherwise, it returns the receiver unchanged.
     *
     * This is useful when treating [androidx.compose.ui.graphics.Color.Companion.Unspecified] as an absent value
     * and normalizing it to `null` for clearer nullable handling.
     *
     * @return this color if defined, or `null` if it is `null` or `Color.Unspecified`
     */
    fun Color?.definedOrNull(): Color? =
        this.takeIf { it != Color.Unspecified }

    /**
     * Transforms an [Int] into a color. If [Int] == -1, returns the fallback color
     * @param fallback Fallback color in-case a [Color] cannot be created from the [Int]
     * @return A [Color]
     */
    fun Int.toColor(
        fallback: Color
    ): Color {
        return if (this == -1) {
            fallback
        } else Color(this)
    }


//    /**
//     * Returns this [Color] if it is non-null, or [default] otherwise.
//     *
//     * This is a convenience extension for providing a fallback color when
//     * working with nullable [Color] values.
//     *
//     * Note that this does not treat [Color.Companion.Unspecified] as null; if the
//     * receiver is `Color.Unspecified`, it will be returned as-is.
//     *
//     * @param default the color to return when the receiver is null
//     * @return the receiver if non-null, otherwise [default]
//     */
//    fun Color?.orDefault(default: Color = Color.Unspecified): Color =
//        this ?: default

    /**
     * Returns a copy of this [Color] with its alpha multiplied by [multiplier].
     *
     * The RGB components remain unchanged. The resulting alpha is computed as:
     * `currentAlpha * multiplier`.
     *
     * This can be used to uniformly increase or decrease transparency while
     * preserving the original opacity proportion.
     *
     * @param multiplier factor applied to the current alpha value
     * @return a copy of this color with the adjusted alpha
     */
    fun Color.alphaMultiplier(multiplier: Float): Color =
        copy(alpha = alpha * multiplier)

    /**
     * Returns this [Color], reducing its alpha by half when [enabled] is false.
     *
     * If [enabled] is true, the color is returned unchanged.
     * If false, the resulting color keeps the same RGB components and
     * multiplies the current alpha by `0.5f`.
     *
     * @param enabled whether the color should remain fully effective
     * @return this color, or a version with its alpha halved when disabled
     */
    fun Color.semiTransparentIfDisabled(enabled: Boolean): Color =
        if (enabled) this else alphaMultiplier(0.5f)


    fun randomColor(
        minLuminance: Float = 0f,
        maxLuminance: Float = 1f,
        alpha: Boolean = false
    ): Color {
        val hue = Random.nextFloat() * 360f
        val saturation = 1f
        val value = Random.nextFloat() * (maxLuminance - minLuminance) + minLuminance

        // Convert HSV to RGB
        val c = value * saturation
        val x = c * (1 - abs((hue / 60f) % 2 - 1))
        val m = value - c

        val (r1, g1, b1) = when (hue) {
            in 0f..60f -> Triple(c, x, 0f)
            in 60f..120f -> Triple(x, c, 0f)
            in 120f..180f -> Triple(0f, c, x)
            in 180f..240f -> Triple(0f, x, c)
            in 240f..300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = r1 + m
        val g = g1 + m
        val b = b1 + m

        return Color(r, g, b, if (alpha) Random.nextFloat() else 1f)
    }
}