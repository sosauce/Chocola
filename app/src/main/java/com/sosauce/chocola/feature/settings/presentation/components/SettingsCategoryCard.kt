package com.sosauce.chocola.feature.settings.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun SettingsCategoryCard(
    icon: Int,
    name: String,
    description: String,
    topDp: Dp,
    bottomDp: Dp,
    onNavigate: () -> Unit
) {
    SettingsCard(
        topDp = topDp,
        bottomDp = bottomDp,
        onClick = onNavigate
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
            Spacer(Modifier.width(15.dp))
            Column {
                Text(name)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}