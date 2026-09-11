package com.rmakiyama.wishline.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals

class SwitchTabTest {

    @Test
    fun `given HOME, when Pool is selected, then Pool sits on top of HOME`() {
        val backStack = NavBackStack<NavKey>(HomeRoute)

        backStack.switchTab(TopLevelDestination.Pool)

        assertEquals(listOf(HomeRoute, PoolRoute), backStack.toList())
    }

    @Test
    fun `given Pool on top, when Archive is selected, then Pool is replaced`() {
        val backStack = NavBackStack<NavKey>(HomeRoute, PoolRoute)

        backStack.switchTab(TopLevelDestination.Archive)

        assertEquals(listOf(HomeRoute, ArchiveRoute), backStack.toList())
    }

    @Test
    fun `given Pool on top, when HOME is selected, then only HOME remains`() {
        val backStack = NavBackStack<NavKey>(HomeRoute, PoolRoute)

        backStack.switchTab(TopLevelDestination.Home)

        assertEquals(listOf(HomeRoute), backStack.toList())
    }

    @Test
    fun `given Pool on top, when Pool is selected again, then nothing changes`() {
        val backStack = NavBackStack<NavKey>(HomeRoute, PoolRoute)

        backStack.switchTab(TopLevelDestination.Pool)

        assertEquals(listOf(HomeRoute, PoolRoute), backStack.toList())
    }
}
