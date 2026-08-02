package com.sosauce.chocola.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.sosauce.chocola.data.datastore.PreferencesKeys.ALBUM_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.ARTIST_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.EQUALIZER_BANDS
import com.sosauce.chocola.data.datastore.PreferencesKeys.EQUALIZER_ENABLED
import com.sosauce.chocola.data.datastore.PreferencesKeys.EQUALIZER_PRESETS
import com.sosauce.chocola.data.datastore.PreferencesKeys.HIDDEN_TRACKS
import com.sosauce.chocola.data.datastore.PreferencesKeys.LAST_MUSIC_STATE
import com.sosauce.chocola.data.datastore.PreferencesKeys.MATCH_CASE_FILTER
import com.sosauce.chocola.data.datastore.PreferencesKeys.MIN_TRACK_DURATION
import com.sosauce.chocola.data.datastore.PreferencesKeys.PAUSE_ON_MUTE
import com.sosauce.chocola.data.datastore.PreferencesKeys.PLAYLIST_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.REGEX_FILTER
import com.sosauce.chocola.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_ALBUMS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_ARTISTS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_PLAYLISTS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_TRACKS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.TRACK_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.WHITELISTED_FOLDERS
import com.sosauce.chocola.data.models.EqualizerBand
import com.sosauce.chocola.data.models.EqualizerPreset
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.utils.AlbumSort
import com.sosauce.chocola.utils.ArtistSort
import com.sosauce.chocola.utils.PlaylistSort
import com.sosauce.chocola.utils.TrackSort
import com.sosauce.chocola.utils.copyMutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class UserPreferences(
    private val context: Context
) {

    val getTrackSort = context.dataStore.data.map {
        val sort = it[TRACK_SORT] ?: 0
        TrackSort.entries[sort]
    }

    val getArtistsSort = context.dataStore.data.map {
        val sort = it[ARTIST_SORT] ?: 0
        ArtistSort.entries[sort]
    }

    val getAlbumsSort = context.dataStore.data.map {
        val sort = it[ALBUM_SORT] ?: 0
        AlbumSort.entries[sort]
    }

    val getPlaylistsSort = context.dataStore.data.map {
        val sort = it[PLAYLIST_SORT] ?: 0
        PlaylistSort.entries[sort]
    }

    val getRegexFilter = context.dataStore.data.map {
       it[REGEX_FILTER] ?: false
    }

    val getMatchCaseFilter = context.dataStore.data.map {
        it[MATCH_CASE_FILTER] ?: false
    }

    val sortTracksAscending = context.dataStore.data.map {
        it[SORT_TRACKS_ASCENDING] ?: true
    }
    val sortArtistsAscending = context.dataStore.data.map {
        it[SORT_ARTISTS_ASCENDING] ?: true
    }

    val sortAlbumsAscending = context.dataStore.data.map {
        it[SORT_ALBUMS_ASCENDING] ?: true
    }

    val sortPlaylistsAscending = context.dataStore.data.map {
        it[SORT_PLAYLISTS_ASCENDING] ?: true
    }

    fun getPauseOnMute() = context.dataStore.data.map { it[PAUSE_ON_MUTE] ?: false }

    fun getHiddenTracks() = context.dataStore.data.map {
        it[HIDDEN_TRACKS] ?: emptySet()
    }

    fun getWhitelistedFolders() = context.dataStore.data.map {
        it[WHITELISTED_FOLDERS] ?: emptySet()
    }

    fun getSafTracks() = context.dataStore.data.map {
        it[SAF_TRACKS] ?: emptySet()
    }

    suspend fun getSavedMusicState() = context.dataStore.data.map {
        val string = it[LAST_MUSIC_STATE] ?: ""
        try {
            Json.decodeFromString<MusicState>(string)
        } catch (e: IllegalArgumentException) {
            MusicState()
        }
    }.first()

    fun getMinTrackDuration() = context.dataStore.data.map {
        it[MIN_TRACK_DURATION] ?: 0
    }

    suspend fun saveSavedMusicState(musicState: MusicState) =
        context.dataStore.edit {
            println("Hello Nekopara: $musicState")
            it[LAST_MUSIC_STATE] = Json.encodeToString(musicState)
        }


    suspend fun unhideTrack(mediaId: String) {
        context.dataStore.edit {
            val alreadyHidden = it[HIDDEN_TRACKS] ?: emptySet()
            it[HIDDEN_TRACKS] = alreadyHidden.copyMutate { remove(mediaId) }
        }
    }

    suspend fun saveEqualizerBands(bands: List<EqualizerBand>) {
        context.dataStore.edit { it[EQUALIZER_BANDS] = Json.encodeToString(bands) }
    }

    suspend fun saveEqualizerPresets(presets: List<EqualizerPreset>) {
        context.dataStore.edit { it[EQUALIZER_PRESETS] = Json.encodeToString(presets) }
    }

    suspend fun getEqualizerBands(): List<EqualizerBand> {
        return context.dataStore.data.map {
            val string = it[EQUALIZER_BANDS] ?: "[]"
            Json.decodeFromString<List<EqualizerBand>>(string)
        }.first()
    }

    suspend fun getEqualizerPresets(): List<EqualizerPreset> {
        return context.dataStore.data.map {
            val string = it[EQUALIZER_PRESETS] ?: "[]"
            Json.decodeFromString<List<EqualizerPreset>>(string)
        }.first()
    }

    fun getEqualizerBandsFlow(): Flow<List<EqualizerBand>> {
        return context.dataStore.data.map {
            val string = it[EQUALIZER_BANDS] ?: "[]"
            Json.decodeFromString<List<EqualizerBand>>(string)
        }
    }

    suspend fun getIsEqualizerEnabled() = context.dataStore.data.map {
        it[EQUALIZER_ENABLED] ?: false
    }.first()

}