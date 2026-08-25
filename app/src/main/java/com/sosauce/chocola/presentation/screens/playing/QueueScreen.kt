@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalUuidApi::class)

package com.sosauce.chocola.presentation.screens.playing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlaySource
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.MusicListItem
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.utils.selfAlignHorizontally
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun QueueScreen(
    musicState: MusicState,
    onNavigateBack: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {

    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val activeTrackId = remember(musicState.track) { musicState.track.mediaId }
    val queueItems = remember {
        musicState.loadedMedias.fastMap {
            QueueItem(
                id = Random.nextInt(),
                track = it
            )
        }.toMutableStateList()
    }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onHandlePlayerAction(
            PlayerActions.ReArrangeQueue(from.index, to.index)
        )
        val itemToMove = queueItems[from.index]
        queueItems.removeAt(from.index)
        queueItems.add(to.index, itemToMove)

        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)

    }

    Scaffold(
        bottomBar = {
            AnimatedFab(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .navigationBarsPadding()
                    .selfAlignHorizontally(Alignment.Start),
                icon = R.drawable.back,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = queueItems,
                key = { it.id }
            ) { item ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = item.id
                ) { isDragging ->
                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) 1.01f else 1f
                    )
                    MusicListItem(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                        track = item.track,
                        onShortClick = {
                            onHandlePlayerAction(
                                PlayerActions.PlayFromSource(
                                    mediaId = item.track.mediaId,
                                    source = PlaySource.ExplicitTracks(musicState.loadedMedias)
                                )
                            )
                        },
                        isActive = item.track.mediaId == activeTrackId,
                        trailingContent = {
                            if (!musicState.shuffle) {
                                IconButton(
                                    onClick = {
                                        onHandlePlayerAction(PlayerActions.RemoveFromQueue(item.track))
                                        queueItems.remove(item)
                                              },
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = {},
                                    shapes = IconButtonDefaults.shapes(),
                                    modifier = Modifier.draggableHandle()
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.drag_handle),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


data class QueueItem(
    val id: Int,
    val track: CuteTrack
)