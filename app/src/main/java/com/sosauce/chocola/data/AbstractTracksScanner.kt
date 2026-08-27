@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.sosauce.chocola.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.ui.util.fastFilter
import androidx.core.net.toUri
import com.sosauce.chocola.data.datastore.TracksSettings
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.repositories.SafManager
import com.sosauce.chocola.utils.TrackSort
import com.sosauce.chocola.utils.combine
import com.sosauce.chocola.utils.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

/**
 * An abstract way of containing function related to scanning tracks, so any part of the app that needs to fetch tracks can use the same scanning rules
 */
class AbstractTracksScanner(
    private val context: Context,
    private val userPreferences: UserPreferences,
    private val ioCoroutineScope: CoroutineScope,
    private val safManager: SafManager
) {

    /**
     * Single source of truth to get all filtered tracks
     */
    val latestTracks = fetchLatestTracks().stateIn(
        ioCoroutineScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private fun fetchLatestTracks(): Flow<List<CuteTrack>> {
        val mediaStoreFlow =
            context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        val minTrackDurationFlow = userPreferences.getMinTrackDuration()
        val hiddenTracksFlow = userPreferences.getHiddenTracks()
        val whitelistedFoldersFlow = userPreferences.getWhitelistedFolders()
        val tracksSettingsFlow = userPreferences.tracksSettings()
            .debounce(250.milliseconds) // Debounce settings if user changes them quickly (prolly unnecessary)

        return combine(
            mediaStoreFlow,
            minTrackDurationFlow,
            hiddenTracksFlow,
            whitelistedFoldersFlow,
            tracksSettingsFlow,
            safManager.fetchLatestSafTracks()
        ) { _, minTrackDuration, hidden, whitelistedFolders, tracksSettings, saf ->

            val rawTracks = fetchTracks(
                tracksSettings = tracksSettings,
                minTrackDuration = minTrackDuration
            )

            rawTracks.fastFilter { track ->
                val isNotHidden = !hidden.contains(track.mediaId)
                val isWhitelisted = whitelistedFolders.contains(track.folder)

                isNotHidden && isWhitelisted
            } + saf
        }.flowOn(Dispatchers.IO)
    }

    private fun fetchTracks(
        tracksSettings: TracksSettings,
        minTrackDuration: Int
    ): List<CuteTrack> {
        val musics = mutableListOf<CuteTrack>()

        val selection = buildString {
            append("${MediaStore.Audio.Media.DURATION} >= ? AND ")
            append("${MediaStore.Audio.Media.IS_MUSIC} != ? ")
        }

        val selectionArgs = buildList {
            add("${minTrackDuration * 1000}")
            add("0")
        }.toTypedArray()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK
        )


        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            tracksSettingsToMediaStore(tracksSettings)
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val trackNbColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)

            while (cursor.moveToNext()) {


                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val filePath = cursor.getString(folderColumn)
                val folder = filePath.substringBeforeLast('/')
                val trackNumber = cursor.getInt(trackNbColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                ).toString()

                val mediaId = id.toString()

                musics.add(
                    CuteTrack(
                        mediaId = mediaId,
                        uriString = uri,
                        artUriString = "$uri/albumart",
                        title = title,
                        artist = artist,
                        album = album,
                        trackNumber = trackNumber,
                        folder = folder,
                        path = filePath,
                        isSaf = false
                    )
                )
            }

        }
        return musics
    }

    private fun tracksSettingsToMediaStore(tracksSettings: TracksSettings): String {
        val data = when (tracksSettings.sort) {
            TrackSort.TITLE -> MediaStore.Audio.Media.TITLE
            TrackSort.ALBUM -> MediaStore.Audio.Media.ALBUM
            TrackSort.ARTIST -> MediaStore.Audio.Media.ARTIST
            TrackSort.YEAR -> MediaStore.Audio.Media.YEAR
            TrackSort.DATE_MODIFIED -> MediaStore.Audio.Media.DATE_MODIFIED
            TrackSort.AS_ADDED -> ""
        }

        val noCase = when (tracksSettings.sort) {
            TrackSort.YEAR, TrackSort.DATE_MODIFIED -> ""
            else -> "COLLATE NOCASE"
        }

        val asc = if (tracksSettings.ascending) "ASC" else "DESC"

        return "$data $noCase $asc"
    }

    fun forceScanDevice() {
        // https://stackoverflow.com/a/77279718
        context.contentResolver.call(
            SCAN_CONTENT_URI,
            SCAN_VOLUME,
            SCAN_STORAGE,
            null
        )
    }

    companion object {
        private const val SCAN_VOLUME = "scan_volume"
        private val SCAN_CONTENT_URI = "content://media".toUri()
        private const val SCAN_STORAGE = "external_primary"
    }

}