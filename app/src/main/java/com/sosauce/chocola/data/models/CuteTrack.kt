package com.sosauce.chocola.data.models

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.media3.common.MediaItem
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.core.net.toUri

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



