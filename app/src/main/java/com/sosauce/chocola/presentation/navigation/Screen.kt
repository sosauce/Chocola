package com.sosauce.chocola.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Main : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object Albums : Screen()

    @Serializable
    data object Artists : Screen()

    @Serializable
    data object Playlists : Screen()

    @Serializable
    data object Setup : Screen()

    @Serializable
    data object Queue : Screen()

    @Serializable
    data object Lyrics : Screen()

    @Serializable
    data class LyricsEditor(
        val trackPath: String
    ) : Screen()
    @Serializable
    data class AlbumsDetails(
        val name: String
    ) : Screen()

    @Serializable
    data class ArtistsDetails(
        val name: String
    ) : Screen()

    @Serializable
    data class PlaylistDetails(
        val id: Int
    ) : Screen()

    @Serializable
    data class MetadataEditor(
        val trackPath: String,
        val trackUri: String
    ) : Screen()

    @Serializable
    data class Transformer(
        val trackUri: String
    ) : Screen()

    companion object {
        fun toScreen(screen: String) : Screen {
            return when(screen) {
                Main.toString() -> Main
                Albums.toString() -> Albums
                Artists.toString() -> Artists
                else -> Main
            }
        }
    }
}