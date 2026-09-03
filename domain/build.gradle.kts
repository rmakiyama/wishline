plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
    }
}
