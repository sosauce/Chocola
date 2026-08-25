package com.sosauce.chocola.presentation.components.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.presentation.components.SelectedItemLogo
import com.sosauce.chocola.utils.bouncySpec

@Composable
fun AnimatedSelectedIcon(
    isSelected: Boolean,
    unselectedContent: @Composable (AnimatedContentScope.() -> Unit)
) {
    AnimatedContent(
        targetState = isSelected,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = scaleIn(bouncySpec()),
                initialContentExit = scaleOut(bouncySpec()),
                sizeTransform = SizeTransform(clip = false)
            )
        },
        modifier = Modifier.padding(start = 10.dp)
    ) {
        if (it) {
            SelectedItemLogo()
        } else {
            unselectedContent()
        }
    }
}