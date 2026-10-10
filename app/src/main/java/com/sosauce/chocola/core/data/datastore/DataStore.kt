package com.sosauce.chocola.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sosauce.chocola.core.data.datastore.LegacyPreferencesKeys.EQUALIZER_BANDS
import com.sosauce.chocola.core.data.datastore.LegacyPreferencesKeys.EQUALIZER_PRESETS

private const val PREFERENCES_NAME = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME,
    produceMigrations = { listOf(CleanupEqualizerSettingsMigration) }
)

// keys that are no longer in use
data object LegacyPreferencesKeys {
    val EQUALIZER_PRESETS = stringPreferencesKey("EQUALIZER_PRESETS")
    val EQUALIZER_BANDS = stringPreferencesKey("EQUALIZER_BANDS")
}

data object PreferencesKeys {
    val THEME = stringPreferencesKey("theme")
    val USE_SYSTEM_FONT = booleanPreferencesKey("use_sys_font")
    val WHITELISTED_FOLDERS = stringSetPreferencesKey("WHITELISTED_FOLDERS")
    val HAS_SEEN_TIP = booleanPreferencesKey("has_seen_tip")
    val SNAP_SPEED_N_PITCH = booleanPreferencesKey("snap_peed_n_pitch")
    val USE_ART_THEME = booleanPreferencesKey("use_art_theme")
    val SHOW_X_BUTTON = booleanPreferencesKey("show_x_button")
    val SHOW_SHUFFLE_BUTTON = booleanPreferencesKey("show_shuffle_button")
    val SAF_TRACKS = stringSetPreferencesKey("saf_tracks")
    val GROUP_BY_FOLDERS = booleanPreferencesKey("GROUP_BY_FOLDERS")
    val CAROUSEL = booleanPreferencesKey("CAROUSEL")
    val MEDIA_INDEX_TO_MEDIA_ID = stringPreferencesKey("MEDIA_INDEX_TO_MEDIA_ID")
    val NUMBER_OF_ALBUM_GRIDS = intPreferencesKey("NUMBER_OF_ALBUM_GRIDS")
    val HIDDEN_FOLDERS = stringSetPreferencesKey("HIDDEN_FOLDERS")
    val ART_AS_BACKGROUND = booleanPreferencesKey("ART_AS_BACKGROUND")
    val ALBUM_SORT = intPreferencesKey("ALBUM_SORT")
    val TRACK_SORT = intPreferencesKey("TRACK_SORT")
    val ARTIST_SORT = intPreferencesKey("ARTIST_SORT")

    val REGEX_FILTER = booleanPreferencesKey("REGEX_FILTER")
    val MATCH_CASE_FILTER = booleanPreferencesKey("MATCH_CASE_FILTER")

    val PAUSE_ON_MUTE = booleanPreferencesKey("PAUSE_ON_MUTE")
    val MIN_TRACK_DURATION = intPreferencesKey("MIN_TRACK_DURATION")
    val PLAYLIST_SORT = intPreferencesKey("PLAYLIST_SORT")
    val ARTWORK_SHAPE = stringPreferencesKey("ARTWORK_SHAPE")
    val HAS_BEEN_THROUGH_SETUP = booleanPreferencesKey("HAS_BEEN_THROUGH_SETUP")
    val SORT_TRACKS_ASCENDING = booleanPreferencesKey("SORT_TRACKS_ASCENDING")
    val LAST_MUSIC_STATE = stringPreferencesKey("LAST_MUSIC_STATE")
    val HIDDEN_TRACKS = stringSetPreferencesKey("HIDDEN_TRACKS")
    val SHOW_ALBUM_NAME = booleanPreferencesKey("SHOW_ALBUM_NAME")
    val LYRICS_ALIGNMENT = stringPreferencesKey("LYRICS_ALIGNMENT")
    val LYRICS_FONT_SIZE = intPreferencesKey("LYRICS_FONT_SIZE")

    val SORT_ARTISTS_ASCENDING = booleanPreferencesKey("SORT_ARTISTS_ASCENDING")
    val SORT_ALBUMS_ASCENDING = booleanPreferencesKey("SORT_ALBUMS_ASCENDING")
    val SORT_PLAYLISTS_ASCENDING = booleanPreferencesKey("SORT_PLAYLISTS_ASCENDING")
    val PALETTE_STYLE = stringPreferencesKey("PALETTE_STYLE")
    val SEEK_BUTTONS_DURATION = intPreferencesKey("SEEK_BUTTONS_DURATION")
    val CENTER_TITLE = booleanPreferencesKey("CENTER_TITLE")
    val EQUALIZER_ENABLED = booleanPreferencesKey("EQUALIZER_ENABLED")
    val THUMB_STYLE = stringPreferencesKey("THUMB_STYLE")
    val TRACK_STYLE = stringPreferencesKey("TRACK_STYLE")

    val ART_LYRICS = booleanPreferencesKey("ART_LYRICS")
    val INITIAL_SCREEN = stringPreferencesKey("INITIAL_SCREEN")
    val EQUALIZER_GAINS = stringPreferencesKey("EQUALIZER_GAINS")

    val DYNAMIC_DURATION = booleanPreferencesKey("DYNAMIC_DURATION")

    val NOW_PLAYING_SHAPE_MORPH = booleanPreferencesKey("NOW_PLAYING_SHAPE_MORPH")
    val KEEP_ALIVE = booleanPreferencesKey("KEEP_ALIVE")
    val AOD_ENABLE_GESTURES = booleanPreferencesKey("AOD_ENABLE_GESTURES")
    val AOD_SHOW_CONTROLS = booleanPreferencesKey("AOD_SHOW_CONTROLS")

}


private object CleanupEqualizerSettingsMigration : DataMigration<Preferences> {
    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: Preferences): Preferences {
        return currentData.toMutablePreferences().apply {
            remove(EQUALIZER_PRESETS)
            remove(EQUALIZER_BANDS)
            println("Keys were removed!")
        }
    }

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        currentData.contains(EQUALIZER_PRESETS) && currentData.contains(EQUALIZER_BANDS)
}
