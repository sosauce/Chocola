@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.data.repositories

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.kyant.taglib.Metadata
import com.kyant.taglib.TagLib
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.utils.getUriFromByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

class SafManager(
    private val context: Context,
    private val userPreferences: UserPreferences
) {


    fun fetchLatestSafTracks(): Flow<List<CuteTrack>> = userPreferences.getSafTracks()
        .mapLatest { tracks ->
            tracks.map { uri ->
                uriToTrack(uri.toUri())
            }
        }
        .flowOn(Dispatchers.IO)


    private fun uriToTrack(uri: Uri): CuteTrack {
        return context.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
            val metadata = TagLib.getMetadata(fd.dup().detachFd())

            val title = metadata?.propertyMap?.get("TITLE")?.getOrNull(0) ?: "<unknown>"
            val artist = metadata?.propertyMap?.get("ARTIST")?.joinToString(", ") ?: "<unknown>"
            val album = metadata?.propertyMap?.get("ALBUM")?.getOrNull(0) ?: "<unknown>"
            val artUri =
                TagLib.getFrontCover(fd.dup().detachFd())?.data?.getUriFromByteArray(context) ?: Uri.EMPTY

            CuteTrack(
                mediaId = uri.hashCode().toString(),
                uriString = uri.toString(),
                artUriString = artUri.toString(),
                title = title,
                artist = artist,
                album = album,
                folder = "-",
                path = uri.path ?: "Unknown path",
                isSaf = true
            )
        } ?: throw IllegalArgumentException("Unable to open file descriptor for uri")
    }

}