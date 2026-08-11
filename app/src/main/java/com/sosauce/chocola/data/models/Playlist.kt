package com.sosauce.chocola.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emoji: String = "",
    val name: String = "",
    val musics: Set<String> = emptySet(), // List of songs ID aka mediaId
    val color: Int = -1,
    val tags: Set<String> = emptySet()
)
