@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playing.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import com.sosauce.chocola.core.domain.player.MusicState
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.navigation.Screen

@Composable
fun PlayingTopRow(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onShrinkToSearchbar: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    hiddenButtons: Set<NowPlayingButton> = emptySet()
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = NowPlayingButton.CLOSE !in hiddenButtons,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            IconButton(
                onClick = onShrinkToSearchbar,
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                ),
                modifier = Modifier
                    .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_down),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Keeps the options pinned to the end even when the close button is hidden
        Spacer(Modifier.weight(1f))

        MoreOptionsButton(
            musicState = musicState,
            onNavigate = onNavigate,
            onHandlePlayerActions = onHandlePlayerActions,
            hiddenButtons = hiddenButtons
        )
    }
}