package com.sosauce.chocola.feature.bakery.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Segmented story progress.
 *
 * State-only: the active segment uses [pageProgress]; completed segments are full and
 * future segments stay empty.
 */
@Composable
fun StoryProgressBar(
    pageCount: Int,
    currentPage: Int,
    pageProgress: Float,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
) {
    if (pageCount <= 0) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(pageCount) { index ->
            val target = when {
                index < currentPage -> 1f
                index == currentPage -> pageProgress.coerceIn(0f, 1f)
                else -> 0f
            }
            val progress by animateFloatAsState(
                targetValue = target,
                label = "storyPageProgress"
            )

            LinearWavyProgressIndicator(
                progress = { progress },
                stopSize = 0.dp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun StoryProgressBarPreview() {
    MaterialTheme {
        Surface {
            StoryProgressBar(
                pageCount = 7,
                currentPage = 3,
                pageProgress = 0.45f,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
