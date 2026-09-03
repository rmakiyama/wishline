plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
    id("wishline.compose.multiplatform")
    id("wishline.metro")
    id("wishline.ios.framework")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kermit)

            implementation(projects.core.ui)
            implementation(projects.core.navigation)
            implementation(projects.feature.home)
            implementation(projects.usecase)
            implementation(projects.domain)
            implementation(projects.data)

            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.foundation)

            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.metro.runtime)
            implementation(libs.metro.runtime.compose)
        }
    }
}
