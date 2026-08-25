package com.sosauce.chocola.presentation.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import com.sosauce.chocola.R
import com.sosauce.chocola.data.widgets.WIDGET_IS_PLAYING
import com.sosauce.chocola.core.PlaybackService
import com.sosauce.chocola.core.MainActivity


object MusicWidget2x1 : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val isPlaying = currentState(WIDGET_IS_PLAYING) == true

            GlanceTheme {
                MusicWidget2x1(
                    isPlaying = isPlaying
                )
            }


        }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable
    ) {
        super.onCompositionError(context, glanceId, appWidgetId, throwable)
        throwable.printStackTrace()
    }

}

class MusicWidget1x1Receiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget2x1
}

@Composable
fun MusicWidget2x1(isPlaying: Boolean) {

    val context = LocalContext.current

    Box(
        modifier = GlanceModifier
            .padding(10.dp)
            .background(GlanceTheme.colors.surface)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            BigButton(
                onClick = {
                    val intent = Intent(context, PlaybackService::class.java).apply {
                        action = PlaybackService.WIDGET_ACTION_PLAY_PAUSE
                    }
                    context.startService(intent)
                },
                icon = if (isPlaying) R.drawable.widget_pause else R.drawable.widget_play,
                backgroundColor = GlanceTheme.colors.primaryContainer,
                contentColor = GlanceTheme.colors.onPrimaryContainer
                ,
                cornerRadius = 50.dp,
                modifier = GlanceModifier.fillMaxWidth()
            )
            Spacer(GlanceModifier.height(5.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                BigButton(
                    onClick = {
                        val intent = Intent(context, PlaybackService::class.java).apply {
                            action = PlaybackService.WIDGET_ACTION_SKIP_PREVIOUS
                        }
                        context.startService(intent)
                    },
                    icon = R.drawable.skip_previous,
                    backgroundColor = GlanceTheme.colors.secondaryContainer,
                    contentColor = GlanceTheme.colors.onSecondaryContainer,
                    cornerRadius = 12.dp,
                    height = 40.dp,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                )
                Spacer(GlanceModifier.width(5.dp))
                BigButton(
                    onClick = {
                        val intent = Intent(context, PlaybackService::class.java).apply {
                            action = PlaybackService.WIDGET_ACTION_SKIP_NEXT
                        }
                        context.startService(intent)
                    },
                    icon = R.drawable.skip_next,
                    backgroundColor = GlanceTheme.colors.secondaryContainer,
                    contentColor = GlanceTheme.colors.onSecondaryContainer,
                    cornerRadius = 12.dp,
                    height = 40.dp,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                )

            }
        }
    }
}

@Composable
private fun BigButton(
    modifier: GlanceModifier = GlanceModifier,
    onClick: () -> Unit,
    icon: Int,
    cornerRadius: Dp,
    backgroundColor: ColorProvider,
    contentColor: ColorProvider,
    height: Dp = 60.dp
) {
    Box(
        modifier = modifier
            .height(height)
            .cornerRadius(cornerRadius)
            .background(backgroundColor)
            .clickable(onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor),
        )
    }
}