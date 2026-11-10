@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.story.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.CuteListItem
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.chocola.feature.bakery.presentation.model.TrackStatUi
import com.sosauce.nekobites.utils.groupedShape
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun TopTracksPage(
    page: StoryPageUi.TopTracks,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()

    ) {
        item("top_tracks") {
            Text(
                text = stringResource(R.string.bakery_top_tracks),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )
        }


        if (page.items.isEmpty()) {
            item("empty") {
                EmptyPageText(stringResource(R.string.bakery_empty_desc))
            }
        } else {
            val items = page.items.take(5)
            itemsIndexed(
                items = items
            ) { index, track ->
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (index in listOf(0, 1, 2)) {
                        val icon = when (index) {
                            0 -> R.drawable.badge_1st
                            1 -> R.drawable.badge_2nd
                            2 -> R.drawable.badge_3rd
                            else -> throw IndexOutOfBoundsException()
                        }

                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    } else {
                        Text(
                            text = "${index + 1}.",
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    CuteListItem(
                        onClick = null,
                        shape = groupedShape(
                            isFirst = index == 0,
                            isLast = index == items.lastIndex
                        ),
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .size(50.dp)
                                    .clip(SquircleShape(smoothing = CornerSmoothing.Full))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.music_note),
                                    contentDescription = null
                                )
                                AsyncImage(
                                    model = track.artworkUri,
                                    contentDescription = stringResource(R.string.artwork),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        },
                        trailingContent = {
                            Text(
                                text = stringResource(
                                    R.string.listens_count_value,
                                    track.listenCount
                                ),
                                style = MaterialTheme.typography.labelMediumEmphasized.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    ) {
                        Text(
                            text = track.title,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = track.artist,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun EmptyPageText(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.cookie),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLargeEmphasized,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TopTracksPagePreview() {

    ChocolaTheme {
        TopTracksPage(
            page = StoryPageUi.TopTracks(
                items = listOf(
                    TrackStatUi("1", "Cinnamon Girl", "Lana Del Rey", "NFR!", null, 412),
                    TrackStatUi("2", "After Dark", "Mr.Kitty", "Time", null, 388),
                    TrackStatUi("3", "Apocalypse", "Cigarettes After Sex", "Cry", null, 302),
                    TrackStatUi(
                        "4",
                        "Chamber of Reflection",
                        "Mac DeMarco",
                        "Salad Days",
                        null,
                        264
                    ),
                    TrackStatUi("5", "Space Song", "Beach House", "Depression Cherry", null, 241)
                )
            )
        )
    }
}
