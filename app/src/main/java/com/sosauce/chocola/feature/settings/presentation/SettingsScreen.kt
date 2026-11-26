@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sosauce.chocola.R
import com.sosauce.chocola.app.navigation.navigate
import com.sosauce.chocola.app.navigation.navigateBack
import com.sosauce.chocola.core.designsystem.GITHUB_RELEASES
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.feature.bakery.presentation.BakeryRoot
import com.sosauce.chocola.feature.playing.presentation.aod.AlwaysOnDisplay
import com.sosauce.chocola.feature.settings.presentation.components.SettingsScreen
import com.sosauce.chocola.feature.settings.presentation.nowplayingbuttons.NowPlayingButtonsRoot
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.animations.rememberAnimatedShape
import com.sosauce.nekobites.components.settings.AboutCard
import com.sosauce.nekobites.components.settings.ScrollPage
import com.sosauce.nekobites.components.settings.SettingsCard
import com.sosauce.nekobites.components.settings.SettingsEntry
import com.sosauce.nekobites.helpers.ObserveAsEvents
import com.sosauce.nekobites.helpers.rememberInteractionSource
import org.koin.androidx.compose.koinViewModel

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
    val backstack = rememberNavBackStack(initialScreen ?: SettingsScreen.Settings)
    var showBackButton by retain { mutableStateOf(true) }

    val goBack: () -> Unit = {
        if (backstack.size == 1) onNavigateUp() else backstack.navigateBack()
    }


    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBackButton,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                AnimatedFab(
                    onClick = goBack,
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
            onBack = goBack,
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
                    SettingsHome(
                        backstack = backstack,
                        contentPadding = paddingValues
                    )
                }

                entry<SettingsScreen.LookAndFeel> {
                    ScrollPage(paddingValues) {
                        SettingsLookAndFeel(
                            onNavigateToNowPlayingButtons = {
                                backstack.navigate(SettingsScreen.NowPlayingButtons)
                            }
                        )
                    }
                }

                entry<SettingsScreen.Navigation> {
                    ScrollPage(paddingValues) {
                        SettingsNavigation()
                    }
                }

                entry<SettingsScreen.NowPlaying> {
                    ScrollPage(paddingValues) {
                        SettingsNowPlaying()
                    }
                }

                entry<SettingsScreen.NowPlayingButtons> {
                    NowPlayingButtonsRoot(contentPadding = paddingValues)
                }

                entry<SettingsScreen.Lyrics> {
                    ScrollPage(paddingValues) {
                        SettingsLyrics()
                    }
                }

                entry<SettingsScreen.Playback> {
                    val viewModel = koinViewModel<PlaybackSettingsViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    ScrollPage(paddingValues) {
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
                        ScrollPage(paddingValues) {
                            AodSettings(onToggleAod = { toggle -> showAod = toggle })
                        }
                    }
                }

                entry<SettingsScreen.Backup> {
                    val viewModel = koinViewModel<BackupViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    ObserveAsEvents(viewModel.events) { event ->
                        val text = when (event) {
                            is BackupEvents.Success -> resources.getString(R.string.success)
                            is BackupEvents.Error -> event.message
                                ?: resources.getString(R.string.generic_error)
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
                        ScrollPage(paddingValues) {
                            SettingsBackup(onBackupAction = viewModel::onAction)
                        }
                    }
                }

                entry<SettingsScreen.Bakery> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(paddingValues)
                    ) {
                        BakeryRoot(
                            onNavigateUp = goBack,
                            onNavigate = onNavigate
                        )
                    }
                }

                entry<SettingsScreen.Library> {
                    val viewModel = koinViewModel<SettingsLibraryViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    ObserveAsEvents(viewModel.events) {
                        when (it) {
                            is LibraryEvents.RescanSuccessful -> Toast.makeText(
                                context,
                                resources.getString(R.string.success),
                                Toast.LENGTH_SHORT
                            ).show()

                            is LibraryEvents.RescanError -> Toast.makeText(
                                context,
                                it.errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
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

@Composable
private fun SettingsHome(
    backstack: NavBackStack<NavKey>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val palettes = listOf(
        colors.primary,
        colors.secondary,
        colors.tertiary
    )
    val screens = listOf(
        SettingsEntry(
            icon = R.drawable.palette,
            name = stringResource(R.string.look_and_feel),
            description = stringResource(R.string.look_and_feel_desc),
            onClick = { backstack.navigate(SettingsScreen.LookAndFeel) }
        ),
        SettingsEntry(
            icon = R.drawable.navigation,
            name = stringResource(R.string.navigation),
            description = stringResource(R.string.navigation_desc),
            onClick = { backstack.navigate(SettingsScreen.Navigation) }
        ),
        SettingsEntry(
            icon = R.drawable.brightness_medium,
            name = stringResource(R.string.aod),
            description = stringResource(R.string.aod_desc),
            onClick = { backstack.navigate(SettingsScreen.AlwaysOnDisplay) }
        ),
        SettingsEntry(
            icon = R.drawable.lyrics_rounded,
            name = stringResource(R.string.lyrics),
            description = stringResource(R.string.lyrics_settings_desc),
            onClick = { backstack.navigate(SettingsScreen.Lyrics) }
        ),
        SettingsEntry(
            icon = R.drawable.headphones,
            name = stringResource(R.string.playback_controls),
            description = stringResource(R.string.playback_controls_desc),
            onClick = { backstack.navigate(SettingsScreen.Playback) }
        ),
        SettingsEntry(
            icon = R.drawable.library,
            name = stringResource(R.string.library),
            description = stringResource(R.string.library_desc),
            onClick = { backstack.navigate(SettingsScreen.Library) }
        ),
        SettingsEntry(
            icon = R.drawable.backup,
            name = stringResource(R.string.backup_and_restore),
            description = stringResource(R.string.backup_and_restore_desc),
            onClick = { backstack.navigate(SettingsScreen.Backup) }
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = contentPadding.calculateBottomPadding() + 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        AboutCard(
            icon = R.drawable.music_note,
            updateLink = GITHUB_RELEASES,
            appName = "Chocola"
        )

        Spacer(Modifier.height(20.dp))
        FeaturedCard(
            icon = R.drawable.cookie,
            title = stringResource(R.string.bakery),
            description = stringResource(R.string.bakery_desc),
            onClick = { backstack.navigate(SettingsScreen.Bakery) }
        )
        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            screens.fastForEachIndexed { index, screen ->
                val containerColor = palettes[index % 3]
                SettingsCard(
                    entry = screen,
                    isFirst = index == 0,
                    isLast = index == screens.lastIndex,
                    badgeContainer = containerColor
                )
            }
        }
    }
}

//TODO: Import from Nekobites next release
@Composable
fun FeaturedCard(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f
    )
    val shape = rememberAnimatedShape(
        condition = isPressed,
        shapeA = MaterialShapes.Cookie9Sided,
        shapeB = MaterialShapes.Circle
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = colors.surfaceContainerHigh,
        contentColor = contentColorFor(colors.surfaceContainerHigh),
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(colors.secondaryContainer, shape)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(colors.secondaryContainer, CircleShape)
            ) {
                Icon(
                    painter = painterResource(com.sosauce.nekobites.R.drawable.arrow_right),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


