@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.dialogs.DeletionDialog
import com.sosauce.chocola.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.chocola.utils.rememberInteractionSource
import com.sosauce.sweetselect.SweetSelectState

@Composable
fun <T> SelectedBarSurface(
    modifier: Modifier = Modifier,
    items: List<T>,
    multiSelectState: SweetSelectState<T>,
    actions: @Composable (RowScope.() -> Unit)
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth(0.85f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = multiSelectState::clearSelected,
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "${multiSelectState.selectedItems.size}"
                )
            }
            Button(
                onClick = {
                    if (multiSelectState.selectedItems.size == items.size) {
                        multiSelectState.clearSelected()
                    } else {
                        multiSelectState.toggleAll(items)
                    }
                },
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )

            ) {

                val icon =
                    if (items.size == multiSelectState.selectedItems.size) R.drawable.unselect_all else R.drawable.select_all

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null
                )
                Spacer(Modifier.width(5.dp))

                val text =
                    if (items.size == multiSelectState.selectedItems.size) R.string.unselect_all else R.string.select_all

                Text(stringResource(text))
            }
        }
        Spacer(Modifier.height(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) { actions() }
    }
}

@Composable
fun TracksSelectedBar(
    modifier: Modifier = Modifier,
    tracks: List<CuteTrack>,
    multiSelectState: SweetSelectState<CuteTrack>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSources = List(3) { rememberInteractionSource() }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = multiSelectState.selectedItems.map { it.mediaId },
            onDismissRequest = { showPlaylistDialog = false },
            onAddingFinished = multiSelectState::clearSelected
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = multiSelectState.selectedItems.toList(),
            onDismissRequest = { showDeletionDialog = false }
        )
    }


    SelectedBarSurface(
        modifier = modifier,
        items = tracks,
        multiSelectState = multiSelectState
    ) {

        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            overflowIndicator = {}
        ) {
            customItem(
                buttonGroupContent = {
                    Button(
                        onClick = { showPlaylistDialog = true },
                        interactionSource = interactionSources[0],
                        shape = RoundedCornerShape(
                            topStart = 50.dp,
                            bottomStart = 50.dp,
                            topEnd = 4.dp,
                            bottomEnd = 4.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                        ),
                        modifier = Modifier
                            .animateWidth(interactionSources[0])
                            .weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.playlist_add),
                            contentDescription = null
                        )
                    }
                },
                menuContent = {}
            )

            customItem(
                buttonGroupContent = {
                    Button(
                        onClick = {
                            onHandlePlayerActions(PlayerActions.AddToQueue(multiSelectState.selectedItems.toList()))
                            multiSelectState.clearSelected()
                        },
                        interactionSource = interactionSources[1],
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                        ),
                        modifier = Modifier
                            .animateWidth(interactionSources[1])
                            .weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add_to_queue),
                            contentDescription = null
                        )
                    }
                },
                menuContent = {}
            )

            customItem(
                buttonGroupContent = {
                    Button(
                        onClick = { showDeletionDialog = true },
                        interactionSource = interactionSources[2],
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            bottomStart = 4.dp,
                            topEnd = 50.dp,
                            bottomEnd = 50.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                        ),
                        modifier = Modifier
                            .animateWidth(interactionSources[2])
                            .weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash_rounded_filled),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                menuContent = {}
            )
        }
    }
}