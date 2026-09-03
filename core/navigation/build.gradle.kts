plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // NavKey is a supertype of every Route, so it belongs on the consumer's compile classpath.
            // SavedStateConfiguration comes with it — it is part of navigation3's own API surface.
            api(libs.androidx.navigation3.runtime)
            implementation(libs.kotlinx.serialization.core)
        }
    }
}
