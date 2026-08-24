@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.chocola.presentation.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.skydoves.cloudy.rememberSky
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberInitialScreenBlocking
import com.sosauce.chocola.presentation.screens.metadata.MetadataActions
import com.sosauce.chocola.presentation.components.MusicViewModel
import com.sosauce.chocola.presentation.components.wrappers.ObserveAsEvents
import com.sosauce.chocola.presentation.screens.album.AlbumDetailsScreen
import com.sosauce.chocola.presentation.screens.album.AlbumDetailsViewModel
import com.sosauce.chocola.presentation.screens.album.AlbumsScreen
import com.sosauce.chocola.presentation.screens.album.AlbumsViewModel
import com.sosauce.chocola.presentation.screens.aod.AlwaysOnDisplay
import com.sosauce.chocola.presentation.screens.artist.ArtistDetailsScreen
import com.sosauce.chocola.presentation.screens.artist.ArtistDetailsViewModel
import com.sosauce.chocola.presentation.screens.artist.ArtistsScreen
import com.sosauce.chocola.presentation.screens.artist.ArtistsViewModel
import com.sosauce.chocola.presentation.screens.lyrics.LyricsEditorScreen
import com.sosauce.chocola.presentation.screens.lyrics.LyricsScreen
import com.sosauce.chocola.presentation.screens.main.MainScreen
import com.sosauce.chocola.presentation.screens.main.MainViewModel
import com.sosauce.chocola.presentation.screens.metadata.MetadataEditorScreen
import com.sosauce.chocola.presentation.screens.metadata.MetadataEvents
import com.sosauce.chocola.presentation.screens.metadata.MetadataViewModel
import com.sosauce.chocola.presentation.screens.playing.QueueScreen
import com.sosauce.chocola.presentation.screens.playlists.PlaylistDetailsScreen
import com.sosauce.chocola.presentation.screens.playlists.PlaylistDetailsViewModel
import com.sosauce.chocola.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.chocola.presentation.screens.playlists.PlaylistsScreen
import com.sosauce.chocola.presentation.screens.settings.SettingsScreen
import com.sosauce.chocola.utils.LocalScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun Nav(
    musicViewModel: MusicViewModel
) {

    val resources = LocalResources.current
    val context = LocalContext.current
    val initialScreen = rememberInitialScreenBlocking()
    val backStack = rememberNavBackStack(initialScreen)
    val currentScreen by remember {
        derivedStateOf { backStack.lastOrNull() ?: Screen.Main }
    }
    val musicState by musicViewModel.musicState.collectAsStateWithLifecycle()

    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalScreen provides currentScreen
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.navigateBack() },
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
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
                entryProvider = entryProvider {

                    entry<Screen.Main> {

                        val viewModel = koinViewModel<MainViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        MainScreen(
                            state = state,
                            musicState = musicState,
                            textFieldState = viewModel.textFieldState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.Albums> {

                        val viewModel = koinViewModel<AlbumsViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        AlbumsScreen(
                            state = state,
                            musicState = musicState,
                            textFieldState = viewModel.textFieldState,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions,
                            onNavigate = backStack::navigate
                        )
                    }

                    entry<Screen.Settings> {
                        SettingsScreen(
                            onNavigateUp = backStack::navigateBack,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.AlbumsDetails> { key ->

                        val viewModel = koinViewModel<AlbumDetailsViewModel>(
                            parameters = { parametersOf(key.name) }
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        AlbumDetailsScreen(
                            state = state,
                            textFieldState = viewModel.textFieldState,
                            onNavigateUp = backStack::navigateBack,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.Artists> {

                        val viewModel = koinViewModel<ArtistsViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        ArtistsScreen(
                            state = state,
                            musicState = musicState,
                            textFieldState = viewModel.textFieldState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions,
                        )
                    }

                    entry<Screen.ArtistsDetails> { key ->

                        val viewModel = koinViewModel<ArtistDetailsViewModel>(
                            parameters = { parametersOf(key.name) }
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        ArtistDetailsScreen(
                            state = state,
                            textFieldState = viewModel.textFieldState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions,
                            musicState = musicState
                        )
                    }

                    entry<Screen.MetadataEditor> { key ->

                        val metadataViewModel = koinViewModel<MetadataViewModel>(
                            parameters = { parametersOf(key.trackPath) }
                        )
                        val state by metadataViewModel.metadataState.collectAsStateWithLifecycle()
                        val legacyPermissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.StartIntentSenderForResult()
                        ) { result ->
                            if (result.resultCode == Activity.RESULT_OK) {
                                metadataViewModel.onHandleMetadataActions(MetadataActions.SaveChanges)
                            } else {
                                Toast.makeText(context, resources.getString(R.string.allow_perform_changes), Toast.LENGTH_SHORT).show()
                            }
                        }

                        ObserveAsEvents(metadataViewModel.events) {
                            when(it) {
                                is MetadataEvents.SaveSuccessful -> backStack.navigateBack()
                                is MetadataEvents.SaveUnsuccessful -> {
                                    val errorMessage = it.error ?: resources.getString(R.string.unknown_error)

                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        ObserveAsEvents(metadataViewModel.legacyAskPermission) {
                            it?.let {
                                val request = IntentSenderRequest.Builder(it.intentSender).build()
                                legacyPermissionLauncher.launch(request)
                            }
                        }


                        MetadataEditorScreen(
                            state = state,
                            trackUri = key.trackUri.toUri(),
                            trackPath = key.trackPath,
                            onNavigateUp = backStack::navigateBack,
                            onNavigate = backStack::navigate,
                            onHandleMetadataActions = metadataViewModel::onHandleMetadataActions
                        )
                    }

                    entry<Screen.Playlists> {

                        val playlistViewModel = koinViewModel<PlaylistViewModel>()
                        val state by playlistViewModel.state.collectAsStateWithLifecycle()

                        PlaylistsScreen(
                            state = state,
                            textFieldState = playlistViewModel.textFieldState,
                            onHandlePlaylistAction = playlistViewModel::handlePlaylistActions,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.PlaylistDetails> { key ->
                        val viewModel = koinViewModel<PlaylistDetailsViewModel>(
                            parameters = { parametersOf(key.id) }
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        PlaylistDetailsScreen(
                            state = state,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            textFieldState = viewModel.textFieldState,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions,
                            onHandlePlaylistAction = viewModel::handlePlaylistActions
                        )
                    }

                    entry<Screen.Queue> {
                        QueueScreen(
                            musicState = musicState,
                            onNavigateUp = backStack::navigateBack,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.Lyrics> {
                        LyricsScreen(
                            onNavigateBack = backStack::navigateBack,
                            onNavigate = backStack::navigate,
                            musicState = musicState,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.LyricsEditor> { key ->
                        LyricsEditorScreen(
                            trackPath = key.trackPath,
                            onNavigateBack = backStack::navigateBack
                        )
                    }

//                    entry<Screen.Transformer> { key ->
//                        val viewModel = koinViewModel<TransformerViewModel>(
//                            parameters = { parametersOf(key.trackUri) }
//                        )
//                    }
                }
            )
        }
    }
}


