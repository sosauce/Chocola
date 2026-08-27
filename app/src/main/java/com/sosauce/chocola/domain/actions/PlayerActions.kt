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
    data class PlayTrack(val track: CuteTrack) : PlayerActions
    data class SeekToSlider(val position: Long) : PlayerActions
    data class RewindTo(val position: Long) : PlayerActions
    data class SetSpeed(val speed: Float) : PlayerActions
    data class SetPitch(val pitch: Float) : PlayerActions

    data class PlayFromSource(
        val mediaId: String?,
        val source: PlaySource
    ) : PlayerActions

    data class StartPlaylist(val source: PlaySource) : PlayerActions

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

sealed interface PlaySource {
    data object All : PlaySource
    data class Album(val name: String) : PlaySource
    data class Artist(val name: String) : PlaySource
    data class ExplicitTracks(val tracks: List<CuteTrack>) : PlaySource
}
