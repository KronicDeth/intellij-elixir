# Elixir plugin

[![Build Status](https://travis-ci.org/KronicDeth/intellij-elixir.svg?branch=master)](https://travis-ci.org/KronicDeth/intellij-elixir)

This is a plugin that adds support for [Elixir](http://http://elixir-lang.org/) to JetBrains IntelliJ IDEA platform IDEs
([0xDBE](http://www.jetbrains.com/dbe/), [AppCode](http://www.jetbrains.com/objc/),
[IntelliJ IDEA](http://www.jetbrains.com/idea/), [PHPStorm](http://www.jetbrains.com/phpstorm/),
[PyCharm](http://www.jetbrains.com/pycharm/), [Rubymine](http://www.jetbrains.com/ruby/),
[WebStorm](http://www.jetbrains.com/webstorm/))

**The parser is incomplete until [v1.0.0](https://github.com/KronicDeth/intellij-elixir/milestones/v1.0.0).  If you see
an odd error from valid Elixir code, it is most likely due to the incomplete parser.  Please subscribe to notifications
or comment on [Issue #6](https://github.com/KronicDeth/intellij-elixir/issues/6) if you want to be notified when the
parser is complete.**

## Features

### Project

**NOTE: The Project Settings is just bookkeeping and visual now.  No other features currently take advantage
of the SDK setting or the marked directories.  Theses Project Settings will be used for later features.**

#### From Existing Directory
**NOTE: This feature only works in IntelliJ IDEA as it depends on an extension point unavailable in language-specific
  IDEs, like Rubymine.**

If you've already created a project with `mix new`, you can load it as an Elixir project into the plugin.

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

##### Project Structure

![Project View](/screenshots/Project%20View.png?raw=true "Project View")

* Excluded
  * `_build` (Output from `mix`)
  * `rel` (Output from `exrm`)
* Sources
  * `lib`
* Test Sources
  * `test`

###### Project Settings

![Project Settings](/screenshots/project_structure/project_settings/Project.png?raw=true "Project Settings")

The Project Settings include
* Project Name
* Project SDK

###### Module Settings


![Module Settings](/screenshots/project_structure/project_settings/Module.png?raw=true "Module Settings")

The Module Settings include Marking directories as
* Excluded
* Sources
* Tests

### New Elixir Module

#### Unqualified

1. Right-click a directory (such as `lib` or `test` in the standard `mix new` layout)
2. Select New > Elixir File.
3. Enter an unqualified Module name, such as `MyModule`.

An underscored file (`lib/my_module.ex`) with the given module name will be created:

```elixir
defmodule MyModule do

end
```

#### Qualified

1. Right-click a directory (such as `lib` or `test` in the standard `mix new` layout).
2. Select New > Elixir File.
3. Enter a qualified Module name, such as `MyNamespace.MyModule`.

An underscored file will be created in an underscored directory (`lib/my_namespace/my_module.ex`) with the given module
name will be created:

```elixir
defmodule MyNamspace.MyModule do

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
* [End of Lines](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L305-L332) (`;`, `\n`, `\r\n`)
* [Escape Sequences](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_interpolation.erl#L71-L116) (`\\<character>`, `\\x<hexadecimal>`, or `\\x{<hexadecimal>}`)
* [Heredocs](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L223-L229) (`"""` or `'''`)
* [Identifiers](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L460-L470), in other words, variable, function and macro names.
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
* Stabs (`->`)
* Structs (`%MyStruct{}`)
* [Strings and Char List](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L231-L236) (`"` or `'`)
* [Tuples] (`{}`)
  
The syntax highlighting colors can be customized in the Color Settings page for Elixir (Preferences > Editor > Color & Fonts > Elixir).

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


#### Missing End-of-Expression

End-of-expressions (`;` or new lines) missing between expressions.

<figure>
  <img alt="Missing End-of-Expression" src="/screenshots/inspection/elixir/missing_end_of_expression/preferences.png?raw=true"/>
  <br/>
  <figcaption>
    Preferences &gt; Inspections &gt; Elixir &gt; Missing End-of-Expression
  </figcaption>
</figure>

<figure>
  <img alt="Missing End-of-Expression error" src="/screenshots/inspection/elixir/missing_end_of_expression/error.png?raw=true"/>
  <br/>
  <figcaption>
    Missing end-of-expression inspection marks the error over the two expressions that should be separated by either a
    `;` or new line. 
  </figcaption>
</figure>

<figure>
  <img alt="Missing End-of-Expression inspection" src="/screenshots/inspection/elixir/ambiguous_parentheses/inspection.png?raw=true"/>
  <br/>
  <figcaption>
    Mousing over the expressions marked as an error in red (or over the red square in the right gutter) will
    show the inspection describing the error.
  </figcaption>
</figure>

### Quick Fixes

Quick Fixes are actions IntelliJ can take to change your code to correct errors (accessed with Alt+Enter by default).

#### Add End-of-Expression

If an end-of-expression is missing, it can be added as a `;` or a newline with Alt+Enter.  (Will vary based on keymap.)

<figure>
  <img alt="Add `;` or newline between expressions" src="/screenshots/local_quick_fix/Add%20End-of-Expression.gif?raw=true"/>
  <br/>
  <figcaption>
    Hitting Alt+Enter on missing end-of-expression will bring up the Local Quick Fixes,
    "Add `;` for missing end-of-expression" and "Add newline for the missing end-of-expression".  Select the
    end-of-expression, hit enter to accept and add the missing end-of-expression.
  </figcaption>
</figure>

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

<form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
<input type="hidden" name="cmd" value="_s-xclick">
<input type="hidden" name="encrypted" value="-----BEGIN PKCS7-----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-----END PKCS7-----
">
<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
</form>

If you'd like to use a different donation mechanism (such as Patreon), please open an issue.
