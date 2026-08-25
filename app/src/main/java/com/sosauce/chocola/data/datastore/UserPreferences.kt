package com.sosauce.chocola.data.datastore

import android.content.Context
import androidx.collection.ArraySet
import androidx.collection.FloatList
import androidx.compose.ui.util.fastMap
import androidx.datastore.preferences.core.edit
import com.sosauce.chocola.data.datastore.PreferencesKeys.ALBUM_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.ARTIST_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.EQUALIZER_ENABLED
import com.sosauce.chocola.data.datastore.PreferencesKeys.EQUALIZER_GAINS
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
import com.sosauce.chocola.data.models.EqualizerPreset
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.utils.AlbumSort
import com.sosauce.chocola.utils.ArtistSort
import com.sosauce.chocola.utils.PlaylistSort
import com.sosauce.chocola.utils.TrackSort
import com.sosauce.chocola.utils.copyMutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.StringFormat
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
            it[LAST_MUSIC_STATE] = Json.encodeToString(musicState)
        }


    suspend fun unhideTrack(mediaId: String) {
        context.dataStore.edit {
            val alreadyHidden = it[HIDDEN_TRACKS] ?: emptySet()
            it[HIDDEN_TRACKS] = alreadyHidden.copyMutate { remove(mediaId) }
        }
    }


    suspend fun getIsEqualizerEnabled() = context.dataStore.data.map {
        it[EQUALIZER_ENABLED] ?: false
    }.first()

    suspend fun getBandGains(): List<Float> {
        val gainsString = context.dataStore.data.map {
            it[EQUALIZER_GAINS] ?: "0,0,0,0,0,0,0,0,0,0"
        }.first()
        return gainsString.split(",").fastMap { it.toFloatOrNull() ?: 0f }
    }
    suspend fun saveBandGains(gains: List<Float>) {
        context.dataStore.edit {
            it[EQUALIZER_GAINS] = gains.joinToString(",")
        }
    }

    fun tracksSettings() = combine(
        getTrackSort,
        sortTracksAscending
    ) { sort, asc ->
        TracksSettings(
            sort = sort,
            ascending = asc
        )
    }

    fun searchSettings() = combine(
        getRegexFilter,
        getMatchCaseFilter
    ) { regex, matchCase ->
        SearchSettings(
            regex = regex,
            matchCase = matchCase
        )
    }


}

data class TracksSettings(
    val sort: TrackSort,
    val ascending: Boolean
)

data class SearchSettings(
    val regex: Boolean,
    val matchCase: Boolean
)