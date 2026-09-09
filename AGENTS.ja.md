# AGENTS.md

Compose Multiplatform で UI を書く Kotlin Multiplatform のモバイルアプリ（Android / iOS）。

ディレクトリごとのメモ:

- `domain/` — 不変条件の置き場、状態の表し方
- `data/` — 状態と履歴、ストレージの制約
- `build-logic/` — Convention Plugin の解決のされ方

## ビルド

```bash
./gradlew assembleDebug                                # Android ビルド
./gradlew testDebugUnitTest testAndroidHostTest        # テスト実行（KMP モジュールは testAndroidHostTest）
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64  # iOS フレームワークのビルド
```

iOS アプリ: `app-ios/app-ios.xcodeproj` を Xcode でビルドする（ビルドフェーズが Gradle で Shared フレームワークをコンパイルする）。

- Java 21（Temurin）。バージョンは `mise.toml` で管理
- CI は `warningsAsErrors=true` を強制する。警告はビルド失敗になる。手元では `-PwarningsAsErrors=true` で再現する
- `commonMain` は Android のビルドだけでは不十分。JVM が暗黙に解決する import は iOS ビルドでしか検出されない。CI は iOS をリンクしないので、push 前に自分で回す

## ユビキタス言語

- `Wish` — やりたいこと
- `BingoCard` — カード
- `BingoSlot` — マス
- カードを **close** する — クローズ。「締める」は使わない。「あきらめる」とも読めるため

## コメント

- 読者から見えないものだけを書く。判断の理由か、別の場所で保たれている不変条件
- コードが既に示していることを言い換えない
- 別の層に属するものの名前を出さない

```kotlin
// 悪い例 — ドメインの型が DB のテーブル名を出している
/** Every status carries when the wish entered it, so `wish` and `wish_status_change` agree. */

// 良い例 — コードでは示せない設計の部分
/** [Planned] carries a time too: a wish returns to it when it is taken back from done or someday. */
```

## 規約

- UI のコードは `commonMain` に置く（Compose Multiplatform）。`expect`/`actual` はプラットフォーム固有の部分だけに使う
- namespace はモジュールのパスから自動で決まる（`NamespaceUtils.kt`）。手で設定しない
- テスト依存（kotlin-test、coroutines-test、turbine、kotest）は Convention Plugin が自動で入れる。モジュールで宣言しない
- mokkery は opt-in。モックを使うモジュールに `id("wishline.mokkery")` を足す。使い方は `.claude/skills/mokkery/SKILL.md`
- テスト名は `given ..., when ..., then ...` で書く。and が必要になる名前はたいてい2つのことを見ているので、分割を検討する
- 新しいコードを足すときは、同じ種類の既存モジュールのパターンに合わせる

## Feature モジュールの追加

1. `feature/<name>/` を作る。`build.gradle.kts` は `id("wishline.feature")` だけでよい
2. `settings.gradle.kts` に `include(":feature:<name>")` を足す
3. `:core:navigation` に Route を足し、バックスタックを復元できるよう `NavKeyConfiguration` に登録する
4. feature モジュールからナビゲーションのエントリ拡張関数を公開する
5. `:shared` の NavGraph にエントリを登録する
