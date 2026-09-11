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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class HomeViewModel(
    getOpenBingoCardsStream: GetOpenBingoCardsStreamUseCase,
    getUnassignedWishesStream: GetUnassignedWishesStreamUseCase,
    private val addWishUseCase: AddWishUseCase,
    private val createBingoCardUseCase: CreateBingoCardUseCase,
) : ViewModel() {
    private val input = MutableStateFlow("")
    private val isCreating = MutableStateFlow(false)
    private val createdCardId = MutableStateFlow<BingoCardId?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        getOpenBingoCardsStream(),
        getUnassignedWishesStream(),
        input,
        isCreating,
        createdCardId,
    ) { cards, wishes, input, isCreating, createdCardId ->
        HomeUiState(
            isLoaded = true,
            cards = cards,
            nextCard = NextCardUiState(wishes = wishes, input = input, isCreating = isCreating),
            createdCardId = createdCardId,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onInputChange(value: String) {
        input.value = value
    }

    /** Saves the current input as a new wish. Blank input is ignored. */
    fun onAddWish() {
        val title = input.value.trim()
        if (title.isEmpty()) return
        input.value = ""
        viewModelScope.launch {
            addWishUseCase(title)
        }
    }

    /** Only when exactly 25 wishes are waiting: any other count needs the selection screen first. */
    fun onCreateCard() {
        val nextCard = uiState.value.nextCard
        if (nextCard.readiness != NextCardReadiness.Ready || nextCard.isCreating) return
        isCreating.value = true
        viewModelScope.launch {
            try {
                createdCardId.value = createBingoCardUseCase(nextCard.wishes)
            } catch (e: Exception) {
                // The next card stays as it was, so the user can simply try again.
            } finally {
                isCreating.value = false
            }
        }
    }

    fun onCreatedCardShown() {
        createdCardId.value = null
    }
}

data class HomeUiState(
    val isLoaded: Boolean = false,
    val cards: List<BingoCard> = emptyList(),
    val nextCard: NextCardUiState = NextCardUiState(),
    /** Set once after a card is created, so the screen can move to it. */
    val createdCardId: BingoCardId? = null,
)

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
