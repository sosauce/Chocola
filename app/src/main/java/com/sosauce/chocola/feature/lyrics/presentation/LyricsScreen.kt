@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.lyrics.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.vibrantFloatingToolbarColors
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.PlayPauseButton
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.nekobites.animations.AnimatedFab

@Composable
fun LyricsScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    Scaffold(
        bottomBar = {
            HorizontalFloatingToolbar(
                expanded = true,
                colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                modifier = Modifier
                    .selfAlignHorizontally()
                    .navigationBarsPadding(),
                floatingActionButton = {
                    AnimatedFab(
                        onClick = onNavigateBack,
                        icon = R.drawable.close,
                        containerColor = vibrantFloatingToolbarColors().fabContainerColor
                    )
                }
            ) {
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_previous),
                        contentDescription = stringResource(R.string.previous_song)
                    )
                }
                PlayPauseButton(
                    isPlaying = musicState.isPlaying,
                    onHandlePlayerActions = onHandlePlayerActions
                )
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = stringResource(R.string.next_song)
                    )
                }
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(Modifier.padding(paddingValues).fillMaxSize()) {
            LyricsList(
                modifier = if (musicState.lyrics.isNotEmpty()) {
                    Modifier
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .drawWithContent {
                            drawContent()
                            val fadeHeight = size.height * 0.14f
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    1f to Color.Black,
                                    endY = fadeHeight
                                ),
                                blendMode = BlendMode.DstIn
                            )
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.Black,
                                    1f to Color.Transparent,
                                    startY = size.height - fadeHeight,
                                    endY = size.height
                                ),
                                blendMode = BlendMode.DstIn
                            )
                        }
                } else Modifier,
                contentPadding = if (musicState.lyrics.isNotEmpty()) {
                    PaddingValues(vertical = maxHeight / 2)
                } else PaddingValues(0.dp),
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions,
                emptyLyrics = {
                    DefaultEmptyLyricsScreen(
                        onNavigate = onNavigate,
                        musicState = musicState,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                }
            )
        }
    }

}

