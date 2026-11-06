package com.sosauce.chocola.feature.library.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
