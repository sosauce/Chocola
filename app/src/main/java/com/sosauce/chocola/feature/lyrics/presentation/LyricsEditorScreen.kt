package com.sosauce.chocola.feature.lyrics.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.core.domain.model.Lyrics
import com.sosauce.chocola.core.presentation.util.rememberFocusRequester
import com.sosauce.chocola.core.presentation.util.toLyricDuration
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.components.NoXFound
import com.sosauce.nekobites.components.Spacer
import com.sosauce.nekobites.utils.groupedShape
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val FieldCorner = 16.dp
private val FocusedFieldCorner = 24.dp

@Composable
fun LyricsEditorScreen(
    trackPath: String,
    onNavigateBack: () -> Unit
) {

    val viewModel = koinViewModel<LyricsEditorViewModel>(
        parameters = { parametersOf(trackPath) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val layoutDirection = LocalLayoutDirection.current
    val createLyricsFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/lrc")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.saveFile(uri)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    icon = R.drawable.add,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
            contentPadding = PaddingValues(
                start = paddingValues.calculateStartPadding(layoutDirection) + 16.dp,
                end = paddingValues.calculateEndPadding(layoutDirection) + 16.dp,
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
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
                        shape = groupedShape(
                            isFirst = index == 0,
                            isLast = index == state.lyrics.lastIndex
                        ),
                        onRemoveLyric = { viewModel.removeLyricLine(lyric) },
                        onModifyLyric = { newLyric ->
                            viewModel.editLyricLine(index, newLyric)
                        }
                    )
                }
            } else {
                item {
                    NoXFound(
                        modifier = Modifier.animateItem(),
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
    shape: Shape,
    onRemoveLyric: () -> Unit,
    onModifyLyric: (Lyrics) -> Unit
) {

    var showLyricDialog by remember { mutableStateOf(false) }
    var showTimestampDialog by remember { mutableStateOf(false) }
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
                EditorField(
                    state = textFieldState,
                    focusRequester = focusRequester,
                    placeholder = stringResource(R.string.type_lyrics_here)
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

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    textFieldStates.forEachIndexed { index, fieldState ->
                        EditorField(
                            state = fieldState,
                            focusRequester = if (index == 0) focusRequester else null,
                            modifier = Modifier.weight(if (index == 2) 1.4f else 1f),
                            textAlign = TextAlign.Center,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = if (index == 2) ImeAction.Done else ImeAction.Next
                            ),
                            inputTransformation = DigitOnlyTransformation
                        )
                        if (index < 2) {
                            Text(
                                text = if (index == 0) ":" else ".",
                                style = MaterialTheme.typography.headlineMediumEmphasized,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        )
    }

    Surface(
        onClick = { showLyricDialog = true },
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            FilledTonalButton(
                onClick = { showTimestampDialog = true },
                shapes = ButtonDefaults.shapes(),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(
                    text = lyric.timestamp.toLyricDuration(),
                    style = MaterialTheme.typography.labelLargeEmphasized
                )
            }
            if (lyric.lineLyrics.isEmpty()) {
                Text(
                    text = stringResource(R.string.click_to_add_lyrics),
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = lyric.lineLyrics,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.weight(1f)
                )
            }
            IconButton(
                onClick = onRemoveLyric,
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun EditorField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    placeholder: String? = null,
    textAlign: TextAlign = TextAlign.Start,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputTransformation: InputTransformation? = null
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val corner by animateDpAsState(
        targetValue = if (isFocused) FocusedFieldCorner else FieldCorner,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )
    val isTimestamp = inputTransformation != null
    val focusedContainerColor = if (isTimestamp) {
        colors.primaryContainer
    } else colors.surfaceContainerHighest

    TextField(
        state = state,
        modifier = modifier
            .fillMaxWidth()
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
        placeholder = placeholder?.let { { Text(it) } },
        textStyle = if (isTimestamp) {
            MaterialTheme.typography.headlineSmallEmphasized.copy(textAlign = textAlign)
        } else {
            MaterialTheme.typography.bodyLargeEmphasized.copy(textAlign = textAlign)
        },
        lineLimits = if (isTimestamp) TextFieldLineLimits.SingleLine else TextFieldLineLimits.Default,
        keyboardOptions = keyboardOptions,
        inputTransformation = inputTransformation,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(corner),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = colors.surfaceContainerHighest,
            focusedTextColor = contentColorFor(focusedContainerColor),
            unfocusedTextColor = contentColorFor(colors.surfaceContainerHighest),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

private object DigitOnlyTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (asCharSequence().any { !it.isDigit() }) {
            revertAllChanges()
        }
    }
}
