package com.sosauce.chocola.presentation.theme


import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.kmpalette.color
import com.kmpalette.rememberDominantColorState


@Composable
fun rememberSeedColor(
    isDark: Boolean,
    useMaterialArt: Boolean,
    imageBitmap: ImageBitmap?
): Color {

    val systemPrimary = if (isDark) anyDarkColorScheme().primary else anyLightColorScheme().primary

    if (!useMaterialArt) return systemPrimary
    if (imageBitmap == null) return systemPrimary

    var seedColor by remember { mutableStateOf(systemPrimary) }
    val dominantColorState = rememberDominantColorState()


    LaunchedEffect(imageBitmap) {
        dominantColorState.updateFrom(imageBitmap)
        seedColor = dominantColorState.color
    }

    return seedColor

}

@Composable
fun anyLightColorScheme(): ColorScheme {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(context)
    } else {
        lightColorScheme()
    }
}

@Composable
fun anyDarkColorScheme(): ColorScheme {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context)
    } else {
        darkColorScheme()
    }
}
