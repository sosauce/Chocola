package com.sosauce.chocola.core.domain.model

import androidx.compose.runtime.Stable

@Stable
data class Artist(
    val id: Long = 0,
    val name: String = "",
    val tracks: List<CuteTrack> = emptyList(),
    val numberAlbums: Int = 0,
    val isFavorite: Boolean = false
)
