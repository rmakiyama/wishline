# build-logic

Convention Plugin。モジュールの build ファイルから id で適用する。

## サードパーティのプラグインを足す

3層をこの順で。

1. ルートの `build.gradle.kts` が `apply false` で宣言する。どこにも適用せずクラスパスに載せるだけの状態になる
2. Convention Plugin が適用する: `pluginManager.apply("dev.mokkery")`
3. ここの `compileOnly(...)` は、このコードが Gradle の型として触るプラグインだけ（AGP、Kotlin、Compose）。id で適用するだけなら書かなくてよい

## opt-in と常時適用

`FeatureConventionPlugin` が適用するのは、すべての feature が必要とするものだけにする。任意のものは専用の Convention Plugin にしてモジュール側から opt-in させる。`wishline.mokkery` がこの形。
