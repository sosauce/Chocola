package com.sosauce.chocola.feature.settings.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sosauce.chocola.R
import com.sosauce.nekobites.components.settings.SettingsClickable
import com.sosauce.nekobites.components.settings.SettingsGroup

@Composable
fun ColumnScope.SettingsBackup(
    onBackupAction: (BackupAction) -> Unit
) {
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { onBackupAction(BackupAction.Export(it)) }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onBackupAction(BackupAction.Import(it)) }
    }

    SettingsGroup {
        SettingsClickable(
            title = stringResource(R.string.export_settings),
            description = stringResource(R.string.export_settings_desc),
            icon = R.drawable.export,
            onClick = { exportLauncher.launch("chocola_backup.json") }
        )
        SettingsClickable(
            title = stringResource(R.string.import_settings),
            description = stringResource(R.string.import_settings_desc),
            icon = R.drawable.backup,
            onClick = { importLauncher.launch(arrayOf("application/json")) }
        )
    }
}
