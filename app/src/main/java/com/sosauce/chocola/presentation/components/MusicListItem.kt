@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberHiddenTracks
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.dialogs.DeletionDialog
import com.sosauce.chocola.presentation.components.dialogs.tracksDetails.TracksDetailsDialog
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.chocola.utils.LocalScreen
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.nekobites.animations.AnimatedDrawable
import com.sosauce.nekobites.animations.AnimatedDrawableFile
import com.sosauce.nekobites.components.AnimatedSelectedIcon
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun MusicListItem(
    modifier: Modifier = Modifier,
    track: CuteTrack,
    onShortClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    isActive: Boolean,
    trailingContent: @Composable () -> Unit
) {


    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.95f else 1f
    )

    val background by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
    )

    CuteListItem(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        onClick = onShortClick,
        onLongClick = onLongClick,
        backgroundColor = background,
        leadingContent = {
            AnimatedSelectedIcon(
                isSelected = isSelected
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(SquircleShape(smoothing = CornerSmoothing.Full))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.music_note),
                        contentDescription = null
                    )
                    AsyncImage(
                        model = track.artUri,
                        contentDescription = stringResource(R.string.artwork),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        trailingContent = trailingContent
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
            style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.basicMarquee()
        )
    }
}

@Composable
fun DefaultMusicListItemTrailingContent(
    track: CuteTrack,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    extraOptions: List<MoreOptions> = emptyList()
) {

    val currentScreen = LocalScreen.current
    var isDropDownExpanded by remember { mutableStateOf(false) }


    if (currentScreen is Screen.AlbumsDetails && track.trackNumber != 0) {
        Badge(
            containerColor = MaterialTheme.colorScheme.tertiary
        ) {
            Text(
                text = track.trackNumber.toString(),
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.padding(3.dp)
            )
        }
    }


    IconButton(
        onClick = { isDropDownExpanded = true },
        shapes = IconButtonDefaults.shapes()
    ) {
        AnimatedDrawable(
            drawable = AnimatedDrawableFile.MORE_VERT,
            atEnd = isDropDownExpanded
        )
    }

    TrackDropdownMenu(
        track = track,
        isExpanded = isDropDownExpanded,
        onDismissRequest = { isDropDownExpanded = false },
        onNavigate = onNavigate,
        onHandlePlayerActions = onHandlePlayerActions,
        extraOptions = extraOptions
    )
}

@Composable
private fun TrackDropdownMenu(
    track: CuteTrack,
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    extraOptions: List<MoreOptions> = emptyList()
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
//        MoreOptions(
//            text = { "Transform" },
//            onClick = {
//                onDismissRequest()
//                onNavigate(Screen.Transformer(track.uri.toString()))
//            },
//            icon = R.drawable.edit_rounded
//        ),
        MoreOptions(
            text = { stringResource(R.string.add_queue) },
            onClick = { onHandlePlayerActions(PlayerActions.AddToQueue(listOf(track))) },
            icon = R.drawable.add_to_queue
        ),
        MoreOptions(
            text = { stringResource(R.string.play_next) },
            onClick = { onHandlePlayerActions(PlayerActions.PlayNext(track)) },
            icon = R.drawable.fast_forward
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
        )
    ) + extraOptions


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


data class MoreOptions(
    val text: @Composable () -> String,
    val onClick: () -> Unit,
    val icon: Int,
    val tint: Color? = null,
    val enabled: Boolean = true
)
