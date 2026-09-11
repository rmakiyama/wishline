package com.rmakiyama.wishline.feature.pool.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.rmakiyama.wishline.feature.pool.PoolScreen
import com.rmakiyama.wishline.navigation.PoolRoute

fun EntryProviderScope<NavKey>.poolEntry() {
    entry<PoolRoute> {
        PoolScreen()
    }
}
