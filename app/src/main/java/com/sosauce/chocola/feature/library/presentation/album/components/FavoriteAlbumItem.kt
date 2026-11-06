package com.sosauce.chocola.feature.library.presentation.album.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.FavoriteArtwork
import com.sosauce.chocola.core.designsystem.components.FavoriteItem
import com.sosauce.chocola.core.domain.model.Album

@Composable
fun FavoriteAlbumItem(
    modifier: Modifier = Modifier,
    album: Album,
    onClick: () -> Unit
) {
    FavoriteItem(
        modifier = modifier,
        title = album.name,
        subtitle = album.artist,
        isSelected = false,
        isInSelectionMode = false,
        onClick = onClick,
        onLongClick = {}
    ) { isPressed ->
        FavoriteArtwork(
            model = album.art,
            placeholderIcon = R.drawable.album_filled,
            isPressed = isPressed
        )
    }
}
