@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.story.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.CuteListItem
import com.sosauce.chocola.feature.bakery.presentation.model.ArtistStatUi
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.nekobites.utils.groupedShape

@Composable
fun TopArtistsPage(
    page: StoryPageUi.TopArtists,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item("top_artists") {
            Text(
                text = stringResource(R.string.bakery_top_artists),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )
        }

        if (page.items.isEmpty()) {
            item("empty") {
                EmptyPageText(stringResource(R.string.bakery_empty_desc))
            }
        } else {
            val artists = page.items.take(5)
            itemsIndexed(
                items = artists
            ) { index, artist ->
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
                            isLast = index == artists.lastIndex
                        ),
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = artist.name.firstOrNull()?.uppercase() ?: "",
                                    style = MaterialTheme.typography.titleLargeEmphasized,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        },
                        trailingContent = {
                            Text(
                                text = stringResource(
                                    R.string.listens_count_value,
                                    artist.listenCount
                                ),
                                style = MaterialTheme.typography.labelMediumEmphasized.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    ) {
                        Text(
                            text = artist.name,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TopArtistsPagePreview() {
    MaterialTheme {
        Surface {
            TopArtistsPage(
                page = StoryPageUi.TopArtists(
                    items = listOf(
                        ArtistStatUi("Lana Del Rey", 1_284),
                        ArtistStatUi("Cigarettes After Sex", 982),
                        ArtistStatUi("Mr.Kitty", 756),
                        ArtistStatUi("Mac DeMarco", 611),
                        ArtistStatUi("Beach House", 540)
                    )
                )
            )
        }
    }
}
