package com.sosauce.chocola.core.domain.model

/**
 * @param albumId Used to get artwork
 */
data class Artist(
    val id: Long = 0,
    val name: String = "",
    val albumId: Long = 0,
    val tracks: List<CuteTrack> = emptyList(),
    val numberAlbums: Int = 0,
    val isFavorite: Boolean = false
)
