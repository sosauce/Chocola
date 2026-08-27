@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.chocola.utils

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey
import com.sosauce.chocola.presentation.navigation.Screen

val LocalScreen = compositionLocalOf<NavKey> { Screen.Main }
