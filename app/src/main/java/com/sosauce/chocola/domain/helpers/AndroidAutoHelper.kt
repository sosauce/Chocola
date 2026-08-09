package com.sosauce.chocola.domain.helpers

import androidx.compose.ui.util.fastMap
import androidx.media3.common.MediaItem
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.mappers.toMediaItem

class AndroidAutoHelper(
    private val abstractTracksScanner: AbstractTracksScanner
) {

    fun getChildrenMediaItems(
        limit: Int,
        offset: Int
    ): List<MediaItem> {
        val allTracks = abstractTracksScanner.latestTracks.value

        return if (limit > 0 && offset >= 0) {
            allTracks
                .drop(offset)
                .take(limit)
                .fastMap { it.toMediaItem() }
        } else {
            allTracks.fastMap { it.toMediaItem() }
        }
    }

}