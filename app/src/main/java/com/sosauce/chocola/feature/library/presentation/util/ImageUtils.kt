package com.sosauce.chocola.feature.library.presentation.util

import android.content.ContentUris
import android.net.Uri
import androidx.core.net.toUri
import com.sosauce.chocola.core.domain.model.CuteTrack
import java.io.File

object ImageUtils {

    fun getAlbumArt(albumId: Long): Uri {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }

    fun getAlbumArt(tracks: List<CuteTrack>, fileName: String?): Uri {
        val lastTrack = tracks.last()
        if (fileName.isNullOrBlank()) return getAlbumArt(lastTrack.albumId)

        val folder = lastTrack.folder
        val sameFolder = tracks.all { !it.isSaf && it.folder == folder }
        if (!sameFolder) return getAlbumArt(lastTrack.albumId)

        val dir = File(folder)
        return if (dir.list()?.contains(fileName) == true) {
            Uri.fromFile(File(dir, fileName))
        } else {
            getAlbumArt(lastTrack.albumId)
        }
    }
}
