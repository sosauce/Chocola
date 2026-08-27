package com.sosauce.chocola.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

object ImageUtils {

    fun getAlbumArt(albumId: Long): Any? {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return try {
            ContentUris.withAppendedId(sArtworkUri, albumId)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    // Kinda ugly fix to always load the new art for MaterialArt, but I guess it's better than loading the viewmodel in the app's theme
    suspend fun loadNewArt(
        context: Context,
        art: Uri?,
        onImageLoad: (ImageBitmap?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val request = ImageRequest.Builder(context)
            .data(art)
            .allowHardware(false)
            .build()
        val result = context.imageLoader.execute(request)


        onImageLoad(result.image?.toBitmap()?.asImageBitmap())

    }
}