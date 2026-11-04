package com.sosauce.chocola.feature.settings.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.sosauce.chocola.core.designsystem.NewFolderPolicy
import com.sosauce.chocola.core.presentation.preferences.rememberMinTrackDuration
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.designsystem.components.CuteListItem
import com.sosauce.chocola.core.presentation.components.MusicListItem
import com.sosauce.chocola.feature.settings.presentation.components.ClickableSettingsCard
import com.sosauce.chocola.feature.settings.presentation.components.SettingsWithTitle
import com.sosauce.chocola.feature.settings.presentation.components.SliderSettingsCards
import com.sosauce.chocola.feature.settings.presentation.components.foldersView
import com.sosauce.chocola.core.domain.util.copyMutate
import com.sosauce.chocola.core.presentation.preferences.rememberAutoAddSubfolders
import com.sosauce.chocola.core.presentation.preferences.rememberHideSingleAlbums
import com.sosauce.chocola.core.presentation.preferences.rememberNewFolderPolicy
import com.sosauce.chocola.feature.settings.presentation.components.SettingOption
import com.sosauce.chocola.feature.settings.presentation.components.SettingsMultipleChoice
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSwitch

@Composable
fun SettingsLibrary(
    state: LibraryState,
    contentPaddingValues: PaddingValues,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onHandleLibraryActions: (LibraryActions) -> Unit
) {

    val context = LocalContext.current
    var minTrackDuration by rememberMinTrackDuration()
    var autoAddSub by rememberAutoAddSubfolders()
    var newFolderPolicy by rememberNewFolderPolicy()
    var showSingleAlbums by rememberHideSingleAlbums()

    val policies = listOf(
        SettingOption(
            value = NewFolderPolicy.WHITELIST,
            labelRes = R.string.whitelist
        ),
        SettingOption(
            value = NewFolderPolicy.BLACKLIST,
            labelRes = R.string.blacklist
        ),
    )

    val safAudioPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            onHandleLibraryActions(
                LibraryActions.ChangeSafTracks(
                    tracks = uris.fastMap { it.toString() },
                    add = true
                )
            )

            uris.fastForEach { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }


    val folderTreePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            // only tree ids from this provider map to filesystem paths
            if (uri.authority != "com.android.externalstorage.documents") {
                Toast.makeText(context, R.string.folder_tree_unsupported, Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            // tree id looks like "primary:Music" (internal) or "1234-ABCD:Music" (SD card)
            val (volume, rel) = DocumentsContract.getTreeDocumentId(uri).split(':', limit = 2) + ""
            val base = if (volume == "primary") Environment.getExternalStorageDirectory().path else "/storage/$volume"
            autoAddSub = true
            onHandleLibraryActions(LibraryActions.WhitelistFolders(listOf("$base/$rel".trimEnd('/'))))
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
                    bottomDp = 2.dp,
                    text = stringResource(R.string.rescan_tracks),
                    optionalDescription = R.string.rescan_tracks_desc
                )
                ClickableSettingsCard(
                    onClick = {
                        try {
                            folderTreePicker.launch(null)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, R.string.no_folder_picker, Toast.LENGTH_SHORT).show()
                        }
                    },
                    topDp = 2.dp,
                    bottomDp = 2.dp,
                    text = stringResource(R.string.whitelist_folder_tree),
                    optionalDescription = R.string.whitelist_folder_tree_desc
                )
                SettingsSwitch(
                    checked = autoAddSub,
                    onCheckedChange = { autoAddSub = !autoAddSub },
                    topDp = 2.dp,
                    bottomDp = 2.dp,
                    text = stringResource(R.string.auto_whitelist_sub),
                    optionalDescription = R.string.auto_whitelist_sub_desc
                )
                SettingsSwitch(
                    checked = showSingleAlbums,
                    onCheckedChange = { showSingleAlbums = !showSingleAlbums },
                    topDp = 2.dp,
                    bottomDp = 2.dp,
                    text = stringResource(R.string.hide_single_album),
                    optionalDescription = R.string.hide_single_album_desc
                )
                SettingsMultipleChoice(
                    active = newFolderPolicy,
                    onSelectValue = { newFolderPolicy = it },
                    items = policies,
                    topDp = 2.dp,
                    bottomDp = 24.dp,
                    text = R.string.new_folder_policy
                )
            }
        }
        foldersView(
            whitelisted = state.whitelistedFolders,
            blacklisted = state.blacklistedFolders,
            onBlacklist = { newList ->
                onHandleLibraryActions(
                    LibraryActions.BlacklistFolders(newList)
                )
            },
            onWhitelist = { newList ->
                onHandleLibraryActions(
                    LibraryActions.WhitelistFolders(newList)
                )
            }
        )

        item {
            SettingsWithTitle(
                title = R.string.hidden_tracks
            ) {
                if (state.hiddenTracks.isEmpty()) {
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
            items = state.hiddenTracks,
            key = { _, track -> track.mediaId }
        ) { index, track ->
            MusicListItem(
                modifier = Modifier.padding(horizontal = 11.dp),
                track = track,
                isActive = false,
                onShortClick = {},
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(
                    topStart = if (index == 0) 24.dp else 2.dp,
                    topEnd = if (index == 0) 24.dp else 2.dp,
                    bottomStart = if (index == state.hiddenTracks.lastIndex) 24.dp else 2.dp,
                    bottomEnd = if (index == state.hiddenTracks.lastIndex) 24.dp else 2.dp,
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
                        .animateItem()
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

        if (state.safTracks.isEmpty()) {
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
            items = state.safTracks,
            key = { _, track -> track.mediaId }
        ) { index, track ->
            MusicListItem(
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = 11.dp),
                track = track,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(
                    topStart = 2.dp,
                    topEnd = 2.dp,
                    bottomStart = if (index == state.safTracks.lastIndex) 24.dp else 2.dp,
                    bottomEnd = if (index == state.safTracks.lastIndex) 24.dp else 2.dp
                ),
                isActive = false,
                onShortClick = {
                    onHandlePlayerActions(
                        PlayerActions.PlayFromSource(
                            mediaId = track.mediaId,
                            source = PlaySource.ExplicitTracks(state.safTracks)
                        )
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            onHandleLibraryActions(
                                LibraryActions.ChangeSafTracks(
                                    tracks = listOf(track.uri.toString()),
                                    add = false
                                )
                            )
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