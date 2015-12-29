<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Upgrading](#upgrading)
  - [v2.0.0](#v200)
    - [Preferences/Settings](#preferencessettings)
    - [Quick Fix](#quick-fix)
  - [v1.0.0](#v100)
  - [v0.3.1](#v031)
  - [v0.3.0](#v030)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Upgrading

## v2.0.0

### Preferences/Settings

The fallback key attribute is `DefaultLanguageHighlighterColors.FUNCTION_CALL`, which is uncolored in most themes, so
users need to customize their themes to see the highlighting for "Kernel Functions", "Kernel Macros", and
"Kernel.SpecialForms Macros".

### Quick Fix

The "Add Newline" and "Add Semicolon" Quick Fixes have been removed as the parsing elements they depend on have been
removed in favor of JetBrains' OpenAPI error handling.  If your workflow depended on these quick fixes you will have to
manually add newlines or semicolons now.

## v1.0.0

New Elixir File has moved to the last item in the New File menu to preserve `CTRL+N ENTER` keyboard shortcut for
`New > File`, so if you got used to using `CTRL+N ENTER` for New Elixir File you'll have to learn the new position at
the bottom of the menu.

## v0.3.1

**NOTE: The Project Structure Detector is just bookkeeping and visual now.  No other features currently take advantage
of the SDK setting or the marked directories.  Theses Project Settings will be used for later features. **

To take advantage of the new Project Structure Detector in IntelliJ, you will want to recreate any project you
previously opened as an Empty Project.

(Copied from Elixir Plugin > Features > Project > From Existing Directory in README.md)
  
1. File > New > Project From Existing Sources...
2. Select the root directory of your project.
3. Leave the default selection, "Create project from existing sources"
4. Click Next
5. Project name will be filled with the basename of the root directory.  Customize it if you like.
6. Project location will be the root directory.
7. Click Next.
8. If you previously opened the directory in IntelliJ or another JetBrains IDE, you'll be prompted to overwrite the
   .idea directory.  Click Yes.
9. You'll be prompted with a list of detected Elixir project roots to add to the project.  Each root contains a
   `mix.exs`.  Uncheck any project roots that you don't want added.
10. Click Next.      
10. Select a Project SDK directory by clicking Configure.
11. The plugin will automatically find the newest version of Elixir installed. (**NOTE: SDK detection only works for
    homebrew installs on OSX.  [Open an issue](https://github.com/KronicDeth/intellij-elixir/issues) with information
    about Elixir install locations on your operating system and package manager to have SDK detection added for it.**)
12. If the automatic detection doesn't find your Elixir SDK or you want to use an older version, manually select select
    the directory above the `bin` directory containing `elixir`, `elixirc`, `iex`, and `mix`.
13. Click Next after you select SDK name from the Project SDK list.
14. Click Finish on the framework page.  (*No framework detection is implemented yet for Elixir.*)
15. Choose whether to open in a New Window or in This Window.

Alternatively, you can manually mark the directories and setup the Elixir SDK.

## v0.3.0

The prior version of New > Elixir File validated that the input name was a valid path and would only allow lowercase
names, so you'd be forced to set the name to `foo`, which would produce a `foo.ex` file, but `foo` would also be used
in the file contents:

```elixir
defmodule foo do

end
```

In v0.3.0, the validator was corrected so that it only allows Alias (with and without `.`), so instead of entering the
name as `foo`, enter it as `Foo`.  The file will still be named `foo.ex`, but the module name will correctly be `Foo`
in the file contents:

```elixir
defmodule Foo do
end
```
