@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.fastFilter
import androidx.media3.common.Player
import com.kyant.taglib.PropertyMap
import com.materialkolor.PaletteStyle
import com.sosauce.chocola.data.datastore.SearchSettings
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.models.Playlist
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

fun ByteArray.getUriFromByteArray(context: Context): Uri {
    val albumArtFile = File(context.cacheDir, "albumArt_${Uuid.random()}.jpg")

    return try {
        FileOutputStream(albumArtFile).use { os ->
            os.write(this)
        }
        Uri.fromFile(albumArtFile)
    } catch (e: Exception) {
        e.printStackTrace()
        Uri.EMPTY
    }
}

fun PropertyMap.toModifiableMap(separator: String = ", "): MutableMap<String, String?> {
    return mutableMapOf(
        "TITLE" to this["TITLE"]?.getOrNull(0),
        "ARTIST" to this["ARTIST"]?.joinToString(separator),
        "ALBUM" to this["ALBUM"]?.getOrNull(0),
        "TRACKNUMBER" to this["TRACKNUMBER"]?.getOrNull(0),
        "DISCNUMBER" to this["DISCNUMBER"]?.getOrNull(0),
        "DATE" to this["DATE"]?.getOrNull(0),
        "GENRE" to this["GENRE"]?.joinToString(separator),
        "LYRICS" to this["LYRICS"]?.getOrNull(0),
        "DATE" to this["DATE"]?.getOrNull(0),
    )
}

fun String?.formatForField(separator: String = ","): Array<String> {
    return this?.split(separator)?.map { it.trim() }?.toTypedArray() ?: arrayOf(this ?: "")
}

@Stable
data class AudioFileMetadata(
    val title: String?,
    val artist: String?,
    val album: String?,
    val trackNumber: String?,
    val discNumber: String?,
    val date: String?,
    val genre: String?,
    val lyrics: String?
)

fun Map<String, String?>.toAudioFileMetadata(): AudioFileMetadata {
    return AudioFileMetadata(
        title = this["TITLE"],
        artist = this["ARTIST"],
        album = this["ALBUM"],
        trackNumber = this["TRACKNUMBER"],
        discNumber = this["DISCNUMBER"],
        date = this["DATE"],
        genre = this["GENRE"],
        lyrics = this["LYRICS"]
    )
}

fun AudioFileMetadata.toPropertyMap(): PropertyMap {
    return hashMapOf(
        "TITLE" to arrayOf(title ?: ""),
        "ARTIST" to artist.formatForField(),
        "ALBUM" to arrayOf(album ?: ""),
        "TRACKNUMBER" to arrayOf(trackNumber ?: ""),
        "DISCNUMBER" to arrayOf(discNumber ?: ""),
        "DATE" to arrayOf(date ?: ""),
        "GENRE" to genre.formatForField(),
        "LYRICS" to arrayOf(lyrics ?: "")
    )
}

inline fun <E> List<E>.copyMutate(block: MutableList<E>.() -> Unit): List<E> {
    return toMutableList().apply(block)
}

inline fun <E> Set<E>.copyMutate(block: MutableSet<E>.() -> Unit): Set<E> {
    return toMutableSet().apply(block)
}


inline fun <T> List<T>.thenIf(
    condition: Boolean,
    crossinline block: List<T>.() -> List<T>
): List<T> = if (condition) block() else this


fun String.regex(matchCase: Boolean): Regex {
    return if (matchCase) {
        toRegex()
    } else {
        toRegex(RegexOption.IGNORE_CASE)
    }
}

fun List<CuteTrack>.search(
    query: String,
    searchSettings: SearchSettings,
): List<CuteTrack> {
    val regexPattern = query.regex(searchSettings.matchCase)
    return fastFilter { track ->
        if (searchSettings.regex) {
            regexPattern.containsMatchIn(track.title)
        } else {
            track.title.contains(query, !searchSettings.matchCase)
        }
    }
}


fun List<Album>.ordered(
    sort: AlbumSort,
    regex: Boolean,
    matchCase: Boolean,
    ascending: Boolean,
    query: String
): List<Album> {
    val regexPattern = query.regex(matchCase)

    val filtered = this.fastFilter { track ->
        if (regex) {
            regexPattern.containsMatchIn(track.name)
        } else {
            track.name.contains(query, !matchCase)
        }
    }

    return filtered
        .sortedBy {
            when (sort) {
                AlbumSort.NAME -> it.name
                AlbumSort.ARTIST -> it.artist
            }
        }.thenIf(!ascending) { asReversed() }
}

fun List<Artist>.ordered(
    sort: ArtistSort,
    regex: Boolean,
    matchCase: Boolean,
    ascending: Boolean,
    query: String
): List<Artist> {
    val regexPattern = query.regex(matchCase)

    val filtered = this.fastFilter { track ->
        if (regex) {
            regexPattern.containsMatchIn(track.name)
        } else {
            track.name.contains(query, !matchCase)
        }
    }

    return filtered
        .sortedBy {
            when (sort) {
                ArtistSort.NAME -> it.name
                ArtistSort.NB_ALBUMS -> it.numberAlbums.toString()
                ArtistSort.NB_TRACKS -> it.tracks.size.toString()
            }
        }.thenIf(!ascending) { asReversed() }
}

fun List<Playlist>.ordered(
    sort: PlaylistSort,
    regex: Boolean,
    matchCase: Boolean,
    ascending: Boolean,
    query: String
): List<Playlist> {
    val regexPattern = query.regex(matchCase)

    val filtered = this.fastFilter { track ->
        if (regex) {
            regexPattern.containsMatchIn(track.name)
        } else {
            track.name.contains(query, !matchCase)
        }
    }

    return filtered
        .sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) {
                when (sort) {
                    PlaylistSort.NAME -> it.name
                    PlaylistSort.NB_TRACKS -> it.musics.size.toString()
                    PlaylistSort.TAGS -> it.tags.size.toString()
                    PlaylistSort.COLOR -> it.color.toString()
                }
            }
        ).thenIf(!ascending) { asReversed() }
}

fun <E> MutableSet<E>.addOrRemove(element: E) {
    if (!add(element)) {
        remove(element)
    }
}


fun ContentResolver.observe(uri: Uri) = callbackFlow {
    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {

            trySend(selfChange)
        }
    }
    registerContentObserver(uri, true, observer)
    trySend(false)
    awaitClose {
        unregisterContentObserver(observer)
    }
}


@Composable
fun rememberInteractionSource(): MutableInteractionSource {
    return remember { MutableInteractionSource() }
}

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember { FocusRequester() }
}


fun <T> bouncySpec() = spring<T>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)


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


fun List<CuteTrack>.orderAlbumTrackNumber(): List<CuteTrack> {
    return sortedWith(
        compareBy(
            { it.trackNumber == 0 },
            { it.trackNumber }
        )
    )
}
