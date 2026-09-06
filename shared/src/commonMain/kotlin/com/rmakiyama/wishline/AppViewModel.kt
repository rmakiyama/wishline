package com.rmakiyama.wishline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmakiyama.wishline.navigation.HomeRoute
import com.rmakiyama.wishline.navigation.OnboardingRoute
import com.rmakiyama.wishline.navigation.Route
import com.rmakiyama.wishline.usecase.ObserveOnboardingCompletedUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take

/**
 * Decides which screen the app opens on. The decision is taken once per process — later changes
 * to the onboarding flag are handled by navigation, not by re-rooting the back stack.
 */
@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class AppViewModel(
    observeOnboardingCompleted: ObserveOnboardingCompletedUseCase,
) : ViewModel() {
    /** `null` while the flag is still being read. */
    val startRoute: StateFlow<Route?> = observeOnboardingCompleted()
        .take(1)
        .map { completed -> if (completed) HomeRoute else OnboardingRoute }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
