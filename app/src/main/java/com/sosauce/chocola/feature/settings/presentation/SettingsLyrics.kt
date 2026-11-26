@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.LyricsAlignment
import com.sosauce.chocola.core.presentation.preferences.rememberArtLyrics
import com.sosauce.chocola.core.presentation.preferences.rememberLyricsAlignment
import com.sosauce.chocola.core.presentation.preferences.rememberLyricsFontSize
import com.sosauce.nekobites.components.settings.SelectableTile
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsPicker
import com.sosauce.nekobites.components.settings.SettingsStepper
import com.sosauce.nekobites.components.settings.SettingsSwitch

@Composable
fun ColumnScope.SettingsLyrics() {
    var lyricsAlignment by rememberLyricsAlignment()
    var lyricsFontSize by rememberLyricsFontSize()
    var artLyrics by rememberArtLyrics()

    val alignments = listOf(
        AlignmentOption(LyricsAlignment.START, R.drawable.align_left, R.string.start),
        AlignmentOption(LyricsAlignment.CENTERED, R.drawable.align_center, R.string.center),
        AlignmentOption(LyricsAlignment.END, R.drawable.align_right, R.string.end)
    )

    SettingsGroup {
        SettingsPicker(title = stringResource(R.string.settings_alignment)) {
            items(alignments) { option ->
                SelectableTile(
                    selected = lyricsAlignment == option.value,
                    onClick = { lyricsAlignment = option.value },
                    label = stringResource(option.label)
                ) {
                    Icon(
                        painter = painterResource(option.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        SettingsStepper(
            title = stringResource(R.string.font_size),
            value = lyricsFontSize,
            range = 20..30,
            onValueChange = { lyricsFontSize = it }
        )
        SettingsSwitch(
            title = stringResource(R.string.art_lyrics),
            description = stringResource(R.string.art_lyrics_desc),
            checked = artLyrics,
            onCheckedChange = { artLyrics = !artLyrics }
        )
    }
}

private data class AlignmentOption(
    val value: String,
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int
)
