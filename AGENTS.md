# AGENTS.md

Kotlin Multiplatform mobile app with Compose Multiplatform UI (Android / iOS).

## Build

```bash
./gradlew assembleDebug                                # Android build
./gradlew testDebugUnitTest testAndroidHostTest        # Run tests (KMP modules use testAndroidHostTest)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64  # iOS framework build
```

iOS app: build `app-ios/app-ios.xcodeproj` with Xcode (the build phase compiles the Shared framework via Gradle).

- Java 21 (Temurin). Version managed via `mise.toml`
- CI enforces `warningsAsErrors=true` — compiler warnings are build failures. Reproduce locally with `-PwarningsAsErrors=true`

## Conventions

- UI code lives in `commonMain` (Compose Multiplatform). Use `expect`/`actual` only for platform-specific pieces
- Namespace is auto-derived from the module path (`NamespaceUtils.kt`). Never set it manually
- Test dependencies (kotlin-test, coroutines-test, turbine, kotest) are added automatically by Convention Plugins. Do not declare them in modules
- When adding new code, follow the patterns in existing modules of the same kind

## Adding a Feature Module

1. Create `feature/<name>/`. `build.gradle.kts` only needs `id("wishline.feature")`
2. Add `include(":feature:<name>")` to `settings.gradle.kts`
3. Add a Route to `:core:navigation` and register it in `NavKeyConfiguration` so the back stack can be restored
4. Expose a navigation entry extension function from the feature module
5. Register the entry in the NavGraph in `:shared`
