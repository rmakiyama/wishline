package com.rmakiyama.wishline.core.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Stroke icons drawn inline so no module depends on material-icons. */
object WlIcons {
    val Grid: ImageVector = strokeIcon("Grid") {
        roundedRect(4f, 4f, 7f, 7f, 1.5f)
        roundedRect(13f, 4f, 7f, 7f, 1.5f)
        roundedRect(4f, 13f, 7f, 7f, 1.5f)
        roundedRect(13f, 13f, 7f, 7f, 1.5f)
    }

    val List: ImageVector = strokeIcon("List") {
        moveTo(9f, 6f); lineTo(20f, 6f)
        moveTo(9f, 12f); lineTo(20f, 12f)
        moveTo(9f, 18f); lineTo(20f, 18f)
        moveTo(4.5f, 6f); lineTo(5.5f, 6f)
        moveTo(4.5f, 12f); lineTo(5.5f, 12f)
        moveTo(4.5f, 18f); lineTo(5.5f, 18f)
    }

    val ArrowUp: ImageVector = strokeIcon("ArrowUp") {
        moveTo(12f, 19f); lineTo(12f, 5f)
        moveTo(6f, 11f); lineTo(12f, 5f); lineTo(18f, 11f)
    }

    /** An empty ring: a wish that is still planned. */
    val Circle: ImageVector = strokeIcon("Circle") {
        moveTo(20f, 12f)
        arcTo(8f, 8f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 4f, y1 = 12f)
        arcTo(8f, 8f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 20f, y1 = 12f)
    }

    /** Three dots: each is a 0.01 long stroke whose round cap gives it its size. */
    val More: ImageVector = strokeIcon("More") {
        moveTo(12f, 5f); lineTo(12f, 5.01f)
        moveTo(12f, 12f); lineTo(12f, 12.01f)
        moveTo(12f, 19f); lineTo(12f, 19.01f)
    }

    val Check: ImageVector = strokeIcon("Check") {
        moveTo(5f, 12f); lineTo(10f, 17f); lineTo(19f, 7f)
    }

    val Pencil: ImageVector = strokeIcon("Pencil") {
        moveTo(4f, 20f); horizontalLineTo(8f); lineTo(18f, 10f); lineTo(14f, 6f); lineTo(4f, 16f); verticalLineTo(20f)
        close()
        moveTo(12.5f, 7.5f); lineTo(16.5f, 11.5f)
    }

    /** Two arrows crossing a small square: turn the card over. */
    val Flip: ImageVector = strokeIcon("Flip") {
        moveTo(4f, 8f); verticalLineTo(4f); horizontalLineTo(8f)
        moveTo(20f, 16f); verticalLineTo(20f); horizontalLineTo(16f)
        moveTo(4f, 4f); lineTo(10f, 10f)
        moveTo(20f, 20f); lineTo(14f, 14f)
        roundedRect(9f, 9f, 6f, 6f, 1.5f)
    }

    val Archive: ImageVector = strokeIcon("Archive") {
        roundedRect(3f, 4f, 18f, 5f, 1.5f)
        moveTo(5f, 9f); verticalLineTo(18f)
        arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 7f, y1 = 20f)
        horizontalLineTo(17f)
        arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 19f, y1 = 18f)
        verticalLineTo(9f)
        moveTo(10f, 13f); lineTo(14f, 13f)
    }
}

private fun strokeIcon(name: String, builder: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
        .path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = builder,
        )
        .build()

private fun PathBuilder.roundedRect(x: Float, y: Float, w: Float, h: Float, r: Float) {
    moveTo(x + r, y)
    horizontalLineTo(x + w - r)
    arcTo(r, r, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = x + w, y1 = y + r)
    verticalLineTo(y + h - r)
    arcTo(r, r, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = x + w - r, y1 = y + h)
    horizontalLineTo(x + r)
    arcTo(r, r, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = x, y1 = y + h - r)
    verticalLineTo(y + r)
    arcTo(r, r, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = x + r, y1 = y)
    close()
}
