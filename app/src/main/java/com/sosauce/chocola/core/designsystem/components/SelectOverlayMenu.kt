@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.core.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme

/**
 * Menu of a [SelectOverlayItem], one row per action.
 * The mandatory "Select" action is always shown first, it starts multi selection with the lifted item.
 * @param onSelect starts multi selection with the lifted item
 * @param itemModifier extra modifier per item, used by the overlay to animate the rows in
 */
@Composable
fun SelectOverlayMenu(
    actions: List<SelectOverlayAction>,
    onActionClick: (SelectOverlayAction) -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    itemModifier: (index: Int) -> Modifier = { Modifier }
) {

    val allActions = listOf(
        SelectOverlayAction(
            label = stringResource(R.string.select),
            icon = R.drawable.check_circle,
            onClick = onSelect
        )
    ) + actions

    DropdownMenuGroup(
        shapes = MenuDefaults.groupShapes(),
        modifier = modifier.width(IntrinsicSize.Max)
    ) {

        allActions.fastForEachIndexed { index, action ->

            val contentColor = if (action.isDestructive) {
                MaterialTheme.colorScheme.error
            } else {
                LocalContentColor.current
            }

            DropdownMenuItem(
                modifier = itemModifier(index),
                onClick = { onActionClick(action) },
                shape = when (index) {
                    0 -> MenuDefaults.leadingItemShape
                    allActions.lastIndex -> MenuDefaults.trailingItemShape
                    else -> MenuDefaults.middleItemShape
                },
                text = {
                    Text(
                        text = action.label,
                        style = MaterialTheme.typography.bodyLargeEmphasized
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(action.icon),
                        contentDescription = null
                    )
                },
                colors = MenuDefaults.itemColors(
                    textColor = contentColor,
                    leadingIconColor = contentColor
                )
            )
        }
    }
}

private val previewActions = listOf(
    SelectOverlayAction("Play next", R.drawable.fast_forward) {},
    SelectOverlayAction("Add to queue", R.drawable.add_to_queue) {},
    SelectOverlayAction("Add to playlist", R.drawable.playlist_add) {},
    SelectOverlayAction("Delete", R.drawable.trash_rounded_filled, isDestructive = true) {}
)

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Preview(
    showBackground = true,
    widthDp = 300,
    heightDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SelectOverlayMenuPreview() {
    ChocolaTheme {
        Box(Modifier.padding(16.dp)) {
            SelectOverlayMenu(
                actions = previewActions,
                onActionClick = {},
                onSelect = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 820)
@Preview(
    showBackground = true,
    widthDp = 380,
    heightDp = 820,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SelectOverlayLiftedPreview() {
    ChocolaTheme {
        val density = LocalDensity.current
        val itemContent: @Composable () -> Unit = {
            FavoriteItem(
                title = "Cinnamon Girl",
                subtitle = "Lana Del Rey",
                isSelected = false,
                isInSelectionMode = false,
                onClick = {},
                onLongClick = {}
            ) { isPressed ->
                FavoriteArtwork(
                    model = null,
                    placeholderIcon = R.drawable.music_note,
                    isPressed = isPressed
                )
            }
        }
        val locals = currentCompositionLocalContext
        val state = remember {
            SelectOverlayState().apply {
                val bounds = with(density) {
                    Rect(Offset(20.dp.toPx(), 120.dp.toPx()), Size(135.dp.toPx(), 160.dp.toPx()))
                }
                show(
                    SelectOverlayEntry(
                        key = Any(),
                        bounds = bounds,
                        actions = previewActions,
                        onSelect = {},
                        locals = locals,
                        content = itemContent
                    )
                )
            }
        }
        Surface {
            SelectOverlayHost(state = state) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, top = 120.dp)
                ) {
                    itemContent()
                }
            }
        }
    }
}
