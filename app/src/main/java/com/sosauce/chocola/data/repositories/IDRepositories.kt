package com.sosauce.chocola.data.repositories

import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

// Helper class to get Ids from MediaStore
class IDRepositories(
    private val context: Context
) {

    fun getAllAlbumIds(): Map<String, Long> {
        val albumMap = mutableMapOf<String, Long>()
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM
        )

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: continue
                albumMap[name] = id
            }
        }
        return albumMap
    }

    fun getAlbumId(album: String): Long {
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID
        )
        val selection = "${MediaStore.Audio.Albums.ALBUM} = ?"
        val args = arrayOf(album)

        context.contentResolver.query(uri, projection, selection, args, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)

            if (cursor.moveToFirst()) {
                return cursor.getLong(idColumn)
            }
        }
        return Random.nextLong()
    }


}