package com.rmakiyama.wishline.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rmakiyama.wishline.core.ui.component.BingoCell
import com.rmakiyama.wishline.core.ui.component.BingoCellState
import com.rmakiyama.wishline.core.ui.component.BingoGrid
import com.rmakiyama.wishline.core.ui.icon.WlIcons
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoSlot
import com.rmakiyama.wishline.domain.SlotStatus
import com.rmakiyama.wishline.domain.WishStatus
import org.jetbrains.compose.resources.stringResource

/** The container every page of the carousel sits in, so the cards and the next card share a frame. */
@Composable
internal fun CardFrame(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(WlTheme.shapes.extraLarge)
            .background(WlTheme.colorScheme.surfaceContainerLow)
            .padding(
                start = WlTheme.spacing.m,
                top = WlTheme.spacing.s + WlTheme.spacing.xs,
                end = WlTheme.spacing.m,
                bottom = WlTheme.spacing.m,
            ),
        verticalArrangement = Arrangement.spacedBy(WlTheme.spacing.s + WlTheme.spacing.xs),
    ) {
        content()
    }
}

@Composable
internal fun BingoCardPage(
    card: BingoCard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSlotClick: (BingoSlot) -> Unit,
) {
    // Sorted so that index equals position: the grid's lines and cell taps both address cells by position.
    val slots = remember(card) { card.slots.sortedBy { it.position } }
    CardFrame {
        CardHeader(card = card, onFlip = onFlip)
        if (isFlipped) {
            CardBack(slots = slots, onSlotClick = onSlotClick)
        } else {
            CardFront(card = card, slots = slots, onSlotClick = onSlotClick)
        }
    }
}

@Composable
private fun CardHeader(card: BingoCard, onFlip: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = WlTheme.spacing.s),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = card.label ?: stringResource(Res.string.home_card_default_label, card.number),
                style = WlTheme.typography.titleMedium,
                color = WlTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(Res.string.home_card_since, card.createdAt.toDateText()),
                style = WlTheme.typography.bodySmall,
                color = WlTheme.colorScheme.onSurfaceVariant,
            )
        }
        FilledIconButton(
            onClick = onFlip,
            modifier = Modifier.size(48.dp),
            shape = WlTheme.shapes.large,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = WlTheme.colorScheme.primaryContainer,
                contentColor = WlTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Icon(WlIcons.Flip, contentDescription = stringResource(Res.string.home_flip))
        }
    }
}

@Composable
private fun ColumnScope.CardFront(
    card: BingoCard,
    slots: List<BingoSlot>,
    onSlotClick: (BingoSlot) -> Unit,
) {
    val cells = remember(slots) { slots.map(BingoSlot::toCell) }
    val completedLines = remember(card) { card.completedLines().map { it.positions } }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        // Square, and shrinks by height when the page is shorter than it is wide.
        BingoGrid(
            cells = cells,
            completedLines = completedLines,
            modifier = Modifier.aspectRatio(1f),
            onCellClick = { position -> onSlotClick(slots[position]) },
        )
    }
}

@Composable
private fun ColumnScope.CardBack(
    slots: List<BingoSlot>,
    onSlotClick: (BingoSlot) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
    ) {
        items(slots, key = { it.position }) { slot ->
            SlotRow(slot = slot, onClick = { onSlotClick(slot) })
            HorizontalDivider(color = WlTheme.colorScheme.surfaceContainerHigh)
        }
    }
}

@Composable
private fun SlotRow(slot: BingoSlot, onClick: () -> Unit) {
    val colors = WlTheme.colorScheme
    val marked = slot.status as? SlotStatus.Marked
    val someday = marked == null && slot.wish.status is WishStatus.Someday
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = WlTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WlTheme.spacing.s + WlTheme.spacing.xs),
    ) {
        when {
            marked != null -> Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(colors.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(WlIcons.Check, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(16.dp))
            }
            someday -> DashedRing(modifier = Modifier.size(24.dp))
            else -> Icon(WlIcons.Circle, contentDescription = null, tint = colors.outline)
        }
        Text(
            text = slot.wish.title,
            style = WlTheme.typography.bodyLarge,
            color = when {
                marked != null -> colors.onSurfaceVariant
                someday -> colors.outline
                else -> colors.onSurface
            },
            textDecoration = if (someday) TextDecoration.LineThrough else null,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = WlTheme.spacing.s),
        )
        val trailing = when {
            marked != null -> marked.at.toMonthDayText()
            someday -> stringResource(Res.string.home_row_someday)
            else -> null
        }
        if (trailing != null) {
            Text(
                text = trailing,
                style = WlTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DashedRing(modifier: Modifier = Modifier) {
    val color = WlTheme.colorScheme.outline
    Box(
        modifier = modifier.drawBehind {
            val stroke = 2.dp.toPx()
            drawCircle(
                color = color,
                radius = size.minDimension / 2 - stroke,
                style = Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())),
                ),
            )
        },
    )
}

private fun BingoSlot.toCell(): BingoCell = BingoCell(
    title = wish.title,
    state = when {
        status is SlotStatus.Marked -> BingoCellState.Marked
        wish.status is WishStatus.Someday -> BingoCellState.Someday
        else -> BingoCellState.Unmarked
    },
)
