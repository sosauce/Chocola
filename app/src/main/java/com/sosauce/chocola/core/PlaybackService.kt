package com.sosauce.chocola.core

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.ExperimentalApi
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.sosauce.chocola.R
import com.sosauce.chocola.data.widgets.WIDGET_ART
import com.sosauce.chocola.data.widgets.WIDGET_ARTIST
import com.sosauce.chocola.data.widgets.WIDGET_IS_PLAYING
import com.sosauce.chocola.data.widgets.WIDGET_TITLE
import com.sosauce.chocola.data.widgets.WidgetsHelper
import com.sosauce.chocola.domain.EqualizerManager
import com.sosauce.chocola.domain.helpers.AndroidAutoHelper
import com.sosauce.chocola.utils.CUTE_MUSIC_ID
import com.sosauce.chocola.utils.playOrPause
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaybackService : MediaLibraryService(), KoinComponent {



    private var mediaLibrarySession: MediaLibrarySession? = null
    private val equalizerManager by inject<EqualizerManager>()
    private val androidAutoHelper by inject<AndroidAutoHelper>()
    private val widgetsHelper by inject<WidgetsHelper>()
    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()


    val listener = object: Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            widgetsHelper.updateMusicWidgetData(WIDGET_IS_PLAYING, isPlaying)
        }


        @SuppressLint("UnsafeOptInUsageError")
        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            super.onAudioSessionIdChanged(audioSessionId)
            lifecycleScope.launch { equalizerManager.initDynamicsProcessing(audioSessionId) }
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            widgetsHelper.updateMusicWidgetData(
                key = WIDGET_TITLE,
                value = mediaMetadata.title?.toString() ?: "<unknown>"
            )
            widgetsHelper.updateMusicWidgetData(
                key = WIDGET_ARTIST,
                value = mediaMetadata.artist?.toString() ?: "<unknown>"
            )

            widgetsHelper.updateMusicWidgetData(
                key = WIDGET_ART,
                value = widgetsHelper.artToByteArrayString(mediaMetadata.artworkData)
            )
        }
    }

    val callback = object: MediaLibrarySession.Callback {

        @UnstableApi
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = MediaItem.Builder()
                .setMediaId(ROOT_ID)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Chocola")
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                        .build()
                ).build()

            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {

            val mediaItems = androidAutoHelper.getChildrenMediaItems(
                limit = pageSize,
                offset = page * pageSize
            )
            val result = LibraryResult.ofItemList(mediaItems, params)

            return Futures.immediateFuture(result)
        }

        override fun onSearch(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> = Futures.immediateFuture(LibraryResult.ofVoid())

        override fun onGetSearchResult(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {

            val mediaItems = androidAutoHelper.getChildrenMediaItems(
                limit = pageSize,
                offset = page * pageSize
            ).fastFilter { it.mediaMetadata.title?.contains(query) == true }

            val result = LibraryResult.ofItemList(mediaItems, params)
            return Futures.immediateFuture(result)
        }


    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = mediaLibrarySession


    @OptIn(ExperimentalApi::class)
    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        val player: Player = ExoPlayer.Builder(applicationContext)
            //.enablePerStreamMediaProgression(true)
            .setDeviceVolumeControlEnabled(true)
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(audioAttributes, true)
            .build()
        mediaLibrarySession = MediaLibrarySession
            .Builder(this, player, callback)
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


        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this).build().apply {
                setSmallIcon(R.drawable.music_note)
            }
        )

        player.addListener(listener)

    }


    @UnstableApi
    override fun onDestroy() {
        equalizerManager.releaseDynamicsProcessing()
        mediaLibrarySession?.let {
            it.player.removeListener(listener)
            it.player.release()
            it.release()
            mediaLibrarySession = null
        }
        pauseAllPlayersAndStopSelf()
        super.onDestroy()
    }



    @UnstableApi
    override fun onTaskRemoved(rootIntent: Intent?) {
        equalizerManager.releaseDynamicsProcessing()
        mediaLibrarySession?.let {
            it.player.removeListener(listener)
            it.player.release()
            it.release()
            mediaLibrarySession = null
        }
        pauseAllPlayersAndStopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action) {
            WIDGET_ACTION_PLAY_PAUSE -> mediaLibrarySession?.player?.playOrPause()
            WIDGET_ACTION_SKIP_NEXT -> mediaLibrarySession?.player?.seekToNext()
            WIDGET_ACTION_SKIP_PREVIOUS -> mediaLibrarySession?.player?.seekToPrevious()
        }

        return super.onStartCommand(intent, flags, startId)
    }





    companion object {
        private const val ROOT_ID = "CHOCOLA_ROOT_ID"

        const val WIDGET_ACTION_PLAY_PAUSE = "0"
        const val WIDGET_ACTION_SKIP_NEXT = "1"
        const val WIDGET_ACTION_SKIP_PREVIOUS = "2"
    }
}