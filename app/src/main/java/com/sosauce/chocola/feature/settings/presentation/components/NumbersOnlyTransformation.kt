package com.sosauce.chocola.feature.settings.presentation.components

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

object NumbersOnlyTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {

        if (asCharSequence().isNotEmpty()) {
            val input = asCharSequence().toString().toIntOrNull()
            if (input == null) {
                revertAllChanges()
            }
        }

    }
}