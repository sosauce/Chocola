@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.chocola.core.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey

val LocalScreen = compositionLocalOf<NavKey> { Screen.Main }
