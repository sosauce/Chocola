package com.sosauce.chocola.presentation.screens.aod

import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sosauce.chocola.R
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.nekobites.components.Spacer

@Composable
fun AlwaysOnDisplay(
    title: String,
    artist: String,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onExitAod: () -> Unit
) {
    val view = LocalView.current
    val activity = LocalActivity.current!!
    val window = activity.window

    DisposableEffect(window) {
        window?.run {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        onDispose {
            window?.run {
                clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    DisposableEffect(view, window) {
        val originalModeId = window?.attributes?.preferredDisplayModeId

        if (window != null) {
            val display = view.display ?: window.decorView.display

            val lowestMode = display?.supportedModes
                ?.minByOrNull { it.refreshRate }

            lowestMode?.let { mode ->
                val params = window.attributes
                params.preferredDisplayModeId = mode.modeId
                window.attributes = params
            }
        }

        window?.let { win ->
            val insetsController = WindowCompat.getInsetsController(win, win.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            window?.let { win ->
                val insetsController = WindowCompat.getInsetsController(win, win.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())

                val params = win.attributes
                params.preferredDisplayModeId = originalModeId ?: 0
                win.attributes = params
            }
        }
    }

    BackHandler(true) { /* do nothing to prevent accidental swipe backs */ }

    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .combinedClickable(
                    onClick = {},
                    indication = null,
                    interactionSource = null,
                    onDoubleClick = onExitAod
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.displayMediumEmphasized.copy(
                    fontWeight = FontWeight.ExtraLight,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(5.dp)
            Text(
                text = artist,
                style = MaterialTheme.typography.headlineSmallEmphasized.copy(
                    fontWeight = FontWeight.ExtraLight,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(15.dp)
            Row {
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
                    isPlaying = isPlaying,
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
    }
}