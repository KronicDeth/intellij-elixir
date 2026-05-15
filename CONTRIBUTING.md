<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Contributing](#contributing)
  - [Development](#development)
    - [Importing the project](#importing-the-project)
    - [Building and running](#building-and-running)
    - [Windows Development Setup](#windows-development-setup)
      - [Prerequisites](#prerequisites)
      - [Building on Windows](#building-on-windows)
      - [Platform Support](#platform-support)
      - [Troubleshooting](#troubleshooting)
    - [From command line](#from-command-line)
      - [Testing in other IDEs](#testing-in-other-ides)
      - [Running the latest EAP snapshot](#running-the-latest-eap-snapshot)
      - [Testing](#testing)
    - [From IntelliJ IDEA](#from-intellij-idea)
      - [Running the plugin in a specific IDE](#running-the-plugin-in-a-specific-ide)
      - [Verification](#verification)
    - [Color Schemes](#color-schemes)
      - [Customizing Scheme](#customizing-scheme)
      - [Exporting Settings](#exporting-settings)
      - [Unpack Settings](#unpack-settings)
      - [Convert ICLS to Additional Text Attributes format](#convert-icls-to-additional-text-attributes-format)
      - [Add Additional Text Attributes to plugin](#add-additional-text-attributes-to-plugin)
  - [Building](#building)
    - [Documentation](#documentation)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Contributing

## Development

### Importing the project

1. Import the project via `File > New > Project from Existing Sources...`  OR from the `Import Project` option of the splash menu
2. Select the checkout of `intellij-elixir` directory
3. Select "Import project from external model" from the radio button group
4. Select Gradle from the external models
5. Click Next
6. In "Import Project from Gradle"
   1. Check "Use auto-import"
   2. Check "Create separate module per source set"
   3. Ensure Gradle JVM is **AT LEAST** Java 21+.
   Your import settings should look something like this:<br/>
   ![Gradle settings](/screenshots/contributing/gradle_settings.png?raw=true "Gradle settings")
   4. Click Finish
7. When the "Gradle Project Data to Import" dialog pops up
   1. Leave "Elixir (root module), ":jps-builder", and "jps-shared" checked.<br/>
   ![Select modules](/screenshots/contributing/select_modules.png?raw=true "Select modules")
   2. Click OK
8. When the "Import Gradle Projects" dialog pops up
   1. Leave "intellij-elixir" checked.  You can remove the old main module:<br/>
     ![Remove module](/screenshots/contributing/remove_module.png?raw=true "Remove module")
9. Install [Kotlin Plugin](https://plugins.jetbrains.com/plugin/6954-kotlin)

### Building and running

Gradle will handle all dependency management, including fetching the Intellij IDEA platform specified in `gradle.properties`, so you can use a normal JDK instead of setting up an "Intellij Platform Plugin SDK".

**NOTE:** The tests require Erlang, Elixir, and Hex installed. The `jps-builder:test` task discovers your Erlang installation by running `erl -eval 'io:format("~s", [code:root_dir()]).'` - ensure `erl` (or `erl.exe` on Windows) is on your PATH. You can set `ERLANG_SDK_HOME` and `ELIXIR_EBIN_DIRECTORY` environment variables to override auto-detection. You can run individual JUnit tests via the JUnit run configuration type, but note that some tests require additional setup which the Gradle `test` task does for you, and these tests won't work when run outside Gradle without some additional work. See `build.gradle.kts` for details.

**NOTE:** If you're having trouble running the plugin against Intellij IDEA 14.1 on Mac, see this [comment](https://github.com/KronicDeth/intellij-elixir/pull/504#issuecomment-284275036).

### Windows Development Setup

#### Prerequisites
- **Build Environment**: Git Bash, MSYS2, WSL, or native Windows
- **Erlang/OTP**: **Required for running tests** (not just building)
  - Download from: https://www.erlang.org/downloads (OTP 24.3+ recommended)
  - Or via Chocolatey: `choco install erlang`
  - **IMPORTANT**: Must be on PATH (`erl.exe` command must work)
  - Tests will auto-detect Erlang installation via `erl -eval` command
  - Alternative: Set `ERLANG_SDK_HOME` environment variable to override auto-detection
- **Elixir**: Required for running tests
  - Download from: https://elixir-lang.org/install.html#windows
  - Or via Chocolatey: `choco install elixir`
  - Or use GitHub Actions approach with `erlef/setup-beam` (installs both Erlang and Elixir)
- **Make**: Required for building Elixir from source
  - Via Chocolatey: `choco install make`
  - Via Git Bash: Included by default
  - Via MSYS2: `pacman -S make`
- **Java 21+**: JetBrains Runtime or any Java 21+ distribution

#### Building on Windows
```bash
# Full build with tests
./gradlew test

# Build plugin only (no tests)
./gradlew buildPlugin
```

#### Platform Support
The build system automatically detects your platform:
- **Windows**: Uses `.bat` executables for Elixir commands, ProcessBuilder for daemon management
- **Linux/macOS**: Uses standard executables, native daemon support

#### Troubleshooting

**Quoter fails to start on Windows:**
- Check that no antivirus is blocking the process
- Verify no orphaned Erlang processes: `tasklist | findstr erl`
- Kill orphaned processes: `taskkill /F /IM erl.exe`

**Elixir build fails:**
- Ensure `make` is on PATH: `make --version`
- Try running in Git Bash (not PowerShell/cmd)

**getQuoterDeps fails in PowerShell (erl not found):**
- Run the task from Git Bash so `/usr/bin/sh` sees a full PATH.
- Example:
  - `cd /c/Users/user/IdeaProjects/intellij-elixir`
  - `./gradlew.bat getQuoterDeps --rerun-tasks --no-build-cache --no-configuration-cache`
- If needed, add Erlang to Git Bash PATH, e.g. `export PATH="/c/Program Files/erl-24.3.4.6/bin:$PATH"`.

**Path with spaces warning:**
- Cosmetic only - Erlang installed in "Program Files" shows warnings but works correctly

**kerl build fails on Linux as of May 2026**

If you see this:
```sh
beam/dist.c:5678:15: error: two or more data types in declaration specifiers
 5678 |         Eterm bool = ((monitor_oflags & ERTS_ML_FLG_SPAWN_MONITOR)
                       ^~~~
```

As the project still uses OTP 24, which uses a local variable named bool in beam/dist.c, which was legal C in 2022... but is not legal C now, because GCC 15 switched its default C standard to C23, which makes bool a reserved built-in type name. ArchLinux (btw) ships GCC 16.1.1, Deian 13 seems to be 14.2.0.

You can work around this by forcing an older C standard, for example:

```sh
export CC=gcc
export CFLAGS="-O2 -g -std=gnu17 -Wno-error -fcommon"
export CPPFLAGS="-D_FORTIFY_SOURCE=0"
export KERL_CONFIGURE_OPTIONS="--disable-debug --without-odbc --without-wx"
unset KERL_USE_AUTOCONF
```

### From command line
> Let's assume that you don't have much knowledge of the Java/Kotlin ecosystem, and just want to help contribute some changes.
> Here are some tips on how make your changes and test them without hopefully too much fuss,

To launch an IDE, you can use [runIde](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-tasks.html#runIde),
which will launch IntelliJ IDEA:

```sh
./gradlew runIntellijIdea
```

#### Testing in other IDEs

> [!IMPORTANT]
> In the 2025 release, JetBrains "unified" the Professional and Community editions of IntelliJ IDEA into a single edition.
> See [The Unified IntelliJ IDEA: More Free Features, a Better Experience, Smoother Flow](https://blog.jetbrains.com/idea/2025/12/intellij-idea-unified-release/)
> Other IDEs, such as [PyCharm](https://www.jetbrains.com/help/pycharm/unified-pycharm.html) was also unified into a single edition.

To test another IDE, you can use one of the following tasks:

- `runIntellijIdea` / `runIntellijIdeaEAP`
- `runRubyMine` / `runRubyMineEAP`
- `runWebStorm` / `runWebStormEAP`
- `runPyCharm` / `runPyCharmEAP`
- `runRustRover` / `runRustRoverEAP`
- `runCLion` / `runCLionEAP`
- `runGoLand` / `runGoLandEAP`

#### Running the latest EAP snapshot

To use `runIde` or `runPyCharm` etc with the latest EAP snapshot, you need to pass the `useDynamicEapVersion` gradle property
when running the task (or Run Configuration in IntelliJ IDEA).

For example, to launch the latest RubyMine EAP:

```sh
./gradlew runRubyMineEAP -PuseDynamicEapVersion=true
```

#### Testing

To run all tests `./gradlew test`.

To run a specific test:
```sh
./gradlew test --tests "org.elixir_lang.psi.operation.PrefixTest"
```

To build (so you get a .zip file):
```sh
./gradlew build
# Or without tests
./gradlew build -x test
```

### From IntelliJ IDEA
#### Running the plugin in a specific IDE
1. Open the Gradle Tool Window (`View > Tool Windows > Gradle` OR from the Gradle button on the right tool button bar)
2. Expand `Elixir (root) > Tasks > intellij platform`
3. Double click `runIntelliJCommunity` (or which IDE you want to test with)
4. Now the Run Configuration will be selected, and you can click the green arrow at the top of the screen.

#### Verification
1. Expand `verification`
2. Double click `test`

### GrammarKit (Parser / PSI Generation)

The Elixir parser and PSI element classes in `gen/` are generated from `src/org/elixir_lang/Elixir.bnf` using the [GrammarKit](https://github.com/JetBrains/Grammar-Kit) plugin. If you modify the `.bnf` file (e.g. adding a `mixin`, changing a rule, or adding a new production), you must regenerate the parser code.

#### Prerequisites
- Install the **GrammarKit** plugin in IntelliJ IDEA (Settings → Plugins → search "Grammar-Kit").

#### Regenerating Parser Code
1. Open `src/org/elixir_lang/Elixir.bnf` in the editor.
2. Right-click inside the file → **Generate Parser Code**.
3. The generator writes updated files into the `gen/` directory.

#### Fixing CRLF Line Endings (Windows)

The GrammarKit generator writes files with CRLF line endings, but the repository uses LF. After regenerating, convert line endings from Git Bash:

```bash
cd /c/Users/steve/IdeaProjects/intellij-elixir/intellij-elixir
find gen -type f | xargs dos2unix.exe
```

Then review the actual changes:
```bash
git diff --stat -- gen/
```

Many `gen/` files may show whitespace-only or formatting diffs from a generator version difference - these can be included in your commit or excluded as appropriate.

#### Source Layout: `gen/` vs `src/`

The `gen/` directory must contain **only** GrammarKit/JFlex-generated files. All hand-written code lives in `src/`. Both directories are source roots with the same package structure (`org.elixir_lang`), so files can be moved between them without changing imports.

Generated files have a header comment: `// This is a generated file. Not intended for manual editing.` (GrammarKit) or `/* The following code was generated by JFlex */` (JFlex). If you need to add hand-written code that lives alongside generated PSI classes (e.g. a new `PsiScopeProcessor`, stub type, or operation interface), place it in `src/` under the matching package path.

#### Key BNF Concepts

**Rule names vs interface names:** GrammarKit generates PSI classes named after the BNF **rule** (e.g. rule `heredoc` → `ElixirHeredoc`). The `implements` attribute on a rule specifies the hand-written **interface** the generated class implements. These are independent - do not confuse them.

**Visitor method generation:** For each rule, GrammarKit generates a `visitRuleName(ElixirRuleName)` method in `ElixirVisitor`. For each interface in `implements`, it generates a `visitInterfaceName(InterfaceName)` bridge method. If a rule name and an interface name (after stripping packages) are identical, the visitor generates a self-recursive method - causing a `StackOverflowError` at runtime.

**Example of the collision:**
```
// BAD: rule "heredoc" implements interface "Heredoc" - visitor generates:
//   visitHeredoc(ElixirHeredoc) { visitHeredoc(this); }  ← infinite recursion!

// GOOD: rule "heredoc" implements interface "HeredocLiteral" - visitor generates:
//   visitHeredoc(ElixirHeredoc) { visitHeredocLiteral(this); }  ← safe dispatch
```

**Resolution:** When adding a new `implements` interface to a rule, ensure the interface's simple name does not match any BNF rule name. If it would collide, rename the interface (e.g. `Heredoc` → `HeredocLiteral`) or the rule.

**`extends` attribute:** Causes the child rule's generated interface to extend the parent rule's interface, AND collapses AST nodes. Use it for expression hierarchies where shallow AST is desired. Do **not** use it solely for visitor type compatibility - it changes the PSI tree shape and will break parsing tests that compare golden `.txt` files.

**`mixin` attribute:** Specifies a hand-written base class for the generated `*Impl` class. Use this to add custom behaviour (e.g. implementing `HintedReferenceHost`) without editing generated code. The mixin class must extend `ASTWrapperPsiElement` (or the appropriate stub base class) and live in `src/`.

**`fake` rules:** Define PSI interfaces and visitor methods without affecting the parser. Useful for creating intermediate types in the PSI hierarchy (e.g. `fake binary_expr ::= expr+` to group add/mul expressions under a common interface).

#### `ElixirPsiImplUtil` Method Resolution

GrammarKit resolves `methods=[...]` declarations by searching `psiImplUtilClass` (`ElixirPsiImplUtil`) for static methods whose first parameter type matches the rule's generated interface or one of its `implements` interfaces. It tries types in order: the concrete generated type first, then each `implements` interface.

**Ambiguity pitfall:** If a generated class implements two interfaces `A` and `B`, and `ElixirPsiImplUtil` has both `foo(A)` and `foo(B)`, the Java compiler reports an ambiguous call even though GrammarKit only emits a call to one. The fix is to consolidate the overloads:
- **Remove the more-specific overload** and fold it into the broader one using `instanceof`, OR
- **Make one interface extend the other** so Java's overload resolution picks the more-specific type

#### Testing After BNF Changes

After regenerating parser code:
1. **Compile first** - fix any ambiguity or type errors in `ElixirPsiImplUtil` before running tests.
2. **Run all parsing tests** - BNF changes often affect golden `.txt` files under `testData/`. If the PSI tree shape is intentionally unchanged but element type names changed (e.g. from a rename), bulk-update the golden files:
   ```bash
   find testData -name '*.txt' -exec grep -l oldName {} \; | xargs sed -i 's/oldName/newName/g'
   ```
3. **Run annotator/inspection tests** - these exercise the visitor and may hit `StackOverflowError` if a visitor collision exists.
4. **Check `base.txt`** - the plugin verifier baseline file also contains PSI element names.

#### Suppressing Warnings in the BNF

**`//noinspection BnfResolve` for JFlex tokens:** GrammarKit warns `Unresolved rule reference` for tokens that are defined by JFlex (e.g. `pin = DO`), not by BNF rules. These are false positives - suppress them with a comment on the line above:
```bnf
//noinspection BnfResolve
pin = DO
```

**`gen/` inspection suppression:** The build script marks `gen/` as generated sources via `idea.module.generatedSourceDirs` so that IntelliJ suppresses inspections (e.g. unused imports) on GrammarKit-generated code. There is no GrammarKit configuration to control which imports the generator emits.

### Color Schemes

JetBrains plugins are able to set the text attribute values for `TextAttributeKey`s that are unique to the plugin by using `additionalTextAttributes` entries in `src/META-INF/plugin.xml`.  If you have a Color Scheme for Elixir you like, you can propose it as the default for a named theme by extracting the `additionTextAttributes` `file` from an Exported Settings `.jar`.

#### Customizing Scheme

1. Preferences > Editor > Colors & Fonts > Elixir
2. Customize the colors
3. Click "Save As" to name the Scheme (`My $SCHEME_NAME`)_

#### Exporting Settings

1. File > Export Settings
2. Click "Select None"
3. Check "Editor Colors"
4. Change the "Export settings to:" path to a place you can easily access it in the terminal
5. Click "OK"

#### Unpack Settings

1. `mkdir settings`
2. `cd settings`
3. `jar xf $SAVE_DIRECTORY/settings.jar`

#### Convert ICLS to Additional Text Attributes format

1. `mv colors/colors/My\ $SCHEME_NAME.icls $INTELLIJ_ELIXIR/colorSchemes/ElixirSCHEME_NAME.xml` (`$SCHEME_NAME` will be `Default`, `Darcula` or another shared theme name.)
2. Remove all elements except for `scheme attributes`.
3. Remove the outer `scheme` tag
4. Rename the `attributes` tag to `list`.
5. Add `<?xml version='1.0'?>` to the top of the file

#### Add Additional Text Attributes to plugin

1. In `plugin.xml` inside the `idea-plugin extensions[defaultExtensionNs="com.intellij"]` tag, add a new additionalTextAttribute tag: `<additionalTextAttributes file="colorSchemes/Elixir$SCHEME_NAME.xml" scheme="SCHEME_NAME"/>`

## Building

### Documentation

The documentation files ([`CHANGELOG.md`](CHANGELOG.md), [`CONTRIBUTING.md`](CONTRIBUTING.md), [`README.md`](README.md),
and [`UPGRADING.md`](UPGRADING.md)) all have table of contents generated by
[`doctoc`](https://github.com/thlorenz/doctoc).

Install `doctoc` (globally) using `npm`

```sh
npm install -g doctoc
```

Then regenerate the table of contents using `doctoc`

```sh
doctoc .
```
