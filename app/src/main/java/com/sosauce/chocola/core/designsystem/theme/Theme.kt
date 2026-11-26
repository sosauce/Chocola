@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.designsystem.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.CuteTheme
import com.sosauce.chocola.core.presentation.preferences.rememberAppTheme
import com.sosauce.chocola.core.presentation.preferences.rememberPaletteStyle
import com.sosauce.chocola.core.presentation.preferences.rememberUseArtTheme
import com.sosauce.chocola.core.presentation.preferences.rememberUseSystemFont
import com.sosauce.chocola.core.presentation.util.toPaletteStyle

@Composable
fun ChocolaTheme(
    artImageBitmap: ImageBitmap? = null,
    content: @Composable () -> Unit,
) {

    val activity = LocalActivity.current
    val window = activity?.window
    val theme by rememberAppTheme()
    val useArtTheme by rememberUseArtTheme()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useSystemFont by rememberUseSystemFont()
    val paletteStyle by rememberPaletteStyle()
    val isDark = when (theme) {
        CuteTheme.DARK, CuteTheme.AMOLED -> true
        CuteTheme.SYSTEM -> isSystemInDarkTheme
        else -> false
    }
    val seedColor = rememberSeedColor(
        isDark = isDark,
        useMaterialArt = useArtTheme,
        imageBitmap = artImageBitmap
    )

    val state = rememberDynamicMaterialThemeState(
        seedColor = seedColor,
        isDark = isDark,
        isAmoled = theme == CuteTheme.AMOLED,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = paletteStyle.toPaletteStyle()
    )

    LaunchedEffect(isDark) {
        if (window == null) return@LaunchedEffect

        SystemUiController.setSystemBarsColors(
            window = window,
            isLight = !isDark
        )
    }
    DynamicMaterialExpressiveTheme(
        state = state,
        motionScheme = MotionScheme.expressive(),
        typography = if (useSystemFont) MaterialTheme.typography else GoogleSansFlexTypography,
        content = content
    )
}

val googleSansFlexFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.Setting("ROND", 100f),
            FontVariation.weight(750),
            FontVariation.width(110f)
        )
    )
)

fun sansFlexCustomWidth(width: Float): FontFamily {
    return FontFamily(
        Font(
            resId = R.font.google_sans_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.Setting("ROND", 100f),
                FontVariation.weight(750),
                FontVariation.width(width)
            )
        )
    )
}

val GoogleSansFlexTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(
            fontFamily = googleSansFlexFontFamily,
        ),
        displayMedium = displayMedium.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        displaySmall = displaySmall.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        headlineLarge = headlineLarge.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        titleLarge = titleLarge.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        titleMedium = titleMedium.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        titleSmall = titleSmall.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        bodyLarge = bodyLarge.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        bodyMedium = bodyMedium.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        bodySmall = bodySmall.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        labelLarge = labelLarge.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        labelMedium = labelMedium.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        labelSmall = labelSmall.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        displayLargeEmphasized = displayLargeEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        displayMediumEmphasized = displayMediumEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        displaySmallEmphasized = displaySmallEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        headlineLargeEmphasized = headlineLargeEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        headlineMediumEmphasized = headlineMediumEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        headlineSmallEmphasized = headlineSmallEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        titleLargeEmphasized = titleLargeEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        titleMediumEmphasized = titleMediumEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        titleSmallEmphasized = titleSmallEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        bodyLargeEmphasized = bodyLargeEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        bodyMediumEmphasized = bodyMediumEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        bodySmallEmphasized = bodySmallEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        labelLargeEmphasized = labelLargeEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        labelMediumEmphasized = labelMediumEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        ),
        labelSmallEmphasized = labelSmallEmphasized.copy(
            fontFamily = googleSansFlexFontFamily
        )
    )
}

