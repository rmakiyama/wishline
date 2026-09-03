package com.rmakiyama.wishline

import android.app.Application
import com.rmakiyama.wishline.di.AppGraph
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class WishlineApplication : Application(), MetroApplication {
    override val appComponentProviders: MetroAppComponentProviders by lazy {
        createGraphFactory<AppGraph.Factory>().create(this)
    }
}
