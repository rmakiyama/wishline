package com.rmakiyama.wishline.feature.archive.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.rmakiyama.wishline.feature.archive.ArchiveScreen
import com.rmakiyama.wishline.navigation.ArchiveRoute

fun EntryProviderScope<NavKey>.archiveEntry() {
    entry<ArchiveRoute> {
        ArchiveScreen()
    }
}
