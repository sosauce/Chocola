package com.sosauce.chocola.feature.bakery.data.mapper

import android.graphics.Bitmap
import com.sosauce.chocola.feature.bakery.data.BakeryRepository
import com.sosauce.chocola.feature.bakery.presentation.model.AllTimeStatsUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodCardUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.chocola.feature.bakery.presentation.model.StreakUi
import com.sosauce.chocola.feature.bakery.presentation.model.TrackStatUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockBakeryRepositoryImpl : BakeryRepository {
    override fun observeAllTimeStats(): Flow<AllTimeStatsUi> = flowOf(
        AllTimeStatsUi(
            totalListens = 234,
            totalListenedMillis = 123423423,
            topTrack = TrackStatUi(
                mediaId = "1",
                title = "Evolved",
                artist = "Yeat",
                album = "Grail",
                artworkUri = null,
                listenCount = 222
            ),
            topArtist = null
        )
    )


    override fun observeStreak(): Flow<StreakUi> = flowOf(StreakUi(/* fake values */))

    override fun observeMonths(): Flow<List<PeriodCardUi>> = flowOf(emptyList())

    override fun observeYears(): Flow<List<PeriodCardUi>> = flowOf(emptyList())

    override fun observeStory(period: PeriodId): Flow<List<StoryPageUi>> = flowOf(emptyList())

    override fun observeHasListens(): Flow<Boolean> = flowOf(true)

    override suspend fun saveSummaryImage(bitmap: Bitmap): Boolean = false
}