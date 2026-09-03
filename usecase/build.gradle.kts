plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
    id("wishline.metro")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            implementation(libs.metro.runtime)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
