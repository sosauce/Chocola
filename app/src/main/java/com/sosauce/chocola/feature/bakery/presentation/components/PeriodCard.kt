@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodCardUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodType

@Composable
fun PeriodCard(
    period: PeriodCardUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean
) {

    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Icon(
                painter = painterResource(R.drawable.cookie),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                modifier = Modifier
                    .size(132.dp)
                    .align(Alignment.Center)
            )

            if (period.topArtworkUri != null) {
                AsyncImage(
                    model = period.topArtworkUri,
                    contentDescription = stringResource(R.string.artwork),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.22f),
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.78f)
                            )
                        )
                    )
            )

            period.year?.let { year ->
                Text(
                    text = year.toString(),
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Crossfade(
                targetState = isActive,
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                if (it) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = period.title,
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(
                                R.string.listens_count_value,
                                period.totalListens
                            ),
                            style = MaterialTheme.typography.bodyMediumEmphasized,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 260, heightDp = 300)
@Composable
private fun PeriodCardPreview() {
    MaterialTheme {
        Surface {
            PeriodCard(
                period = PeriodCardUi(
                    id = PeriodId(PeriodType.MONTH, "2026-10"),
                    title = "October",
                    year = 2026,
                    totalListens = 1_284,
                    topArtworkUri = null
                ),
                onClick = {},
                modifier = Modifier.padding(16.dp),
                isActive = true
            )
        }
    }
}
