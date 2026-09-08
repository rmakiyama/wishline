import org.gradle.api.Plugin
import org.gradle.api.Project

class MokkeryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("dev.mokkery")
        }
    }
}
