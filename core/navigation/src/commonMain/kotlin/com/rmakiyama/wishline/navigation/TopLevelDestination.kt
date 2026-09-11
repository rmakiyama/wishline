package com.rmakiyama.wishline.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/** The screens reachable from the bottom bar, in display order. */
enum class TopLevelDestination(val route: Route) {
    Home(HomeRoute),
    Pool(PoolRoute),
    Archive(ArchiveRoute),
    ;

    companion object {
        fun of(key: NavKey?): TopLevelDestination? = entries.firstOrNull { it.route == key }
    }
}

/**
 * HOME stays at the root and the other tabs sit on top of it one at a time, so back from any tab
 * returns to HOME rather than leaving the app. Only entries above HOME are removed, which keeps
 * HOME's state alive across tab switches; the other tabs start over each time they are shown.
 */
fun NavBackStack<NavKey>.switchTab(destination: TopLevelDestination) {
    if (lastOrNull() == destination.route) return
    while (size > 1) removeLastOrNull()
    if (destination.route != HomeRoute) add(destination.route)
}
