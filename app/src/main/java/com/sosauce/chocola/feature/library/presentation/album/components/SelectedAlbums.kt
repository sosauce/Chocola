@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.SelectedBarSurface
import com.sosauce.chocola.core.presentation.components.dialogs.DeletionDialog
import com.sosauce.nekobites.helpers.rememberInteractionSource
import com.sosauce.sweetselect.SweetSelectState

@Composable
fun SelectedAlbums(
    modifier: Modifier = Modifier,
    albums: List<Album>,
    multiSelectState: SweetSelectState<Album>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    var showDeletionDialog by remember { mutableStateOf(false) }
    val interactionSources = List(2) { rememberInteractionSource() }
    val selectedAlbums = multiSelectState.selectedItems.toList()
    val allFavorite = selectedAlbums.isNotEmpty() && selectedAlbums.all { it.isFavorite }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = selectedAlbums.flatMap { it.tracks },
            onDismissRequest = {
                showDeletionDialog = false
                multiSelectState.clearSelected()
            }
        )
    }

    SelectedBarSurface(
        modifier = modifier,
        items = albums,
        multiSelectState = multiSelectState
    ) {
        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            overflowIndicator = {}
        ) {
            customItem(
                buttonGroupContent = {
                    Button(
                        onClick = {
                            val shouldFavorite = !allFavorite
                            selectedAlbums
                                .filter { it.isFavorite != shouldFavorite }
                                .forEach { onHandlePlayerActions(PlayerActions.ToggleAlbumFavorite(it.id)) }
                            multiSelectState.clearSelected()
                        },
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
                            painter = painterResource(
                                if (allFavorite) R.drawable.favorite_filled else R.drawable.favorite
                            ),
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
                        interactionSource = interactionSources[1],
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
                            .animateWidth(interactionSources[1])
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
