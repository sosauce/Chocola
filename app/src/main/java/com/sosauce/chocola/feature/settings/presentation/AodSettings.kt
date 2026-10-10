package com.sosauce.chocola.feature.settings.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.preferences.rememberAodEnableGestures
import com.sosauce.chocola.core.presentation.preferences.rememberAodShowControls
import com.sosauce.chocola.feature.playing.presentation.aod.AlwaysOnDisplay
import com.sosauce.chocola.feature.settings.presentation.components.ClickableSettingsCard
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSwitch
import com.sosauce.chocola.feature.settings.presentation.components.SettingsWithTitle
import com.sosauce.nekobites.components.Spacer

@Composable
fun AodSettings(
    onToggleAod: (Boolean) -> Unit
) {

    var enableGestures by rememberAodEnableGestures()
    var showControls by rememberAodShowControls()
    SettingsWithTitle(
        title = R.string.aod
    ) {
        ClickableSettingsCard(
            onClick = { onToggleAod(true) },
            topDp = 24.dp,
            bottomDp = 24.dp,
            text = stringResource(R.string.start_aod)
        )
        Spacer(Modifier.height(10.dp))
        SettingsSwitch(
            checked = enableGestures,
            onCheckedChange = { enableGestures = !enableGestures },
            topDp = 24.dp,
            bottomDp = 2.dp,
            text = stringResource(R.string.enable_gestures),
            optionalDescription = R.string.enable_gestures_desc
        )
        SettingsSwitch(
            checked = showControls,
            onCheckedChange = { showControls = !showControls },
            topDp = 2.dp,
            bottomDp = 24.dp,
            text = stringResource(R.string.show_controls)
        )
    }

}