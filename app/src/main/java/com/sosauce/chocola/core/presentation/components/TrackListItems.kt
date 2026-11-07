package com.sosauce.chocola.core.presentation.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sosauce.chocola.core.designsystem.components.SelectOverlayAction
import com.sosauce.chocola.core.designsystem.components.SelectOverlayItem
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.sweetselect.SweetSelectState


fun LazyListScope.trackItems(
    tracks: List<CuteTrack>,
    multiSelectState: SweetSelectState<CuteTrack>,
    activeTrackId: String,
    source: PlaySource,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    extraOptions: @Composable (CuteTrack) -> List<MoreOptions> = { emptyList() },
    extraSelectActions: @Composable (CuteTrack) -> List<SelectOverlayAction> = { emptyList() }
) {
    items(
        items = tracks,
        key = { it.mediaId }
    ) { track ->
        val isSelected by multiSelectState.isSelectedAsState(track)

        SelectOverlayItem(
            modifier = Modifier.animateItem(),
            enabled = !multiSelectState.isInSelectionMode,
            actions = trackSelectActions(
                track = track,
                onNavigate = onNavigate,
                onHandlePlayerAction = onHandlePlayerAction,
                extraActions = extraSelectActions(track)
            ),
            onSelect = { multiSelectState.addAll(listOf(track)) }
        ) { showOverlay ->
            MusicListItem(
                onShortClick = {
                    if (multiSelectState.isInSelectionMode) {
                        multiSelectState.toggle(track)
                    } else {
                        onHandlePlayerAction(
                            PlayerActions.PlayFromSource(
                                mediaId = track.mediaId,
                                source = source
                            )
                        )
                    }
                },
                onLongClick = {
                    if (multiSelectState.isInSelectionMode) {
                        multiSelectState.toggle(track)
                    } else {
                        showOverlay()
                    }
                },
                track = track,
                isSelected = isSelected,
                isActive = track.mediaId == activeTrackId,
                trailingContent = {
                    DefaultMusicListItemTrailingContent(
                        track = track,
                        onNavigate = onNavigate,
                        onHandlePlayerActions = onHandlePlayerAction,
                        extraOptions = extraOptions(track)
                    )
                }
            )
        }
    }
}
