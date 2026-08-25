package com.sosauce.chocola.presentation.screens.settings.compenents

import androidx.navigation3.runtime.NavKey
import com.sosauce.chocola.presentation.navigation.Screen
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsScreens : NavKey {

    @Serializable
    data object Settings : SettingsScreens()

    @Serializable
    data object LookAndFeel : SettingsScreens()

    @Serializable
    data object NowPlaying : SettingsScreens()

    @Serializable
    data object Playback : SettingsScreens()

    @Serializable
    data object Library : SettingsScreens()

    @Serializable
    data object Lyrics : SettingsScreens()

    @Serializable
    data object Navigation : SettingsScreens()

    @Serializable
    data object AlwaysOnDisplay : SettingsScreens()
}