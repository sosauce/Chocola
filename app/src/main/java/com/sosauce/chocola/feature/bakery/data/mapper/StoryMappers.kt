package com.sosauce.chocola.feature.bakery.data.mapper

import com.sosauce.chocola.core.data.bakery.entity.BakeryStreakEntity
import com.sosauce.chocola.core.data.bakery.result.AlbumCount
import com.sosauce.chocola.core.data.bakery.result.ArtistCount
import com.sosauce.chocola.core.data.bakery.result.DayTotal
import com.sosauce.chocola.core.data.bakery.result.PeriodTotal
import com.sosauce.chocola.core.data.bakery.result.TrackWithCount
import com.sosauce.chocola.feature.bakery.presentation.model.FunFactUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi

fun PeriodTotal.toTotalsPage(periodTitle: String): StoryPageUi.Totals =
    StoryPageUi.Totals(
        periodTitle = periodTitle,
        totalListens = totalListens,
        totalListenedMillis = totalListenedMillis
    )

fun List<TrackWithCount>.toTopTracksPage(limit: Int = 5): StoryPageUi.TopTracks =
    StoryPageUi.TopTracks(
        items = take(limit).map { it.toStatUi() }
    )

fun List<ArtistCount>.toTopArtistsPage(limit: Int = 5): StoryPageUi.TopArtists =
    StoryPageUi.TopArtists(
        items = take(limit).map { it.toStatUi() }
    )

fun List<AlbumCount>.toTopAlbumsPage(limit: Int = 5): StoryPageUi.TopAlbums =
    StoryPageUi.TopAlbums(
        items = take(limit).map { it.toStatUi() }
    )

fun DayTotal.toTopDayPage(label: String): StoryPageUi.TopDay =
    StoryPageUi.TopDay(
        day = toTopDayUi(label)
    )

fun BakeryStreakEntity?.toStreakPage(): StoryPageUi.Streak =
    StoryPageUi.Streak(
        streak = toStreakUi()
    )

fun List<FunFactUi>.toFunFactsPage(): StoryPageUi.FunFacts =
    StoryPageUi.FunFacts(facts = this)

fun buildSummaryPage(
    period: PeriodId,
    title: String,
    total: PeriodTotal,
    topTrack: TrackWithCount?,
    topArtist: ArtistCount?,
    topAlbum: AlbumCount?,
    streak: BakeryStreakEntity?,
    topDay: DayTotal?,
    topDayLabel: String
): StoryPageUi.Summary =
    StoryPageUi.Summary(
        period = period,
        title = title,
        totalListens = total.totalListens,
        totalListenedMillis = total.totalListenedMillis,
        topTrack = topTrack?.toStatUi(),
        topArtist = topArtist?.toStatUi(),
        topAlbum = topAlbum?.toStatUi(),
        streak = streak.toStreakUi(),
        topDay = topDay?.toTopDayUi(topDayLabel)
    )
