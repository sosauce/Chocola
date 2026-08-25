@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.sosauce.chocola.data.datastore.rememberArtworkShape
import com.sosauce.chocola.data.datastore.rememberCarousel
import com.sosauce.chocola.data.datastore.rememberCenterTitle
import com.sosauce.chocola.data.datastore.rememberDynamicDuration
import com.sosauce.chocola.data.datastore.rememberNowPlayingShapeMorph
import com.sosauce.chocola.data.datastore.rememberShowAlbumName
import com.sosauce.chocola.data.datastore.rememberThumbStyle
import com.sosauce.chocola.data.datastore.rememberTrackStyle
import com.sosauce.chocola.data.datastore.rememberUseArtAsBackground
import com.sosauce.chocola.presentation.components.LazyRowWithScrollButton
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSwitch
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.presentation.screens.settings.compenents.ShapeSelector
import com.sosauce.chocola.presentation.screens.settings.compenents.SquareSelector
import com.sosauce.chocola.utils.ArtworkShape
import com.sosauce.chocola.utils.ThumbStyle
import com.sosauce.chocola.utils.TrackStyle

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
        SettingsWithTitle(
            title = R.string.artwork
        ) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                )
            ) {
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
            SettingsSwitch(
                checked = useArtBackground,
                onCheckedChange = { useArtBackground = !useArtBackground },
                topDp = 4.dp,
                bottomDp = 4.dp,
                text = stringResource(R.string.art_as_bg)
            )
            SettingsSwitch(
                checked = shapeMorph,
                onCheckedChange = { shapeMorph = !shapeMorph },
                topDp = 4.dp,
                bottomDp = 4.dp,
                text = stringResource(R.string.shape_morph),
                optionalDescription = R.string.shape_morph_desc
            )
            SettingsSwitch(
                checked = useCarousel,
                onCheckedChange = { useCarousel = !useCarousel },
                topDp = 4.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.use_carousel)
            )
        }
        SettingsWithTitle(
            title = R.string.slider
        ) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                )
            ) {
                LazyRowWithScrollButton(
                    items = thumbs
                ) { thumb ->
                    SquareSelector(
                        onClick = { thumbStyle = thumb },
                        isSelected = thumbStyle == thumb
                    ) { ThumbStyle.toThumb(thumb, false) }
                }
            }
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 4.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            ) {
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
        }

        SettingsWithTitle(
            title = R.string.ui
        ) {
            SettingsSwitch(
                checked = centerTitle,
                onCheckedChange = { centerTitle = !centerTitle },
                topDp = 24.dp,
                bottomDp = 4.dp,
                text = stringResource(R.string.centered_title)
            )
            SettingsSwitch(
                checked = showAlbumName,
                onCheckedChange = { showAlbumName = !showAlbumName },
                topDp = 4.dp,
                bottomDp = 4.dp,
                text = stringResource(R.string.show_album_name)
            )
            SettingsSwitch(
                checked = dynamicDuration,
                onCheckedChange = { dynamicDuration = !dynamicDuration },
                topDp = 4.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.dynamic_duration),
                optionalDescription = R.string.dynamic_duration_desc
            )
        }
    }
}