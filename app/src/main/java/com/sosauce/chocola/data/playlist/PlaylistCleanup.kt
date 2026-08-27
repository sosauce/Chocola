package com.sosauce.chocola.data.playlist

import android.content.Context
import android.provider.MediaStore
import com.sosauce.chocola.utils.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class PlaylistCleanup(
    private val context: Context,
    private val playlistDao: PlaylistDao
) {

    private val mediaStoreObserver =
        context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)

    /**
     * Removes deleted mediaIds from playlists
     */
    suspend fun startCleanup() = withContext(Dispatchers.IO) {
        combine(
            mediaStoreObserver,
            playlistDao.getPlaylists()
        ) { _, playlists -> playlists }.collectLatest { playlists ->

            val existingMediaIds = mutableSetOf<String>()
            val projection = arrayOf(MediaStore.Audio.Media._ID)

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                while (cursor.moveToNext()) {
                    existingMediaIds.add(cursor.getString(idColumn))
                }
            }

            playlists.forEach { playlist ->
                val cleanedMusics = playlist.musics.filterTo(mutableSetOf()) { id ->
                    existingMediaIds.contains(id)
                }
                val newPlaylist = playlist.copy(
                    musics = cleanedMusics
                )

                // means there's IDs to delete
                if (cleanedMusics.size != playlist.musics.size) {
                    playlistDao.upsertPlaylist(newPlaylist)
                }
            }
        }
    }
}