package com.sosauce.chocola.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.materialkolor.dynamiccolor.DynamicColor
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.sosauce.chocola.data.datastore.rememberAppTheme
import com.sosauce.chocola.data.playlist.PlaylistCleanup
import com.sosauce.chocola.presentation.components.MusicViewModel
import com.sosauce.chocola.presentation.navigation.Nav
import com.sosauce.chocola.presentation.screens.setup.SetupScreen
import com.sosauce.chocola.presentation.theme.ChocolaTheme
import com.sosauce.chocola.utils.CuteTheme
import com.sosauce.chocola.utils.hasMusicPermission
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val playlistCleanup by inject<PlaylistCleanup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()


        lifecycleScope.launch {
            playlistCleanup.startCleanup()
        }


        setContent {
            val musicViewModel = koinViewModel<MusicViewModel>()
            val theme by rememberAppTheme()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            ChocolaTheme(
                artImageBitmap = musicViewModel.artworkImageBitmap
            ) {

                WindowCompat
                    .getInsetsController(window, window.decorView)
                    .apply {

                        val isLight =
                            if (theme == CuteTheme.SYSTEM) !isSystemInDarkTheme else theme == CuteTheme.LIGHT

                        isAppearanceLightStatusBars = isLight
                        isAppearanceLightNavigationBars = isLight
                    }

                var canProceedToApp by remember { mutableStateOf(hasMusicPermission()) }
                //AlwaysOnDisplay("", "") { }

                if (canProceedToApp) {
                    Nav(
                        musicViewModel = musicViewModel
                    )
                } else {
                    SetupScreen { canProceedToApp = true }
                }

            }
        }

    }

}