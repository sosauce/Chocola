package com.sosauce.chocola.feature.library.presentation.main.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.FavoriteArtwork
import com.sosauce.chocola.core.designsystem.components.FavoriteItem
import com.sosauce.chocola.core.domain.model.CuteTrack

@Composable
fun FavoriteTrackItem(
    modifier: Modifier = Modifier,
    track: CuteTrack,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onUnfavorite: () -> Unit
) {
    FavoriteItem(
        modifier = modifier,
        title = track.title,
        subtitle = track.artist,
        isSelected = isSelected,
        isInSelectionMode = isInSelectionMode,
        onClick = onClick,
        onLongClick = onLongClick,
        onUnfavorite = onUnfavorite
    ) { isPressed ->
        FavoriteArtwork(
            model = track.artUri,
            placeholderIcon = R.drawable.music_note,
            isPressed = isPressed
        )
    }
}
