@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.utils.rememberFocusRequester
import com.sosauce.chocola.utils.selfAlignHorizontally
import kotlinx.coroutines.android.awaitFrame

@Composable
fun RateAdjustmentDialog(
    rate: Float,
    onSetNewRate: (Float) -> Unit,
    onDismissRequest: () -> Unit,
    title: Int
) {
    val focusRequest = rememberFocusRequester()
    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequest.requestFocus()
    }

    val textFieldState = rememberTextFieldState(initialText = "%.2f".format(rate))
    val newRate =
        remember(textFieldState.text) { textFieldState.text.toString().toFloatOrNull() ?: 1.0f }
    val isError = remember(newRate) { newRate !in 0.5f..3.0f }
    val textStyle = LocalTextStyle.current


    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                painter = painterResource(R.drawable.speed_rounded),
                contentDescription = null
            )
        },
        title = { Text(stringResource(id = title)) },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSetNewRate(newRate)
                    onDismissRequest()
                },
                shapes = ButtonDefaults.shapes(),
                enabled = !isError
            ) {
                Text(text = stringResource(id = R.string.set))
            }
        },
        text = {
            Column {
                Text(stringResource(id = R.string.new_rate, 0.5, 3))
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    state = textFieldState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    onKeyboardAction = if (isError) null else KeyboardActionHandler {
                        onSetNewRate(
                            newRate
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .selfAlignHorizontally()
                        .fillMaxWidth(0.5f)
                        .focusRequester(focusRequest),
                    textStyle = textStyle.copy(
                        textAlign = TextAlign.Center
                    ),
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(stringResource(R.string.rate_error, 0.5, 3))
                        }
                    },
                    inputTransformation = RateInputTransformation
                )
            }
        }
    )
}

object RateInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {

        val input = asCharSequence()
        val dotCount = input.count { it == '.' }
        if (dotCount > 1) {
            revertAllChanges()
        } else {
            if (!input.all { it.isDigit() || it == '.' }) {
                revertAllChanges()
            }
        }
    }
}



