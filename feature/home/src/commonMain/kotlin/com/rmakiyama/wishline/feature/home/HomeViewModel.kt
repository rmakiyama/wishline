package com.rmakiyama.wishline.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmakiyama.wishline.core.ui.component.WishAction
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishStatus
import com.rmakiyama.wishline.usecase.AddWishUseCase
import com.rmakiyama.wishline.usecase.ChangeWishTitleUseCase
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
                val id = createBingoCardUseCase(nextCard.wishes)
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

    /** Blank input is ignored; the sheet closes once the new title is on its way. */
    fun onSaveTitle() {
        val sheet = _uiState.value.sheet ?: return
        val title = sheet.titleInput.trim()
        if (title.isEmpty()) return
        _uiState.update { it.copy(sheet = null) }
        viewModelScope.launch {
            changeWishTitleUseCase(sheet.wish.id, title)
        }
    }

    /** Ignores an action the sheet does not offer, so a stale tap cannot move a wish the wrong way. */
    fun onWishAction(action: WishAction) {
        val sheet = _uiState.value.sheet ?: return
        if (action !in sheet.actions) return
        _uiState.update { it.copy(sheet = null) }
        viewModelScope.launch {
            val id = sheet.wish.id
            when (action) {
                WishAction.Achieve -> markWishDoneUseCase(id)
                WishAction.UndoAchieve -> restoreWishUseCase(id)
                WishAction.Someday -> markWishSomedayUseCase(id)
                WishAction.Restore -> restoreWishUseCase(id)
                WishAction.Delete -> deleteWishUseCase(id)
            }
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
    private val cardsLoaded: Boolean = false,
    private val wishesLoaded: Boolean = false,
) {
    /** Nothing is drawn until both streams have answered, so the next card never flashes empty. */
    val isLoaded: Boolean get() = cardsLoaded && wishesLoaded
}

data class NextCardUiState(
    val wishes: List<Wish> = emptyList(),
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

sealed interface WishPlace {
    data object NextCard : WishPlace
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
    /** The first action is the main one. A wish on the next card is always planned. */
    val actions: List<WishAction>
        get() = when (place) {
            WishPlace.NextCard -> listOf(WishAction.Achieve, WishAction.Someday, WishAction.Delete)
            is WishPlace.Card -> when (wish.status) {
                is WishStatus.Planned -> listOf(WishAction.Achieve, WishAction.Someday)
                is WishStatus.Done -> listOf(WishAction.UndoAchieve)
                is WishStatus.Someday -> listOf(WishAction.Restore)
            }
        }
}
