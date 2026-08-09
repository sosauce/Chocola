package com.sosauce.chocola.di

import androidx.room.Room
import com.sosauce.chocola.data.AbstractTracksScanner
import com.sosauce.chocola.data.LyricsParser
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.playlist.MIGRATION_1_2
import com.sosauce.chocola.data.playlist.PlaylistDatabase
import com.sosauce.chocola.data.widgets.WidgetsHelper
import com.sosauce.chocola.domain.EqualizerManager
import com.sosauce.chocola.domain.repository.AlbumsRepository
import com.sosauce.chocola.domain.repository.ArtistsRepository
import com.sosauce.chocola.domain.repository.FoldersRepository
import com.sosauce.chocola.domain.repository.PlaylistsRepository
import com.sosauce.chocola.domain.repository.SafManager
import com.sosauce.chocola.domain.helpers.AndroidAutoHelper
import com.sosauce.chocola.presentation.screens.album.AlbumDetailsViewModel
import com.sosauce.chocola.presentation.screens.album.AlbumsViewModel
import com.sosauce.chocola.presentation.screens.artist.ArtistDetailsViewModel
import com.sosauce.chocola.presentation.screens.artist.ArtistsViewModel
import com.sosauce.chocola.presentation.screens.lyrics.LyricsEditorViewModel
import com.sosauce.chocola.presentation.screens.main.MainViewModel
import com.sosauce.chocola.presentation.screens.metadata.MetadataViewModel
import com.sosauce.chocola.presentation.screens.playlists.PlaylistDetailsViewModel
import com.sosauce.chocola.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.chocola.presentation.screens.quickplay.QuickPlayViewModel
import com.sosauce.chocola.presentation.screens.settings.FoldersViewModel
import com.sosauce.chocola.presentation.screens.settings.HiddenTracksViewModel
import com.sosauce.chocola.presentation.screens.settings.PlaybackSettingsViewModel
import com.sosauce.chocola.presentation.screens.settings.SafViewModel
import com.sosauce.chocola.presentation.screens.transformer.TransformerViewModel
import com.sosauce.chocola.presentation.shared_components.dialogs.DeletionViewModel
import com.sosauce.chocola.presentation.shared_components.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = PlaylistDatabase::class.java,
            name = "playlist.db"
        )
            .addMigrations(MIGRATION_1_2)
            //.addCallback(DEFAULT_PLAYLISTS_CALLBACK)
            .build()
            .dao
    }

    single { CoroutineScope(Dispatchers.IO + SupervisorJob()) }

    singleOf(::AbstractTracksScanner)
    singleOf(::LyricsParser)
    singleOf(::FoldersRepository)
    singleOf(::SafManager)
    singleOf(::AlbumsRepository)
    singleOf(::ArtistsRepository)
    singleOf(::PlaylistsRepository)
    singleOf(::UserPreferences)
    singleOf(::EqualizerManager)
    singleOf(::AndroidAutoHelper)
    singleOf(::WidgetsHelper)



    viewModelOf(::MusicViewModel)
    viewModelOf(::MetadataViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::PlaylistDetailsViewModel)
    viewModelOf(::QuickPlayViewModel)
    viewModelOf(::ArtistsViewModel)
    viewModelOf(::ArtistDetailsViewModel)
    viewModelOf(::AlbumsViewModel)
    viewModelOf(::AlbumDetailsViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::FoldersViewModel)
    viewModelOf(::SafViewModel)
    viewModelOf(::HiddenTracksViewModel)
    viewModelOf(::PlaybackSettingsViewModel)
    viewModelOf(::TransformerViewModel)
    viewModelOf(::DeletionViewModel)
    viewModelOf(::LyricsEditorViewModel)
}