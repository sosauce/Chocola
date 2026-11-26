@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import com.sosauce.chocola.core.presentation.preferences.rememberCenterTitle
import com.sosauce.chocola.core.presentation.preferences.rememberShowAlbumName


@Composable
fun TitleAndArtist(
    titleModifier: Modifier = Modifier,
    title: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    artist: String,
    album: String,
    hiddenButtons: Set<NowPlayingButton> = emptySet()
) {

    val showAlbumName by rememberShowAlbumName()
    val centerTitle by rememberCenterTitle()
    val textAlignment = if (centerTitle) TextAlign.Center else TextAlign.Start

    Row {
        Column(
            modifier = Modifier
                .weight(1f)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = title,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineMediumEmphasized.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = textAlignment
                    ),
                    modifier = titleModifier
                        .fillMaxWidth()
                        .basicMarquee()
                )
            }
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = artist to album,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { (artist, album) ->
                Text(
                    text = buildString {
                        append(artist)
                        if (showAlbumName) {
                            append(" - ")
                            append(album)
                        }
                    },
                    style = MaterialTheme.typography.titleLargeEmphasized.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = textAlignment
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee()
                )
            }
        }

        AnimatedVisibility(
            visible = NowPlayingButton.FAVORITE !in hiddenButtons,
            modifier = Modifier.padding(start = 5.dp),
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            ToggleButton(
                checked = isFavorite,
                onCheckedChange = { onToggleFavorite() },
                colors = ToggleButtonDefaults.colors(
                    checkedContainerColor = MaterialTheme.colorScheme.error,
                    checkedContentColor = contentColorFor(MaterialTheme.colorScheme.error)
                )
            ) {
                Icon(
                    painter = painterResource(if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite_filled),
                    contentDescription = stringResource(if (isFavorite) R.string.remove_favorite else R.string.add_favorite)
                )
            }
        }

    }
}