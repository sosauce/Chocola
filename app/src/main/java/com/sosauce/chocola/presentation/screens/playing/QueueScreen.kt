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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.MusicListItem
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.utils.selfAlignHorizontally
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun QueueScreen(
    musicState: MusicState,
    onNavigateUp: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onHandlePlayerAction(
            PlayerActions.ReArrangeQueue(from.index, to.index)
        )
    }

    Scaffold(
        bottomBar = {
            AnimatedFab(
                onClick = onNavigateUp,
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
                items = musicState.loadedMedias,
                key = { it.mediaItem.mediaId }
            ) { track ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = track.mediaItem.mediaId
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
                        track = track,
                        musicState = musicState,
                        onShortClick = {
                            onHandlePlayerAction(
                                PlayerActions.Play(
                                    index = musicState.loadedMedias.indexOf(track),
                                    tracks = musicState.loadedMedias
                                )
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = { onHandlePlayerAction(PlayerActions.RemoveFromQueue(track)) },
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
                    )
                }
            }
        }
    }
}