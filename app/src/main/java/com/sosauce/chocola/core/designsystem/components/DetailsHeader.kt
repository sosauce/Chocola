@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import com.sosauce.chocola.R
import com.sosauce.nekobites.components.Spacer

private val PosterHeight = 235.dp
private val IconSize = 200.dp
private val CardOverlap = 25.dp
private val SeamY = PosterHeight - CardOverlap

@Composable
fun DetailsHeader(
    title: String,
    subtitle: String?,
    trackCount: Int,
    isFavorite: Boolean,
    onShuffle: () -> Unit,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    poster: @Composable BoxScope.() -> Unit,
    artwork: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    artworkColor: Color = MaterialTheme.colorScheme.primary,
    titleModifier: Modifier = Modifier
) {
    val cookie9 = MaterialShapes.Cookie9Sided.toShape()
    val surface = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PosterHeight + 20.dp) // keep the 20.dp pls
                .align(Alignment.TopCenter)
        ) {
            poster()
        }

        Surface(
            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = SeamY)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = IconSize / 2 + 12.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = title,
                    modifier = titleModifier.basicMarquee(),
                    style = MaterialTheme.typography.headlineLargeEmphasized
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Spacer(5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    FilledTonalButton(
                        onClick = onShuffle,
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .weight(.6f)
                            .size(
                                IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = stringResource(R.string.shuffle)
                        )
                    }

                    Button(
                        onClick = onPlay,
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .weight(1.5f)
                            .size(
                                IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.widget_play),
                            contentDescription = null
                        )
                        Spacer(5.dp)
                        Text(stringResource(R.string.play))
                    }

                    FilledTonalButton(
                        onClick = onToggleFavorite,
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .weight(.6f)
                            .size(
                                IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)
                            )
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite
                            ),
                            contentDescription = stringResource(
                                if (isFavorite) R.string.remove_favorite else R.string.add_favorite
                            )
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = SeamY - IconSize / 2)
        ) {
            Box(
                modifier = Modifier
                    .drawWithCache {
                        val path = cookie9.createOutline(size, layoutDirection, this)
                            .let { outline ->
                                Path().apply {
                                    when (outline) {
                                        is Outline.Generic -> addPath(outline.path)
                                        is Outline.Rounded -> addRoundRect(outline.roundRect)
                                        is Outline.Rectangle -> addRect(outline.rect)
                                    }
                                }
                            }
                        onDrawBehind {
                            withTransform(
                                {
                                    scale(
                                        scaleX = 1.08f,
                                        scaleY = 1.08f,
                                        pivot = center
                                    )
                                }
                            ) {
                                drawPath(
                                    path = path,
                                    color = surface
                                )
                            }
                        }
                    }
                    .size(IconSize)
                    .clip(cookie9)
                    .background(
                        color = artworkColor,
                        shape = cookie9
                    ),
                contentAlignment = Alignment.Center
            ) {
                artwork()
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onTertiaryContainer) {
                    Icon(
                        painter = painterResource(R.drawable.music_note),
                        contentDescription = null
                    )
                    Text(text = trackCount.toString())
                }
            }
        }
    }
}

/**
 * Blurred artwork used as the poster behind a [DetailsHeader].
 */
@Composable
fun DetailsHeaderPoster(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .cloudy(15),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Artwork shown inside the cookie badge of a [DetailsHeader].
 */
@Composable
fun DetailsHeaderArtwork(
    model: Any?,
    @DrawableRes placeholderIcon: Int? = null,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    if (placeholderIcon != null) {
        Icon(
            painter = painterResource(placeholderIcon),
            contentDescription = null,
            tint = contentColorFor(MaterialTheme.colorScheme.primary),
            modifier = modifier.fillMaxSize(0.5f)
        )
    }
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
