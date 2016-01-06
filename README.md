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
    - [Syntax Highlighting](#syntax-highlighting)
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
    - [Building/Compiling](#buildingcompiling)
      - [Settings](#settings)
      - [Individual File](#individual-file)
      - [Project](#project-1)
    - [Run Configurations](#run-configurations)
      - [Mix Tasks](#mix-tasks)
    - [Go To Declaration](#go-to-declaration)
      - [Module](#module)
      - [Module Attribute](#module-attribute)
    - [Find Usage](#find-usage)
      - [Module](#module-1)
      - [Module Attribute](#module-attribute-1)
    - [Refactor](#refactor)
      - [Rename](#rename)
        - [Module Attribute](#module-attribute-2)
  - [Installation](#installation)
    - [Inside IDE using JetBrains repository](#inside-ide-using-jetbrains-repository)
    - [Inside IDE using Github releases](#inside-ide-using-github-releases)
      - [In browser](#in-browser)
      - [In IDE](#in-ide)
  - [Screenshots](#screenshots)
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
8. Ensure the correct "Mix Path" is detected
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
    the directory above the `bin` directory containing `elixir`, `elixirc`, `iex`, and `mix`.
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

### Syntax Highlighting

Syntax highlighting for the following tokens:

* [Aliases](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L454), in other words, module names.
* [Atoms](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L238-L303) (`:`, `:''`, or `:""`)
* Anonymous functions (`fn end`)
* Access/Bracket expressions (`foo[key]` and `@foo[key]`)
* Binaries/Bit Strings (`<<>>`)
* [Character Tokens](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L166-L221) (`?<character>` or `?<escape_sequence>`)
* [Comments](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L139-L143) (`#`)
* Documentation Module Attributes (`@doc`, `@moduledoc`, and `@typedoc`)
* Documentation Text (the string or heredoc value to `@doc`, `@moduledoc`, and `@typedoc`)
* [End of Lines](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L305-L332) (`;`, `\n`, `\r\n`)
* [Escape Sequences](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_interpolation.erl#L71-L116) (`\\<character>`, `\\x<hexadecimal>`, or `\\x{<hexadecimal>}`)
* [Heredocs](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L223-L229) (`"""` or `'''`)
* [Identifiers](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L460-L470), in other words, variable, function and macro names.
* Kernel Functions
* Kernel Macros
* Kernel.SpecialForms Macros
* Keywords (`after`, `catch`, `do`, `else`, `end`, `fn`, and `rescue`)
* Maps (`%{}` and `%{ current | <update> }`)
* Numbers
  * Binary (`0b`) with invalid digit highlighting and missing digit recovery
  * Decimal with invalid digit highlighting
  * Hexadecimal (`0x`) with invalid digit highlighting and missing digit recovery
  * Obsolete Binary (`0B`) with invalid digit highlighting and missing digit recovery
  * Obsolete Hexadecimal (`0X`) with invalid digit highlighting and missing digit recovery
  * Octal (`0o`) with invalid digit highlighting and missing digit recovery
  * Unknown Non-Decimal (`0[A-Za-z]`) with invalid digit highlighting and missing digit recovery
* [Operators](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L343-L424) with [arity, associativity, and precedence](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L44-L71):
  * Addition (`+` and `-`)
  * And (`&&`, `&&&`, and `and`)
  * Arrow (`|>`, `<<<`, `>>>`, `~>>`, `<<~`, `~>`, `<~`, `<~>`, and `<|>`)
  * Association (`=>`)
  * At (`@`)
  * Capture (`&`)
  * Comparison (`==`, `!=`, `=~`, `===`, and `!==`)
  * Dot (`.`)
  * Hat (`^^^`)
  * In (`in`)
  * In Match (`<-` and `\\`)
  * Match (`=`)
  * Multiplication (`*` and `/`)
  * Or (`||`, `|||`, and `or`)
  * Pipe (`|`)
  * Relational (`<`, `>`, `<=`, and `>=`)
  * Stab (`->`)
  * Two (`++`, `--`, `..`, and `<>`)
  * Type (`::`)
  * Unary (`+`, `-`, `!`, `^`, `not`, and `~~~`)
  * When (`when`)
* Parentheticals (`(1 + 2)`)
* [Regular Keywords](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L963-L968) (`end`, `false`, `fn`, `nil`, and `true`)
* [Sigils](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L139-L143) (`~`)
  * CharList Sigils (`~c` and `~C`)
  * Regex Sigils (`~r` and `~R`)
  * String Sigils (`~s` and `~S`)
  * Word Sigils (`~w` and `~W`)
  * Custom Sigils (`~<lower_case_character>` and `~<UPPER_CASE_CHARACTER>`)
* Specifications (function names passed to `@callback`, `@macrocallback` or `@spec`
* Stabs (`->`)
* Structs (`%MyStruct{}`)
* [Strings and Char List](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L231-L236) (`"` or `'`)
* [Tuples] (`{}`)
* Type (Variables/calls in the parameters and return of `@callback`, `@macrocallback`, `@spec`)
* Type Parameters
  * Parameters of `@opaque`, `@type`, `@typep` name
  * Keyword keys from the `when` clause of `@callback`, `@macrocallback` or `@spec` definitions and their usage
  
The syntax highlighting colors can be customized in the Color Settings page for Elixir (Preferences > Editor > Color & Fonts > Elixir).

![Module Attribute annotator](/screenshots/annotator/Module%20Attribute.png?raw=true "Module Attribute annotator")

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

## Donations

If you would like to make a donation you can use Paypal:

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=Kronic%2eDeth%40gmail%2ecom&lc=US&item_name=Elixir%20plugin%20for%20IntelliJ%20IDEA&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted)

If you'd like to use a different donation mechanism (such as Patreon), please open an issue.

### Donors

I'd like to thank those who have donated to help support this project.

* Robin Hillard ([@robinhillard](https://github.com/robinhillard)) of [rocketboots.com](http://www.rocketboots.com)
* William De Melo Gueiros ([williamgueiros](https://github.com/williamgueiros))
* Gerard de Brieder ([@smeevil](https://github.com/smeevil)) of [govannon.nl](http://govannon.nl)
