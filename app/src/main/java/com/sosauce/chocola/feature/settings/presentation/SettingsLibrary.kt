@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.NewFolderPolicy
import com.sosauce.chocola.core.designsystem.components.WavySlider
import com.sosauce.chocola.core.domain.player.PlaySource
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.MusicListItem
import com.sosauce.chocola.core.presentation.preferences.rememberAutoAddSubfolders
import com.sosauce.chocola.core.presentation.preferences.rememberHideSingleAlbums
import com.sosauce.chocola.core.presentation.preferences.rememberMinTrackDuration
import com.sosauce.chocola.core.presentation.preferences.rememberNewFolderPolicy
import com.sosauce.chocola.feature.settings.presentation.components.foldersView
import com.sosauce.nekobites.components.settings.SettingOption
import com.sosauce.nekobites.components.settings.SettingsChoice
import com.sosauce.nekobites.components.settings.SettingsClickable
import com.sosauce.nekobites.components.settings.SettingsEmptyState
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsGroupLabel
import com.sosauce.nekobites.components.settings.SettingsSlider
import com.sosauce.nekobites.components.settings.SettingsSwitch
import com.sosauce.nekobites.components.settings.settingsItemShape

private val LazyItemModifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 2.dp)

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
    var hideSingleAlbums by rememberHideSingleAlbums()

    val policies = listOf(
        SettingOption(value = NewFolderPolicy.WHITELIST, labelRes = R.string.whitelist),
        SettingOption(value = NewFolderPolicy.BLACKLIST, labelRes = R.string.blacklist)
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
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
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
            val base =
                if (volume == "primary") Environment.getExternalStorageDirectory().path else "/storage/$volume"
            autoAddSub = true
            onHandleLibraryActions(LibraryActions.WhitelistFolders(listOf("$base/$rel".trimEnd('/'))))
        }

    LazyColumn(
        modifier = Modifier.statusBarsPadding(),
        contentPadding = PaddingValues(bottom = contentPaddingValues.calculateBottomPadding() + 16.dp)
    ) {

        item(key = "scan") {
            SettingsGroup(title = stringResource(R.string.scan)) {
                SettingsSlider(
                    title = stringResource(R.string.min_track_length_text),
                    description = stringResource(R.string.min_track_duration_desc),
                    value = minTrackDuration,
                    onValueChange = { minTrackDuration = it },
                    unit = "s",
                    slider = { state, onChange -> WavySlider(state, onChange) }
                )
                SettingsClickable(
                    title = stringResource(R.string.rescan_tracks),
                    description = stringResource(R.string.rescan_tracks_desc),
                    icon = R.drawable.library,
                    onClick = { onHandleLibraryActions(LibraryActions.ForceRescan) }
                )
                SettingsClickable(
                    title = stringResource(R.string.whitelist_folder_tree),
                    description = stringResource(R.string.whitelist_folder_tree_desc),
                    icon = R.drawable.folder_rounded,
                    onClick = {
                        try {
                            folderTreePicker.launch(null)
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(context, R.string.no_folder_picker, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
                SettingsSwitch(
                    title = stringResource(R.string.auto_whitelist_sub),
                    description = stringResource(R.string.auto_whitelist_sub_desc),
                    checked = autoAddSub,
                    onCheckedChange = { autoAddSub = !autoAddSub }
                )
                SettingsChoice(
                    title = stringResource(R.string.new_folder_policy),
                    active = newFolderPolicy,
                    options = policies,
                    onSelect = { newFolderPolicy = it }
                )
                SettingsSwitch(
                    title = stringResource(R.string.hide_single_album),
                    description = stringResource(R.string.hide_single_album_desc),
                    checked = hideSingleAlbums,
                    onCheckedChange = { hideSingleAlbums = !hideSingleAlbums }
                )
            }
        }

        foldersView(
            whitelisted = state.whitelistedFolders,
            blacklisted = state.blacklistedFolders,
            onBlacklist = { onHandleLibraryActions(LibraryActions.BlacklistFolders(it)) },
            onWhitelist = { onHandleLibraryActions(LibraryActions.WhitelistFolders(it)) }
        )

        item(key = "hidden_label") {
            SettingsGroupLabel(stringResource(R.string.hidden_tracks))
        }
        if (state.hiddenTracks.isEmpty()) {
            item(key = "hidden_empty") {
                SettingsEmptyState(
                    icon = R.drawable.hide,
                    text = stringResource(R.string.no_hidden_tracks),
                    baseShape = settingsItemShape(isFirst = true, isLast = true),
                    modifier = LazyItemModifier.animateItem()
                )
            }
        }
        itemsIndexed(
            items = state.hiddenTracks,
            key = { _, track -> "hidden_${track.mediaId}" }
        ) { index, track ->
            MusicListItem(
                modifier = Modifier
                    .animateItem()
                    .padding(start = 11.dp, end = 11.dp, bottom = 2.dp),
                track = track,
                isActive = false,
                onShortClick = {},
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = settingsItemShape(
                    isFirst = index == 0,
                    isLast = index == state.hiddenTracks.lastIndex
                ),
                trailingContent = {
                    IconButton(
                        onClick = { onHandleLibraryActions(LibraryActions.UnhideTrack(track.mediaId)) },
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

        item(key = "saf_label") {
            SettingsGroupLabel(stringResource(R.string.saf_manager))
        }
        item(key = "saf_open") {
            SettingsClickable(
                title = stringResource(R.string.open_saf),
                icon = R.drawable.open,
                baseShape = settingsItemShape(isFirst = true, isLast = false),
                onClick = { safAudioPicker.launch(arrayOf("audio/*")) },
                modifier = LazyItemModifier.animateItem()
            )
        }
        if (state.safTracks.isEmpty()) {
            item(key = "saf_empty") {
                SettingsEmptyState(
                    icon = R.drawable.db_off,
                    text = stringResource(R.string.no_saf_tracks),
                    baseShape = settingsItemShape(isFirst = false, isLast = true),
                    modifier = LazyItemModifier.animateItem()
                )
            }
        }
        itemsIndexed(
            items = state.safTracks,
            key = { _, track -> "saf_${track.mediaId}" }
        ) { index, track ->
            MusicListItem(
                modifier = Modifier
                    .animateItem()
                    .padding(start = 11.dp, end = 11.dp, bottom = 2.dp),
                track = track,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = settingsItemShape(
                    isFirst = false,
                    isLast = index == state.safTracks.lastIndex
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
