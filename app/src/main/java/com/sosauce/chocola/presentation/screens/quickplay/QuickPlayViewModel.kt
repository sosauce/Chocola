package com.sosauce.chocola.presentation.screens.quickplay

import android.app.Application
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kyant.taglib.Metadata
import com.kyant.taglib.TagLib
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.utils.changeRepeatMode
import com.sosauce.chocola.utils.getUriFromByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class QuickPlayViewModel(
    private val trackUri: Uri,
    private val application: Application
) : AndroidViewModel(application) {

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()
    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()




    private val listener = object : Player.Listener {



        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _musicState.update {
                it.copy(
                    isPlaying = isPlaying
                )
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            _musicState.update {
                it.copy(
                    repeatMode = repeatMode
                )
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)

            _musicState.update {
                it.copy(duration = player.duration)
            }
            viewModelScope.launch {
                while (player.isPlaying) {
                    _musicState.update {
                        it.copy(
                            position = player.currentPosition
                        )
                    }
                    delay(500.milliseconds)
                }
            }
        }


        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)

            val title = mediaMetadata.title?.toString() ?: trackUri.path?.substringAfterLast('/')?.substringBeforeLast('.')
            val artist = mediaMetadata.artist?.toString() ?: "<unknown>"
            val art = mediaMetadata.artworkData?.getUriFromByteArray(application) ?: Uri.EMPTY

            val track = CuteTrack(
                title = title ?: "<unknown>",
                artist = artist,
                artUriString = art.toString()
            )

            _musicState.update {
                it.copy(
                    track = track
                )
            }
        }

    }


    private val player = ExoPlayer.Builder(application.applicationContext)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .build()
        .apply {
            playWhenReady = true
            setMediaItem(MediaItem.fromUri(trackUri))
            addListener(listener)
            prepare()
        }

    var isSongLoaded by mutableStateOf(false)


//    init {
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _musicState.update {
//                it.copy(
//                    track = loadTrackData()
//                )
//            }
//            isSongLoaded = true
//        }
//    }

    override fun onCleared() {
        player.removeListener(listener)
        player.release()
    }



    fun handlePlayerAction(action: PlayerActions) {
        when (action) {
            is PlayerActions.PlayOrPause -> if (player.isPlaying) player.pause() else player.play()
            is PlayerActions.UpdateCurrentPosition -> {
                _musicState.update {
                    it.copy(
                        position = action.position
                    )
                }
            }

            is PlayerActions.SeekToSlider -> player.seekTo(action.position)
            is PlayerActions.SeekTo -> player.seekTo(player.currentPosition + action.position)
            is PlayerActions.RewindTo -> player.seekTo(player.currentPosition - action.position)
            is PlayerActions.ChangeRepeatMode -> player.changeRepeatMode()
            else -> Unit
        }
    }
}