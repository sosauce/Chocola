@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.playlists.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import kotlinx.coroutines.android.awaitFrame

@Composable
fun NewTagDialog(
    onDismissRequest: () -> Unit,
    tags: List<String>,
    onAddNewTag: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState()
    val isError = remember(textFieldState.text) { textFieldState.text in tags }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onAddNewTag(textFieldState.text.toString())
                    onDismissRequest()
                },
                enabled = !isError,
                shapes = ButtonDefaults.shapes()
            ) { Text(stringResource(R.string.add)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) { Text(stringResource(R.string.cancel)) }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.playlist_tag_add)) },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    state = textFieldState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    onKeyboardAction = if (isError) null else KeyboardActionHandler {
                        onAddNewTag(
                            textFieldState.text.toString()
                        )
                    },
                    modifier = Modifier.focusRequester(focusRequester),
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(stringResource(R.string.playlist_tag_exists))
                        }
                    }
                )
                Spacer(Modifier.height(15.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.warning),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.playlist_tag_no_edit),
                        style = MaterialTheme.typography.bodySmallEmphasized
                    )
                }

            }
        }
    )
}