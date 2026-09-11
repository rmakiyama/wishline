import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.rmakiyama.wishline.getDefaultNamespace
import com.rmakiyama.wishline.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("wishline.kotlin.multiplatform")
            pluginManager.apply("wishline.kotlin.multiplatform.ios")
            pluginManager.apply("wishline.compose.multiplatform")
            pluginManager.apply("wishline.metro")

            extensions.configure<ComposeExtension> {
                extensions.configure<ResourcesExtension> {
                    packageOfResClass = getDefaultNamespace(project)
                }
            }

            extensions.configure<KotlinMultiplatformExtension> {
                targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
                    androidResources { enable = true }
                }

                sourceSets.apply {
                    commonMain.dependencies {
                        implementation(project(":core:ui"))
                        implementation(project(":core:navigation"))
                        implementation(project(":usecase"))
                        implementation(project(":domain"))

                        implementation(libs.findLibrary("compose-ui").get())
                        implementation(libs.findLibrary("compose-material3").get())
                        implementation(libs.findLibrary("compose-foundation").get())
                        implementation(libs.findLibrary("compose-components-resources").get())

                        implementation(libs.findLibrary("androidx-navigation3-runtime").get())
                        implementation(libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
                        implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())

                        implementation(libs.findLibrary("metro-runtime").get())
                        implementation(libs.findLibrary("metro-runtime-compose").get())
                    }
                }
            }
        }
    }
}
