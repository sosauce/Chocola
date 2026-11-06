package com.sosauce.chocola.feature.settings.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import java.io.File

@Composable
fun FolderItem(
    modifier: Modifier = Modifier,
    folder: String,
    topDp: Dp,
    bottomDp: Dp,
    actionButton: @Composable () -> Unit
) {
    SettingsCard(
        modifier = modifier,
        topDp = topDp,
        bottomDp = bottomDp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.folder_rounded),
                contentDescription = null,
                modifier = Modifier.size(33.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(File(folder).name)
                Text(
                    text = folder,
                    style = MaterialTheme.typography.labelSmallEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }
            actionButton()
        }
    }
}