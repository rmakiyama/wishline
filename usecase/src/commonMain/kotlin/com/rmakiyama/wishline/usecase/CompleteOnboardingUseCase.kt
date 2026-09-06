package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.OnboardingRepository
import dev.zacsweers.metro.Inject

interface CompleteOnboardingUseCase {
    suspend operator fun invoke()
}

@Inject
class CompleteOnboarding(
    private val onboardingRepository: OnboardingRepository,
) : CompleteOnboardingUseCase {
    override suspend operator fun invoke() = onboardingRepository.markCompleted()
}
