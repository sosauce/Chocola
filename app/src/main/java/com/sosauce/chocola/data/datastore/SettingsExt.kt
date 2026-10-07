@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.data.datastore

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val state by remember {
        context.dataStore.data
            .mapLatest { it[key] ?: defaultValue }
    }.collectAsStateWithLifecycle(defaultValue)


    return remember(state) {
        object : MutableState<T> {
            override var value: T
                get() = state
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit {
                            it[key] = value
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

@Composable
fun rememberIsLandscape(): Boolean {
    val config = LocalConfiguration.current
    val containerSize = LocalWindowInfo.current.containerSize

    // In split-screen or niche devices the window can be whatever
    // Don't make assumptions, this is Android
    return if (containerSize.width > 0 && containerSize.height > 0) {
        containerSize.width > containerSize.height * 1.15f
    } else {
        config.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}

