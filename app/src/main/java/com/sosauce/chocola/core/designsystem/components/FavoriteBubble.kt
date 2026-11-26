package com.sosauce.chocola.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoriteBubble(modifier: Modifier = Modifier) {

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 3.dp, y = 3.dp)
                .size(12.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialShapes.Circle.toShape()
                )
        )
        Box(
            modifier = Modifier
                .size(25.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialShapes.Circle.toShape()
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.favorite_filled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}