package com.rmakiyama.wishline.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rmakiyama.wishline.designsystem.component.WlAppBar
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.domain.Item
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            WlAppBar(title = stringResource(Res.string.home_title))
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = WlTheme.spacing.m),
            contentPadding = PaddingValues(
                top = WlTheme.spacing.m,
                bottom = WlTheme.spacing.m + WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(WlTheme.spacing.m),
        ) {
            items(uiState.items) { item ->
                ItemCard(item)
            }
        }
    }
}

@Composable
private fun ItemCard(item: Item) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = WlTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(WlTheme.spacing.l),
        ) {
            Text(
                text = item.title,
                style = WlTheme.typography.titleMedium,
                color = WlTheme.colorScheme.onSurface,
            )
            item.description?.let { description ->
                Text(
                    text = description,
                    style = WlTheme.typography.bodyMedium,
                    color = WlTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
