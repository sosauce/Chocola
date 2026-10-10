@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playing.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.domain.util.copyMutate
import com.sosauce.chocola.core.presentation.components.MoreOptions
import com.sosauce.chocola.core.presentation.components.TrackDropdownMenu
import com.sosauce.chocola.core.presentation.components.dialogs.DeletionDialog
import com.sosauce.chocola.core.presentation.components.dialogs.tracksDetails.TracksDetailsDialog
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.core.presentation.components.PlaylistPicker
import com.sosauce.chocola.core.presentation.preferences.rememberHiddenTracks
import com.sosauce.chocola.core.presentation.util.rememberInteractionSource
import com.sosauce.chocola.feature.settings.presentation.components.SettingsScreen
import com.sosauce.nekobites.animations.AnimatedDrawable
import com.sosauce.nekobites.animations.AnimatedDrawableFile

@Composable
fun MoreOptionsButton(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onShrinkToSearchbar: () -> Unit = {},
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {

    var showDetailsDialog by remember { mutableStateOf(false) }
    var showMoreDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    val interactionSources = List(3) { rememberInteractionSource() }

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

    NowPlayingTrackDropdownMenu(
        track = musicState.track,
        isExpanded = showMoreDialog,
        onDismissRequest = { showMoreDialog = false },
        onNavigate = onNavigate,
        onHandlePlayerActions = onHandlePlayerActions
    )

    ButtonGroup(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        overflowIndicator = {}
    ) {
        customItem(
            buttonGroupContent = {
                IconButton(
                    onClick = { onNavigate(Screen.Lyrics) },
                    shape = RoundedCornerShape(
                        topStart = 50.dp,
                        bottomStart = 50.dp,
                        topEnd = 2.dp,
                        bottomEnd = 2.dp
                    ),
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
                    shape = RoundedCornerShape(
                        topStart = 2.dp,
                        bottomStart = 2.dp,
                        topEnd = 2.dp,
                        bottomEnd = 2.dp
                    ),
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
        customItem(
            buttonGroupContent = {
                IconButton(
                    onClick = {
                        showMoreDialog = !showMoreDialog
                    },
                    shape = RoundedCornerShape(
                        topStart = 2.dp,
                        bottomStart = 2.dp,
                        topEnd = 50.dp,
                        bottomEnd = 50.dp
                    ),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    interactionSource = interactionSources[2],
                    modifier = Modifier
                        .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSources[2])
                ) {
                    AnimatedDrawable(
                        drawable = AnimatedDrawableFile.MORE_HOR,
                        atEnd = showMoreDialog
                    )
                }
            },
            menuContent = {}
        )
    }
}

@Composable
private fun NowPlayingTrackDropdownMenu(
    track: CuteTrack,
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var hiddenTracks by rememberHiddenTracks()
    val trackOptions = listOf(
        MoreOptions(
            text = { stringResource(R.string.edit) },
            onClick = { onNavigate(Screen.MetadataEditor(track.path, track.uri.toString())) },
            icon = R.drawable.edit_rounded,
            enabled = !track.isSaf
        ),
        MoreOptions(
            text = { stringResource(R.string.hide_from_tracklist) },
            onClick = { hiddenTracks = hiddenTracks.copyMutate { add(track.mediaId) } },
            icon = R.drawable.hide
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, track.album) },
            onClick = {
                onNavigate(
                    Screen.AlbumsDetails(track.album)
                )
            },
            icon = androidx.media3.session.R.drawable.media3_icon_album
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, track.artist) },
            onClick = {
                onNavigate(
                    Screen.ArtistsDetails(track.artist)
                )
            },
            icon = R.drawable.artist_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.add_to_playlist) },
            onClick = { showPlaylistDialog = true },
            icon = R.drawable.playlist_add
        ),
        MoreOptions(
            text = { stringResource(R.string.open_eq) },
            onClick = {
                onNavigate(
                    Screen.Settings(
                        initialScreen = SettingsScreen.Playback
                    )
                )
            },
            icon = R.drawable.eq
        )
    )


    if (showDetailsDialog) {
        TracksDetailsDialog(
            track = track,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(track.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = listOf(track),
            onDismissRequest = { showDeletionDialog = false }
        )
    }


    DropdownMenuPopup(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShapes()
        ) {
            trackOptions.fastForEachIndexed { index, option ->
                DropdownMenuItem(
                    onClick = {
                        onDismissRequest()
                        option.onClick()
                    },
                    enabled = option.enabled,
                    shape = when (index) {
                        0 -> MenuDefaults.leadingItemShape
                        trackOptions.lastIndex -> MenuDefaults.trailingItemShape
                        else -> MenuDefaults.middleItemShape
                    },
                    text = { Text(option.text()) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(option.icon),
                            contentDescription = null
                        )
                    }
                )
            }
        }
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        ) {
            FilledIconButton(
                onClick = { showDetailsDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                shape = IconButtonDefaults.mediumSquareShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.info_filled),
                    contentDescription = null,
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                )
            }
            FilledIconButton(
                onClick = {
                    ShareCompat.IntentBuilder(context)
                        .setType("audio/*")
                        .setStream(track.path.toUri()) // this instead of passing the path allows to see the file name in the share sheet
                        .setChooserTitle("Share track")
                        .startChooser()
                },
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                shape = IconButtonDefaults.mediumSquareShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.share_filled),
                    contentDescription = null,
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                )
            }
            FilledIconButton(
                onClick = { showDeletionDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                shape = IconButtonDefaults.mediumSquareShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash_rounded_filled),
                    contentDescription = null,
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                )
            }
        }
    }
}
