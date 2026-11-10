@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.setup.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val StepCount = 2

@Composable
fun SetupScreen(
    onNavigateToApp: () -> Unit
) {
    var setupProgress by remember { mutableIntStateOf(0) }
    val progress by animateFloatAsState(
        targetValue = (setupProgress + 1) / StepCount.toFloat(),
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
    )

    Scaffold { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {
            LinearWavyProgressIndicator(
                progress = { progress },
                stopSize = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 1.dp)
            )
            AnimatedContent(
                targetState = setupProgress,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it / 4 } + fadeOut()
                },
                modifier = Modifier.fillMaxSize()
            ) { step ->
                when (step) {
                    0 -> SetupPermissions { setupProgress = 1 }
                    1 -> SetupFolders(onNavigateToApp)
                }
            }
        }
    }
}
