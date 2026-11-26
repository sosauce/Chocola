package com.sosauce.chocola.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R

/**
 * The animated checkbox shown on items that can be selected.
 */
@Composable
fun SelectionCheckbox(
    visible: Boolean,
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val boxShape = RoundedCornerShape(7.dp)
    val fillColor by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest
    )
    val borderColor by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    )

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(spatialSpec) + fadeIn(),
        exit = scaleOut(spatialSpec) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(boxShape)
                .background(fillColor)
                .border(1.5.dp, borderColor, boxShape),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = checked,
                enter = scaleIn(spatialSpec),
                exit = scaleOut(spatialSpec)
            ) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
