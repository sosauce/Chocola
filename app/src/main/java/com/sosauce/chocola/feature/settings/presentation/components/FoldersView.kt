package com.sosauce.chocola.feature.settings.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.Folder
import com.sosauce.nekobites.components.settings.SettingsGroupLabel
import com.sosauce.nekobites.components.settings.SettingsIconBadge
import com.sosauce.nekobites.components.settings.SettingsItem
import com.sosauce.nekobites.components.settings.settingsItemShape
import java.io.File

private val FolderItemModifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 2.dp)

fun LazyListScope.foldersView(
    whitelisted: List<Folder>,
    blacklisted: List<Folder>,
    onBlacklist: (List<String>) -> Unit,
    onWhitelist: (List<String>) -> Unit
) {
    folderSection(
        key = "whitelist",
        folders = whitelisted,
        title = R.string.whitelisted,
        bulkIcon = R.drawable.remove_all_filled,
        bulkDescription = R.string.blacklist,
        itemIcon = R.drawable.close,
        itemDescription = R.string.blacklist,
        onMove = onBlacklist
    )
    folderSection(
        key = "blacklist",
        folders = blacklisted,
        title = R.string.blacklisted,
        bulkIcon = R.drawable.add_all_filled,
        bulkDescription = R.string.whitelist,
        itemIcon = R.drawable.add,
        itemDescription = R.string.whitelist,
        onMove = onWhitelist
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.folderSection(
    key: String,
    folders: List<Folder>,
    @StringRes title: Int,
    @DrawableRes bulkIcon: Int,
    @StringRes bulkDescription: Int,
    @DrawableRes itemIcon: Int,
    @StringRes itemDescription: Int,
    onMove: (List<String>) -> Unit
) {
    if (folders.isEmpty()) return

    item(key = "${key}_label") {
        SettingsGroupLabel(
            text = stringResource(title),
            modifier = Modifier.animateItem()
        ) {
            FilledTonalIconButton(
                onClick = { onMove(folders.fastMap { it.path }) },
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(bulkIcon),
                    contentDescription = stringResource(bulkDescription)
                )
            }
        }
    }
    itemsIndexed(
        items = folders,
        key = { _, folder -> "${key}_${folder.path}" }
    ) { index, folder ->
        SettingsItem(
            baseShape = settingsItemShape(
                isFirst = index == 0,
                isLast = index == folders.lastIndex
            ),
            modifier = FolderItemModifier.animateItem()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)
            ) {
                SettingsIconBadge(
                    icon = R.drawable.folder_rounded,
                    shape = MaterialShapes.Square.toShape()
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = File(folder.path).name,
                        style = MaterialTheme.typography.titleMediumEmphasized
                    )
                    Text(
                        text = folder.path,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )
                }
                IconButton(
                    onClick = { onMove(listOf(folder.path)) },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        painter = painterResource(itemIcon),
                        contentDescription = stringResource(itemDescription)
                    )
                }
            }
        }
    }
}
