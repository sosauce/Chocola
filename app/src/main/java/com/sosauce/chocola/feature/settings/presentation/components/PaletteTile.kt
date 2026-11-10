@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.chocola.core.designsystem.CuteTheme
import com.sosauce.chocola.core.presentation.preferences.rememberAppTheme
import com.sosauce.chocola.core.presentation.util.toPaletteStyle
import com.sosauce.nekobites.components.settings.SelectableTile

@Composable
fun PaletteTile(
    paletteStyle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val theme by rememberAppTheme()
    val isDark = when (theme) {
        CuteTheme.DARK, CuteTheme.AMOLED -> true
        CuteTheme.SYSTEM -> isSystemDark
        else -> false
    }
    val state = rememberDynamicMaterialThemeState(
        seedColor = MaterialTheme.colorScheme.primary,
        isDark = isDark,
        isAmoled = theme == CuteTheme.AMOLED,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = paletteStyle.toPaletteStyle()
    )

    SelectableTile(
        selected = selected,
        onClick = onClick,
        label = paletteStyle
    ) {
        DynamicMaterialExpressiveTheme(state = state, animate = true) {
            val scheme = MaterialTheme.colorScheme
            val quarters =
                listOf(scheme.primary, scheme.secondary, scheme.tertiary, scheme.primaryContainer)
            Canvas(Modifier
                .fillMaxSize()
                .padding(10.dp)) {
                val d = size.minDimension
                quarters.fastForEachIndexed { i, color ->
                    drawArc(
                        color = color,
                        startAngle = 180f + i * 90f,
                        sweepAngle = 90f,
                        useCenter = true,
                        topLeft = Offset((size.width - d) / 2, (size.height - d) / 2),
                        size = Size(d, d)
                    )
                }
            }
        }
    }
}
