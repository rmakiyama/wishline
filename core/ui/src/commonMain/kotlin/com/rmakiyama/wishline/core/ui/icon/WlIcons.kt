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
