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
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.preferences.rememberInitialScreen
import com.sosauce.nekobites.components.settings.SelectableTile
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsPicker

@Composable
fun ColumnScope.SettingsNavigation() {
    var initialScreen by rememberInitialScreen()

    val screens = listOf(
        LaunchScreenOption(Screen.Main.toString(), R.drawable.music_note, R.string.main),
        LaunchScreenOption(Screen.Albums.toString(), R.drawable.album_filled, R.string.albums),
        LaunchScreenOption(Screen.Artists.toString(), R.drawable.artists_filled, R.string.artists),
        LaunchScreenOption(Screen.Playlists.toString(), R.drawable.playlist, R.string.playlists)
    )

    SettingsGroup(title = stringResource(R.string.default_launch_screen)) {
        SettingsPicker {
            items(screens) { screen ->
                val selected = initialScreen == screen.value
                SelectableTile(
                    selected = selected,
                    onClick = { initialScreen = screen.value },
                    label = stringResource(screen.label),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Icon(
                        painter = painterResource(screen.icon),
                        contentDescription = null,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}

private data class LaunchScreenOption(
    val value: String,
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int
)
