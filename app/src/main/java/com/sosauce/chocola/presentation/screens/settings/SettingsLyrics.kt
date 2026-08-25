@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberArtLyrics
import com.sosauce.chocola.data.datastore.rememberLyricsAlignment
import com.sosauce.chocola.data.datastore.rememberLyricsFontSize
import com.sosauce.chocola.presentation.components.LazyRowWithScrollButton
import com.sosauce.chocola.presentation.screens.settings.compenents.SelectorSurface
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsInput
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSelector
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSwitch
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.utils.LyricsAlignment

@Composable
fun SettingsLyrics() {

    var lyricsAlignment by rememberLyricsAlignment()
    var lyricsFontSize by rememberLyricsFontSize()
    var artLyrics by rememberArtLyrics()

    val lyricsAlignmentOptions = listOf(
        LyricsAlignment.START,
        LyricsAlignment.CENTERED,
        LyricsAlignment.END
    )

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


            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomEnd = 4.dp,
                    bottomStart = 4.dp
                )
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
                topDp = 4.dp,
                bottomDp = 4.dp,
                text = R.string.font_size,
                onNewValue = { lyricsFontSize = it }
            )
            SettingsSwitch(
                checked = artLyrics,
                onCheckedChange = { artLyrics = !artLyrics },
                topDp = 4.dp,
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