@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.feature.bakery.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.sosauce.chocola.R
import com.sosauce.chocola.core.data.bakery.BakeryDao
import com.sosauce.chocola.core.data.bakery.entity.BakeryStreakEntity
import com.sosauce.chocola.core.data.bakery.result.AlbumCount
import com.sosauce.chocola.core.data.bakery.result.ArtistCount
import com.sosauce.chocola.core.data.bakery.result.DayTotal
import com.sosauce.chocola.core.data.bakery.result.PeriodTotal
import com.sosauce.chocola.core.data.bakery.result.TrackWithCount
import com.sosauce.chocola.feature.bakery.data.mapper.buildSummaryPage
import com.sosauce.chocola.feature.bakery.data.mapper.toAllTimeStatsUi
import com.sosauce.chocola.feature.bakery.data.mapper.toFunFactsPage
import com.sosauce.chocola.feature.bakery.data.mapper.toPeriodCardUi
import com.sosauce.chocola.feature.bakery.data.mapper.toStreakPage
import com.sosauce.chocola.feature.bakery.data.mapper.toTopAlbumsPage
import com.sosauce.chocola.feature.bakery.data.mapper.toTopArtistsPage
import com.sosauce.chocola.feature.bakery.data.mapper.toTopDayPage
import com.sosauce.chocola.feature.bakery.data.mapper.toTopTracksPage
import com.sosauce.chocola.feature.bakery.data.mapper.toTotalsPage
import com.sosauce.chocola.feature.bakery.presentation.model.AllTimeStatsUi
import com.sosauce.chocola.feature.bakery.presentation.model.FunFactUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodCardUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodType
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.chocola.feature.bakery.presentation.model.StreakUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class BakeryRepositoryImpl(
    private val dao: BakeryDao,
    private val context: Context
) : BakeryRepository {

    override fun observeAllTimeStats(): Flow<AllTimeStatsUi> =
        combine(
            dao.observeAllTimeTotal(),
            dao.observeTopTracks(ALL_TIME_START_EPOCH_DAY, ALL_TIME_END_EPOCH_DAY, 1),
            dao.observeTopArtists(ALL_TIME_START_EPOCH_DAY, ALL_TIME_END_EPOCH_DAY, 1)
        ) { total, topTracks, topArtists ->
            total.toAllTimeStatsUi(
                topTrack = topTracks.firstOrNull(),
                topArtist = topArtists.firstOrNull()
            )
        }

    override fun observeStreak(): Flow<StreakUi> =
        dao.observeStreak()
            .map(::toEffectiveStreak)
            .distinctUntilChanged()

    override fun observeMonths(): Flow<List<PeriodCardUi>> =
        dao.observeMonthKeys().flatMapLatest { keys ->
            if (keys.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(keys.map(::observeMonthCard)) { cards -> cards.toList() }
            }
        }

    override fun observeYears(): Flow<List<PeriodCardUi>> =
        dao.observeYearKeys().flatMapLatest { keys ->
            if (keys.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(keys.map(::observeYearCard)) { cards -> cards.toList() }
            }
        }

    override fun observeStory(period: PeriodId): Flow<List<StoryPageUi>> {
        val range = period.toDayRange()
        val storyData = combine(
            dao.observePeriodTotal(range.startEpochDay, range.endEpochDay),
            dao.observeTopTracks(range.startEpochDay, range.endEpochDay, STORY_TOP_LIMIT),
            dao.observeTopArtists(range.startEpochDay, range.endEpochDay, STORY_TOP_LIMIT),
            dao.observeTopAlbums(range.startEpochDay, range.endEpochDay, STORY_TOP_LIMIT),
            dao.observeTopDay(range.startEpochDay, range.endEpochDay)
        ) { total, topTracks, topArtists, topAlbums, topDay ->
            StoryData(
                total = total,
                topTracks = topTracks,
                topArtists = topArtists,
                topAlbums = topAlbums,
                topDay = topDay
            )
        }

        val extras = combine(
            dao.observeStreak(),
            dao.observeDistinctTrackCount(range.startEpochDay, range.endEpochDay)
        ) { streak, distinctTrackCount ->
            streak to distinctTrackCount
        }

        return combine(storyData, extras) { data, (streak, distinctTrackCount) ->
            buildStoryPages(
                period = period,
                data = data,
                streak = streak,
                distinctTrackCount = distinctTrackCount
            )
        }
    }

    override fun observeHasListens(): Flow<Boolean> =
        dao.observeAllTimeTotal()
            .map { it.totalListens > 0 }
            .distinctUntilChanged()

    override suspend fun saveSummaryImage(bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "Chocola_Bakery_${System.currentTimeMillis()}.png"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/Chocola"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return@withContext false

        try {
            resolver.openOutputStream(uri)?.use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                    throw IOException("Could not encode the summary bitmap")
                }
            } ?: throw IOException("Could not open the summary image output stream")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.update(
                    uri,
                    ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) },
                    null,
                    null
                )
            }
            true
        } catch (cancelled: CancellationException) {
            resolver.delete(uri, null, null)
            throw cancelled
        } catch (_: Exception) {
            resolver.delete(uri, null, null)
            false
        }
    }

    private fun observeMonthCard(key: String): Flow<PeriodCardUi> {
        val month = YearMonth.parse(key)
        val range = month.toDayRange()
        return combine(
            dao.observePeriodTotal(range.startEpochDay, range.endEpochDay),
            dao.observePeriodArtwork(range.startEpochDay, range.endEpochDay)
        ) { total, artworkUri ->
            total.toPeriodCardUi(
                id = PeriodId(PeriodType.MONTH, key),
                title = month.formatMonthName(),
                year = month.year,
                topArtworkUri = artworkUri
            )
        }
    }

    private fun observeYearCard(key: String): Flow<PeriodCardUi> {
        val year = key.toInt()
        val range = year.toDayRange()
        return combine(
            dao.observePeriodTotal(range.startEpochDay, range.endEpochDay),
            dao.observePeriodArtwork(range.startEpochDay, range.endEpochDay)
        ) { total, artworkUri ->
            total.toPeriodCardUi(
                id = PeriodId(PeriodType.YEAR, key),
                title = key,
                year = null,
                topArtworkUri = artworkUri
            )
        }
    }

    private fun buildStoryPages(
        period: PeriodId,
        data: StoryData,
        streak: BakeryStreakEntity?,
        distinctTrackCount: Int
    ): List<StoryPageUi> {
        val topTrack = data.topTracks.firstOrNull()
        val topArtist = data.topArtists.firstOrNull()
        val topAlbum = data.topAlbums.firstOrNull()
        val topDayLabel = data.topDay?.let { formatDayLabel(it.epochDay) }.orEmpty()

        return buildList {
            add(data.total.toTotalsPage(period.title()))
            add(data.topTracks.toTopTracksPage(STORY_TOP_LIMIT))
            add(data.topArtists.toTopArtistsPage(STORY_TOP_LIMIT))
            add(data.topAlbums.toTopAlbumsPage(STORY_TOP_LIMIT))
            data.topDay?.let { add(it.toTopDayPage(topDayLabel)) }
            add(streak.toStreakPage())
            add(
                buildFunFacts(
                    topTrack = topTrack,
                    topArtist = topArtist,
                    topAlbum = topAlbum,
                    distinctTrackCount = distinctTrackCount
                ).toFunFactsPage()
            )
            add(
                buildSummaryPage(
                    period = period,
                    title = period.title(),
                    total = data.total,
                    topTrack = topTrack,
                    topArtist = topArtist,
                    topAlbum = topAlbum,
                    streak = streak,
                    topDay = data.topDay,
                    topDayLabel = topDayLabel
                )
            )
        }
    }

    private fun buildFunFacts(
        topTrack: TrackWithCount?,
        topArtist: ArtistCount?,
        topAlbum: AlbumCount?,
        distinctTrackCount: Int
    ): List<FunFactUi> = buildList {
        topTrack?.let { track ->
            add(
                FunFactUi(
                    title = context.getString(R.string.bakery_most_replayed, track.listenCount),
                    value = track.title
                )
            )
        }
        topArtist?.let { artist ->
            add(
                FunFactUi(
                    title = context.getString(R.string.top_artist),
                    value = artist.name
                )
            )
        }
        topAlbum?.let { album ->
            add(
                FunFactUi(
                    title = context.getString(R.string.bakery_top_albums),
                    value = album.name
                )
            )
        }
        if (distinctTrackCount > 0) {
            add(
                FunFactUi(
                    title = context.getString(R.string.bakery_top_tracks),
                    value = context.getString(
                        R.string.bakery_different_tracks,
                        distinctTrackCount
                    )
                )
            )
        }
    }

    private fun toEffectiveStreak(streak: BakeryStreakEntity?): StreakUi {
        if (streak == null) {
            return StreakUi()
        }

        val today = LocalDate.now().toEpochDay()
        val isStillActive = streak.lastActiveEpochDay >= today - 1
        val current = if (isStillActive) streak.currentStreak else 0
        return StreakUi(
            current = current,
            best = maxOf(streak.bestStreak, current)
        )
    }

    private fun PeriodId.toDayRange(): DayRange = when (type) {
        PeriodType.MONTH -> YearMonth.parse(key).toDayRange()
        PeriodType.YEAR -> key.toInt().toDayRange()
        PeriodType.ALL_TIME -> DayRange(ALL_TIME_START_EPOCH_DAY, ALL_TIME_END_EPOCH_DAY)
    }

    private fun YearMonth.toDayRange(): DayRange = DayRange(
        startEpochDay = atDay(1).toEpochDay(),
        endEpochDay = atEndOfMonth().toEpochDay()
    )

    private fun Int.toDayRange(): DayRange = DayRange(
        startEpochDay = LocalDate.of(this, 1, 1).toEpochDay(),
        endEpochDay = LocalDate.of(this, 12, 31).toEpochDay()
    )

    private fun PeriodId.title(): String = when (type) {
        PeriodType.MONTH -> {
            val month = YearMonth.parse(key)
            "${month.formatMonthName()} ${month.year}"
        }

        PeriodType.YEAR -> key
        PeriodType.ALL_TIME -> context.getString(R.string.all_time)
    }

    private fun YearMonth.formatMonthName(): String =
        month.getDisplayName(TextStyle.FULL, Locale.getDefault())

    private fun formatDayLabel(epochDay: Long): String =
        LocalDate.ofEpochDay(epochDay)
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()))

    private data class StoryData(
        val total: PeriodTotal,
        val topTracks: List<TrackWithCount>,
        val topArtists: List<ArtistCount>,
        val topAlbums: List<AlbumCount>,
        val topDay: DayTotal?
    )

    private data class DayRange(
        val startEpochDay: Long,
        val endEpochDay: Long
    )

    private companion object {
        const val ALL_TIME_START_EPOCH_DAY = 0L
        const val ALL_TIME_END_EPOCH_DAY = Long.MAX_VALUE
        const val STORY_TOP_LIMIT = 5
    }
}
