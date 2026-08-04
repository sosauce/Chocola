package com.sosauce.chocola.data.models

import androidx.collection.FloatList

data class EqualizerPreset(
    val name: String,
    val emoji: String,
    val gains: FloatList
)
