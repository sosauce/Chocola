@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.story.pages

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.theme.sansFlexCustomWidth
import com.sosauce.chocola.feature.bakery.presentation.components.InfoPill
import com.sosauce.chocola.feature.bakery.presentation.components.MiniArtwork
import com.sosauce.chocola.feature.bakery.presentation.model.AlbumStatUi
import com.sosauce.chocola.feature.bakery.presentation.model.ArtistStatUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodType
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.chocola.feature.bakery.presentation.model.StreakUi
import com.sosauce.chocola.feature.bakery.presentation.model.TopDayUi
import com.sosauce.chocola.feature.bakery.presentation.model.TrackStatUi
import com.sosauce.nekobites.components.Spacer


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SummaryPage(
    summary: StoryPageUi.Summary,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val transition = rememberInfiniteTransition(label = "cookies")
    val spinA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(48_000, easing = LinearEasing)),
        label = "spinA"
    )
    val spinB by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(36_000, easing = LinearEasing)),
        label = "spinB"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .offset(x = 90.dp, y = (-40).dp)
                .graphicsLayer { rotationZ = spinA }
                .background(
                    color = colors.primary.copy(alpha = 0.10f),
                    shape = MaterialShapes.Cookie12Sided.toShape()
                )
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.Center)
                .offset(x = (-80).dp, y = 70.dp)
                .graphicsLayer { rotationZ = spinB }
                .background(
                    color = colors.tertiary.copy(alpha = 0.12f),
                    shape = MaterialShapes.Cookie9Sided.toShape()
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── header
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = CircleShape,
                    color = colors.primary,
                    contentColor = colors.onPrimary
                ) {
                    Text(
                        text = stringResource(R.string.bakery_summary_title),
                        style = MaterialTheme.typography.labelLargeEmphasized,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
                Text(
                    text = summary.title,
                    style = MaterialTheme.typography.displaySmallEmphasized,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ── big number
            Column {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = summary.totalListens.toString(),
                        style = MaterialTheme.typography.displayMediumEmphasized.copy(
                            color = colors.primary,
                            fontFamily = sansFlexCustomWidth(300f)
                        ),
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = stringResource(R.string.listens),
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Text(
                    text = stringResource(
                        R.string.listening_time_value,
                        formatListeningTime(summary.totalListenedMillis)
                    ),
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    color = colors.onSurfaceVariant
                )
            }

            summary.topTrack?.let { track ->
                TopTrackCard(track = track)
            }

            // ── pills grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoPill(
                        label = stringResource(R.string.top_artist),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.artists_filled),
                                contentDescription = null
                            )
                        },
                        value = summary.topArtist?.name ?: "-",
                        modifier = Modifier.weight(1f)
                    )
                    InfoPill(
                        label = stringResource(R.string.current_streak),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.streak_filled),
                                contentDescription = null
                            )
                        },
                        value = summary.streak.current.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoPill(
                        label = stringResource(R.string.album),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.album_filled),
                                contentDescription = null
                            )
                        },
                        value = summary.topAlbum?.name ?: "-",
                        modifier = Modifier.weight(1f)
                    )
                    InfoPill(
                        label = stringResource(R.string.bakery_top_day),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.calendar_filled),
                                contentDescription = null
                            )
                        },
                        value = summary.topDay?.label ?: "-",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TopTrackCard(track: TrackStatUi) {
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
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun SummaryPagePreview() {
    MaterialTheme {
        SummaryPage(
            summary = StoryPageUi.Summary(
                period = PeriodId(PeriodType.MONTH, "2026-10"),
                title = "October 2026",
                totalListens = 1_284,
                totalListenedMillis = 21_834_000L,
                topTrack = TrackStatUi(
                    mediaId = "1",
                    title = "Cinnamon Girl",
                    artist = "Lana Del Rey",
                    album = "Norman Fucking Rockwell!",
                    artworkUri = null,
                    listenCount = 412
                ),
                topArtist = ArtistStatUi("Lana Del Rey", 1_024),
                topAlbum = AlbumStatUi(
                    name = "Norman Fucking Rockwell!",
                    artist = "Lana Del Rey",
                    artworkUri = null,
                    listenCount = 892
                ),
                streak = StreakUi(current = 12, best = 36),
                topDay = TopDayUi(
                    epochDay = 20_694,
                    label = "Saturday, October 18",
                    listenCount = 186,
                    listenedMillis = 3_060_000L
                )
            )
        )
    }
}
