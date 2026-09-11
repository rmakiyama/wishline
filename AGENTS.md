# AGENTS.md

Kotlin Multiplatform mobile app with Compose Multiplatform UI (Android / iOS).

Directory-level notes:

- `domain/` — where invariants live, how states are modelled
- `data/` — state and history, storage constraints
- `build-logic/` — how convention plugins resolve

## Build

```bash
./gradlew assembleDebug                                # Android build
./gradlew testDebugUnitTest testAndroidHostTest        # Run tests (KMP modules use testAndroidHostTest)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64  # iOS framework build
```

iOS app: build `app-ios/app-ios.xcodeproj` with Xcode (the build phase compiles the Shared framework via Gradle).

- Java 21 (Temurin). Version managed via `mise.toml`
- CI enforces `warningsAsErrors=true` — compiler warnings are build failures. Reproduce locally with `-PwarningsAsErrors=true`
- For `commonMain`, an Android build is not enough: only the iOS build catches an import the JVM resolves implicitly. CI does not link iOS, so run it before pushing

## Ubiquitous language

- `Wish` — やりたいこと
- `BingoCard` — カード
- `BingoSlot` — マス
- **close** a card — クローズ. Never 締める: in Japanese it also reads as giving up

## Comments

- Write only what the reader cannot see: the reason behind a decision, or an invariant kept somewhere else
- Do not restate what the code already shows
- Do not name anything belonging to another layer

```kotlin
// Bad — a domain type naming database tables
/** Every status carries when the wish entered it, so `wish` and `wish_status_change` agree. */

// Good — the part of the design the code cannot show
/** [Planned] carries a time too: a wish returns to it when it is taken back from done or someday. */
```

## Conventions

- UI code lives in `commonMain` (Compose Multiplatform). Use `expect`/`actual` only for platform-specific pieces
- Namespace is auto-derived from the module path (`NamespaceUtils.kt`). Never set it manually
- Test dependencies (kotlin-test, coroutines-test, turbine, kotest) are added automatically by Convention Plugins. Do not declare them in modules
- mokkery is opt-in. Add `id("wishline.mokkery")` to a module that mocks. Usage notes: `.claude/skills/mokkery/SKILL.md`
- Test names read `given ..., when ..., then ...`. A name that needs "and" usually covers two things — consider splitting the test
- When adding new code, follow the patterns in existing modules of the same kind

## Adding a Feature Module

1. Create `feature/<name>/`. `build.gradle.kts` only needs `id("wishline.feature")`
2. Add `include(":feature:<name>")` to `settings.gradle.kts`
3. Add a Route to `:core:navigation` and register it in `NavKeyConfiguration` so the back stack can be restored
4. Expose a navigation entry extension function from the feature module
5. Register the entry in the NavGraph in `:shared`
