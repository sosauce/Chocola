package com.sosauce.chocola.feature.playlists.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.fastFilter
import com.sosauce.chocola.core.data.local.Playlist
import com.sosauce.chocola.core.domain.model.CuteTrack

@Immutable
data class PlaylistUi(
    val id: Int = 0,
    val emoji: String? = null,
    val name: String = "",
    val tracks: List<CuteTrack> = emptyList(),
    val color: Color? = null,
    val tags: Set<String> = emptySet(),
    val isFavorite: Boolean = false
)

fun Playlist.toUi(allTracks: List<CuteTrack>): PlaylistUi = PlaylistUi(
    id = id,
    emoji = emoji.ifEmpty { null },
    name = name,
    tracks = allTracks.fastFilter { it.mediaId in musics },
    color = if (color == -1) null else Color(color),
    tags = tags,
    isFavorite = isFavorite
)

fun PlaylistUi.toEntity(): Playlist = Playlist(
    id = id,
    emoji = emoji.orEmpty(),
    name = name,
    musics = tracks.mapTo(mutableSetOf()) { it.mediaId },
    color = color?.toArgb() ?: -1,
    tags = tags,
    isFavorite = isFavorite
)
