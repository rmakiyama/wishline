package com.rmakiyama.skeleton.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Base type for every destination. Being sealed keeps all Routes in this module, and `@Serializable`
 * makes its descriptor list every subtype at compile time — that is what lets `RouteRegistrationTest`
 * enumerate them and fail the build on a missing registration below.
 *
 * Do not implement [NavKey] directly: such a Route compiles but escapes that check. This serializer
 * is only used for enumeration in tests — registration still happens per subtype.
 */
@Serializable
sealed interface Route : NavKey

@Serializable
data object HomeRoute : Route

/**
 * Every Route must be registered here, otherwise saving the back stack fails at runtime with
 * `SerializationException: Serializer for subclass '<Route>' is not found`. The failure is logged
 * rather than thrown, so the back stack silently stops persisting.
 *
 * The registration cannot be derived automatically: kotlinx.serialization resolves polymorphic
 * subtypes from the runtime type, and only does so without registration when the base type is a
 * sealed `@Serializable` one — which [NavKey] is not. The reflection-based alternative that skips
 * registration is Android-only and unusable in commonMain.
 */
val NavKeyConfiguration: SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeRoute::class, HomeRoute.serializer())
        }
    }
}
