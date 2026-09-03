import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.rmakiyama.wishline.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.sqldelight.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "wishline.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
register("kotlinMultiplatform") {
            id = "wishline.kotlin.multiplatform"
            implementationClass = "KotlinMultiplatformConventionPlugin"
        }
        register("kotlinMultiplatformIos") {
            id = "wishline.kotlin.multiplatform.ios"
            implementationClass = "KotlinMultiplatformIosConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "wishline.compose.multiplatform"
            implementationClass = "ComposeMultiplatformConventionPlugin"
        }
        register("metro") {
            id = "wishline.metro"
            implementationClass = "MetroConventionPlugin"
        }
        register("iosFramework") {
            id = "wishline.ios.framework"
            implementationClass = "IosFrameworkConventionPlugin"
        }
        register("feature") {
            id = "wishline.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        register("sqldelight") {
            id = "wishline.sqldelight"
            implementationClass = "SqlDelightConventionPlugin"
        }
    }
}
