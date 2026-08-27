@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.sosauce.chocola.data.datastore.rememberCenterTitle
import com.sosauce.chocola.data.datastore.rememberShowAlbumName


@Composable
fun TitleAndArtist(
    titleModifier: Modifier = Modifier,
    title: String,
    artist: String,
    album: String
) {

    val showAlbumName by rememberShowAlbumName()
    val centerTitle by rememberCenterTitle()
    val textAlignment = if (centerTitle) TextAlign.Center else TextAlign.Start

    Column(
        modifier = Modifier.fillMaxWidth()
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
                    fontWeight = FontWeight.ExtraBold,
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
}