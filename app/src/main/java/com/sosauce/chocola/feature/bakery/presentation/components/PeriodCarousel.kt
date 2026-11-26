@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodCardUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodType

@Composable
fun PeriodCarousel(
    @StringRes title: Int,
    periods: List<PeriodCardUi>,
    onPeriodClick: (PeriodId) -> Unit,
    modifier: Modifier = Modifier
) {
    if (periods.isEmpty()) return
    val state = rememberCarouselState { periods.count() }



    Column(modifier = modifier) {
        Text(
            text = stringResource(title),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        HorizontalMultiBrowseCarousel(
            modifier = Modifier.padding(horizontal = 10.dp),
            state = state,
            preferredItemWidth = 270.dp,
            itemSpacing = 5.dp
        ) { index ->
            val period = periods[index]
            val isActive by remember {
                derivedStateOf { state.currentItem == index }
            }

            PeriodCard(
                period = period,
                onClick = { onPeriodClick(period.id) },
                isActive = isActive,
                modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge)
            )
        }

    }
}

@Preview(showBackground = false, widthDp = 380, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PeriodCarouselPreview() {

    ChocolaTheme {
        PeriodCarousel(
            title = R.string.title,
            periods = listOf(
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
                ),
                PeriodCardUi(
                    id = PeriodId(PeriodType.MONTH, "2026-08"),
                    title = "August",
                    year = 2026,
                    totalListens = 932,
                    topArtworkUri = null
                )
            ),
            onPeriodClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}
