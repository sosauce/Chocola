package com.sosauce.chocola.presentation.screens.settings.compenents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberWhitelistedFolders
import com.sosauce.chocola.data.models.Folder
import com.sosauce.chocola.presentation.screens.settings.FoldersViewModel
import com.sosauce.chocola.utils.copyMutate
import org.koin.androidx.compose.koinViewModel


fun LazyListScope.foldersView(
    whitelisted: List<Folder>,
    blacklisted: List<Folder>,
    onBatchEdit: (List<String>) -> Unit,
    onSingleEdit: (String) -> Unit,
) {
    if (whitelisted.isNotEmpty()) {
        item(key = "whitelist_header") {
            FolderHeader(
                modifier = Modifier.animateItem(),
                onClick = {
                    onBatchEdit(
                        whitelisted.fastMap { it.path }
                    )
                },
                text = R.string.whitelisted,
                icon = R.drawable.remove_all_filled
            )
        }
    }
    itemsIndexed(
        items = whitelisted,
        key = { _, folder -> folder.path }
    ) { index, folder ->
        FolderItem(
            folder = folder.path,
            topDp = if (index == 0) 24.dp else 4.dp,
            bottomDp = if (index == whitelisted.lastIndex) 24.dp else 4.dp,
            actionButton = {
                IconButton(
                    onClick = { onSingleEdit(folder.path) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null
                    )
                }
            }
        )
    }
    if (blacklisted.isNotEmpty()) {
        item(key = "blacklist_header") {
            FolderHeader(
                modifier = Modifier.animateItem(),
                onClick = {
                    onBatchEdit(
                        blacklisted.fastMap { it.path }
                    )
                },
                text = R.string.blacklisted,
                icon = R.drawable.add_all_filled
            )
        }
    }
    itemsIndexed(
        items = blacklisted,
        key = { _, folder -> folder.path }
    ) { index, folder ->
        FolderItem(
            folder = folder.path,
            topDp = if (index == 0) 24.dp else 4.dp,
            bottomDp = if (index == blacklisted.lastIndex) 24.dp else 4.dp,
            actionButton = {
                IconButton(
                    onClick = { onSingleEdit(folder.path) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Composable
private fun FolderHeader(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Int,
    text: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 34.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(text),
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
        }
    }
}