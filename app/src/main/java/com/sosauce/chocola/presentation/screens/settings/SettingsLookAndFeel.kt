@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAppTheme
import com.sosauce.chocola.data.datastore.rememberPaletteStyle
import com.sosauce.chocola.data.datastore.rememberShowShuffleButton
import com.sosauce.chocola.data.datastore.rememberUseArtTheme
import com.sosauce.chocola.data.datastore.rememberUseSystemFont
import com.sosauce.chocola.presentation.screens.settings.compenents.PaletteSelector
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSelector
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSwitch
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.presentation.theme.anyDarkColorScheme
import com.sosauce.chocola.presentation.theme.anyLightColorScheme
import com.sosauce.chocola.utils.CutePaletteStyle
import com.sosauce.chocola.utils.CuteTheme
import com.sosauce.nekobites.components.LazyRowWithScrollButton

@Composable
fun SettingsLookAndFeel() {
    var theme by rememberAppTheme()
    var useSystemFont by rememberUseSystemFont()
    var showShuffleButton by rememberShowShuffleButton()
    var useMaterialArt by rememberUseArtTheme()
    var paletteStyle by rememberPaletteStyle()
    val anyDark = anyDarkColorScheme()
    val anyLight = anyLightColorScheme()
    val isSystemDark = isSystemInDarkTheme()

    val themeItems = listOf(
        ThemeItem(
            onClick = { theme = CuteTheme.SYSTEM },
            backgroundColor = if (isSystemDark) anyDark.surfaceContainer else anyLight.surfaceContainer,
            iconColor = if (isSystemDark) anyDark.onSurface else anyLight.onSurface,
            text = R.string.system,
            isSelected = theme == CuteTheme.SYSTEM,
            icon = R.drawable.system_theme
        ),
        ThemeItem(
            onClick = { theme = CuteTheme.DARK },
            backgroundColor = anyDark.surfaceContainer,
            iconColor = anyDark.onSurface,
            text = R.string.dark_mode,
            isSelected = theme == CuteTheme.DARK,
            icon = R.drawable.dark_mode
        ),
        ThemeItem(
            onClick = { theme = CuteTheme.LIGHT },
            backgroundColor = anyLight.surfaceContainer,
            iconColor = anyLight.onSurface,
            text = R.string.light_mode,
            icon = R.drawable.light_mode,
            isSelected = theme == CuteTheme.LIGHT
        ),
        ThemeItem(
            onClick = { theme = CuteTheme.AMOLED },
            backgroundColor = Color.Black,
            iconColor = Color.White,
            text = R.string.amoled_mode,
            icon = R.drawable.amoled,
            isSelected = theme == CuteTheme.AMOLED
        )
    )
    val fontItems = listOf(
        FontItem(
            onClick = { useSystemFont = false },
            isSelected = !useSystemFont,
            icon = R.drawable.match_case,
            text = R.string.default_text
        ),
        FontItem(
            onClick = { useSystemFont = true },
            isSelected = useSystemFont,
            icon = R.drawable.system_font,
            text = R.string.system
        )
    )

    val paletteItems = listOf(
        CutePaletteStyle.TONAL_SPOT,
        CutePaletteStyle.EXPRESSIVE,
        CutePaletteStyle.VIBRANT,
        CutePaletteStyle.FIDELITY,
        CutePaletteStyle.NEUTRAL,
        CutePaletteStyle.MONOCHROME,
        CutePaletteStyle.FRUIT_SALAD
    )


    Column {
        SettingsWithTitle(
            title = R.string.theme
        ) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                LazyRowWithScrollButton(
                    items = themeItems
                ) { theme ->
                    SettingsSelector(
                        onClick = theme.onClick,
                        icon = theme.icon,
                        text = theme.text,
                        isSelected = theme.isSelected,
                        containerColor = theme.backgroundColor,
                        contentColor = theme.iconColor
                    )
                }
            }
        }
        SettingsWithTitle(
            title = R.string.palette
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
                    items = paletteItems
                ) { palette ->
                    PaletteSelector(
                        isSelected = palette == paletteStyle,
                        onSelectNewPalette = { paletteStyle = palette },
                        paletteStyle = palette
                    )
                }
            }
            SettingsSwitch(
                checked = useMaterialArt,
                onCheckedChange = { useMaterialArt = !useMaterialArt },
                topDp = 4.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.use_art)
            )
        }
        SettingsWithTitle(
            title = R.string.font
        ) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                LazyRowWithScrollButton(
                    items = fontItems
                ) { font ->
                    SettingsSelector(
                        onClick = font.onClick,
                        icon = font.icon,
                        text = font.text,
                        isSelected = font.isSelected
                    )
                }
            }
        }
        SettingsWithTitle(
            title = R.string.cute_searchbar
        ) {
            SettingsSwitch(
                checked = showShuffleButton,
                onCheckedChange = { showShuffleButton = !showShuffleButton },
                topDp = 24.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.show_shuffle_btn)
            )
        }
    }
}

data class ThemeItem(
    val onClick: () -> Unit,
    val backgroundColor: Color,
    val iconColor: Color = Color.White,
    val text: Int,
    val icon: Int,
    val isSelected: Boolean
)

data class FontItem(
    val onClick: () -> Unit,
    val icon: Int,
    val text: Int,
    //val text: @Composable () -> Unit,
    val isSelected: Boolean
)


enum class FontStyle {
    DEFAULT,
    SYSTEM
}