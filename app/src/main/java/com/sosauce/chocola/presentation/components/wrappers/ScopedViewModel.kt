package com.sosauce.chocola.presentation.components.wrappers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner

@Composable
fun ScopedViewModel(
    content: @Composable () -> Unit
) {
    val owner = rememberViewModelStoreOwner()
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        content()
    }

}