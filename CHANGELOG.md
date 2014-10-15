# Changelog

## Next Release

* Enhancements
 * All valid escape sequences (`\<character>`, `\x<hexadecimal>`, `\x{<hexadecimal>}` are recognized.
* Bug Fixes
 * Sigil terminator escapes are recognized, so that sigils are no longer prematurely terminated.

## v0.1.3

* Bug Fixes
 * Blank lines are properly parsed as whitespace instead of bad characters.
 * EOL is parsed as bad character in sigil name (after `~`) instead of causing the lexer to fail to match, which raised exceptions in Event Log.

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
  * Literal and interpolated sigils with highlighting
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