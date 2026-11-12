@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.chocola.feature.playlists.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.sosauce.chocola.feature.playlists.presentation.model.PlaylistUi

/**
 * Unified UI for creating and editing a playlist, not a dialog.
 * @param playlist optional playlist to prefill value, will be null when creating one.
 */
@Composable
fun ManagePlaylist(
    playlist: PlaylistUi?
) {

    var showEmojiPicker by remember { mutableStateOf(false) }

    if (showEmojiPicker) {
        ModalBottomSheet(
            onDismissRequest = { showEmojiPicker = false },
            dragHandle = null
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                factory = { ctx ->
                    EmojiPickerView(ctx).apply {
                        setOnEmojiPickedListener(onEmojiPickedListener = {
//                            playlistViewModel.handlePlaylistActions(
//                                PlaylistActions.UpdateStateEmoji(
//                                    it.emoji
//                                )
//                            )
                        })
                    }
                }
            )
        }
    }
}