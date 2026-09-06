package com.rmakiyama.wishline.di

import com.rmakiyama.wishline.data.SQLDelightItemRepository
import com.rmakiyama.wishline.data.SQLDelightOnboardingRepository
import com.rmakiyama.wishline.data.db.DatabaseDriverFactory
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.ItemRepository
import com.rmakiyama.wishline.domain.OnboardingRepository
import com.rmakiyama.wishline.usecase.AddItem
import com.rmakiyama.wishline.usecase.AddItemUseCase
import com.rmakiyama.wishline.usecase.CompleteOnboarding
import com.rmakiyama.wishline.usecase.CompleteOnboardingUseCase
import com.rmakiyama.wishline.usecase.GetItemsStream
import com.rmakiyama.wishline.usecase.GetItemsStreamUseCase
import com.rmakiyama.wishline.usecase.ObserveOnboardingCompleted
import com.rmakiyama.wishline.usecase.ObserveOnboardingCompletedUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Platform-agnostic providers shared by the Android and iOS dependency graphs.
 * Each platform graph supplies its own [DatabaseDriverFactory].
 */
interface SharedProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun provideWishlineDatabase(
        driverFactory: DatabaseDriverFactory,
    ): WishlineDatabase = WishlineDatabase(driverFactory.createDriver())

    @Provides
    @SingleIn(AppScope::class)
    fun provideItemRepository(
        impl: SQLDelightItemRepository,
    ): ItemRepository = impl

    @Provides
    @SingleIn(AppScope::class)
    fun provideOnboardingRepository(
        impl: SQLDelightOnboardingRepository,
    ): OnboardingRepository = impl

    @Provides
    fun provideGetItemsStreamUseCase(
        impl: GetItemsStream,
    ): GetItemsStreamUseCase = impl

    @Provides
    fun provideAddItemUseCase(
        impl: AddItem,
    ): AddItemUseCase = impl

    @Provides
    fun provideObserveOnboardingCompletedUseCase(
        impl: ObserveOnboardingCompleted,
    ): ObserveOnboardingCompletedUseCase = impl

    @Provides
    fun provideCompleteOnboardingUseCase(
        impl: CompleteOnboarding,
    ): CompleteOnboardingUseCase = impl
}
