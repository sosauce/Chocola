@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.lyrics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.res.painterResource
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.chocola.utils.selfAlignHorizontally
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
                        contentDescription = null
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
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        LyricsList(
            contentPadding = paddingValues,
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

