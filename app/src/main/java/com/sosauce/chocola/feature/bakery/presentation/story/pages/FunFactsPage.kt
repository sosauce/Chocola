@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.story.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.CuteListItem
import com.sosauce.chocola.feature.bakery.presentation.model.FunFactUi
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.nekobites.utils.groupedShape

@Composable
fun FunFactsPage(
    page: StoryPageUi.FunFacts,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item("fun_facts") {
            Text(
                text = stringResource(R.string.bakery_fun_facts),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )
        }

        if (page.facts.isEmpty()) {
            item("empty") {
                EmptyPageText(stringResource(R.string.bakery_empty_desc))
            }
        } else {
            itemsIndexed(
                items = page.facts
            ) { index, fact ->
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FunFactRow(
                        index = index,
                        fact = fact,
                        count = page.facts.count()
                    )
                }
            }
        }
    }
}

@Composable
private fun FunFactRow(
    index: Int,
    fact: FunFactUi,
    count: Int
) {
    val containerColor = when (index % 3) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = contentColorFor(containerColor)

    CuteListItem(
        onClick = null,
        shape = groupedShape(isFirst = index == 0, isLast = index == count - 1),
        backgroundColor = containerColor,
        leadingContent = {
            Icon(
                painter = painterResource(
                    when (index % 3) {
                        0 -> R.drawable.cookie
                        1 -> R.drawable.favorite_filled
                        else -> R.drawable.info_rounded
                    }
                ),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(28.dp)
            )
        }
    ) {
        Text(
            text = fact.title,
            style = MaterialTheme.typography.labelMediumEmphasized.copy(
                color = contentColor.copy(0.65f)
            )
        )
        Text(
            text = fact.value,
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun FunFactsPagePreview() {
    MaterialTheme {
        Surface {
            FunFactsPage(
                page = StoryPageUi.FunFacts(
                    facts = listOf(
                        FunFactUi("Still your favourite", "Lana Del Rey, 3 months in a row"),
                        FunFactUi("Different tracks", "428 different tracks"),
                        FunFactUi("Most replayed", "Cinnamon Girl (412)")
                    )
                )
            )
        }
    }
}
