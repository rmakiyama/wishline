package com.rmakiyama.wishline.core.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmakiyama.wishline.core.ui.Res
import com.rmakiyama.wishline.core.ui.icon.WlIcons
import com.rmakiyama.wishline.core.ui.tab_archive
import com.rmakiyama.wishline.core.ui.tab_home
import com.rmakiyama.wishline.core.ui.tab_pool
import com.rmakiyama.wishline.navigation.TopLevelDestination
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WlBottomBar(
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

private val TopLevelDestination.label: StringResource
    get() = when (this) {
        TopLevelDestination.Home -> Res.string.tab_home
        TopLevelDestination.Pool -> Res.string.tab_pool
        TopLevelDestination.Archive -> Res.string.tab_archive
    }

private val TopLevelDestination.icon: ImageVector
    get() = when (this) {
        TopLevelDestination.Home -> WlIcons.Grid
        TopLevelDestination.Pool -> WlIcons.List
        TopLevelDestination.Archive -> WlIcons.Archive
    }
