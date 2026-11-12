@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.sosauce.chocola.core.data.library

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import androidx.core.net.toUri
import com.sosauce.chocola.core.data.content.observe
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.domain.library.sortByAlbum
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.model.TrackSort
import com.sosauce.chocola.core.domain.model.TracksSettings
import com.sosauce.chocola.core.domain.util.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
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

    // Single scan pipeline shared by every consumer: all whitelisted tracks, hidden ones included.
    private val whitelistedTracks = fetchWhitelistedTracks().stateIn(
        ioCoroutineScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val defaultTracks = combine(
        whitelistedTracks,
        userPreferences.getHiddenTracks()
    ) { tracks, hidden ->
        tracks.fastFilter { !hidden.contains(it.mediaId) }
    }.flowOn(Dispatchers.IO).stateIn(
        ioCoroutineScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun latestTracks() = defaultTracks

    private fun fetchWhitelistedTracks(): Flow<List<CuteTrack>> {
        val mediaStoreFlow =
            context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        val minTrackDurationFlow = userPreferences.getMinTrackDuration()
        val whitelistedFoldersFlow = userPreferences.getWhitelistedFolders()
        val tracksSettingsFlow = userPreferences.tracksSettings()
            .debounce(250.milliseconds) // Debounce settings if user changes them quickly (prolly unnecessary)

        return combine(
            mediaStoreFlow,
            minTrackDurationFlow,
            whitelistedFoldersFlow,
            tracksSettingsFlow,
            safManager.fetchLatestSafTracks()
        ) { _, minTrackDuration, whitelistedFolders, tracksSettings, saf ->

            val rawTracks = fetchTracks(
                tracksSettings = tracksSettings,
                minTrackDuration = minTrackDuration
            )

            val filtered = rawTracks.fastFilter { track ->
                whitelistedFolders.contains(track.folder)
            } + saf
            if (tracksSettings.sort == TrackSort.ALBUM && tracksSettings.ascending) filtered.sortByAlbum()
            else if (tracksSettings.sort == TrackSort.ALBUM) filtered.sortByAlbum().asReversed()
            else filtered
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Fetches all hidden tracks, regardless of user settings, if a track is hidden, fetch it no questions asked
     */
    fun fetchHiddenTracks(): Flow<List<CuteTrack>> {
        return userPreferences.getHiddenTracks().mapLatest { hiddenIds ->
            val musics = mutableListOf<CuteTrack>()

            // SQLite caps bound variables (~999)
            hiddenIds.chunked(MAX_SQL_VARIABLES).fastForEach { chunk ->
                val placeholders = chunk.joinToString(",") { "?" }
                val selection =
                    "${MediaStore.Audio.Media._ID} IN ($placeholders) AND " + "${MediaStore.Audio.Media.IS_MUSIC} != ?"


                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    TRACKS_PROJECTION,
                    selection,
                    chunk.toTypedArray() + arrayOf("0"),
                    "${MediaStore.Audio.Media.TITLE} ASC"
                )?.use { cursor ->
                    cursor.readCuteTracks(musics)
                }

            }
            musics
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

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            TRACKS_PROJECTION,
            selection,
            selectionArgs,
            tracksSettingsToMediaStore(tracksSettings)
        )?.use { cursor ->
            cursor.readCuteTracks(musics)
        }
        return musics
    }


    private fun Cursor.readCuteTracks(into: MutableList<CuteTrack>) {
        val idColumn = getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val folderColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val trackNbColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
        val albumIdColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val artistIdColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
        val durationColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (moveToNext()) {


            val id = getLong(idColumn)
            val title = getString(titleColumn)
            val artist = getString(artistColumn)
            val album = getString(albumColumn)
            val filePath = getString(folderColumn)
            val folder = filePath.substringBeforeLast('/')
            val trackNumber = getInt(trackNbColumn)
            val albumId = getLong(albumIdColumn)
            val artistId = getLong(artistIdColumn)
            val durationMillis = getLong(durationColumn)
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )


            val mediaId = id.toString()

            into.add(
                CuteTrack(
                    mediaId = mediaId,
                    uriString = uri.toString(),
                    artUri = "$uri/albumart",
                    title = title,
                    artist = artist,
                    album = album,
                    trackNumber = trackNumber,
                    folder = folder,
                    path = filePath,
                    isSaf = false,
                    albumId = albumId,
                    artistId = artistId,
                    durationMillis = durationMillis
                )
            )
        }
    }

    private fun tracksSettingsToMediaStore(tracksSettings: TracksSettings): String? {
        val column = when (tracksSettings.sort) {
            TrackSort.TITLE -> MediaStore.Audio.Media.TITLE
            TrackSort.ALBUM -> MediaStore.Audio.Media.ALBUM
            TrackSort.ARTIST -> MediaStore.Audio.Media.ARTIST
            TrackSort.YEAR -> MediaStore.Audio.Media.YEAR
            TrackSort.DATE_MODIFIED -> MediaStore.Audio.Media.DATE_MODIFIED
            TrackSort.AS_ADDED -> return null
        }
        if (column.isBlank()) return null

        val noCase = when (tracksSettings.sort) {
            TrackSort.YEAR, TrackSort.DATE_MODIFIED -> ""
            else -> "COLLATE NOCASE"
        }

        val direction = if (tracksSettings.ascending) "ASC" else "DESC"

        return "$column $noCase $direction"
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

        private const val MAX_SQL_VARIABLES = 900

        private val TRACKS_PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION
        )
    }

}
