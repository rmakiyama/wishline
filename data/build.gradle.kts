plugins {
    id("wishline.kotlin.multiplatform")
    id("wishline.kotlin.multiplatform.ios")
    id("wishline.metro")
    id("wishline.sqldelight")
}

sqldelight {
    databases {
        create("WishlineDatabase") {
            packageName.set("com.rmakiyama.wishline.data.db")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            implementation(libs.metro.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
    }
}
