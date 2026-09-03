plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
    id("wishline.compose.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.designsystem)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
        }
    }
}
