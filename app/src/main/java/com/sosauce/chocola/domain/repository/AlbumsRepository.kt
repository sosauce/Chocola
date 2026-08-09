@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.domain.repository

import android.content.Context
import android.provider.MediaStore
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.CuteTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import kotlin.random.Random

class AlbumsRepository(
    private val context: Context,
    private val abstractTracksScanner: AbstractTracksScanner
) {


//    fun fetchLatestAlbumTracks(albumName: String) = abstractTracksScanner.fetchLatestTracks(
//        extraSelection = "${MediaStore.Audio.Media.ALBUM} = ?",
//        extraSelectionArgs = arrayOf(albumName)
//    )

    fun fetchLatestAlbumTracks(albumName: String) = emptyList<CuteTrack>()

    fun fetchAlbums(
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): List<Album> {
        // prevent duplicate albums
        val albums = hashMapOf<String, Album>()


        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )


        context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER,
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val nbTracksColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (cursor.moveToNext()) {

                val nbSongs = cursor.getInt(nbTracksColumn)
                if (nbSongs <= 0) continue

                val album = cursor.getString(albumColumn)
                if (albums.containsKey(album)) continue

                val id = cursor.getLong(idColumn)
                val artist = cursor.getString(artistColumn)
                val albumInfo = Album(id, album, artist)
                albums[album] = albumInfo
            }

        }

        return ArrayList(albums.values)
    }


    suspend fun fetchAlbumDetails(albumName: String): Album = withContext(Dispatchers.IO) {
        context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST
            ),
            "${MediaStore.Audio.Albums.ALBUM} = ?",
            arrayOf(albumName),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)

            while (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val artist = cursor.getString(artistColumn)

                return@withContext Album(
                    id = id,
                    name = name,
                    artist = artist
                )
            }
        }

        return@withContext Album(Random.nextLong())
    }

}