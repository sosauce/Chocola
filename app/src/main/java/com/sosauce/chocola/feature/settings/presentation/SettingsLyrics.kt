@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.feature.settings.presentation.components.SettingsCard
import com.sosauce.chocola.core.presentation.preferences.rememberArtLyrics
import com.sosauce.chocola.core.presentation.preferences.rememberLyricsAlignment
import com.sosauce.chocola.core.presentation.preferences.rememberLyricsFontSize
import com.sosauce.chocola.feature.settings.presentation.components.SettingsInput
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSelector
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSwitch
import com.sosauce.chocola.feature.settings.presentation.components.SettingsWithTitle
import com.sosauce.chocola.core.designsystem.LyricsAlignment
import com.sosauce.nekobites.components.LazyRowWithScrollButton

@Composable
fun SettingsLyrics() {

    var lyricsAlignment by rememberLyricsAlignment()
    var lyricsFontSize by rememberLyricsFontSize()
    var artLyrics by rememberArtLyrics()
    val alignmentItems = listOf(
        AlignmentItem(
            onClick = { lyricsAlignment = LyricsAlignment.START },
            isSelected = lyricsAlignment == LyricsAlignment.START,
            icon = R.drawable.align_left,
            text = R.string.start
        ),
        AlignmentItem(
            onClick = { lyricsAlignment = LyricsAlignment.CENTERED },
            isSelected = lyricsAlignment == LyricsAlignment.CENTERED,
            icon = R.drawable.align_center,
            text = R.string.center
        ),
        AlignmentItem(
            onClick = { lyricsAlignment = LyricsAlignment.END },
            isSelected = lyricsAlignment == LyricsAlignment.END,
            icon = R.drawable.align_right,
            text = R.string.end
        )
    )

    Column {
        SettingsWithTitle(
            title = R.string.lyrics
        ) {


            SettingsCard(
                topDp = 24.dp,
                bottomDp = 2.dp
            ) {
                Column {
                    LazyRowWithScrollButton(
                        items = alignmentItems
                    ) { alignment ->
                        SettingsSelector(
                            onClick = alignment.onClick,
                            icon = alignment.icon,
                            text = alignment.text,
                            isSelected = alignment.isSelected
                        )
                    }
                }
            }

            SettingsInput(
                value = lyricsFontSize,
                minValue = 20,
                maxValue = 30,
                topDp = 2.dp,
                bottomDp = 2.dp,
                text = R.string.font_size,
                onNewValue = { lyricsFontSize = it }
            )
            SettingsSwitch(
                checked = artLyrics,
                onCheckedChange = { artLyrics = !artLyrics },
                topDp = 2.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.art_lyrics),
                optionalDescription = R.string.art_lyrics_desc
            )
        }
    }
}

data class AlignmentItem(
    val onClick: () -> Unit,
    val isSelected: Boolean,
    val icon: Int,
    val text: Int
)