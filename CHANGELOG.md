# Changelog

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
