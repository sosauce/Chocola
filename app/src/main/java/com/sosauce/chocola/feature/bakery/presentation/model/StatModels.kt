package com.sosauce.chocola.feature.bakery.presentation.model

import android.net.Uri
import androidx.compose.runtime.Stable

@Stable
data class AllTimeStatsUi(
    val totalListens: Int,
    val totalListenedMillis: Long,
    val topTrack: TrackStatUi?,
    val topArtist: ArtistStatUi?
)

data class StreakUi(
    val current: Int = 0,
    val best: Int = 0
)

@Stable
data class TrackStatUi(
    val mediaId: String,
    val title: String,
    val artist: String,
    val album: String,
    val artworkUri: Uri?,
    val listenCount: Int
)

data class ArtistStatUi(
    val name: String,
    val listenCount: Int
)

data class AlbumStatUi(
    val name: String,
    val artist: String,
    val artworkUri: String?,
    val listenCount: Int
)

data class TopDayUi(
    val epochDay: Long,
    val label: String,
    val listenCount: Int,
    val listenedMillis: Long
)

data class FunFactUi(
    val title: String,
    val value: String
)
