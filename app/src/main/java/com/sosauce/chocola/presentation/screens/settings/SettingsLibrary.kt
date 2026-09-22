package com.sosauce.chocola.presentation.screens.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAllSafTracks
import com.sosauce.chocola.data.datastore.rememberMinTrackDuration
import com.sosauce.chocola.data.datastore.rememberWhitelistedFolders
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.models.Folder
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlaySource
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.CuteListItem
import com.sosauce.chocola.presentation.components.DefaultMusicListItemTrailingContent
import com.sosauce.chocola.presentation.components.MusicListItem
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.settings.compenents.ClickableSettingsCard
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.presentation.screens.settings.compenents.SliderSettingsCards
import com.sosauce.chocola.presentation.screens.settings.compenents.foldersView
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.selfAlignHorizontally
import kotlin.collections.listOf

@Composable
fun SettingsLibrary(
    safTracksUi: List<CuteTrack>,
    hiddenTracks: List<CuteTrack>,
    folders: List<Folder>,
    musicState: MusicState,
    contentPaddingValues: PaddingValues,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onHandleLibraryActions: (LibraryActions) -> Unit
) {

    val context = LocalContext.current
    var safTracks by rememberAllSafTracks()
    var minTrackDuration by rememberMinTrackDuration()
    var whitelistedFolders by rememberWhitelistedFolders()
    val (whitelisted, blacklisted) = folders.partition { it.path in whitelistedFolders }

    val safAudioPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            safTracks = safTracks.copyMutate { addAll(uris.fastMap { it.toString() }) }

            uris.fastForEach { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }


    LazyColumn(
        contentPadding = contentPaddingValues
    ) {
        item {
            SettingsWithTitle(
                title = R.string.scan
            ) {
                SliderSettingsCards(
                    value = minTrackDuration,
                    onValueChange = { minTrackDuration = it },
                    topDp = 24.dp,
                    bottomDp = 2.dp,
                    text = stringResource(R.string.min_track_length_text),
                    unit = "s",
                    optionalDescription = R.string.min_track_duration_desc
                )
                ClickableSettingsCard(
                    onClick = { onHandleLibraryActions(LibraryActions.ForceRescan) },
                    topDp = 2.dp,
                    bottomDp = 24.dp,
                    text = stringResource(R.string.rescan_tracks),
                    optionalDescription = R.string.rescan_tracks_desc
                )
            }
        }
        foldersView(
            whitelisted = whitelisted,
            blacklisted = blacklisted,
            onBatchEdit = { newList ->
                whitelistedFolders = whitelistedFolders.copyMutate {
                    if (!addAll(newList)) {
                        removeAll(newList)
                    }
                }
            },
            onSingleEdit = { path ->
                whitelistedFolders = whitelistedFolders.copyMutate {
                    if (!add(path)) {
                        remove(path)
                    }
                }
            }
        )

        item {
            SettingsWithTitle(
                title = R.string.hidden_tracks
            ) {
                if (hiddenTracks.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.hide),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.no_hidden_tracks),
                            style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
        itemsIndexed(
            items = hiddenTracks,
            key = { _, track -> track.mediaId }
        ) { index, track ->
            val isActive = musicState.track == track
            MusicListItem(
                modifier = Modifier.padding(horizontal = 11.dp),
                track = track,
                isActive = isActive,
                onShortClick = {},
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(
                    topStart = if (index == 0) 24.dp else 2.dp,
                    topEnd = if (index == 0) 24.dp else 2.dp,
                    bottomStart = if (index == hiddenTracks.lastIndex) 24.dp else 2.dp,
                    bottomEnd = if (index == hiddenTracks.lastIndex) 24.dp else 2.dp,
                ),
                trailingContent = {
                    IconButton(
                        onClick = {
                            onHandleLibraryActions(
                                LibraryActions.UnhideTrack(
                                    track.mediaId
                                )
                            )
                        },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.unhide),
                            contentDescription = null
                        )
                    }
                }
            )
        }

        item {
            SettingsWithTitle(
                title = R.string.saf_manager
            ) {
                CuteListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 11.dp),
                    onClick = { safAudioPicker.launch(arrayOf("audio/*")) },
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 2.dp,
                        bottomEnd = 2.dp
                    ),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.open),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.open_saf)
                    )
                }
            }
        }

        if (safTracksUi.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(
                                topStart = 2.dp,
                                topEnd = 2.dp,
                                bottomStart = 24.dp,
                                bottomEnd = 24.dp
                            )
                        )
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.db_off),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.no_saf_tracks),
                        style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        itemsIndexed(
            items = safTracksUi,
            key = { _, track -> track.mediaId }
        ) { index, track ->
            val isActive = musicState.track == track
            MusicListItem(
                modifier = Modifier.padding(horizontal = 11.dp),
                track = track,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(
                    topStart = 2.dp,
                    topEnd = 2.dp,
                    bottomStart = if (index == safTracksUi.lastIndex) 24.dp else 2.dp,
                    bottomEnd = if (index == safTracksUi.lastIndex) 24.dp else 2.dp
                ),
                isActive = isActive,
                onShortClick = {
                    onHandlePlayerActions(
                        PlayerActions.PlayFromSource(
                            mediaId = track.mediaId,
                            source = PlaySource.ExplicitTracks(safTracksUi)
                        )
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            safTracks = safTracks.copyMutate { remove(track.uri.toString()) }
                        },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}