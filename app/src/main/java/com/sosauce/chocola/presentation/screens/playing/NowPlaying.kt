@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.playing

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberIsLandscape
import com.sosauce.chocola.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.chocola.data.datastore.rememberUseArtAsBackground
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.dialogs.tracksDetails.TracksDetailsDialog
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playing.components.ActionButtonsRow
import com.sosauce.chocola.presentation.screens.playing.components.Artwork
import com.sosauce.chocola.presentation.screens.playing.components.CuteSlider
import com.sosauce.chocola.presentation.screens.playing.components.MoreOptionsButton
import com.sosauce.chocola.presentation.screens.playing.components.QuickActionsRow
import com.sosauce.chocola.presentation.screens.playing.components.SpeedCard
import com.sosauce.chocola.presentation.screens.playing.components.TitleAndArtist
import com.sosauce.chocola.presentation.screens.playlists.components.PlaylistPicker

@Composable
fun NowPlaying(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onShrinkToSearchbar: () -> Unit = {}
) {
    val isLandscape = rememberIsLandscape()
    if (isLandscape) {
        NowPlayingLandscape(
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions,
            onNavigate = onNavigate,
            onShrinkToSearchbar = onShrinkToSearchbar
        )
    } else {
        NowPlayingContent(
            modifier = modifier,
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions,
            onNavigate = onNavigate,
            onShrinkToSearchbar = onShrinkToSearchbar
        )
    }

}

@Composable
private fun NowPlayingContent(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onShrinkToSearchbar: () -> Unit
) {
    var snap by rememberSnapSpeedAndPitch()
    var showSpeedCard by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    val useArtBackground by rememberUseArtAsBackground()


    if (showDetailsDialog) {
        TracksDetailsDialog(
            track = musicState.track,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(musicState.track.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    if (showSpeedCard) {
        SpeedCard(
            musicState = musicState,
            onHandlePlayerAction = onHandlePlayerActions,
            onDismissRequest = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onShrinkToSearchbar,
                        shapes = IconButtonDefaults.shapes(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                        ),
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))

                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_down),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    MoreOptionsButton(
                        modifier = Modifier.padding(end = 15.dp),
                        musicState = musicState,
                        onNavigate = onNavigate,
                        onShrinkToSearchbar = onShrinkToSearchbar
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent
            ) {
                QuickActionsRow(
                    musicState = musicState,
                    onShowSpeedCard = { showSpeedCard = true },
                    onHandlePlayerActions = onHandlePlayerActions
                )
            }
        }
    ) { paddingValues ->


        if (useArtBackground) {
            AsyncImage(
                model = musicState.track.artUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .cloudy(100),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.background.copy(0.7f),
                    blendMode = BlendMode.Darken
                )
            )
        }


        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Artwork(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
            TitleAndArtist(
                title = musicState.track.title,
                artist = musicState.track.artist,
                album = musicState.track.album
            )
            CuteSlider(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
            ActionButtonsRow(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
        }
    }


}

