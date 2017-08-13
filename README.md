<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Elixir plugin](#elixir-plugin)
  - [Features](#features)
    - [Project](#project)
      - [New](#new)
      - [From Existing Sources](#from-existing-sources)
        - [Create project from existing sources](#create-project-from-existing-sources)
        - [Import project from external model](#import-project-from-external-model)
    - [Project Structure](#project-structure)
    - [Project Settings](#project-settings)
    - [Module Settings](#module-settings)
      - [Sources](#sources)
      - [Paths](#paths)
      - [Dependencies](#dependencies)
    - [New Elixir File](#new-elixir-file)
      - [Empty module](#empty-module)
      - [Elixir Application](#elixir-application)
      - [Elixir Supervisor](#elixir-supervisor)
      - [Elixir GenServer](#elixir-genserver)
      - [Elixir GenEvent](#elixir-genevent)
    - [Syntax Highlighting and Semantic Annotation](#syntax-highlighting-and-semantic-annotation)
    - [Grammar parsing](#grammar-parsing)
    - [Inspections](#inspections)
      - [Ambiguous nested calls](#ambiguous-nested-calls)
      - [Ambiguous parentheses](#ambiguous-parentheses)
        - [Empty Parentheses](#empty-parentheses)
        - [Keywords in Parentheses](#keywords-in-parentheses)
        - [Positional arguments in Parentheses](#positional-arguments-in-parentheses)
      - [Keyword pair colon (`:`) used in type spec instead of type operator (`::`)](#keyword-pair-colon--used-in-type-spec-instead-of-type-operator-)
      - [Keywords appear before the end of list.](#keywords-appear-before-the-end-of-list)
      - [Match operator (`=`) used in type spec instead of type operator (`::`)](#match-operator--used-in-type-spec-instead-of-type-operator-)
    - [Quick Fixes](#quick-fixes)
      - [Convert `:` to ` ::` in type specs](#convert--to---in-type-specs)
      - [Convert `=` to ` ::` in type specs](#convert--to---in-type-specs)
      - [Remove space in front of ambiguous parentheses](#remove-space-in-front-of-ambiguous-parentheses)
    - [Code Folding](#code-folding)
      - [Controls](#controls)
        - [Collapsing](#collapsing)
        - [Expanding](#expanding)
      - [Regions](#regions)
    - [Commenter](#commenter)
    - [Debugger](#debugger)
      - [Steps](#steps)
      - [Basics](#basics)
        - [Keyboard Shortcuts](#keyboard-shortcuts)
        - [Environment Variables](#environment-variables)
      - [Breakpoints](#breakpoints)
        - [Accessing Breakpoint Properties](#accessing-breakpoint-properties)
          - [Viewing all breakpoints](#viewing-all-breakpoints)
          - [Viewing a single breakpoint](#viewing-a-single-breakpoint)
        - [Configuring Breakpoints](#configuring-breakpoints)
        - [Creating Line Breakpoints](#creating-line-breakpoints)
        - [Describing Line Breakpoints](#describing-line-breakpoints)
        - [Searching for Line Breakpoints](#searching-for-line-breakpoints)
        - [Jump to Breakpoint Source](#jump-to-breakpoint-source)
        - [Disabling Line Breakpoints](#disabling-line-breakpoints)
        - [Deleting Line Breakpoints](#deleting-line-breakpoints)
      - [Starting the Debugger Session](#starting-the-debugger-session)
      - [Examining Suspended Program](#examining-suspended-program)
        - [Processes](#processes)
        - [Frames](#frames)
          - [Jump to Current Execution Point](#jump-to-current-execution-point)
        - [Variables](#variables)
      - [Stepping](#stepping)
    - [Delimiters](#delimiters)
      - [Auto-inserting](#auto-inserting)
      - [Matching](#matching)
    - [Building/Compiling](#buildingcompiling)
      - [Settings](#settings)
      - [Individual File](#individual-file)
      - [Project](#project-1)
    - [Live Templates](#live-templates)
    - [Run Configurations](#run-configurations)
      - [Mix Tasks](#mix-tasks)
      - [`mix test`](#mix-test)
        - [Creating `mix test` Run Configurations Manually](#creating-mix-test-run-configurations-manually)
        - [Creating `mix test` Run Configurations from context](#creating-mix-test-run-configurations-from-context)
          - [Creating/Running `mix test` Run Configurations from directory](#creatingrunning-mix-test-run-configurations-from-directory)
          - [Creating/Running `mix test` Run Configurations from file](#creatingrunning-mix-test-run-configurations-from-file)
          - [Creating/Running `mix test` Run Configurations from line](#creatingrunning-mix-test-run-configurations-from-line)
    - [Completion](#completion)
      - [Aliases and Modules](#aliases-and-modules)
        - [Aliases inside `{ }`](#aliases-inside--)
      - [Function and Macro Calls](#function-and-macro-calls)
        - [Qualified](#qualified)
        - [Unqualified](#unqualified)
      - [Module Attributes](#module-attributes)
      - [Parameters and Variables](#parameters-and-variables)
    - [Decompilation](#decompilation)
    - [Go To Declaration](#go-to-declaration)
      - [Alias](#alias)
      - [Function or Macro](#function-or-macro)
        - [Imported Functions or Macros](#imported-functions-or-macros)
        - [Local Functions or Macros](#local-functions-or-macros)
        - [Remote Functions or Macros](#remote-functions-or-macros)
      - [Module](#module)
      - [Module Attribute](#module-attribute)
      - [Parameters and Variables](#parameters-and-variables-1)
    - [Formatting](#formatting)
      - [Directory](#directory)
      - [File](#file)
        - [Other File](#other-file)
        - [Current File](#current-file)
      - [Selection](#selection)
    - [Go To Symbol](#go-to-symbol)
    - [Go To Test](#go-to-test)
    - [Go To Test Subject](#go-to-test-subject)
    - [Find Usage](#find-usage)
      - [Module](#module-1)
      - [Module Attribute](#module-attribute-1)
      - [Parameters and Variables](#parameters-and-variables-2)
    - [Refactor](#refactor)
      - [Rename](#rename)
        - [Module Attribute](#module-attribute-2)
        - [Parameters and Variables](#parameters-and-variables-3)
    - [Structure](#structure)
      - [Viewing Structure](#viewing-structure)
      - [Buttons](#buttons)
        - [Sorters](#sorters)
        - [Providers](#providers)
        - [Expanders](#expanders)
        - [Autoscrollers](#autoscrollers)
      - [Elements](#elements)
        - [Icons](#icons)
          - [Time](#time)
          - [Visibility](#visibility)
        - [Call to Element](#call-to-element)
  - [Viewing Embedded Elixir Templates](#viewing-embedded-elixir-templates)
  - [Installation](#installation)
    - [Inside IDE using JetBrains repository](#inside-ide-using-jetbrains-repository)
    - [Inside IDE using Github releases](#inside-ide-using-github-releases)
      - [In browser](#in-browser)
      - [In IDE](#in-ide)
  - [Screenshots](#screenshots)
  - [Error reporting](#error-reporting)
  - [Donations](#donations)
    - [Public Donors](#public-donors)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Elixir plugin

[![Build Status](https://travis-ci.org/KronicDeth/intellij-elixir.svg?branch=master)](https://travis-ci.org/KronicDeth/intellij-elixir)

This is a plugin that adds support for [Elixir](http://elixir-lang.org/) to JetBrains IntelliJ IDEA platform IDEs
([DataGrip](http://www.jetbrains.com/datagrip/), [AppCode](http://www.jetbrains.com/objc/),
[IntelliJ IDEA](http://www.jetbrains.com/idea/), [PHPStorm](http://www.jetbrains.com/phpstorm/),
[PyCharm](http://www.jetbrains.com/pycharm/), [Rubymine](http://www.jetbrains.com/ruby/),
[WebStorm](http://www.jetbrains.com/webstorm/)).

It works with the free,
[open source](https://github.com/JetBrains/intellij-community) Community edition of IntelliJ IDEA in addition to the
paid JetBrains IDEs like Ultimate edition of IntelliJ.  No feature is locked to a the paid version of the IDEs, but
the plugin works best in IntelliJ because only IntelliJ supports projects with different languages than the default
(Java for IntelliJ, Ruby for Rubymine, etc).

The plugin itself is free.  Once you have your IDE of choice installed, you can [install this plugin](#installation)

## Features

### Project
**NOTE: This feature only works in IntelliJ IDEA as it depends on an extension point unavailable in language-specific
  IDEs, like Rubymine.**

#### New

If you want to create a basic (non-`mix`) Elixir project with a `lib` directory, perform the following steps.

1. File > New > Project
   ![File > New > Project](/screenshots/features/project/New.png?raw=true "New Project")
2. Select Elixir from the project type menu on the left
3. Click Next
   ![File > New > Project > Elixir](/screenshots/features/project/new/Elixir.png?raw=true "New Elixir Project")
4. Select a Project SDK directory by clicking Configure.
   ![Project SDK](/screenshots/features/project/SDK.png?raw=true "Project SDK")
4. Select a Project SDK directory by clicking Configure.
5. The plugin will automatically find the newest version of Elixir installed.
   * macOS / OSX
     * Homebrew (`/usr/local/Cellar/elixir`)
     * Nix (`/nix/store`)
   * Linux
     * `/usr/local/lib/elixir`
     * Nix and NixOS (`/nix/store`)
   * Windows
     * 32-bit (`C:\Program Files\Elixir`)
     * 64-bit (`C:\Program Files (x86)\Elixir`)
     * (**NOTE: SDK detection only works for
   [Open an issue](https://github.com/KronicDeth/intellij-elixir/issues) with information about Elixir install locations on your operating system and package manager to have SDK detection added for it.
6. If the automatic detection doesn't find your Elixir SDK or you want to use an older version, manually select select the directory above the `bin` directory containing `elixir`, `elixirc`, `iex`, and `mix`.  If the `bin`, `lib,` or `src` directory is incorrectly selected, it will be corrected to the parent directory.
7. Click Next after you select SDK name from the Project SDK list.
8. Change the `Project name` to the name your want for the project
   ![File > New > Project > Settings](/screenshots/features/project/new/Settings.png?raw=true "New Elixir Project Settings")
9. (Optionally) change the `Project location` if the directory does not match what you want
10. (Optionally) expand `More Settings` to change the `Module name`, `Content root`, `Module file location`, and/or `Project format`.  The defaults derived from the `Project name` and `Project location` should work for most projects.
11. Click Finish
12. Choose whether to open in a New Window or in This Window.
    ![File > New > Project > Window](/screenshots/features/project/new/Settings.png?raw=true "Open Project in New Window or This Window")

#### From Existing Sources

##### Create project from existing sources
If you've already created a (non-`mix`) project, you can load it as an Elixir project into the plugin.

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
    Linux, homebrew installs on OSX, and Windows.  [Open an issue](https://github.com/KronicDeth/intellij-elixir/issues)
    with information about Elixir install locations on your operating system and package manager to have SDK detection
    added for it.**)
12. If the automatic detection doesn't find your Elixir SDK or you want to use an older version, manually select select
    the directory above the `bin` directory containing `elixir`, `elixirc`, `iex`, and `mix`.
13. Click Next after you select SDK name from the Project SDK list.
14. Click Finish on the framework page.  (*No framework detection is implemented yet for Elixir.*)
15. Choose whether to open in a New Window or in This Window.

##### Import project from external model
If you've already created a `mix` project, you can load it as an Elixir project into the plugin.

1. File > New > Project From Existing Sources...
2. Select the root directory of your project.
3. Select "Import project from external model"
4. Select Mix
   ![File > New Project > From Existing Sources > Import project from external model > Mix](/screenshots/features/project/from_existing_sources/import_project_from_external_model/Mix.png?raw=true "Import Mix Project")
5. Click Next
6. The "Mix project root" will be filled in with the selected directory.
7. (Optional) Uncheck "Fetch dependencies with mix" if you don't want to run `mix deps.get` when importing the project
8. Ensure the correct "Mix Path" is detected.  On Windows, the `mix.bat`, such as
   `C:\Program Files (x86)\Elixir\bin\mix.bat` should be used instead of the `mix` file without the extension.
9. Ensure the "Mix Version" is as expected.  The number in parentheses should match the Elixir version.
10. Click Next
11. All directories with `mix.exs` files will be selected as "Mix projects to import".  To import just the main project and not its dependencies, click Unselect All.
12. Check the box next to the project root to use only its `mix.exs`.  (It will likely be the first checkbox at the top.)
13. Click Next
14. Select a Project SDK directory by clicking Configure.
15. The plugin will automatically find the newest version of Elixir installed. (**NOTE: SDK detection only works for
    Linux, homebrew installs on OSX, and Windows.  [Open an issue](https://github.com/KronicDeth/intellij-elixir/issues)
    with information about Elixir install locations on your operating system and package manager to have SDK detection
    added for it.**)
16. If the automatic detection doesn't find your Elixir SDK or you want to use an older version, manually select select
    the directory above the `bin` directory containing `elixir`, `elixirc`, `iex`, and `mix`. (On Windows it is the
    directory containing `elixir.bat`, `elixirc.bat`, `iex.bat`, and `mix.bat`.)
17. Click Finish after you select SDK name from the Project SDK list.

### Project Structure

![Project View](/screenshots/Project%20View.png?raw=true "Project View")

* Excluded
  * `_build` (Output from `mix`)
  * `rel` (Output from `exrm`)
* Sources
  * `lib`
* Test Sources
  * `test`

### Project Settings

![Project Settings](/screenshots/project_settings/Project.png?raw=true "Project Settings")

The Project Settings include
* Project Name
* Project SDK

### Module Settings

#### Sources

![Module Settings > Sources](/screenshots/project_settings/module/Sources.png?raw=true "Module Sources")

The Module Settings include Marking directories as
* Excluded
* Sources
* Tests

#### Paths

![Module Settings > Paths](/screenshots/project_settings/module/Paths.png?raw=true "Module Paths")

Module paths list the output directories when compiling code in the module.  There is a an "Output path" for `dev`
`MIX_ENV` and "Test output path" for the `test` `MIX_ENV`.

#### Dependencies

![Module Settings > Dependencies](/screenshots/project_settings/module/Dependencies.png?raw=true "Module Dependencies")

Module dependencies are currently just the SDK and the sources for the module.  Dependencies in `deps` are not
automatically detected at this time.

### New Elixir File

1. Right-click a directory (such as `lib` or `test` in the standard `mix new` layout)
2. Select New > Elixir File.
   ![New > Elixir File](/screenshots/features/new/Elixir%20File.png?raw=true "New Elixir File")
3. Enter an Alias for the Module name, such as `MyModule` or `MyNamespace.MyModule`.
4. Select a Kind of Elixir File to use a different template.
   ![New > Elixir File > Kind](/screenshots/features/new/elixir_file/Kind.png?raw=true "New Elixir File Kind")

#### Empty module

An underscored file will be created in an underscored directory `lib/my_namespace/my_module.ex`) with the given module
name with be created:

```elixir
defmodule MyNamespace.MyModule do
  @moduledoc false

end
```

#### Elixir Application

An underscored file will be created in an underscored directory `lib/my_namespace/my_module.ex`) with the given module
name with be created. It will have a `start/2` function that calls `MyNamespace.MyModule.Supervisor.start_link/0`.

```elixir
defmodule MyNamespace.MyModule do
  @moduledoc false

  use Application

  def start(_type, _args) do
    MyNamespace.MyModule.Supervisor.start_link()
  end
end
```

#### Elixir Supervisor

An underscored file will be created in an underscored directory `lib/my_namespace/my_module.ex`) with the given module
name with be created. It will have a `start_link/1` function that calls `Supervisor.start_link/0` and `init/1` that sets
up the child specs.  It assumes a `MyWorker` child that should be supervised `:one_for_one`.

```elixir
defmodule MyNamespace.MyModule.Supervisor do
  @moduledoc false

  use Supervisor

  def start_link(arg) do
    Supervisor.start_link(__MODULE__, arg)
  end

  def init(arg) do
    children = [
      worker(MyWorker, [arg], restart: :temporary)
    ]

    supervise(children, strategy: :one_for_one)
  end
end
```

#### Elixir GenServer

An underscored file will be created in an underscored directory `lib/my_namespace/my_module.ex`) with the given module
name with be created. It will have a `start_link/2` function that calls `GenServer.start_link/3` and the minimal
callback implementations for `init/1`, `handle_call/3`, and `handle_cast/2`.

The Elixir `use GenServer` supplies these callbacks, so this template is for when you want to change the callbacks, but
would like the stubs to get started without having to look them up in the documentation.

```elixir
defmodule MyNamespace.MyModule do
  @moduledoc false

  use GenServer

  def start_link(state, opts) do
    GenServer.start_link(__MODULE__, state, opts)
  end

  def init(_opts) do
    {:ok, %{}}
  end

  def handle_call(_msg, _from, state) do
    {:reply, :ok, state}
  end

  def handle_cast(_msg, state) do
    {:noreply, state}
  end
end
```

#### Elixir GenEvent

An underscored file will be created in an underscored directory `lib/my_namespace/my_module.ex`) with the given module
name with be created.  The minimal callback implementations for `init/1`, `handle_event/2`, and `handle_call/2`,
`handle_info/2`.

The Elixir `use GenEvent` supplies these callbacks, so this template is for when you want to change the callbacks, but
would like the stubs to get started without having to look them up in the documentation.

```elixir
defmodule MyNamespace.MyModule do
  @moduledoc false

  use GenEvent

  # Callbacks

  def init(_opts) do
    {:ok, %{}}
  end

  def handle_event(_msg, state) do
    {:ok, state}
  end

  def handle_call(_msg, state) do
    {:ok, :ok, state}
  end

  def handle_info(_msg, state) do
    {:ok, state}
  end
end
```

### Syntax Highlighting and Semantic Annotation

Syntax highlighting of lexer tokens and semantic annotating of parser elements can be customized in in the Color Settings page for Elixir (Preferences > Editor > Color & Fonts > Elixir).

<table>
  <thead>
    <tr>
      <th colspan="3" rowspan="2">Text Attribute Key Display Name</th>
      <th rowspan="2">Tokens/Elements</th>
      <th colspan="2">Scheme</th>
    </tr>
    <tr>
      <th>Default</th>
      <th>Darcula</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Alias</td>
      <td></td>
      <td></td>
      <td><code>String</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Alias.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Alias.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Atom</td>
      <td></td>
      <td></td>
      <td>
        <ul>
          <li><code>:one</code></li>
          <li><code>&lt;&lt;&gt;&gt;:</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Atom.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Atom.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Bit</td>
      <td></td>
      <td>
        <ul>
          <li><code>&lt;&lt;</code></li>
          <li><code>&gt;&gt</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Bit.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Bit.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Braces</td>
      <td></td>
      <td>
        <ul>
          <li><code>{</code></li>
          <li><code>}</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Braces.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Braces.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Brackets</td>
      <td></td>
      <td>
        <ul>
          <li><code>[</code></li>
          <li><code>]</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Brackets.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Brackets.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Character Token</td>
      <td></td>
      <td><code>?</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Character%20Token.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Character%20Token.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Comma</td>
      <td></td>
      <td><code>,</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Comma.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Comma.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Dot</td>
      <td></td>
      <td><code>.</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Dot.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Dot.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Interpolation</td>
      <td></td>
      <td>
        <ul>
          <li><code>#{</code></li>
          <li><code>}</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Interpolation.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Interpolation.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Maps and Structs</td>
      <td>Maps</td>
      <td>
        <ul>
          <li><code>%{</code></li>
          <li><code>}</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Maps%20and%20Structs/Maps.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Maps%20and%20Structs/Maps.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Maps and Structs</td>
      <td>Maps</td>
      <td>
        <ul>
          <li><code>%</code></li>
          <li><code>{</code></li>
          <li><code>}</code></li>
        </ul></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Maps%20and%20Structs/Structs.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Maps%20and%20Structs/Structs.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Operation Sign</td>
      <td></td>
      <td>
        <ul>
          <li><code>=</code></li>
          <li><code>+</code></li>
          <li><code>*</code></li>
          <li><code>==</code></li>
          <li><code>!</code></li>
          <li><code>&&</code></li>
          <li><code>||</code></li>
          <li><code>|&gt;</code></li>
          <li><code>^</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Operation%20Sign.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Operation%20Sign.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Parentheses</td>
      <td></td>
      <td>
        <ul>
          <li><code>(</code></li>
          <li><code>)</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Parentheses.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Parentheses.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Braces and Operators</td>
      <td>Semicolon</td>
      <td></td>
      <td><code>;</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Braces%20and%20Operators/Semicolon.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Braces%20and%20Operators/Semicolon.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Calls</td>
      <td>Function</td>
      <td></td>
      <td><code>inspect</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Calls/Function.png?raw=true"/>*Only the Italic attribute
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Calls/Function.png?raw=true"/>*Only the Italic attribute
      </td>
    </tr>
    <tr>
      <td>Calls</td>
      <td>Macro</td>
      <td></td>
      <td><code>inspect</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Calls/Macro.png?raw=true"/>*Only the Bold and Italic attributes
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Calls/Macro.png?raw=true"/>*Only the Bold and Italic attributes
      </td>
    </tr>
    <tr>
      <td>Calls</td>
      <td>Predefined</td>
      <td></td>
      <td>
        <ul>
          <li>
            <code>Kernel</code>
            <ul>
              <li>functions</li>
              <li>macros</li>
            </ul>
          </li>
          <li>
            <code>Kernel.SpecialForms</code>
            <ul>
              <li>macros</li>
            </ul>
          </li>
        <ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Calls/Predefined.png?raw=true"/>*Only the Foreground attribute
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Calls/Predefined.png?raw=true"/>*Only the Foreground attribute
      </td>
    </tr>
    <tr>
      <td>Comment</td>
      <td></td>
      <td></td>
      <td><code># Numbers</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Comment.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Comment.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Keywords</td>
      <td></td>
      <td></td>
      <td><code>end</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Keywords.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Keywords.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Module Attributes</td>
      <td></td>
      <td></td>
      <td><code>@custom_attr</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Module%20Attributes.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Module%20Attributes.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Module Attributes</td>
      <td>Documentation</td>
      <td></td>
      <td><code>@doc</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Module%20Attributes/Documentation.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Module%20Attributes/Documentation.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Module Attributes</td>
      <td>Documentation</td>
      <td>Text</td>
      <td><code>Simple module docstring</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Module%20Attributes/Documentation/Text.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Module%20Attributes/Documentation/Text.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Module Attributes</td>
      <td>Types</td>
      <td>Callback</td>
      <td><code>func</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Module%20Attributes/Types/Callback.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Module%20Attributes/Types/Callback.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Module Attributes</td>
      <td>Types</td>
      <td>Specification</td>
      <td><code>func</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Module%20Attributes/Types/Specification.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Module%20Attributes/Types/Specification.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Module Attributes</td>
      <td>Types</td>
      <td>Type</td>
      <td><code>parameterized</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Module%20Attributes/Types/Type.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Module%20Attributes/Types/Type.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Module Attributes</td>
      <td>Types</td>
      <td>Type Parameter</td>
      <td><code>type_parameter</code></td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Module%20Attributes/Types/Type%20Parameter.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Module%20Attributes/Types/Type%20Parameter.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Numbers</td>
      <td>Base Prefix</td>
      <td>Non-Decimal</td>
      <td>
        <ul>
          <li><code>0b</code></li>
          <li><code>0x</code></li>
          <li><code>0o</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Numbers/Base%20Prefix/Non-Decimal.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Numbers/Base%20Prefix/Non-Decimal.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Numbers</td>
      <td>Base Prefix</td>
      <td>Obsolete Non-Decimal</td>
      <td>
        <ul>
          <li><code>0B</code></li>
          <li><code>0X</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Numbers/Base%20Prefix/Obsolete%20Non-Decimal.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Numbers/Base%20Prefix/Obsolete%20Non-Decimal.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Numbers</td>
      <td>Decimal Exponent, Mark, and Separator</td>
      <td></td>
      <td>
        <ul>
          <li><code>e</code></li>
          <li><code>.</code></li>
          <li><code>_</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Numbers/Decimal%20Exponent%2C%20Mark%2C%20and%20Separator.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Numbers/Decimal%20Exponent%2C%20Mark%2C%20and%20Separator.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Numbers</td>
      <td>Digits</td>
      <td>Invalid</td>
      <td>
        <ul>
          <li><code>2</code></li>
          <li><code>o</code></li>
          <li><code>r</code></li>
          <li><code>888</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Numbers/Digits/Invalid.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Numbers/Digits/Invalid.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Numbers</td>
      <td>Digits</td>
      <td>Valid</td>
      <td>
        <ul>
          <li><code>1234</code></li>
          <li><code>1A</code></li>
          <li><code>beef</code></li>
          <li><code>123</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Numbers/Digits/Valid.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Numbers/Digits/Valid.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Textual</td>
      <td>Character List</td>
      <td></td>
      <td>
        <code>'This is a list'</code>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Textual/Character%20List.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Textual/Character%20List.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Textual</td>
      <td>Escape Sequence</td>
      <td></td>
      <td>
        <code>\x{12}</code>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Textual/Escape%20Sequence.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Textual/Escape%20Sequence.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Textual</td>
      <td>Sigil</td>
      <td></td>
      <td>
        <ul>
          <li><code>~r//</code></li>
          <li><code>~R''</code></li>
          <li><code>~w()</code></li>
          <li><code>~W()</code></li>
        </ul>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Textual/Sigil.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Textual/Sigil.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Textual</td>
      <td>String</td>
      <td></td>
      <td>
        <code>"Hello world"</code>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Textual/String.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Textual/String.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Variables</td>
      <td>Ignored</td>
      <td></td>
      <td>
        <code>_</code>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Variables/Ignored.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Variables/Ignored.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Variables</td>
      <td>Parameter</td>
      <td></td>
      <td>
        <ul>
          <li><code>a</code></li>
          <li><code>b</code></li>
        </ul>

      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Variables/Parameter.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Variables/Parameter.png?raw=true"/>
      </td>
    </tr>
    <tr>
      <td>Variables</td>
      <td>Variable</td>
      <td></td>
      <td>
        <code>pid</code>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/default/Variables/Variable.png?raw=true"/>
      </td>
      <td>
        <img src="screenshots/preferences/editor/colors_and_fonts/darcula/Variables/Variable.png?raw=true"/>
      </td>
    </tr>
  </tbody>
</table>

### Grammar parsing

Built on top of highlighted tokens above, the parser understands the following parts of Elixir grammar as valid or
allows the grammar because they contain correctable errors:

* [Empty Parentheses](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L299) (`()`)
* [Keyword Lists](http://elixir-lang.org/getting_started/7.html#7.1-keyword-lists)
  * Keyword Keys - Aliases, identifiers, quotes, or operators when followed immediately by a colon and horizontal or vertical space.
  * Keyword Values - Empty parentheses (`()`) and matched expressions.
* [Matched Expressions](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L113-L122),
  in other words, unary and binary operations on variable, function, and macro names and values (numbers, strings,
  char lists, sigils, heredocs, `true`, `false`, and `nil`).
* [No Parentheses expressions](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L124-L125), which
  are function calls with neither parentheses nor `do` blocks that have either (1) a positional argument and keyword
  arguments OR (2) two or more positional arguments with optional keyword arguments.
* Anonymous function calls `.()` with either no arguments; a no parentheses arguments expression as an argument; keywords
  as an argument; positional argument(s); or positional arguments followed by keywords as arguments.
* Remote function calls (`Alias.function`, `:atom.function`, etc) and local function calls (`function`) with...
  * No Parentheses with...
    * No Arguments (`Alias.function`)
    * Keywords (`Alias.function key: value`)
    * Nested No Parentheses Call (`Alias.function Inner.function positional, key: value`)
    * Positional and Keyword arguments (`Alias.function positional, key: value`)
    * Matched Expression (`Alias.function 1 + 2`)
  * Parentheses with...
    * No arguments (`Alias.function()`)
    * No Parentheses Call (`Alias.function(Inner.function positional, key: value`)
    * Keywords (`Alias.function(key: value)`)
    * Positional and Keyword arguments (`Alias.function(positional, key: value)`)
    * Trailing parentheses for quoting (`def unquote(variable)(positional)`)
* Bracket expression (`variable[key]`)
* Block expressions (`function do end`)
* [Unmatched expressions](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b//lib/elixir/src/elixir_parser.yrl#L127-L133),
  in other words combinations of block expressions and matched expressions.

### Inspections

Inspections mark sections of code with warnings and errors.  They can be customized from the Preferences > Inspections > Elixir.

![Elixir Inspections](/screenshots/inspection/Elixir.png?raw=true "Elixir Inspections")

#### Ambiguous nested calls

Detects when compiler will throw `unexpected comma. Parentheses are required to solve ambiguity in nested calls`.
Function calls with multiple arguments without parentheses cannot take as arguments functions with multiple arguments
without parentheses because which functional gets which arguments is unclear as in the following example:

```elixir
outer_function first_outer_argument,
               # second argument is another function call without parentheses, but with multiple arguments
               inner_function first_inner_argument,
               ambiguous_keyword_key: ambiguous_keyword_value
```

To fix the ambiguity if `first_inner_keyword_key: first_inner_keyword_value` should be associated, add parentheses
around the inner function's arguments:

```elixir
# keywords are for inner function
outer_function first_outer_argument
               inner_function(
                 first_inner_argument
                 ambiguous_keyword_key: ambiguous_keyword_value
               )

# keywords are for outer function
outer_function first_outer_argument
               inner_function(
                 first_inner_argument
               ),
               ambiguous_keyword_key: ambiguous_keyword_value
```

<figure>
  <img alt="Ambiguous nested calls preferences" src="/screenshots/inspection/elixir/ambiguous_nested_calls/preferences.png?raw=true"/>
  <br/>
  <figcaption>
    Preferences &gt; Inspections &gt; Elixir &gt; Ambiguous nested calls
  </figcaption>
</figure>

<figure>
  <img alt="Ambiguous nested calls error" src="/screenshots/inspection/elixir/ambiguous_nested_calls/error.png?raw=true"/>
  <br/>
  <figcaption>
    Ambiguous nested call inspection marks the error on the comma that causes the ambiguity.
  </figcaption>
</figure>

<figure>
  <img alt="Ambiguous nested calls inspection" src="/screenshots/inspection/elixir/ambiguous_nested_calls/inspection.png?raw=true"/>
  <br/>
  <figcaption>
    Mousing over the comma marked as an error in red (or over the red square in the right gutter) will show the inspection
    describing the error.
  </figcaption>
</figure>

#### Ambiguous parentheses

Detects when compiler will throw `unexpected parenthesis. If you are making a function call, do not insert spaces in between the function name and the opening parentheses`.
Function calls with space between the function name and the parentheses cannot distinguish between function calls with
parentheses, but with an accidental space before the `(` and function calls without parentheses where the first
positional argument is in parentheses.

##### Empty Parentheses
```elixir
function ()
```

To fix the ambiguity remove the space or add outer parentheses without the space if the first argument should be `()`:
```elixir
# extra space, no arguments to function
function()

# first argument is `()`
function(())
```

##### Keywords in Parentheses
```elixir
function (key: value)
```

Keywords inside parentheses is not valid, so the only way to fix this is to remove the space

```elixir
function(key: value)
```

##### Positional arguments in Parentheses

```elixir
function (first_positional, second_positional)
```

A list of positional arguments in parenthenses is not valid, so the only way to fix this is to remove the space

```elixir
function(first_positional, second_positional)
```

<figure>
  <img alt="Ambiguous parentheses preferences" src="/screenshots/inspection/elixir/ambiguous_parentheses/preferences.png?raw=true"/>
  <br/>
  <figcaption>
    Preferences &gt; Inspections &gt; Elixir &gt; Ambiguous parentheses
  </figcaption>
</figure>

<figure>
  <img alt="Ambiguous parentheses error" src="/screenshots/inspection/elixir/ambiguous_parentheses/error.png?raw=true"/>
  <br/>
  <figcaption>
    Ambiguous parentheses inspection marks the error on the parenthetical group surrounded by the parentheses that are
    ambiguous due to the preceding space.
  </figcaption>
</figure>

<figure>
  <img alt="Ambiguous parentheses" src="/screenshots/inspection/elixir/ambiguous_parentheses/inspection.png?raw=true"/>
  <br/>
  <figcaption>
    Mousing over the parenthetical group marked as an error in red (or over the red square in the right gutter) will
    show the inspection describing the error.
  </figcaption>
</figure>

#### Keyword pair colon (`:`) used in type spec instead of type operator (`::`)

Type specifications separate the name from the definition using `::`.

```elixir
@type name: definition
```

Replace the `:` with ` ::`

```elixir
@type name :: definition
```

#### Keywords appear before the end of list.

```elixir
one.(
  one,
  two positional, key: value,
  three
)
```

Keywords can only appear at the end of an argument list, so either surround the no parentheses expression argument with
parentheses, or move the the keywords to the end of the list if it wasn't meant to be a no parentheses expression.

```elixir
one.(
  one
  two(positional, key: value),
  three
)
```

OR

```elixir
one.(
  one,
  two,
  three,
  key: value
)
```

<figure>
  <img alt="Keywords Not At End" src="/screenshots/inspection/elixir/keywords_not_at_end/preferences.png?raw=true"/>
  <br/>
  <figcaption>
    Preferences &gt; Inspections &gt; Elixir &gt; Keywords Not At End
  </figcaption>
</figure>

<figure>
  <img alt="Keywords Not At End error" src="/screenshots/inspection/elixir/keywords_not_at_end/error.png?raw=true"/>
  <br/>
  <figcaption>
    Keywords Not At End inspection marks the error over the keywords that need to be surrounded by parentheses or moved
    to the end of the list.
  </figcaption>
</figure>

<figure>
  <img alt="Keywords Not At End inspection" src="/screenshots/inspection/elixir/keywords_not_at_end/inspection.png?raw=true"/>
  <br/>
  <figcaption>
    Mousing over the keywords marked as an error in red (or over the red square in the right gutter) will
    show the inspection describing the error.
  </figcaption>
</figure>

#### Match operator (`=`) used in type spec instead of type operator (`::`)

Type specifications separate the name from the definition using `::`.

```elixir
@type name = definition
```

Replace the `=` with ` ::`

```elixir
@type name :: definition
```

### Quick Fixes

Quick Fixes are actions IntelliJ can take to change your code to correct errors (accessed with Alt+Enter by default).

#### Convert `:` to ` ::` in type specs

If a type specification uses a single `:` instead of `::`, then hit Alt+Enter on the `:` to change it to ` ::` and fix the type spec.

#### Convert `=` to ` ::` in type specs

If a type specification uses `=` instead of `::`, then hit Alt+Enter on the `=` to change it to `::` and fix the type spec.

#### Remove space in front of ambiguous parentheses

If a set of parentheses is marked as ambiguous then the space before it can be removed to disambiguate the parentheses
with Alt+Enter. (Will vary based on keymap.)

<figure>
  <img alt="Remove spaces before ambiguous parentheses" src="/screenshots/local_quick_fix/Remove%20Spaces%20Before%20Ambiguous%20Parentheses.gif?raw=true"/>
  <br/>
  <figcaption>
    Hitting Alt+Enter on ambiguous parentheses error will bring up the Local Quick Fix,
    "Remove spaces between function name and parentheses".  Hit Enter to accept and remove the space.
  </figcaption>
</figure>

### Code Folding

You can collapse (fold) pre-defined regions of your Elixir code to make it easier to quickly scroll through files or hide details you don't care about right now.

#### Controls

##### Collapsing

1. Position cursor between lines with with downward facing - arrow and upward facing - arrow.
2. Cmd+-

##### Expanding

1. Position cursor on the collapsed line with the square +
2. Cmd++

#### Regions

| Expanded                          | Collapsed                                  | Folded By Default? |
|-----------------------------------|--------------------------------------------|--------------------|
| `do end`                          | `do: ...`                                  | No                 |
| `->` and right operand            | `-> ...`                                   | No                 |
| `@doc VALUE `                     | `@doc "..."`                               | No                 |
| `@moduledoc VALUE`                | `@moduledoc "..."`                         | No                 |
| `@typedoc VALUE`                  | `@typedoc "..."`                           | No                 |
| alias ALIAS1<br> alias ALIAS1     | `alias ...`                                | Yes                |
| import ALIAS1<br> import ALIAS2   | `import ...`                               | Yes                |
| require ALIAS1<br> require ALIAS2 | `require ...`                              | Yes                |
| use ALIAS1<br> use ALIAS2         | `use ALIAS1`                               | Yes                |
| `@for`                            | `FOR` in `defimpl PROTOCOL, for: FOR`      | Yes                |
| `@protocol`                       | `PROTOCOL` in `defimpl PROTOCOL, for: FOR` | Yes                |
| @MODULE_ATTRIBUTE                 | VALUE in `@MODULE_ATTRIBUTE VALUE`         | No                 |

### Commenter

You can comment or uncomment the current line or selected block of source. By selecting a block of source first you can
quickly comment out and entire function if you're trying to track down a compiling or testing error that's not giving a
helpful line number.

Using the menus

1. Highlight one or more lines
2. Comment (or Uncomment) with one of the following:
  a. Code > Comment with Line Comment
  b. On OSX the key binding is normally `Cmd+/`.

### Debugger

IntelliJ Elixir allows for graphical debugging of `*.ex` files using line breakpoints.

<figure>
  <img alt="Line breakpoints for debugger can be set in gutter of editor tab." src="/screenshots/Debugger.png?raw=true"/>
  <br/>
  <figcaption>
    Line breakpoints can added by clicking in the left-hand gutter of an
    editor tab.  A red dot will appear marking the breakpoint.  When a
    Run Configuration is Run with the Debug (bug) instead of Run (arrow)
    button, execution will stop at the breakpoint and you can view the
    local variables (with Erlang names) and the stackframes.
  </figcaption>
</figure>

#### Steps

1. Define a [run/debug configuration](#run-configurations)
2. [Create breakpoints](#creating-line-breakpoints) in the `*.ex` files
3. [Launch](#starting-the-debugger-session) a debugging session
4. During the debugger session, [step through the breakpoints](#stepping), [examine suspended program](#examining-suspended-program), and [explore frames](#frames).

#### Basics

After you have configured a [run configuration](#run-configuration) for your project, you can launch it in debug mode by pressing `Ctrl+D`.

##### Keyboard Shortcuts

| Action                                  | Keyword Shortcut |
|-----------------------------------------|------------------|
| Toggle Breakpoint                       | `Cmd+F8`         |
| Resume Program                          | `Alt+Cmd+R`      |
| Step Over                               | `F8`             |
| Step Into                               | `F7`             |
| View breakpoint details/all breakpoints | `Shift+Cmd+F8`   |

##### Environment Variables

| Variable                           | Example    | Description                     |
| -----------------------------------|------------| --------------------------------|
| INTELLIJ\_ELIXIR\_DEBUG\_BLACKLIST | iconv,some | Excluding modules from debugger |

#### Breakpoints

When a breakpoint is set, the editor displays a breakpoint icon in the gutter area to the left of the affected source code. A breakpoint icon denotes status of a breakpoint, and provides useful information about its type, location, and action.

The icons serve as convenient shortcuts for managing breakpoints. Clicking an icon removes the breakpoint. Successive use of Alt - click on an icon toggles its state between enabled and disabled. The settings of a breakpoint are shown in a tooltip when a mouse pointer hovers over a breakpoint icon in the gutter area of the editor.

| Status                 | Icon                                                                                                                                | Description                                                                                   |
|------------------------|-------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| Enabled                | <img alt="Red dot" src="screenshots/debugger/breakpoints/Enabled.png?raw=true"/>                                                    | Indicates the debugger will stop at this line when the breakpoint is hit.                     |
| Disabled               | <img alt="Red dot with green dot in center" src="screenshots/debugger/breakpoints/Disabled.png?raw=true"/>                          | Indicates that nothing happens when the breakpoint is hit.                                    |
| Conditionally Disabled | <img alt="Red dot with green dot in top-left corner" src="screenshots/debugger/breakpoints/Conditionally%20Disabled.png?raw=true"/> | This state is assigned to breakpoints when they depend on another breakpoint to be activated. |

When the button <img alt="Red dot surrounded by crossed-out circle" src="screenshots/debugger/breakpoints/Mute.png?raw=true"/> is pressed in the toolbar of the Debug tool window, all the breakpoints in a project are muted, and their icons become grey: <img alt="Grey dot" src="screenshots/debugger/breakpoints/Muted.png?raw=true"/>.

##### Accessing Breakpoint Properties

###### Viewing all breakpoints

To view the list of all breakpoints and their properties, do one of the following:

* Run > View Breakpoints
* `Shift+Cmd+F8`
* Click the <img alt="Two red dots layered vertically on top of each other with smaller grey rings to right of the red dots" src="screenshots/debugger/breakpoints/All.gif?raw=true"/>
* Breakpoints are visible in the Favorites tool window.

###### Viewing a single breakpoint

To view properties of a single breakpoint

* Right-Click a breakpoint icon in the left gutter of the editor.
  <img src="screenshots/debugger/breakpoints/Properties.png?raw=true"/>

##### Configuring Breakpoints

To configure actions, suspend policy and dependencies of a breakpoint

1. Open the Breakpoint Properties
   * Right-click a breakpoint in the left gutter, then click the More link or press `Shift+Cmd+F8`
   * Open the [Breakpoints](#viewing-all-breakpoints) dialog box and select the breakpoint from the list
   * In the Favorites tool window, select the desired breakpoint, and click the pencil icon.
2. Define the actions to be performed by IntelliJ IDEA on hitting breakpoint:
   * To notify about the reaching of a breakpoint with a text message in the debugging console, check the "Log message to console" check box.  A message of the format `*DBG* 'Elixir.IntellijElixir.DebugServer' got cast {breakpoint_reached, PID}` will appear in the console.
   * To set a breakpoint the current one depends on, select it from the "Disabled until selected breakpoint hit" drop-down list. Once dependency has been set, the current breakpoint is disabled until selected one is hit.
     * Choose the "Disable again" radio button to disable the current breakpoint after selected breakpoint was hit.
     * Choose the "Leave enabled" radio button to keep the current breakpoint enabled after selected breakpoint was hit.
   * Enable suspending an application upon reaching a breakpoint by checking the "Suspend" check box.

##### Creating Line Breakpoints

A line breakpoint is a breakpoint assigned to a specific line in the source code.

Line breakpoints can be set on executable lines. Comments, declarations and empty lines are not valid locations for the line breakpoints.

1. Place the caret on the desired line of the source code.
2. Do one of the following:
   * Click the left gutter area at a line where you want to toggle a breakpoint
   * Run > Toggle Line Breakpoint
   * `Cmd+F8`

##### Describing Line Breakpoints

1. [Open the Breakpoints dialog](#viewing-all-breakpoints)
2. Right-click the breakpoint you want to describe
3. Select "Edit description" from the context menu
4. In the "Edit Description" dialog box, type the desired description.

##### Searching for Line Breakpoints

1. [Open the Breakpoints dialog](#viewing-all-breakpoints)
2. Start typing the description of the desired breakpoint

##### Jump to Breakpoint Source

* To view the selected breakpoint without closing the dialog box, use the preview pane.
* To open the file with the selected breakpoint for editing, double-click the desired breakpoint.
* To close Breakpoints dialog, press `Cmd+Down`. The caret will be placed at the line marked with the breakpoint in question.

##### Disabling Line Breakpoints

When you temporarily disable or enable a breakpoint, its icon changes from <img src="screenshots/debugger/breakpoints/Enabled.png?raw=true"/> to <img src="screenshots/debugger/breakpoints/Disabled.png?raw=true"/> and vice versa.

1. Place the caret at the desired line with a breakpoint.
2. Do one of the following:
   * Run > Toggle Breakpoint Enable
   * Right-click the desired breakpoint icon, select or deselect the <breakpoint name> enabled check box, and then click Done.
   * Alt-click the breakpoint icon

##### Deleting Line Breakpoints

Do one of he following:

* In the [Breakpoints](#viewing-all-breakpoints) dialog box, select the desired line breakpoint, and click the red minus sign.
* In the editor, locate the line with the line breakpoint to be deleted, and click its icon in the left gutter.
* Place caret on the desired line and press `Cmd+F8`.

#### Starting the Debugger Session

1. Select the run/debug configuration to execute
2. Do one of the following
   * Click <img alt="Bug" src="screenshots/debugger/Debug.png?raw=true"/> on the toolbar
   * Run > Debug
   * `Ctrl+D`

OR

Debug quick menu

1. `Ctrl+Alt+D`
2. Select the configuration from the pop-up menu
3. Hit `Enter`

#### Examining Suspended Program

##### Processes

<figure>
  <img src="screenshots/debugger/Processes.png?raw=true"/>
  <br/>
  <figcaption>
    The "Thread" drop-down lists the current processes in the local
    node.  Only the current process is suspended.  The rest of the
    processes are still running.
  </figcaption>
</figure>

##### Frames

<figure>
  <img src="screenshots/debugger/Frames.png?raw=true"/>
  <br/>
  <figcaption>
    The Frames for the current process can be navigated up and down
    using the arrow keys or clicking on the frame.
  </figcaption>
</figure>

* Press `Up` or `Down` to change frames
* Click the frame from the list

###### Jump to Current Execution Point

When changing frames or jumping to definitions, you can lose track of where the debugger is paused.  To get back to the current execution point, do one of the following:
1. Run > Show Execution Point.
2. `Alt+F10`
3. Click <img src="screenshots/debugger/Show%20Execution%20Point.png?raw=true"/> on the stepping toolbar of the Debug tool window.

##### Variables

<img src="screenshots/debugger/Variables.png?raw=true"/>

While Elixir allows rebinding variable names, Erlang does not, so when viewed in the Variables pane, variables will have an `@VERSION` after their name indicating which rebinding of a the variable is.  Even if there is no variable reuse, the first variable will still have `@1` in its name.

#### Stepping

| Action               | Icon                                                                    | Shortcut   | Description                                                                                                                                                                                                                                                                                  |
|----------------------|-------------------------------------------------------------------------|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Show Execution Point | <img src="screenshots/debugger/Show%20Execution%20Point.png?raw=true"/> | `Alt+F10`  | Click this button to highlight the current execution point in the editor and show the corresponding stack frame in the Frames pane.                                                                                                                                                          |
| Step Over            | <img src="screenshots/debugger/stepping/Step%20Over.png?raw=true"/>     | `F8`       | Click this button to execute the program until the next line in the current function or file, skipping the function referenced at the current execution point (if any). If the current line is the last one in the function, execution steps to the line executed right after this function. |
| Step Into            | <img src="screenshots/debugger/stepping/Step%20Into.png?raw=true"/>     | `F7`       | Click this button to have the debugger step into the function called at the current execution point.                                                                                                                                                                                         |
| Step Out             | <img src="screenshots/debugger/stepping/Step%20Out.png?raw=true"/>      | `Shift+F8` | Click this button to have the debugger step out of the current function, to the line executed right after it.                                                                                                                                                                                |

### Delimiters

#### Auto-inserting

The right-delimiter will be automatically inserted when the left
delimiter is typed.  In some cases, to prevent false positives, the
the delimiter is only completed if when used for sigils.

| Preceded By     | Left  | Right  |
|-----------------|-------|--------|
|                 | `do ` | ` end` |
|                 | `fn ` | ` end` |
|                 | `[`   | `]`    |
|                 | `{`   | `}`    |
|                 | `(`   | `)`    |
|                 | `'`   | `'`    |
|                 | `'''` | ` '''` |
|                 | `"`   | `"`    |
|                 | `"""` | ` """` |
|                 | `<<`  | `>>`   |
| `~<sigil-name>` | `<`   | `>`    |
| `~<sigil-name>` | `/`   | `/`    |
| `~<sigil-name>` | `|`   | `|`    |

#### Matching

All delimiters that are auto-inserted are also matched for highlighting

| Left  | Right |
|-------|-------|
| `do ` | `end` |
| `fn ` | `end` |
| `[`   | `]`   |
| `{`   | `}`   |
| `(`   | `)`   |
| `'`   | `'`   |
| `'''` | `'''` |
| `"`   | `"`   |
| `"""` | `"""` |
| `<<`  | `>>`  |
| `<`   | `>`   |
| `/`   | `/`   |
| `|`   | `|`   |

### Building/Compiling

#### Settings

![Build, Execution, Deployment > Compiler > Elixir Compiler](/screenshots/features/building/Settings.png?raw=true "Elixir Compiler Settings")

* Compile project with mix (use `mix compile` instead of `elixirc` directly)
* Attach docs (don't use `--no-docs` `elixirc` flag)
* Attach debug info (don't use `--no-debug-info` `elixirc` flag)
* Ignore module conflict (use `--ignore-module-conflict` `elixirc` flag)

#### Individual File

1. Have a file selected in Project view with the Project view in focus OR have an Editor tab in focus
2. Build > Compile 'FILE_NAME'
3. Build results will be shown
    * If compilation is successful, you'll see "Compilation completed successfully" in the Event Log
    * If compilation had errors, you'll see "Compilation completed with N errors and M warnings" in the Event Log and
      the Messages Compile tab will open showing a list of Errors
      ![Messages Compile](/screenshots/features/building/file/Messages%20Compile.png?raw=true "Messages Compile Individual File Build Errors")

#### Project

1. Build > Make Project
2. Build results will be shown
    * If compilation is successful, you'll see "Compilation completed successfully" in the Event Log
    * If compilation had errors, you'll see "Compilation completed with N errors and M warnings" in the Event Log and
      the Messages Compile tab will open showing a list of Errors
      ![Messages Compile](/screenshots/features/building/file/Messages%20Compile.png?raw=true "Messages Compile Individual File Build Errors")

### Live Templates

Live Templates are snippets of code that can be inserted quickly and have placeholder locations that the cursor will
automatically jump to when using the template.  Whenever you start typing, Live Templates will start matching against
the shortcuts.  A template can be selected with Tab.

Live Templates can be customized in Preferences > Editor > Live Templates > Elixir.

<table>
  <caption>
    Metasyntactic variables are locations where the cursor will jump to.  <code>END</code> is the final location of the
    cursor.
  </caption>
  <thead>
    <tr>
      <th>
        Shortcut
      </th>
      <th>
        Code
      </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <code>@doc</code>
      </td>
      <td>
<pre><code>
@doc """
ONE
"""
END
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>case</code>
      </td>
      <td>
<pre><code>
case ONE do
  TWO -> END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>cond</code>
      </td>
      <td>
<pre><code>
cond do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>def</code>
      </td>
      <td>
<pre><code>
def NAME do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>def,</code>
      </td>
      <td>
<pre><code>
def NAME, do: END
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defi</code>
      </td>
      <td>
<pre><code>
defimpl PROTOCOL, for: TYPE do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defm</code>
      </td>
      <td>
<pre><code>
defmodule ALIAS do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defmac</code>
      </td>
      <td>
<pre><code>
defmacro MACRO_NAME do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defmacp</code>
      </td>
      <td>
<pre><code>
defmacrop MACRO_NAME do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defover</code>
      </td>
      <td>
<pre><code>
defoverridable [NAME: END]
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defp</code>
      </td>
      <td>
<pre><code>
defp NAME do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defpro</code>
      </td>
      <td>
<pre><code>
defprotocol PROTOCOL do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>defs</code>
      </td>
      <td>
<pre><code>
defstruct [END]
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>do</code>
      </td>
      <td>
<pre><code>
do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>doc</code>
      </td>
      <td>
<pre><code>
@doc """
ONE
"""
END
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>fn</code>
      </td>
      <td>
<pre><code>
fn ARGS -> END end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>for</code>
      </td>
      <td>
<pre><code>
for A <- B do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>if</code>
      </td>
      <td>
<pre><code>
if TRUE do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>ife</code>
      </td>
      <td>
<pre><code>
if TRUE do
  OK
else
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>ii</code>
      </td>
      <td>
<pre><code>
IO.inspect(END)
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>mdoc</code>
      </td>
      <td>
<pre><code>
@moduledoc """
ONE
"""
END
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>rec</code>
      </td>
      <td>
<pre><code>
receive do
  ONE -> END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>test</code>
      </td>
      <td>
<pre><code>
test "TESTDESC" do
  END
end
</code></pre>
      </td>
    </tr>
    <tr>
      <td>
        <code>try</code>
      </td>
      <td>
<pre><code>
try do
  ONE
rescue
  TWO -> END
</code></pre>
      </td>
    </tr>
  </tbody>
</table>

### Run Configurations

#### Mix Tasks

Much like `rake` tasks in Rubymine, this plugin can run `mix` tasks.

1. Run > Edit Configurations...
   ![Edit Run Configurations](/screenshots/features/run_configurations/Edit%20Configurations.png?raw=true "Edit Run Configurations")
2. Click +
3. Select "Elixir Mix"
   ![Add New Elixir Mix](/screenshots/features/run_configurations/mix_tasks/Add%20New.png?raw=true "Add New Elixir Mix Run Configuration")
4. Fill in the "Program arguments" starting with the name of the `mix` task followed by any arguments to that task
5. Fill in the "Working directory"
  * Type the absolute path to the directory.
  * Select the path using directory picker by clicking the `...` button
6. (Optionally) click the `...` button on the "Environment variables" line to add environment variables.
7. Click "OK" to save the Run Configuration and close the dialog
8. Click the Run arrow in the Toolbar to run the `mix` task
   ![Run](/screenshots/features/run_configurations/mix_tasks/Toolbar%20Run%20Button.png?raw=true "Run Elixir Mix Run Configuration")
9. The Run pane will open, showing the results of the `mix` task.
    * If there is an error with a FILE:LINE stack frame, it will be a clickable link that will take you to that location
      ![Error link](/screenshots/features/run_configurations/mix_tasks/Error%20Link.png?raw=true "Clickable Error Link")

#### `mix test`

The `mix test` task gets a special type of Run Configuration, `Elixir Mix ExUnit`.  Using this Run Configuration type instead, of the basic `Elixir Mix` Run Configuration will cause the IDE to attach a special formatter to `mix test`, so that you get the standard graphical tree of Test Results

The Run pane will show Test Results.  If there is a compilation error before or during `mix test`, it will be shown as a test failure.  If the compilation failure is in a `_test.exs` file can it can be inferred from the stacktrace, the compilation error will show up as a test failure in that specific module.

`doctest` names are rearranged to emphasize the function being tested: `"test doc at MODULE.FUNCTION/ARITY (COUNT)"` becomes `"MODULE.FUNCTION/ARITY doc (COUNT)"`.  If `MODULE` is the same as the test case without the `Test` suffix, then `MODULE` is stripped too and the test name becomes only `FUNCTION/ARITY doc (COUNT)`.

##### Creating `mix test` Run Configurations Manually

1. Run > Edit Configurations...
   ![Edit Run Configurations](/screenshots/features/run_configurations/Edit%20Configurations.png?raw=true "Edit Run Configurations")
2. Click +
3. Select "Elixir Mix ExUnit"
   ![Add New Elixir Mix ExUnit](/screenshots/features/run_configurations/mix_test/Add%20New.png?raw=true "Add New Elixir Mix ExUnit Run Configuration")
4. Fill in the "Program arguments" with the argument(s) to pass to `mix test`.  Normally, this will be a directory like `test`, relative to the "Working directory"
5. Fill in the "Working directory"
  * Type the absolute path to the directory.
  * Select the path using directory picker by clicking the `...` button
6. (Optionally) click the `...` button on the "Environment variables" line to add environment variables.
7. Click "OK" to save the Run Configuration and close the dialog
8. Click the RUn arrow in the Toolbar to run the `mix test` task
9. The Run pane will open showing the Test Results
   ![Test Results](/screenshots/features/run_configurations/mix_test/Test%20Results.png?raw=true "Full Green Test Results")

While you can create `Elixir Mix ExUnit` run configurations manually using the `Run > Edit Configurations...` menu, it is probably more convenient to use the context menu.

##### Creating `mix test` Run Configurations from context

The context menu must know that the the directory, file, or line you are right-clicking is a test.  It does this by checking if the current directory or an ancestor is marked as a Test Sources Root.

1. In the Project pane, ensure your OTP application's `test` directory is marked as a Test Sources Root
  1. Check if the `test` directory is green.  If it is, it is likely a Test Sources Root.  This color may differ in different themes, so to be sure you can check the context menu
  2. Right-click the `test` directory.
  3. Hover over "Mark Directory As &gt;"
    * If "Unmark as Test Sources Root" is shown, then the directory is already configured correctly, and create from context will work.
      ![Mark Directory As &gt; Unmark as Test Sources Root](/screenshots/features/run_configurations/mix_test/mark_directory_as/Unmark%20as%20Test%20Sources%20Root.png?raw=true "Unmark Directory as Test Sources Root")
    * If "Test Sources Root" is shown, then the directory need to be configured by clicking that entry
      ![Mark Directory As &gt; Test Sources Root](/screenshots/features/run_configurations/mix_test/mark_directory_as/Test%20Sources%20Root.png?raw=true "Mark Directory as Test Sources Root")

###### Creating/Running `mix test` Run Configurations from directory

1. Right-click the directory in the Project pane
2. Click "Run Mix ExUnit", which will both create the Run Configuration and Run it.
  ![Run Mix ExUnit](/screenshots/features/run_configurations/mix_test/context/directory/Run%20Mix%20ExUnit.png?raw=true "Run Mix ExUnit from right-clicking directory")
  * If you want to only create the Run Configuration, select "Create Mix ExUnit" instead

Alternatively, you can use keyboard shortcuts

1. Select the directory in the Project pane.
2. `Ctrl+Shift+R` will create the Run Configuration and Run it.

###### Creating/Running `mix test` Run Configurations from file

1. Right-click the file in the Project pane
2. Click "Run Mix ExUnit", which will both create the Run Configuration and Run it.
  * If you want to only create the Run Configuration, select "Create Mix ExUnit" instead

Alternatively, you can use keyboard shortcuts

1. Select the directory in the Project pane.
2. `Ctrl+Shift+R` will create the Run Configuration and Run it.

Finally, you can use the editor tabs

1. Right-click the editor tab for the test file you want to run
  ![Run Mix ExUnit](/screenshots/features/run_configurations/mix_test/context/file/Run%20Mix%20ExUnit.png?raw=true "Run Mix ExUnit from right-clicking file editor tab")
2. Click "Run Mix ExUnit", which will both create the Run Configuration and Run it.
  * If you want to only create the Run Configuration, select "Create Mix ExUnit" instead

###### Creating/Running `mix test` Run Configurations from line

If you want to be able to run a single test, you can create a Run Configuration for a line in that test

1. Right-click a line in the test file
  ![Run Mix ExUnit](/screenshots/features/run_configurations/mix_test/context/line/Run%20Mix%20ExUnit.png?raw=true "Run Mix ExUnit from right-clicking line in editor")
2. Click "Run Mix ExUnit", which will both create the Run Configuration and Run it.
  * If you want to only create the Run Configuration, select "Create Mix ExUnit" instead

Alternatively, you can use keyboard shortcuts

1. Place the cursor on the line you want to test
2. `Ctrl+Shift+R` will create the Run Configuration and Run it.

### Completion

#### Aliases and Modules

When you start typing an Alias, completion will look in three locations:

1. `alias` aliased names in the current file
    1. `Suffix` for `alias Prefix.Suffix`
    2. `MultipleAliasA` or `MultipleAliasB` for `alias Prefix.{MultipleAliasA, MultipleAliasB}`
    3. `As` for `alias Prefix.Suffix, as: As`
2. Indexed module names (as available from [Go To Symbol](#go-to-symbol))
    1. `Prefix.Suffix` from `defmodule Prefix.Suffix`
    2. `MyProtocol` from `defprotocol MyProtocol`
    3. `MyProtocol.MyStruct`
        1. `defimpl MyProtocol, for: MyStruct`
        2. `defimpl MyProtocol` nested under `defmodule MyStruct`
3. Nested modules under aliased names
    1. `Suffix.Nested` for `alias Prefix.Suffix` where `Prefix.Suffix.Nested` is an indexed module, implementation or protocol name.
    2. `MultipleAliasA.Nested` for `alias Prefix.{MultipleAliasA, MultipleAliasB}` where `Prefix.MultipleAliasA.Nested` `alias Prefix.{MultipleAliasA, MultipleAliasB}` is an indexed module, implementation or protocol name.
    3. `As.Nested` for `alias Prefix.Suffix, as: As` where `Prefix.Suffix.Nested` is an indexed module, implementation, or protocol name.

##### Aliases inside `{ }`

When you start typing inside `{ }` for `alias Prefix.{}` or `import Prefix.{}`, completion will look for nested modules under `Prefix` and then remove the `Prefix.`, so completion will look like `Suffix`.

#### Function and Macro Calls

Completion uses the same presentation as [Structure](#structure), so you can tell whether the name is function/macro ([Time](#time)), whether it is public/private ([Visibility](#visibility)) and the Module where it is defined.  Between the icons and the Modules is the name itself, which is highlighted in **bold**, the parameters for the call definition follow, so that you can preview the patterns required for the different clauses.

![Function and Macro Calls Completion](/screenshots/Function%20and%20Macro%20Calls%20Completion.png?raw=true "Function and Macro Calls Completion")

##### Qualified

Qualified functions and macro calls will complete using those functions and macros defined in the qualifying Module (`defmodule`), Implementation (`defimpl`) or Protocol (`defprotocol`).  Completion starts as shown as `.` is typed after a qualifying Alias.

##### Unqualified

Function and macro calls that are unqualified are completed from the index of all function and macro definitions, both public and private. (The index contains only those Elixir functions and macro defined in parsable source, such as those in the project or its dependencies.  Erlang functions and Elixir functions only in compiled `.beam` files, such as the standard library will not complete.)  Private function and macros are shown, so you can choose them and then make the chosen function or macro public if it is a remote call.

#### Module Attributes

Module attributes declared earlier in the file can be completed whenever you type `@` and some letter.  If you want to see all module attributes, you can type `@a`, wait for the completions to appear, then delete the `@` to remove the filtering to `a`.

#### Parameters and Variables

Parameter and variable usages can be completed whenever typing an identifier.  The completions will include all variables know up from that part of the file.  It can include variables from outside macros, like quote blocks.

### Decompilation

`.beam` files, such as those in the Elixir SDK and in your project's `build` directory will be decompiled to equivalent `def` and `defmacro` calls.  The bodies will not be decompiled, only the call definition head and placeholder parameters.  These decompiled call definition heads are enough to allow Go To Declaration, the Structure pane, and Completion to work with the decompiled `.beam` files.

### Go To Declaration

Go To Declaration is a feature of JetBrains IDEs that allows you to jump from the usage of a symbol, such as a Module
Alias, to its declaration, such as the `defmodule` call.

#### Alias

1. Place the cursor over an Alias with an aliased name setup by `alias`
    1. `Suffix` if `alias Prefix.Suffix` called
    2. `MultipleAliasA` if `alias Prefix.{MultipleAliasA, MultipleAliasB}` called
    3. `As` if `alias Prefix.Suffix, as: As`
2. Activate the Go To Declaration action with one of the following:
    1. `Cmd+B`
    2. Select Navigate &gt; Declaration from the menu.
    3. `Cmd+Click`
3. A Go To Declaration lookup menu will appear, allowing you to jump either the `alias` that setup the aliased name or jumping directly to `defmodule` of the unaliased name.  Select which declaration you want
    1. Use arrow keys to select and hit `Enter`
    2. `Click`

#### Function or Macro

You'll know if function or macro usage is resolved and Go To Declaration will work if the call is annotated, which in the default themes will show up as *italics*.

##### Imported Functions or Macros

1. Place the cursor over name of the function or macro call.
2. Activate the Go to Declaration action with one of the following:
    1. `Cmd+B`
    2. Select Navigate &gt; Declaration from the menu.
    3. `Cmd+Click`
3. A Go To Declaration lookup menu will appear, allowing you to jump to either the `import` that imported the function or macro or jumping directly to the function or macro definition clause.  Select which declaration you want.
    1. Use arrow keys to select and hit `Enter`
    2. `Click`

##### Local Functions or Macros

1. Place the cursor over name of the function or macro call.
2. Activate the Go to Declaration action with one of the following:
    1. `Cmd+B`
    2. Select Navigate &gt; Declaration from the menu.
    3. `Cmd+Click`
3. Whether a lookup a Go To Declaration lookup menu appears depends on the number of clauses in the function or macro definition:
    1. If there is only one clause in the function or macro definition, you'll jump immediately to that clause
    2. If there is more than one clause in the function or macro definition, a Go To Declaration lookup menu will appear, allowing you to jump to either the `import` that imported the function or macro or jumping directly to the function or macro definition clause.  Select which declaration you want.
        1. Use arrow keys to select and hit `Enter`
        2. `Click`

##### Remote Functions or Macros

1. Place the cursor over name of the function or macro call that is qualified by an Alias.
2. Activate the Go to Declaration action with one of the following:
    1. `Cmd+B`
    2. Select Navigate &gt; Declaration from the menu.
    3. `Cmd+Click`
3.
    1. If there is only one clause in the function or macro definition, you'll jump immediately to that clause
    2. If there is more than one clause in the function or macro definition, a Go To Declaration lookup menu will appear, allowing you to jump to either the `import` that imported the function or macro or jumping directly to the function or macro definition clause.  Select which declaration you want.
        1. Use arrow keys to select and hit `Enter`
        2. `Click`


#### Module

1. Place the cursor over a fully-qualified Alias
    1. `A.B` in `A.B.func()`
    2. `A.B` in `alias A.B`
    3. `B` in `alias A.{B, C}`
2. Activate the Go To Declaration action with one of the following:
    1. `Cmd+B`
    2. Select Navigate &gt; Declaration from the menu.
    3. `Cmd+Click`

If you hold `Cmd` and hover over the Alias before clicking, the target declaration will be shown.

[![Go To Declaration Demonstration](http://img.youtube.com/vi/nN-DMEe-BQA/0.jpg)](https://www.youtube.com/watch?v=nN-DMEe-BQA)

#### Module Attribute

1. Place the cursor over a `@module_attribute`
2. Activate the Go To Declaration action with one of the following:
    1. `Cmd+B`
    2. Select Navigate &gt; Declaration from the menu.
    3. `Cmd+Click`

If you hold `Cmd` and hover over the `@module_attribute` before clicking, the target declaration will be shown.

#### Parameters and Variables

1. Place the cursor over a parameter or variable usage
2. Active the Go To Declaration action with one of the following:
    1. `Cmd+B`
    2. Select Navigate &gt; Declaration from the menu.
    3. `Cmd+Click`

If you hold `Cmd` and hover over the variable before clicking, it will say `parameter` or `variable`, which matches the annotation color.

### Formatting

IntelliJ Elixir can reformat code to follow a consistent style.

* `do` block lines are indented
* `do` blocks `end` as the last argument of a no parentheses call unindents to the start of the call
* If one clause of a multi-clause anonymous function wraps, all clauses wrap.
* Indent after `else`
* Indent map and struct keys
* All keys wrap if any key wraps
* No spaces around...
  * `.`
* Spaces around...
  * `and`
  * `in`
  * `or`
  * `when`
* Configure spaces around...
  * `=`
  * `<-` and `\\`
  * `!=`, `==`, `=~`, `!==`, and `===`
  *  `<`, `<=`, `>=`, and `>`
  * `+` and `-`
  * `*` and `/`
  * Unary `+`, `-`, `!`, `^`, and `~~~`
  * `->`
  * `::`
  * `|`
  * `||` and `|||`
  * `&&` and `&&&`
  * `<~`, `|>`, `~>`, `<<<`, `<<~`, `<|>`, `<~>`, `>>>`, and `~>>`
  * `..`
  * `^^^`
  * `++`, `--`, `..`, `<>`
  * `=>`
* Configure spaces before...
  * `,`
* No space after...
  * `@`
* Spaces after...
  * `not`
  * `fn`
  * `after`
  * `catch`
  * `rescue`
  * `key:`
* Configure space after...
  * `&`
  * `,`
* Configure spaces within...
  * `{ }`
  * `<< >>`
  * `[ ]`
  * `( )`
* No space around `/` in `&NAME/ARITY` and `&QUALIFIER.NAME/ARITY`
* `when` wraps when its right operand wraps, so that guards start with `when` on a newline when they are too long.
* Align `|>` at start of indented line for pipelines
* Align `end` with start of call instead of start of line for `do` blocks in pipelines
* Indent list elements when wrapped
* Indent tuple elements when wrapped
* Align type definition to right of `::`
* Align guard to right of `when` when guards span multiple lines
* Align two operator (`++`, `--`, `..`, `<>`) operands, so that `<>` binaries are multiple lines align their starts instead of using continuation indent and being indented relative to first operand.
* Align pipe `|` operands, so that alternates in types and specs are aligned instead of continuation indented relative to the first operand.
* Comments in `spec` (that is above operands to `|` align with the operands
* Remove newlines from pipelines, so that all pipelines start with an initial value or call and each `|>` is the start of a successive line.
* Key exclusivity: if an association operation or keyword key is already on a line, the container value automatically has it's elements wrapped if there is nested associations or keyword pairs, so that two levels of keys are not on the same line.
* Indent bit string (`<< >>`) elements when wrapped

#### Directory

All files in a directory can be reformatted.

Using context menu:

1. Open the Project pane
2. Right-click the directory
3. Select Reformat Code
4. Set the desired options in the Reformat Code dialog
5. Click Run

Using keyboard shortcuts:

1. Open the Project pane
2. Select the directory
3. `Alt+Cmd+L`
4. Set the desired options in the Reformat Code dialog
5. Click Run

#### File

##### Other File

All lines in a file can be reformatted.

Using context menu:

1. Open the Project pane
2. Right-click the file
3. Select Reformat Code
4. Set the desired options in the Reformat Code dialog
5. Click OK

Using keyboard shortcuts:

1. Open the Project pane
2. Select the file
3. `Alt+Cmd+L`
4. Set the desired options in the Reformat Code dialog
5. Click OK

##### Current File

All the lines in the current editor tab file can be reformatted with the
current settings.

* Code > Reformat
* `Alt+Cmd+L`
  * `Alt+Shift+Cmd+L` to get the Reformat Code dialog.

#### Selection

A subset of a file can be reformatted.

1. Highlight the selection
2. Use the Reformat Code action
   * Code > Reformat Code
   * `Alt+Shift+Cmd+L`

### Go To Symbol

Go To Symbol is a way to search for any of the following by name:

* Call definition clauses (`def`, `defp`, `defmacro`, and `defmacrop`)
* Callbacks (`@callback` and `@macrocallback`)
* Call definition specifications (`@spec`)
* Call definition heads (`foo(bar)`) for delegation (`defdelegate foo(bar), to: BAZ`)
* Implementations (`defimpl`)
* Protocols (`defprotocol`)

You can bring up Go To Symbol with the keyboard shortcut (⌥⌘O on OSX) or using the menus (Navigate > Symbol...).

### Go To Test

Go to Test allows you to jump from the a Source Module to its corresponding Test Module

1. Have the cursor in the body of a Module
2. Active the Go To Test action with one of the following:
    1. `Shift+Cmd+T`
    2. Select Navigate &gt Test from the menu.

### Go To Test Subject

Go to Test Subject allows you to jump from the a Test Module to its corresponding Source Module

1. Have the cursor in the body of a Test Module
2. Active the Go To Test Subject action with one of the following:
    1. `Shift+Cmd+T`
    2. Select Navigate &gt Test Subject from the menu.

### Find Usage

Find Usage is a feature of JetBrains IDEs that allows you to find all the places a declared symbol, such a Module Alias
in a `defmodule`, is used, including in strings and comments.

#### Module

1. Place cursor over an `defmodule` Alias.
2. Activate the Find Usage action with one of the following:
  a.
    i. Right-click the Alias
    ii. Select "Find Usages" from the context menu
  b. Select Edit &gt; Find &gt; Find Usages from the menu
  c. `Alt+F7`

[![Find Module Usage Demonstration](http://img.youtube.com/vi/n_EEucKK0N0/0.jpg)](https://www.youtube.com/watch?v=n_EEucKK0N0)

#### Module Attribute

1. Place cursor over the `@module_attribute` part of the declaration `@module_attribute value`.
2. Activate the Find Usage action with one of the following:
    1.
        1. Right-click the module attribute
        2. Select "Find Usages" from the context menu
    2. Select Edit &gt; Find &gt; Find Usages from the menu
    3. `Alt+F7`

#### Parameters and Variables

1. Place cursor over the parameter or variable declaration.
2. Active the Find Usage action with one of the following:
    1.
        1. Right-click the Alias
        2. Select "Find Usages" from the context menu
    2. Select Edit &gt; Find &gt; Find Usages from the menu
    3. `Alt+F7`

### Refactor

#### Rename

##### Module Attribute

1. Place the cursor over the `@module_attribute` usage or declaration.
2. Active the Rename Refactoring action with one of the following:
    1.
        1. Right-click the module attribute
        2. Select Refactoring from the context menu
        3. Select "Rename..." from the Refactoring submenu
    2. `Shift+F6`
3. Edit the name inline and have the declaration and usages update.

##### Parameters and Variables

1. Place the cursor over the parameter or variable usage or declaration
2. Active the Rename Refactoring action with one of the following:
    1.
        1. Right-click the module attribute
        2. Select Refactoring from the context menu
        3. Select "Rename..." from the Refactoring submenu
    2. `Shift+F6`
3. Edit the name inline and have the declaration and usages update.

### Structure

You can view the structure of the currently open editor tab using the Structure tool window.

#### Viewing Structure

* View > Tool Windows > Structure
* Click the Structure Button (normally in the left tool buttons)
  1. If you can't see the Tool Buttons, they can be enabled with View > Tool Buttons
* `Cmd+7`

#### Buttons

![Structure Buttons](/screenshots/structure/Buttons.png?raw=true "Sort By Time, Sort By Visibility, Sort Alphabetically, Show Used, Expand All, Collapse All, Autoscroll to Source, Autoscroll from Source")

The buttons in the Structure tool are broken into 4 categories:
* [Sorters](#sorters)
* [Providers](#providers)
* [Expanders](#expanders)
* [Autoscrollers](#autoscrollers)

##### Sorters

![Structure Sorter Buttons](/screenshots/structure/button/Sorters.png?raw=true "Sort By Time, Sort By Visibility, Sort Alphabetically")

<table>
  <thead>
    <tr>
      <th>Icon</th>
      <th>Tooltip</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <img alt="Sort by Time" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/objectBrowser/sortByType.png?raw=true" />
      </td>
      <td>Sort by Time</td>
      <td>
        When the defined callable is usable:
        <ol>
          <li>Compile time</li>
          <li>Both or None</li>
          <li>Runtime</li>
        </ol>
        Macros are compile time while functions are runtime.
      </td>
    </tr>
    <tr>
      <td>
        <img alt="Sort by Visibility" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/objectBrowser/visibilitySort.png?raw=true" />
      </td>
      <td>Sort by Visibility</td>
      <td>
        Whether the element visible outside its defining module:
        <ol>
          <li>Public</li>
          <li><None</li>
          <li>Private</li>
        </ol>
      </td>
    </tr>
    <tr>
      <td>
        <img alt="Sort Alphabetically" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/objectBrowser/sorted.png?raw=true" />
      </td>
      <td>
        Sort Alphabetically
      </td>
      <td>
        Sort by name
      </td>
    </tr>
  </tbody>
</table>

**NOTE: When any combination of sorters is turned on, they are sorted from left to right (as shown in the button bar),
so with all 3 sorters on, the elements are first grouped by Time, then inside each Time group, they are sorted by
Visibility, then in each Visibility group, they are sorted by name.**

##### Providers

![Structure Provider Buttons](/screenshots/structure/button/Providers.png?raw=true "Providers")

The providers add nodes not in the text of the file, but that will appear in the compiled Module.

<table>
  <thead>
    <tr>
      <th>Icon</th>
      <th>Tooltip</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <img alt="Show Used" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/hierarchy/supertypes.png?raw=true" />
      </td>
      <td>
        Show Used
      </td>
      <td>
        In Modules that `use <Alias>` or `use <Alias>, arg`, the elements from the last `quote` block in the
        `__using__/1` for `<Alias>` are injected.
      </td>
    </tr>
  </tbody>
</table>

##### Expanders

![Structure Expander Buttons](/screenshots/structure/button/Expanders.png?raw=true "Expanders")

The expanders expand or collapse all the elements in the Structure tool window.

<table>
  <thead>
    <tr>
      <th>Icon</th>
      <th>Tooltip</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <img alt="Expand All" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/expandall.png?raw=true" />
      </td>
      <td>
        Expand All
      </td>
      <td>
        Expand All Elements in the Structure tool window
      </td>
    </tr>
    <tr>
      <td>
        <img alt="Expand All" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/collapseall.png?raw=true" />
      </td>
      <td>
        Collapse All
      </td>
      <td>
        Collapse All Elements in the Structure tool window
      </td>
    </tr>
  </tbody>
</table>

##### Autoscrollers

![Structure Autoscroller Buttons](/screenshots/structure/button/Autoscrollers.png?raw=true "Autoscrollers")

The autoscrollers link together the editor tab's location and the Structure tool windows selected element.

<table>
  <thead>
    <tr>
      <th>Icon</th>
      <th>Tooltip</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <img alt="Autoscroll to Source" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/autoscrollToSource.png?raw=true" />
      </td>
      <td>
        Autoscroll to Source
      </td>
      <td>
        Clicking an element in the Structure tool window will scroll the editor window to the location of the
        corresponding source.
      </td>
    </tr>
    <tr>
      <td>
        <img alt="Autoscroll from Source" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/autoscrollFromSource.png" />
      </td>
      <td>
        Autoscroll from Source
      </td>
      <td>
        When moving the cursor in the editor window, the selected element in the Structure tool window will change to
        the corresponding element.
      </td>
    </tr>
  </tbody>
</table>

#### Elements

##### Icons

###### Time

The Time icons indicate whether the element is usable at compile time or runtime.

<table>
  <thead>
    <tr>
      <th>Icon</th>
      <th>Tooltip</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <img alt="Compile Time" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/compile.png?raw=true" />
      </td>
      <td>
        Compile Time
      </td>
      <td>
        The element is used or checked at compile time and (may) not even be accessible at run time, such as macros.
      </td>
    </tr>
    <tr>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        Runtime
      </td>
      <td>
        The element is usable at runtime, such as a function.
      </td>
    </tr>
  </tbody>
</table>

###### Visibility

The Visibility icons indicated whether the element is usable outside its defining Module.

<table>
  <thead>
    <tr>
      <th>Icon</th>
      <th>Tooltip</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        Public
      </td>
      <td>
        Public elements are accessible outside their defining Module.
      </td>
    </tr>
    <tr>
      <td>
        <img alt="Private" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_private.png?raw=true" />
      </td>
      <td>
        Private
      </td>
      <td>
        Private elements are only accessible in their defining Module.  The macros that define private elements usually
        end in <code>p</code>.
      </td>
    </tr>
  </tbody>
</table>

##### Call to Element

<table>
  <thead>
    <tr>
      <th rowspan="2">
        Call
      </th>
      <th colspan="7">
        Icons
      </th>
      <th rowspan="2">
        Text
      </th>
      <th rowspan="2">
        Description
      </th>
    </tr>
    <tr>
      <th>
        Macro Type
      </th>
      <th>
        Time
      </th>
      <th>
        Visibility
      </th>
      <th>
        Function
      </th>
      <th>
        Module Local
      </th>
      <th>
        Overridable
      </th>
      <th>
        Override
      </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="2">
        <code>def</code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        Groups together <code>def</code> with the same name and arity.
      </td>
    </tr>
    <tr>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
        <img alt="Module Local" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_plocal.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>[<code>(</code>][<code>ARGUMENTS</code>][<code>)</code>][when <code>...</code>]
      </td>
      <td>
        The function head for function clause, including the name, arguments, and <code>when</code> if present
      </td>
    </tr>
    <tr>
      <td>
        <code>defdelegate</code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <img alt="Module Local" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_plocal.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        defdelegate append_first: false<code>|</code>true, to: <code>ALIAS|ATOM</code>
      </td>
      <td>
        Groups together all the delegated functions for a single <code>defdelegate</code> call.
      </td>
    </tr>
    <tr>
      <td>
        <code><i>defdelegate</i> func(arg)<i>, to: ALIAS</i></code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        Groups together implied <code>def</code> and any explicit <code>@spec</code> for the given function head
        (<code>func(arg)</code>)
      </td>
    </tr>
    <tr>
      <td>
        <code><i>defdelegate</i> func(arg)<i>, to: ALIAS</i></code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
        <img alt="Module Local" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_plocal.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>func(arg)</code>
      </td>
      <td>
        The function head implied by the <code>defdelegate</code> list of function heads.
      </td>
    </tr>
    <tr>
      <td>
        <code>defexception</code>
      </td>
      <td>
        <img alt="Exception" src="https://github.com/JetBrains/intellij-community/blob/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/exceptionClass.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>RELATIVE_ALIAS</code>
      </td>
      <td>
        The exception has the same name as the parent Module, but will display with only the relative name (the last
        Alias without a <code>.</code>) with its location as the qualifying Alias.
      </td>
    </tr>
    <tr>
      <td>
        <code>defexception</code>
      </td>
      <td>
        <img alt="Struct" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/toolwindows/toolWindowStructure.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        %<code>RELATIVE_ALIAS</code>{}
      </td>
      <td>
        Exceptions are defined as structs, so any <code>defexception<code> also defines a struct with the same name.
      </td>
    </tr>
    <tr>
      <td>
        <code><i>defexception</i> list_or_keywords</code>
      </td>
      <td>
        <img alt="Field" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/nodes/field.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>: <code>DEFAULT_VALUE</code>
      </td>
      <td>
        The fields and default values (or <code>nil</code> if a list is used instead of a keyword list) for the struct
        as passed in the first argument to <code>defexception</code>.
      </td>
    </tr>
    <tr>
      <td>
        <code>defimpl PROTOCOL, for: MODULE</code>
      </td>
      <td>
        <img alt="Protocol" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/nodes/anonymousClass.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <img alt="Override" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/general/overridingMethod.png?raw=true" />
      </td>
      <td>
        <code>MODULE</code> (<code>PROTOCOL</code>)
      </td>
      <td>
        <code>defimpl</code> defines a protocol implementation that defines a Module that concatenates the
        <code>PROTOCOL</code> name and the <code>MODULE</code> name.  If no <code>:for</code> is given, then the outer
        Module is used.
      </td>
    </tr>
    <tr>
      <td rowspan="2">
        <code>defmacro</code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Compile Time" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/compile.png?raw=true" />
      </td>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        Groups together <code>defmacro</code> with the same name and arity.
      </td>
    </tr>
    <tr>
      <td>
      </td>
      <td>
        <img alt="Compile Time" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/compile.png?raw=true" />
      </td>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
        <img alt="Module Local" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_plocal.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>[<code>(</code>][<code>ARGUMENTS</code>][<code>)</code>][when <code>...</code>]
      </td>
      <td>
        The macro head for macro clause, including the name, arguments, and <code>when</code> if present
      </td>
    </tr>
    <tr>
      <td rowspan="2">
        <code>defmacrop</code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Compile Time" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/compile.png?raw=true" />
      </td>
      <td>
        <img alt="Private" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_private.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        Groups together <code>defmacrop</code> with the same name and arity.
      </td>
    </tr>
    <tr>
      <td>
      </td>
      <td>
        <img alt="Compile Time" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/compile.png?raw=true" />
      </td>
      <td>
        <img alt="Private" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_private.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
        <img alt="Module Local" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_plocal.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>[<code>(</code>][<code>ARGUMENTS</code>][<code>)</code>][when <code>...</code>]
      </td>
      <td>
        The macro head for macro clause, including the name, arguments, and <code>when</code> if present
      </td>
    </tr>
    <tr>
      <td>
        <code>defmacro</code> AND <code>defmacrop</code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Compile Time" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/actions/compile.png?raw=true" />
      </td>
      <td>
        <img alt="Unknown" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/runConfigurations/unknown.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        Groups together <code>defmacro</code> AND <code>defmacrop</code> with the same name and arity.  This will be a
        compile error, but is represented with
        <img alt="Unknown" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/runConfigurations/unknown.png?raw=true" />
        Unknown for the Visibility until corrected.
      </td>
    </tr>
    <tr>
      <td>
        <code>defmodule ALIAS</code>
      </td>
      <td>
        <img alt="Module" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/package.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>RELATIVE_ALIAS</code> (<code>QUALIFIER</code>)
      </td>
      <td>
        Top-level Modules show only the <code>ALIAS</code> with no location, while qualified Aliases or nested Modules
        show the <code>RELATIVE_ALIAS</code> and the <code>QUALIFIER</code> as the location.
      </td>
    </tr>
    <tr>
      <td>
        <code>defoverridable</code>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <img alt="Overridable" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/general/overridenMethod.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        Mark previously declared functions as overridable.  Overridable functions are listed as children of this
        element.
      </td>
    </tr>
    <tr>
      <td>
        <code><i>defoverridable</i> NAME: ARITY<i>, ...</i></code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Public" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_public.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
        <img alt="Overridable" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/general/overridenMethod.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        The <code>NAME</code> and <code>ARITY</code> of the function that is overridable.  Matches the icon and text
        for <code>def</code>, but with the addition of
        <img alt="Overridable" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/general/overridenMethod.png?raw=true" />
        Overridable
      </td>
    </tr>
    <tr>
      <td rowspan="2">
        <code>defp</code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Private" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_private.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        Groups together <code>def</code> with the same name and arity.
      </td>
    </tr>
    <tr>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Private" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_private.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
        <img alt="Module Local" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/c_plocal.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>[<code>(</code>][<code>ARGUMENTS</code>][<code>)</code>][when <code>...</code>]
      </td>
      <td>
        The function head for function clause, including the name, arguments, and <code>when</code> if present
      </td>
    </tr>
    <tr>
      <td>
        <code>def</code> AND <code>defp</code>
      </td>
      <td>
      </td>
      <td>
        <img alt="Runtime" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/general/run.png?raw=true" />
      </td>
      <td>
        <img alt="Unknown" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/runConfigurations/unknown.png?raw=true" />
      </td>
      <td>
        <img alt="Function" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/nodes/function.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>/<code>ARITY</code>
      </td>
      <td>
        Groups together <code>def</code> AND <code>defp</code> with the same name and arity.  This will be a
        compile error, but is represented with
        <img alt="Unknown" src="https://raw.githubusercontent.com/JetBrains/intellij-community/a034d3874d3f126b3de3d665ee18bc7e8abe5fb4/platform/icons/src/runConfigurations/unknown.png?raw=true" />
        Unknown for the Visibility until corrected.
      </td>
    </tr>
    <tr>
      <td>
        <code>defprotocol PROTOCOL</code>
      </td>
      <td>
        <img alt="Protocol" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/nodes/anonymousClass.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <img alt="Overridable" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/general/overridenMethod.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
        <code>PROTOCOL</code>
      </td>
      <td>
        The <code>protocol</code> name.  Functions required by the protocol are children of this element.
      </td>
    </tr>
    <tr>
      <td>
        <code>defstruct</code>
      </td>
      <td>
        <img alt="Struct" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/toolwindows/toolWindowStructure.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        %<code>RELATIVE_ALIAS</code>{}
      </td>
      <td>
        Structs have the same <code>RELATIVE_ALIAS<code> as their parent Module.
      </td>
    </tr>
    <tr>
      <td>
        <code><i>defstruct</i> NAME<i>:</i> DEFAULT_VALUE<i>, ...</i></code>
      </td>
      <td>
        <img alt="Field" src="https://raw.githubusercontent.com/JetBrains/intellij-community/master/platform/icons/src/nodes/field.png?raw=true" />
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
      </td>
      <td>
        <code>NAME</code>: <code>DEFAULT_VALUE</code>
      </td>
      <td>
        The fields and default values (or <code>nil</code> if a list is used instead of a keyword list) for the struct.
      </td>
    </tr>
  </tbody>
</table>

## Viewing Embedded Elixir Templates

There is currently no direct support for Embedded Elixir (`*.eex`) templates.

However, because the Elixir syntax is so similar to Ruby, you can use
the Ruby language support for RHTML/ERB to get some syntax highlighting support
in `*.eex` views.

Note that this involves disabling some of the support for Ruby, but
if you don't write Ruby, or if you write it in a different IDE (e.g. RubyMine),
it won't matter.

Here's the steps in Preferences (for OSX, other platforms may differ):

* Install the [standard Jetbrains Ruby plugin](https://confluence.jetbrains.com/display/RUBYDEV/RubyMine+and+IntelliJ+IDEA+Ruby+Plugin)
* Editor -> File Types -> RHTML: Add "`*.eex`" as type
* Editor -> Inspections -> Ruby -> Unresolved Ruby Reference: Uncheck
* Editor -> Inspections -> Ruby -> Double Quoted String: Uncheck

Some non-Ruby syntax (e.g. `->` or `do`) will still show as an
error, and of course none of the native Elixir support works, but most
things will highlight reasonably well.  Unfortunately it's not
possible to disable all error highlighting, but you can
[vote for this issue](https://youtrack.jetbrains.com/issue/IDEA-173521)
to try and get that fixed (click the "thumbs up" next to "Voters").

You *can* disable the errors on a per-file basis, though, with the
following steps:

* Open the `*.eex` file which is showing a Ruby syntax error inspection
* From the menu pick `Analyze -> Configure Current File Analysis`
* Move the "ruby" Highlighting Level slider to "None"


## Installation

### Inside IDE using JetBrains repository

1. Preferences
2. Plugins
3. Browse Repositories
4. Select Elixir
5. Install plugin
6. Apply
7. Restart the IDE

### Inside IDE using Github releases

#### In browser

1. Go to [releases](https://github.com/KronicDeth/intellij-elixir/releases).
2. Download the lastest zip.

#### In IDE

1. Preferences
2. Plugins
3. Install plugin from disk...
4. Select the downloaded zip.
5. Apply
7. Restart the IDE.

## Screenshots

![Color Settings](/screenshots/Color%20Settings.png?raw=true "Color Settings")
![New Elixir File](/screenshots/New%20Elixir%20File.png?raw=true "New Elixir File")

## Error reporting

If the plugin encounters an error, there is a custom error handler registered, so you can open a pre-populated issue in your browser.

1. Click the red error notification in bottom right corner of the IDE window.
![Fatal IDE Errors](/screenshots/error_handler/IDE%20Fatal%20Errors.png?raw=true "Fatal IDE Errors")
2. Fill in a description of what you were doing when the error occurred.
3. Click "Open Issue against https://github.com/KronicDeth/intellij-elixir"
4. The IDE will open your browser to https://github.com/KronicDeth/intellij-elixir/issues/new
![Write New Issue](/screenshots/error_handler/Write%20New%20Issue.png?raw=true "Write New Issue")
5. The title will be filled as `[auto-generated]`, but if you can summarize the issue, change the title.
6. If the "Fatal IDE Errors" dialog has Attachments, copy their contents to the `Attachments` section of the issue body.
7. **Review for IP disclosures. This will be public, so use your best judgement of how much of your code to post in the issue.**
8. Click the "Preview" tab to ensure the Markdown formatting looks correct.
9. Click "Submit new issue".

## Donations

If you would like to make a donation you can use Paypal:

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=Kronic%2eDeth%40gmail%2ecom&lc=US&item_name=Elixir%20plugin%20for%20IntelliJ%20IDEA&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted)

If you'd like to use a different donation mechanism (such as Patreon), please open an issue.

### Public Donors

I'd like to thank those who have donated to help support this project.

* Robin Hillard ([@robinhillard](https://github.com/robinhillard)) of [rocketboots.com](http://www.rocketboots.com)
* William De Melo Gueiros ([williamgueiros](https://github.com/williamgueiros))
* Gerard de Brieder ([@smeevil](https://github.com/smeevil)) of [govannon.nl](http://govannon.nl)
* [TreasureData](https://www.treasuredata.com/)

