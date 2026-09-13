package com.rmakiyama.wishline.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmakiyama.wishline.core.ui.component.WishAction
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.SlotStatus
import com.rmakiyama.wishline.domain.UnassignedWish
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import com.rmakiyama.wishline.usecase.AddWishUseCase
import com.rmakiyama.wishline.usecase.ChangeBingoCardLabelUseCase
import com.rmakiyama.wishline.usecase.ChangeWishTitleUseCase
import com.rmakiyama.wishline.usecase.CloseBingoCardUseCase
import com.rmakiyama.wishline.usecase.CreateBingoCardUseCase
import com.rmakiyama.wishline.usecase.DeleteWishUseCase
import com.rmakiyama.wishline.usecase.GetOpenBingoCardsStreamUseCase
import com.rmakiyama.wishline.usecase.GetUnassignedWishesStreamUseCase
import com.rmakiyama.wishline.usecase.MarkWishDoneUseCase
import com.rmakiyama.wishline.usecase.MarkWishSomedayUseCase
import com.rmakiyama.wishline.usecase.RestoreWishUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class HomeViewModel(
    private val getOpenBingoCardsStream: GetOpenBingoCardsStreamUseCase,
    private val getUnassignedWishesStream: GetUnassignedWishesStreamUseCase,
    private val addWishUseCase: AddWishUseCase,
    private val createBingoCardUseCase: CreateBingoCardUseCase,
    private val changeWishTitleUseCase: ChangeWishTitleUseCase,
    private val markWishDoneUseCase: MarkWishDoneUseCase,
    private val markWishSomedayUseCase: MarkWishSomedayUseCase,
    private val restoreWishUseCase: RestoreWishUseCase,
    private val deleteWishUseCase: DeleteWishUseCase,
    private val closeBingoCardUseCase: CloseBingoCardUseCase,
    private val changeBingoCardLabelUseCase: ChangeBingoCardLabelUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeOpenCards()
        observeUnassignedWishes()
    }

    private fun observeOpenCards() {
        viewModelScope.launch {
            getOpenBingoCardsStream().collect { cards ->
                _uiState.update { it.copy(cards = cards, cardsLoaded = true) }
            }
        }
    }

    private fun observeUnassignedWishes() {
        viewModelScope.launch {
            getUnassignedWishesStream().collect { wishes ->
                _uiState.update { it.copy(nextCard = it.nextCard.copy(wishes = wishes), wishesLoaded = true) }
            }
        }
    }

    fun onInputChange(value: String) {
        _uiState.update { it.copy(nextCard = it.nextCard.copy(input = value)) }
    }

    /** Saves the current input as a new wish. Blank input is ignored. */
    fun onAddWish() {
        val title = _uiState.value.nextCard.input.trim()
        if (title.isEmpty()) return
        _uiState.update { it.copy(nextCard = it.nextCard.copy(input = "")) }
        viewModelScope.launch {
            addWishUseCase(title)
        }
    }

    /** Only when exactly 25 wishes are waiting: any other count needs the selection screen first. */
    fun onCreateCard() {
        val nextCard = _uiState.value.nextCard
        if (nextCard.readiness != NextCardReadiness.Ready || nextCard.isCreating) return
        _uiState.update { it.copy(nextCard = it.nextCard.copy(isCreating = true)) }
        viewModelScope.launch {
            try {
                val id = createBingoCardUseCase(nextCard.wishes.map { it.wish })
                _uiState.update { it.copy(createdCardId = id) }
            } catch (e: Exception) {
                // The next card stays as it was, so the user can simply try again.
            } finally {
                _uiState.update { it.copy(nextCard = it.nextCard.copy(isCreating = false)) }
            }
        }
    }

    fun onCreatedCardShown() {
        _uiState.update { it.copy(createdCardId = null) }
    }

    fun onFlipCard(id: BingoCardId) {
        _uiState.update {
            val flipped = if (id in it.flippedCardIds) it.flippedCardIds - id else it.flippedCardIds + id
            it.copy(flippedCardIds = flipped)
        }
    }

    fun onWishClick(wish: Wish, place: WishPlace) {
        _uiState.update { it.copy(sheet = WishSheetState(wish = wish, place = place)) }
    }

    fun onDismissSheet() {
        _uiState.update { it.copy(sheet = null) }
    }

    fun onStartEditTitle() {
        _uiState.update { state ->
            state.copy(sheet = state.sheet?.let { it.copy(isEditingTitle = true, titleInput = it.wish.title) })
        }
    }

    fun onTitleInputChange(value: String) {
        _uiState.update { state -> state.copy(sheet = state.sheet?.copy(titleInput = value)) }
    }

    fun onCancelEditTitle() {
        _uiState.update { state -> state.copy(sheet = state.sheet?.copy(isEditingTitle = false)) }
    }

    fun onSaveTitle() {
        val sheet = _uiState.value.sheet ?: return
        val title = sheet.titleInput.trim()
        if (title.isEmpty()) return
        _uiState.update { it.copy(sheet = null) }
        write { changeWishTitleUseCase(sheet.wish.id, title) }
    }

    /** Ignores an action the sheet does not offer, so a stale tap cannot move a wish the wrong way. */
    fun onWishAction(action: WishAction) {
        val sheet = _uiState.value.sheet ?: return
        if (action !in sheet.actions) return
        _uiState.update { it.copy(sheet = null) }
        val id = sheet.wish.id
        write {
            when (action) {
                WishAction.Achieve -> {
                    markWishDoneUseCase(id)
                    closeIfCompleted(sheet.place, id)
                }
                WishAction.UndoAchieve -> restoreWishUseCase(id)
                WishAction.Someday -> markWishSomedayUseCase(id)
                WishAction.Restore -> restoreWishUseCase(id)
                WishAction.Delete -> deleteWishUseCase(id)
            }
        }
    }

    /** Marking the last slot closes the card on its own: there is nothing left to decide, so no confirmation. */
    private suspend fun closeIfCompleted(place: WishPlace, marked: WishId) {
        val cardId = (place as? WishPlace.Card)?.id ?: return
        val card = _uiState.value.cards.firstOrNull { it.id == cardId } ?: return
        val completed = card.slots.all { it.status is SlotStatus.Marked || it.wish.id == marked }
        if (completed) closeBingoCardUseCase(cardId)
    }

    fun onCloseCardClick(card: BingoCard) {
        _uiState.update { it.copy(cardDialog = CardDialog.CloseConfirm(card)) }
    }

    // TODO: アーカイブ詳細ができたら、クローズ後にそこへ遷移する
    fun onConfirmClose() {
        val dialog = _uiState.value.cardDialog as? CardDialog.CloseConfirm ?: return
        _uiState.update { it.copy(cardDialog = null) }
        write { closeBingoCardUseCase(dialog.card.id) }
    }

    fun onEditLabelClick(card: BingoCard) {
        _uiState.update { it.copy(cardDialog = CardDialog.EditLabel(card, input = card.label.orEmpty())) }
    }

    fun onLabelInputChange(value: String) {
        _uiState.update { state ->
            val dialog = state.cardDialog as? CardDialog.EditLabel ?: return@update state
            state.copy(cardDialog = dialog.copy(input = value))
        }
    }

    fun onSaveLabel() {
        val dialog = _uiState.value.cardDialog as? CardDialog.EditLabel ?: return
        _uiState.update { it.copy(cardDialog = null) }
        write { changeBingoCardLabelUseCase(dialog.card.id, dialog.input) }
    }

    fun onDismissCardDialog() {
        _uiState.update { it.copy(cardDialog = null) }
    }

    /**
     * Not rethrown: an uncaught failure here would kill the app, while the wish is still as it was
     * and the streams keep showing it.
     */
    private fun write(block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
        }
    }
}

