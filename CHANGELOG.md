# Changelog

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