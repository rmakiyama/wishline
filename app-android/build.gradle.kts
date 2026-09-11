plugins {
    id("wishline.android.application")
    id("wishline.metro")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

android {
    defaultConfig {
        applicationId = "com.rmakiyama.wishline"
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.metro.runtime)
    implementation(libs.metro.runtime.compose)
    implementation(libs.metro.android)

    implementation(projects.shared)
    implementation(projects.core.ui)
    implementation(projects.core.navigation)
    implementation(projects.feature.home)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.pool)
    implementation(projects.feature.archive)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.usecase)

    debugImplementation(libs.compose.ui.tooling)
}
