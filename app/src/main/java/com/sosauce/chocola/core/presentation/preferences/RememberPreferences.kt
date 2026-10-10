@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.core.presentation.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.ALBUM_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.AOD_ENABLE_GESTURES
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.AOD_SHOW_CONTROLS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.ARTIST_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.ARTWORK_SHAPE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.ART_AS_BACKGROUND
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.ART_LYRICS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.CAROUSEL
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.CENTER_TITLE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.DYNAMIC_DURATION
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.EQUALIZER_ENABLED
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.GROUP_BY_FOLDERS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.HAS_BEEN_THROUGH_SETUP
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.HAS_SEEN_TIP
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.HIDDEN_FOLDERS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.HIDDEN_TRACKS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.INITIAL_SCREEN
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.KEEP_ALIVE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.LYRICS_ALIGNMENT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.LYRICS_FONT_SIZE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.MATCH_CASE_FILTER
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.MIN_TRACK_DURATION
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.NOW_PLAYING_SHAPE_MORPH
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.NUMBER_OF_ALBUM_GRIDS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.PALETTE_STYLE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.PAUSE_ON_MUTE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.PLAYLIST_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.REGEX_FILTER
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SEEK_BUTTONS_DURATION
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SHOW_ALBUM_NAME
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SHOW_SHUFFLE_BUTTON
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SNAP_SPEED_N_PITCH
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_ALBUMS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_ARTISTS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_PLAYLISTS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.SORT_TRACKS_ASCENDING
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.THEME
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.THUMB_STYLE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.TRACK_SORT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.TRACK_STYLE
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.USE_ART_THEME
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.USE_SYSTEM_FONT
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.WHITELISTED_FOLDERS
import com.sosauce.chocola.core.data.datastore.dataStore
import com.sosauce.chocola.core.designsystem.ArtworkShape
import com.sosauce.chocola.core.designsystem.CutePaletteStyle
import com.sosauce.chocola.core.designsystem.CuteTheme
import com.sosauce.chocola.core.designsystem.LyricsAlignment
import com.sosauce.chocola.core.designsystem.ThumbStyle
import com.sosauce.chocola.core.designsystem.TrackStyle
import com.sosauce.chocola.core.presentation.navigation.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.runBlocking

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
fun rememberKeepAlive() =
    rememberPreference(key = KEEP_ALIVE, defaultValue = false)


@Composable
fun rememberInitialScreen() =
    rememberPreference(key = INITIAL_SCREEN, defaultValue = Screen.Main.toString())

@Composable
fun rememberAodEnableGestures() =
    rememberPreference(key = AOD_ENABLE_GESTURES, defaultValue = true)

@Composable
fun rememberAodShowControls() =
    rememberPreference(key = AOD_SHOW_CONTROLS, defaultValue = true)
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

