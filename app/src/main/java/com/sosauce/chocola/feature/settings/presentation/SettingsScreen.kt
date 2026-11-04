@file:OptIn(ExperimentalUuidApi::class)

package com.sosauce.chocola.feature.settings.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
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
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.app.navigation.navigate
import com.sosauce.chocola.app.navigation.navigateBack
import com.sosauce.chocola.feature.playing.presentation.aod.AlwaysOnDisplay
import com.sosauce.chocola.feature.bakery.presentation.BakeryRoot
import com.sosauce.chocola.feature.settings.presentation.components.AboutCard
import com.sosauce.chocola.feature.settings.presentation.nowplayingbuttons.NowPlayingButtonsRoot
import com.sosauce.chocola.feature.settings.presentation.components.SettingsCategoryCard
import com.sosauce.chocola.feature.settings.presentation.components.SettingsScreen
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.helpers.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Composable
fun SettingsScreen(
    initialScreen: SettingsScreen?,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val scrollState = rememberScrollState()
    val backstack = rememberNavBackStack(initialScreen ?: SettingsScreen.Settings)
    var showBackButton by retain { mutableStateOf(true) }

    val items = listOf(
        Item(
            icon = R.drawable.palette,
            name = stringResource(R.string.look_and_feel),
            description = stringResource(R.string.look_and_feel_desc),
            onNavigate = { backstack.navigate(SettingsScreen.LookAndFeel) }
        ),
        Item(
            icon = R.drawable.navigation,
            name = stringResource(R.string.navigation),
            description = stringResource(R.string.navigation_desc),
            onNavigate = { backstack.navigate(SettingsScreen.Navigation) }
        ),
        Item(
            icon = R.drawable.brightness_medium,
            name = stringResource(R.string.aod),
            description = stringResource(R.string.aod_desc),
            onNavigate = { backstack.navigate(SettingsScreen.AlwaysOnDisplay) }
        ),
        Item(
            icon = R.drawable.lyrics_rounded,
            name = stringResource(R.string.lyrics),
            description = stringResource(R.string.lyrics_settings_desc),
            onNavigate = { backstack.navigate(SettingsScreen.Lyrics) }
        ),
        Item(
            icon = R.drawable.headphones,
            name = stringResource(R.string.playback_controls),
            description = stringResource(R.string.playback_controls_desc),
            onNavigate = { backstack.navigate(SettingsScreen.Playback) }
        ),
        Item(
            icon = R.drawable.library,
            name = stringResource(R.string.library),
            description = stringResource(R.string.library_desc),
            onNavigate = { backstack.navigate(SettingsScreen.Library) }
        ),
        Item(
            icon = R.drawable.backup,
            name = stringResource(R.string.backup_and_restore),
            description = stringResource(R.string.backup_and_restore_desc),
            onNavigate = { backstack.navigate(SettingsScreen.Backup) }
        ),
        Item(
            icon = R.drawable.cookie,
            name = stringResource(R.string.bakery),
            description = stringResource(R.string.bakery_desc),
            onNavigate = { backstack.navigate(SettingsScreen.Bakery) }
        )
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBackButton,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
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
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }
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
                entry<SettingsScreen.Settings> {
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
                                topDp = if (index == 0) 24.dp else 2.dp,
                                bottomDp = if (index == items.lastIndex) 24.dp else 2.dp,
                                onNavigate = item.onNavigate
                            )
                        }
                    }
                }

                entry<SettingsScreen.LookAndFeel> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsLookAndFeel(
                            onNavigateToNowPlayingButtons = {
                                backstack.navigate(SettingsScreen.NowPlayingButtons)
                            }
                        )
                    }
                }

                entry<SettingsScreen.Navigation> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsNavigation()
                    }
                }

                entry<SettingsScreen.NowPlaying> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsNowPlaying()
                    }
                }

                entry<SettingsScreen.NowPlayingButtons> {
                    NowPlayingButtonsRoot(
                        contentPadding = paddingValues
                    )
                }

                entry<SettingsScreen.Lyrics> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        SettingsLyrics()
                    }
                }

                entry<SettingsScreen.Playback> {
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
                entry<SettingsScreen.AlwaysOnDisplay> {
                    var showAod by retain { mutableStateOf(false) }

                    RetainedEffect(showAod) {
                        showBackButton = !showAod
                        onRetire { showBackButton = true }
                    }

                    if (showAod) {
                        AlwaysOnDisplay(
                            title = musicState.track.title,
                            artist = musicState.track.artist,
                            isPlaying = musicState.isPlaying,
                            onHandlePlayerActions = onHandlePlayerActions,
                            onExitAod = { showAod = false }
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(paddingValues)
                        ) {
                            AodSettings(
                                onToggleAod = { toggle ->
                                    showAod = toggle
                                }
                            )
                        }
                    }
                }

                entry<SettingsScreen.Backup> {

                    val viewModel = koinViewModel<BackupViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    ObserveAsEvents(viewModel.events) { event ->

                        val text = when(event) {
                            is BackupEvents.Success -> resources.getString(R.string.success)
                            is BackupEvents.Error -> event.message ?: resources.getString(R.string.generic_error)
                        }
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }

                    if (state.isImporting) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.importing_in_progress),
                                    style = MaterialTheme.typography.bodyLargeEmphasized
                                )
                                LinearWavyProgressIndicator()

                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(paddingValues)
                        ) {
                            SettingsBackup(
                                onBackupAction = viewModel::onAction
                            )
                        }
                    }

                }

                entry<SettingsScreen.Bakery> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        BakeryRoot(
                            onNavigateUp = {
                                if (backstack.size == 1) {
                                    onNavigateUp()
                                } else {
                                    backstack.navigateBack()
                                }
                            },
                            onNavigate = onNavigate
                        )
                    }
                }

                entry<SettingsScreen.Library> {
                    val viewModel = koinViewModel<SettingsLibraryViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    ObserveAsEvents(viewModel.events) {
                        when (it) {
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
                        state = state,
                        onHandlePlayerActions = onHandlePlayerActions,
                        onHandleLibraryActions = viewModel::handleLibraryAction,
                        contentPaddingValues = paddingValues
                    )
                }

            }
        )
    }

}

@Stable
private data class Item(
    val id: String = Uuid.random().toString(),
    val name: String,
    val description: String,
    val icon: Int,
    val onNavigate: () -> Unit
)
