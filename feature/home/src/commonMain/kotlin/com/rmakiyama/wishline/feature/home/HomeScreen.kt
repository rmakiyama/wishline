package com.rmakiyama.wishline.feature.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rmakiyama.wishline.core.ui.component.WishSheet
import com.rmakiyama.wishline.core.ui.component.WishSheetPlace
import com.rmakiyama.wishline.core.ui.component.WishSheetStatus
import com.rmakiyama.wishline.designsystem.component.WlAppBar
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishStatus
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onInputChange = viewModel::onInputChange,
        onAddWish = viewModel::onAddWish,
        onCreateCard = viewModel::onCreateCard,
        onSelectClick = onSelectClick,
        onCreatedCardShown = viewModel::onCreatedCardShown,
        onFlipCard = viewModel::onFlipCard,
        onWishClick = viewModel::onWishClick,
        onCloseCardClick = viewModel::onCloseCardClick,
        onEditLabelClick = viewModel::onEditLabelClick,
        modifier = modifier,
    )

    val dialog = uiState.cardDialog
    val dialogCard = uiState.dialogCard
    if (dialog != null && dialogCard != null) {
        when (dialog) {
            is CardDialog.CloseConfirm -> CloseCardSheet(
                returningCount = dialogCard.wishesReturningOnClose().size,
                onConfirm = viewModel::onConfirmClose,
                onDismiss = viewModel::onDismissCardDialog,
            )
            is CardDialog.EditLabel -> EditLabelDialog(
                input = dialog.input,
                cardNumber = dialogCard.number,
                onInputChange = viewModel::onLabelInputChange,
                onSave = viewModel::onSaveLabel,
                onDismiss = viewModel::onDismissCardDialog,
            )
        }
    }

    val sheet = uiState.sheet
    if (sheet != null) {
        WishSheet(
            title = sheet.wish.title,
            place = when (val place = sheet.place) {
                is WishPlace.NextCard -> WishSheetPlace.NextCard
                is WishPlace.Card -> WishSheetPlace.Card(place.number)
            },
            status = when (sheet.wish.status) {
                is WishStatus.Planned -> WishSheetStatus.Planned
                is WishStatus.Done -> WishSheetStatus.Done
                is WishStatus.Someday -> WishSheetStatus.Someday
            },
            actions = sheet.actions,
            isEditingTitle = sheet.isEditingTitle,
            titleInput = sheet.titleInput,
            onDismiss = viewModel::onDismissSheet,
            onStartEditTitle = viewModel::onStartEditTitle,
            onTitleInputChange = viewModel::onTitleInputChange,
            onSaveTitle = viewModel::onSaveTitle,
            onCancelEditTitle = viewModel::onCancelEditTitle,
            onAction = viewModel::onWishAction,
        )
    }
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onInputChange: (String) -> Unit,
    onAddWish: () -> Unit,
    onCreateCard: () -> Unit,
    onSelectClick: () -> Unit,
    onCreatedCardShown: () -> Unit,
    onFlipCard: (BingoCardId) -> Unit,
    onWishClick: (Wish, WishPlace) -> Unit,
    onCloseCardClick: (BingoCardId) -> Unit,
    onEditLabelClick: (BingoCardId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            WlAppBar(title = stringResource(Res.string.home_title))
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        if (!uiState.isLoaded) {
            Box(Modifier.fillMaxSize().padding(paddingValues))
            return@Scaffold
        }
        // The next card is always the last page.
        val pageCount = uiState.cards.size + 1
        val pagerState = rememberPagerState(pageCount = { pageCount })

        LaunchedEffect(uiState.createdCardId, uiState.cards) {
            val index = uiState.cards.indexOfFirst { it.id == uiState.createdCardId }
            if (index >= 0) {
                pagerState.animateScrollToPage(index)
                onCreatedCardShown()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(top = WlTheme.spacing.s, bottom = WlTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(WlTheme.spacing.m),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = WlTheme.spacing.l),
                pageSpacing = WlTheme.spacing.s + WlTheme.spacing.xs,
                key = { page -> uiState.cards.getOrNull(page)?.id?.value ?: NEXT_CARD_KEY },
            ) { page ->
                val card = uiState.cards.getOrNull(page)
                if (card != null) {
                    BingoCardPage(
                        card = card,
                        isFlipped = card.id in uiState.flippedCardIds,
                        onFlip = { onFlipCard(card.id) },
                        onSlotClick = { slot -> onWishClick(slot.wish, WishPlace.Card(card.id, card.number)) },
                        onCloseClick = { onCloseCardClick(card.id) },
                        onEditLabelClick = { onEditLabelClick(card.id) },
                    )
                } else {
                    NextCardPage(
                        uiState = uiState.nextCard,
                        onInputChange = onInputChange,
                        onAddWish = onAddWish,
                        onCreateCard = onCreateCard,
                        onSelectClick = onSelectClick,
                        onWishClick = { onWishClick(it.wish, WishPlace.NextCard(hasBeenOnCard = it.hasBeenOnCard)) },
                    )
                }
            }
            PageIndicator(count = pageCount, current = pagerState.currentPage)
        }
    }
}

private const val NEXT_CARD_KEY = "next-card"

@Composable
private fun PageIndicator(count: Int, current: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, alignment = Alignment.CenterHorizontally),
    ) {
        repeat(count) { index ->
            val active = index == current
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .height(6.dp)
                    .width(if (active) 18.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) WlTheme.colorScheme.primary else WlTheme.colorScheme.outlineVariant,
                    ),
            )
        }
    }
}
