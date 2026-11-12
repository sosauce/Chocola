package com.sosauce.chocola.core.domain.model

import androidx.core.net.toUri
import kotlinx.serialization.Serializable

@Serializable
data class CuteTrack(
    val mediaId: String = "",
    private val uriString: String = "",
    val artUri: String? = null,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val trackNumber: Int = 0,
    val folder: String = "",
    val path: String = "",
    val isSaf: Boolean = false,
    val albumId: Long = -1,
    val isFavorite: Boolean = false,
    val artistId: Long = -1,
    val durationMillis: Long = 0
) {
    val uri
        get() = uriString.toUri()

}

