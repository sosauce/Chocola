package com.sosauce.chocola.feature.settings.presentation.components

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsScreen : NavKey {

    @Serializable
    data object Settings : SettingsScreen()

    @Serializable
    data object LookAndFeel : SettingsScreen()

    @Serializable
    data object NowPlaying : SettingsScreen()

    @Serializable
    data object Playback : SettingsScreen()

    @Serializable
    data object Library : SettingsScreen()

    @Serializable
    data object Lyrics : SettingsScreen()

    @Serializable
    data object Navigation : SettingsScreen()

    @Serializable
    data object AlwaysOnDisplay : SettingsScreen()
}