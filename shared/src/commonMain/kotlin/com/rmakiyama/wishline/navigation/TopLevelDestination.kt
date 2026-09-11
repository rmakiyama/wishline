package com.rmakiyama.wishline.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.rmakiyama.wishline.shared.Res
import com.rmakiyama.wishline.shared.tab_archive
import com.rmakiyama.wishline.shared.tab_home
import com.rmakiyama.wishline.shared.tab_pool
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The screens reachable from the bottom bar, in display order. */
internal enum class TopLevelDestination(
    val route: Route,
    val label: StringResource,
    val icon: ImageVector,
) {
    Home(HomeRoute, Res.string.tab_home, GridIcon),
    Pool(PoolRoute, Res.string.tab_pool, ListIcon),
    Archive(ArchiveRoute, Res.string.tab_archive, ArchiveIcon),
    ;

    companion object {
        fun of(route: Route?): TopLevelDestination? = entries.firstOrNull { it.route == route }
    }
}

@Composable
internal fun WishlineBottomBar(
    current: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == current,
                onClick = { onSelect(destination) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(stringResource(destination.label)) },
            )
        }
    }
}

// Stroke icons drawn inline so the module does not depend on material-icons.
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

private val GridIcon: ImageVector = strokeIcon("Grid") {
    roundedRect(4f, 4f, 7f, 7f, 1.5f)
    roundedRect(13f, 4f, 7f, 7f, 1.5f)
    roundedRect(4f, 13f, 7f, 7f, 1.5f)
    roundedRect(13f, 13f, 7f, 7f, 1.5f)
}

private val ListIcon: ImageVector = strokeIcon("List") {
    moveTo(9f, 6f); lineTo(20f, 6f)
    moveTo(9f, 12f); lineTo(20f, 12f)
    moveTo(9f, 18f); lineTo(20f, 18f)
    moveTo(4.5f, 6f); lineTo(5.5f, 6f)
    moveTo(4.5f, 12f); lineTo(5.5f, 12f)
    moveTo(4.5f, 18f); lineTo(5.5f, 18f)
}

private val ArchiveIcon: ImageVector = strokeIcon("Archive") {
    roundedRect(3f, 4f, 18f, 5f, 1.5f)
    moveTo(5f, 9f); verticalLineTo(18f)
    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 7f, y1 = 20f)
    horizontalLineTo(17f)
    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, x1 = 19f, y1 = 18f)
    verticalLineTo(9f)
    moveTo(10f, 13f); lineTo(14f, 13f)
}
