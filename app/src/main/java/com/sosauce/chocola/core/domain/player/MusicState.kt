package com.sosauce.chocola.core.domain.player

import androidx.media3.common.Player
import androidx.compose.runtime.Stable
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.model.Lyrics
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class MusicState(
    val track: CuteTrack = CuteTrack(),
    val isPlaying: Boolean = false,
    val duration: Long = 0L,
    val position: Long = 0L,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val isPlayerReady: Boolean = false,
    val sleepTimerRemainingDuration: Long = 0,
    val mediaIndex: Int = 0,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffle: Boolean = false,
    val loadedMedias: List<CuteTrack> = emptyList(),
    val audioSessionAudio: Int = 0,
    val lyrics: List<Lyrics> = emptyList(),
)
