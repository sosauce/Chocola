package com.sosauce.chocola.presentation.components

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sosauce.chocola.R
import com.sosauce.chocola.utils.bouncySpec
import kotlinx.coroutines.launch

@Composable
fun <T> LazyRowWithScrollButton(
    modifier: Modifier = Modifier,
    items: List<T>,
    content: @Composable (T) -> Unit
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box {
        LazyRow(
            state = state,
            modifier = modifier
        ) {
            items(
                items = items,
                key = { it.hashCode() }
            ) { type ->
                content(type)
            }
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = state.canScrollForward,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = slideInHorizontally(bouncySpec()) { it },
            exit = slideOutHorizontally(bouncySpec()) { it }
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        state.animateScrollToItem(items.lastIndex)
                    }
                },
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = null
                )
            }
        }
    }
}