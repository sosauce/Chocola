@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.core.data.library

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.kyant.taglib.TagLib
import com.sosauce.chocola.core.data.content.getUriFromByteArray
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.domain.model.CuteTrack
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
            tracks.mapNotNull { uri ->
                uriToTrack(uri.toUri())
            }
        }
        .flowOn(Dispatchers.IO)

    private fun uriToTrack(
        uri: Uri
    ): CuteTrack? {
        return context.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
            val metadata = TagLib.getMetadata(fd.dup().detachFd())

            val title = metadata?.propertyMap?.get("TITLE")?.getOrNull(0) ?: "<unknown>"
            val artist = metadata?.propertyMap?.get("ARTIST")?.joinToString(", ") ?: "<unknown>"
            val album = metadata?.propertyMap?.get("ALBUM")?.getOrNull(0) ?: "<unknown>"
            val artUri =
                TagLib.getFrontCover(fd.dup().detachFd())?.data?.getUriFromByteArray(context)


            CuteTrack(
                mediaId = uri.hashCode().toString(),
                uriString = uri.toString(),
                artUri = artUri?.toString(),
                title = title,
                artist = artist,
                album = album,
                folder = "S.A.F",
                path = uri.path ?: "Unknown path",
                isSaf = true
            )
        }
    }
}