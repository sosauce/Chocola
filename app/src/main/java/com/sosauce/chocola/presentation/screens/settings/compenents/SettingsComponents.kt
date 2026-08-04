@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings.compenents

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAppTheme
import com.sosauce.chocola.data.models.EqualizerPreset
import com.sosauce.chocola.presentation.screens.playing.components.WavySlider
import com.sosauce.chocola.presentation.screens.settings.FontItem
import com.sosauce.chocola.presentation.screens.settings.FontStyle
import com.sosauce.chocola.presentation.screens.settings.ThemeItem
import com.sosauce.chocola.utils.ArtworkShape
import com.sosauce.chocola.utils.CuteTheme
import com.sosauce.chocola.utils.rememberInteractionSource
import com.sosauce.chocola.utils.toPaletteStyle


@Composable
fun SettingsSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    topDp: Dp,
    bottomDp: Dp,
    text: String,
    onCheckedChange: () -> Unit,
    @StringRes optionalDescription: Int? = null
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        ),
        onClick = onCheckedChange
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(text)
                    optionalDescription?.let {
                        Text(
                            text = stringResource(it),
                            style = MaterialTheme.typography.labelSmallEmphasized.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun ClickableSettingsCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    topDp: Dp,
    bottomDp: Dp,
    text: String,
    @StringRes optionalDescription: Int? = null
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        ),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text)
                    optionalDescription?.let {
                        Text(
                            text = stringResource(it),
                            style = MaterialTheme.typography.labelSmallEmphasized.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun <T> SettingsDropdownMenu(
    value: T,
    topDp: Dp,
    bottomDp: Dp,
    text: Int,
    @StringRes optionalDescription: Int? = null,
    dropdownContent: @Composable (ColumnScope.(onClose: () -> Unit) -> Unit)
) {

    var expanded by remember { mutableStateOf(false) }


    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                Column {
                    Text(stringResource(text))
                    optionalDescription?.let {
                        Text(
                            text = stringResource(it),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            TextButton(
                onClick = { expanded = true },
                shapes = ButtonDefaults.shapes()
            ) {
                AnimatedContent(
                    targetState = value
                ) {
                    Text(
                        text = it.toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                }


                DropdownMenuPopup(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuGroup(
                        shapes = MenuDefaults.groupShapes(),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) { dropdownContent { expanded = false } }
                }
            }
        }
    }
}

@Composable
fun SliderSettingsCards(
    value: Int,
    topDp: Dp,
    bottomDp: Dp,
    text: String,
    unit: String? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..60f,
    onValueChange: (Int) -> Unit,
    @StringRes optionalDescription: Int? = null,
) {

    val animatedValue by animateIntAsState(value)
    val sliderState = rememberSliderState(
        value = value.toFloat(),
        valueRange = valueRange,
    )
    sliderState.onValueChange = { onValueChange(it.toInt()) }

    LaunchedEffect(animatedValue) {
        sliderState.value = animatedValue.toFloat()
    }

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text)
                }
                Text(
                    text = buildString {
                        append(animatedValue.toString())
                        unit?.let { append(it) }
                    }
                )
            }
            WavySlider(state = sliderState)
            optionalDescription?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.labelSmallEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun ThemeSelector(theme: ThemeItem) {
    val borderColor by animateColorAsState(
        targetValue = if (theme.isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )



    SelectorSurface(
        onClick = theme.onClick
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = MaterialShapes.Cookie9Sided.toShape()
                )
                .background(theme.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(theme.iconAndTint.first),
                contentDescription = null,
                tint = theme.iconAndTint.second,
            )
        }
        Text(theme.text)
    }
}

@Composable
fun FontSelector(fontItem: FontItem) {

    SelectorSurface(
        onClick = fontItem.onClick
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .border(
                    width = 2.dp,
                    color = fontItem.borderColor,
                    shape = MaterialShapes.Cookie9Sided.toShape()
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) { fontItem.text() }
        Text(
            text = if (fontItem.fontStyle == FontStyle.SYSTEM) stringResource(R.string.system) else stringResource(
                R.string.default_text
            )
        )
    }
}

@Composable
fun PaletteSelector(
    isSelected: Boolean,
    paletteStyle: String,
    onSelectNewPalette: () -> Unit
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val theme by rememberAppTheme()

    val state = rememberDynamicMaterialThemeState(
        seedColor = MaterialTheme.colorScheme.primary,
        isDark = if (theme == CuteTheme.SYSTEM) isSystemInDarkTheme else if (theme == CuteTheme.AMOLED) true else theme == CuteTheme.DARK,
        isAmoled = theme == CuteTheme.AMOLED,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = paletteStyle.toPaletteStyle()
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
    )

    DynamicMaterialExpressiveTheme(
        state = state,
        animate = true
    ) {
        val dynamicColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
        )

        SelectorSurface(
            onClick = onSelectNewPalette
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(ShapeDefaults.Medium)
                    .width(60.dp)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = borderColor,
                        shape = ShapeDefaults.Medium
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                dynamicColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                    )
                }
            }
            Text(paletteStyle)
        }
    }
}

@Composable
fun EqualizerPresetSelector(
    preset: EqualizerPreset,
    onClick: () -> Unit
) {
    SelectorSurface(
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) { Text(preset.emoji) }
        Text(preset.name)
    }
}

@Composable
fun ShapeSelector(
    onClick: () -> Unit,
    shape: String,
    isSelected: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )

    SelectorSurface(
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(ArtworkShape.toShape(shape))
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = ArtworkShape.toShape(shape)
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}

@Composable
fun SliderSelector(
    onClick: () -> Unit,
    isSelected: Boolean,
    slider: @Composable () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )

    SelectorSurface(
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .height(50.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(5.dp)
            ) {
                slider()
            }
        }
    }
}

@Composable
fun SquareSelector(
    onClick: () -> Unit,
    isSelected: Boolean,
    height: Dp = 50.dp,
    width: Dp = 50.dp,
    content: @Composable () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )

    SelectorSurface(
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .height(height)
                .width(width)
                .clip(RoundedCornerShape(5.dp))
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(5.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SelectorSurface(
    onClick: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            //.height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        content = content
    )
}