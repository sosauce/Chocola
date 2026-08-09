@file:OptIn(ExperimentalUuidApi::class)

package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.settings.compenents.AboutCard
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsCategoryCard
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsScreens
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedFab
import com.sosauce.chocola.utils.navigateBack
import com.sosauce.chocola.utils.navigationBouncySpec
import com.sosauce.chocola.utils.selfAlignHorizontally
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    val scrollState = rememberScrollState()
    val backStack = rememberNavBackStack(SettingsScreens.Settings)
    val items = listOf(
        Item(
            icon = R.drawable.palette,
            name = stringResource(R.string.look_and_feel),
            description = stringResource(R.string.look_and_feel_desc),
            onNavigate = { backStack.add(SettingsScreens.LookAndFeel) }
        ),
        Item(
            icon = R.drawable.music_note,
            name = stringResource(R.string.now_playing),
            description = stringResource(R.string.now_playing_desc),
            onNavigate = { backStack.add(SettingsScreens.NowPlaying) }
        ),
        Item(
            icon = R.drawable.navigation,
            name = stringResource(R.string.navigation),
            description = stringResource(R.string.navigation_desc),
            onNavigate = { backStack.add(SettingsScreens.Navigation) }
        ),
        Item(
            icon = R.drawable.lyrics_rounded,
            name = stringResource(R.string.lyrics),
            description = stringResource(R.string.lyrics_settings_desc),
            onNavigate = { backStack.add(SettingsScreens.Lyrics) }
        ),
        Item(
            icon = R.drawable.headphones,
            name = stringResource(R.string.playback_controls),
            description = stringResource(R.string.playback_controls_desc),
            onNavigate = { backStack.add(SettingsScreens.Playback) }
        ),
        Item(
            icon = R.drawable.library,
            name = stringResource(R.string.library),
            description = stringResource(R.string.library_desc),
            onNavigate = { backStack.add(SettingsScreens.Library) }
        ),
//        Item(
//            icon = rememberVectorPainter(Icons.Outlined.MoreHoriz),
//            name = stringResource(R.string.more),
//            description = stringResource(R.string.more_desc),
//            onNavigate = { backStack.add() }
//        )

    )

    Scaffold(
        bottomBar = {
            AnimatedFab(
                onClick = {
                    if (backStack.size == 1) {
                        onNavigateUp()
                    } else {
                        backStack.navigateBack()
                    }
                },
                modifier = Modifier
                    .padding(start = 15.dp)
                    .navigationBarsPadding()
                    .selfAlignHorizontally(Alignment.Start),
                icon = R.drawable.back,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    ) { paddingValues ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(paddingValues),
            onBack = {
                if (backStack.size == 1) {
                    onNavigateUp()
                } else {
                    backStack.navigateBack()
                }
            },
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = slideInHorizontally(navigationBouncySpec) { it } + fadeIn(),
                    initialContentExit = fadeOut()
                )
            },
            popTransitionSpec = {
                ContentTransform(
                    targetContentEnter = slideInHorizontally(navigationBouncySpec) { -it } + fadeIn(),
                    initialContentExit = fadeOut()
                )
            },
            predictivePopTransitionSpec = {
                ContentTransform(
                    fadeIn(),
                    slideOutHorizontally(navigationBouncySpec) { it },
                )
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<SettingsScreens.Settings> {
                    Column {
                        AboutCard()
                        Spacer(Modifier.height(20.dp))
                        items.fastForEachIndexed { index, item ->
                            SettingsCategoryCard(
                                icon = item.icon,
                                name = item.name,
                                description = item.description,
                                topDp = if (index == 0) 24.dp else 4.dp,
                                bottomDp = if (index == items.lastIndex) 24.dp else 4.dp,
                                onNavigate = item.onNavigate
                            )
                        }
                    }
                }

                entry<SettingsScreens.LookAndFeel> {
                    SettingsLookAndFeel()
                }

                entry<SettingsScreens.Navigation> {
                    SettingsNavigation()
                }

                entry<SettingsScreens.NowPlaying> {
                    SettingsNowPlaying()
                }

                entry<SettingsScreens.Lyrics> {
                    SettingsLyrics()
                }

                entry<SettingsScreens.Playback> {
                    val viewModel = koinViewModel<PlaybackSettingsViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    SettingsPlayback(
                        state = state,
                        onHandlePlaybackSettingsActions = viewModel::handlePlaybackSettingsActions
                    )
                }

                entry<SettingsScreens.Library> {

                    // TODO unified viewmodel
                    val viewModel = koinViewModel<SafViewModel>()
                    val hiddenTracksViewModel = koinViewModel<HiddenTracksViewModel>()
                    val safTracks by viewModel.safTracks.collectAsStateWithLifecycle()
                    val hiddenTracks by hiddenTracksViewModel.hiddenTracks.collectAsStateWithLifecycle()

                    SettingsLibrary(
                        safTracksUi = safTracks,
                        hiddenTracks = hiddenTracks,
                        musicState = musicState,
                        onNavigate = onNavigate,
                        onHandlePlayerActions = onHandlePlayerActions,
                        onUnhideTrack = hiddenTracksViewModel::unhideTrack
                    )
                }

            }
        )
    }

}

@Immutable
private data class Item(
    val id: String = Uuid.random().toString(),
    val name: String,
    val description: String,
    val icon: Int,
    val onNavigate: () -> Unit
)
