@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.theme

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAppTheme
import com.sosauce.chocola.data.datastore.rememberPaletteStyle
import com.sosauce.chocola.data.datastore.rememberUseArtTheme
import com.sosauce.chocola.data.datastore.rememberUseSystemFont
import com.sosauce.chocola.utils.CuteTheme
import com.sosauce.chocola.utils.toPaletteStyle

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
        typography = if (useSystemFont) MaterialTheme.typography else NunitoTypography,
        content = content
    )
}

val nunitoFontFamily = FontFamily(
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.nunito_extralight, FontWeight.ExtraLight, FontStyle.Normal)
)

val NunitoTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displayMedium = displayMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displaySmall = displaySmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineLarge = headlineLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleLarge = titleLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleMedium = titleMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleSmall = titleSmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyLarge = bodyLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyMedium = bodyMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodySmall = bodySmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelLarge = labelLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelMedium = labelMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelSmall = labelSmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displayLargeEmphasized = displayLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displayMediumEmphasized = displayMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displaySmallEmphasized = displaySmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineLargeEmphasized = headlineLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineMediumEmphasized = headlineMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineSmallEmphasized = headlineSmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleLargeEmphasized = titleLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleMediumEmphasized = titleMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleSmallEmphasized = titleSmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyLargeEmphasized = bodyLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyMediumEmphasized = bodyMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodySmallEmphasized = bodySmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelLargeEmphasized = labelLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelMediumEmphasized = labelMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelSmallEmphasized = labelSmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        )
    )
}


