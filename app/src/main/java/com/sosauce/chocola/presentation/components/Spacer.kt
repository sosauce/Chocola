@file:Suppress("UnusedReceiverParameter")

package com.sosauce.chocola.presentation.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

// https://github.com/Elnix90/Dragon-Launcher/blob/3031fa8f86b912dbd2882df68324944bdaf24822/core/ui/base/src/main/kotlin/org/elnix/dragonlauncher/ui/base/components/Spacer.kt

@Composable
fun RowScope.Spacer() {
    Spacer(Modifier.weight(1f))
}

@Composable
fun ColumnScope.Spacer() {
    Spacer(Modifier.weight(1f))
}

@Suppress("UnusedReceiverParameter")
@Composable
fun RowScope.Spacer(width: Dp) {
    Spacer(Modifier.width(width))
}

@Composable
fun ColumnScope.Spacer(height: Dp) {
    Spacer(Modifier.height(height))
}