package com.rmakiyama.wishline.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rmakiyama.wishline.core.ui.Res
import com.rmakiyama.wishline.core.ui.icon.WlIcons
import com.rmakiyama.wishline.core.ui.wish_action_achieve
import com.rmakiyama.wishline.core.ui.wish_action_delete
import com.rmakiyama.wishline.core.ui.wish_action_restore
import com.rmakiyama.wishline.core.ui.wish_action_someday
import com.rmakiyama.wishline.core.ui.wish_action_undo_achieve
import com.rmakiyama.wishline.core.ui.wish_sheet_cancel
import com.rmakiyama.wishline.core.ui.wish_sheet_edit_title
import com.rmakiyama.wishline.core.ui.wish_sheet_place_card
import com.rmakiyama.wishline.core.ui.wish_sheet_place_next_card
import com.rmakiyama.wishline.core.ui.wish_sheet_save
import com.rmakiyama.wishline.core.ui.wish_sheet_status_done
import com.rmakiyama.wishline.core.ui.wish_sheet_status_planned
import com.rmakiyama.wishline.core.ui.wish_sheet_status_someday
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import org.jetbrains.compose.resources.stringResource

enum class WishSheetStatus { Planned, Done, Someday }

sealed interface WishSheetPlace {
    data object NextCard : WishSheetPlace
    data class Card(val number: Int) : WishSheetPlace
}

/** What the sheet offers; which ones apply is decided by the caller from the wish's place and status. */
enum class WishAction { Achieve, UndoAchieve, Someday, Restore, Delete }

/** [actions] appear in the given order: the first is the primary button and the rest share a row below it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishSheet(
    title: String,
    place: WishSheetPlace,
    status: WishSheetStatus,
    actions: List<WishAction>,
    isEditingTitle: Boolean,
    titleInput: String,
    onDismiss: () -> Unit,
    onStartEditTitle: () -> Unit,
    onTitleInputChange: (String) -> Unit,
    onSaveTitle: () -> Unit,
    onCancelEditTitle: () -> Unit,
    onAction: (WishAction) -> Unit,
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
            if (isEditingTitle) {
                TitleEditor(
                    value = titleInput,
                    onValueChange = onTitleInputChange,
                    onSave = onSaveTitle,
                    onCancel = onCancelEditTitle,
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = WlTheme.typography.titleLarge,
                        color = WlTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onStartEditTitle) {
                        Icon(
                            WlIcons.Pencil,
                            contentDescription = stringResource(Res.string.wish_sheet_edit_title),
                            tint = WlTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                    PlaceChip(place)
                    StatusChip(status)
                }
                Spacer(Modifier.height(spacing.s))
                Actions(actions = actions, onAction = onAction)
            }
        }
    }
}

@Composable
private fun TitleEditor(
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = WlTheme.shapes.large,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSave() }),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WlTheme.spacing.s, Alignment.End),
    ) {
        TextButton(onClick = onCancel) {
            Text(stringResource(Res.string.wish_sheet_cancel))
        }
        Button(onClick = onSave, enabled = value.isNotBlank()) {
            Text(stringResource(Res.string.wish_sheet_save))
        }
    }
}

@Composable
private fun PlaceChip(place: WishSheetPlace) {
    val text = when (place) {
        WishSheetPlace.NextCard -> stringResource(Res.string.wish_sheet_place_next_card)
        is WishSheetPlace.Card -> stringResource(Res.string.wish_sheet_place_card, place.number)
    }
    Chip(text = text, filled = true)
}

@Composable
private fun StatusChip(status: WishSheetStatus) {
    val text = when (status) {
        WishSheetStatus.Planned -> stringResource(Res.string.wish_sheet_status_planned)
        WishSheetStatus.Done -> stringResource(Res.string.wish_sheet_status_done)
        WishSheetStatus.Someday -> stringResource(Res.string.wish_sheet_status_someday)
    }
    Chip(text = text, filled = false)
}

@Composable
private fun Chip(text: String, filled: Boolean) {
    val colors = WlTheme.colorScheme
    val frame = if (filled) {
        Modifier.background(colors.primaryContainer, CircleShape)
    } else {
        Modifier.border(1.dp, colors.outlineVariant, CircleShape)
    }
    Text(
        text = text,
        style = WlTheme.typography.labelMedium,
        color = if (filled) colors.onPrimaryContainer else colors.onSurfaceVariant,
        modifier = frame.padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun Actions(actions: List<WishAction>, onAction: (WishAction) -> Unit) {
    val primary = actions.firstOrNull() ?: return
    val secondary = actions.drop(1)
    Column(verticalArrangement = Arrangement.spacedBy(WlTheme.spacing.s)) {
        Button(
            onClick = { onAction(primary) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            if (primary == WishAction.Achieve) {
                Icon(WlIcons.Check, contentDescription = null)
                Spacer(Modifier.width(WlTheme.spacing.s))
            }
            Text(text = primary.label(), style = WlTheme.typography.titleMedium)
        }
        if (secondary.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(WlTheme.spacing.s)) {
                secondary.forEach { action ->
                    SecondaryActionButton(
                        action = action,
                        onClick = { onAction(action) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SecondaryActionButton(action: WishAction, onClick: () -> Unit, modifier: Modifier = Modifier) {
    when (action) {
        WishAction.Delete -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WlTheme.colorScheme.error),
        ) {
            Text(text = action.label(), style = WlTheme.typography.titleMedium)
        }
        else -> FilledTonalButton(onClick = onClick, modifier = modifier) {
            Text(text = action.label(), style = WlTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun WishAction.label(): String = when (this) {
    WishAction.Achieve -> stringResource(Res.string.wish_action_achieve)
    WishAction.UndoAchieve -> stringResource(Res.string.wish_action_undo_achieve)
    WishAction.Someday -> stringResource(Res.string.wish_action_someday)
    WishAction.Restore -> stringResource(Res.string.wish_action_restore)
    WishAction.Delete -> stringResource(Res.string.wish_action_delete)
}
