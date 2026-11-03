@file:OptIn(ExperimentalSerializationApi::class)

package com.sosauce.chocola.core.data.datastore

import android.content.Context
import android.net.Uri
import androidx.compose.ui.util.fastMap
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.sosauce.chocola.core.data.backup.BackupSettingsFile
import com.sosauce.chocola.core.data.backup.SETTINGS_BACKUP_APP_NAME
import com.sosauce.chocola.core.data.backup.SettingType
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.ALBUM_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.ARTIST_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.AUTO_ADD_SUBFOLDERS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.BLACKLISTED_FOLDERS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.EQUALIZER_ENABLED
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.EQUALIZER_GAINS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.GROUP_BY_FOLDERS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.HIDDEN_FOLDERS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.HIDDEN_TRACKS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.LAST_MUSIC_STATE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.MATCH_CASE_FILTER
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.MIN_TRACK_DURATION
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.NEW_FOLDER_POLICY
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.PAUSE_ON_MUTE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.PLAYLIST_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.REGEX_FILTER
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_ALBUMS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_ARTISTS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_PLAYLISTS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_TRACKS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.TRACK_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.WHITELISTED_FOLDERS
import com.sosauce.chocola.core.data.mapper.toJsonMap
import com.sosauce.chocola.core.designsystem.NewFolderPolicy
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.model.AlbumSort
import com.sosauce.chocola.core.domain.model.ArtistSort
import com.sosauce.chocola.core.domain.model.PlaylistSort
import com.sosauce.chocola.core.domain.model.SearchSettings
import com.sosauce.chocola.core.domain.model.TrackSort
import com.sosauce.chocola.core.domain.model.TracksSettings
import com.sosauce.chocola.core.domain.util.addOrRemove
import com.sosauce.chocola.core.domain.util.copyMutate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.double
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.collections.emptySet
import kotlin.collections.forEach

