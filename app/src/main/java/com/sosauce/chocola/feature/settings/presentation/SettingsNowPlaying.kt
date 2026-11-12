@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.ArtworkShape
import com.sosauce.chocola.core.designsystem.ThumbStyle
import com.sosauce.chocola.core.designsystem.TrackStyle
import com.sosauce.chocola.core.presentation.preferences.rememberArtworkShape
import com.sosauce.chocola.core.presentation.preferences.rememberCarousel
import com.sosauce.chocola.core.presentation.preferences.rememberCenterTitle
import com.sosauce.chocola.core.presentation.preferences.rememberDynamicDuration
import com.sosauce.chocola.core.presentation.preferences.rememberNowPlayingShapeMorph
import com.sosauce.chocola.core.presentation.preferences.rememberShowAlbumName
import com.sosauce.chocola.core.presentation.preferences.rememberThumbStyle
import com.sosauce.chocola.core.presentation.preferences.rememberTrackStyle
import com.sosauce.chocola.core.presentation.preferences.rememberUseArtAsBackground
import com.sosauce.chocola.core.presentation.preferences.rememberUseTurntable
import com.sosauce.nekobites.components.settings.SelectableTile
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsPicker
import com.sosauce.nekobites.components.settings.SettingsSwitch

/** Now playing settings, as two groups (style pickers + behaviour switches). */
@Composable
fun ColumnScope.SettingsNowPlaying() {
    var artworkShape by rememberArtworkShape()
    var useCarousel by rememberCarousel()
    var showAlbumName by rememberShowAlbumName()
    var centerTitle by rememberCenterTitle()
    var thumbStyle by rememberThumbStyle()
    var trackStyle by rememberTrackStyle()
    var useArtBackground by rememberUseArtAsBackground()
    var useTurntable by rememberUseTurntable()
    var dynamicDuration by rememberDynamicDuration()
    var shapeMorph by rememberNowPlayingShapeMorph()

    val shapes = listOf(
        ArtworkShape.ROUNDED,
        ArtworkShape.CIRCLE,
        ArtworkShape.COOKIE_4,
        ArtworkShape.COOKIE_9,
        ArtworkShape.COOKIE_12,
        ArtworkShape.CLOVER_8,
        ArtworkShape.SUNNY,
        ArtworkShape.ARROW,
        ArtworkShape.DIAMOND,
        ArtworkShape.BUN,
        ArtworkShape.HEART
    )
    val thumbs = listOf(ThumbStyle.STRAIGHT, ThumbStyle.BALL, ThumbStyle.MORPHING)
    val tracks = listOf(TrackStyle.WAVY, TrackStyle.STRAIGHT)

    SettingsGroup {
        SettingsPicker(title = stringResource(R.string.artwork_shape)) {
            items(shapes) { shape ->
                SelectableTile(
                    selected = artworkShape == shape,
                    onClick = { artworkShape = shape }
                ) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = ArtworkShape.toShape(shape)
                            )
                    )
                }
            }
        }
        SettingsPicker(title = stringResource(R.string.slider_thumb)) {
            items(thumbs) { thumb ->
                SelectableTile(
                    selected = thumbStyle == thumb,
                    onClick = { thumbStyle = thumb }
                ) {
                    ThumbStyle.toThumb(thumb, false)
                }
            }
        }
        SettingsPicker(title = stringResource(R.string.slider_track)) {
            items(tracks) { track ->
                SelectableTile(
                    selected = trackStyle == track,
                    onClick = { trackStyle = track },
                    width = 120.dp
                ) {
                    Box(Modifier.padding(horizontal = 12.dp)) {
                        TrackStyle.toTrack(track, true, rememberSliderState(value = 0.5f))
                    }
                }
            }
        }
    }

    SettingsGroup {
        SettingsSwitch(
            title = stringResource(R.string.art_as_bg),
            checked = useArtBackground,
            onCheckedChange = { useArtBackground = !useArtBackground }
        )
        SettingsSwitch(
            checked = useTurntable,
            onCheckedChange = { useTurntable = !useTurntable },
            title = stringResource(R.string.use_turntable),
            description = stringResource(R.string.use_turntable_desc)
        )
        SettingsSwitch(
            title = stringResource(R.string.shape_morph),
            description = stringResource(R.string.shape_morph_desc),
            checked = shapeMorph,
            onCheckedChange = { shapeMorph = !shapeMorph }
        )
        SettingsSwitch(
            title = stringResource(R.string.use_carousel),
            checked = useCarousel,
            onCheckedChange = { useCarousel = !useCarousel }
        )
        SettingsSwitch(
            title = stringResource(R.string.centered_title),
            checked = centerTitle,
            onCheckedChange = { centerTitle = !centerTitle }
        )
        SettingsSwitch(
            title = stringResource(R.string.show_album_name),
            checked = showAlbumName,
            onCheckedChange = { showAlbumName = !showAlbumName }
        )
        SettingsSwitch(
            title = stringResource(R.string.dynamic_duration),
            description = stringResource(R.string.dynamic_duration_desc),
            checked = dynamicDuration,
            onCheckedChange = { dynamicDuration = !dynamicDuration }
        )
    }
}
