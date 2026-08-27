package com.sosauce.chocola.presentation.widgets

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
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
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.sosauce.chocola.R
import com.sosauce.chocola.core.MainActivity
import com.sosauce.chocola.core.PlaybackService
import com.sosauce.chocola.data.widgets.WIDGET_ART
import com.sosauce.chocola.data.widgets.WIDGET_ARTIST
import com.sosauce.chocola.data.widgets.WIDGET_IS_PLAYING
import com.sosauce.chocola.data.widgets.WIDGET_TITLE


object MusicWidget4x1 : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val title = currentState(WIDGET_TITLE)
            val artist = currentState(WIDGET_ARTIST)
            val art = currentState(WIDGET_ART)
            val isPlaying = currentState(WIDGET_IS_PLAYING) == true

            GlanceTheme {
                MusicWidget4x1(
                    title = title,
                    artist = artist,
                    art = art,
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

class MusicWidget4x1Receiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget4x1
}

@Composable
fun MusicWidget4x1(
    title: String?,
    artist: String?,
    isPlaying: Boolean,
    art: String?
) {

    val context = LocalContext.current


    Box(
        modifier = GlanceModifier
            .background(GlanceTheme.colors.surface)
            .padding(10.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = GlanceModifier
                    .size(60.dp)
                    .background(GlanceTheme.colors.tertiaryContainer)
                    .cornerRadius(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (art.isNullOrEmpty()) {
                    Image(
                        provider = ImageProvider(R.drawable.music_note),
                        contentDescription = null,
                        modifier = GlanceModifier.size(30.dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onTertiaryContainer)
                    )
                } else {
                    val byteArray = Base64.decode(art, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                    Image(
                        provider = ImageProvider(bitmap),
                        contentDescription = null,
                        modifier = GlanceModifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }


            Spacer(GlanceModifier.width(5.dp))
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Spacer(GlanceModifier.height(5.dp))
                Text(
                    text = title ?: "No title",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onSurface
                    ),
                    maxLines = 1
                )
                Text(
                    text = artist ?: "No artist",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant
                    ),
                    maxLines = 1
                )

            }

            //Spacer(GlanceModifier.defaultWeight())
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.widget_previous),
                contentDescription = null,
                backgroundColor = GlanceTheme.colors.secondaryContainer,
                contentColor = GlanceTheme.colors.onSecondaryContainer,
                onClick = {
                    val intent = Intent(context, PlaybackService::class.java).apply {
                        action = PlaybackService.WIDGET_ACTION_SKIP_PREVIOUS
                    }
                    context.startService(intent)
                },
                modifier = GlanceModifier.size(45.dp)
            )
            Spacer(GlanceModifier.width(5.dp))
            val icon = if (isPlaying) R.drawable.widget_pause else R.drawable.widget_play
            CircleIconButton(
                imageProvider = ImageProvider(icon),
                contentDescription = null,
                backgroundColor = GlanceTheme.colors.primaryContainer,
                contentColor = GlanceTheme.colors.onPrimaryContainer,
                onClick = {
                    val intent = Intent(context, PlaybackService::class.java).apply {
                        action = PlaybackService.WIDGET_ACTION_PLAY_PAUSE
                    }
                    context.startService(intent)
                },
                modifier = GlanceModifier.size(45.dp)
            )
            Spacer(GlanceModifier.width(5.dp))

            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.widget_next),
                backgroundColor = GlanceTheme.colors.secondaryContainer,
                contentColor = GlanceTheme.colors.onSecondaryContainer,
                contentDescription = null,
                onClick = {
                    val intent = Intent(context, PlaybackService::class.java).apply {
                        action = PlaybackService.WIDGET_ACTION_SKIP_NEXT
                    }
                    context.startService(intent)
                },
                modifier = GlanceModifier.size(45.dp)
            )
        }
    }
}

