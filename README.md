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
      - [Keywords appear before the end of list.](#keywords-appear-before-the-end-of-list)
    - [Quick Fixes](#quick-fixes)
      - [Remove space in front of ambiguous parentheses](#remove-space-in-front-of-ambiguous-parentheses)
    - [Commenter](#commenter)
    - [Building/Compiling](#buildingcompiling)
      - [Settings](#settings)
      - [Individual File](#individual-file)
      - [Project](#project-1)
    - [Live Templates](#live-templates)
    - [Run Configurations](#run-configurations)
      - [Mix Tasks](#mix-tasks)
    - [Go To Declaration](#go-to-declaration)
      - [Module](#module)
      - [Module Attribute](#module-attribute)
    - [Go To Symbol](#go-to-symbol)
    - [Find Usage](#find-usage)
      - [Module](#module-1)
      - [Module Attribute](#module-attribute-1)
    - [Refactor](#refactor)
      - [Rename](#rename)
        - [Module Attribute](#module-attribute-2)
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
  - [Installation](#installation)
    - [Inside IDE using JetBrains repository](#inside-ide-using-jetbrains-repository)
    - [Inside IDE using Github releases](#inside-ide-using-github-releases)
      - [In browser](#in-browser)
      - [In IDE](#in-ide)
  - [Screenshots](#screenshots)
  - [Error reporting](#error-reporting)
  - [Donations](#donations)
    - [Donors](#donors)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Elixir plugin

[![Build Status](https://travis-ci.org/KronicDeth/intellij-elixir.svg?branch=master)](https://travis-ci.org/KronicDeth/intellij-elixir)

This is a plugin that adds support for [Elixir](http://elixir-lang.org/) to JetBrains IntelliJ IDEA platform IDEs
([0xDBE](http://www.jetbrains.com/dbe/), [AppCode](http://www.jetbrains.com/objc/),
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
5. The plugin will automatically find the newest version of Elixir installed. (**NOTE: SDK detection only works for
   Linux, homebrew installs on OSX, and Windows.  [Open an issue](https://github.com/KronicDeth/intellij-elixir/issues)
   with information about Elixir install locations on your operating system and package manager to have SDK detection
   added for it.**)
6. If the automatic detection doesn't find your Elixir SDK or you want to use an older version, manually select select
    the directory above the `bin` directory containing `elixir`, `elixirc`, `iex`, and `mix`.
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

### Quick Fixes

Quick Fixes are actions IntelliJ can take to change your code to correct errors (accessed with Alt+Enter by default).

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

### Commenter

You can comment or uncomment the current line or selected block of source. By selecting a block of source first you can
quickly comment out and entire function if you're trying to track down a compiling or testing error that's not giving a
helpful line number.

Using the menus

1. Highlight one or more lines
2. Comment (or Uncomment) with one of the following:
  a. Code > Comment with Line Comment 
  b. On OSX the key binding is normally `Cmd+/`.  

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
4. Fill in the "Name" for the Run Configuration
5. Fill in the command (`mix` task) to run
   ![Edit Elixir Mix Run Configuration](/screenshots/features/run_configurations/mix_tasks/Edit.png?raw=true "Edit Elixir Mix Run Configuration")
6. Click "OK" to save the Run Configuration and close the dialog
7. Click the Run arrow in the Toolbar to run the `mix` task
   ![Run](/screenshots/features/run_configurations/mix_tasks/Toolbar%20Run%20Button.png?raw=true "Run Elixir Mix Run Configuration")
8. The Run pane will open, showing the results of the `mix` task.
    * If there is an error with a FILE:LINE stack frame, it will be a clickable link that will take you to that location
      ![Error link](/screenshots/features/run_configurations/mix_tasks/Error%20Link.png?raw=true "Clickable Error Link")

### Go To Declaration

Go To Declaration is a feature of JetBrains IDEs that allows you to jump from the usage of a symbol, such as a Module
Alias, to its declaration, such as the `defmodule` call.

#### Module

1. Place the cursor over an Alias
2. Activate the Go To Declaration action with one of the following:
  a. `Cmd+B`
  b. Select Navigate &gt; Declaration from the menu.
  c. `Cmd+Click`

If you hold `Cmd` and hover over the Alias before clicking, the target declaration will be shown.

[![Go To Declaration Demonstration](http://img.youtube.com/vi/nN-DMEe-BQA/0.jpg)](https://www.youtube.com/watch?v=nN-DMEe-BQA)

#### Module Attribute

1. Place the cursor over a `@module_attribute`
2. Activate the Go To Declaration action with one of the following:
  a. `Cmd+B`
  b. Select Navigate &gt; Declaration from the menu.
  c. `Cmd+Click`

If you hold `Cmd` and hover over the `@module_attribute` before clicking, the target declaration will be shown.

### Go To Symbol

Go To Symbol is a way to search for any of the following by name:

* Call definition clauses (`def`, `defp`, `defmacro`, and `defmacrop`)
* Callbacks (`@callback` and `@macrocallback`)
* Call definition specifications (`@spec`)
* Call definition heads (`foo(bar)`) for delegation (`defdelegate foo(bar), to: BAZ`)
* Implementations (`defimpl`)
* Protocols (`defprotocol`)

You can bring up Go To Symbol with the keyboard shortcut (⌥⌘O on OSX) or using the menus (Navigate > Symbol...).

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
  a.
    i. Right-click the module attribute
    ii. Select "Find Usages" from the context menu
  b. Select Edit &gt; Find &gt; Find Usages from the menu
  c. `Alt+F7`

### Refactor

#### Rename

##### Module Attribute

1. Place the cursor over the `@module_attribute` usage or declaration.
2. Active the Rename Refactoring action with one of the following:
  a.
    i. Right-click the module attribute
    ii. Select Refactoring from the context menu
    iii. Select "Rename..." from the Refactoring submenu
  b. `Shift+F6`
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

### Donors

I'd like to thank those who have donated to help support this project.

* Robin Hillard ([@robinhillard](https://github.com/robinhillard)) of [rocketboots.com](http://www.rocketboots.com)
* William De Melo Gueiros ([williamgueiros](https://github.com/williamgueiros))
* Gerard de Brieder ([@smeevil](https://github.com/smeevil)) of [govannon.nl](http://govannon.nl)
