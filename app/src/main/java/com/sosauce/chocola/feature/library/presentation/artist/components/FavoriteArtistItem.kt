package com.sosauce.chocola.feature.library.presentation.artist.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.FavoriteArtwork
import com.sosauce.chocola.core.designsystem.components.FavoriteItem
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.feature.library.presentation.util.ImageUtils

@Composable
fun FavoriteArtistItem(
    modifier: Modifier = Modifier,
    artist: Artist,
    onClick: () -> Unit
) {
    FavoriteItem(
        modifier = modifier,
        title = artist.name,
        subtitle = pluralStringResource(
            R.plurals.tracks_plural,
            artist.tracks.size,
            artist.tracks.size
        ),
        isSelected = false,
        isInSelectionMode = false,
        onClick = onClick,
        onLongClick = {}
    ) { isPressed ->
        FavoriteArtwork(
            model = ImageUtils.getAlbumArt(artist.albumId),
            placeholderIcon = R.drawable.artists_filled,
            isPressed = isPressed
        )
    }
}
