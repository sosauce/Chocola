package com.sosauce.chocola.utils

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

object NumbersOnlyTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {

        if (!asCharSequence().isEmpty()) {
            val input = asCharSequence().toString().toIntOrNull()
            if (input == null) {
                revertAllChanges()
            }
        }

    }
}