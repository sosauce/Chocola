package com.sosauce.chocola.feature.library.presentation.artist.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.FavoriteArtwork
import com.sosauce.chocola.core.designsystem.components.FavoriteItem
import com.sosauce.chocola.core.domain.model.Artist

@Composable
fun FavoriteArtistItem(
    modifier: Modifier = Modifier,
    artist: Artist,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onUnfavorite: () -> Unit
) {
    FavoriteItem(
        modifier = modifier,
        title = artist.name,
        subtitle = pluralStringResource(
            R.plurals.tracks_plural,
            artist.tracks.size,
            artist.tracks.size
        ),
        isSelected = isSelected,
        isInSelectionMode = isInSelectionMode,
        onClick = onClick,
        onLongClick = onLongClick,
        onUnfavorite = onUnfavorite
    ) { isPressed ->
        FavoriteArtwork(
            model = null,
            placeholderIcon = R.drawable.artists_filled,
            isPressed = isPressed
        )
    }
}
