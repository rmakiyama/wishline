package com.rmakiyama.wishline.di

import com.rmakiyama.wishline.data.SQLDelightBingoCardRepository
import com.rmakiyama.wishline.data.SQLDelightOnboardingRepository
import com.rmakiyama.wishline.data.SQLDelightWishQueries
import com.rmakiyama.wishline.data.SQLDelightWishRepository
import com.rmakiyama.wishline.data.db.DatabaseDriverFactory
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.BingoCardRepository
import com.rmakiyama.wishline.domain.OnboardingRepository
import com.rmakiyama.wishline.domain.WishQueries
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.usecase.AddWish
import com.rmakiyama.wishline.usecase.AddWishUseCase
import com.rmakiyama.wishline.usecase.ChangeBingoCardLabel
import com.rmakiyama.wishline.usecase.ChangeBingoCardLabelUseCase
import com.rmakiyama.wishline.usecase.ChangeWishTitle
import com.rmakiyama.wishline.usecase.ChangeWishTitleUseCase
import com.rmakiyama.wishline.usecase.CloseBingoCard
import com.rmakiyama.wishline.usecase.CloseBingoCardUseCase
import com.rmakiyama.wishline.usecase.CompleteOnboarding
import com.rmakiyama.wishline.usecase.CompleteOnboardingUseCase
import com.rmakiyama.wishline.usecase.CreateBingoCard
import com.rmakiyama.wishline.usecase.CreateBingoCardUseCase
import com.rmakiyama.wishline.usecase.DeleteWish
import com.rmakiyama.wishline.usecase.DeleteWishUseCase
import com.rmakiyama.wishline.usecase.GetClosedBingoCardsStream
import com.rmakiyama.wishline.usecase.GetClosedBingoCardsStreamUseCase
import com.rmakiyama.wishline.usecase.GetOpenBingoCardsStream
import com.rmakiyama.wishline.usecase.GetOpenBingoCardsStreamUseCase
import com.rmakiyama.wishline.usecase.GetUnassignedWishesStream
import com.rmakiyama.wishline.usecase.GetUnassignedWishesStreamUseCase
import com.rmakiyama.wishline.usecase.GetWishesStream
import com.rmakiyama.wishline.usecase.GetWishesStreamUseCase
import com.rmakiyama.wishline.usecase.MarkWishDone
import com.rmakiyama.wishline.usecase.MarkWishDoneUseCase
import com.rmakiyama.wishline.usecase.MarkWishSomeday
import com.rmakiyama.wishline.usecase.MarkWishSomedayUseCase
import com.rmakiyama.wishline.usecase.ObserveOnboardingCompleted
import com.rmakiyama.wishline.usecase.ObserveOnboardingCompletedUseCase
import com.rmakiyama.wishline.usecase.RestoreWish
import com.rmakiyama.wishline.usecase.RestoreWishUseCase
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
    fun provideWishRepository(
        impl: SQLDelightWishRepository,
    ): WishRepository = impl

    @Provides
    @SingleIn(AppScope::class)
    fun provideWishQueries(
        impl: SQLDelightWishQueries,
    ): WishQueries = impl

    @Provides
    @SingleIn(AppScope::class)
    fun provideBingoCardRepository(
        impl: SQLDelightBingoCardRepository,
    ): BingoCardRepository = impl

    @Provides
    @SingleIn(AppScope::class)
    fun provideOnboardingRepository(
        impl: SQLDelightOnboardingRepository,
    ): OnboardingRepository = impl

    @Provides
    fun provideGetWishesStreamUseCase(
        impl: GetWishesStream,
    ): GetWishesStreamUseCase = impl

    @Provides
    fun provideGetUnassignedWishesStreamUseCase(
        impl: GetUnassignedWishesStream,
    ): GetUnassignedWishesStreamUseCase = impl

    @Provides
    fun provideGetOpenBingoCardsStreamUseCase(
        impl: GetOpenBingoCardsStream,
    ): GetOpenBingoCardsStreamUseCase = impl

    @Provides
    fun provideGetClosedBingoCardsStreamUseCase(
        impl: GetClosedBingoCardsStream,
    ): GetClosedBingoCardsStreamUseCase = impl

    @Provides
    fun provideAddWishUseCase(
        impl: AddWish,
    ): AddWishUseCase = impl

    @Provides
    fun provideChangeWishTitleUseCase(
        impl: ChangeWishTitle,
    ): ChangeWishTitleUseCase = impl

    @Provides
    fun provideMarkWishDoneUseCase(
        impl: MarkWishDone,
    ): MarkWishDoneUseCase = impl

    @Provides
    fun provideMarkWishSomedayUseCase(
        impl: MarkWishSomeday,
    ): MarkWishSomedayUseCase = impl

    @Provides
    fun provideRestoreWishUseCase(
        impl: RestoreWish,
    ): RestoreWishUseCase = impl

    @Provides
    fun provideDeleteWishUseCase(
        impl: DeleteWish,
    ): DeleteWishUseCase = impl

    @Provides
    fun provideCreateBingoCardUseCase(
        impl: CreateBingoCard,
    ): CreateBingoCardUseCase = impl

    @Provides
    fun provideCloseBingoCardUseCase(
        impl: CloseBingoCard,
    ): CloseBingoCardUseCase = impl

    @Provides
    fun provideChangeBingoCardLabelUseCase(
        impl: ChangeBingoCardLabel,
    ): ChangeBingoCardLabelUseCase = impl

    @Provides
    fun provideObserveOnboardingCompletedUseCase(
        impl: ObserveOnboardingCompleted,
    ): ObserveOnboardingCompletedUseCase = impl

    @Provides
    fun provideCompleteOnboardingUseCase(
        impl: CompleteOnboarding,
    ): CompleteOnboardingUseCase = impl
}
