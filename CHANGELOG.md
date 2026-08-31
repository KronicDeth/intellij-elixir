# Changelog

## [Unreleased]

### Breaking changes

### Enhancements

- [#3954](https://github.com/KronicDeth/intellij-elixir/pull/3954) [@sh41](https://github.com/sh41)
  - **`~E` sigils now get EEx highlighting and completion.** `~H` injected HEEx and `~L` injected EEx,
    but Phoenix.HTML's own `~E` fell through to no injection at all, leaving the template as plain
    string text. It is EEx, like `~L`, and shares the same experimental HTML-injection setting.
    Refs [#1257](https://github.com/KronicDeth/intellij-elixir/issues/1257).

- [#3925](https://github.com/KronicDeth/intellij-elixir/pull/3925) [@sh41](https://github.com/sh41)
  - **File and line are now linked inside an inspected stack trace.** A crash report often carries
    its trace as a term rather than a formatted trace, putting each frame's location in a keyword
    list - `[file: 'lib/gald/phase.ex', line: 75]`. Only the formatted `(app) path:line:` frames
    were clickable, so the frame that named the failing call had to be found by hand. Every location
    on the line is linked now, and a path inspected as a `~c` sigil is read the same as a plain
    charlist. Where `inspect` wrapped the entry so the number sits on its own line, the path still
    navigates to that line in the Mix, Elixir, Distillery and test-runner consoles; in the IEx
    consoles it opens the file, because a filter there is given one line at a time with no way to
    reach the next. Refs [#510](https://github.com/KronicDeth/intellij-elixir/issues/510).
  - **Stack traces are now linked in the Terminal tool window as well.** `iex -S mix`, `mix test`
    and `mix phx.server` run from the Terminal had no Elixir linking at all, because the filters
    were only ever attached to consoles the plugin's own run configurations built. They are now
    contributed to every console the IDE builds for a project with an Elixir module or SDK, and on
    the reworked terminal engine a wrapped entry links to its exact line rather than to the top of
    the file.
  - **Erlang, HEEx and LEEx locations in a trace are now linked too.** A stack trace mixes them with
    Elixir freely, but only `.ex`, `.exs` and `.eex` were recognised, so an Erlang dependency's frame
    stayed plain text beside the Elixir frames around it. Whether a path then opens still depends on
    the file being indexed - see the Erlang SDK source roots below.
  - **An Erlang SDK now exposes its own sources.** OTP ships them beside the compiled beams, in
    `lib/<app>-<version>/src`, but only the `ebin` directories were registered, so an OTP frame in a
    stack trace named a file nothing indexed could find and `gen_server.erl` was not navigable.
    Existing SDKs pick the roots up on the next refresh.
  - **External Libraries now shows an SDK application's source beside its beams.** A tree node for an
    application that ships both is now the application itself, holding `ebin` and the `lib` or `src`
    next to it; one shipping only beams is unchanged. An SDK node's children are its class roots
    alone, so the source directory has to be put there deliberately - registering it as a source root
    makes it searchable, not visible.

- [#3923](https://github.com/KronicDeth/intellij-elixir/pull/3923) [@sh41](https://github.com/sh41)
  - **Elixir and Erlang SDKs under `/usr/lib64` are now detected automatically.** Gentoo builds both
    with the multilib libdir, and Fedora does for Erlang, so the SDK had to be selected by hand on
    those distributions even though the path was valid. Refs
    [#312](https://github.com/KronicDeth/intellij-elixir/issues/312).
  - **Elixir SDKs under `/usr/share/elixir/<version>` are now detected automatically.** Fedora and
    RHEL package Elixir there rather than under a libdir; the version is read from the directory
    name, so several installed versions are offered separately.
  - **Homebrew Elixir installed since 2024-09-22 is now detected and accepted.** Homebrew changed to
    the Makefile's `install` target, which nests the SDK home in `lib/elixir` instead of placing it
    directly in the version directory. Installs predating the change keep working. Refs
    [#3670](https://github.com/KronicDeth/intellij-elixir/issues/3670).
  - **Homebrew SDKs on Apple Silicon are now detected.** Only the Intel `/usr/local/Cellar` prefix
    was scanned, never `/opt/homebrew/Cellar`.
  - **Selecting an install prefix in the SDK chooser now finds the home inside it.** A prefix such
    as a Homebrew version directory, `/usr` or `/usr/local` holds the SDK in `lib/<tool>` and only
    symlinks in `bin`, so picking the directory a user naturally lands on was rejected as an invalid
    home with no hint of where the real one was. Erlang had no adjustment at all. Refs
    [#3670](https://github.com/KronicDeth/intellij-elixir/issues/3670),
    [#312](https://github.com/KronicDeth/intellij-elixir/issues/312).
  - **Version-pinned Homebrew formulae are now detected.** `erlang@25` through `erlang@28` live
    under their own Cellar directory rather than beside the unpinned versions, so a pinned OTP
    release - the usual way to hold an OTP version for a given Elixir - was never offered.
  - **macOS now detects SDKs installed by the `elixir-install` script.** Every platform now scans
    the same set of sources, so a source can no longer be present on one platform and missing on
    another.
  - **Homebrew SDKs on Linux and WSL are now detected.** Homebrew's Linux prefix
    (`/home/linuxbrew/.linuxbrew`) was never scanned on any platform, and a Homebrew home there is
    now labelled as such in the SDK list.

- [#3914](https://github.com/KronicDeth/intellij-elixir/pull/3914) [@sh41](https://github.com/sh41)
  - **Elixir 1.13 through 1.20 are now fully supported.** Code written for any Elixir from 1.13.4 to
    1.20.2 parses without spurious errors, including syntax whose meaning changed between releases.
  - **`.beam` files compiled by OTP 24 through 29 decompile to valid Elixir.**

- [#3696](https://github.com/KronicDeth/intellij-elixir/pull/3696) [@mwnciau](https://github.com/mwnciau), [@sh41](https://github.com/sh41)
  - **`.heex` files are now recognized as HEEx**, with dedicated syntax highlighting for `{@assigns}`/
    `{expressions}`, `<% %>`/`<%= %>` tags, and `<%!-- ... --%>` HEEx comments.
  - **`~H` sigils now use the same HEEx language as `.heex` files**, with separate HTML and Elixir
    PSI roots, instead of being treated as plain HTML text. Opt-in via Settings → Languages &
    Frameworks → Elixir → Experimental → "Enable ~H Sigil HEEx language injection".
  - **`<.component>` and `<Module.component>` tags now resolve** to their `def`/`defp` definition -
    local components, components brought in via an explicit `import`, and components brought in via
    `use MyAppWeb, :html` all navigate to their definition. A dotted name that is not valid component
    syntax is flagged by `HtmlUnknownTagInspection` like any other unknown tag.
  - **Find Usages and Rename on a `def`/`defp` include its HEEx component tags**, in `.heex` files
    and inside `~H` sigils; a renamed tag keeps its `.`/module-alias prefix.
  - **Rename works with the caret on a component tag**, not only on the `def`/`defp`.
  - **Quick Docs and hover on a component tag show the function's documentation**, including inside
    a `~H` sigil.
  - **Find Usages finds a plain Elixir call embedded in a `~H` sigil**, e.g. `{some_function()}`.

### Bug Fixes

- [#3968](https://github.com/KronicDeth/intellij-elixir/pull/3968) [@sh41](https://github.com/sh41)
  - **"Align operands of pipe operator (|)" no longer staircases a multi-operand union.** `a | b | c`
    parses as nested pipe operations, and each level built its own alignment, so every level anchored
    its operands one column-step further right than the level above - turning the setting off was the
    only way to get them lined up. A nested level now shares the alignment of the pipe operation it
    sits in, matching what `mix format` produces. Fixes
    [#1787](https://github.com/KronicDeth/intellij-elixir/issues/1787).

- [#3965](https://github.com/KronicDeth/intellij-elixir/pull/3965) [@sh41](https://github.com/sh41)
  - **Renaming a variable no longer rewrites an inner binding that shadows it.** The shared usage
    search matched by name and scope alone, so an `fn` parameter, `case` clause pattern or `for`
    generator reusing an outer name was renamed with it. Fixes
    [#1479](https://github.com/KronicDeth/intellij-elixir/issues/1479).
  - **Renaming from a rebinding inside an `fn` or `case` body now covers the whole variable.**

- [#3964](https://github.com/KronicDeth/intellij-elixir/pull/3964) [@sh41](https://github.com/sh41)
  - **"Remove space between function name and parentheses" now removes the space instead of
    throwing.** The space stopped being a child of the element the quick fix searched when the
    grammar gained `noParenthesesOneArgument` in 2015. Fixes
    [#3107](https://github.com/KronicDeth/intellij-elixir/issues/3107).

- [#3963](https://github.com/KronicDeth/intellij-elixir/pull/3963) [@sh41](https://github.com/sh41)
  - **A `@spec` whose head has no function name no longer crashes the structure view.** Heads like
    `@spec foo.() :: term` and `@spec bar not in baz :: term` parse to a call with no name, so the
    malformed spec is now left out of the tree instead of throwing. Fixes
    [#1564](https://github.com/KronicDeth/intellij-elixir/issues/1564).

- [#3962](https://github.com/KronicDeth/intellij-elixir/pull/3962) [@sh41](https://github.com/sh41)
  - **Ctrl+Click and highlighting no longer throw on a function the decompiler could not recreate.**
    Refs [#3309](https://github.com/KronicDeth/intellij-elixir/issues/3309).

- [#3957](https://github.com/KronicDeth/intellij-elixir/pull/3957) [@sh41](https://github.com/sh41)
  - **A dep with `allow_pre:` no longer reports an unknown Mix dep option.** It is a Hex flag that
    cannot change where a dep is checked out. Fixes
    [#2487](https://github.com/KronicDeth/intellij-elixir/issues/2487).

- [#3953](https://github.com/KronicDeth/intellij-elixir/pull/3953) [@sh41](https://github.com/sh41)
  - **"Cannot find enclosing Modular" no longer fires for a `def` inside a map-held `quote`, or for a
    `@callback` behind an infix operator.** The walk that finds a definition's enclosing module treated
    lists and tuples as transparent but not a map's internals, so a `quote` held as a map value stopped
    it; and it knew `=` and `|>` but no other infix operator, so an `@callback` on the right of `||`
    stopped it too. Every infix operator is transparent now, since a binary operator is never itself a
    module. Refs [#1695](https://github.com/KronicDeth/intellij-elixir/issues/1695) and
    [#1438](https://github.com/KronicDeth/intellij-elixir/issues/1438).

- [#3951](https://github.com/KronicDeth/intellij-elixir/pull/3951) [@sh41](https://github.com/sh41)
  - **`defstruct do ... end` and `defexception do ... end` no longer crash the structure view.** Both
    were gated on the arity Elixir resolves, which counts a `do` block as an argument even though
    nothing is actually written inside the call - so the structure view then asserted on an argument
    list that was empty. Fixes [#2107](https://github.com/KronicDeth/intellij-elixir/issues/2107) and
    [#1095](https://github.com/KronicDeth/intellij-elixir/issues/1095).
  - **A piped `defdelegate` with a `do` block no longer throws while resolving variables.** Same
    resolved-arity-vs-written-arity mismatch as above, reached through `defdelegate`'s own gate.

- [#3948](https://github.com/KronicDeth/intellij-elixir/pull/3948) [@sh41](https://github.com/sh41)
  - **Auto-Indent Lines and paste no longer throw on a map update with a literal on the left**, such
    as `%{%{a} | k: 1}`.

- [#3947](https://github.com/KronicDeth/intellij-elixir/pull/3947) [@sh41](https://github.com/sh41)
  - **Large integer literals in a decompiled BEAM now show their real value**, and the Code tab no
    longer throws on some files.

- [#3946](https://github.com/KronicDeth/intellij-elixir/pull/3946) [@sh41](https://github.com/sh41)
  - **Configuring an Elixir SDK no longer crashes PhpStorm, WebStorm and the other small IDEs** when
    another plugin registers a root type of its own.

- [#3934](https://github.com/KronicDeth/intellij-elixir/pull/3934) [@sh41](https://github.com/sh41)
  - **A trailing comma in a call's argument list no longer breaks the rest of the file.** Typing
    `foo(a,)` on the way to the next argument used to discard the enclosing definition and everything
    after it, taking parameter hints with it. Applies to `foo(a,)`, `Mod.fun(a,)` and `fun.(a,)`.
  - **Parameter hints no longer describe functions you are not calling.** `Enum.reduce` listed eight
    signatures, including `reduce_while`'s. Each arity of the called function still gets its own hint.
  - **Parameter hints now appear when a completion is accepted.** Accepting a name from the completion
    popup left the caret between the inserted parentheses with no hint there.
  - **Pressing Enter after a delimiter now indents the caret where an element goes** rather than back
    to the enclosing statement: after `[`, `{`, `%{`, `%S{`, `<<` and `Foo.{`, after a comma, after
    `->` in a clause, after the `rescue`, `after`, `else` and `catch` keywords, and inside empty
    parentheses. Every column is the one `mix format` produces. Refs
    [#799](https://github.com/KronicDeth/intellij-elixir/issues/799).

- [#3925](https://github.com/KronicDeth/intellij-elixir/pull/3925) [@sh41](https://github.com/sh41)
  - **A console path written with backslashes now links on Windows.** A compile error prints the
    path the way the platform writes it, while the virtual file system holds forward slashes, and
    the two were compared without normalising either - so on the one platform where every console
    path looks like that, the frame resolved to nothing.

- [#3924](https://github.com/KronicDeth/intellij-elixir/pull/3924) [@sh41](https://github.com/sh41)
  - **Variable completion no longer shows a parameter's name twice.** A parameter that is not bound
    by a match is its own enclosing match, so the match appended after the name only repeated it.
    Variables bound by a match still show that match, which is what makes the tail text useful.
    Fixes [#496](https://github.com/KronicDeth/intellij-elixir/issues/496).

- [#3923](https://github.com/KronicDeth/intellij-elixir/pull/3923) [@sh41](https://github.com/sh41)
  - **Homebrew Erlang SDKs no longer report an unknown version.** The version was read from the
    home path after the Homebrew layout adjustment had been applied, so every Homebrew Erlang was
    keyed on the literal string `erlang` instead of its version, leaving them unsorted and
    unlabelled in the SDK list.

- [#3696](https://github.com/KronicDeth/intellij-elixir/pull/3696) [@mwnciau](https://github.com/mwnciau), [@sh41](https://github.com/sh41)
  - **`\{` and `\}` in HEEx are now literal braces** instead of affecting `{...}` expression nesting.
  - **A component tag's attribute after a `{...}`-valued one no longer gets swallowed into that
    value**, e.g. `<.tag one={""} class=""/>` now parses `class` as its own attribute.
  - **`{` inside `<script>`/`<style>` stays literal after an embedded `<% %>` tag**, and
    `</script>`/`</style>` still close the tag.
  - **A self-closing `<script .../>` or `<style .../>` no longer swallows the rest of the file** as
    script or style content.
  - **`<% %>` tags that produce no output (comments, `<% if %>`) no longer inject placeholder text**
    into the HTML tree.
  - **Other HTML-based template languages are no longer affected by HEEx's outer-language patcher**,
    which ran for every HTML-data template language in the IDE.
  - **HEEx's special attributes and `phx-` bindings are no longer reported as "not allowed here"
    on HTML tags**: `:let`, `:if`, `:for`, `:key`, `:type` and every `phx-*` binding.
  - **`<:slot>` tags are no longer validated as HTML elements.** `<:col>` was checked against HTML's
    `<col>` and `<:action>` reported as an unknown tag.
  - **A component tag that doesn't resolve no longer shows "Cannot resolve symbol".**
  - **Components brought into scope by a `use` nested inside `use MyAppWeb, :html` now resolve**,
    such as `Phoenix.Component`'s `<.link>` and `<.live_title>`: `use` resolution now follows a
    `__using__` whose body ends in a list of fragments, a `quote` bound to a variable, or another
    `use`.
  - **Rename and Find Usages on a component tag could miss the tag**, varying between IDE sessions:
    at a tag's offset the Elixir root of a `.heex` file or `~H` sigil could win over the HTML root.
  - **An unqualified call inside a `~H` sigil now resolves to a `def` in the surrounding module.**

- [#3914](https://github.com/KronicDeth/intellij-elixir/pull/3914) [@sh41](https://github.com/sh41)
  - **Reformat Code no longer changes what a capture expression means.** Since Elixir 1.15,
    `&1` and `& 1` are different expressions; the formatter was inserting that space, silently
    turning `&(&1 + &2)` into code meaning `&(&(1 + &2))`.
  - **`& 1` now parses the way the containing module/project's configured Elixir SDK parses it**. The
    plugin previously always used the pre-1.15 meaning.
    - On Elixir 1.15 and higher: `&` applied to the whole following expression.
    - On Elixir 1.14 and lower: the single capture argument `&1`.
  - **The stepped-range atom `:..//` no longer shows a parse error.** It was previously read as
    `:..` followed by a stray `//`.
  - **Decompiled `.beam` files no longer show a parse error when a `cond` or `case` clause condition
    ends in `end`**. This fixes decompilation of `Mix.Project`, `Mix.Release`, and `Mix.Task` on
    Elixir 1.20+.

- [#3919](https://github.com/KronicDeth/intellij-elixir/pull/3919) [@sh41](https://github.com/sh41)
  - **New Elixir projects get the right compiler output directory.** Leaving `--app` blank in the
    New Project wizard - which is allowed, and is what most people do - configured the module with
    `_build/dev/lib/ebin` instead of `_build/dev/lib/<app>/ebin`. It now falls back to the project
    name, which is what `mix new` itself uses.
  - **`--sup` is no longer offered for umbrella projects.** `mix new` ignores it at an umbrella root
    without reporting anything, so ticking it appeared to work and silently did nothing.
  - **New umbrella projects no longer get a stray empty `lib/` or a compiler output directory that
    never exists.** An umbrella root has no application of its own, so neither applies to it.
  - **New Elixir projects and modules are no longer reported as having "an outdated format".** Every
    project the plugin created was offered for conversion the next time it was opened, because the
    module was written with compiler-output exclusion switched on - the exact setting the plugin's
    converter exists to remove. Elixir compiles to `_build`, so the setting never applied.
  - **Umbrella sub-apps now get their `lib/` and `test/` marked.** `mix new` writes an app from an
    external process, so the IDE could hold a stale view of the directory that did not include its
    `mix.exs` - and every folder-mark scan skipped the app in silence because of it. The New Project
    wizard, *Reconfigure Elixir Module Setup* and the module-setup check now refresh first.

- [#3921](https://github.com/KronicDeth/intellij-elixir/pull/3921) [@sh41](https://github.com/sh41)
  - **Variables bound by a macro call in a match now resolve.** `session(id, user) = raw` binds all
    of the macro's arguments, but every use of them was reported as unresolved: only three `Kernel`
    names were recognised as binding, and any other parenthesised call was assumed to be a function,
    whose arguments are values. The call is now resolved to decide whether it is a macro. Calls that
    resolve to a function are unaffected, as are macros that cannot be resolved at all.

- [#3918](https://github.com/KronicDeth/intellij-elixir/pull/3918) [@sh41](https://github.com/sh41)
  - **Saved run configurations no longer record the module twice.** Every Elixir run configuration
    wrote a second `module` element on top of the one the IDE already writes, which the platform
    logs as "Module serialized more than one time".
  - **"Include system environment variables" is now remembered.** The setting was accepted in the
    run configuration editor but silently discarded when the configuration was saved.
  - **A run configuration whose module is not loaded yet keeps it.** Reading a saved configuration
    before its module exists no longer clears the module instead of leaving the IDE to resolve it.

- [#3915](https://github.com/KronicDeth/intellij-elixir/pull/3915) [@sh41](https://github.com/sh41)
  - **The IDE no longer starts every WSL distro that hosts a registered Erlang or Elixir SDK, and
    will no longer freeze if WSL is slow or hangs while starting.** Checking whether two WSL-hosted
    SDKs are the same no longer touches the distro's filesystem.

### Threading / Platform Hygiene

- [#3950](https://github.com/KronicDeth/intellij-elixir/pull/3950) [@sh41](https://github.com/sh41)
  - **The formatter's `Block` is clean under IntelliJ's inspections** - 18 warnings down to 3. Four
    nullable-node dereferences now state the `AbstractBlock` non-null invariant,
    `ContainerBlockListReducer` stops declaring an `Indent` non-null that callers pass as null, and the
    interpolation overload no longer takes two parameters that were always the same constants.

### Build / CI

- [#3969](https://github.com/KronicDeth/intellij-elixir/pull/3969) [@sh41](https://github.com/sh41)
  - **The quoter daemon is now started on every test run, not just the first in a checkout.** Its files
    made `startQuoter` look up to date, but the daemon it starts is stopped again when the build ends,
    so from the second run onwards the task was skipped while its marker still reported
    `quoter.available=true` and every test that quotes failed on a PID timeout. The task is now always
    out of date and never cached.
  - **The quoter's Erlang distribution and `epmd` now bind loopback rather than every interface.** Both
    listeners bound the wildcard address despite the nodes being local, which is what made Windows
    Firewall prompt for the release's `erl.exe` and the IDE's `java.exe`, once per worktree. An `epmd`
    that is already running is used as it is; only a fresh start asks for loopback.

- [#3960](https://github.com/KronicDeth/intellij-elixir/pull/3960) [@sh41](https://github.com/sh41)
  - **`epmd` is now started from the Erlang SDK rather than the quoter's own bundled ERTS.** A
    distributed node starts `epmd` from the ERTS of whatever release is starting, detached, so the one
    the quoter left behind ran from under `cache/` inside the checkout. On Windows a running executable
    pins its own directory, which made deleting a checkout or a git worktree fail with "Device or
    resource busy" long after the build finished. The build now starts `epmd` from the SDK first and
    passes `-start_epmd false`, falling back to the previous behaviour when no SDK `epmd` is found.

- [#3913](https://github.com/KronicDeth/intellij-elixir/pull/3913) [@sh41](https://github.com/sh41)
  - **Quoter tests now respect the quoting rules for the Elixir version under test.** Six quoted-form
    divergences are gated on the release that introduced each, so the same fixtures pass on every
    Elixir/OTP pair CI tests.
  - **A quoter that fails to build no longer blocks the whole test suite.** Tests that require the
    quoter fail immediately with the recorded reason; all other tests run as normal.
    `-PquoterRequired=true` restores the hard stop for anyone debugging the quoter itself.
  - **CI gets the quoter cache paths from the build** (a `quoterCachePaths` task) instead of
    re-deriving them in bash, so the workflow can no longer disagree with Gradle about where the
    cache lives.
  - **The quoter is pinned to a commit of `sh41/intellij_elixir` which can be built without warnings
    on all Elixir versions 1.13-1.20**, pending merge/tagging of the
    [PR](https://github.com/KronicDeth/intellij_elixir/pull/11).
  - **The quoter builds with an explicit `MIX_ENV=prod`**, and only prod dependencies are fetched. An exported `MIX_ENV` still overrides it.

- [#3914](https://github.com/KronicDeth/intellij-elixir/pull/3914) [@sh41](https://github.com/sh41)
  - **All Elixir/OTP test legs now pass and are required for merge.** Getting there also gated two
    more quoted-form divergences by version (1.20's `__block__` line metadata, pre-1.17 parens
    metadata) and froze copies of stdlib files that newer Elixirs deleted, so the parsing corpus no
    longer shifts underneath the tests.

- [#3916](https://github.com/KronicDeth/intellij-elixir/pull/3916) [@sh41](https://github.com/sh41)
  - **`testUI` builds and runs again.** `kodein-di-jvm` and `kotlinx-coroutines-core-jvm` were
    silently dropped from its dependencies by an earlier commit; both are genuinely required.
  - **The released plugin now compiles against 2025.3, its declared minimum supported version**,
    instead of a newer platform - closing a gap where only the post-release Plugin Verifier check
    stood between an incompatibility and a real user's install.
  - **The shipped plugin no longer accidentally bundles its own copy of `kotlin-stdlib`.**
  - Bumped `gradle-wrapper` to 9.7.0 (with its distribution checksum now verified),
    `org.jetbrains.changelog` to 2.5.0, and migrated `com.github.ben-manes.versions` to the
    maintained `io.github.ben-manes.versions` id at 0.61.0. Dropped the unused `qodana` version
    catalog entry (`qodana.yml`'s linter tag is the only declaration now, bumped to 2026.2).

- [#3887](https://github.com/KronicDeth/intellij-elixir/pull/3887), [#3917](https://github.com/KronicDeth/intellij-elixir/pull/3917) [@dependabot](https://github.com/dependabot)
  - Bumped `kodein-di-jvm` to 7.33.0 and `gradle-wrapper` to 9.7.1.

## [24.0.1] - 2026-08-09

### Enhancements

- [@sh41](https://github.com/sh41)
  - **The plugin's "What's New" now shows the last six releases instead of the whole history.** Each
    version is listed with its release date, so upgrading after skipping a few releases shows what you
    missed rather than one undifferentiated list.

### Bug Fixes

- [@sh41](https://github.com/sh41)
  - **Adding an Elixir SDK registers it again.** [#3888](https://github.com/KronicDeth/intellij-elixir/issues/3888) -
    the SDK list stayed empty and "Elixir Facet SDK is not defined" persisted, because setting up the
    SDK's paths read the SDK table without a read lock and the resulting error aborted registration
    before the SDK reached the list.
  - **A newly added Elixir SDK is paired with the Erlang SDK you picked,** rather than whichever one
    happened to be registered first. The chosen SDK is not committed until the dialog is applied, and
    the pairing was resolved without consulting the dialog's uncommitted SDKs.
  - **The "Configure from mise" action no longer goes missing from the SDK banner.** The startup scan
    request could be dropped before its collector was listening, leaving the project with no tool
    manager scan at all.
- [#3901](https://github.com/KronicDeth/intellij-elixir/pull/3901) - [@sh41](https://github.com/sh41)
  - **Semantic syntax highlighting now appears in decompiled `.beam` files.** Function names, types and
    the rest were being coloured by the annotators and then discarded before they reached the editor, so
    decompiled code showed only the plain lexer colours. v24.0.0 announced this as working; it was not.
  - **Opening a decompiled `.beam` file no longer reports "PsiFile's context does not match the context
    of the editor",** which reached users as an IDE error report.

### Build / CI

- [@sh41](https://github.com/sh41)
  - `CHANGELOG.md` is now in [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format and is the
    single source of the plugin change notes, rendered by the Gradle Changelog Plugin.
    `resources/META-INF/changelog.html`, which held a separately hand-written copy, is removed - it had
    gone stale, so 24.0.0 shipped with v23.9.0 as its newest section.
  - Every pull request now needs a `CHANGELOG.md` entry, checked by `changelog.yml`: it must land under
    `## [Unreleased]`, be a list item, and use a known group. Apply the `no-changelog` label when there

    is genuinely nothing to record.
  - The Tag Release workflow validates the tag shape against the `prerelease` input, that a release is
    cut from `main`, that the tag is new and increases, and that it matches `pluginVersion`.
  - Plugin verifier reports are now uploaded whenever a verification leg fails, including on pull
    requests. Previously the upload was gated on an input that is always empty for `pull_request`, so
    the reports were unobtainable on exactly the runs that needed them and the evidence had to be dug
    out of a raw job log. Green runs still upload nothing unless a caller asks.
  - Everything scoped to an Elixir/OTP pair is now keyed on the pair: `MIX_HOME` joins `MIX_ARCHIVES`,
    and the quoter's `_build`/`deps` join both. Two OTPs for one Elixir previously shared a build tree
    and overwrote each other, and switching versions re-downloaded hex and rebar. The first build per
    pair after this change reinstalls and rebuilds once.
  - CI now covers OTP 25, and runs a decompiler sweep against OTP 28 for the first time. Two
    `beam.additional` pairs were added, `1.13.4 / 25.3.2.21` and `1.18.4 / 28.4`, chosen to cover OTP
    majors rather than Elixir minors: most of the decompiled surface is Erlang and the BEAM chunk formats
    track OTP. **OTP 25 is now a supported pair** - measured locally at 6 560 tests, 0 failures, so its
    leg is required and any later change that breaks it fails the pipeline. OTP 28 stays informational:
    its 291 failures are all pre-existing Elixir-keyed quoting cases, with no OTP-28 decompiler
    failures.
  - Switching Elixir/OTP versions now re-runs the tests. The versions reach the test JVM as environment
    variables, which Gradle could not see, so `mise use erlang@X elixir@Y` followed by `check` reported
    the *previous* pair's results - the task was up to date, and the build cache would even restore
    those results after a `cleanTest`. Both test tasks now declare the versions as inputs.
  - A transient 5xx from JetBrains' artifact CDN no longer loses a whole test leg. Gradle already treats
    repository server errors as retryable, but its default budget is three attempts over about three
    seconds; `gradle.properties` now allows eight from a 3-second doubling backoff, roughly six minutes,
    for every build - CI and local alike.
  - CI restores the Elixir quoter cache again - it never had. `actions/cache` identifies an entry by key
    *and* by a version hashed from the literal `path:` lines, and the restore and save steps listed
    different paths, so they addressed different entries under one key. Every leg missed, rebuilt its
    quoter dependencies from Hex, then failed to save against the entry an earlier run had left there -
    reported as `Unable to reserve cache`, which reads as a harmless race between legs. Both steps now
    take the path list from one place.
  - The test results comment now leads with failures on the **required** legs, the only figure that
    decides whether a pull request can merge, and reports `tests per leg` in place of a cross-leg
    union that read 12,913 for a run in which no leg ran more than 6,659.

## [24.0.0] - 2026-08-04

### Enhancements

- [#3866](https://github.com/KronicDeth/intellij-elixir/pull/3866) - [@sh41](https://github.com/sh41)
  - **Rename (Shift+F6) now works for functions, `name:arity` pairs, modules, module attributes, types, `@callback`s, protocol functions, and atoms**, propagating across every usage site -- including keyword-key usages like `import ... only:`, `@compile inline:`, `@dialyzer`, and `defoverridable`.
  - **New navigation:** `name: arity` function references support Go-to-Declaration/Find Usages/rename; `@callback`/`@macrocallback` definitions navigate to and from their implementations; protocol functions support Find Usages to call sites; Ctrl-click on a `GenServer.call/cast` or `Process.send`/`send_after` message now jumps to the matching `handle_call`/`handle_cast`/`handle_info` clause.
  - **Type variables** (e.g. `a` in `@type box(a) :: {:box, a}` or `@spec ... when a: term()`) now support Go-to-Declaration, Find Usages, and rename, scoped to the enclosing type/spec.
  - **Decompiled `.beam` files now navigate like source.** Ctrl-click and Find Usages work from and into decompiled definitions and their call/spec sites, and semantic syntax highlighting now runs over decompiled text.
  - **Completion improvements:** function-capture names (`&map_it/0`) filtered by arity; function names inside MFA-style atom references (`{Module, :function, args}`); module-attribute references; rebound/shadowed variables and implicitly-imported `Kernel` names no longer offered twice.
  - **Two new inspections:** "Type variable used once" flags a type/spec variable referenced only once; "Unresolvable type" flags type names in specs that don't resolve to any type definition.
- [#3868](https://github.com/KronicDeth/intellij-elixir/pull/3868) - [@sh41](https://github.com/sh41)
  - **Decompiled BEAM files now show real `@type`/`@typep`/`@opaque` definitions** instead of collapsing them all to `term()`. For example, Erlang's `:queue` now decompiles its opaque type with its real parameters and body.
  - **Map comprehension generators (`m_generate`, OTP 26+) in decompiled code now render as a proper `{key, value} <- map` pattern** instead of an unparseable expression.
- [#3873](https://github.com/KronicDeth/intellij-elixir/pull/3873) - [@sh41](https://github.com/sh41)
  - **Small IDE (RubyMine, PyCharm, etc.) users can now configure Elixir SDKs from mise.** Tool manager support -- including the Settings → Elixir → Tool Managers opt-in page -- is now available in small IDEs, not just IntelliJ IDEA. When mise has an Elixir installed for a module, a **"Configure from mise"** button appears on the "SDK is not defined" editor banner and next to that module's SDK selector in Settings → Elixir. One click creates the Elixir and Erlang SDKs and selects them for that module.
  - **Settings → Elixir now guides SDK setup.** The page explains that SDKs are added on the SDKs / Internal Erlang SDKs child pages and links directly to them. Each module's SDK selector shows a live status line (resolved Elixir and Erlang SDK names, or what is wrong -- no SDK, invalid SDK, missing Erlang SDK), matching the status bar widget's wording. In small IDEs the page now lists **all** modules, including projects originally created in IntelliJ IDEA, which previously had no way to pick a per-module SDK in small IDEs.
- [#3856](https://github.com/KronicDeth/intellij-elixir/pull/3856) - [@sh41](https://github.com/sh41)
  - **Mise users: the plugin now watches your mise config files and re-detects SDKs automatically.** New opt-in per-project settings page (Settings → Elixir → Tool Managers) controls which tool managers are active. Running `mise trust` or editing `.mise.toml` triggers a re-scan without restarting the IDE. Errors like "untrusted config" are now shown in notifications instead of silently ignored.
  - **SDK setup on project open is more reliable.** The initial SDK notification scan now waits for the IDE's internal project model to finish loading, preventing a race where newly registered SDKs could disappear moments after being added.
- [#3846](https://github.com/KronicDeth/intellij-elixir/pull/3846) - [@sh41](https://github.com/sh41)
  - **You'll now be warned if your Elixir SDK was compiled against a different OTP version than your configured Erlang SDK.** Warnings appear in Project Structure (Additional Data panel), the status bar widget balloon, and the "Refresh All Elixir SDK Paths" summary. You can suppress the warning per-SDK if the mismatch is intentional.
- [#3845](https://github.com/KronicDeth/intellij-elixir/pull/3845) - [@sh41](https://github.com/sh41)
  - **SDK version detection is faster and works without a working Elixir installation.** The plugin now reads the version directly from the `elixir.app` file instead of running `elixir --short-version`. SDK names show the OTP major version (e.g. `mise Elixir 1.15.7-otp-26`). Mise detection now honours `.tool-versions` and parent-directory configs via `mise ls --current`.
- [#3851](https://github.com/KronicDeth/intellij-elixir/pull/3851) - [@sh41](https://github.com/sh41)
  - **The External Libraries tree is easier to navigate.** SDK library roots now show their OTP app name (e.g. "phoenix" instead of a raw path), and `.ex` source files appear alongside `.beam` files.
- [#3861](https://github.com/KronicDeth/intellij-elixir/pull/3861) - [@sh41](https://github.com/sh41)
  - **Run/Debug gutter icons now work in WSL-hosted projects.** Icons indicating test status also propagate from individual `test`/`describe` blocks up to the enclosing `describe` and `defmodule`, so you can see the status of the entire group from the gutter. The plugin reads simple `test_load_filters`/`test_paths` from `mix.exs` for correct test file detection.
- [#3858](https://github.com/KronicDeth/intellij-elixir/pull/3858) - [@sh41](https://github.com/sh41)
  - **Autocomplete now includes functions from BEAM-only (Erlang) dependencies.** Decompiled exported functions appear in unqualified completion with `/arity` tail text and a proper icon.
  - **BEAM viewer improvement (View → Tool Windows → BEAM Viewer):** the StrT (string table) tab now shows individual strings with their lengths and auto-sizes columns.
- [#3852](https://github.com/KronicDeth/intellij-elixir/pull/3852) - [@sh41](https://github.com/sh41)
  - **Fewer "unknown AST node" warnings when decompiling OTP 26+ BEAM files.** Map comprehension nodes (`m_generate`, `mc`) introduced in OTP 26 are now decompiled correctly.
- [#3836](https://github.com/KronicDeth/intellij-elixir/pull/3836) - [@joshuataylor](https://github.com/joshuataylor)
  - Mix settings and Mix deps checking reworked, Experimental Settings added, and mise SDK detection improved.
- [#3843](https://github.com/KronicDeth/intellij-elixir/pull/3843) - [@sh41](https://github.com/sh41)
  - **The New Project Wizard now configures the Elixir SDK correctly.** SDK type handling was also split into single-responsibility objects.
- [#3891](https://github.com/KronicDeth/intellij-elixir/pull/3891) - [@makoto-developer](https://github.com/makoto-developer)
  - **README typo fixes** (`Subcription`, `referneced`, `Configuations`, `Wih`) - a first contribution, thank you!
- Elixir settings consolidated -- renamed "Experimental Settings" to "Elixir Settings" and moved all settings into the top-level Elixir configurable (no more separate child page). - [@joshuataylor](https://github.com/joshuataylor)
- Mix deps checker setting -- added an "Enable automatic Mix deps checking" toggle (default enabled) under Elixir Settings. When disabled, no deps check runs on project open or file changes. The checker also skips with a debug log when no Elixir SDK is configured, instead of showing the unhelpful "Mix deps check failed" notification. - [@joshuataylor](https://github.com/joshuataylor)
- Erlang SDK prompt for mise Elixir SDKs -- when adding a mise-detected Elixir SDK without an Erlang SDK registered, a chooser dialog now lists valid mise-installed Erlang SDKs (sorted newest first, broken installations filtered out). The selected Erlang SDK is registered and linked automatically. - [@joshuataylor](https://github.com/joshuataylor)
- Status bar widget -- when no Elixir SDK is configured, the widget popup now shows a "Detected Elixir SDKs" section listing valid mise installations. Clicking one registers the Elixir SDK, prompts for Erlang if needed, and sets it as the project SDK. - [@joshuataylor](https://github.com/joshuataylor)
- When an Elixir SDK is added via Project Structure and no Erlang SDK is explicitly set, `configureInternalErlangSdk` now falls back to any Erlang SDK already registered in `ProjectJdkTable`. - [@joshuataylor](https://github.com/joshuataylor)

### Bug Fixes

- [#3873](https://github.com/KronicDeth/intellij-elixir/pull/3873) - [@sh41](https://github.com/sh41)
  - **The "Setup Elixir Module SDK" / "Setup Elixir Facet SDK" editor banner links now work in small IDEs.** They previously called a Project Structure API that is a silent no-op in RubyMine and other small IDEs; they now open the Elixir SDK settings.
  - **Settings → Elixir → SDKs / Internal Erlang SDKs no longer show an empty list** when SDKs exist. The shared SDK model was initialised without a project, which loads nothing from the SDK table.
  - **Removing an SDK in Settings → Elixir → SDKs no longer throws** and the removed SDK no longer lingers as a "ghost" entry in the per-module SDK chooser.
  - **SDKs registered outside the settings dialog (e.g. via "Configure from mise") now appear in Settings without restarting the IDE.** The settings model refreshes when the SDK table changes.
- [#3849](https://github.com/KronicDeth/intellij-elixir/pull/3849) - [@sh41](https://github.com/sh41)
  - **Ctrl-click on `div`, `rem`, `is_nil` etc. now navigates to Elixir source instead of landing on a `.beam` file.** The resolver now sorts source results before decompiled ones.
  - **`describe`/`test` blocks resolve correctly when using `ExUnit.CaseTemplate`.** Previously these showed `?` icons in the structure view and wouldn't navigate. Now the plugin follows `use MyApp.ConnCase` chains to find the underlying `ExUnit.Case` macros.
  - **Go-to-Declaration is less noisy in multi-module projects.** Resolver scope narrowed from the entire project to the current module's dependencies, reducing false matches from unrelated modules and/or SDKs.
  - **Navigation works from VCS diff views.** Synthetic files (e.g. the diff editor) now resolve to their real on-disk counterparts.
- [#3850](https://github.com/KronicDeth/intellij-elixir/pull/3850) - [@sh41](https://github.com/sh41)
  - **Go-to-Declaration on the `String` module (and other modules with `\u{…}` in their docs) now works.** Previously the lexer mishandled Unicode escape sequences inside `~S"""…"""` heredocs, corrupting the parse tree for the rest of the file and causing navigation to fall through to `.beam`.
- [#3839](https://github.com/KronicDeth/intellij-elixir/pull/3839) - [@sh41](https://github.com/sh41)
  - **IDE no longer freezes when dependencies change.** The dependency sync system has been rewritten from a legacy thread-based watcher to a coroutine pipeline. Changing `mix.lock` now re-scans only the affected project root instead of all roots. Notifications show which module is affected.
- [#3848](https://github.com/KronicDeth/intellij-elixir/pull/3848) - [@sh41](https://github.com/sh41)
  - **Dialyzer and Credo inspections now analyse the latest code.** Open documents are saved before the inspection runs, so on-disk state matches what you see in the editor.
  - **Umbrella sub-app path dependencies are now classified correctly** during mix sync (previously could appear under the wrong module).
  - **Debugger no longer risks a threading crash** when gathering SDK paths at session start.
- [#3855](https://github.com/KronicDeth/intellij-elixir/pull/3855) - [@sh41](https://github.com/sh41)
  - **Run configurations are more stable on 2025.3+.** Internal threading contracts are now enforced on the code paths that build Mix/Elixir/IEx command lines, preventing potential crashes when launching run configs.
- [#3841](https://github.com/KronicDeth/intellij-elixir/pull/3841) - [@sh41](https://github.com/sh41)
  - **Decompiling `.beam` files built by newer OTP no longer fails on unknown instructions.** Adds `TypedRegister` (OTP 25+) and opcodes 177-191 (OTP 25-29) to the BEAM decompiler.
- [#3837](https://github.com/KronicDeth/intellij-elixir/pull/3837) - [@sh41](https://github.com/sh41)
  - **Elixir code blocks inside `@doc`/`@moduledoc` heredocs inject correctly.** Credo and Dialyzer also gained module guards.
- [#3844](https://github.com/KronicDeth/intellij-elixir/pull/3844) - [@sh41](https://github.com/sh41)
  - **An Elixir SDK keeps a stable identity across restarts,** keyed on its Erlang home path rather than a derived variant name.
- [#3834](https://github.com/KronicDeth/intellij-elixir/pull/3834) - [@joshuataylor](https://github.com/joshuataylor)
  - Rethrow `ProcessCanceledException` in spell checking `Splitter` instead of swallowing it.
  - Fix intention preview for `ConvertMatchToTypeOperation`.
- [#3835](https://github.com/KronicDeth/intellij-elixir/pull/3835) - [@joshuataylor](https://github.com/joshuataylor)
  - `ProgressManager.checkCanceled()` added to `while` loops in `@RequiresReadLock` methods.
  - Replace `Dispatchers.EDT` with `Dispatchers.UI` in `ElixirSdkStatusWidget` (EDT holds write-intent lock unnecessarily for pure UI updates).
  - Use no-arg `AnAction` constructor in `RefreshAllElixirSdksAction` to fix DevKit inspection.

### Threading / Platform Hygiene

- *No user-visible behaviour changes. These improve long-term stability and IDE responsiveness.*
- [#3858](https://github.com/KronicDeth/intellij-elixir/pull/3858) - [@sh41](https://github.com/sh41)
  - `@RequiresReadLock` / `@RequiresBackgroundThread` annotations added across PSI, structure view, navigation, SDK, mix, and utility layers.
  - `ProgressManager.checkCanceled()` added in large-set iteration loops (improves cancellation responsiveness during long operations).
  - Numerous deprecated-API replacements for forward compatibility with future IDE versions.
  - Shadowed extension functions renamed to avoid GrammarKit member conflicts.

### Build / CI

- *No user-visible changes. These affect contributors and CI infrastructure only.*
- [#3855](https://github.com/KronicDeth/intellij-elixir/pull/3855) - [@sh41](https://github.com/sh41)
  - GitHub Actions now summarizes the first ≤10 failed tests in the job summary.
  - Reverted to upstream `setup-beam` action (win25 runner issues resolved upstream).
- [#3850](https://github.com/KronicDeth/intellij-elixir/pull/3850), [#3845](https://github.com/KronicDeth/intellij-elixir/pull/3845) - [@sh41](https://github.com/sh41)
  - CONTRIBUTING.md: corrected JFlex regeneration instructions; `ElixirFlexLexer.java` moved from `src/` to `gen/`; documented 253 API constraints for debugger and terminal console.
- [#3863](https://github.com/KronicDeth/intellij-elixir/pull/3863) - [@sh41](https://github.com/sh41)
  - Plugin artifact uploaded unpacked so downloading from GitHub Actions produces a ready-to-install zip without double-wrapping.
- [#3879](https://github.com/KronicDeth/intellij-elixir/pull/3879) - [@sh41](https://github.com/sh41)
  - Consolidated internal usage of `JpsProjectLoadingManager` (SDK-sync API) to a single, documented call site.
- [#3877](https://github.com/KronicDeth/intellij-elixir/pull/3877), [#3878](https://github.com/KronicDeth/intellij-elixir/pull/3878) - [@joshuataylor](https://github.com/joshuataylor)
  - Verified compatibility against IntelliJ IDEA 2026.2 EAP (262.8665.258); dependencies pinned to bundled platform versions.
- [#3875](https://github.com/KronicDeth/intellij-elixir/pull/3875) - [@sh41](https://github.com/sh41)
  - CI test results now published via `workflow_run` instead of the same-run job.
- [#3874](https://github.com/KronicDeth/intellij-elixir/pull/3874), [#3864](https://github.com/KronicDeth/intellij-elixir/pull/3864) - [@sh41](https://github.com/sh41)
  - Routine dependency bumps, including `org.jetbrains.qodana` to 2026.1.3.
- [#3882](https://github.com/KronicDeth/intellij-elixir/pull/3882) - [@sh41](https://github.com/sh41)
  - Plugin verifier: explicitly declare the dependency on `intellij.testRunner.plugin` so verification doesn't fail on its absence.
- [#3847](https://github.com/KronicDeth/intellij-elixir/pull/3847) - [@joshuataylor](https://github.com/joshuataylor)
  - `vendorName` and copyright headers updated to the maintainer's current name.
- [#3854](https://github.com/KronicDeth/intellij-elixir/pull/3854) - [@joshuataylor](https://github.com/joshuataylor)
  - Canary releases built by a single workflow.
- [#3862](https://github.com/KronicDeth/intellij-elixir/pull/3862) - [@sh41](https://github.com/sh41)
  - Dependency and Gradle bumps; added the `dependencyUpdates` task plugin.
- [#3884](https://github.com/KronicDeth/intellij-elixir/pull/3884) - [@sh41](https://github.com/sh41)
  - `setup-env` defaults to the minimum supported platform (2025.3.6).
- [#3886](https://github.com/KronicDeth/intellij-elixir/pull/3886) - [@dependabot](https://github.com/dependabot)
  - Bump `actions/checkout` from 7.0.0 to 7.0.1.
- [#3890](https://github.com/KronicDeth/intellij-elixir/pull/3890) - [@joshuataylor](https://github.com/joshuataylor)
  - Worked around a `publish-unit-test-result-action` failure.
- [#3895](https://github.com/KronicDeth/intellij-elixir/pull/3895) - [@sh41](https://github.com/sh41)
  - Test Results checks no longer fail the run when tests fail.

## [23.8.2] - 2026-05-16

### Enhancements

- [#3822](https://github.com/KronicDeth/intellij-elixir/pull/3822) - [@sh41](https://github.com/sh41)
  - Moved 108 hand-written `.kt`/`.java` files from `gen/` to `src/` -- these files were vulnerable to silent overwrite by parser regeneration. Regenerating `gen/` previously clobbered them with GrammarKit stubs, causing `StackOverflowError` at runtime when hand-written interface names matched BNF rule names (visitor generates self-recursive methods).
  - Renamed 3 PSI interfaces to avoid GrammarKit visitor collisions: `Heredoc` -> `HeredocLiteral`, `HeredocLine` -> `HeredocLineable`, `SigilHeredoc` -> `SigilHeredocLiteral`.
  - Consolidated ambiguous `ElixirPsiImplUtil` overloads (`processDeclarations`, `getNameIdentifier`, `getReference`) using Java 21 pattern matching switch expressions so that hand edits are no longer needed in generated files after parser regeneration.
  - Marked `gen/` as generated sources in Gradle (`idea.module.generatedSourceDirs`) to suppress inspections on GrammarKit-generated PSI classes.
  - `CONTRIBUTING.md` updated with comprehensive GrammarKit usage conventions: rule names vs interface names, visitor collision avoidance, `extends`/`mixin`/`fake` rules, `ElixirPsiImplUtil` method resolution and ambiguity pitfalls, source layout (`gen/` vs `src/`), CRLF conversion, and testing workflow after BNF changes.
- [#3821](https://github.com/KronicDeth/intellij-elixir/pull/3821) - [@sh41](https://github.com/sh41)
  - MFA tuple reference resolution: Go-to-Declaration, Find Usages, and hover documentation now work on `:function` atoms inside `{Module, :function, arity}` MFA tuples. Covers Supervisor child specs, `@doc delegate_to:` attributes, and general MFA references throughout Elixir code.
  - Supports both Elixir modules (`{Enum, :map, 2}`) and Erlang modules (`{:math, :sqrt, 1}`). Arity can be an integer literal, an args list (`[arg1, arg2]`), or a dynamic expression/wildcard (resolves with `isValidResult=false` for navigation support without error highlighting).
  - Implemented via `MfaTupleReferenceContributor` + `MfaTupleReferenceProvider` + `MfaFunctionReference` (poly-variant, uses `ResolveCache`). `ElixirAtomMixin` registered as `HintedReferenceHost` so `PsiReferenceService` picks up contributed references. Soft reference -- unresolvable MFA tuples produce no error highlighting.
  - New `UnresolvableModuleQualifier` inspection (`enabledByDefault=true`, error level) flags unresolvable module qualifiers in qualified calls. Highlights the qualifier itself, not the entire call expression.
  - False-positive guards: dynamic qualifiers (module attributes, variables, function call results, bracket access, chained calls), injected doc code fragments (`@doc`/`@moduledoc` heredocs), non-source roots, Phoenix `Router.Helpers` (verifies parent Router module exists, handles both FQN and aliased forms), and opaque `use` calls where `__using__/1` macro cannot be traced (e.g. `ExUnit.CaseTemplate`).
  - Resolver source-over-decompiled preference consolidated into `Resolver.preferred()`. Previous order: `valid -> same-module`. New order: `valid -> source -> same-module`. Redundant `preferSource()` call removed from `TargetElementEvaluator.getTargetCandidates()`. `Nested.kt` ordering updated to match.
- [#3833](https://github.com/KronicDeth/intellij-elixir/pull/3833) - [@joshuataylor](https://github.com/joshuataylor)
  - Added `@RequiresReadLock` annotations to PSI-accessing methods across 10 files (`CallDefinitionClause`, `Definition`, `Implementation`, `Module`, `Protocol`, `ElixirPsiImplUtil`, `PsiElementImpl`, `PsiNamedElementImpl`, `CallImpl`, `CanonicallyNamedImpl`). Enables the `ThreadingConcurrency` inspection to statically detect callers that don't hold the read lock.

### Bug Fixes

- [#3832](https://github.com/KronicDeth/intellij-elixir/pull/3832) - [@joshuataylor](https://github.com/joshuataylor)
  - `BulkDecompilation` -- wrapped `ModuleManager.modules`, `ModuleRootManager.sdk`/`.contentRoots`, and `ProjectRootManager.projectSdk` access in `readAction {}`. Previously accessed on `Dispatchers.Default` without a read lock, causing crashes on 2025.2+.
  - `DirectoryConfigurator` -- wrapped `ModuleManager.getInstance(otpAppProject).modules` in `ReadAction.nonBlocking` inside `Task.Backgroundable` for newly attached umbrella sub-projects.
- [#3831](https://github.com/KronicDeth/intellij-elixir/pull/3831) - [@joshuataylor](https://github.com/joshuataylor)
  - Dialyzer inspection -- use explicit `runReadAction` blocks around PSI and module-model access instead of `isReadActionNeeded = true`. The previous approach (`isReadActionNeeded = true`) held the read lock for the entire inspection run including `waitFor()` on the Dialyzer process, blocking writes. Now the read lock is acquired only for the PSI/model access windows.
  - Status bar SDK widget -- restructured `updateWidget()` to run `detectSdkStatus()` on the widget's coroutine scope (background thread with read action) and dispatch UI updates via `Dispatchers.EDT`. Previously ran SDK/module-model queries without a read lock, crashing on 2025.2+.
  - Reference search (`ReferencesSearch.java`) -- wrapped PSI access (`Implementation.is`, `Module.is`, `getName`, `getLanguage`) in `ReadAction.nonBlocking().executeSynchronously()`. `processQuery` runs on a pooled thread without a read lock.
  - `OtpApp.kt` -- merged `elixirFile()` and `appList()` PSI traversal into a single read action. Previously `appList()` ran heavy PSI walking (`.modulars()`, `.macroChildCallList()`, `nameArityInterval()`) outside the read action returned by `computeReadAction`.
  - `DepsWatcher` and `DepsCheckerService` -- wrapped `ProjectRootManager`, `ModuleManager`, and `ModuleRootManager` access in `ReadAction.nonBlocking`. These run on `Alarm(POOLED_THREAD)` callbacks without a read lock.
  - Mix `Watcher` -- wrapped `ModuleRootManager.getInstance(module).contentRoots` in `ReadAction.nonBlocking`. Runs inside `Task.Backgroundable` without a read lock.
  - Credo inspection -- wrapped `ModuleManager.getInstance(project).modules` model access in the `workingDirectorySet()` method with a read action. The inspection uses `isReadActionNeeded = false` and module-model access was unprotected.
  - `ReconfigureModuleSetupAction` -- wrapped `ModuleManager.getInstance(project).modules` and `ElixirSdkType.mostSpecificSdk(project)` in `update()` with `ReadAction.nonBlocking`. `ActionUpdateThread.BGT` does not guarantee a read lock since 2024.2.
  - Downgraded non-critical decompiler/documentation-provider errors from `Logger.error()` to `logger.warn()`. `Logger.error()` creates a `Throwable` and shows an IDE error notification in internal mode -- too noisy for situations where code simply doesn't recognise an element (unknown Erlang AST nodes from newer OTP versions, missing decompiled functions, unhandled element types).

### Build / CI

- [#3830](https://github.com/KronicDeth/intellij-elixir/pull/3830) - [@joshuataylor](https://github.com/joshuataylor)
  - Bumped IntelliJ 2026.1.x target to 2026.1.2.
- [#3832](https://github.com/KronicDeth/intellij-elixir/pull/3832) - [@joshuataylor](https://github.com/joshuataylor)
  - Bumped Gradle to 9.5.1.
  - Bumped intellij-platform plugin to 2.16.0.
- [#3831](https://github.com/KronicDeth/intellij-elixir/pull/3831) - [@joshuataylor](https://github.com/joshuataylor)
  - Bumped JetBrains JDK to `jetbrains-21.0.10-b1163.110`.

## [23.5.0] - 2026-05-15

### Enhancements

- [#3817](https://github.com/KronicDeth/intellij-elixir/pull/3817) - [@sh41](https://github.com/sh41)
  - All Elixir settings panels (Credo, Dialyzer, SDKs, Experimental Settings) grouped under a single "Elixir" parent configurable in both full IDEs and small IDEs (RubyMine, PyCharm, etc.). Dropped redundant "Elixir" prefixes from child panel display names.
  - Top-level configurable selection refactored to a service-provider strategy (`TopLevelElixirConfigurableFactory`) with small IDE and rich platform implementations, replacing the `isSmallIde` class detection approach.
  - Settings search indexing via `ElixirSearchableOptionContributor` -- typing "credo", "dialyzer", "elixir", or "liveview" in the Settings search box now surfaces the relevant Elixir settings pages.
  - Credo inspection -- umbrella project support: `resolveCredoWorkingDirectory` walks up from `apps/<app>` content roots to the umbrella root so Credo runs once per umbrella root instead of once per app. Working directories deduplicated by ancestor path.
  - Credo inspection -- execution failure surfacing: failures (missing SDK, missing Credo dependency, compilation errors) now appear as inspection problems on `mix.exs` and as aggregated IDE notifications, instead of being silently dropped.
  - Credo inspection -- partial result preservation: when a Credo run emits some findings before hitting a fatal error, the findings are kept and a warning notification is shown alongside them.
  - Flycheck output parsing extracted and hardened: two-phase approach (record split, then location parse) correctly handles line+column, line-only, and file-level findings. Invalid paths and out-of-range offsets logged at debug level instead of crashing the inspection run.
  - `--mute-exit-status` added to Credo command line so lint-level exit codes are not treated as execution failures.

### Bug Fixes

- [#3817](https://github.com/KronicDeth/intellij-elixir/pull/3817) - [@sh41](https://github.com/sh41)
  - Credo inspection read-action lock churn: consolidated 4-6 separate `runReadAction(Computable { })` calls per output line into a single `runReadAction {}` block per finding. Under 2025.3+ writer-preference locking, the repeated lock acquire/release blocked the EDT when a write action was pending. Partially fixes [#3790](https://github.com/KronicDeth/intellij-elixir/issues/3790).
  - Credo "Configure credo" notification action used internal `ShowSettingsUtilImpl` API -- replaced with `ShowSettingsUtil.getInstance()` and added `project.isDisposed` guard.
  - Credo and Dialyzer configurable IDs (`"Credo"`, `"Dialyzer"`) collided with display names -- separated into distinct `language.elixir.credo` / `language.elixir.dialyzer` IDs.
  - Java-style `//` comment in `plugin.xml` replaced with proper XML `<!-- -->` comment.

## [23.4.0] - 2026-05-15

### Enhancements

- [#3820](https://github.com/KronicDeth/intellij-elixir/pull/3820) - [@sh41](https://github.com/sh41)
  - Status bar SDK widget -- module SDK inconsistency detection: detects dangling SDK references (module `.iml` references an SDK name that no longer exists in the JDK table, e.g. project cloned between WSL distributions) and SDK mismatches (module uses a different SDK than the project SDK). Notifications fire reactively with deduplication and include specific navigation instructions.
  - Status bar SDK widget -- folder mark validation: detects misconfigured source/test/excluded folder marks on Mix project modules. Uses debounced `rootsChanged()` via `MutableSharedFlow` + 2-second debounce to prevent thrashing during bulk operations. Tracks active notifications and expires them when the issue resolves.
  - "Reconfigure Elixir Module Setup" action (`ReconfigureModuleSetupAction`): additively applies canonical folder marks and fixes dangling/mismatched SDK entries. Preserves user-customised source roots, skips non-existent directories and non-Elixir modules. Available via Tools menu and status bar widget popup.
  - `CANONICAL_FOLDER_MARKS` unification: New Project Wizard-created projects now get the full mark set (`lib/`, `web/`, `spec/`, `test/`, plus 7 exclusions). Previously NPW projects missed `spec/` (ESpec), `web/` (pre-Phoenix 1.3), and all exclusions.
  - `ProjectModuleSetupValidator`: inspects every Mix module's content entries against canonical marks and returns a list of discrepancies.
  - Module type utilities: `isElixirModule()` and `getMixContentRoots()` helpers in `ModuleExtensions.kt`.
  - `isSmallIde` detection fix: `ApplicationInfo` product codes instead of class detection (class detection broke in 2026.1).

### Bug Fixes

- [#3820](https://github.com/KronicDeth/intellij-elixir/pull/3820) - [@sh41](https://github.com/sh41)
  - "Run Mix ExUnit" context menu missing on test directories containing non-matching `.ex` files in subdirectories: `containsFileWithSuffix` recursive directory walker returned `false` (stop) on non-matching `ElixirFile` instead of `true` (continue), and the `PsiDirectory` branch propagated that premature stop. Renamed `Finder.kt` -> `ContainsFileWithSuffix.kt`. Fixes [#3804](https://github.com/KronicDeth/intellij-elixir/issues/3804).
  - Replaced deprecated `SystemUtils.isWindows`/`isMac` (Apache Commons) with IntelliJ `SystemInfo`/`OS` utilities across the codebase.
  - Replaced `commons-lang NotImplementedException` with `UnsupportedOperationException` across all uses.
  - Replaced deprecated `SimpleConfigurable.create(Getter)` with `Supplier` overload.
  - `detectSdkVersion` EDT guard: guarded with `runWithModalProgressBlocking` to prevent blocking on the EDT. Possibly addresses [#2980](https://github.com/KronicDeth/intellij-elixir/issues/2980).

## [23.3.0] - 2026-05-15

### Enhancements

- [#3819](https://github.com/KronicDeth/intellij-elixir/pull/3819) - [@sh41](https://github.com/sh41)
  - Erlang external documentation support: parse external `.chunk` files (`<app>/doc/chunks/<module>.chunk`) and normalise doc payload across binary, charlist, and structured-term (`application/erlang+html`) variants. Covers OTP 23, 26, and 27 packaging layouts. BEAM decompilation now loads external chunk docs into generated mirror source.
  - Erlang module resolution via atom qualifier syntax: `:math.sqrt(2)`, `:ets.lookup(table, key)` and similar calls now resolve correctly. `maybeModularNameToModulars()` widened from `Set<Call>` to `Set<PsiNamedElement>` to include BEAM-decompiled `ModuleImpl` instances. Unblocks Go-to-Declaration, Find Usages, and autocomplete for all Erlang modules used with atom qualifier syntax.
  - Syntax highlighting in Quick Documentation code blocks: registered `CodeBlockHtmlProvider` and `CodeFenceHtmlProvider` in `MarkdownFlavourDescriptor`. Indented code blocks default to Elixir; fenced blocks dispatch by language hint with Elixir fallback. `RenderedDocCodeBlockRenderer` applies semantic overlays for alias, function call, macro call, and declaration styling.
  - Erlang atom hover resolution: atom targets routed explicitly in documentation lookup with richer function head presentation using metadata-derived spec signatures.
  - `isDocumentationHost` consolidated into single source of truth in `PsiLanguageInjectionHost`, used by both the injection host and the markdown `Injector`.

### Bug Fixes

- [#3819](https://github.com/KronicDeth/intellij-elixir/pull/3819) - [@sh41](https://github.com/sh41)
  - Quick Documentation (hover/Ctrl+Q) for qualified function calls like `Enum.map(list, fun)` and `GenServer.call(pid, msg)` -- previously showed no docs or fell back to module doc. Four root causes fixed: `ElixirRelativeIdentifier` not forwarded in `getCustomDocumentationElement`; `singleOrNull` returning `null` for multi-clause resolutions (replaced with `firstOrNull`); `filterIsInstance<Call>()` dropping `CallDefinitionImpl` from BEAM-only modules; arity-relaxed fallback not filtering by exact function name. Fixes [#3636](https://github.com/KronicDeth/intellij-elixir/issues/3636).
  - Hover docs for BEAM macros with default arguments (e.g. `Logger.info("hello")`): added `Docs.documentedByNameFallback()` that searches the name's `TreeMap` for the nearest arity. `BeamDocsHelper` now dispatches by `Definition` kind (function vs macro). Possibly addresses [#3650](https://github.com/KronicDeth/intellij-elixir/issues/3650), [#3553](https://github.com/KronicDeth/intellij-elixir/issues/3553), [#3552](https://github.com/KronicDeth/intellij-elixir/issues/3552), [#3324](https://github.com/KronicDeth/intellij-elixir/issues/3324), [#3468](https://github.com/KronicDeth/intellij-elixir/issues/3468), [#2691](https://github.com/KronicDeth/intellij-elixir/issues/2691).
  - Red parser-error squiggles in Elixir code blocks injected into `@doc`/`@moduledoc`/`@typedoc` heredocs suppressed via `DocCodeBlockHighlightErrorFilter` -- documentation snippets are inherently partial and should not show errors.
  - `@delegate_to` doc attributes no longer trigger "Do not know whether to inject Markdown" error log entries.
  - `do`/`end`/`fn` keywords in injected doc code blocks now styled correctly via a new `Keyword` annotator scoped to injected fragments.
  - Erroneous `startInjecting(MarkdownLanguage.INSTANCE)` removed from `injectElixirInCodeBlocksInQuote` -- the function injects Elixir into indented code blocks; starting a competing Markdown injection was incorrect.

## [23.2.0] - 2026-05-15

### Enhancements

- [#3818](https://github.com/KronicDeth/intellij-elixir/pull/3818) - [@sh41](https://github.com/sh41)
  - Code completion deduplication: multi-clause functions (e.g. `Enum.map_every` with 5 clauses) now appear once in completion results instead of once per clause head. Shared `PreferFunctionHead` logic selects bare function heads over implementation clauses.
  - Parameter info deduplication: parameter hints grouped by `(name, arity)` -- separate arities still show distinct hints, but multiple clauses of the same arity no longer produce duplicate entries.
  - Completion prefers source-defined modulars over BEAM stubs when both are available, eliminating duplicate completion entries.
  - Transitive alias resolution: when stub-index lookup finds nothing for a module name that is itself a `QualifiableAlias`, resolution now follows alias chains transitively. Possibly addresses [#1806](https://github.com/KronicDeth/intellij-elixir/issues/1806).
  - `DefinitionsScopedSearch` cancellation: added `ProgressManager.checkCanceled()` at loop boundaries and honour `Processor.process()` return value for early-exit, preventing hangs during large-project searches.

### Bug Fixes

- [#3818](https://github.com/KronicDeth/intellij-elixir/pull/3818) - [@sh41](https://github.com/sh41)
  - **Breaking change**: removed `nameArityInAnyModule` global fallback from resolver. Previously, when `resolveInScope` found no results, the resolver fell back to a global stub-index search returning every function with a matching name from every module (all marked `validResult=false`). This polluted parameter hints with unrelated modules (e.g. hovering `Enum.map()` showed hints from `Stream.Reducers`, `Ecto`, `Phoenix`), caused Go-to-Definition to navigate to wrong-module definitions, and filled the resolution cache with irrelevant results. Calls that were previously "resolved" to functions in unrelated modules will now correctly appear as unresolved references.
  - Infinite loop in `UnaliasedName.up` when resolving `QualifiedMultipleAliases` -- function overload ordering caused mutual recursion.
  - Infinite recursion and NPE prevention in PSI resolve/tree-walk paths via `RecursionManager.doPreventingRecursion()` and null-safe `VISITED_ELEMENT_SET` access in `ResolveState`.
  - `@spec` line marker grouping checked arity equality before name equality -- specs for different functions with the same arity were incorrectly grouped together.
  - Gutter icons anchored to leaf `PsiElement`s per the `LineMarkerProvider` contract. Non-leaf elements caused markers to blink or appear in wrong positions after edits.
  - Removed redundant `computeReadAction`/`runReadAction` wrappers from `CallImpl` getters (`functionName`, `moduleName`, `resolvedPrimaryArity`) and `PsiNamedElementImpl` name getters. These trivial PSI reads were called from paths already holding a read lock; under 2025.3+ writer-preference locking, re-acquiring blocks the EDT when a write action is pending. Partially fixes [#3790](https://github.com/KronicDeth/intellij-elixir/issues/3790).
  - `Elixir.` prefix stripping for module name resolution -- stub index stores names without the prefix, so `Elixir.Enum` lookups now match correctly.

## [23.1.0] - 2026-05-15

### Enhancements

- [#3816](https://github.com/KronicDeth/intellij-elixir/pull/3816) - [@sh41](https://github.com/sh41)
  - Project-scoped coroutine service (`ElixirCoroutineService`) with `supervisedChildScope` for structured, lifecycle-bound concurrency across plugin subsystems.
  - Debugger runtime (Process, Node, MailBox) migrated from unmanaged `executeOnPooledThread` to structured coroutine scopes with cooperative cancellation via `ensureActive()`. Clean `CancellationException` handling prevents false error reports on shutdown.
  - Serialised node-facing debugger operations on a dedicated single-lane `nodeDispatcher`, preventing concurrent BEAM RPC/network calls and preserving operation ordering.
  - WSL debugger source resolution: when a BEAM-reported source path is Linux-style and doesn't resolve locally, converts to Windows UNC via WSL compat and retries. Applied to breakpoint hit navigation, stack frame source mapping, and failed-breakpoint presentation. WSL distribution cached on the debugger process to avoid repeated lookups.
  - `mix format` integration replaced: removed legacy `MixFormatExternalFormatProcessor`, added `MixFormatFormattingService` using `AsyncDocumentFormattingService`. Improved stderr parsing, notification rendering, and error offset navigation.
  - CLI ANSI toggle: added `ansi` parameter to control ANSI escape codes in subprocess output. Disabled for formatter subprocess to keep parsing deterministic.
  - Breakpoint availability hot-path optimisation: added `isInsideModule()` for cheap boolean module-boundary checks, avoiding full module-name assembly. `getModuleName()` traversal now stops at file boundaries to avoid directory traversal.
  - SDK setup modernisation: replaced hand-rolled `invokeAndWait` + boolean-flag pattern in `SdkRegistrar` with `edtWriteAction {}`. `registerOrUpdateErlangSdk` / `registerOrUpdateElixirSdk` are now suspend funs. Replaced `ProgressManager.runProcessWithProgressSynchronously` in `erlang/Type.setupSdkPaths` with `runWithModalProgressBlocking + withContext(Dispatchers.IO)`.
  - Removed unnecessary environment picker complexity from dependent SDK creation flow.
  - Removed blocking pooled-thread wrappers from SDK lookup; tightened read-action/background boundaries.

### Bug Fixes

- [#3816](https://github.com/KronicDeth/intellij-elixir/pull/3816) - [@sh41](https://github.com/sh41)
  - Debugger node network calls dispatched off EDT via serial `nodeDispatcher`, fixing EDT blocking during debug sessions.
  - VFS blocking read in debugger breakpoint availability checks -- `getModuleName()` directory traversal triggered blocking disk reads on the VFS, now stops at file boundaries. Partially fixes [#3790](https://github.com/KronicDeth/intellij-elixir/issues/3790).
  - WSL Linux paths not resolving for debugger source navigation -- breakpoint hits, stack frames, and failed-breakpoint messages now resolve correctly when the BEAM reports Linux-style paths on Windows.
  - Erlang SDK `detectSdkVersion` EDT path guarded with `runWithModalProgressBlocking` to prevent unguarded EDT blocking.
  - Debugger deprecation warning and unused variable warning resolved.

## [23.0.7] - 2026-05-15

### Enhancements

- [#3815](https://github.com/KronicDeth/intellij-elixir/pull/3815) - [@sh41](https://github.com/sh41)
  - Unicode identifier and atom support in the JFlex lexer -- Elixir supports Unicode identifiers (e.g. `def ΦΤ§ do`) and bare Unicode atoms (e.g. `:ΦΤ§`), but the lexer only recognised ASCII characters. Added Unicode letter/digit support using JFlex POSIX character classes.
  - Erlang private functions now decompiled as `defp` instead of `def`, using the BEAM export table to classify functions.
  - Strip `-type`/`-opaque` prefix from Erlang type signatures, mapping to `@typep`/`@opaque`.
  - `Elixir.` prefix stripping for uniform module name resolution -- `Enum.map()` and `Elixir.Enum.map()` now resolve consistently.
  - Type rendering: replaced a `TODO()` crash in `appendTypes()` with actual BEAM doc-chunk signature rendering.
  - Non-blocking bulk decompilation: moved bulk decompile scan off the EDT with run-scoped log deduplication to prevent log floods during library indexing.
  - Improved parse error diagnostics: error reports now include the parser error description and failing source line, with top-10 unique error pattern summary in the bulk-decompile run log.

### Bug Fixes

- [#3815](https://github.com/KronicDeth/intellij-elixir/pull/3815) - [@sh41](https://github.com/sh41)
  - FD leak fix (WSL/IJent): `InputStream` opened per BEAM file was never closed; after ~20K sequential reads during bulk decompilation, IJent exhausted its vsock FD limit, crashed the gRPC connection, and froze the IDE. Wrapped in `use {}` to release FDs immediately. Fixes [#3613](https://github.com/KronicDeth/intellij-elixir/issues/3613).
  - Parenthesise block expressions (`case`/`if`/`try`) and nested binary literals inside binary element type specs -- previously emitted unparseable Elixir. Fixes [#3554](https://github.com/KronicDeth/intellij-elixir/issues/3554), [#3555](https://github.com/KronicDeth/intellij-elixir/issues/3555).
  - Space after word-based unary operators (`not` etc.) to prevent keyword-argument misparse. Fixes [#3556](https://github.com/KronicDeth/intellij-elixir/issues/3556).
  - Sanitise Erlang compiler-generated variable names (e.g. `f@_1`) containing `@` -- invalid in Elixir identifiers -- replacing with `_`. Fixes [#3557](https://github.com/KronicDeth/intellij-elixir/issues/3557).
  - Escape interpolation markers (`#{`) in decompiled atom and string values. Fixes [#3519](https://github.com/KronicDeth/intellij-elixir/issues/3519).
  - Strip `\r` from BEAM documentation chunk strings before inserting into IntelliJ `Document`. Fixes [#3433](https://github.com/KronicDeth/intellij-elixir/issues/3433).
  - Handle `:elixir_erl` Dbgi metadata value `:none` (modules compiled with `debug_info: false`) gracefully instead of logging SEVERE errors. Fixes [#3454](https://github.com/KronicDeth/intellij-elixir/issues/3454).
  - Escape `\u{...}` sequences in decompiled doc strings to prevent Elixir misinterpreting them as Unicode code-point escapes -- was silently breaking the entire `String` module mirror mapping. Fixes [#3412](https://github.com/KronicDeth/intellij-elixir/issues/3412).
  - Emit `def` prefix on overridden `__struct__/1` signatures so PSI mirror mapping resolves arity-1 struct functions. Fixes [#3596](https://github.com/KronicDeth/intellij-elixir/issues/3596).
  - Handle Erlang wildcard variable `_` in `record_field` -- eliminates ~100 SEVERE log entries per bulk decompile run. Fixes [#3403](https://github.com/KronicDeth/intellij-elixir/issues/3403).
  - Fix backslash escaping order: escape `\` before `'` in Erlang charlist rendering. Fixes [#3234](https://github.com/KronicDeth/intellij-elixir/issues/3234).
  - Add `not` to reserved variable keywords. Fixes [#2825](https://github.com/KronicDeth/intellij-elixir/issues/2825).
  - Emit `()` for zero-argument anonymous function clauses. Fixes [#2916](https://github.com/KronicDeth/intellij-elixir/issues/2916).
  - Emit source expression for empty map update associations. Fixes [#2745](https://github.com/KronicDeth/intellij-elixir/issues/2745).
  - Propagate `doBlock` flag through match expressions in comprehension generators. Fixes [#2907](https://github.com/KronicDeth/intellij-elixir/issues/2907), [#2908](https://github.com/KronicDeth/intellij-elixir/issues/2908).
  - Parenthesise nested bitstring generator expressions and wrap non-literal binary element sizes in `size()`. Fixes [#3469](https://github.com/KronicDeth/intellij-elixir/issues/3469), [#3423](https://github.com/KronicDeth/intellij-elixir/issues/3423).
  - Render non-integer binary element type specifier values. Fixes [#3240](https://github.com/KronicDeth/intellij-elixir/issues/3240).
  - Validate alias shape for `Elixir.`-prefixed atoms, falling back to quoted atom form for invalid names. Fixes [#3197](https://github.com/KronicDeth/intellij-elixir/issues/3197), [#3206](https://github.com/KronicDeth/intellij-elixir/issues/3206).
  - Indexing deadlock prevention: `Cache.from(FileContent)` now uses `Beam.from(fileContent)` instead of reopening `VirtualFile.inputStream` on cache misses. Fixes [#2544](https://github.com/KronicDeth/intellij-elixir/issues/2544), [#2333](https://github.com/KronicDeth/intellij-elixir/issues/2333).
  - Escape backslashes in quoted strings. Fixes [#2728](https://github.com/KronicDeth/intellij-elixir/issues/2728), [#2635](https://github.com/KronicDeth/intellij-elixir/issues/2635).
  - Fix decompilation of modules with `Elixir.`-prefixed atoms that contain `do`-`end` operands causing parse cascades. Fixes [#778](https://github.com/KronicDeth/intellij-elixir/issues/778), [#2769](https://github.com/KronicDeth/intellij-elixir/issues/2769), [#3420](https://github.com/KronicDeth/intellij-elixir/issues/3420).

## [23.0.6] - 2026-05-14

### Enhancements

- [#3814](https://github.com/KronicDeth/intellij-elixir/pull/3814) - [@sh41](https://github.com/sh41)
  - 2026.2 compatibility: use deprecated `selectSdkHome` 2-arg form in SDK setup to compile against both 261 and 262 API. **Note**: On WSL, the SDK file chooser now opens at the user home directory instead of the WSL distribution root. This will be restored when 261 support is dropped.

### Bug Fixes

- [#3814](https://github.com/KronicDeth/intellij-elixir/pull/3814) - [@sh41](https://github.com/sh41)
  - Fix unreachable `?: "IU"` fallback for `platformType` Gradle property -- `get()` throws on missing properties, so the elvis operator was dead code. Now uses `getOrElse("IU")`.

### Build / CI

- [#3814](https://github.com/KronicDeth/intellij-elixir/pull/3814) - [@sh41](https://github.com/sh41)
  - CI matrix updated to `windows-2025` runners. IDEA EAP version pinned to `261.24374.34` (Java 25 incompatibility with `LATEST-EAP-SNAPSHOT`).
  - Plugin verifier pinned to 1.384 with markdown/HTML/plain report formats and clickable report output.

## [23.0.5] - 2026-04-28

### Bug Fixes

- Fixed Umbrella project import crash when root folder and child app share the same name (e.g., `emqx/` root with `apps/emqx/` child). The quick import path (`File` -> `Open` on `mix.exs`) bypassed the wizard's duplicate detection, causing `ModuleWithNameAlreadyExists`. Module names are now disambiguated using the relative path (e.g., `emqx` for the root, `emqx-apps-emqx` for the child). - [@joshuataylor](https://github.com/joshuataylor), (Thanks to [@JiaRG](https://github.com/JiaRG) for the thorough and reproducible bug report and excellent example umbrella project!)

## [23.0.4] - 2026-04-26

### Enhancements

- 2026.1 compatibility: remove deprecated `OpenProjectTask.copy()` call in `DirectoryConfigurator`. - [@joshuataylor](https://github.com/joshuataylor)
- 2026.1 compatibility: use `ActionUtil.performAction()` instead of directly invoking `@OverrideOnly` method in `InstallMixDependenciesAction`. - [@joshuataylor](https://github.com/joshuataylor)
- Remove internal API usage: replace `DiagnosticBundle`, `AbstractMessage`, and `PlatformUtils` with public alternatives for plugin verification compatibility. - [@joshuataylor](https://github.com/joshuataylor)

### Build / CI

- Bump Gradle from 9.3.1 to 9.4.1. - [@joshuataylor](https://github.com/joshuataylor)
- Bump kotlinx-coroutines from 1.9.0 to 1.10.2. - [@joshuataylor](https://github.com/joshuataylor)
- Bump gradle-download from 5.6.0 to 5.7.0. - [@joshuataylor](https://github.com/joshuataylor)
- Bump intellij-platform from 2.12.0 to 2.15.0. - [@joshuataylor](https://github.com/joshuataylor)
- Bump Kotlin to 2.3.21. - [@joshuataylor](https://github.com/joshuataylor)

## [23.0.3] - 2026-04-26

### Bug Fixes

- [#3806](https://github.com/KronicDeth/intellij-elixir/pull/3806) - [@sh41](https://github.com/sh41)
  - Fix indexing deadlock by reading BEAM files from `FileContent` instead of reopening `VirtualFile` input stream. Avoids deadlock on WSL/IJ environments during annotation and highlighting passes.
- [#3807](https://github.com/KronicDeth/intellij-elixir/pull/3807) - [@sh41](https://github.com/sh41)
  - Remove stale library roots on dependency re-sync, fixing libraries XML files getting updated with redundant data endlessly. Fixes [#3804](https://github.com/KronicDeth/intellij-elixir/issues/3804).

## [23.0.2] - 2026-04-17

### Enhancements

- [#3763](https://github.com/KronicDeth/intellij-elixir/pull/3763) - [@sh41](https://github.com/sh41)
  - JPS plugin refactor: fix JPS builder classpath (broken since September 2024), move `HomePath` into IDE module, persist Elixir SDK derived data (`mix-home`, `wsl-unc-path`) with a `data-version` marker and migration, and separate JPS builder from shared modules.
- [#3792](https://github.com/KronicDeth/intellij-elixir/pull/3792) - [@sh41](https://github.com/sh41)
  - Mix deps tooling: dedicated `mix deps` status parsing, deps checker service with debounced notifications and install action, SDK creation/registration flow with explicit Erlang dependency wiring, and Mise plugin integration for auto-install/configure of Elixir/Erlang SDKs.
  - Non-blocking write actions in UI and formatter to avoid background/EDT exceptions.
  - Hardened CLI argument construction across Mix/IEx/ESpec/ExUnit configurations.
  - WSL path canonicalization and env var handling improvements.
  - 2026.1 compatibility: ignore `ExperimentalPsiDummyBlock` type in `ElixirDocumentationProvider`.
- [#3791](https://github.com/KronicDeth/intellij-elixir/pull/3791) - [@soomtong](https://github.com/soomtong)
  - Update New Project Wizard to use the new `LanguageGeneratorNewProjectWizard` API with Elixir icon display.
- OTP 28 support: handle `AtU8` atom tables and stubless BEAM fallback. - [@sh41](https://github.com/sh41)
- Handle `@moduledoc`-style `ElixirMatchedAtOperation` in markdown injection. - [@sh41](https://github.com/sh41)
- Handle uncompressed literals and improve error reporting in BEAM `Literals` parsing. - [@sh41](https://github.com/sh41)

### Bug Fixes

- Fix broken IDE navigation by specifying base platform rather than module. - [@sh41](https://github.com/sh41)
- Fix process kill on Windows. - [@sh41](https://github.com/sh41)
- Fix compile server classpath for JPS module jars. - [@sh41](https://github.com/sh41)
- Fix WSL path handling: canonicalize WSL paths, convert only known env vars to WSL paths. - [@sh41](https://github.com/sh41)
- Ensure debugger path is added to code paths if unavailable. - [@sh41](https://github.com/sh41)
- Assert read access in all resolvers to avert deadlocks. - [@sh41](https://github.com/sh41)
- Less aggressive SDK version differentiation in `SdkHomeScan`. - [@sh41](https://github.com/sh41)

### Build / CI

- Bump intellij-platform from 2.11.0 to 2.12.0. - [@joshuataylor](https://github.com/joshuataylor)

## [23.0.1] - 2026-03-06

### Enhancements

- [#3787](https://github.com/KronicDeth/intellij-elixir/pull/3787) - [@joshuataylor](https://github.com/joshuataylor)
  - Remove internal API usage to improve plugin verification compatibility.
- [#3788](https://github.com/KronicDeth/intellij-elixir/pull/3788) - [@joshuataylor](https://github.com/joshuataylor)
  - Add separate GitHub Actions job for tagging/releasing, fixing release being overridden on every merge to main.

## [23.0.0] - 2026-02-09

### Enhancements

- WSL Support - [@sh41](https://github.com/sh41) - see [release announcement](https://github.com/KronicDeth/intellij-elixir/releases/tag/v23.0.0) for full details!
  - Windows Subsystem for Linux support enabling Elixir development in WSL from Windows JetBrains IDEs. Originally submitted as [#3749](https://github.com/KronicDeth/intellij-elixir/pull/3749), broken into 7 stacked PRs for review. Resolves [#1384](https://github.com/KronicDeth/intellij-elixir/issues/1384), [#1911](https://github.com/KronicDeth/intellij-elixir/issues/1911), [#2499](https://github.com/KronicDeth/intellij-elixir/issues/2499), [#3470](https://github.com/KronicDeth/intellij-elixir/issues/3470), [#3674](https://github.com/KronicDeth/intellij-elixir/issues/3674), [#3746](https://github.com/KronicDeth/intellij-elixir/issues/3746). May also help with [#3659](https://github.com/KronicDeth/intellij-elixir/issues/3659), [#3716](https://github.com/KronicDeth/intellij-elixir/issues/3716), [#3715](https://github.com/KronicDeth/intellij-elixir/issues/3715). See the [README WSL section](https://github.com/KronicDeth/intellij-elixir?tab=readme-ov-file#windows-subsystem-for-linux-wsl-support) for setup instructions.
  - SDK discovery for Elixir and Erlang inside WSL distributions (asdf, mise, kerl, kiex, Homebrew, Nix).
  - WSL path conversion supporting both `\\wsl$\` and `\\wsl.localhost\` UNC path formats.
  - All run configurations work with WSL: Mix tasks, IEx, ExUnit, ESpec, Distillery, external tools.
  - WSL-safe process handling with graceful BEAM termination across the WSL boundary.
  - WSL-aware SDK naming in UI and status bar widget.
  - SDK settings improvements for small-IDE compatibility (RubyMine, WebStorm, etc.).
- [#3753](https://github.com/KronicDeth/intellij-elixir/pull/3753) - [@sh41](https://github.com/sh41)
  - Windows development infrastructure: platform-aware build services, Gradle tasks, CI caching (~4.5 GB fork-safe cache), and updated CONTRIBUTING.md.
- [#3757](https://github.com/KronicDeth/intellij-elixir/pull/3757) - [@sh41](https://github.com/sh41)
  - Mix dependency health checks on project open with install action and notifications via shared MixTaskRunner.
  - Faster and more robust Mix project import with pre-scan of OTP apps off EDT.
- [#3759](https://github.com/KronicDeth/intellij-elixir/pull/3759) - [@sh41](https://github.com/sh41)
  - IntelliJ IDE Starter-based UI test infrastructure for automated IDE testing.

### Bug Fixes

- [#3761](https://github.com/KronicDeth/intellij-elixir/pull/3761) - [@sh41](https://github.com/sh41)
  - Fix deadlock when `/deps` directory is deleted while the deps watcher is active.
  - Fix JPS Builder module name handling (broken since September 2024).
- [#3765](https://github.com/KronicDeth/intellij-elixir/pull/3765) - [@sh41](https://github.com/sh41)
  - Fix false inspection warnings in `~r` regex sigils containing interpolated variables.

## [22.0.0] - 2025-12-16

### Breaking changes

- This release supports 2025.3+ IDEs only (253.xxx).

### Enhancements

- [#3696](https://github.com/KronicDeth/intellij-elixir/pull/3696) - [@mwnciau](https://github.com/mwnciau)
  - HEEx support with `.heex` file type recognition, syntax highlighting for `{@assigns}` and `{expressions}`, relative component support (`<.component>` tags), and CSS/JavaScript injection in `<style>` and `<script>` tags.
- Language Injection for literal sigils (`~H`, `~r`, etc.) - [@polymorfiq](https://github.com/polymorfiq)
- [#3711](https://github.com/KronicDeth/intellij-elixir/pull/3711) - [@joshuataylor](https://github.com/joshuataylor)
  - Status Bar Widget showing the current project's Elixir SDK version.
  - "Refresh Elixir SDK Classpaths" action to fix SDK classpath issues.
- Compatibility fixes for 2025.1+ (understatement of changes, countless amount of changes to help improve things, thank you so micj) - [@sh41](https://github.com/sh41)
- Extensive EDT threading fixes for 2025.2+ compatibility.

## [21.0.0] - 2025-05-17

### Enhancements

- [#3651](https://github.com/KronicDeth/intellij-elixir/pull/3681) - [@joshuataylor](https://github.com/joshuataylor)
  - Support Jetbrains 2025.1 and relax until-build.

## [20.0.1] - 2024-11-29

### Bug Fixes

- [#3651](https://github.com/KronicDeth/intellij-elixir/pull/3667) - [@joshuataylor](https://github.com/joshuataylor)
  - Support Webstorm 2024.3 (243.21565.180). Thanks to [@Kae-Tempest](https://github.com/Kae-Tempest) for the report!

## [20.0.0] - 2024-11-14

### Enhancements

- [#3651](https://github.com/KronicDeth/intellij-elixir/pull/3651) - [@joshuataylor](https://github.com/joshuataylor)
  - Support Jetbrains 2024.3 (243.21565.193)

## [19.0.1] - 2024-08-20

### Enhancements

- [#3639](https://github.com/KronicDeth/intellij-elixir/pull/3639) - [@joshuataylor](https://github.com/joshuataylor)
  - Support JetBrains Exception Analyzer
- [#3640](https://github.com/KronicDeth/intellij-elixir/pull/3640) - [@joshuataylor](https://github.com/joshuataylor)
  - Bump intellij platform gradle to 2.0.1 and IdeaVIM to 2.16.0
- [#3643](https://github.com/KronicDeth/intellij-elixir/pull/3643) - [@joshuataylor](https://github.com/joshuataylor)
  - Fix RubyMine freezing for umbrella projects but showing the new project wizard as a temporary workaround.

## [19.0.0] - 2024-08-14

### Breaking changes

- [#3619](https://github.com/KronicDeth/intellij-elixir/pull/3619) - [@joshuataylor](https://github.com/joshuataylor)
  - Drop support for < 2024.2 IDEs

### Enhancements

- [#3619](https://github.com/KronicDeth/intellij-elixir/pull/3619) - [@joshuataylor](https://github.com/joshuataylor)
  - Support IntelliJ Gradle Plugin 2.0

## [18.0.1] - 2024-08-05

### Enhancements

- [#3582](https://github.com/KronicDeth/intellij-elixir/pull/3582) - [@rNoz](https://github.com/rNoz)
  - Enabling proper code generation for comments (Comment with Line/Block Comment)

## [18.0.0] - 2024-08-03

### Breaking changes

- [#3569](https://github.com/KronicDeth/intellij-elixir/pull/3569) - [@ashleysommer](https://github.com/ashleysommer), [@joshuataylor](https://github.com/joshuataylor)
  - Drop support for < 2024.1 IDEs.

## [17.0.1] - 2023-12-24

### Bug Fixes

- [#3491](https://github.com/KronicDeth/intellij-elixir/pull/3491) - [@neominik](https://github.com/neominik)
  - Render code snippets that are not links.
- [#3562](https://github.com/KronicDeth/intellij-elixir/pull/3562) - [@ashleysommer](https://github.com/ashleysommer)
  - Add the correct ERL and elixir arguments for starting IEx depending on the version of Elixir SDK.
- [#3563](https://github.com/KronicDeth/intellij-elixir/pull/3563) - [@ashleysommer](https://github.com/ashleysommer)
  - Bundle latest OtpErlang.jar from JInterface v1.14 for OTP v26.

## [17.0.0] - 2024-01-11

### Breaking changes

- [#3500](https://github.com/KronicDeth/intellij-elixir/pull/3500) - [@KronicDeth](https://github.com/KronicDeth)
  - Drop support for <= 2023.2 IDEs.

### Enhancements

- [#2402](https://github.com/KronicDeth/intellij-elixir/pull/3402) - [@joshuataylor](https://github.com/joshuataylor)
  - Support 2023.3 IDEs.

### Bug Fixes

- [#3431](https://github.com/KronicDeth/intellij-elixir/pull/3431) - [@KronicDeth](https://github.com/KronicDeth)
  - Add `displayName` in `plugin.xml` for configurables for faster menu loading.
    - `org.elixir_lang.facet.configurable.Project` - "Elixir"
    - `org.elixir_lang.facets.sdks.erlang.Configurable` - "Internal Erlang SDKs"
    - `org.elixir_lang.facets.sdks.elixir.Configurable` - "SDKs"

## [16.0.0] - 2023-09-12

- The [CHANGELOG for v15](https://github.com/KronicDeth/intellij-elixir/blob/v15.1.0/CHANGELOG.md) can be found in [the v16.0.0 tag](https://github.com/KronicDeth/intellij-elixir/tree/v16.0.0).

[Unreleased]: https://github.com/KronicDeth/intellij-elixir//compare/v24.0.1...HEAD
[24.0.1]: https://github.com/KronicDeth/intellij-elixir//compare/v24.0.0...v24.0.1
[24.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v23.8.2...v24.0.0
[23.8.2]: https://github.com/KronicDeth/intellij-elixir//compare/v23.5.0...v23.8.2
[23.5.0]: https://github.com/KronicDeth/intellij-elixir//compare/v23.4.0...v23.5.0
[23.4.0]: https://github.com/KronicDeth/intellij-elixir//compare/v23.3.0...v23.4.0
[23.3.0]: https://github.com/KronicDeth/intellij-elixir//compare/v23.2.0...v23.3.0
[23.2.0]: https://github.com/KronicDeth/intellij-elixir//compare/v23.1.0...v23.2.0
[23.1.0]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.7...v23.1.0
[23.0.7]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.6...v23.0.7
[23.0.6]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.5...v23.0.6
[23.0.5]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.4...v23.0.5
[23.0.4]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.3...v23.0.4
[23.0.3]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.2...v23.0.3
[23.0.2]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.1...v23.0.2
[23.0.1]: https://github.com/KronicDeth/intellij-elixir//compare/v23.0.0...v23.0.1
[23.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v22.0.0...v23.0.0
[22.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v21.0.0...v22.0.0
[21.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v20.0.1...v21.0.0
[20.0.1]: https://github.com/KronicDeth/intellij-elixir//compare/v20.0.0...v20.0.1
[20.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v19.0.1...v20.0.0
[19.0.1]: https://github.com/KronicDeth/intellij-elixir//compare/v19.0.0...v19.0.1
[19.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v18.0.1...v19.0.0
[18.0.1]: https://github.com/KronicDeth/intellij-elixir//compare/v18.0.0...v18.0.1
[18.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v17.0.1...v18.0.0
[17.0.1]: https://github.com/KronicDeth/intellij-elixir//compare/v17.0.0...v17.0.1
[17.0.0]: https://github.com/KronicDeth/intellij-elixir//compare/v16.0.0...v17.0.0
[16.0.0]: https://github.com/KronicDeth/intellij-elixir//commits/v16.0.0
