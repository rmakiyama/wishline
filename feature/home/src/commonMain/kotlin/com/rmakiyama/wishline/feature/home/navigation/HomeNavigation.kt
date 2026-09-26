package com.rmakiyama.wishline.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.rmakiyama.wishline.feature.home.HomeScreen
import com.rmakiyama.wishline.feature.home.SelectionScreen
import com.rmakiyama.wishline.navigation.HomeRoute
import com.rmakiyama.wishline.navigation.SelectionRoute

fun EntryProviderScope<NavKey>.homeEntry(onSelectClick: () -> Unit) {
    entry<HomeRoute> {
        HomeScreen(onSelectClick = onSelectClick)
    }
}

fun EntryProviderScope<NavKey>.selectionEntry(onFinished: () -> Unit) {
    entry<SelectionRoute> {
        SelectionScreen(onFinished = onFinished)
    }
}
