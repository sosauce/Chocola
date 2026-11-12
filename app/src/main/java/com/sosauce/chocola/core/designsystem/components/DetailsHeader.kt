@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)

package com.sosauce.chocola.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import com.sosauce.chocola.R
import com.sosauce.chocola.core.presentation.preferences.rememberIsLandscape
import com.sosauce.nekobites.helpers.rememberInteractionSource

private val ArtworkSize = 190.dp
private val LandscapeArtworkSize = 148.dp

@Composable
fun DetailsHeader(
    title: @Composable () -> Unit,
    artwork: @Composable (BoxScope.() -> Unit)?,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    info: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    backdrop: (@Composable BoxScope.() -> Unit)? = null,
    artworkShape: Shape = RoundedCornerShape(32.dp),
    artworkContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
) {
    val isLandscape = rememberIsLandscape()
    val frame: @Composable (Dp) -> Unit = { size ->
        artwork?.let {
            DetailsHeaderArtworkFrame(
                size = size,
                shape = artworkShape,
                containerColor = artworkContainerColor,
                content = it
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
            .background(containerColor)
    ) {
        backdrop?.let { Box(Modifier.matchParentSize(), content = it) }

        if (isLandscape) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                frame(LandscapeArtworkSize)
                Column(Modifier.weight(1f)) {
                    DetailsHeaderTitles(
                        title = title,
                        subtitle = subtitle,
                        alignment = Alignment.CenterStart,
                        textAlign = TextAlign.Start
                    )
                    info?.let {
                        Spacer(Modifier.height(12.dp))
                        DetailsHeaderInfo(
                            arrangement = Arrangement.spacedBy(6.dp),
                            content = it
                        )
                    }
                    actions?.let {
                        Spacer(Modifier.height(16.dp))
                        DetailsHeaderActions(it)
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                    .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
            ) {
                frame(ArtworkSize)
                Spacer(Modifier.height(10.dp))
                DetailsHeaderTitles(
                    title = title,
                    subtitle = subtitle,
                    alignment = Alignment.Center,
                    textAlign = TextAlign.Center
                )
                info?.let {
                    Spacer(Modifier.height(14.dp))
                    DetailsHeaderInfo(Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), it)
                }
                actions?.let {
                    Spacer(Modifier.height(14.dp))
                    DetailsHeaderActions(it)
                }
            }
        }
    }
}

@Composable
private fun DetailsHeaderArtworkFrame(
    size: Dp,
    shape: Shape,
    containerColor: Color,
    content: @Composable BoxScope.() -> Unit
) {
    var revealed by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }
    val progress by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        label = "artworkReveal"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                val scale = 0.92f + 0.08f * progress
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation = 12.dp, shape = shape)
            .clip(shape)
            .background(containerColor),
        content = content
    )
}

@Composable
private fun DetailsHeaderTitles(
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)?,
    alignment: Alignment,
    textAlign: TextAlign
) {
    val colors = MaterialTheme.colorScheme

    Box(
        contentAlignment = alignment,
        modifier = Modifier.fillMaxWidth()
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.onSurface) {
            ProvideTextStyle(
                value = MaterialTheme.typography.displaySmallEmphasized.copy(textAlign = textAlign),
                content = title
            )
        }
    }
    subtitle?.let {
        Spacer(Modifier.height(4.dp))
        Box(
            contentAlignment = alignment,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(LocalContentColor provides colors.onSurfaceVariant) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.headlineSmallEmphasized.copy(textAlign = textAlign),
                    content = it
                )
            }
        }
    }
}

@Composable
private fun DetailsHeaderInfo(
    arrangement: Arrangement.Horizontal,
    content: @Composable () -> Unit
) {
    FlowRow(
        horizontalArrangement = arrangement,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) { content() }
}

@Composable
private fun DetailsHeaderActions(content: @Composable RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(ButtonDefaults.MediumContainerHeight),
        content = content
    )
}

