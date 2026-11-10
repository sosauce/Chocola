@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.dialogs.DeletionDialog
import com.sosauce.nekobites.helpers.rememberInteractionSource
import com.sosauce.sweetselect.SweetSelectState

@Composable
fun <T> CollectionSelectedBar(
    modifier: Modifier = Modifier,
    items: List<T>,
    multiSelectState: SweetSelectState<T>,
    tracksOf: (T) -> List<CuteTrack>,
    isFavoriteOf: (T) -> Boolean,
    onToggleFavorite: (T) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    var showPlaylistPicker by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    val interactionSources = List(4) { rememberInteractionSource() }
    val selectedItems = multiSelectState.selectedItems.toList()
    val selectedTracks = selectedItems.flatMap(tracksOf)
    val allFavorite = selectedItems.isNotEmpty() && selectedItems.all(isFavoriteOf)
    val errorColor = MaterialTheme.colorScheme.error

    if (showPlaylistPicker) {
        PlaylistPicker(
            mediaId = selectedTracks.map { it.mediaId },
            onDismissRequest = { showPlaylistPicker = false },
            onAddingFinished = multiSelectState::clearSelected
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = selectedTracks,
            onDismissRequest = {
                showDeletionDialog = false
                multiSelectState.clearSelected()
            }
        )
    }

    SelectedBarSurface(
        modifier = modifier,
        items = items,
        multiSelectState = multiSelectState
    ) {
        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            overflowIndicator = {}
        ) {
            selectedBarButton(
                icon = R.drawable.playlist_add,
                interactionSource = interactionSources[0],
                shape = RoundedCornerShape(
                    topStart = 50.dp,
                    bottomStart = 50.dp,
                    topEnd = 4.dp,
                    bottomEnd = 4.dp
                ),
                onClick = { showPlaylistPicker = true }
            )
            selectedBarButton(
                icon = R.drawable.add_to_queue,
                interactionSource = interactionSources[1],
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    onHandlePlayerActions(PlayerActions.AddToQueue(selectedTracks))
                    multiSelectState.clearSelected()
                }
            )
            selectedBarButton(
                icon = if (allFavorite) R.drawable.favorite_filled else R.drawable.favorite,
                interactionSource = interactionSources[2],
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    val shouldFavorite = !allFavorite
                    selectedItems
                        .filter { isFavoriteOf(it) != shouldFavorite }
                        .forEach(onToggleFavorite)
                    multiSelectState.clearSelected()
                }
            )
            selectedBarButton(
                icon = R.drawable.trash_rounded_filled,
                interactionSource = interactionSources[3],
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    bottomStart = 4.dp,
                    topEnd = 50.dp,
                    bottomEnd = 50.dp
                ),
                tint = errorColor,
                onClick = { showDeletionDialog = true }
            )
        }
    }
}

private fun ButtonGroupScope.selectedBarButton(
    icon: Int,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    onClick: () -> Unit,
    tint: Color? = null
) {
    customItem(
        buttonGroupContent = {
            Button(
                onClick = onClick,
                interactionSource = interactionSource,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                ),
                modifier = Modifier
                    .animateWidth(interactionSource)
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = tint ?: contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )
            }
        },
        menuContent = {}
    )
}
