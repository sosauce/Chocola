package com.sosauce.chocola.presentation.screens.lyrics

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.domain.model.Lyrics
import com.sosauce.chocola.presentation.components.CuteListItem
import com.sosauce.chocola.presentation.components.NoXFound
import com.sosauce.chocola.presentation.components.Spacer
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.utils.rememberFocusRequester
import com.sosauce.chocola.utils.toLyricDuration
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LyricsEditorScreen(
    trackPath: String,
    onNavigateBack: () -> Unit
) {


    val viewModel = koinViewModel<LyricsEditorViewModel>(
        parameters = { parametersOf(trackPath) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val createLyricsFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/lrc")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.saveFile(uri)
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 15.dp)
            ) {
                AnimatedFab(
                    onClick = onNavigateBack,
                    icon = R.drawable.back,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
                Spacer()
                AnimatedFab(
                    onClick = {
                        viewModel.addLyricLine(
                            Lyrics(
                                timestamp = 0,
                                lineLyrics = ""
                            )
                        )
                    },
                    icon = R.drawable.add
                )
                AnimatedFab(
                    onClick = {
                        val fileName = trackPath
                            .substringAfterLast('/')
                            .substringBeforeLast('.')
                        createLyricsFileLauncher.launch("${fileName}.lrc")
                    },
                    icon = R.drawable.save_filled,
                    enabled = state.lyrics.isNotEmpty()
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.lyrics.isNotEmpty()) {
                itemsIndexed(
                    items = state.lyrics,
                    key = { _, lyric -> lyric.id }
                ) { index, lyric ->
                    LyricLineItem(
                        modifier = Modifier.animateItem(),
                        lyric = lyric,
                        onRemoveLyric = { viewModel.removeLyricLine(lyric) },
                        onModifyLyric = { newLyric ->
                            viewModel.editLyricLine(index, newLyric)
                        }
                    )
                }
            } else {
                item {
                    NoXFound(
                        icon = R.drawable.lyrics_rounded,
                        headlineText = R.string.no_lyrics_added_yet,
                        bodyText = R.string.click_plus_to_start
                    )
                }
            }
        }


    }
}


@Composable
private fun LyricLineItem(
    modifier: Modifier = Modifier,
    lyric: Lyrics,
    onRemoveLyric: () -> Unit,
    onModifyLyric: (Lyrics) -> Unit
) {

    var showLyricDialog by remember { mutableStateOf(false) }
    var showTimestampDialog by remember { mutableStateOf(false) }
    val textStyle = LocalTextStyle.current
    val focusRequester = rememberFocusRequester()



    if (showLyricDialog) {
        val textFieldState = rememberTextFieldState()

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        AlertDialog(
            onDismissRequest = { showLyricDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.lyrics_rounded),
                    contentDescription = null
                )
            },
            title = { Text(stringResource(R.string.add_lyrics)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newLyric = lyric.copy(
                            lineLyrics = textFieldState.text.toString()
                        )
                        onModifyLyric(newLyric)
                        showLyricDialog = false
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLyricDialog = false },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                OutlinedTextField(
                    state = textFieldState,
                    placeholder = {
                        Text(stringResource(R.string.type_lyrics_here))
                    },
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }
        )
    }

    if (showTimestampDialog) {
        val timeParts = lyric.timestamp.toLyricDuration().split(':', '.')
        val textFieldStates = List(3) { index ->
            val initialChunk = timeParts.getOrElse(index) { "" }
            rememberTextFieldState(initialText = initialChunk)
        }

        AlertDialog(
            onDismissRequest = { showTimestampDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.timer),
                    contentDescription = null
                )
            },
            title = { Text(stringResource(R.string.adjust_timestamp)) },
            confirmButton = {
                TextButton(
                    onClick = {

                        val minutes = textFieldStates[0].text.toString().toIntOrNull() ?: 0
                        val seconds = textFieldStates[1].text.toString().toIntOrNull() ?: 0
                        val millis = textFieldStates[2].text.toString().toIntOrNull() ?: 0

                        val timestamp = (minutes * 60_000) + (seconds * 1_000) + millis

                        val newLyric = lyric.copy(
                            timestamp = timestamp
                        )

                        onModifyLyric(newLyric)
                        showTimestampDialog = false
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimestampDialog = false },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    repeat(3) { index ->

                        val state = textFieldStates[index]

                        LaunchedEffect(Unit) {
                            if (index == 0) focusRequester.requestFocus()
                        }

                        OutlinedTextField(
                            state = state,
                            modifier = Modifier
                                .then(
                                    if (index == 0) {
                                        Modifier.focusRequester(focusRequester)
                                    } else Modifier
                                )
                                .weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = if (index == 2) ImeAction.Done else ImeAction.Next
                            ),
                            textStyle = textStyle.copy(
                                textAlign = TextAlign.Center
                            ),
                            lineLimits = TextFieldLineLimits.SingleLine,
                            inputTransformation = DigitOnlyTransformation,
                        )
                    }

                }

            }
        )
    }


    CuteListItem(
        modifier = modifier,
        onClick = { showLyricDialog = true },
        trailingContent = {
            IconButton(
                onClick = onRemoveLyric,
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null
                )
            }
        },
        leadingContent = {
            TextButton(
                onClick = { showTimestampDialog = true },
                shapes = ButtonDefaults.shapes()
            ) {
                Text(lyric.timestamp.toLyricDuration())
            }
        }
    ) {
        if (lyric.lineLyrics.isEmpty()) {
            Text(
                text = stringResource(R.string.click_to_add_lyrics),
                style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        } else {
            Text(lyric.lineLyrics)
        }
    }
}

private object DigitOnlyTransformation: InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (asCharSequence().any { !it.isDigit() }) {
            revertAllChanges()
        }
    }
}