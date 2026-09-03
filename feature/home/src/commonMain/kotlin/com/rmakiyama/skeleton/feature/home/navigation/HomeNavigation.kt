package com.rmakiyama.skeleton.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.rmakiyama.skeleton.feature.home.HomeScreen
import com.rmakiyama.skeleton.navigation.HomeRoute

fun EntryProviderScope<NavKey>.homeEntry() {
    entry<HomeRoute> {
        HomeScreen()
    }
}
