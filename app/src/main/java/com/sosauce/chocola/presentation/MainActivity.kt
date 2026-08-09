package com.sosauce.chocola.presentation

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAppTheme
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.presentation.navigation.Nav
import com.sosauce.chocola.presentation.screens.aod.AlwaysOnDisplay
import com.sosauce.chocola.presentation.shared_components.MusicViewModel
import com.sosauce.chocola.presentation.theme.CuteMusicTheme
import com.sosauce.chocola.presentation.widgets.MusicWidget4x1Receiver
import com.sosauce.chocola.utils.CuteTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.system.measureTimeMillis

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val musicViewModel = koinViewModel<MusicViewModel>()
            val theme by rememberAppTheme()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            CuteMusicTheme(artImageBitmap = musicViewModel.artworkImageBitmap) {

                WindowCompat
                    .getInsetsController(window, window.decorView)
                    .apply {

                        val isLight =
                            if (theme == CuteTheme.SYSTEM) !isSystemInDarkTheme else theme == CuteTheme.LIGHT

                        isAppearanceLightStatusBars = isLight
                        isAppearanceLightNavigationBars = isLight
                    }
                //AlwaysOnDisplay("", "") { }
                Nav(
                    musicViewModel = musicViewModel
                )
            }
        }

    }


//    override fun onDestroy() {
//        super.onDestroy()
//        sendBroadcast(
//            Intent(
//                "CM_CUR_PLAY_CHANGED"
//            ).apply {
//                putExtra("currentlyPlaying", "")
//            }
//        )
//    }
}