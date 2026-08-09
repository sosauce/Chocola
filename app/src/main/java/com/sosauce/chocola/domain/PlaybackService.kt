@file:OptIn(DelicateCoroutinesApi::class)

package com.sosauce.chocola.domain

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.os.Build
import android.os.Bundle
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaConstants
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.EqualizerPreset
import com.sosauce.chocola.presentation.MainActivity
import com.sosauce.chocola.presentation.widgets.WidgetBroadcastReceiver
import com.sosauce.chocola.presentation.widgets.WidgetCallback
import com.sosauce.chocola.utils.CUTE_MUSIC_ID
import com.sosauce.chocola.utils.PACKAGE
import com.sosauce.chocola.utils.WIDGET_NEW_DATA
import com.sosauce.chocola.utils.WIDGET_NEW_IS_PLAYING
import com.sosauce.chocola.utils.copyMutate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.definition.indexKey


class PlaybackService : MediaLibraryService(), MediaLibrarySession.Callback, Player.Listener,
    WidgetCallback, KoinComponent {



    private var mediaLibrarySession: MediaLibrarySession? = null

    private val userPreferences by inject<UserPreferences>()
    private val equalizerManager by inject<EqualizerManager>()
    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val widgetReceiver = WidgetBroadcastReceiver()





    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            putExtra(WIDGET_NEW_DATA, WIDGET_NEW_DATA)
            putExtra("title", mediaMetadata.title)
            putExtra("artist", mediaMetadata.artist)



            putExtra("artUri", mediaMetadata.artworkUri)
        }

        sendBroadcast(intent)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            putExtra(WIDGET_NEW_DATA, WIDGET_NEW_IS_PLAYING)
            putExtra("isPlaying", isPlaying)
        }

        sendBroadcast(intent)
    }


    @SuppressLint("UnsafeOptInUsageError")
    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        super.onAudioSessionIdChanged(audioSessionId)
        lifecycleScope.launch { equalizerManager.initDynamicsProcessing(audioSessionId) }
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        val player: Player = ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        mediaLibrarySession = MediaLibrarySession
            .Builder(this, player, this)
            .setId(CUTE_MUSIC_ID)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()





        IntentFilter(PACKAGE).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    widgetReceiver,
                    it,
                    RECEIVER_EXPORTED
                )

            } else {
                registerReceiver(widgetReceiver, it)
            }
        }
        widgetReceiver.startCallback(this)

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this).build().apply {
                setSmallIcon(R.drawable.music_note_rounded)
            }
        )

        player.addListener(this)

    }


    override fun onDestroy() {
        equalizerManager.releaseDynamicsProcessing()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        widgetReceiver.also {
            it.stopCallback()
            unregisterReceiver(it)
        }
        stopSelf()
        super.onDestroy()
    }

    // Android Auto support ?
    // https://github.com/androidx/media/blob/release/demos/session_automotive/src/main/java/androidx/media3/demo/session/automotive/AutomotiveService.kt
    @UnstableApi
    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> = Futures.immediateFuture(
        LibraryResult.ofItem(
            MediaItem.Builder()
                .setMediaId("CHOCOLA_ROOT_ID")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setIsPlayable(false)
                        .setIsBrowsable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                        .build()
                )
                .build(),
            params
        )
    )



    @UnstableApi
    override fun onTaskRemoved(rootIntent: Intent?) {
        equalizerManager.releaseDynamicsProcessing()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        pauseAllPlayersAndStopSelf()
        super.onTaskRemoved(rootIntent)
    }


    override fun skipToNext() {
        mediaLibrarySession?.player?.seekToNextMediaItem()
    }

    override fun playOrPause() {

        if (mediaLibrarySession?.player?.isPlaying == true) {
            mediaLibrarySession?.player?.pause()
        } else {
            mediaLibrarySession?.player?.play()
        }
    }

    override fun skipToPrevious() {
        mediaLibrarySession?.player?.seekToPrevious()
    }
}
