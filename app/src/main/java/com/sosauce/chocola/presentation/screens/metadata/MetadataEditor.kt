@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.metadata

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kyant.taglib.Picture
import com.sosauce.chocola.R
import com.sosauce.chocola.domain.actions.MetadataActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.shared_components.ThreadDivider
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedFab
import com.sosauce.chocola.utils.selfAlignHorizontally
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MetadataEditor(
    trackUri: Uri,
    trackPath: String,
    onNavigateUp: () -> Unit,
    onNavigate: (Screen) -> Unit,
    metadataViewModel: MetadataViewModel
) {

    val metadataState by metadataViewModel.metadataState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val resources = LocalResources.current

    val editSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                metadataViewModel.onHandleMetadataActions(MetadataActions.SaveChanges)
                onNavigateUp()
            } else {
                Toast.makeText(
                    context,
                    resources.getString(R.string.error_saving),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val legacyPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            metadataViewModel.onHandleMetadataActions(MetadataActions.SaveChanges)
            onNavigateUp()
        } else {
            Toast.makeText(context, resources.getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(true) {
        metadataViewModel.legacyAskPermission.collectLatest {
            it?.let {
                val request = IntentSenderRequest.Builder(it.intentSender).build()
                legacyPermissionLauncher.launch(request)
            }
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
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
                AnimatedFab(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val intentSender = MediaStore.createWriteRequest(
                                context.contentResolver,
                                listOf(trackUri)
                            ).intentSender
                            editSongLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        } else {
                            metadataViewModel.onHandleMetadataActions(MetadataActions.SaveChanges)
                        }
                    },
                    icon = R.drawable.check,
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 5.dp)
                .imePadding()
        ) {
            MetadataArt(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .align(Alignment.CenterHorizontally),
                art = metadataState.art,
                onHandleMetadataActions = metadataViewModel::onHandleMetadataActions
            )
            Column {
                EditTextField(
                    initialValue = metadataState.mutablePropertiesMap["TITLE"],
                    label = {
                        Text(
                            text = stringResource(R.string.title)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.music_note),
                            contentDescription = null
                        )
                    }
                ) { title ->
                    metadataState.mutablePropertiesMap["TITLE"] = title
                }
                Row(
                    modifier = Modifier.padding(start = 20.dp, top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThreadDivider(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${stringResource(R.string.file_name)}: ${trackPath.substringAfterLast('/').substringBeforeLast(".")}",
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .basicMarquee()

                    )
                }

                EditTextField(
                    initialValue = metadataState.mutablePropertiesMap["ARTIST"],
                    label = {
                        Text(
                            text = stringResource(R.string.artist)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.artist_rounded),
                            contentDescription = null
                        )
                    }
                ) { artist ->
                    metadataState.mutablePropertiesMap["ARTIST"] = artist
                }
                EditTextField(
                    initialValue = metadataState.mutablePropertiesMap["ALBUM"],
                    label = {
                        Text(
                            text = stringResource(R.string.album)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                            contentDescription = null
                        )
                    }
                ) { album ->
                    metadataState.mutablePropertiesMap["ALBUM"] = album
                }
                Spacer(Modifier.height(20.dp))

                Row {
                    EditTextField(
                        initialValue = metadataState.mutablePropertiesMap["DATE"],
                        label = {
                            Text(
                                text = stringResource(R.string.year)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { year ->
                        metadataState.mutablePropertiesMap["DATE"] = year
                    }
                    EditTextField(
                        initialValue = metadataState.mutablePropertiesMap["GENRE"],
                        label = {
                            Text(
                                text = stringResource(R.string.genre)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) { genre ->
                        metadataState.mutablePropertiesMap["GENRE"] = genre
                    }
                }
                Row {
                    EditTextField(
                        initialValue = metadataState.mutablePropertiesMap["TRACKNUMBER"],
                        label = {
                            Text(
                                text = stringResource(R.string.track_nb),
                                modifier = Modifier.basicMarquee()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { track ->
                        metadataState.mutablePropertiesMap["TRACKNUMBER"] = track
                    }
                    EditTextField(
                        initialValue = metadataState.mutablePropertiesMap["DISCNUMBER"],
                        label = {
                            Text(
                                text = stringResource(R.string.disc_nb),
                                modifier = Modifier.basicMarquee()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { disc ->
                        metadataState.mutablePropertiesMap["DISCNUMBER"] = disc
                    }
                }
                EditTextField(
                    initialValue = metadataState.mutablePropertiesMap["LYRICS"],
                    label = {
                        Text(
                            text = stringResource(R.string.lyrics),
                            modifier = Modifier.basicMarquee()
                        )
                    },
                    imeAction = ImeAction.Default
                ) { lyrics ->
                    metadataState.mutablePropertiesMap["LYRICS"] = lyrics
                }
                Button(
                    onClick = { onNavigate(Screen.LyricsEditor(trackPath)) },
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.selfAlignHorizontally()
                ) {
                    Text(stringResource(R.string.lyrics_editor))
                }
            }
        }
    }
}


@Composable
private fun EditTextField(
    modifier: Modifier = Modifier,
    initialValue: String?,
    label: (@Composable () -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    leadingIcon: @Composable (() -> Unit)? = null,
    returnModifiedValue: (String) -> Unit
) {


    OutlinedTextField(
        value = initialValue ?: "",
        onValueChange = { returnModifiedValue(it) },
        label = label,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 1.dp)
    )
}

@Composable
private fun MetadataArt(
    modifier: Modifier = Modifier,
    art: Picture?,
    onHandleMetadataActions: (MetadataActions) -> Unit
) {

    val context = LocalContext.current
    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            onHandleMetadataActions(
                MetadataActions.UpdateAudioArt(
                    it ?: Uri.EMPTY
                )
            )
        }

    Box(modifier = modifier) {
        if (art != null) {
            AsyncImage(
                model = art.data,
                contentDescription = stringResource(id = R.string.artwork),
                modifier = Modifier
                    .size(230.dp)
                    .clip(RoundedCornerShape(10))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(10))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                    .size(230.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = stringResource(id = R.string.artwork)
                )
            }
        }
        AnimatedVisibility(
            visible = art != null,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = (-20).dp)
        ) {
            FilledIconButton(
                onClick = { onHandleMetadataActions(MetadataActions.RemoveArtwork) },
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

