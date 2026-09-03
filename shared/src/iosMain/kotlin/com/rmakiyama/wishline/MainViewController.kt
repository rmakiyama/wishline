package com.rmakiyama.wishline

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.rmakiyama.wishline.di.IosAppGraph
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import platform.UIKit.UIViewController

private val appGraph: IosAppGraph by lazy { createGraph<IosAppGraph>() }

@Suppress("unused", "FunctionName") // Called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController {
    CompositionLocalProvider(
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
    ) {
        App()
    }
}
