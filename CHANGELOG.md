<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Changelog](#changelog)
  - [v4.3.0](#v430)
    - [Enhancements](#enhancements)
    - [Bug Fixes](#bug-fixes)
  - [v4.2.0](#v420)
    - [Enhancements](#enhancements-1)
    - [Bug Fixes](#bug-fixes-1)
  - [v4.1.0](#v410)
    - [Enhancements](#enhancements-2)
    - [Bug Fixes](#bug-fixes-2)
  - [v4.0.0](#v400)
    - [Enhancements](#enhancements-3)
    - [Bug Fixes](#bug-fixes-3)
    - [Incompatible Changes](#incompatible-changes)
  - [v3.0.1](#v301)
    - [Bug Fixes](#bug-fixes-4)
  - [v3.0.0](#v300)
    - [Enhancements](#enhancements-4)
    - [Bug Fixes](#bug-fixes-5)
    - [Incompatible Changes](#incompatible-changes-1)
  - [v2.2.0](#v220)
    - [Enhancement](#enhancement)
    - [Bug Fixes](#bug-fixes-6)
  - [v2.1.0](#v210)
    - [Enhancement](#enhancement-1)
    - [Bug Fixes](#bug-fixes-7)
  - [v2.0.0](#v200)
    - [Enhancements](#enhancements-5)
    - [Bug Fixes](#bug-fixes-8)
    - [Incompatible Changes](#incompatible-changes-2)
  - [v1.2.1](#v121)
    - [Enhancements](#enhancements-6)
    - [Bug Fixes](#bug-fixes-9)
  - [v1.2.0](#v120)
    - [Enhancements](#enhancements-7)
    - [Bug Fixes](#bug-fixes-10)
  - [v1.1.0](#v110)
    - [Enhancements](#enhancements-8)
  - [v1.0.0](#v100)
    - [Enhancements](#enhancements-9)
    - [Bug Fixes](#bug-fixes-11)
    - [Incompatible Fixes](#incompatible-fixes)
  - [v0.3.5](#v035)
    - [Enhancements](#enhancements-10)
    - [Bug Fixes](#bug-fixes-12)
  - [v0.3.4](#v034)
    - [Enhancements](#enhancements-11)
  - [v0.3.3](#v033)
    - [Enhancements](#enhancements-12)
  - [v0.3.2](#v032)
    - [Bug Fixes](#bug-fixes-13)
  - [v0.3.1](#v031)
    - [Enhancements](#enhancements-13)
  - [v0.3.0](#v030)
    - [Enhancements](#enhancements-14)
    - [Incompatible Changes](#incompatible-changes-3)
  - [v0.2.1](#v021)
    - [Enhancements](#enhancements-15)
    - [Bug Fixes](#bug-fixes-14)
  - [v0.2.0](#v020)
    - [Enhancements](#enhancements-16)
    - [Incompatible Changes](#incompatible-changes-4)
  - [v0.1.4](#v014)
    - [Enhancements](#enhancements-17)
    - [Bug Fixes](#bug-fixes-15)
  - [v0.1.3](#v013)
    - [Bug Fixes](#bug-fixes-16)
  - [v0.1.2](#v012)
    - [Enhancements](#enhancements-18)
  - [v0.1.1](#v011)
    - [Bug Fixes](#bug-fixes-17)
  - [v0.1.0](#v010)
    - [Enhancements](#enhancements-19)
    - [Bug Fixes](#bug-fixes-18)
  - [v0.0.3](#v003)
    - [Enhancements](#enhancements-20)
  - [v0.0.2](#v002)
    - [Enhancements](#enhancements-21)
    - [Bug Fixes](#bug-fixes-19)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Changelog

## v4.3.0

### Enhancements
* [#387](https://github.com/KronicDeth/intellij-elixir/pull/387) - Resolve aliased modules to their `alias` call, from the alias call, you can Go To Declaration for the module itself. - [@KronicDeth](https://github.com/KronicDeth)
  * Code structure
    * Module resolution uses the OpenAPI convention of `treeWalkUp` now instead of custom code.
    * Resolvable names has been extracted to its own class
  * Resolution use cases
    * `Suffix` resolves to `alias Prefix.Suffix`
    * `Suffix.Nested` resolves to `alias Prefix.Suffix`
    * `As` resolves `to `alias Prefix.Suffix, as: As`
    * `NestedSuffix` resolves to `alias __MODULE__.NestedSuffix`
* [#389](https://github.com/KronicDeth/intellij-elixir/pull/389) - Completion for module aliases - [@KronicDeth](https://github.com/KronicDeth)
    * From in-file aliases
    * `__MODULE__`
    * In project modules (using index already used for Go To Declaration)
* [#393](https://github.com/KronicDeth/intellij-elixir/pull/393) - In addition to `StubBased#canonicalName`, there now also `StubBased#canonicalNames`, for when a call defines multiple canonical names, as is the case for `defimpl <PROTOCOL>, for: [<TYPE>, ...]`. - [@KronicDeth](https://github.com/KronicDeth)
* [#397](https://github.com/KronicDeth/intellij-elixir/pull/397) - When a aliased name is added to the module list for completion, it's unaliased name is searched for in the `AllName` index, if any nested modules are found for the unaliased name, then those nested names are aliased and also shown for completion. - [@KronicDeth](https://github.com/KronicDeth)
* [#399](https://github.com/KronicDeth/intellij-elixir/pull/399) - `resolvableName` allows nested modules under multiple aliases to be completed. - [@KronicDeth](https://github.com/KronicDeth)
* [#403](https://github.com/KronicDeth/intellij-elixir/pull/403) - By user request, the folding will be off-by-default now, but can be re-enabled, like the old behavior by checking the checkbox in Preferences > Editor > General > Code Folding > Elixir Module directive (`alias`, `import`, `require` or `use`) groups. - [@KronicDeth](https://github.com/KronicDeth)
* [#405](https://github.com/KronicDeth/intellij-elixir/pull/405) - [@KronicDeth](https://github.com/KronicDeth)
  * Resolve `as:` aliased name to both `alias` and `defmodule`
  * Complete modules nested under `as:` aliased name.

### Bug Fixes
* [#393](https://github.com/KronicDeth/intellij-elixir/pull/393) - [@KronicDeth](https://github.com/KronicDeth)
  * `defimpl <PROTOCOL>, for: [<TYPE>, ...]` generates multiple canonical names, which are stored in the stub index.
    * When retrieved from the `AllName` index, the `defimpl`'s Implementation will render as if only the `defimpl <PROTOCOL>, for: <TYPE>` was used for the `<TYPE>` matching the lookup name in the Goto Symbol dialog.  For example, if you search for `Tuple`, `JSX.Encoder.Tuple` will match for [`defimpl JSX.Encoder, for: for: [Tuple, PID, Port, Reference, Function, Any]`](https://github.com/talentdeficit/exjsx/blob/master/lib/jsx.ex#L152-L155).
* [#400](https://github.com/KronicDeth/intellij-elixir/pull/400) - [@KronicDeth](https://github.com/KronicDeth)
  * Look outside Enum.map for enclosing macro call because `Ecto` defines the clauses of `__schema__(:type, ...)` using `Enum.map`, but `enclosingMacroCall` only knews to jump over enclosing macros like `for`, so a special case was added for anonymous function given to `Enum.map`.
  * Fix if-else-ordering bug where `Call` appeared before operations (which are usually `Call`s) like `Match`.
* [#401](https://github.com/KronicDeth/intellij-elixir/pull/401) - In `@type unquote({name, nil, []}) :: foo`, `name` will be highlighted as a type parameter even though it is not strictly the name that will appear as a type parameter. - [@KronicDeth](https://github.com/KronicDeth)
* [#405](https://github.com/KronicDeth/intellij-elixir/pull/405) - [@KronicDeth](https://github.com/KronicDeth)
  * Resolve alias nested under aliased modules to both the `alias` and `defmodule`, as resolving to only the `alias` loses the nested name, so it wasn't possible to jump to the nested name's `defmodule`.
  * Resolve aliased name to both the `alias` and the `defmodule`, so you can skip jumping to the `alias` before jumping to the `defmodule`.


## v4.2.0

### Enhancements
* [#371](https://github.com/KronicDeth/intellij-elixir/pull/371) - [@KronicDeth](https://github.com/KronicDeth)
  * `BraceMatcher`
    * Matches the following pairs:
      * `do` to `end`
      * `fn` to `end`
      * `"""` to `"""`
      *  `'''` to `'''`
      * `<<` to `>>`
      * `<` to `>`
      * `[` to `]`
      * `{` to `}`
      * `(` to `)`
    * Completes the following pairs:
      * `[` with `]`
      * `{` with `}`
      * `(` with `)`
  * `QuoteHandler` completes standard quotes (that start with `"` or `'`)
    * `'` with `'`
    * `"` with `"`
    * `'''` with `'''`
    * `"""` with `"""`
  * `TypedHandler` completes the non-standard quotes and braces
    * `do` with ` end`
    * `fn` with ` end`
    * ` `<<` with `>>`
    * `<` with `>`  (for promoters)
    * `/` with `/`  (for promoters)
    * `|` with `|` (for promoters)

### Bug Fixes
* [#372](https://github.com/KronicDeth/intellij-elixir/pull/372) - Check parent for `isVariable(ElixirMapUpdateArguments)` - [@KronicDeth](https://github.com/KronicDeth)
* [#374](https://github.com/KronicDeth/intellij-elixir/pull/374) - [@KronicDeth](https://github.com/KronicDeth)
  * IntelliJ 15.0.4 is no longer available from JetBrains, so if the cache is not available, the builds don't work, so use 15.0.6, which is available in 15.0.4's place as the test version for 15.X.
  * IntelliJ 2016.2 is no longer available from JetBrains, so if the cache is not available, the builds don't work, so use 2016.2, which is available in 2016.1's places at the test version for 2016.X.
* [#378](https://github.com/KronicDeth/intellij-elixir/pull/378) - `enclosingMacroCall` could climb out the stab after a `do`, but not the `else` in an `if`, which is used for defined functions conditionally in [`Phoenix.Endpoint.server/0`](https://github.com/phoenixframework/phoenix/blob/v1.2.0/lib/phoenix/endpoint.ex#L542-L548) - [@KronicDeth](https://github.com/KronicDeth)
* [#380](https://github.com/KronicDeth/intellij-elixir/pull/380) - A lot of ppl use the doc template after already typing `@`, but the doc template starts with `@`, so it ends up inserting `@@doc ...`. The `@doc` template is the same code, but since the name starts with `@`, it doesn't insert a second `@`. Unfortunately, the template search code doesn't prompt when just typing `@`, so you end up having to type `@doc` before only one template is selected. The `@doc` template will show in the lookup as soon as `@d` is typed, but you have to select it from the list then before tabbing to accept. - [@KronicDeth](https://github.com/KronicDeth)
* [#381](https://github.com/KronicDeth/intellij-elixir/pull/381) - Look at end of `do` instead of start for `end` completion to stop ignoring the `:` in `do: `, when `caret - 3` would land on the `o`, now all tests are meant to land on the `o`, so `do: ` won't complete with `end` incorrectly anymore. - [@KronicDeth](https://github.com/KronicDeth)
* [#382](https://github.com/KronicDeth/intellij-elixir/pull/382) - Ignore `ElixirVariable` in `highlightTypesAndTypeParameterUsages` - [@KronicDeth](https://github.com/KronicDeth)

## v4.1.0

### Enhancements
* [#331](https://github.com/KronicDeth/intellij-elixir/pull/331) - [@KronicDeth](https://github.com/KronicDeth)
  * Allow `do end` blocks to fold to `do: ...`
  * Allow `->` operator and the right operand to fold to `-> ...`
  * Allow `@doc`, `@moduledoc` and `@typedoc` value to fold to `"..."`.
  * Fold runs of adjacent `alias`, `import`, `require`, or `use` to be followed to a single `alias`, `import`, `require`, or `use` followed by `...`. 
* [#334](https://github.com/KronicDeth/intellij-elixir/pull/334) - Function separators - [@KronicDeth](https://github.com/KronicDeth)
  * Show a function separator (Preferences > Editor > General > Appearance > Show method separators) above the group of `@doc`, `@spec` and `def`, `defp`, `defmacro`, and `defmacrop` (call definition clauses) of the same name and arity range.  Arity range will be used if one of the call definition clauses uses default arguments.
* [#337](https://github.com/KronicDeth/intellij-elixir/pull/337) - [@KronicDeth](https://github.com/KronicDeth)
  * `@for` folds to the resolved module name in `defimpl`
  * `@protocol` folds to the protocol name in `defimpl`
* [#343](https://github.com/KronicDeth/intellij-elixir/pull/343) - Share code between `mix` and `elixir` version parsing. - [@KronicDeth](https://github.com/KronicDeth)
* [#344](https://github.com/KronicDeth/intellij-elixir/pull/344) - [@KronicDeth](https://github.com/KronicDeth)
  * Allow Unknown modulars in the Structure pane, in addition to Go To Symbol.  Their icon is a big ? to indicate their correct usage is unknown.
* [#348](https://github.com/KronicDeth/intellij-elixir/pull/348) - [@KronicDeth](https://github.com/KronicDeth)
  * Regenerate `gen` folder using Grammar Kit 1.4.1 and fix some bugs (including [JetBrains/Grammar-Kit#126](https://github.com/JetBrains/Grammar-Kit/issues/126)) manually.
* [#349](https://github.com/KronicDeth/intellij-elixir/pull/349) - Have both `QualifiedBracketOperation` and `UnqualifiedBracketOperation` extend `BracketOperation`, so that `BracketOperation` can be used to match both when the qualification does not matter. - [@KronicDeth](https://github.com/KronicDeth)
* [#364](https://github.com/KronicDeth/intellij-elixir/pull/364) - [@KronicDeth](https://github.com/KronicDeth)
  * Regenerate parser with GrammarKit 1.4.2
  * `ElixirSdkRelease` is now `Comparable`, so version checks can be done for tests to restrict them to Elixir 1.2+ for multiple alias support.
  * Resolve Multiple Aliases with unqualified Alias in tuples.
  * `canonicalName` borrows from the idea of `PsiReference#canonicalText`: an element can have both a Name (from `getName`), which is the literal name in the code, which can be renamed, and a Canonical Name, which is the name to refer to the element without need for imports or aliases.  For this change, `defimpl`, `defmodule`, and `defprotocol` will show their full module Alias for their Canonical Name.
    
    This change addresses the use case of Go To Declaration that should resolved to a nested `defmodule`.
  
### Bug Fixes
* [#330](https://github.com/KronicDeth/intellij-elixir/pull/330) - Check if `parameter` is `null` before `Variable#execute` call in `Variable#execute(PsiElement[], ResolveState)`. - [@KronicDeth](https://github.com/KronicDeth)
* [#336](https://github.com/KronicDeth/intellij-elixir/pull/336) - Fix `isVariable` and `variableUseScope` for `var!(name)[...]` - [@KronicDeth](https://github.com/KronicDeth)
* [#337](https://github.com/KronicDeth/intellij-elixir/pull/337) - [@KronicDeth](https://github.com/KronicDeth)
  * `@for` is no longer marked as unresolved in `defimpl` and instead resolve to the either the `<name>` in `for: <name>` or the module name for the enclosing module when `for: ` is not given.
  * `@protocol` is no longer marked as unresolved in `defimpl` and instead resolve to the `<name>` in `defimpl <name>`.
* [#342](https://github.com/KronicDeth/intellij-elixir/pull/342) - [@KronicDeth](https://github.com/KronicDeth)
  * Instead of `assert checkRight || checkLeft` in `Match#processDeclaraions`, do the normal code if `checkRight || checkLeft` and log an error report otherwise, so that the exact code that trigger this error can be reported and the method fixed to handle that form of `Match` later.
* [#343](https://github.com/KronicDeth/intellij-elixir/pull/343) - Be able to parse mix version from 1.3.0+ - [@KronicDeth](https://github.com/KronicDeth)
  * Check all lines of output for mix version as Elixir 1.3.0 changed the format of `mix --version`, so that it includes the Erlang header (`Erlang/OTP ... [erts-...] [source] [64-bit] [smp:..:..] [async-threads:..] [hipe] [kernel-poll:false] [dtrace]`) on the first line and `Mix <version>` on the 3rd line.  Previously the parsing expected `Mix <version>` to be the first line.
* [#344](https://github.com/KronicDeth/intellij-elixir/pull/344) - [@KronicDeth](https://github.com/KronicDeth)
  * If no known modular (Module, Implementation, Protocol, Quote, or Use) matches the call, then use Unknown, which accepts any macro with a `do` block or keyword.  This allows Go To Symbol to no error in projects using Dogma as `defrule` is now treated as Unknown instead of causing an error that the enclosing modular could not be found.
* [#349](https://github.com/KronicDeth/intellij-elixir/pull/349) - `BracketOperations` are neither parameters nor variables. - [@KronicDeth](https://github.com/KronicDeth)
* [#353](https://github.com/KronicDeth/intellij-elixir/pull/353) - Fix stacktrace linking picking wrong file with same basename - [@KronicDeth](https://github.com/KronicDeth)
  * Strip spaces from front of file path in `mix` output, which allows file looks to work correctly.
  * Ensure file reference highlight doesn't include the leading and trailing characters by fix off-by-one errors.
* [#358](https://github.com/KronicDeth/intellij-elixir/pull/358) - Determine whether to check left, right, or both by doing isAncestor checks for all operands, not just the normalized operand.  The normalized operand is still used for `PsiScopeProcessor#execute` since `#execute` is not expected to handle error elements. - [@KronicDeth](https://github.com/KronicDeth)
* [#364](https://github.com/KronicDeth/intellij-elixir/pull/364) - [@KronicDeth](https://github.com/KronicDeth)
  * Add `A.{B, C}` to grammar with quoting to check consistence with Elixir 1.2.  Ports [elixir-lang/elixir#3666](https://github.com/elixir-lang/elixir/pull/3666).
  * Use `fullyQualifiedName` instead of `getName` for `resolvableName` because `fullyQualifiedName` is needed so that qualified aliases inside of the `{ }` of a multiple alias will not have a name as `getName` is `null` for those qualified aliases because the name from `getName` has to be a literal name that can be renamed and qualified names can't be renamed.
* [#365](https://github.com/KronicDeth/intellij-elixir/pull/365) - The `Module` icon got the same icon as `Unknown` when creating `Unknown` somehow, I assume due to find-replace. - [@KronicDeth](https://github.com/KronicDeth)
  
## v4.0.0

### Enhancements
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

### Bug Fixes
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
    
### Incompatible Changes
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

### Bug Fixes
* [#287](https://github.com/KronicDeth/intellij-elixir/pull/287) - Use the error reporter logger instead of plain `assert` in `Prefix#operator`.  **NOTE: This does not address error recovery recovery since I don't have a regression test case.** - [@KronicDeth](https://github.com/KronicDeth)
* [#283](https://github.com/KronicDeth/intellij-elixir/pull/283) - All function name elements act as `PsiNameIdentifier`s now even if they don't resolve, but that means they all need to support `FindUsagesProvider#getType`, which they don't, so use a placeholder of "unknown call type" for any `Call` that can't be matched and "unknown element" for anything else. - [@KronicDeth](https://github.com/KronicDeth)
* [#284](https://github.com/KronicDeth/intellij-elixir/pull/284) - Enumerate all Kernel Functions, Macros, and Special Forms in the Syntax Highlighting section of the README, so that users searching for which category controls highlighting a given call can find it. - [@KronicDeth](https://github.com/KronicDeth)
  
## v3.0.0

### Enhancements
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

### Bug Fixes
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

### Incompatible Changes
* [#276](https://github.com/KronicDeth/intellij-elixir/pull/276) - Drop support for IntelliJ 14.0 because the parser generated by Grammar Kit 1.3.0 is not compatible with the OpenAPI libraries shipped in IntelliJ 14.0.  Still compatible with 14.1 through 2016.1. - [@KronicDeth](https://github.com/KronicDeth)

## v2.2.0

### Enhancement
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

### Bug Fixes
* [#244](https://github.com/KronicDeth/intellij-elixir/pull/244) - Elixir version parsing handles both pre and build numbers if present by using the same regular expression as Elixir itself uses for the `Version` module - [KronicDeth](https://github.com/KronicDeth)
* [#245](https://github.com/KronicDeth/intellij-elixir/pull/245) - Better error handling in Structure View - [KronicDeth](https://github.com/KronicDeth)

## v2.1.0

### Enhancement
* [#236](https://github.com/KronicDeth/intellij-elixir/pull/236) - `\u` in strings and char lists for unicode mapping - [KronicDeth](https://github.com/KronicDeth)
* [#237](https://github.com/KronicDeth/intellij-elixir/pull/237) - Test against Elixir 1.1.1 and 1.2.0 - [KronicDeth](https://github.com/KronicDeth)
* [#233](https://github.com/KronicDeth/intellij-elixir/pull/233) - More flexible `elixir --version` parsing: works with `elixir` 1.2.0 and earlier - [bitgamma](https://github.com/bitgamma)

### Bug Fixes
* [#231](https://github.com/KronicDeth/intellij-elixir/pull/231) - Update IntelliJ to 14.1.6 to fix 403 errors in Travis-CI build - [sholden](https://github.com/sholden)

## v2.0.0

### Enhancements
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

### Bug Fixes 
* [#206](https://github.com/KronicDeth/intellij-elixir/pull/206) - Change "edition" to "addition" in README. - [folz](https://github.com/folz)
* [#225](https://github.com/KronicDeth/intellij-elixir/pull/225) - Sped up reparsing when [ENTER] is hit in the middle of comment by removing the custom error handling element, adjacentExpression, and going with the default error handling provided by JetBrains' OpenAPI. - [KronicDeth](https://github.com/KronicDeth)
* [#226](https://github.com/KronicDeth/intellij-elixir/pull/226) - Fix `mix` version detection on Windows - [KronicDeth](https://github.com/KronicDeth)

### Incompatible Changes
* [#225](https://github.com/KronicDeth/intellij-elixir/pull/225) - [KronicDeth](https://github.com/KronicDeth)
  * Removed "Add Newline" Quick Fix as it depended on `adjacentExpression` elements, which have now been removed to speed up error handling when comments become code.
  * Removed "Add Semicolon" Quick Fix as it depended on `adjacentExpression` elements, which have now been removed to speed up error handling when comments become code.

## v1.2.1

### Enhancements
* [#204](https://github.com/KronicDeth/intellij-elixir/pull/204) - Keywords not at the end of no parentheses calls will be properly marked as errors. - [KronicDeth](https://github.com/KronicDeth)

### Bug Fixes
* [#200](https://github.com/KronicDeth/intellij-elixir/pull/200) - Fix `IllegalStateException` for file delete and rename by giving `ElixirFile`s descriptive names for safe-refactoring displaying file usage. - [KronicDeth](https://github.com/KronicDeth)
* [#201](https://github.com/KronicDeth/intellij-elixir/pull/201) - [KronicDeth](https://github.com/KronicDeth)
  * README states explicitly that the plugin works with *both* IntelliJ Community and Ultimate.
  * README states that the plugin is free.
* [#202](https://github.com/KronicDeth/intellij-elixir/pull/202) - Prevent match error when typing `~` to start a sigil that is followed later by a `\n` by matching `EOL` in the `NAMED_SIGIL` state as a `BAD_CHARACTER` - [KronicDeth](https://github.com/KronicDeth)
* [#204](https://github.com/KronicDeth/intellij-elixir/pull/204) - Keywords at the end of a no parentheses call that is surrounded by parentheses will not be marked as an error when that parenthetical group appears in the middle of an outer call. - [KronicDeth](https://github.com/KronicDeth)

## v1.2.0

### Enhancements
* [#184](https://github.com/KronicDeth/intellij-elixir/pull/184) - If (1) you have intellij-erlang installed and (2) you have an atom in Erlang that starts with `Elixir.`, such as `'Elixir.Test'`, then intellij-elixir will annotate whether it can resolve the name to a `defmodule` call in Elixir files. - [KronicDeth](https://github.com/KronicDeth)
* [#188](https://github.com/KronicDeth/intellij-elixir/pull/188) - Default SDK path for Linux and Windows - [zyuyou](https://github.com/zyuyou)
* [#198](https://github.com/KronicDeth/intellij-elixir/pull/198) - [KronicDeth](https://github.com/KronicDeth)
  * Go To Declaration (`Cmd+Click`, `Cmd+B`, `Navigate > Declaration`) from Alias to `defmodule` where Alias is declared.
  * Index `defmodule`s for fast Go To Declaration in [elixir-lang/elixir](https://github.com/elixir-lang/elixir) and other large projects.
  * Find Usage for Alias in `defmodule`
### Bug Fixes
* [#187](https://github.com/KronicDeth/intellij-elixir/pull/187) - Fix links to screenshots in README - [zhyu](https://github.com/zhyu)

## v1.1.0

### Enhancements
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

### Enhancements
* [#168](https://github.com/KronicDeth/intellij-elixir/pull/168) - Update ant build on travis-ci.org to use IDEA 14.1.4 (from 14.0.2) - [KronicDeth](https://github.com/KronicDeth)
* [#174](https://github.com/KronicDeth/intellij-elixir/pull/174) - Parser is verified to quote the same as native Elixir - [KronicDeth](https://github.com/KronicDeth)

### Bug Fixes
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

### Incompatible Fixes
* [#162](https://github.com/KronicDeth/intellij-elixir/pull/162) - New Elixir File has moved to the last item in the New File menu to preserve `CTRL+N ENTER` keyboard shortcut for `New > File` - [jaketrent](https://github.com/jaketrent)

## v0.3.5

### Enhancements
* [#135](https://github.com/KronicDeth/intellij-elixir/pull/135) - `do` blocks (`do end) - [@KronicDeth](https://github.com/KronicDeth)
* [#152](https://github.com/KronicDeth/intellij-elixir/pull/152) - Unmatched expressions (operations involving `do` block calls and normal matched expressions) - [Kronicdeth](https://github.com/KronicDeth)

### Bug Fixes
* [#137](https://github.com/KronicDeth/intellij-elixir/pull/137) - Lex full atom instead of just identifier-like operator prefix (`:in<nospace>dex` before vs `:index` after) - [@KronicDeth](https://github.com/KronicDeth)
* [#138](https://github.com/KronicDeth/intellij-elixir/pull/138) - `!` and `not` are properly wrapped in `__block__`s when in stab bodies - [@KronicDeth](https://github.com/KronicDeth)

## v0.3.4

### Enhancements
* [#126](https://github.com/KronicDeth/intellij-elixir/pull/126) - Bracket at expression (`@foo[key]`) - [@KronicDeth](https://github.com/KronicDeth)
* [#127](https://github.com/KronicDeth/intellij-elixir/pull/127) - Anonymous functions (`fn end`), stab clauses (`->`), and parentheticals (`(1 + 2)`) - [@KronicDeth](https://github.com/KronicDeth)
* [#128](https://github.com/KronicDeth/intellij-elixir/pull/128) - Maps (`%{}`) and structs (`%User{}`) - [@KronicDeth](https://github.com/KronicDeth)
* [#129](https://github.com/KronicDeth/intellij-elixir/pull/129) - Tuples (`{}`) - [@KronicDeth](https://github.com/KronicDeth)
* [#130](https://github.com/KronicDeth/intellij-elixir/pull/130) - Bit strings (`<<>>`) - [@KronicDeth](https://github.com/KronicDeth)

## v0.3.3

### Enhancements
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

### Bug Fixes
* [#121](https://github.com/KronicDeth/intellij-elixir/pull/121) - Fix `NoSuchElementException` when no suggested SDK home paths are available.  Thanks to [@zyuyou](https://github.com/zyuyou) for [reporting](https://github.com/KronicDeth/intellij-elixir/issues/120) - [@KronicDeth](https://github.com/KronicDeth)

## v0.3.1

### Enhancements
* [#112](https://github.com/KronicDeth/intellij-elixir/pull/112) - File > New > Project From Existing Sources can be used in IntelliJ to setup the excludes, sources, tests, SDK and libraries for an Elixir project that has already been created with `mix new`. - [@KronicDeth](https://github.com/KronicDeth)
* [#114](https://github.com/KronicDeth/intellij-elixir/pull/114) - Operators can be qualified function names - [@KronicDeth](https://github.com/KronicDeth)
* [#118](https://github.com/KronicDeth/intellij-elixir/pull/114) - [@KronicDeth](https://github.com/KronicDeth)
  * Anonymous function calls (`.(...)`)
  * Inspection that marks errors when keywords aren't at end of list.

## v0.3.0

### Enhancements
* [#108](https://github.com/KronicDeth/intellij-elixir/pull/108) - `\x` is marked as an error in CharLists, CharList Heredocs, Strings, and String Heredocs, but not in any sigils. - [@KronicDeth](https://github.com/KronicDeth)
* [#111](https://github.com/KronicDeth/intellij-elixir/pull/111) - New Elixir File will automatically underscore the camel case module name when creating the file name and will convert qualifying aliases before the last `.` to directories - [@KronicDeth](https://github.com/KronicDeth)

### Incompatible Changes
* [#111](https://github.com/KronicDeth/intellij-elixir/pull/111) - New Elixir File validates that the name is a valid Alias, so each `.` separated part must start with a capital letter.  Previous New Elixir File validated that the name was a valid path, and so forced the name to be lowercase. - [@KronicDeth](https://github.com/KronicDeth)

## v0.2.1

### Enhancements
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

### Bug Fixes
* [#74](https://github.com/KronicDeth/intellij-elixir/pull/74) - Right hand-side of `dot_alias` and `dot_identifier`
  was translated incorrectly. Only Aliases and Identifiers are allowed now. [@KronicDeth](https://github.com/KronicDeth)

## v0.2.0

### Enhancements
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

### Incompatible Changes
 * [#73](https://github.com/KronicDeth/intellij-elixir/pull/73): Number attribute has been removed from Color Settings page - [@KronicDeth](https://github.com/KronicDeth)

## v0.1.4


### Enhancements
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

### Bug Fixes
 * [#17](https://github.com/KronicDeth/intellij-elixir/pull/17): Sigil terminator escapes are recognized, so that sigils are no longer prematurely terminated. - [@KronicDeth](https://github.com/KronicDeth)
 * [#24](https://github.com/KronicDeth/intellij-elixir/pull/24): Comments do not consume EOL, so trailing comments don't cause error parsing expression on following line. - [@KronicDeth](https://github.com/KronicDeth)
 * [#36](https://github.com/KronicDeth/intellij-elixir/pull/36): Sigil modifiers now work on groups in addition to heredocs. - [@KronicDeth](https://github.com/KronicDeth)
 * [#47](https://github.com/KronicDeth/intellij-elixir/pull/47): `;` is separate from `EOL` and either or both can separate expressions, but only `EOL` can separate operators and operands for operations - [@KronicDeth](https://github.com/KronicDeth) 

## v0.1.3

### Bug Fixes
 * [#10](https://github.com/KronicDeth/intellij-elixir/pull/10): Blank lines are properly parsed as whitespace instead of bad characters. - [@KronicDeth](https://github.com/KronicDeth)
 * [#13](https://github.com/KronicDeth/intellij-elixir/pull/13): EOL is parsed as bad character in sigil name (after `~`) instead of causing the lexer to fail to match, which raised exceptions in Event Log. - [@KronicDeth](https://github.com/KronicDeth)

## v0.1.2

### Enhancements
 * Atoms with highlighting
  * Atom with double or single quotes to allow interpolation.  Double quotes are highlighted as 'String' while single
    quotes are highlighted as 'Char List'.  This may be changed in the future.
  * Literal atoms highlighted as 'Atom'.
  * Operator atoms highlighted as 'Atom'.

## v0.1.1

### Bug Fixes
* Build using JDK 6 instead of 7 so that plugin will work by default on OSX Mavericks.

## v0.1.0

### Enhancements
* [#3](https://github.com/KronicDeth/intellij-elixir/pull/3): Literal and interpolated sigils with highlighting - [@KronicDeth](https://github.com/KronicDeth)
  * CharList Sigils (`~c` and `~C`) highlighted as 'Char List' in Settings.
  * Regex Sigils (`~r` and `~R`) highlighted as 'Sigil' in Settings. **NOTE: Regex syntax is not internally highlighted yet**
  * String Sigils (`~s` and `~S`) highlighted as 'String' in Settings.
  * Word Sigils (`~w` and `~W`) highlighted as 'Sigil' in Settings.
  * Custom Sigils highlighted as 'Sigil' in Settings.
  * Modifiers are highlighted on Regex, Word, and Custom while modifiers aren't allowed on CharList and String Sigils.

### Bug Fixes
* Single-quoted strings are correctly referred to as 'Character List' now instead of 'String' in Settings.
* Double-quoted strings are correctly referred to as 'String' now instead of 'Interpolated String' in Settings.
* Non-Heredoc CharLists and Strings can be multiline.
* CharLists and Strings support interpolation and escape sequences.

## v0.0.3

### Enhancements
* Single quoted strings with highlighting. ('String' in Color Settings.)
* Double quoted strings with highlighting. ('Interpolated String' in Color Settings.)
   * Interpolation (`#{` and `}`) with highlighting. ('Expression Substitution Mark' in Color Settings.)
   * Escape sequences for `"` and `#` with highlighting. ('Escape Sequence' in Color Settings.)   

## v0.0.2

### Enhancements
* Binary, Hexadecimal, and Octal numbers (including deprecated syntax) are recognized as numbers.
* Syntax Highlighting for numbers.
* Color Settings page for changing the color of comments and numbers for Elixir (Preferences > Editor > Colors & Fonts > Elixir).

### Bug Fixes
* Parser no longer freezes IDE on tokens it doesn't understand.
* White space at beginning of lines no longer leads to annotation errors.
* White space and EOLs at beginning of file no longer lead to annotation errors.
