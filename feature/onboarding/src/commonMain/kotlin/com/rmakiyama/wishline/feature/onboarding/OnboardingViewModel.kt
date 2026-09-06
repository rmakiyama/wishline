package com.rmakiyama.wishline.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmakiyama.wishline.usecase.AddItemUseCase
import com.rmakiyama.wishline.usecase.CompleteOnboardingUseCase
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
class OnboardingViewModel(
    private val addItemUseCase: AddItemUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    /** Appends the current input as a pending item. Blank input is ignored. */
    fun onAddItem() {
        val title = _uiState.value.input.trim()
        if (title.isEmpty()) return
        _uiState.update { it.copy(items = it.items + title, input = "") }
    }

    fun onRemoveItem(index: Int) {
        _uiState.update { state ->
            state.copy(items = state.items.filterIndexed { i, _ -> i != index })
        }
    }

    /**
     * Saves the pending items, then completes onboarding. Used by both "はじめる" and "あとで":
     * whatever the user typed is kept either way. If saving fails, the screen becomes usable
     * again so the user can retry.
     */
    fun onFinish() {
        val state = _uiState.value
        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                if (state.items.isNotEmpty()) addItemUseCase(state.items)
                completeOnboardingUseCase()
                _uiState.update { it.copy(isCompleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}

data class OnboardingUiState(
    val items: List<String> = emptyList(),
    val input: String = "",
    val isSubmitting: Boolean = false,
    val isCompleted: Boolean = false,
) {
    val canStart: Boolean get() = items.isNotEmpty() && !isSubmitting
}
