plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
    id("wishline.compose.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.foundation)
        }
    }
}
