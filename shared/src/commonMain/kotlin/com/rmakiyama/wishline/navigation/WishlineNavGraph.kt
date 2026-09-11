package com.rmakiyama.wishline.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.rmakiyama.wishline.AppViewModel
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.feature.archive.navigation.archiveEntry
import com.rmakiyama.wishline.feature.home.navigation.homeEntry
import com.rmakiyama.wishline.feature.onboarding.navigation.onboardingEntry
import com.rmakiyama.wishline.feature.pool.navigation.poolEntry
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun WishlineNavGraph(
    appViewModel: AppViewModel = metroViewModel(),
) {
    val startRoute by appViewModel.startRoute.collectAsStateWithLifecycle()

    // Paint the surface color until the onboarding flag has been read from the database.
    val start = startRoute
    if (start == null) {
        Box(Modifier.fillMaxSize().background(WlTheme.colorScheme.surface))
        return
    }
    WishlineNavGraph(startRoute = start)
}

@Composable
private fun WishlineNavGraph(startRoute: Route) {
    val backStack = rememberNavBackStack(NavKeyConfiguration, startRoute)
    val currentTab = TopLevelDestination.of(backStack.lastOrNull() as? Route)

    Scaffold(
        bottomBar = {
            if (currentTab != null) {
                WishlineBottomBar(
                    current = currentTab,
                    onSelect = { backStack.switchTab(it) },
                )
            }
        },
    ) { paddingValues ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                onboardingEntry(
                    onFinished = {
                        // Onboarding is shown once; HOME replaces it so back does not return here.
                        backStack.clear()
                        backStack.add(HomeRoute)
                    },
                )
                homeEntry()
                poolEntry()
                archiveEntry()
            },
        )
    }
}

/**
 * HOME stays at the root and the other tabs sit on top of it one at a time, so back from any tab
 * returns to HOME rather than leaving the app. Only entries above HOME are removed, which keeps
 * HOME's state alive across tab switches.
 */
private fun NavBackStack<NavKey>.switchTab(destination: TopLevelDestination) {
    if (lastOrNull() == destination.route) return
    while (size > 1) removeLastOrNull()
    if (destination.route != HomeRoute) add(destination.route)
}
