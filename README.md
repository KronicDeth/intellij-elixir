# Elixir plugin

This is a plugin that adds support for [Elixir](http://http://elixir-lang.org/) to JetBrains IntelliJ IDEA platform IDEs
([0xDBE](http://www.jetbrains.com/dbe/), [AppCode](http://www.jetbrains.com/objc/),
[IntelliJ IDEA](http://www.jetbrains.com/idea/), [PHPStorm](http://www.jetbrains.com/phpstorm/),
[PyCharm](http://www.jetbrains.com/pycharm/), [Rubymine](http://www.jetbrains.com/ruby/),
[WebStorm](http://www.jetbrains.com/webstorm/))

## Features

### Syntax Highlighting

Syntax highlighting for the following tokens:

* [Aliases](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L454), in other words, module names.
* [Atoms](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L238-L303) (`:`, `:''`, or `:""`)
* [Character Tokens](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L166-L221) (`?<character>` or `?<escape_sequence>`)
* [Comments](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L139-L143) (`#`)
* [End of Lines](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L305-L332) (`;`, `\n`, `\r\n`)
* [Escape Sequences](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_interpolation.erl#L71-L116) (`\\<character>`, `\\x<hexadecimal>`, or `\\x{<hexadecimal>}`)
* [Heredocs](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L223-L229) (`"""` or `'''`)
* [Identifiers](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L460-L470), in other words, variable, function and macro names.
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
* [Regular Keywords](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L963-L968) (`end`, `false`, `fn`, `nil`, and `true`)
* [Sigils](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L139-L143) (`~`)
  * CharList Sigils (`~c` and `~C`)
  * Regex Sigils (`~r` and `~R`)
  * String Sigils (`~s` and `~S`)
  * Word Sigils (`~w` and `~W`)
  * Custom Sigils (`~<lower_case_character>` and `~<UPPER_CASE_CHARACTER>`)
* [Strings and Char List](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L231-L236) (`"` or `'`)
  
The syntax highlighting colors can be customized in the Color Settings page for Elixir (Preferences > Editor > Color & Fonts > Elixir).

### Grammar parsing

Built on top of highlighted tokens above, the parser understands the following parts of Elixir grammar as valid or
allows the grammar because they contain correctable errors:
 
* [Empty Parentheses](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L299) (`()`)
* [Keyword Lists](http://elixir-lang.org/getting_started/7.html#7.1-keyword-lists)
  * Keyword Keys - Aliases, identifiers, quotes, or operators when followed immediately by a colon and horizontal or vertical space.
  * Keyword Values *PARTIAL SUPPORT* - Only empty parentheses (`()`).
* [Matched Expressions](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L113-L122),
  in other words, unary and binary operations on variable, function, and macro names and values (numbers, strings,
  char lists, sigils, heredocs, `true`, `false`, and `nil`).
* [No Parentheses expressions](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L124-L125), which
  are function calls with neither parentheses nor `do` blocks that have either (1) a positional argument and keyword
  arguments OR (2) two or more positional arguments with optional keyword arguments.
 
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
