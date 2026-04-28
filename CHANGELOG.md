# Changelog

## v23.0.5

### Bug Fixes
* Fixed Umbrella project import crash when root folder and child app share the same name (e.g., `emqx/` root with `apps/emqx/` child). The quick import path (`File` -> `Open` on `mix.exs`) bypassed the wizard's duplicate detection, causing `ModuleWithNameAlreadyExists`. Module names are now disambiguated using the relative path (e.g., `emqx` for the root, `emqx-apps-emqx` for the child). - [@joshuataylor](https://github.com/joshuataylor), (Thanks to [@JiaRG](https://github.com/JiaRG) for the thorough and reproducible bug report and excellent example umbrella project!)

## v23.0.4

### Enhancements
* 2026.1 compatibility: remove deprecated `OpenProjectTask.copy()` call in `DirectoryConfigurator`. - [@joshuataylor](https://github.com/joshuataylor)
* 2026.1 compatibility: use `ActionUtil.performAction()` instead of directly invoking `@OverrideOnly` method in `InstallMixDependenciesAction`. - [@joshuataylor](https://github.com/joshuataylor)
* Remove internal API usage: replace `DiagnosticBundle`, `AbstractMessage`, and `PlatformUtils` with public alternatives for plugin verification compatibility. - [@joshuataylor](https://github.com/joshuataylor)

### Build
* Bump Gradle from 9.3.1 to 9.4.1. - [@joshuataylor](https://github.com/joshuataylor)
* Bump kotlinx-coroutines from 1.9.0 to 1.10.2. - [@joshuataylor](https://github.com/joshuataylor)
* Bump gradle-download from 5.6.0 to 5.7.0. - [@joshuataylor](https://github.com/joshuataylor)
* Bump intellij-platform from 2.12.0 to 2.15.0. - [@joshuataylor](https://github.com/joshuataylor)
* Bump Kotlin to 2.3.21. - [@joshuataylor](https://github.com/joshuataylor)

## v23.0.3

### Bug Fixes
* [#3806](https://github.com/KronicDeth/intellij-elixir/pull/3806) - [@sh41](https://github.com/sh41)
  * Fix indexing deadlock by reading BEAM files from `FileContent` instead of reopening `VirtualFile` input stream. Avoids deadlock on WSL/IJ environments during annotation and highlighting passes.
* [#3807](https://github.com/KronicDeth/intellij-elixir/pull/3807) - [@sh41](https://github.com/sh41)
  * Remove stale library roots on dependency re-sync, fixing libraries XML files getting updated with redundant data endlessly. Fixes [#3804](https://github.com/KronicDeth/intellij-elixir/issues/3804).

## v23.0.2

### Enhancements
* [#3763](https://github.com/KronicDeth/intellij-elixir/pull/3763) - [@sh41](https://github.com/sh41)
  * JPS plugin refactor: fix JPS builder classpath (broken since September 2024), move `HomePath` into IDE module, persist Elixir SDK derived data (`mix-home`, `wsl-unc-path`) with a `data-version` marker and migration, and separate JPS builder from shared modules.
* [#3792](https://github.com/KronicDeth/intellij-elixir/pull/3792) - [@sh41](https://github.com/sh41)
  * Mix deps tooling: dedicated `mix deps` status parsing, deps checker service with debounced notifications and install action, SDK creation/registration flow with explicit Erlang dependency wiring, and Mise plugin integration for auto-install/configure of Elixir/Erlang SDKs.
  * Non-blocking write actions in UI and formatter to avoid background/EDT exceptions.
  * Hardened CLI argument construction across Mix/IEx/ESpec/ExUnit configurations.
  * WSL path canonicalization and env var handling improvements.
  * 2026.1 compatibility: ignore `ExperimentalPsiDummyBlock` type in `ElixirDocumentationProvider`.
* [#3791](https://github.com/KronicDeth/intellij-elixir/pull/3791) - [@soomtong](https://github.com/soomtong)
  * Update New Project Wizard to use the new `LanguageGeneratorNewProjectWizard` API with Elixir icon display.
* OTP 28 support: handle `AtU8` atom tables and stubless BEAM fallback. - [@sh41](https://github.com/sh41)
* Handle `@moduledoc`-style `ElixirMatchedAtOperation` in markdown injection. - [@sh41](https://github.com/sh41)
* Handle uncompressed literals and improve error reporting in BEAM `Literals` parsing. - [@sh41](https://github.com/sh41)

### Bug Fixes
* Fix broken IDE navigation by specifying base platform rather than module. - [@sh41](https://github.com/sh41)
* Fix process kill on Windows. - [@sh41](https://github.com/sh41)
* Fix compile server classpath for JPS module jars. - [@sh41](https://github.com/sh41)
* Fix WSL path handling: canonicalize WSL paths, convert only known env vars to WSL paths. - [@sh41](https://github.com/sh41)
* Ensure debugger path is added to code paths if unavailable. - [@sh41](https://github.com/sh41)
* Assert read access in all resolvers to avert deadlocks. - [@sh41](https://github.com/sh41)
* Less aggressive SDK version differentiation in `SdkHomeScan`. - [@sh41](https://github.com/sh41)

### Build
* Bump intellij-platform from 2.11.0 to 2.12.0. - [@joshuataylor](https://github.com/joshuataylor)

## v23.0.1

### Enhancements
* [#3787](https://github.com/KronicDeth/intellij-elixir/pull/3787) - [@joshuataylor](https://github.com/joshuataylor)
  * Remove internal API usage to improve plugin verification compatibility.
* [#3788](https://github.com/KronicDeth/intellij-elixir/pull/3788) - [@joshuataylor](https://github.com/joshuataylor)
  * Add separate GitHub Actions job for tagging/releasing, fixing release being overridden on every merge to main.

## v23.0.0

### Enhancements
* WSL Support - [@sh41](https://github.com/sh41) - see [release announcement](https://github.com/KronicDeth/intellij-elixir/releases/tag/v23.0.0) for full details!
  * Windows Subsystem for Linux support enabling Elixir development in WSL from Windows JetBrains IDEs. Originally submitted as [#3749](https://github.com/KronicDeth/intellij-elixir/pull/3749), broken into 7 stacked PRs for review. Resolves [#1384](https://github.com/KronicDeth/intellij-elixir/issues/1384), [#1911](https://github.com/KronicDeth/intellij-elixir/issues/1911), [#2499](https://github.com/KronicDeth/intellij-elixir/issues/2499), [#3470](https://github.com/KronicDeth/intellij-elixir/issues/3470), [#3674](https://github.com/KronicDeth/intellij-elixir/issues/3674), [#3746](https://github.com/KronicDeth/intellij-elixir/issues/3746). May also help with [#3659](https://github.com/KronicDeth/intellij-elixir/issues/3659), [#3716](https://github.com/KronicDeth/intellij-elixir/issues/3716), [#3715](https://github.com/KronicDeth/intellij-elixir/issues/3715). See the [README WSL section](https://github.com/KronicDeth/intellij-elixir?tab=readme-ov-file#windows-subsystem-for-linux-wsl-support) for setup instructions.
  * SDK discovery for Elixir and Erlang inside WSL distributions (asdf, mise, kerl, kiex, Homebrew, Nix).
  * WSL path conversion supporting both `\\wsl$\` and `\\wsl.localhost\` UNC path formats.
  * All run configurations work with WSL: Mix tasks, IEx, ExUnit, ESpec, Distillery, external tools.
  * WSL-safe process handling with graceful BEAM termination across the WSL boundary.
  * WSL-aware SDK naming in UI and status bar widget.
  * SDK settings improvements for small-IDE compatibility (RubyMine, WebStorm, etc.).
* [#3753](https://github.com/KronicDeth/intellij-elixir/pull/3753) - [@sh41](https://github.com/sh41)
  * Windows development infrastructure: platform-aware build services, Gradle tasks, CI caching (~4.5 GB fork-safe cache), and updated CONTRIBUTING.md.
* [#3757](https://github.com/KronicDeth/intellij-elixir/pull/3757) - [@sh41](https://github.com/sh41)
  * Mix dependency health checks on project open with install action and notifications via shared MixTaskRunner.
  * Faster and more robust Mix project import with pre-scan of OTP apps off EDT.
* [#3759](https://github.com/KronicDeth/intellij-elixir/pull/3759) - [@sh41](https://github.com/sh41)
  * IntelliJ IDE Starter-based UI test infrastructure for automated IDE testing.

### Bug Fixes
* [#3761](https://github.com/KronicDeth/intellij-elixir/pull/3761) - [@sh41](https://github.com/sh41)
  * Fix deadlock when `/deps` directory is deleted while the deps watcher is active.
  * Fix JPS Builder module name handling (broken since September 2024).
* [#3765](https://github.com/KronicDeth/intellij-elixir/pull/3765) - [@sh41](https://github.com/sh41)
  * Fix false inspection warnings in `~r` regex sigils containing interpolated variables.

## v22.0.0

### Breaking changes
* This release supports 2025.3+ IDEs only (253.xxx).

### Enhancements
* [#3696](https://github.com/KronicDeth/intellij-elixir/pull/3696) - [@mwnciau](https://github.com/mwnciau)
  * HEEx support with `.heex` file type recognition, syntax highlighting for `{@assigns}` and `{expressions}`, relative component support (`<.component>` tags), and CSS/JavaScript injection in `<style>` and `<script>` tags.
* Language Injection for literal sigils (`~H`, `~r`, etc.) - [@polymorfiq](https://github.com/polymorfiq)
* [#3711](https://github.com/KronicDeth/intellij-elixir/pull/3711) - [@joshuataylor](https://github.com/joshuataylor)
  * Status Bar Widget showing the current project's Elixir SDK version.
  * "Refresh Elixir SDK Classpaths" action to fix SDK classpath issues.
* Compatibility fixes for 2025.1+ (understatement of changes, countless amount of changes to help improve things, thank you so micj) - [@sh41](https://github.com/sh41)
* Extensive EDT threading fixes for 2025.2+ compatibility.

## v21.0.0
### Enhancements
* [#3651](https://github.com/KronicDeth/intellij-elixir/pull/3681) - [@joshuataylor](https://github.com/joshuataylor)
  * Support Jetbrains 2025.1 and relax until-build.

## v20.0.1

### Bug Fixes
* [#3651](https://github.com/KronicDeth/intellij-elixir/pull/3667) - [@joshuataylor](https://github.com/joshuataylor)
  * Support Webstorm 2024.3 (243.21565.180). Thanks to [@Kae-Tempest](https://github.com/Kae-Tempest) for the report!

## v20.0.0

### Enhancements
* [#3651](https://github.com/KronicDeth/intellij-elixir/pull/3651) - [@joshuataylor](https://github.com/joshuataylor)
  * Support Jetbrains 2024.3 (243.21565.193)

## v19.0.1

### Enhancements
* [#3639](https://github.com/KronicDeth/intellij-elixir/pull/3639) - [@joshuataylor](https://github.com/joshuataylor)
  * Support JetBrains Exception Analyzer

* [#3640](https://github.com/KronicDeth/intellij-elixir/pull/3640) - [@joshuataylor](https://github.com/joshuataylor)
  * Bump intellij platform gradle to 2.0.1 and IdeaVIM to 2.16.0

* [#3643](https://github.com/KronicDeth/intellij-elixir/pull/3643) - [@joshuataylor](https://github.com/joshuataylor)
  * Fix RubyMine freezing for umbrella projects but showing the new project wizard as a temporary workaround. 

## v19.0.0

### Breaking changes
* [#3619](https://github.com/KronicDeth/intellij-elixir/pull/3619) - [@joshuataylor](https://github.com/joshuataylor)
  * Drop support for < 2024.2 IDEs

### Enhancements
* [#3619](https://github.com/KronicDeth/intellij-elixir/pull/3619) - [@joshuataylor](https://github.com/joshuataylor)
  * Support IntelliJ Gradle Plugin 2.0

## v18.0.1
### Enhancements
* [#3582](https://github.com/KronicDeth/intellij-elixir/pull/3582) - [@rNoz](https://github.com/rNoz)
  * Enabling proper code generation for comments (Comment with Line/Block Comment)

## v18.0.0
### Breaking changes
* [#3569](https://github.com/KronicDeth/intellij-elixir/pull/3569) - [@ashleysommer](https://github.com/ashleysommer), [@joshuataylor](https://github.com/joshuataylor)
  * Drop support for < 2024.1 IDEs.

## v17.0.1

### Bug Fixes
* [#3491](https://github.com/KronicDeth/intellij-elixir/pull/3491) - [@neominik](https://github.com/neominik)
  * Render code snippets that are not links.
* [#3562](https://github.com/KronicDeth/intellij-elixir/pull/3562) - [@ashleysommer](https://github.com/ashleysommer)
  * Add the correct ERL and elixir arguments for starting IEx depending on the version of Elixir SDK.
* [#3563](https://github.com/KronicDeth/intellij-elixir/pull/3563) - [@ashleysommer](https://github.com/ashleysommer)
  * Bundle latest OtpErlang.jar from JInterface v1.14 for OTP v26.

## v17.0.0

### Breaking changes
* [#3500](https://github.com/KronicDeth/intellij-elixir/pull/3500) - [@KronicDeth](https://github.com/KronicDeth)
  * Drop support for <= 2023.2 IDEs.

### Enhancements
* [#2402](https://github.com/KronicDeth/intellij-elixir/pull/3402) - [@joshuataylor](https://github.com/joshuataylor)
  * Support 2023.3 IDEs.

### Bug Fixes
* [#3431](https://github.com/KronicDeth/intellij-elixir/pull/3431) - [@KronicDeth](https://github.com/KronicDeth)
  * Add `displayName` in `plugin.xml` for configurables for faster menu loading.
    * `org.elixir_lang.facet.configurable.Project` - "Elixir"
    * `org.elixir_lang.facets.sdks.erlang.Configurable` - "Internal Erlang SDKs"
    * `org.elixir_lang.facets.sdks.elixir.Configurable` - "SDKs"

## v16

The [CHANGELOG for v15](https://github.com/KronicDeth/intellij-elixir/blob/v15.1.0/CHANGELOG.md) can be found in [the v16.0.0 tag](https://github.com/KronicDeth/intellij-elixir/tree/v16.0.0).
