package com.rmakiyama.skeleton.di

import com.rmakiyama.skeleton.data.SQLDelightItemRepository
import com.rmakiyama.skeleton.data.db.DatabaseDriverFactory
import com.rmakiyama.skeleton.data.db.SkeletonDatabase
import com.rmakiyama.skeleton.domain.ItemRepository
import com.rmakiyama.skeleton.usecase.GetItemsStream
import com.rmakiyama.skeleton.usecase.GetItemsStreamUseCase
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
    fun provideSkeletonDatabase(
        driverFactory: DatabaseDriverFactory,
    ): SkeletonDatabase = SkeletonDatabase(driverFactory.createDriver())

    @Provides
    @SingleIn(AppScope::class)
    fun provideItemRepository(
        impl: SQLDelightItemRepository,
    ): ItemRepository = impl

    @Provides
    fun provideGetItemsStreamUseCase(
        impl: GetItemsStream,
    ): GetItemsStreamUseCase = impl
}
