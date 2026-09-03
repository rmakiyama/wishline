plugins {
    id("skeleton.kotlin.multiplatform")
    id("skeleton.kotlin.multiplatform.ios")
    id("skeleton.compose.multiplatform")
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
