package com.sosauce.chocola.app.di

import androidx.room.Room
import com.sosauce.chocola.core.data.bakery.BakeryDatabase
import com.sosauce.chocola.core.data.bakery.StatsRecorder
import com.sosauce.chocola.feature.bakery.data.BakeryRepository
import com.sosauce.chocola.feature.bakery.data.BakeryRepositoryImpl
import com.sosauce.chocola.feature.bakery.presentation.BakeryViewModel
import com.sosauce.chocola.feature.bakery.presentation.story.BakeryStoryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Standalone Bakery database + presentation wiring.
 */
val bakeryModule = module {
    single {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = BakeryDatabase::class.java,
            name = "bakery.db"
        ).build().dao
    }

    single<BakeryRepository> {
        BakeryRepositoryImpl(
            dao = get(),
            context = androidContext()
        )
    }
    factory { StatsRecorder(dao = get()) }

    viewModelOf(::BakeryViewModel)
    viewModelOf(::BakeryStoryViewModel)
}
