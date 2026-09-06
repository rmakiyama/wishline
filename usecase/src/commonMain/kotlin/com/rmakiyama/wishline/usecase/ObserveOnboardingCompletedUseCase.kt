package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.OnboardingRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface ObserveOnboardingCompletedUseCase {
    operator fun invoke(): Flow<Boolean>
}

@Inject
class ObserveOnboardingCompleted(
    private val onboardingRepository: OnboardingRepository,
) : ObserveOnboardingCompletedUseCase {
    override operator fun invoke(): Flow<Boolean> = onboardingRepository.isCompletedStream()
}
