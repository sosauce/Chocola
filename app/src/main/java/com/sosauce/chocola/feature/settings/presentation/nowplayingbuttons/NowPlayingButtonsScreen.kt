package com.sosauce.chocola.feature.settings.presentation.nowplayingbuttons

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.NowPlayingButton
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.core.presentation.preferences.rememberIsLandscape
import com.sosauce.chocola.feature.settings.presentation.components.SettingsSwitch
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

    val layoutDirection = LocalLayoutDirection.current
    val listPadding = PaddingValues(
        bottom = contentPadding.calculateBottomPadding() + 16.dp
    )

    if (rememberIsLandscape()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection)
                )
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp, top = 8.dp, bottom = contentPadding.calculateBottomPadding()),
                contentAlignment = Alignment.Center
            ) {
                NowPlayingMiniPreview(hiddenButtons = state.hiddenButtons)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                NowPlayingMiniPreview(hiddenButtons = state.hiddenButtons)
            }
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
        contentPadding = contentPadding
    ) {
        itemsIndexed(
            items = buttons,
            key = { _, button -> button.name }
        ) { index, button ->
            val isVisible = button !in hiddenButtons
            SettingsSwitch(
                checked = isVisible,
                onCheckedChange = {
                    onAction(NowPlayingButtonsAction.SetButtonVisible(button, !isVisible))
                },
                topDp = if (index == 0) 24.dp else 2.dp,
                bottomDp = if (index == buttons.lastIndex) 24.dp else 2.dp,
                text = stringResource(button.label),
                icon = button.icon
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 820, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NowPlayingButtonsScreenPreview() {
    ChocolaTheme {
        NowPlayingButtonsScreen(
            state = NowPlayingButtonsState(
                isLoading = false,
                hiddenButtons = setOf(
                    NowPlayingButton.QUEUE,
                    NowPlayingButton.REWIND,
                    NowPlayingButton.FORWARD,
                    NowPlayingButton.SHUFFLE
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 820)
@Composable
private fun NowPlayingButtonsScreenAllVisiblePreview() {
    ChocolaTheme {
        NowPlayingButtonsScreen(
            state = NowPlayingButtonsState(isLoading = false),
            onAction = {}
        )
    }
}
