package com.rmakiyama.wishline.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.UnassignedWish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.usecase.CreateBingoCardUseCase
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
class SelectionViewModel(
    private val getUnassignedWishesStream: GetUnassignedWishesStreamUseCase,
    private val createBingoCardUseCase: CreateBingoCardUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectionUiState())
    val uiState: StateFlow<SelectionUiState> = _uiState.asStateFlow()

    init {
        observeUnassignedWishes()
    }

    private fun observeUnassignedWishes() {
        viewModelScope.launch {
            getUnassignedWishesStream().collect { wishes ->
                _uiState.update { it.copy(wishes = wishes, isLoaded = true) }
            }
        }
    }

    fun onToggle(id: WishId) {
        _uiState.update { state ->
            val selected = if (id in state.selectedIds) state.selectedIds - id else state.selectedIds + id
            state.copy(selectedIds = selected)
        }
    }

    fun onToggleAll() {
        _uiState.update { state ->
            val selected = if (state.isAllSelected) emptySet() else state.wishes.mapTo(mutableSetOf()) { it.wish.id }
            state.copy(selectedIds = selected)
        }
    }

    fun onCreate() {
        val state = _uiState.value
        if (!state.canCreate) return
        _uiState.update { it.copy(isCreating = true) }
        viewModelScope.launch {
            try {
                createBingoCardUseCase(state.selectedWishes.map { it.wish })
                _uiState.update { it.copy(isCreated = true) }
            } catch (e: Exception) {
                // The selection stays as it was, so the user can simply try again.
            } finally {
                _uiState.update { it.copy(isCreating = false) }
            }
        }
    }
}

data class SelectionUiState(
    val wishes: List<UnassignedWish> = emptyList(),
    /** Can hold a wish that has since left the list; [selectedWishes] is what counts. */
    val selectedIds: Set<WishId> = emptySet(),
    val isLoaded: Boolean = false,
    val isCreating: Boolean = false,
    /** Set once the card exists, so the screen can leave. */
    val isCreated: Boolean = false,
) {
    val selectedWishes: List<UnassignedWish> get() = wishes.filter { it.wish.id in selectedIds }

    val selectedCount: Int get() = selectedWishes.size

    val isAllSelected: Boolean get() = wishes.isNotEmpty() && selectedCount == wishes.size

    val canCreate: Boolean get() = selectedCount == BingoCard.SLOT_COUNT && !isCreating && !isCreated
}
