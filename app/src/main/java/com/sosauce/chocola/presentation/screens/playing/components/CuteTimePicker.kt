@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.playing.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import kotlin.time.ExperimentalTime

@Composable
fun CuteTimePicker(
    currentTimerTime: Long,
    onDismissRequest: () -> Unit,
    onSetTimer: (hours: Long, minutes: Long) -> Unit,
    onCancelTimer: () -> Unit
) {

    val timePickerState = rememberTimePickerState(
        is24Hour = true
    )
    val hasRunningTimer = currentTimerTime > 0

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.set_sleep_timer)
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.sleep_timer_filled),
                contentDescription = null
            )
        },
        text = {
            if (hasRunningTimer) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.ongoing_sleep_timer))
                    Text(
                        text = stringResource(R.string.timer_will_end_in) + " " + DateUtils.formatElapsedTime(currentTimerTime / 1000),
                        style = MaterialTheme.typography.labelSmallEmphasized
                    )
                    Button(
                        onClick = onCancelTimer,
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Text("Cancel timer")
                    }
                }
            } else {
                TimePicker(timePickerState)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (hasRunningTimer) {
                        onDismissRequest()
                    } else {
                        onSetTimer(
                            timePickerState.hour.toLong(),
                            timePickerState.minute.toLong()
                        )
                    }
                },
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.okay))
            }
        },
        dismissButton = {
            if (currentTimerTime == 0L) {
                TextButton(
                    onClick = onDismissRequest,
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        },
        onDismissRequest = onDismissRequest
    )
}