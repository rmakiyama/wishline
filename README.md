# Wishline

A mobile app for Android and iOS built with Kotlin Multiplatform and Compose Multiplatform.

Generated from [app-skeleton-cmp](https://github.com/rmakiyama/app-skeleton-cmp).

## Requirements

- Java 21 (Temurin). The version is pinned in `mise.toml`; run `mise install` to set it up
- Android SDK. Point Gradle to it via `ANDROID_HOME` or `local.properties`:
  ```properties
  sdk.dir=/path/to/Android/sdk
  ```
- Xcode (iOS only)

## Build & Run

### Android

```bash
./gradlew assembleDebug
```

Or open the project in Android Studio and run the `app-android` configuration.

### iOS

Open `app-ios/app-ios.xcodeproj` in Xcode and run the `app-ios` scheme.
The build phase compiles the shared framework via Gradle, so Java 21 must be
resolvable from the environment Xcode runs in.

From the command line:

```bash
xcodebuild -project app-ios/app-ios.xcodeproj -scheme app-ios \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 17' build
```

### Tests

```bash
./gradlew testDebugUnitTest testAndroidHostTest
```

CI runs with `warningsAsErrors=true`. Reproduce locally with `-PwarningsAsErrors=true`.

## Architecture

Clean Architecture with a multi-module structure. The UI is shared across
Android and iOS with Compose Multiplatform: `shared` hosts the app-level
Compose entry points (`App`, NavGraph, DI) and exposes the iOS framework.

```mermaid
graph TD
    app-android --> shared
    app-ios --> shared

    shared --> feature:home
    shared --> core:ui
    shared --> core:navigation
    shared --> usecase
    shared --> domain
    shared --> data

    feature:home --> core:ui
    feature:home --> core:navigation
    feature:home --> usecase
    feature:home --> domain

    core:ui --> designsystem

    usecase --> domain
    data --> domain

    model

    style app-android fill:#4CAF50,color:#fff
    style shared fill:#607D8B,color:#fff
    style domain fill:#FF9800,color:#fff
    style data fill:#2196F3,color:#fff
    style usecase fill:#9C27B0,color:#fff
    style feature:home fill:#E91E63,color:#fff
    style designsystem fill:#00BCD4,color:#fff
```

See [AGENTS.md](AGENTS.md) for conventions and how to add a feature module.

## Tech Stacks

- UI (shared across Android / iOS)
    - Compose Multiplatform
    - Material 3
    - Navigation3
    - Edge-to-Edge
- Android
    - Jetpack Compose host (`app-android`)
- iOS
    - SwiftUI host with `ComposeUIViewController` (`app-ios`)
    - Shared framework (static)
- Kotlin Multiplatform
    - Metro (compile-time DI)
    - Coroutines
    - SQLDelight
    - Kermit
- Testing
    - Mokkery
    - Turbine
    - Kotest
- Development
    - Convention Plugins (`build-logic/`)
    - Gradle Version Catalog
