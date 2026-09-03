package com.rmakiyama.skeleton.di

import com.rmakiyama.skeleton.data.db.DatabaseDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface IosAppGraph : ViewModelGraph, SharedProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()
}
