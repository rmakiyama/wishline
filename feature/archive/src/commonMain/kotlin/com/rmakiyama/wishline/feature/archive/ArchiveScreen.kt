package com.rmakiyama.wishline.feature.archive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rmakiyama.wishline.designsystem.component.WlAppBar
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            WlAppBar(title = stringResource(Res.string.archive_title))
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.archive_empty),
                style = WlTheme.typography.bodyLarge,
                color = WlTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
