@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.data.datastore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sosauce.chocola.data.datastore.LegacyPreferencesKeys.EQUALIZER_BANDS
import com.sosauce.chocola.data.datastore.LegacyPreferencesKeys.EQUALIZER_PRESETS
import com.sosauce.chocola.data.datastore.PreferencesKeys.ALBUM_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.ARTIST_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.ARTWORK_SHAPE
import com.sosauce.chocola.data.datastore.PreferencesKeys.ART_AS_BACKGROUND
import com.sosauce.chocola.data.datastore.PreferencesKeys.ART_LYRICS
import com.sosauce.chocola.data.datastore.PreferencesKeys.CAROUSEL
import com.sosauce.chocola.data.datastore.PreferencesKeys.CENTER_TITLE
import com.sosauce.chocola.data.datastore.PreferencesKeys.DYNAMIC_DURATION
import com.sosauce.chocola.data.datastore.PreferencesKeys.EQUALIZER_ENABLED
import com.sosauce.chocola.data.datastore.PreferencesKeys.GROUP_BY_FOLDERS
import com.sosauce.chocola.data.datastore.PreferencesKeys.HAS_BEEN_THROUGH_SETUP
import com.sosauce.chocola.data.datastore.PreferencesKeys.HAS_SEEN_TIP
import com.sosauce.chocola.data.datastore.PreferencesKeys.HIDDEN_FOLDERS
import com.sosauce.chocola.data.datastore.PreferencesKeys.HIDDEN_TRACKS
import com.sosauce.chocola.data.datastore.PreferencesKeys.INITIAL_SCREEN
import com.sosauce.chocola.data.datastore.PreferencesKeys.LYRICS_ALIGNMENT
import com.sosauce.chocola.data.datastore.PreferencesKeys.LYRICS_FONT_SIZE
import com.sosauce.chocola.data.datastore.PreferencesKeys.MATCH_CASE_FILTER
import com.sosauce.chocola.data.datastore.PreferencesKeys.MIN_TRACK_DURATION
import com.sosauce.chocola.data.datastore.PreferencesKeys.NOW_PLAYING_SHAPE_MORPH
import com.sosauce.chocola.data.datastore.PreferencesKeys.NUMBER_OF_ALBUM_GRIDS
import com.sosauce.chocola.data.datastore.PreferencesKeys.PALETTE_STYLE
import com.sosauce.chocola.data.datastore.PreferencesKeys.PAUSE_ON_MUTE
import com.sosauce.chocola.data.datastore.PreferencesKeys.PLAYLIST_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.REGEX_FILTER
import com.sosauce.chocola.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.chocola.data.datastore.PreferencesKeys.SEEK_BUTTONS_DURATION
import com.sosauce.chocola.data.datastore.PreferencesKeys.SHOW_ALBUM_NAME
import com.sosauce.chocola.data.datastore.PreferencesKeys.SHOW_SHUFFLE_BUTTON
import com.sosauce.chocola.data.datastore.PreferencesKeys.SNAP_SPEED_N_PITCH
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_ALBUMS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_ARTISTS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_PLAYLISTS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.SORT_TRACKS_ASCENDING
import com.sosauce.chocola.data.datastore.PreferencesKeys.THEME
import com.sosauce.chocola.data.datastore.PreferencesKeys.THUMB_STYLE
import com.sosauce.chocola.data.datastore.PreferencesKeys.TRACK_SORT
import com.sosauce.chocola.data.datastore.PreferencesKeys.TRACK_STYLE
import com.sosauce.chocola.data.datastore.PreferencesKeys.USE_ART_THEME
import com.sosauce.chocola.data.datastore.PreferencesKeys.USE_SYSTEM_FONT
import com.sosauce.chocola.data.datastore.PreferencesKeys.WHITELISTED_FOLDERS
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.utils.ArtworkShape
import com.sosauce.chocola.utils.CutePaletteStyle
import com.sosauce.chocola.utils.CuteTheme
import com.sosauce.chocola.utils.LyricsAlignment
import com.sosauce.chocola.utils.ThumbStyle
import com.sosauce.chocola.utils.TrackStyle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.runBlocking

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
    val KILL_SERVICE = booleanPreferencesKey("kill_service")
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

}


@Composable
fun rememberAppTheme() =
    rememberPreference(key = THEME, defaultValue = CuteTheme.SYSTEM)

@Composable
fun rememberUseSystemFont() =
    rememberPreference(key = USE_SYSTEM_FONT, defaultValue = false)

@Composable
fun rememberSnapSpeedAndPitch() =
    rememberPreference(key = SNAP_SPEED_N_PITCH, defaultValue = false)

@Composable
fun rememberUseArtTheme() =
    rememberPreference(key = USE_ART_THEME, defaultValue = false)

@Composable
fun rememberShowShuffleButton() =
    rememberPreference(key = SHOW_SHUFFLE_BUTTON, defaultValue = true)

@Composable
fun rememberAllSafTracks() =
    rememberPreference(key = SAF_TRACKS, defaultValue = emptySet())

@Composable
fun rememberGroupByFolders() =
    rememberPreference(key = GROUP_BY_FOLDERS, defaultValue = false)

@Composable
fun rememberCarousel() =
    rememberPreference(key = CAROUSEL, defaultValue = false)

@Composable
fun rememberAlbumGrids() =
    rememberPreference(key = NUMBER_OF_ALBUM_GRIDS, defaultValue = 2)

