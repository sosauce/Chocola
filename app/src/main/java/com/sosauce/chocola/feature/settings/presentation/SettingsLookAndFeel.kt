@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.CutePaletteStyle
import com.sosauce.chocola.core.designsystem.CuteTheme
import com.sosauce.chocola.core.designsystem.theme.anyDarkColorScheme
import com.sosauce.chocola.core.designsystem.theme.anyLightColorScheme
import com.sosauce.chocola.core.presentation.preferences.rememberAppTheme
import com.sosauce.chocola.core.presentation.preferences.rememberPaletteStyle
import com.sosauce.chocola.core.presentation.preferences.rememberShowShuffleButton
import com.sosauce.chocola.core.presentation.preferences.rememberUseArtTheme
import com.sosauce.chocola.core.presentation.preferences.rememberUseSystemFont
import com.sosauce.chocola.feature.settings.presentation.components.PaletteTile
import com.sosauce.nekobites.components.settings.SelectableTile
import com.sosauce.nekobites.components.settings.SettingsClickable
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsPicker
import com.sosauce.nekobites.components.settings.SettingsSwitch

/** Look & feel page content (appearance + now playing). Meant to be placed in a SettingsPage. */
@Composable
fun ColumnScope.SettingsLookAndFeel(
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

    val themes = listOf(
        ThemeOption(
            value = CuteTheme.SYSTEM,
            label = R.string.system,
            icon = R.drawable.system_theme,
            background = if (isSystemDark) anyDark.surfaceContainerHighest else anyLight.surfaceContainerHighest,
            iconColor = if (isSystemDark) anyDark.onSurface else anyLight.onSurface
        ),
        ThemeOption(
            value = CuteTheme.DARK,
            label = R.string.dark_mode,
            icon = R.drawable.dark_mode,
            background = anyDark.surfaceContainerHighest,
            iconColor = anyDark.onSurface
        ),
        ThemeOption(
            value = CuteTheme.LIGHT,
            label = R.string.light_mode,
            icon = R.drawable.light_mode,
            background = anyLight.surfaceContainerHighest,
            iconColor = anyLight.onSurface
        ),
        ThemeOption(
            value = CuteTheme.AMOLED,
            label = R.string.amoled_mode,
            icon = R.drawable.amoled,
            background = Color.Black,
            iconColor = Color.White
        )
    )

    val palettes = listOf(
        CutePaletteStyle.TONAL_SPOT,
        CutePaletteStyle.EXPRESSIVE,
        CutePaletteStyle.VIBRANT,
        CutePaletteStyle.FIDELITY,
        CutePaletteStyle.NEUTRAL,
        CutePaletteStyle.MONOCHROME,
        CutePaletteStyle.FRUIT_SALAD
    )

    SettingsGroup(title = stringResource(R.string.appearance)) {
        SettingsPicker(title = stringResource(R.string.theme)) {
            items(themes) { option ->
                SelectableTile(
                    selected = theme == option.value,
                    onClick = { theme = option.value },
                    label = stringResource(option.label),
                    containerColor = option.background
                ) {
                    Icon(
                        painter = painterResource(option.icon),
                        contentDescription = null,
                        tint = option.iconColor
                    )
                }
            }
        }
        SettingsPicker(title = stringResource(R.string.palette)) {
            items(palettes) { palette ->
                PaletteTile(
                    paletteStyle = palette,
                    selected = palette == paletteStyle,
                    onClick = { paletteStyle = palette }
                )
            }
        }
        SettingsPicker(title = stringResource(R.string.font)) {
            item {
                FontTile(
                    selected = !useSystemFont,
                    onClick = { useSystemFont = false },
                    icon = R.drawable.match_case,
                    label = R.string.default_text
                )
            }
            item {
                FontTile(
                    selected = useSystemFont,
                    onClick = { useSystemFont = true },
                    icon = R.drawable.system_font,
                    label = R.string.system
                )
            }
        }
        SettingsSwitch(
            title = stringResource(R.string.use_art),
            checked = useMaterialArt,
            onCheckedChange = { useMaterialArt = !useMaterialArt }
        )
        SettingsSwitch(
            title = stringResource(R.string.show_shuffle_btn),
            checked = showShuffleButton,
            onCheckedChange = { showShuffleButton = !showShuffleButton }
        )
    }

    SettingsGroup(title = stringResource(R.string.now_playing)) {
        SettingsClickable(
            title = stringResource(R.string.now_playing_buttons),
            description = stringResource(R.string.now_playing_buttons_desc),
            icon = R.drawable.unhide,
            onClick = onNavigateToNowPlayingButtons
        )
    }

    SettingsNowPlaying()
}

@Composable
private fun FontTile(
    selected: Boolean,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    @StringRes label: Int
) {
    SelectableTile(
        selected = selected,
        onClick = onClick,
        label = stringResource(label)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(26.dp)
        )
    }
}

private data class ThemeOption(
    val value: String,
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    val background: Color,
    val iconColor: Color
)
