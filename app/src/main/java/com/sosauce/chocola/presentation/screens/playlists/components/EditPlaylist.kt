@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("AssignedValueIsNeverRead")

package com.sosauce.chocola.presentation.screens.playlists.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.Playlist
import com.sosauce.chocola.domain.actions.PlaylistActions
import com.sosauce.chocola.presentation.components.EmojiPicker
import com.sosauce.chocola.presentation.components.Spacer
import com.sosauce.chocola.presentation.components.animations.Icon
import com.sosauce.chocola.presentation.components.animations.rememberClipboardIconController
import com.sosauce.chocola.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.chocola.utils.ColorUtils
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.rememberInteractionSource
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds


@SuppressLint("ResourceType")
@Composable
fun EditPlaylist(
    playlist: Playlist?,
    onHandlePlaylistActions: ((PlaylistActions) -> Unit)?,
    onDismissRequest: () -> Unit,
) {
    val isCreatingPlaylist = playlist == null

    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val state by playlistViewModel.state.collectAsStateWithLifecycle()
    val localizedPlaylist = stringResource(R.string.playlist)

    var newPlaylist by remember { mutableStateOf(playlist ?: Playlist()) }
    val name = rememberTextFieldState(initialText = newPlaylist.name)
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showNewTagDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    /**
     *  Handles a name change:
     *  - if the initial playlist is null (means the dialog is in create more), it uses the default name `"Playlist {playlistNumber}"`
     *  - else: it defaults to the playlist name
     */
    LaunchedEffect(name.text) {
        val newName = if (name.text.isEmpty()) {
            playlist?.name ?: "$localizedPlaylist ${state.playlists.size + 1}"
        } else name.text.toString()
        newPlaylist = newPlaylist.copy(
            name = newName
        )
    }

    if (showNewTagDialog) {
        NewTagDialog(
            onDismissRequest = { showNewTagDialog = false },
            tags = newPlaylist.tags.toList(),
            onAddNewTag = { newTag ->
                newPlaylist = newPlaylist.copy(
                    tags = newPlaylist.tags.copyMutate { add(newTag) }
                )
            }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onDismissRequest = { showColorPicker = false },
            onAddNewColor = { newColor ->
                newPlaylist = newPlaylist.copy(
                    color = newColor
                )
            },
            initialColor = Color(newPlaylist.color)
        )
    }



    if (showEmojiPicker) {
        Dialog(
            onDismissRequest = { showEmojiPicker = false },
            properties = DialogProperties(dismissOnBackPress = true, usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
        ) {
            EmojiPicker(
                onEmojiPicked = { newPlaylist = newPlaylist.copy(emoji = it) },
                onDismiss = { showEmojiPicker = false }
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(if (isCreatingPlaylist) R.string.create_playlist else R.string.edit_playlist)) },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isCreatingPlaylist) {
                        playlistViewModel.handlePlaylistActions(PlaylistActions.CreatePlaylist(newPlaylist))
                    } else {
                        onHandlePlaylistActions?.invoke(
                            PlaylistActions.UpsertPlaylist(newPlaylist)
                        )
                    }
                    onDismissRequest()
                },
                enabled = isCreatingPlaylist || newPlaylist != playlist,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(if (isCreatingPlaylist) R.string.create else R.string.edit))
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
                painter = painterResource(if (isCreatingPlaylist) R.drawable.playlist_add else R.drawable.edit_rounded),
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = stringResource(R.string.name_and_emoji),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                )
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { showEmojiPicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (newPlaylist.emoji.isNotBlank()) {
                                AnimatedContent(newPlaylist.emoji) {
                                    Text(
                                        text = it,
                                        fontSize = 35.sp
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.add_emoji_rounded),
                                    contentDescription = stringResource(R.string.emoji),
                                    modifier = Modifier.size(35.dp)
                                )
                            }
                        }
                        OutlinedTextField(
                            state = name,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text(newPlaylist.name) }
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.color),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                )
                val colorCard = if (newPlaylist.color != -1) Color(newPlaylist.color) else MaterialTheme.colorScheme.surfaceContainerHighest

                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = { showColorPicker = true },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorCard,
                            contentColor = if (colorCard.luminance() > 0.5f) Color.Black else Color.White
                        )
                    ) {
                        val icon = if (newPlaylist.color != -1) R.drawable.edit_filled else R.drawable.add
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp)
                        )
                    }


                    val randomColorStatus = rememberClipboardIconController()
                    Spacer(5.dp)

                    randomColorStatus.Icon(R.drawable.shuffle) {
                        newPlaylist = newPlaylist.copy(
                            color = ColorUtils.randomColor(1f).toArgb()
                        )
                        randomColorStatus.setSuccess()
                    }
                }
                Text(
                    text = stringResource(R.string.tags),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                )
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                ) {
                    val hapticFeedback = LocalHapticFeedback.current

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        item(
                            key = "Add tag"
                        ) {
                            Button(
                                modifier = Modifier.animateItem(),
                                onClick = { showNewTagDialog = true },
                                shapes = ButtonDefaults.shapes(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add),
                                    contentDescription = null
                                )
                            }
                        }
                        items(
                            items = newPlaylist.tags.toList()
                        ) { tag ->
                            val interactionSource = rememberInteractionSource()
                            val isPressed by interactionSource.collectIsPressedAsState()


                            var canDelete by remember { mutableStateOf(false) }
                            LaunchedEffect(isPressed) {
                                if (isPressed) {
                                    delay(250.milliseconds)
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                    canDelete = true
                                } else {
                                    canDelete = false
                                }
                            }

                            val containerColor by animateColorAsState(
                                if (canDelete) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.surfaceContainerHigh
                            )


                            Button(
                                modifier = Modifier.animateItem(),
                                onClick = {
                                    if (canDelete) {
                                        newPlaylist = newPlaylist.copy(
                                            tags = newPlaylist.tags.copyMutate { remove(tag) }
                                        )
                                    }
                                },
                                interactionSource = interactionSource,
                                shapes = ButtonDefaults.shapes(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = containerColor,
                                    contentColor = contentColorFor(containerColor)
                                )
                            ) {
                                AnimatedContent(canDelete) {
                                    if (it) {
                                        Icon(
                                            painter = painterResource(R.drawable.close),
                                            contentDescription = stringResource(R.string.delete)
                                        )
                                    } else {
                                        Text(tag)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CreatePlaylistDialog(
    onDismissRequest: () -> Unit
) {
    EditPlaylist(null, null, onDismissRequest)
}