@Composable
fun rememberArtworkShape() =
    rememberPreference(key = ARTWORK_SHAPE, defaultValue = ArtworkShape.ROUNDED)

@Composable
fun rememberHiddenFolders() =
    rememberPreference(key = HIDDEN_FOLDERS, defaultValue = emptySet())

@Composable
fun rememberUseArtAsBackground() =
    rememberPreference(key = ART_AS_BACKGROUND, defaultValue = false)

@Composable
fun rememberAlbumSort() =
    rememberPreference(key = ALBUM_SORT, defaultValue = 0)

@Composable
fun rememberTrackSort() =
    rememberPreference(key = TRACK_SORT, defaultValue = 0)

@Composable
fun rememberPlaylistSort() =
    rememberPreference(key = PLAYLIST_SORT, defaultValue = 0)

@Composable
fun rememberArtistSort() =
    rememberPreference(key = ARTIST_SORT, defaultValue = 0)

@Composable
fun rememberPauseOnMute() =
    rememberPreference(key = PAUSE_ON_MUTE, defaultValue = false)

@Composable
fun rememberMinTrackDuration() =
    rememberPreference(key = MIN_TRACK_DURATION, defaultValue = 0)

@Composable
fun rememberWhitelistedFolders() =
    rememberPreference(key = WHITELISTED_FOLDERS, defaultValue = emptySet())

@Composable
fun rememberHasBeenThroughSetup() =
    rememberPreference(key = HAS_BEEN_THROUGH_SETUP, defaultValue = false)

@Composable
fun rememberHasSeenTip() =
    rememberPreference(key = HAS_SEEN_TIP, defaultValue = false)

@Composable
fun rememberSortTracksAscending() =
    rememberPreference(key = SORT_TRACKS_ASCENDING, defaultValue = true)

@Composable
fun rememberRegexFilter() =
    rememberPreference(key = REGEX_FILTER, defaultValue = false)

@Composable
fun rememberMatchCaseFilter() =
    rememberPreference(key = MATCH_CASE_FILTER, defaultValue = false)

@Composable
fun rememberHiddenTracks() =
    rememberPreference(key = HIDDEN_TRACKS, defaultValue = emptySet())

@Composable
fun rememberShowAlbumName() =
    rememberPreference(key = SHOW_ALBUM_NAME, defaultValue = false)

@Composable
fun rememberLyricsAlignment() =
    rememberPreference(key = LYRICS_ALIGNMENT, defaultValue = LyricsAlignment.START)

@Composable
fun rememberLyricsFontSize() =
    rememberPreference(key = LYRICS_FONT_SIZE, defaultValue = 22)

@Composable
fun rememberSortArtistsAscending() =
    rememberPreference(key = SORT_ARTISTS_ASCENDING, defaultValue = true)

@Composable
fun rememberSortAlbumsAscending() =
    rememberPreference(key = SORT_ALBUMS_ASCENDING, defaultValue = true)

@Composable
fun rememberSortPlaylistsAscending() =
    rememberPreference(key = SORT_PLAYLISTS_ASCENDING, defaultValue = true)

@Composable
fun rememberPaletteStyle() =
    rememberPreference(key = PALETTE_STYLE, defaultValue = CutePaletteStyle.FIDELITY)

@Composable
fun rememberSeekButtonsDuration() =
    rememberPreference(key = SEEK_BUTTONS_DURATION, defaultValue = 5)

@Composable
fun rememberCenterTitle() =
    rememberPreference(key = CENTER_TITLE, defaultValue = false)

@Composable
fun rememberThumbStyle() =
    rememberPreference(key = THUMB_STYLE, defaultValue = ThumbStyle.STRAIGHT)


@Composable
fun rememberTrackStyle() =
    rememberPreference(key = TRACK_STYLE, defaultValue = TrackStyle.WAVY)

@Composable
fun rememberEnableEqualizer() =
    rememberPreference(key = EQUALIZER_ENABLED, defaultValue = false)

@Composable
fun rememberArtLyrics() =
    rememberPreference(key = ART_LYRICS, defaultValue = false)

@Composable
fun rememberDynamicDuration() =
    rememberPreference(key = DYNAMIC_DURATION, defaultValue = false)

@Composable
fun rememberNowPlayingShapeMorph() =
    rememberPreference(key = NOW_PLAYING_SHAPE_MORPH, defaultValue = true)


@Composable
fun rememberInitialScreen() =
    rememberPreference(key = INITIAL_SCREEN, defaultValue = Screen.Main.toString())

@Composable
fun rememberInitialScreenBlocking(): Screen {
    val context = LocalContext.current

    val screen = runBlocking {
        context.dataStore.data.mapLatest {
            it[INITIAL_SCREEN] ?: Screen.Main.toString()
        }.first()
    }
    return Screen.toScreen(screen)
}
//
//suspend fun getPauseOnMute(context: Context) =
//    getPreference(key = PAUSE_ON_MUTE, defaultValue = false, context = context)


//suspend fun saveMediaIndexToMediaIdMap(pair: LastPlayed, context: Context) =
//    saveCustomPreference(value = pair, key = MEDIA_INDEX_TO_MEDIA_ID, context = context)

//suspend fun getMediaIndexToMediaIdMap(context: Context) =
//    getCustomPreference(
//        key = MEDIA_INDEX_TO_MEDIA_ID,
//        defaultValue = LastPlayed("", 0L),
//        context = context
//    )


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



