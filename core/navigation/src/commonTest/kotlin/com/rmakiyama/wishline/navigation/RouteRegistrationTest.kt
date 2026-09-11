package com.rmakiyama.wishline.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.elementDescriptors
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * A Route that is not registered in [NavKeyConfiguration] makes back stack persistence fail at
 * runtime, and the failure is only logged. Enumerating the sealed hierarchy keeps that mistake
 * from reaching a device.
 */
@OptIn(ExperimentalSerializationApi::class)
class RouteRegistrationTest {

    @Test
    fun `given the sealed Route hierarchy, then its subtypes can be enumerated`() {
        assertTrue(routeSerialNames().isNotEmpty(), "No Route subtype was found — the enumeration broke")
    }

    @Test
    fun `given every Route subtype, then each one is registered in NavKeyConfiguration`() {
        routeSerialNames().forEach { serialName ->
            assertNotNull(
                NavKeyConfiguration.serializersModule.getPolymorphic(NavKey::class, serialName),
                "Route '$serialName' is missing from NavKeyConfiguration",
            )
        }
    }

    private fun routeSerialNames(): List<String> = Route.serializer().descriptor
        .getElementDescriptor(1)
        .elementDescriptors
        .map { it.serialName }
}
