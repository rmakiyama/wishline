package com.rmakiyama.wishline.feature.pool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rmakiyama.wishline.designsystem.component.WlAppBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun PoolScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            WlAppBar(title = stringResource(Res.string.pool_title))
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}
