package com.rmakiyama.wishline.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rmakiyama.wishline.core.ui.component.BingoCell
import com.rmakiyama.wishline.core.ui.component.BingoCellState
import com.rmakiyama.wishline.core.ui.component.BingoGrid
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoSlot
import com.rmakiyama.wishline.domain.SlotStatus
import com.rmakiyama.wishline.domain.WishStatus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

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
internal fun BingoCardPage(card: BingoCard) {
    CardFrame {
        Column(
            modifier = Modifier.padding(top = WlTheme.spacing.s),
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
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            BingoGrid(
                cells = card.slots.map(BingoSlot::toCell),
                completedLines = card.completedLines().map { it.positions },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )
        }
    }
}

private fun BingoSlot.toCell(): BingoCell = BingoCell(
    title = wish.title,
    state = when {
        status is SlotStatus.Marked -> BingoCellState.Marked
        wish.status is WishStatus.Someday -> BingoCellState.Someday
        else -> BingoCellState.Unmarked
    },
)

private fun Instant.toDateText(): String {
    val date = toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date.year}/${date.month.number}/${date.day}"
}