class UserPreferences(
    private val context: Context
) {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }


    val getTrackSort = context.dataStore.data.map {
        val sort = it[TRACK_SORT] ?: 0
        TrackSort.entries.getOrNull(sort) ?: TrackSort.TITLE
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
    fun getBlacklistedFolders() = context.dataStore.data.map {
        it[BLACKLISTED_FOLDERS] ?: emptySet()
    }

    fun getSafTracks() = context.dataStore.data.map {
        it[SAF_TRACKS] ?: emptySet()
    }

    suspend fun getSavedMusicState() = context.dataStore.data.map {
        val string = it[LAST_MUSIC_STATE] ?: ""
        try {
            Json.decodeFromString<MusicState>(string)
        } catch (_: IllegalArgumentException) {
            MusicState()
        }
    }.first()

    fun getMinTrackDuration() = context.dataStore.data.map {
        it[MIN_TRACK_DURATION] ?: 0
    }

    fun getNewFolderPolicy() = context.dataStore.data.map {
        it[NEW_FOLDER_POLICY] ?: NewFolderPolicy.BLACKLIST
    }

    fun getAutoAddSubfolders() = context.dataStore.data.map {
        it[AUTO_ADD_SUBFOLDERS] ?: false
    }

    val getHiddenFolders = context.dataStore.data.map {
            it[HIDDEN_FOLDERS] ?: emptySet()
        }

    val getGroupByFolders = context.dataStore.data.map {
            it[GROUP_BY_FOLDERS] ?: false
        }

    suspend fun toggleHiddenFolder(folder: String) =
        context.dataStore.edit {
            val current = it[HIDDEN_FOLDERS] ?: emptySet()
            it[HIDDEN_FOLDERS] = current.copyMutate { addOrRemove(folder) }
        }

    suspend fun setGroupByFolders(enabled: Boolean) =
        context.dataStore.edit {
            it[GROUP_BY_FOLDERS] = enabled
        }

    suspend fun saveSavedMusicState(musicState: MusicState) =
        context.dataStore.edit {
            it[LAST_MUSIC_STATE] = Json.encodeToString(musicState)
        }


    suspend fun addWhitelistedFolders(whitelist: Set<String>) =
        context.dataStore.edit {
            val moved = (it[BLACKLISTED_FOLDERS] ?: emptySet()).under(whitelist)
            it[WHITELISTED_FOLDERS] = (it[WHITELISTED_FOLDERS] ?: emptySet()) + whitelist + moved
            it[BLACKLISTED_FOLDERS] = (it[BLACKLISTED_FOLDERS] ?: emptySet()) - moved
        }

    suspend fun addBlacklistedFolders(blacklist: Set<String>) =
        context.dataStore.edit {
            val moved = (it[WHITELISTED_FOLDERS] ?: emptySet()).under(blacklist)
            it[BLACKLISTED_FOLDERS] = (it[BLACKLISTED_FOLDERS] ?: emptySet()) + blacklist + moved
            it[WHITELISTED_FOLDERS] = (it[WHITELISTED_FOLDERS] ?: emptySet()) - blacklist - moved
        }

    // the given roots themselves plus everything below them
    private fun Set<String>.under(roots: Set<String>) =
        filterTo(mutableSetOf()) { p -> roots.any { p == it || p.startsWith("$it/") } }

    suspend fun saveFoldersList(whitelist: Set<String>, blacklist: Set<String>) =
        context.dataStore.edit {
            it[WHITELISTED_FOLDERS] = whitelist
            it[BLACKLISTED_FOLDERS] = blacklist - whitelist
        }

    suspend fun hasFolderLists(): Boolean =
        context.dataStore.data.first().let { WHITELISTED_FOLDERS in it || BLACKLISTED_FOLDERS in it }

    suspend fun seedInitialFoldersIfAbsent(blacklist: Set<String>) =
        context.dataStore.edit {
            it[WHITELISTED_FOLDERS] = emptySet()
            it[BLACKLISTED_FOLDERS] = blacklist
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
        return gainsString.split(",").map { it.toFloatOrNull() ?: 0f }
    }

    suspend fun saveBandGains(gains: List<Float>) {
        context.dataStore.edit {
            it[EQUALIZER_GAINS] = gains.joinToString(",")
        }
    }

    /**
     * Doesn't override, appends the given tracks to the already saved
     * @param add if true, appends the given tracks, else, removes them
     */
    suspend fun saveSafTracks(
        additionalTracks: Set<String>,
        add: Boolean
    ) {

        println("bouta change: $add")
        context.dataStore.edit {
            val existing = it[SAF_TRACKS] ?: emptySet()
            if (add) {
                it[SAF_TRACKS] = existing + additionalTracks
            } else {
                it[SAF_TRACKS] = existing - additionalTracks
            }
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


    suspend fun exportSettingsJson(): BackupSettingsFile {
        val prefs = context.dataStore.data.first()
        val jsonMap = prefs.toJsonMap()

        return BackupSettingsFile(
            createdAtMillis = System.currentTimeMillis(),
            settings = jsonMap
        )
    }

    suspend fun importSettingsJson(uri: Uri) = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use {
            val file = Json.decodeFromStream<BackupSettingsFile>(it)

            require(file.app == SETTINGS_BACKUP_APP_NAME) { "Not a Chocola backup file!" }

            val settings = file.settings

            context.dataStore.edit { prefs ->
                settings.forEach { (key, setting) ->
                    val v = setting.value
                    when (setting.type) {
                        SettingType.BOOLEAN -> prefs[booleanPreferencesKey(key)] = v.jsonPrimitive.boolean
                        SettingType.INT -> prefs[intPreferencesKey(key)] = v.jsonPrimitive.int
                        SettingType.LONG -> prefs[longPreferencesKey(key)] = v.jsonPrimitive.long
                        SettingType.FLOAT -> prefs[floatPreferencesKey(key)] = v.jsonPrimitive.float
                        SettingType.DOUBLE -> prefs[doublePreferencesKey(key)] = v.jsonPrimitive.double
                        SettingType.STRING -> prefs[stringPreferencesKey(key)] = v.jsonPrimitive.content
                        SettingType.SET_STRING -> prefs[stringSetPreferencesKey(key)] = v.jsonArray.fastMap { it.jsonPrimitive.content }.toSet()
                    }

                }
            }
        }
    }


    suspend fun writeBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val backup = exportSettingsJson()
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(json.encodeToString(backup).toByteArray())
        }
    }

}