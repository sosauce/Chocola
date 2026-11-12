package com.sosauce.chocola.feature.library.presentation.util

import android.content.ContentUris
import android.net.Uri
import androidx.core.net.toUri

object ImageUtils {

    fun getAlbumArt(albumId: Long): Uri {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }
}
