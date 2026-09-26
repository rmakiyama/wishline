package com.rmakiyama.wishline.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rmakiyama.wishline.core.ui.icon.WlIcons
import com.rmakiyama.wishline.designsystem.component.WlAppBar
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.UnassignedWish
import com.rmakiyama.wishline.domain.WishId
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectionScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SelectionViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isCreated) {
        if (uiState.isCreated) onFinished()
    }

    SelectionScreen(
        uiState = uiState,
        onClose = onFinished,
        onToggle = viewModel::onToggle,
        onToggleAll = viewModel::onToggleAll,
        onCreate = viewModel::onCreate,
        modifier = modifier,
    )
}

@Composable
private fun SelectionScreen(
    uiState: SelectionUiState,
    onClose: () -> Unit,
    onToggle: (WishId) -> Unit,
    onToggleAll: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            WlAppBar(
                title = stringResource(Res.string.selection_title, BingoCard.SLOT_COUNT),
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(WlIcons.Close, contentDescription = stringResource(Res.string.selection_close))
                    }
                },
                actions = {
                    TextButton(onClick = onToggleAll, enabled = uiState.wishes.isNotEmpty()) {
                        Text(
                            stringResource(
                                if (uiState.isAllSelected) Res.string.selection_clear_all else Res.string.selection_select_all,
                            ),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = onCreate,
                enabled = uiState.canCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = WlTheme.spacing.l, vertical = WlTheme.spacing.m)
                    .height(56.dp),
            ) {
                Text(stringResource(Res.string.selection_create), style = WlTheme.typography.titleMedium)
            }
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        if (!uiState.isLoaded) {
            Box(Modifier.fillMaxSize().padding(paddingValues))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Counter(count = uiState.selectedCount)
            Box(modifier = Modifier.weight(1f)) {
                // The fade covers the bottom, so the last row needs room to scroll out from under it.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = FADE_HEIGHT),
                ) {
                    items(uiState.wishes, key = { it.wish.id.value }) { unassigned ->
                        SelectionRow(
                            wish = unassigned,
                            isSelected = unassigned.wish.id in uiState.selectedIds,
                            onToggle = { onToggle(unassigned.wish.id) },
                        )
                    }
                }
                val surface = WlTheme.colorScheme.surface
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(FADE_HEIGHT)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, surface))),
                )
            }
        }
    }
}

private val FADE_HEIGHT = 32.dp

@Composable
private fun Counter(count: Int) {
    val spacing = WlTheme.spacing
    val over = count - BingoCard.SLOT_COUNT
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing.l, end = spacing.l, top = spacing.s, bottom = spacing.m),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(
                text = count.toString(),
                modifier = Modifier.alignByBaseline(),
                style = WlTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                // Going over is allowed on the way to 25, so it is only coloured, never blocked.
                color = if (over > 0) WlTheme.colorScheme.error else WlTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(Res.string.selection_total, BingoCard.SLOT_COUNT),
                style = WlTheme.typography.titleLarge,
                color = WlTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alignByBaseline(),
            )
        }
        when {
            over > 0 -> CountChip(
                text = stringResource(Res.string.selection_over, over),
                containerColor = WlTheme.colorScheme.errorContainer,
                contentColor = WlTheme.colorScheme.onErrorContainer,
            )
            over < 0 -> CountChip(
                text = stringResource(Res.string.selection_remaining, -over),
                containerColor = WlTheme.colorScheme.surfaceContainerHigh,
                contentColor = WlTheme.colorScheme.onSurfaceVariant,
            )
            else -> Unit
        }
    }
}

@Composable
private fun CountChip(text: String, containerColor: Color, contentColor: Color) {
    Text(
        text = text,
        style = WlTheme.typography.labelLarge,
        color = contentColor,
        modifier = Modifier
            .padding(bottom = WlTheme.spacing.xs)
            .clip(WlTheme.shapes.small)
            .background(containerColor)
            .padding(horizontal = WlTheme.spacing.s + WlTheme.spacing.xs, vertical = 6.dp),
    )
}

@Composable
private fun SelectionRow(
    wish: UnassignedWish,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = isSelected, role = Role.Checkbox, onValueChange = { onToggle() })
            .background(if (isSelected) WlTheme.colorScheme.surfaceContainerLow else Color.Transparent)
            .heightIn(min = 56.dp)
            .padding(horizontal = WlTheme.spacing.l, vertical = WlTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WlTheme.spacing.s + WlTheme.spacing.xs),
    ) {
        CheckMark(isSelected = isSelected)
        Text(
            text = wish.wish.title,
            style = WlTheme.typography.bodyLarge,
            color = WlTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CheckMark(isSelected: Boolean) {
    val shape = RoundedCornerShape(6.dp)
    if (isSelected) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(shape)
                .background(WlTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                WlIcons.Check,
                contentDescription = null,
                tint = WlTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, WlTheme.colorScheme.onSurfaceVariant, shape),
        )
    }
}
