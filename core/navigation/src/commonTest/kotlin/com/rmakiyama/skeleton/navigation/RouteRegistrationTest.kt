package com.rmakiyama.skeleton.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.elementDescriptors
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RouteRegistrationTest {

    /**
     * A Route that is not registered in [NavKeyConfiguration] makes back stack persistence fail at
     * runtime, and the failure is only logged. Enumerating the sealed hierarchy keeps that mistake
     * from reaching a device.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun everyRouteIsRegisteredInNavKeyConfiguration() {
        val serialNames = Route.serializer().descriptor
            .getElementDescriptor(1)
            .elementDescriptors
            .map { it.serialName }

        assertTrue(serialNames.isNotEmpty(), "No Route subtype was found — the enumeration broke")

        serialNames.forEach { serialName ->
            assertNotNull(
                NavKeyConfiguration.serializersModule.getPolymorphic(NavKey::class, serialName),
                "Route '$serialName' is missing from NavKeyConfiguration",
            )
        }
    }
}
