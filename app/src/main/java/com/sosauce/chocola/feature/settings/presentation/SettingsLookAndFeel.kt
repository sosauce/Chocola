@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.sosauce.chocola.core.presentation.preferences.rememberAppTheme
import com.sosauce.chocola.core.presentation.preferences.rememberPaletteStyle
import com.sosauce.chocola.core.presentation.preferences.rememberShowShuffleButton
import com.sosauce.chocola.core.presentation.preferences.rememberUseArtTheme
import com.sosauce.chocola.core.presentation.preferences.rememberUseSystemFont
import com.sosauce.chocola.feature.settings.presentation.components.PaletteSelector
import com.sosauce.chocola.feature.settings.presentation.components.SettingsCard
import com.sosauce.chocola.feature.settings.presentation.components.SettingsCardHeader
import com.sosauce.chocola.feature.settings.presentation.components.SettingsCategoryCard
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSelector
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSwitch
import com.sosauce.chocola.feature.settings.presentation.components.SettingsWithTitle
import com.sosauce.chocola.core.designsystem.theme.anyDarkColorScheme
import com.sosauce.chocola.core.designsystem.theme.anyLightColorScheme
import com.sosauce.chocola.core.designsystem.CutePaletteStyle
import com.sosauce.chocola.core.designsystem.CuteTheme
import com.sosauce.nekobites.components.LazyRowWithScrollButton

@Composable
fun SettingsLookAndFeel(
    onNavigateToNowPlayingButtons: () -> Unit
) {
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
            title = R.string.appearance
        ) {
            SettingsCard(
                topDp = 24.dp,
                bottomDp = 2.dp
            ) {
                SettingsCardHeader(R.string.theme)
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
            SettingsCard(
                topDp = 2.dp,
                bottomDp = 2.dp
            ) {
                SettingsCardHeader(R.string.palette)
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
            SettingsCard(
                topDp = 2.dp,
                bottomDp = 2.dp
            ) {
                SettingsCardHeader(R.string.font)
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
            SettingsSwitch(
                checked = useMaterialArt,
                onCheckedChange = { useMaterialArt = !useMaterialArt },
                topDp = 2.dp,
                bottomDp = 2.dp,
                text = stringResource(R.string.use_art)
            )
            SettingsSwitch(
                checked = showShuffleButton,
                onCheckedChange = { showShuffleButton = !showShuffleButton },
                topDp = 2.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.show_shuffle_btn)
            )
        }
        SettingsWithTitle(
            title = R.string.now_playing
        ) {
            SettingsCategoryCard(
                icon = R.drawable.unhide,
                name = stringResource(R.string.now_playing_buttons),
                description = stringResource(R.string.now_playing_buttons_desc),
                topDp = 24.dp,
                bottomDp = 24.dp,
                onNavigate = onNavigateToNowPlayingButtons
            )
            Spacer(Modifier.height(10.dp))
            SettingsNowPlaying()
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