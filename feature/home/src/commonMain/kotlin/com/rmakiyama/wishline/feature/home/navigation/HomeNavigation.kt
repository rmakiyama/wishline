package com.rmakiyama.wishline.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.rmakiyama.wishline.feature.home.HomeScreen
import com.rmakiyama.wishline.navigation.HomeRoute

fun EntryProviderScope<NavKey>.homeEntry() {
    entry<HomeRoute> {
        HomeScreen()
    }
}
