# v23.0.0

v23.0.0 brings a commonly requested feature for those using Windows -- WSL support (!!!).

Windows users can now develop Elixir projects hosted inside WSL directly from their JetBrains IDE. This closes issues dating back to 2018!

Also, "funnily" enough, 2025.3 broke quite a few features with WSL when @sh41 was working on it, and was fixed in [2025.3.1.1](https://youtrack.jetbrains.com/articles/IDEA-A-2100662602/IntelliJ-IDEA-2025.3.1.1-253.29346.240-build-Release-Notes).

This release also includes fixes for a bunch of other various issues, such as a deps watcher deadlock, broken JPS Builder module names, and false warnings in regex sigils with interpolation.

## v23.0.0 , thanks to @sh41!

This release is entirely the work of one person:

- [sh41 (Steve Hall)](https://github.com/sh41) -- @sh41 submitted the original [WSL support PR (#3749)](https://github.com/KronicDeth/intellij-elixir/pull/3749) as a 63-commit "let's fix WSL" proof of concept, then patiently broke it into 7 PRs for review.

Beyond WSL itself, @sh41 fixed a deps watcher deadlock, a JPS Builder regression, added Mix dependency health checks, built out Windows dev infrastructure, added UI test infrastructure, updated documentation, and fixed regex sigil inspection warnings.

Thank you, @sh41 for the incredible work!

## WSL Support

The headline feature of this release.

If you're developing Elixir on Windows using WSL, the plugin should hopefully be a bit better for you - if you find anything weird, please [submit an issue](https://github.com/KronicDeth/intellij-elixir/issues/new/choose).

The work was split from [#3749] into these PRs:

- [#3753](https://github.com/KronicDeth/intellij-elixir/pull/3753)
- [#3754](https://github.com/KronicDeth/intellij-elixir/pull/3754)
- [#3755](https://github.com/KronicDeth/intellij-elixir/pull/3755)
- [#3756](https://github.com/KronicDeth/intellij-elixir/pull/3756)
- [#3757](https://github.com/KronicDeth/intellij-elixir/pull/3757)
- [#3758](https://github.com/KronicDeth/intellij-elixir/pull/3758)
- [#3759](https://github.com/KronicDeth/intellij-elixir/pull/3759)

This resolves the following issues:

- [#1384](https://github.com/KronicDeth/intellij-elixir/issues/1384) - No WSL support (2018)
- [#1911](https://github.com/KronicDeth/intellij-elixir/issues/1911) - Cannot add Erlang/Elixir SDK using Windows WSL
- [#2499](https://github.com/KronicDeth/intellij-elixir/issues/2499) - WSL: "Selected directory is not a valid home for SDK"
- [#3470](https://github.com/KronicDeth/intellij-elixir/issues/3470) - Cannot set SDK for Erlang and Elixir from WSL
- [#3674](https://github.com/KronicDeth/intellij-elixir/issues/3674) - Cannot set Elixir SDK when opening project from WSL2 folder
- [#3746](https://github.com/KronicDeth/intellij-elixir/issues/3746) - MIX_HOME points to wrong directory with asdf/mise

This may also help with:

- [#3659](https://github.com/KronicDeth/intellij-elixir/issues/3659) - SDK setup issues (improved SDK validation)
- [#3716](https://github.com/KronicDeth/intellij-elixir/issues/3716) - SDK not being applied (improved persistence)
- [#3715](https://github.com/KronicDeth/intellij-elixir/issues/3715) - Can't add Erlang SDK (better error handling)

> It should be noted that @sh41 works on Windows, and I (Josh) use Linux/Mac.

### What works

> Please see the [Windows Subsystem for Linux (WSL) Support in the README](https://github.com/KronicDeth/intellij-elixir?tab=readme-ov-file#windows-subsystem-for-linux-wsl-support).

- SDK detection and configuration for Elixir/Erlang in WSL (asdf, mise, kerl, kiex)
- All run configurations (Mix, ExUnit, ESpec, Elixir, IEx)
- External tools (Credo, Dialyzer, Mix Format)
- Project creation wizard with WSL SDKs
- Path conversion (Windows UNC paths <-> WSL POSIX paths)
- Graceful BEAM process termination (double SIGINT)
- Auto-assignment of project SDK when the first Elixir SDK is created
- Proper `MIX_HOME` detection for mise/asdf version managers

### Known Limitations

> [!WARNING]
> - JPS Builder does not support WSL -- use Mix run configurations to compile instead.
> - SDK setup tip: Click "OK" directly after configuring SDKs. Avoid clicking "Apply" then "OK", as this can cause persistence issues.
> - Performance: WSL is slower than native Linux. This is a WSL limitation, not a plugin issue.

### Tested Environment

Tested on Windows 11 with Ubuntu 24.04 on WSL2, with Elixir/Erlang installed via both asdf and mise. There are many possible combinations of Windows versions, WSL distributions, and version managers -- we'd appreciate community testing and bug reports for configurations we haven't covered.

### SDK Discovery

The plugin can now discover Elixir and Erlang SDKs installed inside your WSL distributions. All the version managers you'd expect are supported:

- asdf - `~/.asdf/installs/`
- mise - mise install directories
- kerl - Erlang installations via kerl
- kiex - Elixir installations via kiex
- Homebrew (Linuxbrew) - SDKs installed via Homebrew on Linux
- Nix - SDKs in Nix store paths

SDK discovery has been consolidated into a single `SdkHomeScan` component that works across both native and WSL environments, so the same scanning logic applies everywhere.

During SDK setup, you can choose which WSL distribution to scan. Once an SDK is created, the plugin auto-assigns it as the project SDK if it's the first Elixir SDK configured. SDK persistence has been fixed to resolve workspace model mismatches that previously caused SDKs to "disappear" after restarting the IDE.

### Path Conversion

Transparent path conversion between Windows UNC paths and WSL Linux paths. Both `\\wsl$\` and `\\wsl.localhost\` formats are handled. You can open a project from a WSL network share and everything just works -- file paths are correctly resolved when communicating with WSL-hosted tooling.

### Run Configurations

All run configurations work with WSL-hosted SDKs and projects:

- Mix tasks (`mix compile`, `mix format`, custom tasks)
- IEx sessions
- ExUnit tests (with clickable file/line links back to the IDE)
- ESpec tests
- Distillery releases
- External tools (Credo, Dialyzer, Mix Format)
- Project creation wizard with WSL SDKs

All run configurations have been migrated to extend a new `WslSafeCommandLineState` base class. Test frameworks share a common `TestRunnerCommandLineState`. New WSL-aware command line classes (`WslAwareCommandLine`, `WslAwarePtyCommandLine`) handle path translation in command arguments, environment variables, and working directories behind the scenes. `ColoredProcessHandler` has been replaced with a custom `ElixirProcessHandler` across all configurations.

### Process Handling

Processes running inside WSL cannot be signaled directly from Windows, so dedicated handling was needed. The BEAM VM requires a double SIGINT to terminate gracefully (rather than SIGKILL), and this doesn't work across the WSL boundary with standard IntelliJ process handling.

- DoubleSignalTerminator - Sends SIGINT twice to gracefully shut down the BEAM VM, working correctly across the WSL boundary
- ElixirProcessHandler - WSL-safe process handler with proper state tracking, replacing `ColoredProcessHandler`
- CommandLineLogging - Diagnostic logging for command-line construction to aid troubleshooting when things go wrong

### WSL-aware SDK UI

SDKs from WSL distributions are clearly labeled in the UI and status bar widget, so you can tell at a glance whether you're looking at a native Windows SDK or a WSL one. The SDK selection flow is WSL-aware and works in small IDEs (RubyMine, WebStorm, etc.) that don't have the full "Project Structure" dialog.

## Mix Dependency Health Checks

When you open a project, the plugin now checks whether Mix dependencies are installed. If they're missing or stale, you'll get a notification with an action to run `mix deps.get`. This works for both native and WSL-hosted projects via a shared `MixTaskRunner`.

The Mix project import flow is also faster and more robust -- OTP apps are pre-scanned off the EDT, and results are reused in the import wizard.

## Bug Fixes

### Deps Watcher Deadlock ([#3761](https://github.com/KronicDeth/intellij-elixir/pull/3761))

Fixed a deadlock that occurred when the `/deps` directory was deleted while the deps watcher was active. This also fixes JPS Builder module name handling that had been broken since September 2024.

### Regex Sigil Interpolation ([#3765](https://github.com/KronicDeth/intellij-elixir/pull/3765))

Fixed false inspection warnings in `~r` regex sigils containing interpolated variables (e.g., `~r/#{var}/`). When the `~H` sigil injection feature is enabled, the regex inspection engine was seeing the interpolation syntax and complaining. Interpolations are now stripped before passing the regex to the IDE's inspection engine.

## Infrastructure

Some behind the scenes features, which is really nice!

- Windows development - Platform-aware build services, Gradle tasks, and run configurations for developing the plugin on Windows. Updated `CONTRIBUTING.md` with Windows setup instructions.
- CI improvements - Fork-safe Gradle caching (~4.5 GB cache) to reduce Maven dependency downloads, Maven Central access diagnostics.
- UI test infrastructure - IntelliJ IDE Starter-based UI tests under `testUI/` for automated IDE testing, including Ultimate-only tests for license activation and project import flows.
- Documentation - README updated with WSL setup instructions, configuration steps, and troubleshooting guidance.

## Installation steps

Download the `Elixir-23.0.0.zip` file from below, under `Assets`.

> [!TIP]
> Please refer to the [Install plugin from disk](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk) JetBrains documentation for how to install plugins manually.

1. Ensure your IDE is 2025.3 or above.
2. Open `Settings` -> `Plugins`.
3. Click the cog icon and select `Install Plugin from Disk`.
4. Select where the plugin `.zip` was downloaded and install it.
5. Ensure the plugin is version 23.0.0.
6. Click `OK` to close the plugin window and reload the IDE.