data class HomeUiState(
    val cards: List<BingoCard> = emptyList(),
    val nextCard: NextCardUiState = NextCardUiState(),
    /** Set once after a card is created, so the screen can move to it. */
    val createdCardId: BingoCardId? = null,
    val flippedCardIds: Set<BingoCardId> = emptySet(),
    val sheet: WishSheetState? = null,
    val cardDialog: CardDialog? = null,
    private val cardsLoaded: Boolean = false,
    private val wishesLoaded: Boolean = false,
) {
    /** Nothing is drawn until both streams have answered, so the next card never flashes empty. */
    val isLoaded: Boolean get() = cardsLoaded && wishesLoaded
}

data class NextCardUiState(
    val wishes: List<UnassignedWish> = emptyList(),
    val input: String = "",
    val isCreating: Boolean = false,
) {
    val readiness: NextCardReadiness
        get() = when {
            wishes.size < BingoCard.SLOT_COUNT -> NextCardReadiness.Filling
            wishes.size == BingoCard.SLOT_COUNT -> NextCardReadiness.Ready
            else -> NextCardReadiness.Overflowing
        }
}

enum class NextCardReadiness { Filling, Ready, Overflowing }

sealed interface CardDialog {
    data class CloseConfirm(val card: BingoCard) : CardDialog {
        /** These are the wishes that return to the next card. */
        val plannedCount: Int
            get() = card.slots.count { it.status is SlotStatus.Unmarked && it.wish.status is WishStatus.Planned }
    }

    data class EditLabel(val card: BingoCard, val input: String) : CardDialog
}

sealed interface WishPlace {
    data class NextCard(val hasBeenOnCard: Boolean) : WishPlace
    data class Card(val id: BingoCardId, val number: Int) : WishPlace
}

/**
 * The wish is a snapshot from when the sheet opened. Every action closes the sheet, so it never
 * has to follow a wish that changes underneath it.
 */
data class WishSheetState(
    val wish: Wish,
    val place: WishPlace,
    val isEditingTitle: Boolean = false,
    val titleInput: String = "",
) {
    /** A wish on the next card is always planned, so its place alone decides. */
    val actions: List<WishAction>
        get() = when (place) {
            is WishPlace.NextCard -> buildList {
                add(WishAction.Achieve)
                add(WishAction.Someday)
                if (!place.hasBeenOnCard) add(WishAction.Delete)
            }
            is WishPlace.Card -> when (wish.status) {
                is WishStatus.Planned -> listOf(WishAction.Achieve, WishAction.Someday)
                is WishStatus.Done -> listOf(WishAction.UndoAchieve)
                is WishStatus.Someday -> listOf(WishAction.Restore)
            }
        }
}
