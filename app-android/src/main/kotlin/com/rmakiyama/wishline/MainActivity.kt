package com.rmakiyama.wishline

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey
@Inject
class MainActivity(private val metroVmf: MetroViewModelFactory) : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels { metroVmf }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Compose resolves the same instance from the activity's store, so this holds the splash
        // until the start route is known instead of drawing an empty first frame.
        splashScreen.setKeepOnScreenCondition { appViewModel.startRoute.value == null }

        setContent {
            CompositionLocalProvider(
                LocalMetroViewModelFactory provides metroVmf,
            ) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
