<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Changelog](#changelog)
  - [v4.0.1-dev](#v401-dev)
  - [v4.0.0](#v400)
  - [v3.0.1](#v301)
  - [v3.0.0](#v300)
  - [v2.2.0](#v220)
  - [v2.1.0](#v210)
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

## v4.0.1-dev

* Enhancements
  * [#331](https://github.com/KronicDeth/intellij-elixir/pull/331) - [@KronicDeth](https://github.com/KronicDeth)
    * Allow `do end` blocks to fold to `do: ...`
    * Allow `->` operator and the right operand to fold to `-> ...`
    * Allow `@doc`, `@moduledoc` and `@typedoc` value to fold to `"..."`.
    * Fold runs of adjacent `alias`, `import`, `require`, or `use` to be followed to a single `alias`, `import`, `require`, or `use` followed by `...`. 
  * [#334](https://github.com/KronicDeth/intellij-elixir/pull/334) - Function separators - [@KronicDeth](https://github.com/KronicDeth)
    * Show a function separator (Preferences > Editor > General > Appearance > Show method separators) above the group of `@doc`, `@spec` and `def`, `defp`, `defmacro`, and `defmacrop` (call definition clauses) of the same name and arity range.  Arity range will be used if one of the call definition clauses uses default arguments.
  
* Bug Fixes
  * [#330](https://github.com/KronicDeth/intellij-elixir/pull/330) - Check if `parameter` is `null` before `Variable#execute` call in `Variable#execute(PsiElement[], ResolveState)`. - [@KronicDeth](https://github.com/KronicDeth)

## v4.0.0
* Enhancements
  * [#314](https://github.com/KronicDeth/intellij-elixir/pull/314) - Call references for unqualified no argument calls that work as variables or parameters - [@KronicDeth](https://github.com/KronicDeth)
    * Resolve and highlight parameter references
      * Resolve call definition clause (`def(macro)?p?`) parameters to themselves
      * Resolve call definition parameter with default to itself
      * Add Parameter ot Color Settings Page
      * Parameters in any macro with do block or keyword pair
    * Resolve and highlight variable references
      * Properly identifier variable declared in `for` comprehension as variable
      * Add Variable to Color Settings Page
      * Highlight bind quoted keyword key as Variable
      * Resolve references to earlier `&&` operands, which handles code that matches a variable and only uses the variable on success like [`((cwd = cwd()) && write_tmp_dir(cwd))`](https://github.com/elixir-lang/elixir/blob/ccf6d14e3ec2eb96090222dad6f395b5b9ab72ac/lib/elixir/lib/system.ex#L268)
      * Resolve variables from `destructure`
    * Detect bitstring segment options and don't treat them as variables.
      * Highlight bitstring segment type options as Type, the same highlight as used for `@type` names.
      * Don't generate (unresolved) references for bitstring segment options
    * Resolve `_` to only itself, no matter how many are used to reflect that it is non-binding, while `_<name>`  will resolve to `_<name>` as it does bind.
      * Add Ignored Variable to Color Settings Page
    * Reimplement module attribute renaming so that variable renaming can be implemented using a different validator for renaming (since module attribute names include the `@`).  Non-inplace renaming should also be supported, but inplace is preferred.  (There's a setting to turn off in-place renaming in JetBrains IDEs.)
    * `operation.infix.Normalized`
      * Normalizes leftOperand, operator, and rightOperand for an Infix operation that may have errors (in either operand).  If there is an error in the operand then its normalized value is `null`.
    * Keyword key type descriptions
      * Default to `"keyword key"`. 
      * Detect `bind_quoted:` usage and call those `"quote bound variable"`.
    * Add interfaces to unify matching of `Matched` and `Unmatched` form of operations when the code cares about the operator
      * `And`
      * `UnaryNonNumericOperation`
    * Add `processDeclarations` to support variable and parameter resolution using `PsiTreeUtil.treeWalkUp` and `PsiScopeProcessors`
      * `ElixirStabBody`
      * StabOperations
    * Treat variables and parameters as `NamedElements`, so they can be Rename Refactored.
    * Move reused Module and Function names to `org.elixir_lang.psi.name.{Module,Function}` constants.
    * Parameter and Variable completion  
  * [#318](https://github.com/KronicDeth/intellij-elixir/pull/318) - Highlight keyword keys (`key:` in `key: value`) that aren't quotes (`"key": value` or `'key': value`) as Atom. - [@KronicDeth](https://github.com/KronicDeth)
  * [#320](https://github.com/KronicDeth/intellij-elixir/pull/320) - [@KronicDeth](https://github.com/KronicDeth)
    * Show annotator applied highlights in the Preferences > Editor > Colors & Fonts > Elixir.
      * Errors
      * `Alias`
      * `Braces and Operators`
        * `Bit` (`<<` and `>>`)
        * `Braces` (`{` and `}`)
        * `Brackets` (`[` and `]`)
        * `Char Tokens` (`?`)
        * `Comma` (`,`)
        * `Dot` (`.`)
        * `Interpolation` (`#{` and `}`)
        * `Maps and Structs`
          * `Maps` (`%{` and `}`)
          * `Structs` (`%{` and `}` when used for struct.  The Alias is still highlighted using `Alias`)
        * `Operation Sign`
        * `Parentheses` (`(` and `)`)
        * `Semicolon` (`;`)
      * `Calls`
        * `Function` (currently only combined with `Predefined` to highlight `Kernel` functions.  Will be used later for all function calls once function references are implemented.)
        * `Macro` (curently only combined with `Predefined` to highlight `Kernel` and `Kernel.SpecialForms` macros. Will be used later for all macro calls once macro references are implemented.)
        * `Predefined` (Combined with `Function` to highlight `Kernel` functions.  Combined with `Macro` to highlight `Kernel` and `Kernel.SpecialForms` macros.)
      * `Escape Sequence`
      * `Module Attributes`
        * `Documentation` (Previously `Documentation Module Attributes`)
          * `Text` (Previously `Documentation Text`)
        * `Types`
          * `Callback` (`my_callback` in `@callback my_callback() :: :ok` or `my_macro_callback` in `@macrocallback my_macro_callback`)
          * `Specification` (`my_function` in `@spec my_function() :: :ok`)
          * `Type`
            * `typ` and `integer` in  `@type typ :: integer`
            * `parameterized` in `@type parameterized(type_parameter) :: type_parameter`
            * `typtyp` in `@opaque typtyp :: 1..10`
            * `typ` and `typtyp` in `@callback func(typ, typtyp) :: :ok | :fail`
            * `binary` and `utf8` in `<< "hello" :: binary, c :: utf8, x = 4 * 2 >> = "hello™1"`
        * `Type Parameters` (`type_parameter` in `@type parameterized(type_parameter) :: type_parameter`)      
      * `Numbers`
        * `Binary, Decimal, Hexadecimal, and Octal Digits` (Previously at top-level.)
        * `Decimal Exponent, Mark and Separator` (Previously at top-level)
        * `Invalid Binary, Decimal, Hexadecimal, and Octal Digits` (Previously at top-level.)
        * `Non-Decimal Base Prefix` (Previously at top-level.)
        * `Obsolete Non-Decimal Base Prefix`
      * `Variables`
        * `Ignored`
        * `Parameter`
        * `Variable`
    * Recover in expression until close of subexpression
      * `\n`
      * `\r\n`
      * `>>`
      * `]`
      * `}`
      * `)`
      * `;`
      * `->`
      * `end`
      * `after`
      * `catch`
      * `else`
      * `rescue`
    * Update Preferences > Editor > Colors & Fonts > Elixir example text's bitstring syntax to Elixir post-1.0.0 (Use `-` to separate segment options instead of a list.)
    * Use same algorithm for `ElixirStabBody` and `ElixirFile` because they are sequences of expressions.
    * Highlight atom keywords (`false`, `nil`, and `true`) as merge of `Atom` and `Keyword` text attributes.  If both only use foreground color, `Keyword` wins.
    * Annotate `QualifiableAlias` as `Alias`.
    * Highlight keyword list and map keywords (`<key>:`) as `Atom`.
    * Add `with` to highlighted special forms  
  * [#322](https://github.com/KronicDeth/intellij-elixir/pull/322) - Additional Text Attributes - [@KronicDeth](https://github.com/KronicDeth)
    * Default text attributes for "Darcula" and "Default" themes: almost every Text Attribute Key has a unique hue for the Foreground color.
    * Explain how to add `additionalTextAttributes` to `plugin.xml` in `CONTRIBUTING.md`
    * Group Textual Text Attribute Keys Together: Next "Textual" group is created and "Character List", "Escape Sequence", "Sigil", and "String" are moved under the group.
    * Describe relations between different text attributes in `COLOR_SCHEMA_DESIGN.xml`, so they can be applied to different base schemes, such as applying the current Darcula additonalTextAttributes to Default.
* Bug Fixes
  * [#314](https://github.com/KronicDeth/intellij-elixir/pull/314) - [@KronicDeth](https://github.com/KronicDeth)
    * Don't generate module attribute references for control attributes: Module attributes that control compilation or are predefined by the standard library: `@behaviour`, `@callback`, `@macrocallback`, `@doc`, `@moduledoc`, `@typedoc`, `@spec`, `@opaque`, `@type`, and `@typep`, should not have references because their uses are unrelated.
    * Drop requirement that there are 2 children and only require there be 1 and assume that is the Operator.
    * Don't count @(...) as a module attribute usage: Module attribute declarations are defined as `defmacro @(...)` in Kernel and that `@` should count as a function name, not a prefix for a module attribute name.
    * Allow `null` Module for Scratch File use scope
    * Default to `"call"` for Call type
    * Fix typo that had `*Two` operations using `Type` interface
    * Don't process `AccessExpression` declarations
  * [#316](https://github.com/KronicDeth/intellij-elixir/pull/316) - [@KronicDeth](https://github.com/KronicDeth)
    * Highlight `foo` in `@spec foo` as a type, which occurs while typing a new `@spec` before `::` can be typed.
    * Check if `leftOperand` is `null` even when `checkLeft` is `true` because `checkLeft` can be `true` and `leftOperand` is `null` when the `lastParent` is the operand or operation as a whole, but there is an error in the unnormalized `leftOperand` leading to the normalized `leftOperand` being `null`.
    * Check if reference is `null` before checking if it resolves to `null` when replacing module attribute usages with their value because `AtNonNumericOperation`s can have a `null` reference when they are non-referencing, like `@spec`.
  * [#317](https://github.com/KronicDeth/intellij-elixir/pull/317) - Leave normal highlighting for char tokens when highlighting types - [@KronicDeth](https://github.com/KronicDeth)
  * [#320](https://github.com/KronicDeth/intellij-elixir/pull/320) - [@KronicDeth](https://github.com/KronicDeth)
    * Stab operation parameter Use Scope is the stab operation.
    * Skip over `PsiLeafElement` when looking for variables because the `PsiLeafElement` is an error.
    * In a script file where the parent of a `Match` is a `PsiFile`, the `Match` Use Scope is the rest of the file.
    * Add `=` to `Operator Signs`
    * Skip `NoParenthesesKeywords` when highlighting types, which occurs when the `::` has no proper right operand and the following one-liner function clause with `do:` is parsed as the right operand.
    * Skip `DUMMY_BLOCK` when looking for Variable, which prevents walking through errors.
    * Use `Normalized` pattern for `Prefix`, so that the operand is `null` when only the operator matches or the operand has errors.
    * Work-around Phoenix .ex templates that contain EEX: if `<%=` from EEX is detected, don't throw error when `Modular` can't be found.
    * Fix capitalization error in example text
  * [#323](https://github.com/KronicDeth/intellij-elixir/pull/323) - Build `jps-builder` using only Java 1.6 compatible `.class`es - [@KronicDeth](https://github.com/KronicDeth)
    * In IntelliJ 14.1, all of `openapi.jar` targets Java 1.6 (with `MAJOR.MINOR` `50.0`), but in IntelliJ 2016.1, some of `openapi.jar` targets only Java 1.8 (with `MAJOR.MINOR` `52.0`), since `jps-builders` require parts of `openapi.jar` and must target Java 1.6 even for IntelliJ 2016.1, the `52.0` `.class`es needed to be ported into `org.elixir_lang.jps.builder`, so that the `52.0` version in `openapi.jar` wouldn't be attempted to be loaded.
      
      This ended up being 5 classes:
    
      * `ExecutionException`
      * `GeneralCommandLine`
      * `ParametersList`
      * `ParamsGroup`
      * `ProcessNotCreatedException`
    
      Only `GeneralCommandLine` was used directly, all others are dependencies of it.
* Incompatible Changes
  * [#320](https://github.com/KronicDeth/intellij-elixir/pull/320) - [@KronicDeth](https://github.com/KronicDeth)
    * Preferences > Editor > Colors & Fonts > Elixir restructured to group together related highlights and to match grouping used for Colors & Fonts > Language Defaults and Colors & Fonts > Java.
      * `Documentation Module Attributes` renamed to `Module Attributes` > `Documentation`
      * `Documentation Text` renamed to `Module Attributes > Documentation > Text`
      * `Expression Substitution Mark` renamed to `Braces and Operators > Interpolation`.
      * The following are now nested under `Numbers` instead of being at the top-level:
        * `Binary, Decimal, Hexadecimal, and Octal Digits`
        * `Decimal Exponent, Mark and Separator` 
        * `Invalid Binary, Decimal, Hexadecimal, and Octal Digits`
        * `Non-Decimal Base Prefix`
        * `Obsolete Non-Decimal Base Prefix`
  * [#322](https://github.com/KronicDeth/intellij-elixir/pull/322) - "Character List", "Escape Sequence", "Sigil", and "String" are moved under the new "Textual" group. - [@KronicDeth](https://github.com/KronicDeth)
  * [#324](https://github.com/KronicDeth/intellij-elixir/pull/324) - Group Numbers subcategories and lower display name verbosity - [@KronicDeth](https://github.com/KronicDeth)
    * "Numbers > Binary, Decimal, Hexadecimal, and Octal Digits" renamed to "Numbers > Digits > Valid"
    * "Numbers > Invalid Binary, Decimal, Hexadecimal, and Octal Digits" renamed to "Numbers > Digits > Invalid"
    * "Numbers > Non-Decimal Base Prefix" renamed to "Numbers > Base Prefix > Non-Decimal"
    * "Numbers > Obsolete Non-Decimal Base" renamed to "Numbers > Base Prefix > Obsolete > Non-Decimal"
## v3.0.1
* Bug Fixes
  * [#287](https://github.com/KronicDeth/intellij-elixir/pull/287) - Use the error reporter logger instead of plain `assert` in `Prefix#operator`.  **NOTE: This does not address error recovery recovery since I don't have a regression test case.** - [@KronicDeth](https://github.com/KronicDeth)
  * [#283](https://github.com/KronicDeth/intellij-elixir/pull/283) - All function name elements act as `PsiNameIdentifier`s now even if they don't resolve, but that means they all need to support `FindUsagesProvider#getType`, which they don't, so use a placeholder of "unknown call type" for any `Call` that can't be matched and "unknown element" for anything else. - [@KronicDeth](https://github.com/KronicDeth)
  * [#284](https://github.com/KronicDeth/intellij-elixir/pull/284) - Enumerate all Kernel Functions, Macros, and Special Forms in the Syntax Highlighting section of the README, so that users searching for which category controls highlighting a given call can find it. - [@KronicDeth](https://github.com/KronicDeth)
  
## v3.0.0
* Enhancements
  * [#257](https://github.com/KronicDeth/intellij-elixir/pull/257) - [@KronicDeth](https://github.com/KronicDeth)
    * Go To Symbol (⌥⌘O)
      * Call definition clauses (`def`, `defp`, `defmacro`, and `defmacrop`)
      * Callbacks (`@callback` and `@macrocallback`)
      * Call definition specifications (`@spec`)
      * Call definition heads (`foo(bar)`) for delegation (`defdelegate foo(bar), to: BAZ`)
      * Implementations (`defimpl`)
      * Protocols (`defprotocol`)
    * Go To Declaration for Aliases now uses the same `isModular` checks as the stubbing for the index uses for Go To Symbol.
  * [#263](https://github.com/KronicDeth/intellij-elixir/pull/263) - Build against 14.0, 14.1, 15.0, and 2016.1 on travis-ci to ensure continued compatibility. - [@KronicDeth](https://github.com/KronicDeth)
  * [#273](https://github.com/KronicDeth/intellij-elixir/pull/273) - Error reporting for type highlighter gives the Class, Excerpt and Full Text when an unknown element type is encountered as the Excerpt alone is not enough sometimes. - [@KronicDeth](https://github.com/KronicDeth)
  * [#275](https://github.com/KronicDeth/intellij-elixir/pull/275) - [@KronicDeth](https://github.com/KronicDeth)
    * Custom error handling that will open an issue against https://github.com/KronicDeth/intellij-elixir with the exception messsage and stacktrace filled in.
    * Changed `NotImplementedExceptions` and (some) `assert`s to logging custom error message that include the `PsiElement` text and the containing file as an attachment.  The files make the URL too big for the error handler to put the file contents in when opening the browser with the error handler, so the issue body instead explains how to get the attachment text out of IntelliJ's "IDE Fatal Errors"
  * [#276](https://github.com/KronicDeth/intellij-elixir/pull/276) - Update to Grammar Kit 1.3.0. - [@KronicDeth](https://github.com/KronicDeth)
* Bug Fixes
  * [#256](https://github.com/KronicDeth/intellij-elixir/pull/256) - Fix Elixir Mix Run configuration not persisting past restart - [@zyuyou](https://github.com/zyuyou)
  * [#259](https://github.com/KronicDeth/intellij-elixir/pull/259) - Allow `Infix#operator` to work on operations with errors, which eliminates the `AssertionError` reported when typing infix operation and they are incomplete - [@KronicDeth](https://github.com/KronicDeth)
  * [#259](https://github.com/KronicDeth/intellij-elixir/pull/260) - Add Keywords to the Preferences > Editor > Colors & Fonts > Elixir settings page, so it can be customized for just Elixir instead of having to change Preferences > Editor > Colors & Fonts > General > Keyword - [@KronicDeth](https://github.com/KronicDeth)
  * [#263](https://github.com/KronicDeth/intellij-elixir/pull/263) - Ensure compatibility from 14.0 to 2016.1 - [@KronicDeth](https://github.com/KronicDeth)
    * Use `TextAttributesKey`s that aren't deprecated in 2016.1 and work back to 14.1
         
      All of `CodeInsightColors` is deprecated, so all constants from there had to be replaced.  Unfortunately, the recommended replacements don't have the same color as the original, so I used different `DefaultLanguageHighlighterColors` constants for some.
    
      "Module Attribute" is now based on `DefaultLanguageHighlighterColors.CONSTANT` (which is purplish in Darcula) instead of the recommended `METADATA`, which is yellow.  Although module attributes don't have to be constant since they can be set to accumulate, often they are used as constants and not really as metadata, since they are just data then.  All the `metadata` uses of module attributes have a separate color.
    
      "Specification" is now based on `DefaultLanguageHighlighterColors.FUNCTION_DECLARATION`, which maintains the golden color that `CodeInsightColors.METHOD_DECLARATION_ATTRIBUTES` had.
    
      "Type" is now based on `DefaultLanguageHighlighterColors.METADATA`, which is bright yellow unlike `CodeInsightColors.ANNOTATION_ATTRIBUTE_NAME_ATTRIBUTES`, which was a bright white.
    
      "Type Parameter" is now based on `DefaultLanguageHighlighterColors.PARAMETER`, which unfortunately has no attributes associated with it, but the constant name was too good a fit not to use, so if you want the old color, you'll need to customize it yourself.
    * Restore compatibility with the IntelliJ IDEA 14.0 release line
      * By using reflection to call `FileTemplateManager#getInstance` if `FileTemplateManager#getDefaultInstance` is not available
      * By calling `FileChooserDescriptorFactory#createSingleLocalFileDescriptor` (which works in 14.0 through 2016.1) instead of `FileChooserDescriptorFactory#createSingleFileDescriptor` (which only works in 14.1 through 2016.1)
  * [#264](https://github.com/KronicDeth/intellij-elixir/pull/264) - Use more human-readable error message for Elixir File action - [@KronicDeth](https://github.com/KronicDeth)
    * Instead of showing the regular expression pattern, which may be confusing to new developers, explain in English the expected pattern. I also included the description, which explains how nesting is mapped to directories, of the action since it doesn't actually show up in the dialog otherwise.
  * [#265](https://github.com/KronicDeth/intellij-elixir/pull/265) - Check if a file exists before allowing Elixir Module to be created. If it exists, show an error with the conflicting path. - [@KronicDeth](https://github.com/KronicDeth)
  * [#272](https://github.com/KronicDeth/intellij-elixir/pull/272) - Fix (one cause) of `AssertionError` in `GoToSymbolContributor` when the `Modular` (`defimpl`, `demodule`, `defprotocol`, and `quote`) could not be resolved due a `def` being surrounded by a `for` comprehension, which is common in Elixir libraries as was the case for `Postgrex`: any enclosing `for` comprehension(s) will now be ignored and the next enclosing macro will be checked to see if it is a `Modular`. - [@KronicDeth](https://github.com/KronicDeth)
  * [#273](https://github.com/KronicDeth/intellij-elixir/pull/273) - While typing, before `:` in keyword pairs, after the `when`, such as in `@spec foo(id) :: id when id` before finishing typing `@spec foo(id) :: id when id: String.t`, the keyword key will be properly highlighted as a Type Parameter - [@KronicDeth](https://github.com/KronicDeth)
  * [#276](https://github.com/KronicDeth/intellij-elixir/pull/276) - [@KronicDeth](https://github.com/KronicDeth)
    * Properly handle the `Infix#rightOperand` being `null` due to the Pratt Parser matching up through the operator and then ignoring the mismatched right operand, which leads to the `Infix` having only 2 elements: the left operand and the operator.
    * `@doc` and other module attributes appearing as the right operand of `@type name ::` will be ignored as it is common when adding a new type above pre-existing, documented functions.
    * Only error in `Infix#leftOperand` if there are not 2-3 children for `Infix` instead of a strict 3.
* Incompatible Changes
  * [#276](https://github.com/KronicDeth/intellij-elixir/pull/276) - Drop support for IntelliJ 14.0 because the parser generated by Grammar Kit 1.3.0 is not compatible with the OpenAPI libraries shipped in IntelliJ 14.0.  Still compatible with 14.1 through 2016.1. - [@KronicDeth](https://github.com/KronicDeth)

## v2.2.0
* Enhancement
  * [#240](https://github.com/KronicDeth/intellij-elixir/pull/240) - Code Commenter - [qertoip](https://github.com/qertoip)
  * [#243](https://github.com/KronicDeth/intellij-elixir/pull/243), [#248](https://github.com/KronicDeth/intellij-elixir/pull/248) - Structure View - [KronicDeth](https://github.com/KronicDeth)
    * Controls
      * Scroll to Source and Scroll From Source
      * Sorting
        * Runtime (functions) vs Compile (macros)
        * Visibility (public [`def`, `macro`, etc] vs private [`defp`, `macrop` and `@typep`])
        * Alphabetical
      * Show Used - injects structure from `use Alias` call's `__using__`'s `quote` into the call site Structure
    * Elements
      * Callbacks (`@callback`) show their name/arity and then a nest spec
      * CallDefinition groups together CallDefinitionClause of the same name/arity
      * CallDefinitionClause (`def`, `defp`, `macro`, and `macrop`) shows the head of each definition under the CallDefinition.
      * CallDefinitionSpecification (`@spec`) show the type specification for a CallDefinition
      * CallReference `name: arity` shows the name/arity for a `defoverridable`.
      * Delegation shows all the `:append_first` and `:to` options with the implied Function Delegation nested underneath
      * Exception (`defexception`) show the implicit struct and nest any callback functions, such as `exception/1` or `message/1`.
      * Overridable `defoverridable` tracks overridable functions and is used to mark CallDefinitions are overrides.
      * Quote `quote do end` models quote blocks so they can be injected `use Alias` sites.
      * Use `use Alias` show `use` calls.    
  * [#241](https://github.com/KronicDeth/intellij-elixir/pull/241) - Live Templates - [pfitz](https://github.com/pfitz)
  * [#220](https://github.com/KronicDeth/intellij-elixir/pull/220) - Added clarification to "Import Project from External Model" that `mix.bat` should be used instead of `mix` - [f-lombardo](https://github.com/f-lombardo)
  * [#244](https://github.com/KronicDeth/intellij-elixir/pull/244) - Get the Elixir version directly from `System.build_info[:version]` instead of processing the formatted output of `elixir --version` as the build info version is more stable - [KronicDeth](https://github.com/KronicDeth)
* Bug Fixes
  * [#244](https://github.com/KronicDeth/intellij-elixir/pull/244) - Elixir version parsing handles both pre and build numbers if present by using the same regular expression as Elixir itself uses for the `Version` module - [KronicDeth](https://github.com/KronicDeth)
  * [#245](https://github.com/KronicDeth/intellij-elixir/pull/245) - Better error handling in Structure View - [KronicDeth](https://github.com/KronicDeth)

## v2.1.0
* Enhancement
  * [#236](https://github.com/KronicDeth/intellij-elixir/pull/236) - `\u` in strings and char lists for unicode mapping - [KronicDeth](https://github.com/KronicDeth)
  * [#237](https://github.com/KronicDeth/intellij-elixir/pull/237) - Test against Elixir 1.1.1 and 1.2.0 - [KronicDeth](https://github.com/KronicDeth)
  * [#233](https://github.com/KronicDeth/intellij-elixir/pull/233) - More flexible `elixir --version` parsing: works with `elixir` 1.2.0 and earlier - [bitgamma](https://github.com/bitgamma)
* Bug Fixes
  * [#231](https://github.com/KronicDeth/intellij-elixir/pull/231) - Update IntelliJ to 14.1.6 to fix 403 errors in Travis-CI build - [sholden](https://github.com/sholden)

## v2.0.0
* Enhancements
  * [#207](https://github.com/KronicDeth/intellij-elixir/pull/207) - [KronicDeth](https://github.com/KronicDeth)
    * Highlighters for
      * Kernel Functions
      * Kernel Macros
      * Kernel.SpecialForms Macros
  * [#219](https://github.com/KronicDeth/intellij-elixir/pull/219) - Test against Elixir v1.1.1 - [KronicDeth](https://github.com/KronicDeth)
  * [#221](https://github.com/KronicDeth/intellij-elixir/pull/221) - Highlight `after`, `catch`, `do`, `else`, `end`, `fn`, and `rescue` as keywords - [KronicDeth](https://github.com/KronicDeth)
  * [#223](https://github.com/KronicDeth/intellij-elixir/pull/223), [#227](https://github.com/KronicDeth/intellij-elixir/pull/227) - Annotate Module Attributes - [KronicDeth](https://github.com/KronicDeth)
    * Documentation module attributes (`@doc`, `@moduledoc`, and `@typedoc`) are annotated as "Documentation Module Attributes" while all other module attributes are annotated as "Module Attributes".
    * The string or heredoc argument to a documentation module attribute (`@doc`, `@moduledoc`, and `@typedoc`) is annotated as "Documentation Text"
    * Function names passed to `@callback`, `@macrocallback` or `@spec` are annotated as "Specification".
    * Variables/calls in the parameters and return of `@callback`, `@macrocallback`, `@spec` are annotated as "Type".
    * Parameters of `@opaque`, `@type`, `@typep` names are annotated as "Type Parameter"
    * Keyword keys from the `when` clause of `@callback`, `@macrocallback` or `@spec` definitions and their usage are annotated as "Type Parameters"
    * `@doc false`, `@moduledoc false`, and `@typedoc false` will annotate the `false` with a weak warning: "Will make documented invisible to the documentation extraction tools like ExDoc.".
  * [#228](https://github.com/KronicDeth/intellij-elixir/pull/228) - Module attributes resolution and refactoring - [KronicDeth](https://github.com/KronicDeth)
    * Go To Definition for module attributes.
    * Module attribute completion (**NOTE: works after typing first character after `@`.  To see all module attributes, type a character after `@`, then delete the character to get the full list.**)
    * Module attributes that can't be resolved will have "Unresolved module attribute" error annotation (i.e. red squiggly underline).
    * Find Usages of module attributes from their declarations.
    * Rename module attributes inline (editing the name in the declaration will change the name at the usage site at the same time without a dialog).
    * The module attribute value (from the declaration site) will be folded into the usage site.  It can be reverted to the literal `@module_name` text by clicking the + to unfold.
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
