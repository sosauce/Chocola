@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.metadata.presentation

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kyant.taglib.Picture
import com.sosauce.chocola.R
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.nekobites.animations.AnimatedFab

private val OuterCorner = 24.dp
private val InnerCorner = 2.dp
private val FocusedCorner = 16.dp
private val Gap = 2.dp

@Composable
fun MetadataEditorScreen(
    state: MetadataState,
    trackUri: Uri,
    trackPath: String,
    onHandleMetadataActions: (MetadataActions) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val fields = state.mutablePropertiesMap

    val editSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                onHandleMetadataActions(MetadataActions.SaveChanges)
            } else {
                Toast.makeText(
                    context,
                    resources.getString(R.string.allow_perform_changes),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedFab(
                    onClick = onNavigateUp,
                    icon = R.drawable.back,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
                AnimatedFab(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val intentSender = MediaStore.createWriteRequest(
                                context.contentResolver,
                                listOf(trackUri)
                            ).intentSender
                            editSongLauncher.launch(
                                IntentSenderRequest.Builder(intentSender).build()
                            )
                        } else {
                            onHandleMetadataActions(MetadataActions.SaveChanges)
                        }
                    },
                    icon = R.drawable.check
                )
            }
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding() + 16.dp)
                .imePadding()
        ) {
            ArtworkHero(
                art = state.art,
                onHandleMetadataActions = onHandleMetadataActions
            )

            Spacer(Modifier.height(16.dp))

            FileNamePill(
                name = trackPath.substringAfterLast('/').substringBeforeLast(".")
            )

            FieldGroup {
                FieldTile(
                    value = fields["TITLE"],
                    onValueChange = { fields["TITLE"] = it },
                    label = stringResource(R.string.title),
                    icon = R.drawable.music_note
                )
                FieldTile(
                    value = fields["ARTIST"],
                    onValueChange = { fields["ARTIST"] = it },
                    label = stringResource(R.string.artist),
                    icon = R.drawable.artist_rounded
                )
                FieldTile(
                    value = fields["ALBUM"],
                    onValueChange = { fields["ALBUM"] = it },
                    label = stringResource(R.string.album),
                    icon = androidx.media3.session.R.drawable.media3_icon_album
                )
            }

            FieldGroup {
                Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                    FieldTile(
                        value = fields["DATE"],
                        onValueChange = { fields["DATE"] = it },
                        label = stringResource(R.string.year),
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    FieldTile(
                        value = fields["GENRE"],
                        onValueChange = { fields["GENRE"] = it },
                        label = stringResource(R.string.genre),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                    FieldTile(
                        value = fields["TRACKNUMBER"],
                        onValueChange = { fields["TRACKNUMBER"] = it },
                        label = stringResource(R.string.track_nb),
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    FieldTile(
                        value = fields["DISCNUMBER"],
                        onValueChange = { fields["DISCNUMBER"] = it },
                        label = stringResource(R.string.disc_nb),
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            FieldGroup {
                FieldTile(
                    value = fields["LYRICS"],
                    onValueChange = { fields["LYRICS"] = it },
                    label = stringResource(R.string.lyrics),
                    icon = R.drawable.lyrics_rounded,
                    imeAction = ImeAction.Default,
                    singleLine = false
                )
                LyricsEditorRow(
                    onClick = { onNavigate(Screen.LyricsEditor(trackPath)) }
                )
            }
        }
    }
}

@Composable
private fun ArtworkHero(
    art: Picture?,
    onHandleMetadataActions: (MetadataActions) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            onHandleMetadataActions(MetadataActions.UpdateAudioArt(it ?: Uri.EMPTY))
        }
    val pickImage = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            onClick = pickImage,
            shape = RoundedCornerShape(32.dp),
            color = colors.surfaceContainerHighest,
            modifier = Modifier
                .size(210.dp)
                .shadow(12.dp, RoundedCornerShape(32.dp))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = stringResource(R.string.artwork),
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                if (art != null) {
                    AsyncImage(
                        model = art.data,
                        contentDescription = stringResource(R.string.artwork),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier.height(ButtonDefaults.MinHeight)
        ) {
            Button(
                onClick = { onHandleMetadataActions(MetadataActions.RemoveArtwork) },
                enabled = art != null,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.error)
                ),
                shapes = ButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash_rounded),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.delete))
            }
        }
    }
}

@Composable
private fun FileNamePill(name: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.audio_file),
                contentDescription = stringResource(R.string.file_name),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelLargeEmphasized,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}

@Composable
private fun FieldGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Gap),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            .clip(RoundedCornerShape(OuterCorner)),
        content = content
    )
}

@Composable
private fun FieldTile(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    imeAction: ImeAction = ImeAction.Done,
    singleLine: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val corner by animateDpAsState(
        targetValue = if (isFocused) FocusedCorner else InnerCorner,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )
    val container by animateColorAsState(
        if (isFocused) colors.surfaceContainerHigh else colors.surfaceContainer
    )

    Surface(
        shape = RoundedCornerShape(corner),
        color = container,
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = value ?: "",
            onValueChange = onValueChange,
            label = { Text(text = label, maxLines = 1, modifier = Modifier.basicMarquee()) },
            leadingIcon = icon?.let { { FieldIcon(it) } },
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3,
            keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
            interactionSource = interactionSource,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun FieldIcon(@DrawableRes icon: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(start = 8.dp)
            .size(36.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LyricsEditorRow(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(InnerCorner),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit_rounded),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.lyrics_editor),
                style = MaterialTheme.typography.titleMediumEmphasized,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null
            )
        }
    }
}
