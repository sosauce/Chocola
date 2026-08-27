package com.sosauce.chocola.data.models

import androidx.core.net.toUri
import kotlinx.serialization.Serializable

@Serializable
data class CuteTrack(
    val mediaId: String = "",
    private val uriString: String = "",
    private val artUriString: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val trackNumber: Int = 0,
    val folder: String = "",
    val path: String = "",
    val isSaf: Boolean = false
) {
    val uri
        get() = uriString.toUri()

    val artUri
        get() = artUriString.toUri()
}



