package com.sosauce.chocola.feature.bakery.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface StoryPageUi {

    @Immutable
    data class Totals(
        val periodTitle: String,
        val totalListens: Int,
        val totalListenedMillis: Long
    ) : StoryPageUi

    @Immutable
    data class TopTracks(
        val items: List<TrackStatUi>
    ) : StoryPageUi

    @Immutable
    data class TopArtists(
        val items: List<ArtistStatUi>
    ) : StoryPageUi

    @Immutable
    data class TopAlbums(
        val items: List<AlbumStatUi>
    ) : StoryPageUi

    @Immutable
    data class TopDay(
        val day: TopDayUi
    ) : StoryPageUi

    @Immutable
    data class Streak(
        val streak: StreakUi
    ) : StoryPageUi

    @Immutable
    data class FunFacts(
        val facts: List<FunFactUi>
    ) : StoryPageUi

    @Immutable
    data class Summary(
        val period: PeriodId,
        val title: String,
        val totalListens: Int,
        val totalListenedMillis: Long,
        val topTrack: TrackStatUi?,
        val topArtist: ArtistStatUi?,
        val topAlbum: AlbumStatUi?,
        val streak: StreakUi,
        val topDay: TopDayUi?
    ) : StoryPageUi
}
