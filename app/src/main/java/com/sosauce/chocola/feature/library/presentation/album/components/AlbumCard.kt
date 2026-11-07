@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.SelectionCheckbox
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils
import com.sosauce.chocola.core.domain.player.PlayerActions
import com.sosauce.chocola.core.presentation.components.MoreOptions
import com.sosauce.chocola.core.presentation.components.dialogs.DeletionDialog
import com.sosauce.nekobites.animations.AnimatedDrawable
import com.sosauce.nekobites.animations.AnimatedDrawableFile
import com.sosauce.nekobites.animations.bouncySpec
import com.sosauce.nekobites.helpers.rememberInteractionSource

@Composable
fun SharedTransitionScope.AlbumCard(
    modifier: Modifier = Modifier,
    album: Album,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    isInSelectionMode: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {

    var showMoreMenu by remember { mutableStateOf(false) }
    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed || isSelected) 0.95f else 1f,
        animationSpec = bouncySpec()
    )

    Box(
        modifier = modifier
            .padding(horizontal = 5.dp, vertical = 10.dp)
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                interactionSource = interactionSource,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.album_filled),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        AsyncImage(
            model = album.art,
            contentDescription = stringResource(id = R.string.artwork),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        AnimatedVisibility(
            visible = !isInSelectionMode,
            modifier = Modifier.align(Alignment.TopEnd),
            enter = scaleIn(bouncySpec()),
            exit = scaleOut(bouncySpec())
        ) {
            IconButton(
                onClick = { showMoreMenu = true },
                shapes = IconButtonDefaults.shapes()
            ) {
                AnimatedDrawable(
                    drawable = AnimatedDrawableFile.MORE_VERT,
                    atEnd = showMoreMenu
                )

                AlbumDropdownMenu(
                    album = album,
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false },
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
        SelectionCheckbox(
            visible = isInSelectionMode,
            checked = isSelected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
                .padding(15.dp)
        ) {
            Text(
                text = album.name,
                maxLines = 1,
                style = MaterialTheme.typography.titleMediumEmphasized,
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(album.name + album.id),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                    )
                    .basicMarquee()
            )
            Text(
                text = album.artist,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(album.artist + album.id),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                    )
                    .basicMarquee()

            )
        }
    }
}

@Composable
private fun AlbumDropdownMenu(
    album: Album,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onToggleFavorite: (() -> Unit)?
) {

    var showDeletionDialog by remember { mutableStateOf(false) }

    if (showDeletionDialog) {
        DeletionDialog(
            tracks = album.tracks,
            onDismissRequest = { showDeletionDialog = false }
        )
    }

    val options = mutableListOf(
        AlbumMore(
            onClick = {},
            icon = R.drawable.add_to_queue,
            text = R.string.add_queue,
        ),
        AlbumMore(
            onClick = { showDeletionDialog = true },
            icon = R.drawable.trash_rounded,
            text = R.string.delete,
            isDestructive = true
        )
    ).apply {
        onToggleFavorite?.let {
            add(
                index = 0,
                element = AlbumMore(
                    onClick = it,
                    icon = R.drawable.favorite,
                    text = R.string.add_favorite
                )
            )
        }
    }

    DropdownMenuPopup(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShapes()
        ) {
            options.fastForEachIndexed { index, option ->

                val tint = if (option.isDestructive) {
                    MaterialTheme.colorScheme.error
                } else LocalContentColor.current

                DropdownMenuItem(
                    onClick = {
                        onDismissRequest()
                        option.onClick()
                    },
                    shape = when (index) {
                        0 -> MenuDefaults.leadingItemShape
                        options.lastIndex -> MenuDefaults.trailingItemShape
                        else -> MenuDefaults.middleItemShape
                    },
                    text = { Text(stringResource(option.text)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(option.icon),
                            contentDescription = null
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        leadingIconColor = tint,
                        textColor = tint
                    )
                )
            }
        }
    }
}

private data class AlbumMore(
    val onClick: () -> Unit,
    @StringRes val text: Int,
    @DrawableRes val icon: Int,
    val isDestructive: Boolean = false
)
