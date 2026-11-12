package com.sosauce.chocola.core.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.core.designsystem.components.SelectOverlayAction
import com.sosauce.chocola.core.designsystem.components.SelectOverlayItem
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.nekobites.utils.groupedShape
import com.sosauce.sweetselect.SweetSelectState


fun LazyListScope.trackItems(
    tracks: List<CuteTrack>,
    multiSelectState: SweetSelectState<CuteTrack>,
    activeTrackId: String,
    source: PlaySource,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    groupContainTracks: Boolean = false,
    extraOptions: @Composable (CuteTrack) -> List<MoreOptions> = { emptyList() },
    extraSelectActions: @Composable (CuteTrack) -> List<SelectOverlayAction> = { emptyList() }
) {
    itemsIndexed(
        items = tracks,
        key = { _, track -> track.mediaId }
    ) { index, track ->
        val isSelected by multiSelectState.isSelectedAsState(track)

        SelectOverlayItem(
            modifier = Modifier
                .then(
                    if (groupContainTracks) {
                        Modifier.padding(horizontal = 10.dp)
                    } else Modifier
                )
                .animateItem(),
            enabled = !multiSelectState.isInSelectionMode,
            actions = trackSelectActions(
                track = track,
                onNavigate = onNavigate,
                onHandlePlayerAction = onHandlePlayerAction,
                extraActions = extraSelectActions(track)
            ),
            onSelect = { multiSelectState.addAll(listOf(track)) }
        ) { showOverlay ->
            val backgroundColor =
                if (groupContainTracks) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background
            val shape = if (groupContainTracks) groupedShape(
                isFirst = index == 0,
                isLast = index == tracks.lastIndex
            ) else RoundedCornerShape(24.dp)

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
                backgroundColor = backgroundColor,
                shape = shape,
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
