package com.sosauce.chocola.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.sosauce.chocola.app.navigation.Nav
import com.sosauce.chocola.core.data.library.FoldersClassifier
import com.sosauce.chocola.core.data.local.PlaylistCleanup
import com.sosauce.chocola.core.designsystem.components.SelectOverlayHost
import com.sosauce.chocola.core.designsystem.components.rememberSelectOverlayState
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.core.presentation.player.MusicViewModel
import com.sosauce.chocola.core.presentation.util.hasMusicPermission
import com.sosauce.chocola.feature.setup.presentation.SetupScreen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val playlistCleanup by inject<PlaylistCleanup>()
    private val foldersClassifier by inject<FoldersClassifier>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()


        setContent {
            val musicViewModel = koinViewModel<MusicViewModel>()

            ChocolaTheme(
                artImageBitmap = musicViewModel.artworkImageBitmap
            ) {

                var canProceedToApp by remember { mutableStateOf(hasMusicPermission()) }
                if (canProceedToApp) {

                    val lifecycleOwner = LocalLifecycleOwner.current

                    // don't run cleanup jobs in setup
                    LaunchedEffect(Unit) {
                        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            launch { playlistCleanup.startCleanup() }
                            launch { foldersClassifier.start() }
                            launch {
                                // recheck has permissions when app comes back to foreground
                                canProceedToApp = hasMusicPermission()
                            }
                        }
                    }

                    SelectOverlayHost(state = rememberSelectOverlayState()) {
                        Nav(
                            musicViewModel = musicViewModel
                        )
                    }
                } else {
                    SetupScreen { canProceedToApp = true }
                }

            }
        }

    }

}