package com.rmakiyama.wishline.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.usecase.AddWishUseCase
import com.rmakiyama.wishline.usecase.CreateBingoCardUseCase
import com.rmakiyama.wishline.usecase.GetOpenBingoCardsStreamUseCase
import com.rmakiyama.wishline.usecase.GetUnassignedWishesStreamUseCase
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
}

data class HomeUiState(
    val cards: List<BingoCard> = emptyList(),
    val nextCard: NextCardUiState = NextCardUiState(),
    /** Set once after a card is created, so the screen can move to it. */
    val createdCardId: BingoCardId? = null,
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
