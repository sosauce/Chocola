@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.components

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.core.designsystem.theme.sansFlexCustomWidth
import com.sosauce.chocola.feature.bakery.presentation.model.AllTimeStatsUi
import com.sosauce.chocola.feature.bakery.presentation.model.ArtistStatUi
import com.sosauce.chocola.feature.bakery.presentation.model.StreakUi
import com.sosauce.chocola.feature.bakery.presentation.model.TrackStatUi
import com.sosauce.nekobites.components.Spacer
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AllTimeHeader(
    stats: AllTimeStatsUi,
    streak: StreakUi,
    modifier: Modifier = Modifier
) {


    val transition = rememberInfiniteTransition(label = "cookies")
    val spinA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(48_000, easing = LinearEasing))
    )
    val spinB by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(36_000, easing = LinearEasing))
    )

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 70.dp, y = (-70).dp)
                    .graphicsLayer { rotationZ = spinA }
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                        shape = MaterialShapes.Cookie12Sided.toShape()
                    )
            )

            Box(
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-60).dp, y = 60.dp)
                    .graphicsLayer { rotationZ = spinB }
                    .background(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.20f),
                        shape = MaterialShapes.Cookie9Sided.toShape()
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = stringResource(R.string.all_time).uppercase(),
                            style = MaterialTheme.typography.labelLargeEmphasized,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.streak),
                                contentDescription = null
                            )
                            Spacer(5.dp)
                            Text(
                                text = streak.current.toString(),
                                style = MaterialTheme.typography.labelLargeEmphasized,
                            )
                        }
                    }

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stats.totalListens.toString(),
                        style = MaterialTheme.typography.displayLargeEmphasized.copy(
                            fontFamily = sansFlexCustomWidth(300f)
                        ),
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = stringResource(R.string.listens),
                        style = MaterialTheme.typography.titleMediumEmphasized.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoPill(
                        label = stringResource(R.string.listening_time),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.sleep_timer_filled),
                                contentDescription = null
                            )
                        },
                        value = formatListeningTime(stats.totalListenedMillis),
                        modifier = Modifier.weight(1f)
                    )
                    stats.topArtist?.let {
                        InfoPill(
                            label = stringResource(R.string.top_artist),
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.artists_filled),
                                    contentDescription = null
                                )
                            },
                            value = it.name,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                stats.topTrack?.let { track ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                            Text(
                                text = stringResource(R.string.top_track),
                                style = MaterialTheme.typography.labelSmallEmphasized.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                            )
                            Spacer(10.dp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MiniArtwork(artworkUri = track.artworkUri)

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.titleSmallEmphasized.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.basicMarquee()
                                    )
                                    Text(
                                        text = track.artist,
                                        style = MaterialTheme.typography.bodySmallEmphasized.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.basicMarquee()
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 6.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.music_note),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(4.dp)
                                        Text(
                                            text = track.listenCount.toString(),
                                            style = MaterialTheme.typography.labelMediumEmphasized
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoPill(
    label: String,
    icon: @Composable () -> Unit,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialShapes.Circle.toShape()
                ),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onTertiaryContainer) {
                icon()
            }
        }
        Spacer(20.dp)

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmallEmphasized.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLargeEmphasized.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MiniArtwork(artworkUri: Uri?) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(SquircleShape(smoothing = CornerSmoothing.Full))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.music_note),
            contentDescription = null,
            tint = contentColorFor(MaterialTheme.colorScheme.primaryContainer)
        )
        AsyncImage(
            model = artworkUri,
            contentDescription = stringResource(R.string.artwork),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun formatListeningTime(millis: Long): String {
    val totalMinutes = (millis / 60_000L).coerceAtLeast(0L)
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return if (hours > 0L) {
        stringResource(R.string.bakery_hours_minutes_short, hours, minutes)
    } else {
        stringResource(R.string.bakery_minutes_short, minutes)
    }
}

@Preview(showBackground = true, widthDp = 380, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AllTimeHeaderPreview() {

    ChocolaTheme {
        AllTimeHeader(
            stats = AllTimeStatsUi(
                totalListens = 12_482,
                totalListenedMillis = 218_340_000L,
                topTrack = TrackStatUi(
                    mediaId = "1",
                    title = "Cinnamon Girl",
                    artist = "Lana Del Rey",
                    album = "Norman Fucking Rockwell!",
                    artworkUri = null,
                    listenCount = 412
                ),
                topArtist = ArtistStatUi(
                    name = "Lana Del Rey",
                    listenCount = 1_284
                )
            ),
            streak = StreakUi(current = 12, best = 36),
            modifier = Modifier.padding(16.dp)
        )
    }
}
