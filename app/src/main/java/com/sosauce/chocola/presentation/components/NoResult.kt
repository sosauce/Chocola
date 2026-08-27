@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sosauce.chocola.R
import com.sosauce.nekobites.components.NoXFound

@Composable
fun NoResult(
    modifier: Modifier = Modifier
) {
    NoXFound(
        modifier = modifier,
        headlineText = R.string.no_result_found,
        bodyText = R.string.better_luck_next_time,
        icon = R.drawable.search
    )
}