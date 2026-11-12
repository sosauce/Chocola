@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.settings.presentation.nowplayingbuttons

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.core.presentation.preferences.rememberIsLandscape
import com.sosauce.nekobites.components.settings.SettingsSwitch
import com.sosauce.nekobites.components.settings.settingsItemShape
import com.sosauce.nekobites.helpers.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun NowPlayingButtonsRoot(
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val viewModel = koinViewModel<NowPlayingButtonsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            NowPlayingButtonsEvent.SaveFailed -> Toast.makeText(
                context,
                resources.getString(R.string.generic_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    NowPlayingButtonsScreen(
        state = state,
        onAction = viewModel::onAction,
        contentPadding = contentPadding
    )
}

@Composable
fun NowPlayingButtonsScreen(
    state: NowPlayingButtonsState,
    onAction: (NowPlayingButtonsAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    if (state.isLoading) return

    val listPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 16.dp)

    if (rememberIsLandscape()) {
        Row(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                PreviewStage(
                    hiddenButtons = state.hiddenButtons,
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = 16.dp,
                            bottom = contentPadding.calculateBottomPadding() + 16.dp
                        )
                )
            }
            ButtonToggles(
                hiddenButtons = state.hiddenButtons,
                onAction = onAction,
                contentPadding = listPadding,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            PreviewStage(
                hiddenButtons = state.hiddenButtons,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.42f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ButtonToggles(
                hiddenButtons = state.hiddenButtons,
                onAction = onAction,
                contentPadding = listPadding,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

/** Live preview sitting on a tinted stage. */
@Composable
private fun PreviewStage(
    hiddenButtons: Set<NowPlayingButton>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            NowPlayingMiniPreview(hiddenButtons = hiddenButtons)
        }
    }
}

@Composable
private fun ButtonToggles(
    hiddenButtons: Set<NowPlayingButton>,
    onAction: (NowPlayingButtonsAction) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val buttons = NowPlayingButton.hideable

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = contentPadding.calculateBottomPadding()
        )
    ) {
        itemsIndexed(
            items = buttons,
            key = { _, button -> button.name }
        ) { index, button ->
            val isVisible = button !in hiddenButtons
            SettingsSwitch(
                title = stringResource(button.label),
                icon = button.icon,
                checked = isVisible,
                onCheckedChange = {
                    onAction(NowPlayingButtonsAction.SetButtonVisible(button, !isVisible))
                },
                baseShape = settingsItemShape(
                    isFirst = index == 0,
                    isLast = index == buttons.lastIndex
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 2.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 380,
    heightDp = 820,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NowPlayingButtonsScreen2Preview() {
    ChocolaTheme {
        NowPlayingButtonsScreen(
            state = NowPlayingButtonsState(
                isLoading = false,
                hiddenButtons = setOf(NowPlayingButton.QUEUE, NowPlayingButton.SHUFFLE)
            ),
            onAction = {}
        )
    }
}
