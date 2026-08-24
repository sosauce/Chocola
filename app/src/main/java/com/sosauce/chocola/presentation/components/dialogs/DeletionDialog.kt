@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.sosauce.chocola.presentation.components.dialogs

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastMap
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.models.Playlist
import com.sosauce.chocola.presentation.screens.playlists.PlaylistActions
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

/**
 * A dialog that should be used as a confirmation to delete
 */
@Composable
fun DeletionDialog(
    tracks: List<CuteTrack>,
    onDismissRequest: () -> Unit
) {

    val context = LocalContext.current
    val resources = LocalResources.current
    val deletionViewModel = koinViewModel<DeletionViewModel>()
    val deleteSongLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.error_deleting_song),
                    Toast.LENGTH_SHORT
                ).show()
            } else { onDismissRequest() }
        }
    val legacyDeleteSongLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            deletionViewModel.deleteTrackBelowAndroid10(tracks.fastMap { it.uri })
            onDismissRequest()
        } else {
            Toast.makeText(context, resources.getString(R.string.error_deleting_song), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(true) {
        deletionViewModel.legacyAskPermission.collectLatest {
            it?.let {
                val request = IntentSenderRequest.Builder(it.intentSender).build()
                legacyDeleteSongLauncher.launch(request)
            }
        }
    }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(pluralStringResource(R.plurals.delete_tracks, tracks.size, tracks.size)) },
        confirmButton = {
            TextButton(
                onClick = { deletionViewModel.deleteTrack(tracks.fastMap { it.uri }, deleteSongLauncher) },
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.trash_rounded),
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = pluralStringResource(R.plurals.delete_tracks_u_sure, tracks.size, tracks.size),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(5.dp))
                HorizontalCenteredHeroCarousel(
                    state = rememberCarouselState { tracks.count() },
                    itemSpacing = 5.dp
                ) { page ->
                    val track = tracks[page]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .maskClip(MaterialTheme.shapes.extraLarge),
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
            }

        }
    )
}

@Composable
fun PlaylistDeletionDialog(
    playlists: List<Playlist>,
    onDismissRequest: () -> Unit,
    onHandlePlaylistAction: (PlaylistActions) -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(pluralStringResource(R.plurals.delete_playlists, playlists.size, playlists.size)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onHandlePlaylistAction(PlaylistActions.DeletePlaylists(playlists))
                    onDismissRequest()
                },
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.trash_rounded),
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = pluralStringResource(R.plurals.delete_playlists_u_sure, playlists.size, playlists.size),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(5.dp))
                HorizontalCenteredHeroCarousel(
                    state = rememberCarouselState { playlists.count() },
                    itemSpacing = 5.dp
                ) { page ->
                    val playlist = playlists[page]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .maskClip(MaterialTheme.shapes.extraLarge),
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
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                if (playlist.emoji.isNotEmpty()) {
                                    Text(
                                        text = playlist.emoji,
                                        fontSize = 25.sp
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.queue_music_rounded),
                                        contentDescription = null
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = playlist.name,
                                    modifier = Modifier
                                        .basicMarquee()
                                )
                                Text(
                                    text = pluralStringResource(R.plurals.tracks, playlist.musics.size, playlist.musics.size),
                                    modifier = Modifier.basicMarquee()
                                )
                            }
                        }
                    }
                }
            }
        }
    )

}