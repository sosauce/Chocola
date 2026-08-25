@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.presentation.components

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.LyricsParser
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.mappers.toMediaItem
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlaySource
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.core.PlaybackService
import com.sosauce.chocola.utils.changeRepeatMode
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.ordered
import com.sosauce.chocola.utils.pauseWithFadeOut
import com.sosauce.chocola.utils.playOrPause
import com.sosauce.chocola.utils.playRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MusicViewModel(
    private val application: Application,
    private val userPreferences: UserPreferences,
    private val lyricsParser: LyricsParser,
    private val abstractTracksScanner: AbstractTracksScanner
) : AndroidViewModel(application) {

    private var mediaController: MediaController? = null
    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val tracks = abstractTracksScanner.latestTracks

    var artworkImageBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    var sleepCountdownTimer: CountDownTimer? = null
    private val playerListener =
        @UnstableApi
        object : Player.Listener {



            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                if (mediaItem == null) return

                tracks.value.fastFirstOrNull { track ->
                    track.mediaId == mediaItem.mediaId
                }?.also { track ->
                    _musicState.update {
                        it.copy(
                            track = track
                        )
                    }
                    loadNewArt(track.artUri)
                    parseLyrics(track.path)

                }
            }

            // timeline = queue/playlist, window = 1 song: that's easier to remember
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                updateLoadedMedias(timeline)
            }

            override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                super.onDeviceVolumeChanged(volume, muted)

                viewModelScope.launch {
                    val shouldMuteOnPause = userPreferences.getPauseOnMute().first()
                    if (muted && shouldMuteOnPause) mediaController!!.pause()
                }
            }


            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                super.onAudioSessionIdChanged(audioSessionId)
                _musicState.update {
                    it.copy(
                        audioSessionAudio = audioSessionId
                    )
                }
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                super.onPlaybackParametersChanged(playbackParameters)
                _musicState.update {
                    it.copy(
                        speed = playbackParameters.speed,
                        pitch = playbackParameters.pitch
                    )
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                _musicState.update {
                    it.copy(
                        isPlaying = isPlaying
                    )
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                _musicState.update {
                    it.copy(
                        shuffle = shuffleModeEnabled
                    )
                }
                updateLoadedMedias(mediaController!!.currentTimeline)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
                _musicState.update {
                    it.copy(
                        repeatMode = repeatMode
                    )
                }
            }


            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        _musicState.update {
                            it.copy(
                                isPlayerReady = false
                            )
                        }
                    }
                    Player.STATE_READY -> {
                        _musicState.update {
                            it.copy(
                                isPlayerReady = true
                            )
                        }
                    }
                    else -> {
                        _musicState.update {
                            it.copy(
                                isPlayerReady = true
                            )
                        }
                    }
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                _musicState.update {
                    it.copy(
                        position = newPosition.positionMs.coerceAtLeast(0)
                    )
                }
            }


            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                _musicState.update {
                    it.copy(
                        duration = player.duration
                    )
                }


                viewModelScope.launch {
                    while (player.isPlaying) {
                        _musicState.update {
                            it.copy(
                                position = player.currentPosition
                            )
                        }
                        delay(1.seconds)
                    }
                }
            }
        }

    private fun loadNewArt(art: Uri?) {
        viewModelScope.launch {
            val request = ImageRequest.Builder(application)
                .data(art)
                //.allowHardware(false)
                .build()
            val result = application.imageLoader.execute(request)

            artworkImageBitmap = result.image?.toBitmap()?.asImageBitmap()
        }
    }

    private fun updateLoadedMedias(timeline: Timeline) {
        val allTracks = tracks.value
        val window = Timeline.Window()
        val newTracks = buildList(timeline.windowCount) {

            var currentWindowIndex = timeline.getFirstWindowIndex(mediaController!!.shuffleModeEnabled)

            while (currentWindowIndex != C.INDEX_UNSET) {
                timeline.getWindow(currentWindowIndex, window)

                val mediaId = window.mediaItem.mediaId
                val found = allTracks.fastFirstOrNull { it.mediaId == mediaId }
                found?.let { add(it) }

                currentWindowIndex = timeline.getNextWindowIndex(
                    currentWindowIndex,
                    Player.REPEAT_MODE_OFF,
                    mediaController!!.shuffleModeEnabled
                )
            }
        }



        _musicState.update {
            it.copy(
                loadedMedias = newTracks,
                mediaIndex = newTracks.indexOf(it.track),
            )
        }
    }

    private fun parseLyrics(trackPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _musicState.update {
                it.copy(
                    lyrics = lyricsParser.parseLyrics(trackPath)
                )
            }
        }
    }

    init {
        MediaController
            .Builder(
                application,
                SessionToken(
                    application,
                    ComponentName(application, PlaybackService::class.java)
                )
            )
            .buildAsync()
            .apply {
                addListener(
                    {
                        mediaController = get()
                        mediaController!!.addListener(playerListener)
                        loadPlaybackPreferences()
                    },
                    MoreExecutors.directExecutor()
                )
            }
    }


    @androidx.annotation.OptIn(UnstableApi::class)
    private fun loadPlaybackPreferences() {
        viewModelScope.launch {

            val savedMusicState = userPreferences.getSavedMusicState()

            mediaController?.run {
                repeatMode = savedMusicState.repeatMode
                shuffleModeEnabled = savedMusicState.shuffle
                mediaController!!.playbackParameters = mediaController!!.playbackParameters.withSpeed(savedMusicState.speed)
                mediaController!!.playbackParameters = mediaController!!.playbackParameters.withPitch(savedMusicState.pitch)
                val mediaItems = savedMusicState.loadedMedias.fastMap { it.toMediaItem() }
                val index = savedMusicState.loadedMedias.indexOf(savedMusicState.track)


                if (savedMusicState.loadedMedias.isNotEmpty()) {
                    setMediaItems(
                        mediaItems,
                        index,
                        savedMusicState.position
                    )
                    prepare()
                    _musicState.update { it.copy(track = savedMusicState.track, loadedMedias = savedMusicState.loadedMedias) }
                }
            }
        }
    }


    override fun onCleared() {
        runBlocking { userPreferences.saveSavedMusicState(musicState.value) }
        mediaController!!.removeListener(playerListener)
        mediaController!!.release()
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    fun handlePlayerActions(action: PlayerActions) {
        when (action) {
            is PlayerActions.RestartSong -> mediaController!!.seekTo(0)
            is PlayerActions.PlayRandom -> mediaController!!.playRandom()
            is PlayerActions.PlayOrPause -> mediaController!!.playOrPause()
            is PlayerActions.SeekToNextMusic -> mediaController!!.seekToNextMediaItem()
            is PlayerActions.SeekToPreviousMusic -> mediaController!!.seekToPreviousMediaItem()
            is PlayerActions.SeekTo -> mediaController!!.seekTo(mediaController!!.currentPosition + action.position)
            is PlayerActions.SeekToSlider -> mediaController!!.seekTo(action.position)
            is PlayerActions.RewindTo -> mediaController!!.seekTo(mediaController!!.currentPosition - action.position)
            is PlayerActions.PlayTrack -> {
                val targetMediaId = action.track.mediaId

                val index = (0 until mediaController!!.mediaItemCount).firstOrNull { index ->
                    mediaController!!.getMediaItemAt(index).mediaId == targetMediaId
                }

                index?.let { index ->
                    mediaController!!.seekTo(index, 0)
                    mediaController!!.play()
                }
            }
            is PlayerActions.StopPlayback -> {
                mediaController?.run {
                    stop()
                    clearMediaItems()
                    seekTo(0)
                }

                _musicState.update {
                    it.copy(
                        loadedMedias = emptyList()
                    )
                }
            }
            is PlayerActions.Shuffle -> mediaController!!.shuffleModeEnabled = !mediaController!!.shuffleModeEnabled
            is PlayerActions.ChangeRepeatMode -> mediaController!!.changeRepeatMode()
            is PlayerActions.SetSpeed -> mediaController!!.playbackParameters = mediaController!!.playbackParameters.withSpeed(action.speed)
            is PlayerActions.SetPitch -> mediaController!!.playbackParameters = mediaController!!.playbackParameters.withPitch(action.pitch)
            is PlayerActions.CancelSleepTimer -> {
                sleepCountdownTimer?.cancel()
                sleepCountdownTimer = null
                _musicState.update {
                    it.copy(
                        sleepTimerRemainingDuration = 0
                    )
                }
            }
            is PlayerActions.PlayFromSource -> {


                val currentTracks = tracks.value
                val random = action.mediaId == null


                val targetTracks = when (val source = action.source) {
                    is PlaySource.All -> currentTracks
                    is PlaySource.Album -> currentTracks.fastFilter { it.album == source.name }
                    is PlaySource.Artist -> currentTracks.fastFilter { it.artist == source.name }
                    is PlaySource.ExplicitTracks -> source.tracks
                }

                if (random) {
                    val mediaItems = targetTracks.fastMap { it.toMediaItem() }
                    mediaController?.apply {
                        setMediaItems(mediaItems)
                        playRandom()
                    }
                } else {
                    val index = targetTracks.indexOfFirst { it.mediaId == action.mediaId }.coerceAtLeast(0)

                    if (targetTracks == musicState.value.loadedMedias) {
                        mediaController?.seekTo(index, 0)
                        mediaController?.play()
                    } else {
                        val mediaItems = targetTracks.fastMap { it.toMediaItem() }
                        mediaController?.apply {
                            setMediaItems(mediaItems, index, 0)
                            play()
                        }
                    }
                }


            }

            is PlayerActions.UpdateCurrentPosition -> {
                _musicState.update {
                    it.copy(
                        position = action.position
                    )
                }
            }

            is PlayerActions.SetSleepTimer -> {
                val totalTimeMillis =
                    Duration.ofHours(action.hours).plusMinutes(action.minutes).toMillis()

                // Cancel any active timer before setting a new one
                sleepCountdownTimer?.cancel()
                sleepCountdownTimer = null

                sleepCountdownTimer = object : CountDownTimer(totalTimeMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        _musicState.update {
                            it.copy(
                                sleepTimerRemainingDuration = millisUntilFinished
                            )
                        }
                    }

                    override fun onFinish() {
                        mediaController!!.pauseWithFadeOut()
                        cancel()
                        sleepCountdownTimer = null
                        _musicState.update {
                            it.copy(
                                sleepTimerRemainingDuration = 0
                            )
                        }
                    }


                }
                sleepCountdownTimer?.start()
            }

            // Cannot be called when shuffle is on
            is PlayerActions.ReArrangeQueue -> {
                mediaController!!.moveMediaItem(
                    action.from,
                    action.to
                )
                val loadedMedias = musicState.value.loadedMedias.copyMutate {
                    val itemToMove = this[action.from]
                    removeAt(action.from)
                    add(action.to, itemToMove)
                }


                _musicState.update {
                    it.copy(
                        loadedMedias = loadedMedias
                    )
                }
            }

            // Cannot be called when shuffle is on
            is PlayerActions.RemoveFromQueue -> {
                val index = musicState.value.loadedMedias.indexOf(action.track)
                if (index != -1) {
                    mediaController?.removeMediaItem(index)
                }
            }
            is PlayerActions.AddToQueue -> {
                val newUniqueTracks = action.cuteTracks.fastFilter { it !in musicState.value.loadedMedias }


                if (newUniqueTracks.isNotEmpty()) {
                    _musicState.update {
                        it.copy(
                            loadedMedias = it.loadedMedias + newUniqueTracks
                        )
                    }

                    mediaController?.addMediaItems(
                        newUniqueTracks.fastMap { it.toMediaItem() }
                    )
                }
            }

            is PlayerActions.PlayNext -> {

                val index = mediaController!!.currentMediaItemIndex + 1

                mediaController?.shuffleModeEnabled = false
                mediaController?.addMediaItem(index, action.cuteTrack.toMediaItem())
            }
            is PlayerActions.LoadLyrics -> {
                viewModelScope.launch {
                    val lyrics = lyricsParser.parseLyrics(action.uri.path ?: return@launch)
                    _musicState.update {
                        it.copy(
                            lyrics = lyrics
                        )
                    }
                }
            }
            is PlayerActions.StartPlaylist -> {
                val currentTracks = tracks.value


                val targetTracks = when (val source = action.source) {
                    is PlaySource.All -> currentTracks
                    is PlaySource.Album -> currentTracks.fastFilter { it.album == source.name }
                    is PlaySource.Artist -> currentTracks.fastFilter { it.artist == source.name }
                    is PlaySource.ExplicitTracks -> source.tracks
                }

                mediaController!!.setMediaItems(targetTracks.fastMap { it.toMediaItem() }, 0, 0)
            }
        }
    }
}