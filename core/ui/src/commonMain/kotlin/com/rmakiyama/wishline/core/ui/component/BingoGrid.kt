package com.rmakiyama.wishline.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmakiyama.wishline.designsystem.theme.WlTheme

private const val SIZE = 5

enum class BingoCellState { Unmarked, Marked, Someday }

@Immutable
data class BingoCell(
    val title: String,
    val state: BingoCellState,
)

/**
 * A 5×5 bingo grid. [cells] are in row-major order and [completedLines] list the positions of each
 * completed line; a capsule is drawn under those cells.
 */
@Composable
fun BingoGrid(
    cells: List<BingoCell>,
    completedLines: List<List<Int>>,
    modifier: Modifier = Modifier,
    onCellClick: ((position: Int) -> Unit)? = null,
) {
    require(cells.size == SIZE * SIZE) { "A bingo grid takes ${SIZE * SIZE} cells, was ${cells.size}" }
    val gap = 6.dp
    val lineColor = WlTheme.extendedColors.line
    BoxWithConstraints(modifier = modifier) {
        val cellSize = (maxWidth - gap * (SIZE - 1)) / SIZE
        Column(
            modifier = Modifier.drawBehind {
                val step = (cellSize + gap).toPx()
                val half = cellSize.toPx() / 2
                completedLines.forEach { positions ->
                    val first = positions.first()
                    val last = positions.last()
                    drawLine(
                        color = lineColor,
                        start = Offset(first % SIZE * step + half, first / SIZE * step + half),
                        end = Offset(last % SIZE * step + half, last / SIZE * step + half),
                        strokeWidth = cellSize.toPx() + 10.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            },
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            repeat(SIZE) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    repeat(SIZE) { column ->
                        val position = row * SIZE + column
                        BingoCellView(
                            cell = cells[position],
                            modifier = Modifier.size(cellSize),
                            onClick = onCellClick?.let { { it(position) } },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BingoCellView(
    cell: BingoCell,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = WlTheme.colorScheme
    val extended = WlTheme.extendedColors
    val shape: Shape
    val background: Color
    val contentColor: Color
    when (cell.state) {
        BingoCellState.Unmarked -> {
            shape = WlTheme.shapes.medium
            background = colors.surfaceContainerHigh
            contentColor = colors.onSurfaceVariant
        }
        BingoCellState.Marked -> {
            shape = CircleShape
            background = extended.fill
            contentColor = extended.onFill
        }
        BingoCellState.Someday -> {
            shape = WlTheme.shapes.medium
            background = colors.surfaceContainerLowest
            contentColor = colors.outline
        }
    }
    val dashColor = colors.outlineVariant
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .then(
                if (cell.state == BingoCellState.Someday) {
                    Modifier.drawBehind {
                        val stroke = 1.5.dp.toPx()
                        inset(stroke / 2) {
                            drawOutline(
                                outline = shape.createOutline(size, layoutDirection, this),
                                color = dashColor,
                                style = Stroke(
                                    width = stroke,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                                ),
                            )
                        }
                    }
                } else {
                    Modifier
                },
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = cell.title,
            color = contentColor,
            fontSize = 10.sp,
            lineHeight = 12.5.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (cell.state == BingoCellState.Someday) TextDecoration.LineThrough else null,
        )
    }
}
