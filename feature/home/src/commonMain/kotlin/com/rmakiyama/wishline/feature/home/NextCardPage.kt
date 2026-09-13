package com.rmakiyama.wishline.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rmakiyama.wishline.core.ui.icon.WlIcons
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.Wish
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NextCardPage(
    uiState: NextCardUiState,
    onInputChange: (String) -> Unit,
    onAddWish: () -> Unit,
    onCreateCard: () -> Unit,
    onWishClick: (Wish) -> Unit,
) {
    val spacing = WlTheme.spacing
    CardFrame {
        Header(uiState = uiState, onCreateCard = onCreateCard)
        // Newest at the bottom, right above the input, and the list hugs the input when short.
        val listState = rememberLazyListState()
        // Keys pin the scroll position to an item, so a new wish would slide in out of view.
        LaunchedEffect(uiState.wishes.size) {
            listState.animateScrollToItem(0)
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true,
        ) {
            items(uiState.wishes.asReversed(), key = { it.id.value }) { wish ->
                WishRow(wish = wish, onClick = { onWishClick(wish) })
                HorizontalDivider(color = WlTheme.colorScheme.surfaceContainerHigh)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            OutlinedTextField(
                value = uiState.input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(Res.string.home_input_placeholder)) },
                singleLine = true,
                shape = WlTheme.shapes.large,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddWish() }),
            )
            FilledIconButton(
                onClick = onAddWish,
                enabled = uiState.input.isNotBlank(),
                modifier = Modifier.size(56.dp),
                shape = WlTheme.shapes.large,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = WlTheme.colorScheme.primaryContainer,
                    contentColor = WlTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Icon(WlIcons.ArrowUp, contentDescription = stringResource(Res.string.home_add))
            }
        }
    }
}

@Composable
private fun Header(
    uiState: NextCardUiState,
    onCreateCard: () -> Unit,
) {
    val count = uiState.wishes.size
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = WlTheme.spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WlTheme.spacing.s),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.home_next_card_title),
                style = WlTheme.typography.titleMedium,
                color = WlTheme.colorScheme.onSurface,
            )
            Text(
                text = when (uiState.readiness) {
                    NextCardReadiness.Filling -> stringResource(
                        Res.string.home_next_card_progress,
                        count,
                        BingoCard.SLOT_COUNT,
                        BingoCard.SLOT_COUNT - count,
                    )
                    NextCardReadiness.Ready -> stringResource(
                        Res.string.home_next_card_ready,
                        count,
                        BingoCard.SLOT_COUNT,
                    )
                    NextCardReadiness.Overflowing -> stringResource(Res.string.home_next_card_overflow, count)
                },
                style = WlTheme.typography.bodySmall,
                color = WlTheme.colorScheme.onSurfaceVariant,
            )
        }
        when (uiState.readiness) {
            NextCardReadiness.Filling -> Unit
            NextCardReadiness.Ready -> Button(onClick = onCreateCard, enabled = !uiState.isCreating) {
                Text(stringResource(Res.string.home_next_card_create))
            }
            // TODO: 選抜画面ができたら有効にして、そこへ遷移する
            NextCardReadiness.Overflowing -> Button(onClick = {}, enabled = false) {
                Text(stringResource(Res.string.home_next_card_select))
            }
        }
    }
}

@Composable
private fun WishRow(wish: Wish, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = WlTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WlTheme.spacing.s + WlTheme.spacing.xs),
    ) {
        Icon(
            WlIcons.Circle,
            contentDescription = null,
            tint = WlTheme.colorScheme.outline,
        )
        Text(
            text = wish.title,
            style = WlTheme.typography.bodyLarge,
            color = WlTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = WlTheme.spacing.s),
        )
    }
}
