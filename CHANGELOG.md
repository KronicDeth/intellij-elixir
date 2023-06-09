# Changelog

## v15.0.2

### Bug Fixes
* [#3185](https://github.com/KronicDeth/intellij-elixir/pull/3213) - [@KronicDeth](https://github.com/KronicDeth)
  * Replace uses of `Cell.horizontalAlign(HorizontalAlign)`
    The API is scheduled for removal and is replaced by `Call.align(AlignX.FILL)`.
* [#3214](https://github.com/KronicDeth/intellij-elixir/pull/3214) - [@KronicDeth](https://github.com/KronicDeth)
  * Ignore `group:` for docs.
* [#3242](https://github.com/KronicDeth/intellij-elixir/pull/3242) - [@KronicDeth](https://github.com/KronicDeth)
  * Don't resolve built-in types against the index if index is updating.
* [#3249](https://github.com/KronicDeth/intellij-elixir/pull/3249) - [@KronicDeth](https://github.com/KronicDeth)
  * `findModuleForPsiElement` in `mostSpecificSdk` in read action.
* [#3250](https://github.com/KronicDeth/intellij-elixir/pull/3250) - [@KronicDeth](https://github.com/KronicDeth)
  * Skip finding `mix.exs` for OTP apps if it can't be read.
* [#3251](https://github.com/KronicDeth/intellij-elixir/pull/3251) - [@KronicDeth](https://github.com/KronicDeth)
  * Include `mix new` stderr in `IOException` for better triage.
* [#3251](https://github.com/KronicDeth/intellij-elixir/pull/3251) - [@KronicDeth](https://github.com/KronicDeth)
  * Include `mix new` stderr in `IOException` for better triage.
* [#3252](https://github.com/KronicDeth/intellij-elixir/pull/3252) - [@KronicDeth](https://github.com/KronicDeth)
  * Highlight binary numbers as usual in types.
* [#3253](https://github.com/KronicDeth/intellij-elixir/pull/3253) - [@KronicDeth](https://github.com/KronicDeth)
  * Highlight module attributes as usual in types.
* [#3254](https://github.com/KronicDeth/intellij-elixir/pull/3254) - [@KronicDeth](https://github.com/KronicDeth)
  * Use `org.apache.commons.lang.SystemUtils` instead of `org.codehaus.plexus.interpolation.os.Os` to detect if on Windows for Test marker file URL.
* [#3260](https://github.com/KronicDeth/intellij-elixir/pull/3260) - [@KronicDeth](https://github.com/KronicDeth)
  * Call `FileIndex.getContentRootForFile` in `ReadAction` when getting working directory for `mix format`.
* [#3261](https://github.com/KronicDeth/intellij-elixir/pull/3261) - [@KronicDeth](https://github.com/KronicDeth)
  * Don't include `null` target usage types when finding usage type across all targets.

## v15.0.1

### Bug Fixes
* [#3183](https://github.com/KronicDeth/intellij-elixir/pull/3183) - [@vanderson139](https://github.com/vanderson139)
  * Support 2023.1 RubyMine and WebStorm.
    RubyMine and WebStorm have a `FIX` version of `174`, which is less than IntelliJ's `175` in IntelliJ 2023.1's builder number, `231.8109.175`.

## v15.0.0

### Incompatible Changes
* [#3146](https://github.com/KronicDeth/intellij-elixir/pull/3146) - [@ViseLuca](https://github.com/ViseLuca)
  * Drop support for <= 2022 IDEs.

### Enhancements
* [#3146](https://github.com/KronicDeth/intellij-elixir/pull/3146) - [@ViseLuca](https://github.com/ViseLuca)
  * Support 2023.1 IDEs.

### Bug Fixes
* [#3172](https://github.com/KronicDeth/intellij-elixir/pull/3172) - [@KronicDeth](https://github.com/KronicDeth)
  * Ignore from preload list that doesn't have square brackets.
    When trying to resolve keyword keys to `from`, don't error on unknown keys if any previous key was preload as this may be a list of preloads that is missing the square brackets.
* [#3176](https://github.com/KronicDeth/intellij-elixir/pull/3176) - [@sh41](https://github.com/sh41)
  * Re-enable canary releases.
* [#3180](https://github.com/KronicDeth/intellij-elixir/pull/3180) - [@KronicDeth](https://github.com/KronicDeth)
  * Remove duplicate dependency on `com.intellij.modules.java` plugin.

## v14

The [CHANGELOG for v14](https://github.com/KronicDeth/intellij-elixir/blob/v14.0.1/CHANGELOG.md) can be found in [the v14.01 tag](https://github.com/KronicDeth/intellij-elixir/tree/v14.0.1).
