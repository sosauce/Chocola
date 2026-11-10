@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.presentation.components

import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.SelectOverlayAction
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.dialogs.DeletionDialog
import com.sosauce.nekobites.animations.AnimatedDrawable
import com.sosauce.nekobites.animations.AnimatedDrawableFile

@Composable
fun collectionSelectActions(
    tracks: List<CuteTrack>,
    source: PlaySource,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
): List<SelectOverlayAction> {
    return rememberCollectionOptions(
        tracks = tracks,
        source = source,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onHandlePlayerActions = onHandlePlayerActions
    ).fastMap { option ->
        SelectOverlayAction(
            label = stringResource(option.text),
            icon = option.icon,
            isDestructive = option.isDestructive,
            onClick = option.onClick
        )
    }
}

@Composable
fun CollectionMoreButton(
    tracks: List<CuteTrack>,
    source: PlaySource,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val options = rememberCollectionOptions(
        tracks = tracks,
        source = source,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onHandlePlayerActions = onHandlePlayerActions
    )

    IconButton(
        onClick = { isExpanded = true },
        shapes = IconButtonDefaults.shapes()
    ) {
        AnimatedDrawable(
            drawable = AnimatedDrawableFile.MORE_VERT,
            atEnd = isExpanded
        )
    }

    DropdownMenuPopup(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false }
    ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShapes()
        ) {
            options.fastForEachIndexed { index, option ->
                val tint =
                    if (option.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        option.onClick()
                    },
                    shape = when (index) {
                        0 -> MenuDefaults.leadingItemShape
                        options.lastIndex -> MenuDefaults.trailingItemShape
                        else -> MenuDefaults.middleItemShape
                    },
                    text = { Text(text = stringResource(option.text), color = tint) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(option.icon),
                            contentDescription = null,
                            tint = tint
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun rememberCollectionOptions(
    tracks: List<CuteTrack>,
    source: PlaySource,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
): List<CollectionOption> {
    var showPlaylistPicker by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }

    if (showPlaylistPicker) {
        PlaylistPicker(
            mediaId = tracks.fastMap { it.mediaId },
            onDismissRequest = { showPlaylistPicker = false }
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = tracks,
            onDismissRequest = { showDeletionDialog = false }
        )
    }

    return listOf(
        CollectionOption(R.string.shuffle, R.drawable.shuffle) {
            onHandlePlayerActions(PlayerActions.PlayFromSource(mediaId = null, source = source))
        },
        CollectionOption(R.string.add_queue, R.drawable.add_to_queue) {
            onHandlePlayerActions(PlayerActions.AddToQueue(tracks))
        },
        CollectionOption(R.string.add_to_playlist, R.drawable.playlist_add) {
            showPlaylistPicker = true
        },
        CollectionOption(
            text = if (isFavorite) R.string.remove_favorite else R.string.add_favorite,
            icon = if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite,
            onClick = onToggleFavorite
        ),
        CollectionOption(R.string.delete, R.drawable.trash_rounded_filled, isDestructive = true) {
            showDeletionDialog = true
        }
    )
}

private class CollectionOption(
    val text: Int,
    val icon: Int,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)
