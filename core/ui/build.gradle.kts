plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
    id("wishline.compose.multiplatform")
}

compose.resources {
    packageOfResClass = "com.rmakiyama.wishline.core.ui"
}

kotlin {
    androidLibrary {
        androidResources { enable = true }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.designsystem)
            implementation(projects.core.navigation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
        }
    }
}
