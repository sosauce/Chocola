@file:kotlin.OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.app

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
import androidx.media3.common.PlaybackParameters
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
import com.sosauce.chocola.core.data.audio.EqualizerManager
import com.sosauce.chocola.core.data.auto.AndroidAutoHelper
import com.sosauce.chocola.core.data.bakery.PlaybackSnapshot
import com.sosauce.chocola.core.data.bakery.StatsRecorder
import com.sosauce.chocola.core.data.datastore.PreferencesKeys.KEEP_ALIVE
import com.sosauce.chocola.core.data.datastore.dataStore
import com.sosauce.chocola.core.data.widgets.WIDGET_ART
import com.sosauce.chocola.core.data.widgets.WIDGET_ARTIST
import com.sosauce.chocola.core.data.widgets.WIDGET_IS_PLAYING
import com.sosauce.chocola.core.data.widgets.WIDGET_TITLE
import com.sosauce.chocola.core.data.widgets.WidgetsHelper
import com.sosauce.chocola.core.designsystem.CUTE_MUSIC_ID
import com.sosauce.chocola.core.presentation.util.playOrPause
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaybackService : MediaLibraryService(), KoinComponent {


    private var mediaLibrarySession: MediaLibrarySession? = null
    private val equalizerManager by inject<EqualizerManager>()
    private val androidAutoHelper by inject<AndroidAutoHelper>()
    private val widgetsHelper by inject<WidgetsHelper>()

    private val statsRecorder by inject<StatsRecorder>()
    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private var keepAliveEnabled = false
    private var keepAliveJob: Job? = null

    val listener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            widgetsHelper.updateMusicWidgetData(WIDGET_IS_PLAYING, isPlaying)
            playbackSnapshot()?.let(statsRecorder::onIsPlayingChanged)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                statsRecorder.onPlaybackEnded()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            val item = mediaItem ?: return
            playbackSnapshot(item)?.let(statsRecorder::onMediaItemTransition)
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            if (
                reason == Player.DISCONTINUITY_REASON_SEEK ||
                reason == Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT
            ) {
                playbackSnapshot()?.let(statsRecorder::onSeek)
            }
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(playbackParameters)
            playbackSnapshot()?.let(statsRecorder::onPlaybackSpeedChanged)
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

    private fun playbackSnapshot(mediaItem: MediaItem? = null): PlaybackSnapshot? {
        val player = mediaLibrarySession?.player ?: return null
        val item = mediaItem ?: player.currentMediaItem ?: return null
        val metadata = item.mediaMetadata

        return PlaybackSnapshot(
            mediaId = item.mediaId,
            title = metadata.title?.toString().orEmpty(),
            artist = metadata.artist?.toString().orEmpty(),
            album = metadata.albumTitle?.toString().orEmpty(),
            artworkUri = metadata.artworkUri?.toString(),
            durationMillis = player.duration.takeIf { it > 0 }
                ?: metadata.durationMs?.takeIf { it > 0 }
                ?: 0L,
            positionMillis = player.currentPosition,
            playbackSpeed = player.playbackParameters.speed,
            isPlaying = player.isPlaying
        )
    }

    val callback = object : MediaLibrarySession.Callback {


        @UnstableApi
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {

            val playerCommands = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                .add(Player.COMMAND_PLAY_PAUSE)
                .add(Player.COMMAND_SEEK_TO_NEXT)
                .add(Player.COMMAND_SEEK_TO_PREVIOUS)
                .build()

            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS)
                .setAvailablePlayerCommands(playerCommands)
                .build()
        }

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


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession


    @OptIn(ExperimentalApi::class)
    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        val player: Player = ExoPlayer.Builder(applicationContext)
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

        keepAliveJob = lifecycleScope.launch {
            applicationContext.dataStore.data
                .mapLatest { it[KEEP_ALIVE] ?: false }
                .collectLatest { keepAliveEnabled = it }
        }

    }


    @UnstableApi
    override fun onDestroy() {
        keepAliveJob?.cancel()
        keepAliveJob = null
        statsRecorder.release()
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
        if (keepAliveEnabled && mediaLibrarySession!!.player.isPlaying) {
            super.onTaskRemoved(rootIntent)
            return
        }
        statsRecorder.onPlaybackEnded()
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

        when (intent?.action) {
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