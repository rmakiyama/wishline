# build-logic

Convention plugins, applied by id from module build files.

## Adding a third-party plugin

Three layers, in this order:

1. The root `build.gradle.kts` declares it with `apply false`. That puts it on the classpath
   without applying it anywhere
2. A convention plugin applies it: `pluginManager.apply("dev.mokkery")`
3. `compileOnly(...)` here is only for plugins whose Gradle types this code touches — AGP, Kotlin,
   Compose. Applying by id alone needs no entry

## Opt-in versus always on

`FeatureConventionPlugin` should apply only what every feature needs. Anything optional gets its
own convention plugin so a module can opt in — `wishline.mokkery` works this way.
