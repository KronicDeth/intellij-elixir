# Changelog

## v23.5.0

### Enhancements
* [#3817](https://github.com/KronicDeth/intellij-elixir/pull/3817) - [@sh41](https://github.com/sh41)
  * All Elixir settings panels (Credo, Dialyzer, SDKs, Experimental Settings) grouped under a single "Elixir" parent configurable in both full IDEs and small IDEs (RubyMine, PyCharm, etc.). Dropped redundant "Elixir" prefixes from child panel display names.
  * Top-level configurable selection refactored to a service-provider strategy (`TopLevelElixirConfigurableFactory`) with small IDE and rich platform implementations, replacing the `isSmallIde` class detection approach.
  * Settings search indexing via `ElixirSearchableOptionContributor` -- typing "credo", "dialyzer", "elixir", or "liveview" in the Settings search box now surfaces the relevant Elixir settings pages.
  * Credo inspection -- umbrella project support: `resolveCredoWorkingDirectory` walks up from `apps/<app>` content roots to the umbrella root so Credo runs once per umbrella root instead of once per app. Working directories deduplicated by ancestor path.
  * Credo inspection -- execution failure surfacing: failures (missing SDK, missing Credo dependency, compilation errors) now appear as inspection problems on `mix.exs` and as aggregated IDE notifications, instead of being silently dropped.
  * Credo inspection -- partial result preservation: when a Credo run emits some findings before hitting a fatal error, the findings are kept and a warning notification is shown alongside them.
  * Flycheck output parsing extracted and hardened: two-phase approach (record split, then location parse) correctly handles line+column, line-only, and file-level findings. Invalid paths and out-of-range offsets logged at debug level instead of crashing the inspection run.
  * `--mute-exit-status` added to Credo command line so lint-level exit codes are not treated as execution failures.

### Bug Fixes
* [#3817](https://github.com/KronicDeth/intellij-elixir/pull/3817) - [@sh41](https://github.com/sh41)
  * Credo inspection read-action lock churn: consolidated 4-6 separate `runReadAction(Computable { })` calls per output line into a single `runReadAction {}` block per finding. Under 2025.3+ writer-preference locking, the repeated lock acquire/release blocked the EDT when a write action was pending. Partially fixes [#3790](https://github.com/KronicDeth/intellij-elixir/issues/3790).
  * Credo "Configure credo" notification action used internal `ShowSettingsUtilImpl` API -- replaced with `ShowSettingsUtil.getInstance()` and added `project.isDisposed` guard.
  * Credo and Dialyzer configurable IDs (`"Credo"`, `"Dialyzer"`) collided with display names -- separated into distinct `language.elixir.credo` / `language.elixir.dialyzer` IDs.
  * Java-style `//` comment in `plugin.xml` replaced with proper XML `<!-- -->` comment.

## v23.4.0

### Enhancements
* [#3820](https://github.com/KronicDeth/intellij-elixir/pull/3820) - [@sh41](https://github.com/sh41)
  * Status bar SDK widget -- module SDK inconsistency detection: detects dangling SDK references (module `.iml` references an SDK name that no longer exists in the JDK table, e.g. project cloned between WSL distributions) and SDK mismatches (module uses a different SDK than the project SDK). Notifications fire reactively with deduplication and include specific navigation instructions.
  * Status bar SDK widget -- folder mark validation: detects misconfigured source/test/excluded folder marks on Mix project modules. Uses debounced `rootsChanged()` via `MutableSharedFlow` + 2-second debounce to prevent thrashing during bulk operations. Tracks active notifications and expires them when the issue resolves.
  * "Reconfigure Elixir Module Setup" action (`ReconfigureModuleSetupAction`): additively applies canonical folder marks and fixes dangling/mismatched SDK entries. Preserves user-customised source roots, skips non-existent directories and non-Elixir modules. Available via Tools menu and status bar widget popup.
  * `CANONICAL_FOLDER_MARKS` unification: New Project Wizard-created projects now get the full mark set (`lib/`, `web/`, `spec/`, `test/`, plus 7 exclusions). Previously NPW projects missed `spec/` (ESpec), `web/` (pre-Phoenix 1.3), and all exclusions.
  * `ProjectModuleSetupValidator`: inspects every Mix module's content entries against canonical marks and returns a list of discrepancies.
  * Module type utilities: `isElixirModule()` and `getMixContentRoots()` helpers in `ModuleExtensions.kt`.
  * `isSmallIde` detection fix: `ApplicationInfo` product codes instead of class detection (class detection broke in 2026.1).

### Bug Fixes
* [#3820](https://github.com/KronicDeth/intellij-elixir/pull/3820) - [@sh41](https://github.com/sh41)
  * "Run Mix ExUnit" context menu missing on test directories containing non-matching `.ex` files in subdirectories: `containsFileWithSuffix` recursive directory walker returned `false` (stop) on non-matching `ElixirFile` instead of `true` (continue), and the `PsiDirectory` branch propagated that premature stop. Renamed `Finder.kt` -> `ContainsFileWithSuffix.kt`. Fixes [#3804](https://github.com/KronicDeth/intellij-elixir/issues/3804).
  * Replaced deprecated `SystemUtils.isWindows`/`isMac` (Apache Commons) with IntelliJ `SystemInfo`/`OS` utilities across the codebase.
  * Replaced `commons-lang NotImplementedException` with `UnsupportedOperationException` across all uses.
  * Replaced deprecated `SimpleConfigurable.create(Getter)` with `Supplier` overload.
  * `detectSdkVersion` EDT guard: guarded with `runWithModalProgressBlocking` to prevent blocking on the EDT. Possibly addresses [#2980](https://github.com/KronicDeth/intellij-elixir/issues/2980).

## v23.3.0

### Enhancements
* [#3819](https://github.com/KronicDeth/intellij-elixir/pull/3819) - [@sh41](https://github.com/sh41)
  * Erlang external documentation support: parse external `.chunk` files (`<app>/doc/chunks/<module>.chunk`) and normalise doc payload across binary, charlist, and structured-term (`application/erlang+html`) variants. Covers OTP 23, 26, and 27 packaging layouts. BEAM decompilation now loads external chunk docs into generated mirror source.
  * Erlang module resolution via atom qualifier syntax: `:math.sqrt(2)`, `:ets.lookup(table, key)` and similar calls now resolve correctly. `maybeModularNameToModulars()` widened from `Set<Call>` to `Set<PsiNamedElement>` to include BEAM-decompiled `ModuleImpl` instances. Unblocks Go-to-Declaration, Find Usages, and autocomplete for all Erlang modules used with atom qualifier syntax.
  * Syntax highlighting in Quick Documentation code blocks: registered `CodeBlockHtmlProvider` and `CodeFenceHtmlProvider` in `MarkdownFlavourDescriptor`. Indented code blocks default to Elixir; fenced blocks dispatch by language hint with Elixir fallback. `RenderedDocCodeBlockRenderer` applies semantic overlays for alias, function call, macro call, and declaration styling.
  * Erlang atom hover resolution: atom targets routed explicitly in documentation lookup with richer function head presentation using metadata-derived spec signatures.
  * `isDocumentationHost` consolidated into single source of truth in `PsiLanguageInjectionHost`, used by both the injection host and the markdown `Injector`.

### Bug Fixes
* [#3819](https://github.com/KronicDeth/intellij-elixir/pull/3819) - [@sh41](https://github.com/sh41)
  * Quick Documentation (hover/Ctrl+Q) for qualified function calls like `Enum.map(list, fun)` and `GenServer.call(pid, msg)` -- previously showed no docs or fell back to module doc. Four root causes fixed: `ElixirRelativeIdentifier` not forwarded in `getCustomDocumentationElement`; `singleOrNull` returning `null` for multi-clause resolutions (replaced with `firstOrNull`); `filterIsInstance<Call>()` dropping `CallDefinitionImpl` from BEAM-only modules; arity-relaxed fallback not filtering by exact function name. Fixes [#3636](https://github.com/KronicDeth/intellij-elixir/issues/3636).
  * Hover docs for BEAM macros with default arguments (e.g. `Logger.info("hello")`): added `Docs.documentedByNameFallback()` that searches the name's `TreeMap` for the nearest arity. `BeamDocsHelper` now dispatches by `Definition` kind (function vs macro). Possibly addresses [#3650](https://github.com/KronicDeth/intellij-elixir/issues/3650), [#3553](https://github.com/KronicDeth/intellij-elixir/issues/3553), [#3552](https://github.com/KronicDeth/intellij-elixir/issues/3552), [#3324](https://github.com/KronicDeth/intellij-elixir/issues/3324), [#3468](https://github.com/KronicDeth/intellij-elixir/issues/3468), [#2691](https://github.com/KronicDeth/intellij-elixir/issues/2691).
  * Red parser-error squiggles in Elixir code blocks injected into `@doc`/`@moduledoc`/`@typedoc` heredocs suppressed via `DocCodeBlockHighlightErrorFilter` -- documentation snippets are inherently partial and should not show errors.
  * `@delegate_to` doc attributes no longer trigger "Do not know whether to inject Markdown" error log entries.
  * `do`/`end`/`fn` keywords in injected doc code blocks now styled correctly via a new `Keyword` annotator scoped to injected fragments.
  * Erroneous `startInjecting(MarkdownLanguage.INSTANCE)` removed from `injectElixirInCodeBlocksInQuote` -- the function injects Elixir into indented code blocks; starting a competing Markdown injection was incorrect.

## v23.2.0

### Enhancements
* [#3818](https://github.com/KronicDeth/intellij-elixir/pull/3818) - [@sh41](https://github.com/sh41)
  * Code completion deduplication: multi-clause functions (e.g. `Enum.map_every` with 5 clauses) now appear once in completion results instead of once per clause head. Shared `PreferFunctionHead` logic selects bare function heads over implementation clauses.
  * Parameter info deduplication: parameter hints grouped by `(name, arity)` -- separate arities still show distinct hints, but multiple clauses of the same arity no longer produce duplicate entries.
  * Completion prefers source-defined modulars over BEAM stubs when both are available, eliminating duplicate completion entries.
  * Transitive alias resolution: when stub-index lookup finds nothing for a module name that is itself a `QualifiableAlias`, resolution now follows alias chains transitively. Possibly addresses [#1806](https://github.com/KronicDeth/intellij-elixir/issues/1806).
  * `DefinitionsScopedSearch` cancellation: added `ProgressManager.checkCanceled()` at loop boundaries and honour `Processor.process()` return value for early-exit, preventing hangs during large-project searches.

### Bug Fixes
* [#3818](https://github.com/KronicDeth/intellij-elixir/pull/3818) - [@sh41](https://github.com/sh41)
  * **Breaking change**: removed `nameArityInAnyModule` global fallback from resolver. Previously, when `resolveInScope` found no results, the resolver fell back to a global stub-index search returning every function with a matching name from every module (all marked `validResult=false`). This polluted parameter hints with unrelated modules (e.g. hovering `Enum.map()` showed hints from `Stream.Reducers`, `Ecto`, `Phoenix`), caused Go-to-Definition to navigate to wrong-module definitions, and filled the resolution cache with irrelevant results. Calls that were previously "resolved" to functions in unrelated modules will now correctly appear as unresolved references.
  * Infinite loop in `UnaliasedName.up` when resolving `QualifiedMultipleAliases` -- function overload ordering caused mutual recursion.
  * Infinite recursion and NPE prevention in PSI resolve/tree-walk paths via `RecursionManager.doPreventingRecursion()` and null-safe `VISITED_ELEMENT_SET` access in `ResolveState`.
  * `@spec` line marker grouping checked arity equality before name equality -- specs for different functions with the same arity were incorrectly grouped together.
  * Gutter icons anchored to leaf `PsiElement`s per the `LineMarkerProvider` contract. Non-leaf elements caused markers to blink or appear in wrong positions after edits.
  * Removed redundant `computeReadAction`/`runReadAction` wrappers from `CallImpl` getters (`functionName`, `moduleName`, `resolvedPrimaryArity`) and `PsiNamedElementImpl` name getters. These trivial PSI reads were called from paths already holding a read lock; under 2025.3+ writer-preference locking, re-acquiring blocks the EDT when a write action is pending. Partially fixes [#3790](https://github.com/KronicDeth/intellij-elixir/issues/3790).
  * `Elixir.` prefix stripping for module name resolution -- stub index stores names without the prefix, so `Elixir.Enum` lookups now match correctly.

## v23.1.0

### Enhancements
* [#3816](https://github.com/KronicDeth/intellij-elixir/pull/3816) - [@sh41](https://github.com/sh41)
  * Project-scoped coroutine service (`ElixirCoroutineService`) with `supervisedChildScope` for structured, lifecycle-bound concurrency across plugin subsystems.
  * Debugger runtime (Process, Node, MailBox) migrated from unmanaged `executeOnPooledThread` to structured coroutine scopes with cooperative cancellation via `ensureActive()`. Clean `CancellationException` handling prevents false error reports on shutdown.
  * Serialised node-facing debugger operations on a dedicated single-lane `nodeDispatcher`, preventing concurrent BEAM RPC/network calls and preserving operation ordering.
  * WSL debugger source resolution: when a BEAM-reported source path is Linux-style and doesn't resolve locally, converts to Windows UNC via WSL compat and retries. Applied to breakpoint hit navigation, stack frame source mapping, and failed-breakpoint presentation. WSL distribution cached on the debugger process to avoid repeated lookups.
  * `mix format` integration replaced: removed legacy `MixFormatExternalFormatProcessor`, added `MixFormatFormattingService` using `AsyncDocumentFormattingService`. Improved stderr parsing, notification rendering, and error offset navigation.
  * CLI ANSI toggle: added `ansi` parameter to control ANSI escape codes in subprocess output. Disabled for formatter subprocess to keep parsing deterministic.
  * Breakpoint availability hot-path optimisation: added `isInsideModule()` for cheap boolean module-boundary checks, avoiding full module-name assembly. `getModuleName()` traversal now stops at file boundaries to avoid directory traversal.
  * SDK setup modernisation: replaced hand-rolled `invokeAndWait` + boolean-flag pattern in `SdkRegistrar` with `edtWriteAction {}`. `registerOrUpdateErlangSdk` / `registerOrUpdateElixirSdk` are now suspend funs. Replaced `ProgressManager.runProcessWithProgressSynchronously` in `erlang/Type.setupSdkPaths` with `runWithModalProgressBlocking + withContext(Dispatchers.IO)`.
  * Removed unnecessary environment picker complexity from dependent SDK creation flow.
  * Removed blocking pooled-thread wrappers from SDK lookup; tightened read-action/background boundaries.

### Bug Fixes
* [#3816](https://github.com/KronicDeth/intellij-elixir/pull/3816) - [@sh41](https://github.com/sh41)
  * Debugger node network calls dispatched off EDT via serial `nodeDispatcher`, fixing EDT blocking during debug sessions.
  * VFS blocking read in debugger breakpoint availability checks -- `getModuleName()` directory traversal triggered blocking disk reads on the VFS, now stops at file boundaries. Partially fixes [#3790](https://github.com/KronicDeth/intellij-elixir/issues/3790).
  * WSL Linux paths not resolving for debugger source navigation -- breakpoint hits, stack frames, and failed-breakpoint messages now resolve correctly when the BEAM reports Linux-style paths on Windows.
  * Erlang SDK `detectSdkVersion` EDT path guarded with `runWithModalProgressBlocking` to prevent unguarded EDT blocking.
  * Debugger deprecation warning and unused variable warning resolved.

## v23.0.7

### Enhancements
* [#3815](https://github.com/KronicDeth/intellij-elixir/pull/3815) - [@sh41](https://github.com/sh41)
  * Unicode identifier and atom support in the JFlex lexer -- Elixir supports Unicode identifiers (e.g. `def ΦΤ§ do`) and bare Unicode atoms (e.g. `:ΦΤ§`), but the lexer only recognised ASCII characters. Added Unicode letter/digit support using JFlex POSIX character classes.
  * Erlang private functions now decompiled as `defp` instead of `def`, using the BEAM export table to classify functions.
  * Strip `-type`/`-opaque` prefix from Erlang type signatures, mapping to `@typep`/`@opaque`.
  * `Elixir.` prefix stripping for uniform module name resolution -- `Enum.map()` and `Elixir.Enum.map()` now resolve consistently.
  * Type rendering: replaced a `TODO()` crash in `appendTypes()` with actual BEAM doc-chunk signature rendering.
  * Non-blocking bulk decompilation: moved bulk decompile scan off the EDT with run-scoped log deduplication to prevent log floods during library indexing.
  * Improved parse error diagnostics: error reports now include the parser error description and failing source line, with top-10 unique error pattern summary in the bulk-decompile run log.

### Bug Fixes
* [#3815](https://github.com/KronicDeth/intellij-elixir/pull/3815) - [@sh41](https://github.com/sh41)
  * FD leak fix (WSL/IJent): `InputStream` opened per BEAM file was never closed; after ~20K sequential reads during bulk decompilation, IJent exhausted its vsock FD limit, crashed the gRPC connection, and froze the IDE. Wrapped in `use {}` to release FDs immediately. Fixes [#3613](https://github.com/KronicDeth/intellij-elixir/issues/3613).
  * Parenthesise block expressions (`case`/`if`/`try`) and nested binary literals inside binary element type specs -- previously emitted unparseable Elixir. Fixes [#3554](https://github.com/KronicDeth/intellij-elixir/issues/3554), [#3555](https://github.com/KronicDeth/intellij-elixir/issues/3555).
  * Space after word-based unary operators (`not` etc.) to prevent keyword-argument misparse. Fixes [#3556](https://github.com/KronicDeth/intellij-elixir/issues/3556).
  * Sanitise Erlang compiler-generated variable names (e.g. `f@_1`) containing `@` -- invalid in Elixir identifiers -- replacing with `_`. Fixes [#3557](https://github.com/KronicDeth/intellij-elixir/issues/3557).
  * Escape interpolation markers (`#{`) in decompiled atom and string values. Fixes [#3519](https://github.com/KronicDeth/intellij-elixir/issues/3519).
  * Strip `\r` from BEAM documentation chunk strings before inserting into IntelliJ `Document`. Fixes [#3433](https://github.com/KronicDeth/intellij-elixir/issues/3433).
  * Handle `:elixir_erl` Dbgi metadata value `:none` (modules compiled with `debug_info: false`) gracefully instead of logging SEVERE errors. Fixes [#3454](https://github.com/KronicDeth/intellij-elixir/issues/3454).
  * Escape `\u{...}` sequences in decompiled doc strings to prevent Elixir misinterpreting them as Unicode code-point escapes -- was silently breaking the entire `String` module mirror mapping. Fixes [#3412](https://github.com/KronicDeth/intellij-elixir/issues/3412).
  * Emit `def` prefix on overridden `__struct__/1` signatures so PSI mirror mapping resolves arity-1 struct functions. Fixes [#3596](https://github.com/KronicDeth/intellij-elixir/issues/3596).
  * Handle Erlang wildcard variable `_` in `record_field` -- eliminates ~100 SEVERE log entries per bulk decompile run. Fixes [#3403](https://github.com/KronicDeth/intellij-elixir/issues/3403).
  * Fix backslash escaping order: escape `\` before `'` in Erlang charlist rendering. Fixes [#3234](https://github.com/KronicDeth/intellij-elixir/issues/3234).
  * Add `not` to reserved variable keywords. Fixes [#2825](https://github.com/KronicDeth/intellij-elixir/issues/2825).
  * Emit `()` for zero-argument anonymous function clauses. Fixes [#2916](https://github.com/KronicDeth/intellij-elixir/issues/2916).
  * Emit source expression for empty map update associations. Fixes [#2745](https://github.com/KronicDeth/intellij-elixir/issues/2745).
  * Propagate `doBlock` flag through match expressions in comprehension generators. Fixes [#2907](https://github.com/KronicDeth/intellij-elixir/issues/2907), [#2908](https://github.com/KronicDeth/intellij-elixir/issues/2908).
  * Parenthesise nested bitstring generator expressions and wrap non-literal binary element sizes in `size()`. Fixes [#3469](https://github.com/KronicDeth/intellij-elixir/issues/3469), [#3423](https://github.com/KronicDeth/intellij-elixir/issues/3423).
  * Render non-integer binary element type specifier values. Fixes [#3240](https://github.com/KronicDeth/intellij-elixir/issues/3240).
  * Validate alias shape for `Elixir.`-prefixed atoms, falling back to quoted atom form for invalid names. Fixes [#3197](https://github.com/KronicDeth/intellij-elixir/issues/3197), [#3206](https://github.com/KronicDeth/intellij-elixir/issues/3206).
  * Indexing deadlock prevention: `Cache.from(FileContent)` now uses `Beam.from(fileContent)` instead of reopening `VirtualFile.inputStream` on cache misses. Fixes [#2544](https://github.com/KronicDeth/intellij-elixir/issues/2544), [#2333](https://github.com/KronicDeth/intellij-elixir/issues/2333).
  * Escape backslashes in quoted strings. Fixes [#2728](https://github.com/KronicDeth/intellij-elixir/issues/2728), [#2635](https://github.com/KronicDeth/intellij-elixir/issues/2635).
  * Fix decompilation of modules with `Elixir.`-prefixed atoms that contain `do`-`end` operands causing parse cascades. Fixes [#778](https://github.com/KronicDeth/intellij-elixir/issues/778), [#2769](https://github.com/KronicDeth/intellij-elixir/issues/2769), [#3420](https://github.com/KronicDeth/intellij-elixir/issues/3420).

## v23.0.6

### Enhancements
* [#3814](https://github.com/KronicDeth/intellij-elixir/pull/3814) - [@sh41](https://github.com/sh41)
  * 2026.2 compatibility: use deprecated `selectSdkHome` 2-arg form in SDK setup to compile against both 261 and 262 API. **Note**: On WSL, the SDK file chooser now opens at the user home directory instead of the WSL distribution root. This will be restored when 261 support is dropped.

### Bug Fixes
* [#3814](https://github.com/KronicDeth/intellij-elixir/pull/3814) - [@sh41](https://github.com/sh41)
  * Fix unreachable `?: "IU"` fallback for `platformType` Gradle property -- `get()` throws on missing properties, so the elvis operator was dead code. Now uses `getOrElse("IU")`.

### Build
* [#3814](https://github.com/KronicDeth/intellij-elixir/pull/3814) - [@sh41](https://github.com/sh41)
  * CI matrix updated to `windows-2025` runners. IDEA EAP version pinned to `261.24374.34` (Java 25 incompatibility with `LATEST-EAP-SNAPSHOT`).
  * Plugin verifier pinned to 1.384 with markdown/HTML/plain report formats and clickable report output.

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
