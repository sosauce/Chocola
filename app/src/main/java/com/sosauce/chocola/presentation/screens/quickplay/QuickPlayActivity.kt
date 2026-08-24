@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.sosauce.chocola.presentation.screens.quickplay

import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import coil3.toBitmap
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberArtworkShape
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.LoadingBox
import com.sosauce.chocola.presentation.components.Spacer
import com.sosauce.chocola.presentation.components.animations.AnimatedDrawable
import com.sosauce.chocola.presentation.screens.playing.NowPlaying
import com.sosauce.chocola.presentation.screens.playing.components.Artwork
import com.sosauce.chocola.presentation.screens.playing.components.CuteSlider
import com.sosauce.chocola.presentation.screens.playing.components.TitleAndArtist
import com.sosauce.chocola.presentation.theme.ChocolaTheme
import com.sosauce.chocola.utils.ArtworkShape
import com.sosauce.chocola.utils.rememberInteractionSource
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
class QuickPlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uri = intent?.data ?: return

        setContent {

            val viewModel = koinViewModel<QuickPlayViewModel>(
                parameters = { parametersOf(uri) }
            )
            val state by viewModel.musicState.collectAsStateWithLifecycle()
            ChocolaTheme {

                // Overlay box
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = ::finish,
                            indication = null,
                            interactionSource = null
                        )
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                    ) {
                        Column(
                            modifier = Modifier.padding(15.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.music_note),
                                        contentDescription = null
                                    )
                                    AsyncImage(
                                        model = state.track.artUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                                Spacer(10.dp)
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = state.track.title,
                                        style = MaterialTheme.typography.bodyMediumEmphasized,
                                        modifier = Modifier.basicMarquee()
                                    )
                                    Text(
                                        text = state.track.artist,
                                        style = MaterialTheme.typography.bodySmallEmphasized.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.basicMarquee()
                                    )
                                }
                                Spacer(5.dp)
                                ToggleButton(
                                    checked = state.isPlaying,
                                    onCheckedChange = { viewModel.handlePlayerAction(PlayerActions.PlayOrPause) },
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    )
                                ) {
                                    AnimatedDrawable(
                                        drawable = R.drawable.play_to_pause,
                                        atEnd = state.isPlaying
                                    )
                                }
                            }
                            Spacer(10.dp)
                            CuteSlider(
                                musicState = state,
                                onHandlePlayerActions = viewModel::handlePlayerAction
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                ToggleButton(
                                    checked = state.repeatMode != Player.REPEAT_MODE_OFF,
                                    onCheckedChange = { viewModel.handlePlayerAction(PlayerActions.ChangeRepeatMode) },
                                    modifier = Modifier.size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    )
                                ) {
                                    val icon = if (state.repeatMode != Player.REPEAT_MODE_OFF) R.drawable.repeat_one else R.drawable.repeat
                                    Icon(
                                        painter = painterResource(icon),
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = ::finish,
                                    shapes = IconButtonDefaults.shapes(),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    ),
                                    modifier = Modifier.size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                    }
                }


//                AlertDialog(
//                    onDismissRequest = ::finishAndRemoveTask,
//                    confirmButton = {},
//                    text = {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.Start
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(80.dp)
//                                    .background(
//                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
//                                        shape = RoundedCornerShape(24.dp)
//                                    ),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Icon(
//                                    painter = painterResource(R.drawable.music_note),
//                                    contentDescription = null
//                                )
//                            }
//                            TitleAndArtist(
//                                musicState = state
//                            )
//
//                        }
//
//                    }
//                )
            }


//            ChocolaTheme {
//                LoadingBox(
//                    isLoading = !viewModel.isSongLoaded
//                ) {
//                    NowPlaying(
//                        musicState = state,
//                        onHandlePlayerActions = {},
//                        onNavigate = {}
//                    )
//                }
//            }
        }
    }
}