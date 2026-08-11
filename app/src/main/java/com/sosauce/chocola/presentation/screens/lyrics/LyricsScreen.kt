@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.lyrics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.vibrantFloatingToolbarColors
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.presentation.components.animations.AnimatedIconButton
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.chocola.utils.selfAlignHorizontally

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
                AnimatedIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                    icon = R.drawable.skip_previous,
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_back_description)
                )
                PlayPauseButton(
                    isPlaying = musicState.isPlaying,
                    onHandlePlayerActions = onHandlePlayerActions
                )
                AnimatedIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                    icon = R.drawable.skip_next,
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_to_next_description)
                )
            }
        }
    ) { paddingValues ->
        LyricsList(
            contentPadding = paddingValues,
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions,
            emptyLyrics = {
                DefaultEmptyLyricsScreen(
                    musicState = musicState,
                    onNavigate = onNavigate,
                    onHandlePlayerActions = onHandlePlayerActions
                )
            }
        )
    }

}

