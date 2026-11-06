@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.presentation.preferences.rememberArtworkShape
import com.sosauce.chocola.core.presentation.preferences.rememberCarousel
import com.sosauce.chocola.core.presentation.preferences.rememberCenterTitle
import com.sosauce.chocola.core.presentation.preferences.rememberDynamicDuration
import com.sosauce.chocola.core.presentation.preferences.rememberNowPlayingShapeMorph
import com.sosauce.chocola.core.presentation.preferences.rememberShowAlbumName
import com.sosauce.chocola.core.presentation.preferences.rememberThumbStyle
import com.sosauce.chocola.core.presentation.preferences.rememberTrackStyle
import com.sosauce.chocola.core.presentation.preferences.rememberUseArtAsBackground
import com.sosauce.chocola.feature.settings.presentation.components.SettingsCard
import com.sosauce.chocola.feature.settings.presentation.components.SettingsCardHeader
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSwitch
import com.sosauce.chocola.feature.settings.presentation.components.SettingsWithTitle
import com.sosauce.chocola.feature.settings.presentation.components.ShapeSelector
import com.sosauce.chocola.feature.settings.presentation.components.SquareSelector
import com.sosauce.chocola.core.designsystem.ArtworkShape
import com.sosauce.chocola.core.designsystem.ThumbStyle
import com.sosauce.chocola.core.designsystem.TrackStyle
import com.sosauce.nekobites.components.LazyRowWithScrollButton

@Composable
fun SettingsNowPlaying() {

    var artworkShape by rememberArtworkShape()
    var useCarousel by rememberCarousel()
    var showAlbumName by rememberShowAlbumName()
    var centerTitle by rememberCenterTitle()
    var thumbStyle by rememberThumbStyle()
    var trackStyle by rememberTrackStyle()
    var useArtBackground by rememberUseArtAsBackground()
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
    val thumbs = listOf(
        ThumbStyle.STRAIGHT,
        ThumbStyle.BALL,
        ThumbStyle.MORPHING
    )
    val tracks = listOf(
        TrackStyle.WAVY,
        TrackStyle.STRAIGHT
    )

    Column {
        SettingsCard(
            topDp = 24.dp,
            bottomDp = 2.dp
        ) {
            SettingsCardHeader(R.string.artwork_shape)
            LazyRowWithScrollButton(
                items = shapes
            ) { shape ->
                ShapeSelector(
                    onClick = { artworkShape = shape },
                    shape = shape,
                    isSelected = artworkShape == shape
                )
            }
        }
        SettingsCard(
            topDp = 2.dp,
            bottomDp = 2.dp
        ) {
            SettingsCardHeader(R.string.slider_thumb)
            LazyRowWithScrollButton(
                items = thumbs
            ) { thumb ->
                SquareSelector(
                    onClick = { thumbStyle = thumb },
                    isSelected = thumbStyle == thumb
                ) { ThumbStyle.toThumb(thumb, false) }
            }
        }
        SettingsCard(
            topDp = 2.dp,
            bottomDp = 2.dp
        ) {
            SettingsCardHeader(R.string.slider_track)
            LazyRowWithScrollButton(
                items = tracks
            ) { track ->
                SquareSelector(
                    onClick = { trackStyle = track },
                    isSelected = trackStyle == track,
                    width = 100.dp
                ) { TrackStyle.toTrack(track, true, rememberSliderState(value = 0.5f)) }
            }
        }
        SettingsSwitch(
            checked = useArtBackground,
            onCheckedChange = { useArtBackground = !useArtBackground },
            topDp = 2.dp,
            bottomDp = 2.dp,
            text = stringResource(R.string.art_as_bg)
        )
        SettingsSwitch(
            checked = shapeMorph,
            onCheckedChange = { shapeMorph = !shapeMorph },
            topDp = 2.dp,
            bottomDp = 2.dp,
            text = stringResource(R.string.shape_morph),
            optionalDescription = R.string.shape_morph_desc
        )
        SettingsSwitch(
            checked = useCarousel,
            onCheckedChange = { useCarousel = !useCarousel },
            topDp = 2.dp,
            bottomDp = 2.dp,
            text = stringResource(R.string.use_carousel)
        )
        SettingsSwitch(
            checked = centerTitle,
            onCheckedChange = { centerTitle = !centerTitle },
            topDp = 2.dp,
            bottomDp = 2.dp,
            text = stringResource(R.string.centered_title)
        )
        SettingsSwitch(
            checked = showAlbumName,
            onCheckedChange = { showAlbumName = !showAlbumName },
            topDp = 2.dp,
            bottomDp = 2.dp,
            text = stringResource(R.string.show_album_name)
        )
        SettingsSwitch(
            checked = dynamicDuration,
            onCheckedChange = { dynamicDuration = !dynamicDuration },
            topDp = 2.dp,
            bottomDp = 24.dp,
            text = stringResource(R.string.dynamic_duration),
            optionalDescription = R.string.dynamic_duration_desc
        )
    }
}