package com.sosauce.chocola.presentation.screens.album.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R


@Composable
fun NumberOfTracks(size: Int) {

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = pluralStringResource(
                    R.plurals.tracks,
                    size,
                    size
                )
            )
        }
    }
}