@file:OptIn(ExperimentalUuidApi::class)

package com.sosauce.chocola.presentation.screens.settings

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
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
import com.sosauce.chocola.presentation.components.wrappers.ObserveAsEvents
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.navigation.navigate
import com.sosauce.chocola.presentation.navigation.navigateBack
import com.sosauce.chocola.presentation.screens.aod.AlwaysOnDisplay
import com.sosauce.chocola.presentation.screens.settings.compenents.AboutCard
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsCategoryCard
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsScreens
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
    val context = LocalContext.current
    val resources = LocalResources.current
    val scrollState = rememberScrollState()
    val backstack = rememberNavBackStack(SettingsScreens.Settings)
    val items = listOf(
        Item(
            icon = R.drawable.palette,
            name = stringResource(R.string.look_and_feel),
            description = stringResource(R.string.look_and_feel_desc),
            onNavigate = { backstack.navigate(SettingsScreens.LookAndFeel) }
        ),
        Item(
            icon = R.drawable.music_note,
            name = stringResource(R.string.now_playing),
            description = stringResource(R.string.now_playing_desc),
            onNavigate = { backstack.navigate(SettingsScreens.NowPlaying) }
        ),
        Item(
            icon = R.drawable.navigation,
            name = stringResource(R.string.navigation),
            description = stringResource(R.string.navigation_desc),
            onNavigate = { backstack.navigate(SettingsScreens.Navigation) }
        ),
        Item(
            icon = R.drawable.brightness_medium,
            name = stringResource(R.string.aod),
            description = stringResource(R.string.aod_desc),
            onNavigate = { backstack.navigate(SettingsScreens.AlwaysOnDisplay) }
        ),
        Item(
            icon = R.drawable.lyrics_rounded,
            name = stringResource(R.string.lyrics),
            description = stringResource(R.string.lyrics_settings_desc),
            onNavigate = { backstack.navigate(SettingsScreens.Lyrics) }
        ),
        Item(
            icon = R.drawable.headphones,
            name = stringResource(R.string.playback_controls),
            description = stringResource(R.string.playback_controls_desc),
            onNavigate = { backstack.navigate(SettingsScreens.Playback) }
        ),
        Item(
            icon = R.drawable.library,
            name = stringResource(R.string.library),
            description = stringResource(R.string.library_desc),
            onNavigate = { backstack.navigate(SettingsScreens.Library) }
        )
    )

    Scaffold(
        bottomBar = {
            AnimatedFab(
                onClick = {
                    if (backstack.size == 1) {
                        onNavigateUp()
                    } else {
                        backstack.navigateBack()
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
            backStack = backstack,
            onBack = {
                if (backstack.size == 1) {
                    onNavigateUp()
                } else {
                    backstack.navigateBack()
                }
            },
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = slideInHorizontally { it } + fadeIn(),
                    initialContentExit = slideOutHorizontally { -it / 4 } + fadeOut()
                )
            },
            predictivePopTransitionSpec = {
                ContentTransform(
                    targetContentEnter = slideInHorizontally { -it / 4 } + fadeIn(),
                    initialContentExit = slideOutHorizontally { it } + fadeOut()
                )
            },
            popTransitionSpec = {
                ContentTransform(
                    targetContentEnter = slideInHorizontally { -it / 4 } + fadeIn(),
                    initialContentExit = slideOutHorizontally { it } + fadeOut()
                )
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<SettingsScreens.Settings> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
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
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsLookAndFeel()
                    }
                }

                entry<SettingsScreens.Navigation> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsNavigation()
                    }
                }

                entry<SettingsScreens.NowPlaying> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsNowPlaying()
                    }
                }

                entry<SettingsScreens.Lyrics> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsLyrics()
                    }
                }

                entry<SettingsScreens.Playback> {
                    val viewModel = koinViewModel<PlaybackSettingsViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsPlayback(
                            state = state,
                            onHandlePlaybackSettingsActions = viewModel::handlePlaybackSettingsActions
                        )
                    }
                }
                entry<SettingsScreens.AlwaysOnDisplay> {
                    AlwaysOnDisplay(
                        title = musicState.track.title,
                        artist = musicState.track.artist,
                        isPlaying = musicState.isPlaying,
                        onHandlePlayerActions = onHandlePlayerActions,
                        onExitAod = backstack::navigateBack
                    )
                }

                entry<SettingsScreens.Library> {



                    val viewModel = koinViewModel<SettingsLibraryViewModel>()
                    val safTracks by viewModel.safTracks.collectAsStateWithLifecycle()
                    val hiddenTracks by viewModel.hiddenTracks.collectAsStateWithLifecycle()
                    val folders by viewModel.folders.collectAsStateWithLifecycle()

                    ObserveAsEvents(viewModel.events) {
                        when(it) {
                            is LibraryEvents.RescanSuccessful -> {
                                Toast.makeText(
                                    context,
                                    resources.getString(R.string.success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is LibraryEvents.RescanError -> {
                                Toast.makeText(
                                    context,
                                    it.errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    SettingsLibrary(
                        safTracksUi = safTracks,
                        hiddenTracks = hiddenTracks,
                        folders = folders,
                        musicState = musicState,
                        onNavigate = onNavigate,
                        onHandlePlayerActions = onHandlePlayerActions,
                        onHandleLibraryActions = viewModel::handleLibraryAction,
                        contentPaddingValues = paddingValues
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
