package com.rmakiyama.wishline.di

import android.content.Context
import com.rmakiyama.wishline.data.db.DatabaseDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph, MetroAppComponentProviders, SharedProviders {

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): AppGraph
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabaseDriverFactory(
        context: Context,
    ): DatabaseDriverFactory = DatabaseDriverFactory(context)
}
