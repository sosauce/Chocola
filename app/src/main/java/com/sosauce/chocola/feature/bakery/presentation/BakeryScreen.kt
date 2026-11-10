@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.core.presentation.navigation.Screen
import com.sosauce.chocola.feature.bakery.presentation.components.AllTimeHeader
import com.sosauce.chocola.feature.bakery.presentation.components.PeriodCarousel
import com.sosauce.chocola.feature.bakery.presentation.model.AllTimeStatsUi
import com.sosauce.chocola.feature.bakery.presentation.model.ArtistStatUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodCardUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodType
import com.sosauce.chocola.feature.bakery.presentation.model.StreakUi
import com.sosauce.chocola.feature.bakery.presentation.model.TrackStatUi
import com.sosauce.nekobites.components.LoadingBox
import com.sosauce.nekobites.helpers.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun BakeryRoot(
    onNavigateUp: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val viewModel = koinViewModel<BakeryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BakeryEvent.NavigateToStory -> onNavigate(Screen.BakeryStory(event.period))
            BakeryEvent.NavigateBack -> onNavigateUp()
        }
    }

    BakeryScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun BakeryScreen(
    state: BakeryState,
    onAction: (BakeryAction) -> Unit
) {
    val isEmpty = state.allTime == null && state.months.isEmpty() && state.years.isEmpty()

    LoadingBox(
        isLoading = state.isLoading
    ) {
        if (isEmpty) {
            BakeryEmptyState(
                modifier = Modifier.fillMaxSize()
            )
        } else {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                state.allTime?.let { allTime ->
                    AllTimeHeader(
                        stats = allTime,
                        streak = state.streak,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                if (state.months.isNotEmpty()) {
                    PeriodCarousel(
                        title = R.string.monthly_bakeries,
                        periods = state.months,
                        onPeriodClick = { onAction(BakeryAction.OpenPeriod(it)) }
                    )
                }

                if (state.years.isNotEmpty()) {
                    PeriodCarousel(
                        title = R.string.yearly_bakeries,
                        periods = state.years,
                        onPeriodClick = { onAction(BakeryAction.OpenPeriod(it)) }
                    )
                }
            }
        }

    }
}

@Composable
private fun BakeryEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.cookie),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                modifier = Modifier.size(84.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.bakery_empty_title),
                style = MaterialTheme.typography.headlineSmallEmphasized,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.bakery_empty_desc),
                style = MaterialTheme.typography.bodyMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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
private fun BakeryScreenPreview() {

    ChocolaTheme {
        BakeryScreen(
            state = BakeryState(
                isLoading = false,
                allTime = AllTimeStatsUi(
                    totalListens = 12_482,
                    totalListenedMillis = 218_340_000L,
                    topTrack = TrackStatUi(
                        mediaId = "1",
                        title = "Cinnamon Girl",
                        artist = "Lana Del Rey",
                        album = "Norman Fucking Rockwell!",
                        artworkUri = null,
                        listenCount = 412
                    ),
                    topArtist = ArtistStatUi("Lana Del Rey", 1_284)
                ),
                streak = StreakUi(current = 12, best = 36),
                months = listOf(
                    PeriodCardUi(
                        id = PeriodId(PeriodType.MONTH, "2026-10"),
                        title = "October",
                        year = 2026,
                        totalListens = 1_284,
                        topArtworkUri = null
                    ),
                    PeriodCardUi(
                        id = PeriodId(PeriodType.MONTH, "2026-09"),
                        title = "September",
                        year = 2026,
                        totalListens = 2_048,
                        topArtworkUri = null
                    )
                ),
                years = listOf(
                    PeriodCardUi(
                        id = PeriodId(PeriodType.YEAR, "2026"),
                        title = "2026",
                        year = null,
                        totalListens = 8_420,
                        topArtworkUri = null
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 820)
@Composable
private fun BakeryScreenEmptyPreview() {
    MaterialTheme {
        Surface {
            BakeryScreen(
                state = BakeryState(isLoading = false),
                onAction = {}
            )
        }
    }
}
