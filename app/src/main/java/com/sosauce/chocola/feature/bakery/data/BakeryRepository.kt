package com.sosauce.chocola.feature.bakery.data

import android.graphics.Bitmap
import com.sosauce.chocola.feature.bakery.presentation.model.AllTimeStatsUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodCardUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.chocola.feature.bakery.presentation.model.StreakUi
import kotlinx.coroutines.flow.Flow

interface BakeryRepository {

    fun observeAllTimeStats(): Flow<AllTimeStatsUi>

    fun observeStreak(): Flow<StreakUi>

    fun observeMonths(): Flow<List<PeriodCardUi>>

    fun observeYears(): Flow<List<PeriodCardUi>>

    fun observeStory(period: PeriodId): Flow<List<StoryPageUi>>

    fun observeHasListens(): Flow<Boolean>

    suspend fun saveSummaryImage(bitmap: Bitmap): Boolean
}
