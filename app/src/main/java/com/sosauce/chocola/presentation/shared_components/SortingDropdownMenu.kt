@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.sosauce.chocola.presentation.shared_components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.utils.rememberInteractionSource

@Composable
fun SortingDropdownMenu(
    isSortedAscending: Boolean,
    isMatchCaseFilter: Boolean?,
    isRegexFilter: Boolean?,
    onChangeSorting: (Boolean) -> Unit,
    onChangeMatchCaseFilter: ((Boolean) -> Unit)?,
    onChangeRegexFilter: ((Boolean) -> Unit)?,
    topContent: @Composable (() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {

    var expanded by remember { mutableStateOf(false) }
    val interactionSources = List(4) { rememberInteractionSource() }

    Box {
        IconButton(
            onClick = { expanded = !expanded },
            shapes = IconButtonDefaults.shapes()
        ) {
            AnimatedContent(
                targetState = expanded
            ) { isExpanded ->
                val icon = if (!isExpanded) R.drawable.sort else R.drawable.close
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null
                )
            }
        }
        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(0, 2)
            ) { topContent?.invoke() }
            Spacer(Modifier.height(MenuDefaults.GroupSpacing))
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(1, 2),
                content = content
            )
            Spacer(Modifier.height(MenuDefaults.GroupSpacing))
            ButtonGroup(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                overflowIndicator = {}
            ) {

                customItem(
                    buttonGroupContent = {
                        val shape by animateDpAsState(
                            targetValue = if (isSortedAscending) 50.dp else 12.dp
                        )

                        FilledIconButton(
                            onClick = { onChangeSorting(true) },
                            interactionSource = interactionSources[0],
                            modifier = Modifier
                                .animateWidth(interactionSources[0])
                                .weight(1f)
                                .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isSortedAscending) MenuDefaults.groupVibrantContainerColor else MenuDefaults.groupStandardContainerColor
                            ),
                            shape = RoundedCornerShape(shape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.up),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                            )
                        }
                    },
                    menuContent = {}
                )

                customItem(
                    buttonGroupContent = {
                        val shape2 by animateDpAsState(
                            targetValue = if (!isSortedAscending) 50.dp else 12.dp
                        )

                        FilledIconButton(
                            onClick = { onChangeSorting(false) },
                            interactionSource = interactionSources[1],
                            modifier = Modifier
                                .animateWidth(interactionSources[1])
                                .weight(1f)
                                .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (!isSortedAscending) MenuDefaults.groupVibrantContainerColor else MenuDefaults.groupStandardContainerColor
                            ),
                            shape = RoundedCornerShape(shape2)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.down),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                            )
                        }
                    },
                    menuContent = {}
                )
            }

            if (isRegexFilter != null || isMatchCaseFilter != null) {
                Spacer(Modifier.height(MenuDefaults.GroupSpacing))

                ButtonGroup(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                    overflowIndicator = {}
                ) {

                    if (isRegexFilter != null) {
                        customItem(
                            buttonGroupContent = {
                                val shape3 by animateDpAsState(
                                    targetValue = if (isRegexFilter) 50.dp else 12.dp
                                )

                                FilledIconButton(
                                    onClick = { onChangeRegexFilter!!(!isRegexFilter) },
                                    interactionSource = interactionSources[2],
                                    modifier = Modifier
                                        .animateWidth(interactionSources[2])
                                        .weight(1f)
                                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = if (isRegexFilter) MenuDefaults.groupVibrantContainerColor else MenuDefaults.groupStandardContainerColor
                                    ),
                                    shape = RoundedCornerShape(shape3)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.regular_expression),
                                        contentDescription = null,
                                        modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                                    )
                                }
                            },
                            menuContent = {}
                        )
                    }

                    if (isMatchCaseFilter != null) {
                        customItem(
                            buttonGroupContent = {
                                val shape4 by animateDpAsState(
                                    targetValue = if (isMatchCaseFilter) 50.dp else 12.dp
                                )

                                FilledIconButton(
                                    onClick = { onChangeMatchCaseFilter!!(!isMatchCaseFilter) },
                                    interactionSource = interactionSources[3],
                                    modifier = Modifier
                                        .animateWidth(interactionSources[3])
                                        .weight(1f)
                                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = if (isMatchCaseFilter) MenuDefaults.groupVibrantContainerColor else MenuDefaults.groupStandardContainerColor
                                    ),
                                    shape = RoundedCornerShape(shape4)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.match_case),
                                        contentDescription = null,
                                        modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                                    )
                                }
                            },
                            menuContent = {}
                        )
                    }
                }
            }
        }
    }

}