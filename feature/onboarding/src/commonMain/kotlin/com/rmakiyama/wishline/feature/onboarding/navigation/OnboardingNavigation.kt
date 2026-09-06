package com.rmakiyama.wishline.feature.onboarding.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.rmakiyama.wishline.feature.onboarding.OnboardingScreen
import com.rmakiyama.wishline.navigation.OnboardingRoute

fun EntryProviderScope<NavKey>.onboardingEntry(onFinished: () -> Unit) {
    entry<OnboardingRoute> {
        OnboardingScreen(onFinished = onFinished)
    }
}
