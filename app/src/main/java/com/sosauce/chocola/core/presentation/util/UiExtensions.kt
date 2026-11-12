package com.sosauce.chocola.core.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.media3.common.Player
import com.materialkolor.PaletteStyle
import com.sosauce.chocola.core.designsystem.CutePaletteStyle
import com.sosauce.chocola.core.designsystem.LyricsAlignment
import java.util.Locale
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

val Context.appVersion
    get() = packageManager.getPackageInfo(packageName, 0).versionName

fun Context.hasMusicPermission(): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun Modifier.selfAlignHorizontally(align: Alignment.Horizontal = Alignment.CenterHorizontally): Modifier {
    return then(
        Modifier
            .fillMaxWidth()
            .wrapContentWidth(align)
    )
}

fun Player.playRandom() {

    if (mediaItemCount == 0) return

    val randomIndex = Random.nextInt(mediaItemCount)
    seekTo(randomIndex, 0)
    play()
    shuffleModeEnabled = true
}

fun Player.playOrPause() {
    if (isPlaying) pause() else play()
}

fun Player.pauseWithFadeOut(durationMs: Long = 1000, steps: Int = 10) {
    val handler = Handler(Looper.getMainLooper())
    val interval = durationMs / steps
    val volumeStep = 1.0f / steps
    var currentVolume = 1.0f

    val fadeRunnable = object : Runnable {
        override fun run() {
            currentVolume -= volumeStep
            if (currentVolume <= 0f) {
                volume = 0f
                pause()
                volume = 1.0f
            } else {
                volume = currentVolume
                handler.postDelayed(this, interval)
            }
        }
    }
    handler.post(fadeRunnable)
}

fun Player.changeRepeatMode() {

    val repeatMode = when (repeatMode) {
        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
        else -> Player.REPEAT_MODE_OFF
    }
    this.repeatMode = repeatMode
}

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember { FocusRequester() }
}


val barsContentTransform = ContentTransform(
    targetContentEnter = slideInVertically(
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) { it } + fadeIn(),
    initialContentExit = slideOutVertically(
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) { it } + fadeOut(),
    sizeTransform = SizeTransform(clip = false) // prevents the content from getting clipped during bounce
)


fun String.toLyricsAlignment(): TextAlign {
    return when (this) {
        LyricsAlignment.START -> TextAlign.Start
        LyricsAlignment.CENTERED -> TextAlign.Center
        LyricsAlignment.END -> TextAlign.End
        else -> TextAlign.Start
    }
}

fun String.toPaletteStyle(): PaletteStyle {
    return when (this) {
        CutePaletteStyle.EXPRESSIVE -> PaletteStyle.Expressive
        CutePaletteStyle.FIDELITY -> PaletteStyle.Fidelity
        CutePaletteStyle.TONAL_SPOT -> PaletteStyle.TonalSpot
        CutePaletteStyle.NEUTRAL -> PaletteStyle.Neutral
        CutePaletteStyle.VIBRANT -> PaletteStyle.Vibrant
        CutePaletteStyle.MONOCHROME -> PaletteStyle.Monochrome
        CutePaletteStyle.FRUIT_SALAD -> PaletteStyle.FruitSalad
        else -> throw IllegalArgumentException("Not a valid palette!")
    }
}


fun Int.toLyricDuration(): String {
    val duration = this.milliseconds
    return duration.toComponents { _, minutes, seconds, nanoseconds ->
        val millis = nanoseconds / 1_000_000
        String.format(Locale.getDefault(), "%d:%02d.%03d", minutes, seconds, millis)
    }
}

fun Long.millisToDuration(): String {

    if (this == 0L) return "No time!"

    val underAMinute = this < 60_000

    if (underAMinute) {
        return String.format(Locale.getDefault(), "%ds", this / 1000)
    } else {
        val totalMinutes = this / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%dh%02d", hours, minutes)
        } else {
            String.format(Locale.getDefault(), "%dm", minutes)
        }
    }

}
