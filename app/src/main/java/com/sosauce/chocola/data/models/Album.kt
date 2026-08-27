package com.sosauce.chocola.data.models

data class Album(
    val id: Long = 0,
    val name: String = "",
    val artist: String = "",
    val tracks: List<CuteTrack> = emptyList()
)
