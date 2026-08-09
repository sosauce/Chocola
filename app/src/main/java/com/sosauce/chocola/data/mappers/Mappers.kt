package com.sosauce.chocola.data.mappers

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sosauce.chocola.data.models.CuteTrack

fun CuteTrack.toMediaItem(): MediaItem {

    val metadata = MediaMetadata.Builder()
        .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
        .setIsPlayable(true)
        .setIsBrowsable(false)
        .setTitle(title)
        .setArtist(artist)
        .setAlbumTitle(album)
        .setArtworkUri(artUri)
        .build()

    return MediaItem.Builder()
        .setMediaId(mediaId)
        .setUri(uri)
        .setMediaMetadata(metadata)
        .build()
}