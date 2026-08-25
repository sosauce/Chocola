@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.presentation.components.MoreOptions
import com.sosauce.chocola.presentation.components.dialogs.DeletionDialog
import com.sosauce.chocola.presentation.components.dialogs.tracksDetails.TracksDetailsDialog
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.chocola.utils.rememberInteractionSource

@Composable
fun MoreOptionsButton(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onShrinkToSearchbar: () -> Unit = {}
) {

    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showMoreDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    val interactionSources = List(2) { rememberInteractionSource() }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val moreOptions = listOf(
//        MoreOptions(
//            text = { stringResource(R.string.open_eq) },
//            onClick = {
//                try {
//                    val intent =
//                        Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
//                            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicState.audioSessionAudio)
//                            putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
//                            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
//                        }
//                    activityResultLauncher.launch(intent)
//                } catch (e: Exception) {
//                    Toast.makeText(context, "Unable to open system equalizer", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            },
//            icon = R.drawable.eq
//        ),
        MoreOptions(
            text = { stringResource(R.string.edit) },
            onClick = {
                showMoreDialog = false
                onNavigate(
                    Screen.MetadataEditor(
                        musicState.track.path,
                        musicState.track.uri.toString()
                    )
                )
            },
            icon = R.drawable.edit_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, musicState.track.album) },
            onClick = {
                showMoreDialog = false
                onNavigate(
                    Screen.AlbumsDetails(musicState.track.album)
                )
            },
            icon = androidx.media3.session.R.drawable.media3_icon_album
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, musicState.track.artist) },
            onClick = {
                showMoreDialog = false
                onNavigate(
                    Screen.ArtistsDetails(musicState.track.artist)
                )
            },
            icon = R.drawable.artist_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.add_to_playlist) },
            onClick = { showPlaylistDialog = true },
            icon = R.drawable.playlist_add
        )
    )

    if (showDetailsDialog) {
        TracksDetailsDialog(
            track = musicState.track,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(musicState.track.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = listOf(musicState.track),
            onDismissRequest = { showDeletionDialog = false }
        )
    }

    ButtonGroup(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        overflowIndicator = {}
    ) {
        customItem(
            buttonGroupContent = {
                IconButton(
                    onClick = { onNavigate(Screen.Lyrics) },
                    shape = RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp, topEnd = 4.dp, bottomEnd = 4.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    interactionSource = interactionSources[0],
                    modifier = Modifier
                        .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSources[0])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lyrics_filled),
                        contentDescription = null
                    )
                }
            },
            menuContent = {}
        )

        customItem(
            buttonGroupContent = {
                IconButton(
                    onClick = {
                        onShrinkToSearchbar()
                        onNavigate(Screen.Queue)
                    },
                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 50.dp, bottomEnd = 50.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    interactionSource = interactionSources[1],
                    modifier = Modifier
                        .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSources[1])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.queue),
                        contentDescription = null
                    )
                }
            },
            menuContent = {}
        )
    }

}
