# Changelog

## v0.0.2

* Enhancements
  * Binary, Hexadecimal, and Octal numbers (including deprecated syntax) are recognized as numbers.
  * Syntax Highlighting for numbers.
  * Color Settings page for changing the color of comments and numbers for Elixir (Preferences > Editor > Colors & Fonts > Elixir).

* Bug Fixes
  * Parser no longer freezes IDE on tokens it doesn't understand.
  * White space at beginning of lines no longer leads to annotation errors.
  * White space and EOLs at beginning of file no longer lead to annotation errors.