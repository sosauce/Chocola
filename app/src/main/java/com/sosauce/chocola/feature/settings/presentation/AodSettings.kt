package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sosauce.chocola.R
import com.sosauce.chocola.core.presentation.preferences.rememberAodEnableGestures
import com.sosauce.chocola.core.presentation.preferences.rememberAodShowControls
import com.sosauce.nekobites.components.settings.SettingsClickable
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsSwitch

@Composable
fun ColumnScope.AodSettings(
    onToggleAod: (Boolean) -> Unit
) {
    var enableGestures by rememberAodEnableGestures()
    var showControls by rememberAodShowControls()

    SettingsGroup {
        SettingsClickable(
            title = stringResource(R.string.start_aod),
            icon = R.drawable.brightness_medium,
            onClick = { onToggleAod(true) }
        )
    }

    SettingsGroup {
        SettingsSwitch(
            title = stringResource(R.string.enable_gestures),
            description = stringResource(R.string.enable_gestures_desc),
            checked = enableGestures,
            onCheckedChange = { enableGestures = !enableGestures }
        )
        SettingsSwitch(
            title = stringResource(R.string.show_controls),
            checked = showControls,
            onCheckedChange = { showControls = !showControls }
        )
    }
}
