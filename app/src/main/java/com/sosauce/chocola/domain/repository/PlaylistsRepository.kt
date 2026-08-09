@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.domain.repository

import android.provider.MediaStore
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.models.CuteTrack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepository(
    private val abstractTracksScanner: AbstractTracksScanner
) {
    fun fetchLatestPlaylistTracks(mediaIds: List<String>): Flow<List<CuteTrack>> {
        val selection = "${MediaStore.Audio.Media._ID} IN (${mediaIds.joinToString(",") { "?" }})"
        return flow {  }
//        )        return abstractTracksScanner.fetchLatestTracks(
//            extraSelection = selection,
//            extraSelectionArgs = mediaIds.toTypedArray()
//        )
    }
}