object DetailsHeaderDefaults {

    @Composable
    fun Title(
        text: String,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = text,
            maxLines = 1,
            modifier = modifier.basicMarquee()
        )
    }

    @Composable
    fun Subtitle(
        text: String,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = text,
            maxLines = 1,
            modifier = modifier.basicMarquee()
        )
    }

    @Composable
    fun InfoChip(
        text: String,
        modifier: Modifier = Modifier,
        @DrawableRes icon: Int? = null,
        containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Surface(
            shape = CircleShape,
            color = containerColor,
            contentColor = contentColor,
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = if (icon != null) 10.dp else 14.dp,
                    end = 14.dp,
                    top = 6.dp,
                    bottom = 6.dp
                )
            ) {
                icon?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    maxLines = 1
                )
            }
        }
    }

    @Composable
    fun Backdrop(
        model: Any?,
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.55f }
                    .cloudy(17)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, containerColor)
                        )
                    )
            )
        }
    }

    @Composable
    fun GradientBackdrop(
        top: Color = MaterialTheme.colorScheme.primaryContainer,
        bottom: Color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(top, bottom)))
        )
    }
}

@Composable
fun DetailsHeaderPlaybackActions(
    isFavorite: Boolean,
    onShuffle: () -> Unit,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val iconSize = ButtonDefaults.MediumIconSize
    val interactionSources = List(3) { rememberInteractionSource() }

    ButtonGroup(
        overflowIndicator = {},
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        customItem(
            buttonGroupContent = {
                FilledTonalButton(
                    onClick = onShuffle,
                    shapes = ButtonShapes(
                        shape = ButtonGroupDefaults.connectedLeadingButtonShape,
                        pressedShape = ButtonGroupDefaults.connectedLeadingButtonPressShape
                    ),
                    interactionSource = interactionSources[0],
                    modifier = Modifier
                        .animateWidth(interactionSource = interactionSources[0])
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = stringResource(R.string.shuffle),
                        modifier = Modifier.size(iconSize)
                    )
                }
            },
            menuContent = {}
        )

        customItem(
            buttonGroupContent = {
                Button(
                    onClick = onPlay,
                    shapes = ButtonShapes(
                        shape = ShapeDefaults.Small,
                        pressedShape = ButtonGroupDefaults.connectedMiddleButtonPressShape
                    ),
                    interactionSource = interactionSources[1],
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier
                        .animateWidth(interactionSources[1])
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.widget_play),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.play),
                        style = MaterialTheme.typography.titleMediumEmphasized
                    )
                }
            },
            menuContent = {}
        )
        customItem(
            buttonGroupContent = {
                ToggleButton(
                    checked = isFavorite,
                    onCheckedChange = { onToggleFavorite() },
                    buttonSize = ToggleButtonSize.Medium,
                    shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                    colors = ToggleButtonColors(
                        containerColor = colors.secondaryContainer,
                        contentColor = colors.onSecondaryContainer,
                        disabledContainerColor = colors.onSurface.copy(alpha = 0.1f),
                        disabledContentColor = colors.onSurface.copy(alpha = 0.38f),
                        checkedContainerColor = colors.tertiary,
                        checkedContentColor = colors.onTertiary
                    ),
                    interactionSource = interactionSources[2],
                    modifier = Modifier
                        .animateWidth(interactionSources[2])
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite
                        ),
                        contentDescription = stringResource(
                            if (isFavorite) R.string.remove_favorite else R.string.add_favorite
                        ),
                        modifier = Modifier.size(iconSize)
                    )
                }
            },
            menuContent = {}
        )
    }
}

@Composable
fun BoxScope.DetailsHeaderArtwork(
    model: Any?,
    @DrawableRes placeholderIcon: Int,
    contentDescription: String? = null
) {
    Icon(
        painter = painterResource(placeholderIcon),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize(0.36f)
    )
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
