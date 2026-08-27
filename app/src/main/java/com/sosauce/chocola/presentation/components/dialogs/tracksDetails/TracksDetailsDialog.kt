@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.components.dialogs.tracksDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.helpers.ScopedViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun TracksDetailsDialog(
    track: CuteTrack,
    onDismissRequest: () -> Unit
) {
    ScopedViewModel {
        val viewModel = koinViewModel<TracksDetailsDialogViewModel>(
            parameters = { parametersOf(track) }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()



        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest,
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(
                        text = stringResource(R.string.okay)
                    )
                }
            },
            text = {
                LoadingBox(
                    isLoading = state.isLoading
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2)
                    ) {
                        item(
                            key = "Main info",
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(15.dp)
                                            .clip(SquircleShape(smoothing = CornerSmoothing.Full))
                                    ) {
                                        // This will only display if the below art doesn't load
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.music_note),
                                                contentDescription = null
                                            )
                                        }
                                        AsyncImage(
                                            model = track.artUri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = track.title,
                                            modifier = Modifier
                                                .basicMarquee()
                                        )
                                        Text(
                                            text = track.artist,
                                            modifier = Modifier.basicMarquee()
                                        )
                                    }
                                }
                            }
                        }

                        item(
                            key = "Spacer",
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            Spacer(Modifier.height(15.dp))
                        }

                        item(
                            key = "About track",
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            Text(
                                text = stringResource(R.string.about_track),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                            )
                        }

                        itemsIndexed(
                            items = state.trackInfo
                        ) { index, details ->

                            val cardShape = RoundedCornerShape(
                                topStart = if (index == 0) 24.dp else 4.dp,
                                topEnd = if (index == 1) 24.dp else 4.dp,
                                bottomStart = if (index == state.trackInfo.lastIndex - 1) 24.dp else 4.dp,
                                bottomEnd = if (index == state.trackInfo.lastIndex) 24.dp else 4.dp
                            )
                            TrackDetails(
                                details = details,
                                shape = cardShape
                            )
                        }

                        item(
                            key = "About file",
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            Text(
                                text = stringResource(R.string.about_file),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                            )
                        }

                        itemsIndexed(
                            items = state.fileInfo
                        ) { index, details ->

                            val cardShape = RoundedCornerShape(
                                topStart = if (index == 0) 24.dp else 4.dp,
                                topEnd = if (index == 1) 24.dp else 4.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 4.dp
                            )
                            TrackDetails(
                                details = details,
                                shape = cardShape
                            )
                        }

                        item(
                            key = "Track path",
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            TrackDetails(
                                details = TrackDetails(
                                    icon = R.drawable.folder_rounded,
                                    text = R.string.path,
                                    data = track.folder
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 4.dp,
                                    bottomStart = 24.dp,
                                    bottomEnd = 24.dp
                                )
                            )
                        }
                    }
                }
            }
        )
    }

}

@Composable
private fun TrackDetails(
    details: TrackDetails,
    shape: Shape
) {
    Card(
        modifier = Modifier.padding(1.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(details.icon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = stringResource(details.text),
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = details.data,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}

data class TrackDetails(
    val icon: Int,
    val text: Int,
    val data: String
)