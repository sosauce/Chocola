package com.sosauce.chocola.presentation.screens.metadata

import android.net.Uri

sealed interface MetadataActions {

    data class UpdateAudioArt(
        val newArtUri: Uri
    ) : MetadataActions

    data object SaveChanges : MetadataActions

    data object RemoveArtwork : MetadataActions

}