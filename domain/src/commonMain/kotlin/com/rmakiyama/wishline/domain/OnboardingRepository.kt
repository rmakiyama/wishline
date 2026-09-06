package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    /** Emits `true` once the user has finished (or skipped) onboarding. */
    fun isCompletedStream(): Flow<Boolean>
    suspend fun markCompleted()
}
