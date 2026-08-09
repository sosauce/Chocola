@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.domain.repository

import android.content.Context
import android.provider.MediaStore
import androidx.compose.ui.util.fastDistinctBy
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.data.models.CuteTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ArtistsRepository(
    private val context: Context,
    private val abstractTracksScanner: AbstractTracksScanner
) {

    fun fetchLatestArtistTracks(artistName: String) = emptyList<CuteTrack>()
//    fun fetchLatestArtistTracks(artistName: String) = abstractTracksScanner.fetchLatestTracks(
//        extraSelection = "${MediaStore.Audio.Media.ARTIST} = ?",
//        extraSelectionArgs = arrayOf(artistName)
//    )


    suspend fun fetchArtists(): List<Artist> = withContext(Dispatchers.IO) {

        val artists = mutableListOf<Artist>()
        //val allAlbumsIds = getAllArtistAlbumIds()


        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
        )

        context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val numberAlbumsColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val numberTracksColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

            while (cursor.moveToNext()) {
                val numberTracks = cursor.getInt(numberTracksColumn)
                val numberAlbums = cursor.getInt(numberAlbumsColumn)

                if (numberTracks <= 0 && numberAlbums <= 0) continue

                val id = cursor.getLong(idColumn)
                val artist = cursor.getString(artistColumn)

                val artistInfo = Artist(
                    id = id,
                    name = artist,
                    //albumId = allAlbumsIds[id] ?: 0,
                    numberTracks = numberTracks,
                    numberAlbums = numberAlbums

                )
                artists.add(artistInfo)
            }
        }

        return@withContext artists
    }

    fun fetchArtistDetails(artistName: String): Artist {


        context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
            ),
            "${MediaStore.Audio.Artists.ARTIST} = ?",
            arrayOf(artistName),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val nbAlbumsColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val nbTracksColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val nbAlbums = cursor.getInt(nbAlbumsColumn)
                val nbTracks = cursor.getInt(nbTracksColumn)

                return Artist(
                    id = id,
                    name = name,
                    albumId = getArtistAlbumId(id),
                    numberAlbums = nbAlbums,
                    numberTracks = nbTracks
                )
            }
        }

        return Artist(Random.nextLong())
    }

    private fun getArtistAlbumId(artistId: Long): Long {
        val uri = MediaStore.Audio.Artists.Albums.getContentUri("external", artistId)

        context.contentResolver.query(
            uri,
            arrayOf(
                MediaStore.Audio.Artists.Albums.ALBUM_ID
            ),
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM_ID)

            if (cursor.moveToFirst()) {
                return cursor.getLong(idColumn)
            }
        }

        return 0
    }

    private fun getAllArtistAlbumIds(): HashMap<Long, Long> {
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val artistToAlbumId = hashMapOf<Long, Long>()

        context.contentResolver.query(
            uri,
            arrayOf(
                MediaStore.Audio.Albums.ALBUM_ID,
                MediaStore.Audio.Albums.ARTIST_ID
            ),
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST_ID)

            while (cursor.moveToNext()) {
                val artist = cursor.getLong(artistColumn)
                val albumId = cursor.getLong(idColumn)

                if (!artistToAlbumId.containsKey(artist)) {
                    artistToAlbumId[artist] = albumId
                }
            }
        }

        return artistToAlbumId
    }

}