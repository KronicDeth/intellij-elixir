# Changelog

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
