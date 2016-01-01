<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Changelog](#changelog)
  - [v2.0.0](#v200)
  - [v1.2.1](#v121)
  - [v1.2.0](#v120)
  - [v1.1.0](#v110)
  - [v1.0.0](#v100)
  - [v0.3.5](#v035)
  - [v0.3.4](#v034)
  - [v0.3.3](#v033)
  - [v0.3.2](#v032)
  - [v0.3.1](#v031)
  - [v0.3.0](#v030)
  - [v0.2.1](#v021)
  - [v0.2.0](#v020)
  - [v0.1.4](#v014)
  - [v0.1.3](#v013)
  - [v0.1.2](#v012)
  - [v0.1.1](#v011)
  - [v0.1.0](#v010)
  - [v0.0.3](#v003)
  - [v0.0.2](#v002)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Changelog

## v2.0.0
* Enhancements
  * [#207](https://github.com/KronicDeth/intellij-elixir/pull/207) - [KronicDeth](https://github.com/KronicDeth)
    * Highlighters for
      * Kernel Functions
      * Kernel Macros
      * Kernel.SpecialForms Macros
  * [#219](https://github.com/KronicDeth/intellij-elixir/pull/219) - Test against Elixir v1.1.1 - [KronicDeth](https://github.com/KronicDeth)
  * [#221](https://github.com/KronicDeth/intellij-elixir/pull/221) - Highlight `after`, `catch`, `do`, `else`, `end, `fn`, and `rescue` as keywords - [KronicDeth](https://github.com/KronicDeth)
  * [#223](https://github.com/KronicDeth/intellij-elixir/pull/223), [#227](https://github.com/KronicDeth/intellij-elixir/pull/227) - Annotate Module Attributes - [KronicDeth](https://github.com/KronicDeth)
    * Documentation module attributes (`@doc`, `@moduledoc`, and `@typedoc`) are annotated as "Documentation Module Attributes" while all other module attributes are annotated as "Module Attributes".
    * The string or heredoc argument to a documentation module attribute (`@doc`, `@moduledoc`, and `@typedoc` is annotated as "Documentation Text"
    * Function names passed to `@callback`, `@macrocallback` or `@specification` are annotated as "Specification".
    * Variables/calls in the parameters and return of `@callback`, `@macrocallback`, `@specification` are annotated as "Type".
    * Parameters of `@opaque`, `@type`, `@typep` names are annotated as "Type Parameter"
    * Keyword keys from the `when` clause of `@callback`, `@macrocallback` or `@spec` definitions and their usage are annotated as "Type Parameters"
    * `@doc false`, `@moduledoc false`, and `@typedoc false` will annotate the `false` with a weak warning: "Will make documented invisible to the documentation extraction tools like ExDoc.".
* Bug Fixes 
  * [#206](https://github.com/KronicDeth/intellij-elixir/pull/206) - Change "edition" to "addition" in README. - [folz](https://github.com/folz)
  * [#225](https://github.com/KronicDeth/intellij-elixir/pull/225) - Sped up reparsing when [ENTER] is hit in the middle of comment by removing the custom error handling element, adjacentExpression, and going with the default error handling provided by JetBrains' OpenAPI. - [KronicDeth](https://github.com/KronicDeth)
  * [#226](https://github.com/KronicDeth/intellij-elixir/pull/226) - Fix `mix` version detection on Windows - [KronicDeth](https://github.com/KronicDeth)
* Incompatible Changes
  * [#225](https://github.com/KronicDeth/intellij-elixir/pull/225) - [KronicDeth](https://github.com/KronicDeth)
    * Removed "Add Newline" Quick Fix as it depended on `adjacentExpression` elements, which have now been removed to speed up error handling when comments become code.
    * Removed "Add Semicolon" Quick Fix as it depended on `adjacentExpression` elements, which have now been removed to speed up error handling when comments become code.

## v1.2.1
* Enhancements
  * [#204](https://github.com/KronicDeth/intellij-elixir/pull/204) - Keywords not at the end of no parentheses calls will be properly marked as errors. - [KronicDeth](https://github.com/KronicDeth)
* Bug Fixes
  * [#200](https://github.com/KronicDeth/intellij-elixir/pull/200) - Fix `IllegalStateException` for file delete and rename by giving `ElixirFile`s descriptive names for safe-refactoring displaying file usage. - [KronicDeth](https://github.com/KronicDeth)
  * [#201](https://github.com/KronicDeth/intellij-elixir/pull/201) - [KronicDeth](https://github.com/KronicDeth)
    * README states explicitly that the plugin works with *both* IntelliJ Community and Ultimate.
    * README states that the plugin is free.
  * [#202](https://github.com/KronicDeth/intellij-elixir/pull/202) - Prevent match error when typing `~` to start a sigil that is followed later by a `\n` by matching `EOL` in the `NAMED_SIGIL` state as a `BAD_CHARACTER` - [KronicDeth](https://github.com/KronicDeth)
  * [#204](https://github.com/KronicDeth/intellij-elixir/pull/204) - Keywords at the end of a no parentheses call that is surrounded by parentheses will not be marked as an error when that parenthetical group appears in the middle of an outer call. - [KronicDeth](https://github.com/KronicDeth)

## v1.2.0
* Enhancements
  * [#184](https://github.com/KronicDeth/intellij-elixir/pull/184) - If (1) you have intellij-erlang installed and (2) you have an atom in Erlang that starts with `Elixir.`, such as `'Elixir.Test'`, then intellij-elixir will annotate whether it can resolve the name to a `defmodule` call in Elixir files. - [KronicDeth](https://github.com/KronicDeth)
  * [#188](https://github.com/KronicDeth/intellij-elixir/pull/188) - Default SDK path for Linux and Windows - [zyuyou](https://github.com/zyuyou)
  * [#198](https://github.com/KronicDeth/intellij-elixir/pull/198) - [KronicDeth](https://github.com/KronicDeth)
    * Go To Declaration (`Cmd+Click`, `Cmd+B`, `Navigate > Declaration`) from Alias to `defmodule` where Alias is declared.
    * Index `defmodule`s for fast Go To Declaration in [elixir-lang/elixir](https://github.com/elixir-lang/elixir) and other large projects.
    * Find Usage for Alias in `defmodule`
* Bug Fixes
  * [#187](https://github.com/KronicDeth/intellij-elixir/pull/187) - Fix links to screenshots in README - [zhyu](https://github.com/zhyu)

## v1.1.0

* Enhancements
  * [#167](https://github.com/KronicDeth/intellij-elixir/pull/167) - [zyuyou](https://github.com/zyuyou)
    * `Build`
      * `Compile` an individual file
      * `Make Project` to build the entire project
    * `New`
      * `Elixir File` has new templates
        * `Empty module`
        * `Elixir Application`
        * `Elixir Supervisor`
        * `Elixir GenServer`
        * `Elixir GenEvent`
      * `Project > Elixir` creates a new Elixir project with an empty `lib` directory marked as source directory.
      * `Project from Existing Sources... `
        * `Create project from existing sources` sets up the project with SDK using a pre-existing directory.
        * `Import project from external model > Mix`
          * Fetches the the dependencies with the local version of `mix`
          * Marks `lib` directory as source
          * Marks `test` directory as test sources
    * `Run > Elixir Mix` to setup Run Configurations to run `mix` tasks. 

## v1.0.0
* Enhancements
  * [#168](https://github.com/KronicDeth/intellij-elixir/pull/168) - Update ant build on travis-ci.org to use IDEA 14.1.4 (from 14.0.2) - [KronicDeth](https://github.com/KronicDeth)
  * [#174](https://github.com/KronicDeth/intellij-elixir/pull/174) - Parser is verified to quote the same as native Elixir - [KronicDeth](https://github.com/KronicDeth)
* Bug Fixes
  * [#154](https://github.com/KronicDeth/intellij-elixir/pull/154) - Fix parsing of unary vs binary +/- with leading and trailing spaces and newlines - [KronicDeth](https://github.com/KronicDeth)
  * [#155](https://github.com/KronicDeth/intellij-elixir/pull/155) - Allow EOL between list arguments and `]` - [KronicDeth](https://github.com/KronicDeth)
  * [#156](https://github.com/KronicDeth/intellij-elixir/pull/156) - [KronicDeth](https://github.com/KronicDeth)
    * Relative identifiers after `.` that start with `and`, `or`, and `not` will be lexed as a single identifier instead
      of `and`, `or`, or `not` followed by another identifier.
    * `end` is allowed as a relative identifier after `.`
  * [#157](https://github.com/KronicDeth/intellij-elixir/pull/157) - Fix `(...)` as part of matched expression in no parentheses stab signature - [KronicDeth](https://github.com/KronicDeth)
  * [#158](https://github.com/KronicDeth/intellij-elixir/pull/158) - Allow multiple newlines to mark the end of an expression, but only one `;` - [KronicDeth](https://github.com/KronicDeth)
  * [#159](https://github.com/KronicDeth/intellij-elixir/pull/159) - Allow operators in function references (`<op>/<arity>`) for function captures (`&<op>/<arity>`) - [KronicDeth](https://github.com/KronicDeth)
  * [#160](https://github.com/KronicDeth/intellij-elixir/pull/160) - `unquote_splicing` is properly wrapped in `__block__` when in stab bodies - [KronicDeth](https://github.com/KronicDeth)
  * [#163](https://github.com/KronicDeth/intellij-elixir/pull/160) - Check for matching terminator in heredocs when determining white space type at beginning of line - [KronicDeth](https://github.com/KronicDeth)
  * [#170](https://github.com/KronicDeth/intellij-elixir/pull/170) - Allow <space>+<EOL> to count as addition - [KronicDeth](https://github.com/KronicDeth)
  * [#171](https://github.com/KronicDeth/intellij-elixir/pull/171) - Unary expressions inside parentheses are no longer marked `ambiguous_op: nil` - [KronicDeth](https://github.com/KronicDeth)
  * [#173](https://github.com/KronicDeth/intellij-elixir/pull/173) - Differentiate between `Qualifier.'relative'()` vs `Qualifier.'relative' ()` and `Qualifier."relative"()` vs `Qualifier."relative" ()` - [KronicDeth](https://github.com/KronicDeth)
  * [#176](https://github.com/KronicDeth/intellij-elixir/pull/176) - Fix link to Elixir website in README - [shalecraig](https://github.com/shalecraig)
  * [#178](https://github.com/KronicDeth/intellij-elixir/pull/178) - All tokens have human-readable names and/or expected characters for better error messages - [KronicDeth](https://github.com/KronicDeth)
* Incompatible Fixes
  * [#162](https://github.com/KronicDeth/intellij-elixir/pull/162) - New Elixir File has moved to the last item in the New File menu to preserve `CTRL+N ENTER` keyboard shortcut for `New > File` - [jaketrent](https://github.com/jaketrent)

## v0.3.5
* Enhancements
  * [#135](https://github.com/KronicDeth/intellij-elixir/pull/135) - `do` blocks (`do end) - [@KronicDeth](https://github.com/KronicDeth)
  * [#152](https://github.com/KronicDeth/intellij-elixir/pull/152) - Unmatched expressions (operations involving `do` block calls and normal matched expressions) - [Kronicdeth](https://github.com/KronicDeth)
* Bug Fixes
  * [#137](https://github.com/KronicDeth/intellij-elixir/pull/137) - Lex full atom instead of just identifier-like operator prefix (`:in<nospace>dex` before vs `:index` after) - [@KronicDeth](https://github.com/KronicDeth)
  * [#138](https://github.com/KronicDeth/intellij-elixir/pull/138) - `!` and `not` are properly wrapped in `__block__`s when in stab bodies - [@KronicDeth](https://github.com/KronicDeth)

## v0.3.4
* Enhancements
  * [#126](https://github.com/KronicDeth/intellij-elixir/pull/126) - Bracket at expression (`@foo[key]`) - [@KronicDeth](https://github.com/KronicDeth)
  * [#127](https://github.com/KronicDeth/intellij-elixir/pull/127) - Anonymous functions (`fn end`), stab clauses (`->`), and parentheticals (`(1 + 2)`) - [@KronicDeth](https://github.com/KronicDeth)
  * [#128](https://github.com/KronicDeth/intellij-elixir/pull/128) - Maps (`%{}`) and structs (`%User{}`) - [@KronicDeth](https://github.com/KronicDeth)
  * [#129](https://github.com/KronicDeth/intellij-elixir/pull/129) - Tuples (`{}`) - [@KronicDeth](https://github.com/KronicDeth)
  * [#130](https://github.com/KronicDeth/intellij-elixir/pull/130) - Bit strings (`<<>>`) - [@KronicDeth](https://github.com/KronicDeth)

## v0.3.3
* Enhancements
  * [#122](https://github.com/KronicDeth/intellij-elixir/pull/122) - [@KronicDeth](https://github.com/KronicDeth)
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
  * [#125](https://github.com/KronicDeth/intellij-elixir/pull/125) - Bracket expression (`foo[key]`) - [@KronicDeth](https://github.com/KronicDeth)
        
## v0.3.2
* Bug Fixes
  * [#121](https://github.com/KronicDeth/intellij-elixir/pull/121) - Fix `NoSuchElementException` when no suggested SDK home paths are available.  Thanks to [@zyuyou](https://github.com/zyuyou) for [reporting](https://github.com/KronicDeth/intellij-elixir/issues/120) - [@KronicDeth](https://github.com/KronicDeth)

## v0.3.1
* Enhancements
  * [#112](https://github.com/KronicDeth/intellij-elixir/pull/112) - File > New > Project From Existing Sources can be used in IntelliJ to setup the excludes, sources, tests, SDK and libraries for an Elixir project that has already been created with `mix new`. - [@KronicDeth](https://github.com/KronicDeth)
  * [#114](https://github.com/KronicDeth/intellij-elixir/pull/114) - Operators can be qualified function names - [@KronicDeth](https://github.com/KronicDeth)
  * [#118](https://github.com/KronicDeth/intellij-elixir/pull/114) - [@KronicDeth](https://github.com/KronicDeth)
    * Anonymous function calls (`.(...)`)
    * Inspection that marks errors when keywords aren't at end of list.

## v0.3.0
* Enhancements
  * [#108](https://github.com/KronicDeth/intellij-elixir/pull/108) - `\x` is marked as an error in CharLists, CharList Heredocs, Strings, and String Heredocs, but not in any sigils. - [@KronicDeth](https://github.com/KronicDeth)
  * [#111](https://github.com/KronicDeth/intellij-elixir/pull/111) - New Elixir File will automatically underscore the camel case module name when creating the file name and will convert qualifying aliases before the last `.` to directories - [@KronicDeth](https://github.com/KronicDeth)
* Incompatible Changes
  * [#111](https://github.com/KronicDeth/intellij-elixir/pull/111) - New Elixir File validates that the name is a valid Alias, so each `.` separated part must start with a capital letter.  Previous New Elixir File validated that the name was a valid path, and so forced the name to be lowercase. - [@KronicDeth](https://github.com/KronicDeth)

## v0.2.1
* Enhancements
  * [#105](https://github.com/KronicDeth/intellij-elixir/pull/105) - No parentheses function calls can occur as the right operand in binary infix operations or the sole operand of unary prefix operation. - [@KronicDeth](https://github.com/KronicDeth)
  * [#74](https://github.com/KronicDeth/intellij-elixir/pull/74) - [@KronicDeth](https://github.com/KronicDeth)
    * Function calls with neither parentheses nor `do` blocks that have at least 2 arguments: a positional argument and keyword arguments or 2 or more positional argument(s) followed by optional keyword arguments.
    * Inspection that marks errors for ambiguous commas
    * Inspection that marks errors for ambiguous parentheses
    * Quick Fix for the ambiguous parentheses to remove the space between the function name and the opening parentheses.
  * [#75](https://github.com/KronicDeth/intellij-elixir/pull/75) - [@KronicDeth](https://github.com/KronicDeth)
    * Inspection that marks errors for missing end-of-expressions (`;` and newlines) between expressions.
    * Quick Fix to insert `;` for missing end-of-expression.
    * Quick Fix to insert newline for missing end-of-expression.
* Bug Fixes
  * [#74](https://github.com/KronicDeth/intellij-elixir/pull/74) - Right hand-side of `dot_alias` and `dot_identifier`
    was translated incorrectly. Only Aliases and Identifiers are allowed now. [@KronicDeth](https://github.com/KronicDeth)

## v0.2.0
* Enhancements
 * [#73](https://github.com/KronicDeth/intellij-elixir/pull/73) - [@KronicDeth](https://github.com/KronicDeth)
   * New attributes for parts of numbers on Color Settings Page
     * Binary, Decimal, Hexadecimal, and Octal Digits
     * Decimal Exponent, Mark, and Separator
     * Invalid Binary, Decimal, Hexadecimal, and Octal Digits
       * 2-9, A-Z, and a-z will be parsed as invalid binary digits
       * 8-9, A-Z, and a-z will be parsed as invalid octal digits
       * G-Z and g-z will be parsed as invalid hexadecimal digits
     * Non-Decimal Base Prefix
       * Any letter other than b, o, or x, in either case, will be recognized as an invalid whole number base
     * Obsolete Non-Decimal Base Prefix (`B` for binary and `X` for hexadecimal)
   * Any digit, 0-9, A-Z, or a-z will be parsed as invalid for invalid whole number based numbers
   * Recovery for non-decimal whole numbers if the prefix is given, but no digits are given
* Incompatible Changes
 * [#73](https://github.com/KronicDeth/intellij-elixir/pull/73): Number attribute has been removed from Color Settings page - [@KronicDeth](https://github.com/KronicDeth)

## v0.1.4

* Enhancements
 * [#17](https://github.com/KronicDeth/intellij-elixir/pull/17): All valid escape sequences (`\<character>`, `\x<hexadecimal>`, `\x{<hexadecimal>}` are recognized. - [@KronicDeth](https://github.com/KronicDeth)
 * [#18](https://github.com/KronicDeth/intellij-elixir/pull/18): Support for creation of Elixir modules - [@abaire](https://github.com/abaire) ![New Elixir File](/screenshots/New%20Elixir%20File.png?raw=true "New Elixir File")
 * [#21](https://github.com/KronicDeth/intellij-elixir/pull/21): Use [pygments' elixir_example.ex](https://bitbucket.org/birkenfeld/pygments-main/src/102ed2526c384403fd0e0129b3e31832a6583ea1/tests/examplefiles/example_elixir.ex?at=default) supplied by [@alco](https://github.com/alco) for Color Settings Page - [@KronicDeth](https://github.com/KronicDeth)
 * [#25](https://github.com/KronicDeth/intellij-elixir/pull/25): `?` before any character or valid escape sequence will be recognized as a character token. - [@KronicDeth](https://github.com/KronicDeth)
 * [#35](https://github.com/KronicDeth/intellij-elixir/pull/35): `;` is recognized as EOL. `\r\n` and `\n` style EOL can be escaped with `\` and will be treated as whitespace.  - [@KronicDeth](https://github.com/KronicDeth)
 * [#38](https://github.com/KronicDeth/intellij-elixir/pull/38): Operator arity, associativity, and precedence - [@KronicDeth](https://github.com/KronicDeth)
 * [#39](https://github.com/KronicDeth/intellij-elixir/pull/38): Decimal integers and floats - [@KronicDeth](https://github.com/KronicDeth)
 * [#40](https://github.com/KronicDeth/intellij-elixir/pull/40): Identifiers (variable, function, and macro names) - [@KronicDeth](https://github.com/KronicDeth)
 * [#41](https://github.com/KronicDeth/intellij-elixir/pull/41): `...` identifier - [@KronicDeth](https://github.com/KronicDeth)
 * [#42](https://github.com/KronicDeth/intellij-elixir/pull/42): Aliases (module names) - [@KronicDeth](https://github.com/KronicDeth)
 * [#45](https://github.com/KronicDeth/intellij-elixir/pull/45): Keyword identifiers - [@KronicDeth](https://github.com/KronicDeth)
 * [#49](https://github.com/KronicDeth/intellij-elixir/pull/49): Empty Parentheses - [@KronicDeth](https://github.com/KronicDeth)
 * [#52](https://github.com/KronicDeth/intellij-elixir/pull/52): In Operator - [@KronicDeth](https://github.com/KronicDeth)
 * [#54](https://github.com/KronicDeth/intellij-elixir/pull/54): Dot Operator - [@KronicDeth](https://github.com/KronicDeth)
 * [#56](https://github.com/KronicDeth/intellij-elixir/pull/56): Keyword Lists - [@KronicDeth](https://github.com/KronicDeth)
 * [#70](https://github.com/KronicDeth/intellij-elixir/pull/70): Matched Expressions - [@KronicDeth](https://github.com/KronicDeth)
 * [#72](https://github.com/KronicDeth/intellij-elixir/pull/72): Regular Keywords (`end`, `false`, `fn`, `nil`, and `true`) - [@KronicDeth](https://github.com/KronicDeth)
* Bug Fixes
 * [#17](https://github.com/KronicDeth/intellij-elixir/pull/17): Sigil terminator escapes are recognized, so that sigils are no longer prematurely terminated. - [@KronicDeth](https://github.com/KronicDeth)
 * [#24](https://github.com/KronicDeth/intellij-elixir/pull/24): Comments do not consume EOL, so trailing comments don't cause error parsing expression on following line. - [@KronicDeth](https://github.com/KronicDeth)
 * [#36](https://github.com/KronicDeth/intellij-elixir/pull/36): Sigil modifiers now work on groups in addition to heredocs. - [@KronicDeth](https://github.com/KronicDeth)
 * [#47](https://github.com/KronicDeth/intellij-elixir/pull/47): `;` is separate from `EOL` and either or both can separate expressions, but only `EOL` can separate operators and operands for operations - [@KronicDeth](https://github.com/KronicDeth) 

## v0.1.3

* Bug Fixes
 * [#10](https://github.com/KronicDeth/intellij-elixir/pull/10): Blank lines are properly parsed as whitespace instead of bad characters. - [@KronicDeth](https://github.com/KronicDeth)
 * [#13](https://github.com/KronicDeth/intellij-elixir/pull/13): EOL is parsed as bad character in sigil name (after `~`) instead of causing the lexer to fail to match, which raised exceptions in Event Log. - [@KronicDeth](https://github.com/KronicDeth)

## v0.1.2

* Enhancements
 * Atoms with highlighting
    * Atom with double or single quotes to allow interpolation.  Double quotes are highlighted as 'String' while single
      quotes are highlighted as 'Char List'.  This may be changed in the future.
    * Literal atoms highlighted as 'Atom'.
    * Operator atoms highlighted as 'Atom'.

## v0.1.1

* Bug Fixes
  * Build using JDK 6 instead of 7 so that plugin will work by default on OSX Mavericks.

## v0.1.0

* Enhancements
  * [#3](https://github.com/KronicDeth/intellij-elixir/pull/3): Literal and interpolated sigils with highlighting - [@KronicDeth](https://github.com/KronicDeth)
    * CharList Sigils (`~c` and `~C`) highlighted as 'Char List' in Settings.
    * Regex Sigils (`~r` and `~R`) highlighted as 'Sigil' in Settings. **NOTE: Regex syntax is not internally highlighted yet**
    * String Sigils (`~s` and `~S`) highlighted as 'String' in Settings.
    * Word Sigils (`~w` and `~W`) highlighted as 'Sigil' in Settings.
    * Custom Sigils highlighted as 'Sigil' in Settings.
    * Modifiers are highlighted on Regex, Word, and Custom while modifiers aren't allowed on CharList and String Sigils.
* Bug Fixes
  * Single-quoted strings are correctly referred to as 'Character List' now instead of 'String' in Settings.
  * Double-quoted strings are correctly referred to as 'String' now instead of 'Interpolated String' in Settings.
  * Non-Heredoc CharLists and Strings can be multiline.
  * CharLists and Strings support interpolation and escape sequences.

## v0.0.3

* Enhancements
  * Single quoted strings with highlighting. ('String' in Color Settings.)
  * Double quoted strings with highlighting. ('Interpolated String' in Color Settings.)
     * Interpolation (`#{` and `}`) with highlighting. ('Expression Substitution Mark' in Color Settings.)
     * Escape sequences for `"` and `#` with highlighting. ('Escape Sequence' in Color Settings.)   

## v0.0.2

* Enhancements
  * Binary, Hexadecimal, and Octal numbers (including deprecated syntax) are recognized as numbers.
  * Syntax Highlighting for numbers.
  * Color Settings page for changing the color of comments and numbers for Elixir (Preferences > Editor > Colors & Fonts > Elixir).
* Bug Fixes
  * Parser no longer freezes IDE on tokens it doesn't understand.
  * White space at beginning of lines no longer leads to annotation errors.
  * White space and EOLs at beginning of file no longer lead to annotation errors.
