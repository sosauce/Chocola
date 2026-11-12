package com.sosauce.chocola.core.domain.model

import android.net.Uri
import androidx.compose.runtime.Stable

@Stable
data class Album(
    val id: Long = 0,
    val name: String = "",
    val artist: String = "",
    val art: Uri? = null,
    val tracks: List<CuteTrack> = emptyList(),
    val isFavorite: Boolean = false,
) {
    val durationMillis: Long
        get() = tracks.sumOf { it.durationMillis }
}
