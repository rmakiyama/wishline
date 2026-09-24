package com.rmakiyama.wishline.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CloseCardSheet(
    returningCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val spacing = WlTheme.spacing
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = WlTheme.shapes.extraLarge,
        containerColor = WlTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = spacing.l, end = spacing.l, bottom = spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.m),
        ) {
            Text(
                text = stringResource(Res.string.home_close_title),
                style = WlTheme.typography.headlineSmall,
                color = WlTheme.colorScheme.onSurface,
            )
            Text(
                text = if (returningCount > 0) {
                    stringResource(Res.string.home_close_body, returningCount)
                } else {
                    stringResource(Res.string.home_close_body_none)
                },
                style = WlTheme.typography.bodyLarge,
                color = WlTheme.colorScheme.onSurfaceVariant,
            )
            Column(
                modifier = Modifier.padding(top = spacing.s),
                verticalArrangement = Arrangement.spacedBy(spacing.s),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text(stringResource(Res.string.home_close_confirm), style = WlTheme.typography.titleMedium)
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.home_cancel))
                }
            }
        }
    }
}

@Composable
internal fun EditLabelDialog(
    input: String,
    cardNumber: Int,
    onInputChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.home_label_title)) },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        stringResource(
                            Res.string.home_label_placeholder,
                            stringResource(Res.string.home_card_default_label, cardNumber),
                        ),
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSave() }),
                singleLine = true,
                shape = WlTheme.shapes.large,
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(Res.string.home_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.home_cancel))
            }
        },
    )
}
