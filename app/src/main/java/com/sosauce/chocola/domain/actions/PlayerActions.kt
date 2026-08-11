package com.sosauce.chocola.domain.actions

import android.net.Uri
import com.sosauce.chocola.data.models.CuteTrack

sealed interface PlayerActions {
    data object PlayOrPause : PlayerActions
    data object SeekToNextMusic : PlayerActions
    data object SeekToPreviousMusic : PlayerActions
    data object RestartSong : PlayerActions
    data object PlayRandom : PlayerActions
    data object StopPlayback : PlayerActions
    data object Shuffle : PlayerActions
    data object ChangeRepeatMode : PlayerActions
    data object CancelSleepTimer : PlayerActions
    data class SeekTo(val position: Long) : PlayerActions
    data class SeekToSlider(val position: Long) : PlayerActions
    data class RewindTo(val position: Long) : PlayerActions
    data class SeekToMusicIndex(val index: Int) : PlayerActions
    data class SetSpeed(val speed: Float) : PlayerActions
    data class SetPitch(val pitch: Float) : PlayerActions

    data class Play(
        val index: Int,
        val tracks: List<CuteTrack>,
        val random: Boolean = false
    ) : PlayerActions

    /**
     * @param data What we want to play, for example, an album's name. For a playlist, it will be it's mediaIds separated with a space, for main screen it will be null
     */
    data class Play2(
        val mediaId: String,
        val playlist: Int,
        val data: String?
    ) : PlayerActions

    data class UpdateCurrentPosition(
        val position: Long
    ) : PlayerActions

    data class SetSleepTimer(
        val hours: Long,
        val minutes: Long
    ) : PlayerActions

    data class ReArrangeQueue(
        val from: Int,
        val to: Int
    ) : PlayerActions

    data class RemoveFromQueue(
        val track: CuteTrack
    ) : PlayerActions

    data class AddToQueue(
        val cuteTracks: List<CuteTrack>
    ) : PlayerActions

    data class PlayNext(
        val cuteTrack: CuteTrack
    ) : PlayerActions

    data class LoadLyrics(
        val uri: Uri
    ) : PlayerActions
}
