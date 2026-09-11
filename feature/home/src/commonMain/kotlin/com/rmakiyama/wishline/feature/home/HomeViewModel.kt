package com.rmakiyama.wishline.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.usecase.GetWishesStreamUseCase
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
    private val getWishesStreamUseCase: GetWishesStreamUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeWishes()
    }

    private fun observeWishes() {
        viewModelScope.launch {
            getWishesStreamUseCase()
                .collect { wishes ->
                    _uiState.update { it.copy(wishes = wishes) }
                }
        }
    }
}

data class HomeUiState(
    val wishes: List<Wish> = emptyList(),
)
