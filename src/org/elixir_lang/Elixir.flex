package org.elixir_lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import com.intellij.psi.TokenType;

%%

%class ElixirFlexLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private java.util.Stack<Integer> lexicalStateStack = new java.util.Stack<Integer>();

  private void callState(int nextLexicalState) {
    lexicalStateStack.push(yystate());
    yybegin(nextLexicalState);
  }

  private void returnFromState() {
    int previousLexicalState = lexicalStateStack.pop();
    yybegin(previousLexicalState);
  }

  private void handleInState(int nextLexicalState) {
    yypushback(yylength());
    yybegin(nextLexicalState);
  }
%}

EOL = \n|\r|\r\n
WHITE_SPACE=[\ \t\f]

COMMENT = "#" [^\r\n]* {EOL}?

/*
 *   Integers
 */

// Include deprecated '0B' so it can be corrected
BINARY_INTEGER = "0" [Bb][01]+
// Include deprecated '0X' so it can be corrected
HEXADECIMAL_INTEGER = "0" [Xx][A-Fa-f0-9]+
// Include deprecated '0' so it can be corrected
OCTAL_INTEGER = "0" o?[0-7]+
INTEGER = {BINARY_INTEGER} | {HEXADECIMAL_INTEGER} | {OCTAL_INTEGER}

ESCAPE = "\\"

SINGLE_QUOTE = "'"
ESCAPED_SINGLE_QUOTE = {ESCAPE} {SINGLE_QUOTE}

DOUBLE_QUOTES = "\""
ESCAPED_DOUBLE_QUOTES = {ESCAPE} {DOUBLE_QUOTES}

INTERPOLATION_START = "#{"
ESCAPED_INTERPOLATION_START = {ESCAPE} {INTERPOLATION_START}
INTERPOLATION_END = "}"

TRIPLE_SINGLE_QUOTE = {SINGLE_QUOTE}{3}
TRIPLE_DOUBLE_QUOTES = {DOUBLE_QUOTES}{3}

VALID_ESCAPE_SEQUENCE = {ESCAPED_DOUBLE_QUOTES} |
                        {ESCAPED_SINGLE_QUOTE} |
                        {ESCAPED_INTERPOLATION_START}

NON_WHITE_SPACE = {COMMENT} |
                  {INTEGER} |
                  // MUST be before {SINGLE_QUOTE} so that 3 {SINGLE_QUOTE} are NOT matched as (1,1,1)
                  {TRIPLE_SINGLE_QUOTE} |
                  {SINGLE_QUOTE} |
                  // MUST be before {DOUBLE_QUOTES} so that 3 {DOUBLE_QUOTES} are NOT matched as (1,1,1)
                  {TRIPLE_DOUBLE_QUOTES} |
                  {DOUBLE_QUOTES} |
                  .

// state after YYINITIAL has taken care of any white space prefix
%state BODY
%state CHAR_LIST_HEREDOC_END
%state CHAR_LIST_HEREDOC_LINE_BODY
%state CHAR_LIST_HEREDOC_LINE_START
%state CHAR_LIST_HEREDOC_START
%state STRING
%state INTERPOLATED_HEREDOC_END
%state INTERPOLATED_HEREDOC_LINE_BODY
%state INTERPOLATED_HEREDOC_LINE_START
%state INTERPOLATED_HEREDOC_START
%state INTERPOLATION
%state CHAR_LIST

%%

<YYINITIAL> {
  /* Turn EOL and whitespace at beginning of file into a single {@link org.elixir_lang.psi.ElixirTypes.WHITE_SPACE} so
   * it is filtered out.
   */
  ({EOL}|{WHITE_SPACE})+ { yybegin(BODY);
                           return TokenType.WHITE_SPACE; }

  // Push back and left BODY handle normal actions so they don't need to be duplicated in YYINITIAL and BODY.
  {NON_WHITE_SPACE}      { handleInState(BODY); }
}

// Rules common to interpolated CharLists and Strings
<CHAR_LIST,
 CHAR_LIST_HEREDOC_LINE_BODY,
 STRING,
 INTERPOLATED_HEREDOC_LINE_BODY> {
  {INTERPOLATION_START}   { callState(INTERPOLATION);
                            return ElixirTypes.INTERPOLATION_START; }
  {VALID_ESCAPE_SEQUENCE} { return ElixirTypes.VALID_ESCAPE_SEQUENCE; }
}

// Rules that aren't common to STRING and INTERPOLATED_HEREDOC_BODY
<STRING> {
  {DOUBLE_QUOTES} { returnFromState();
                    return ElixirTypes.DOUBLE_QUOTES; }
  {EOL}|.         { return ElixirTypes.STRING_FRAGMENT; }
}

// Rules that aren't dependent on detecting the end of INTERPOLATION can be shared between <BODY> and <INTERPOLATION>
<BODY, INTERPOLATION> {
  /* Compress {EOL} followed by more {EOL} or whitespace into {EOL} as only {EOL} is significant for Elixir's grammar.
   *
   * @see https://github.com/idavis/Innovatian.Idea.PowerShell/blob/80bbe5bbcb15f95d8b33f4a34b86acb6b65ac67e/src/lang/lexer/PowerShell.flex#L78
   * @see https://github.com/idavis/Innovatian.Idea.PowerShell/blob/80bbe5bbcb15f95d8b33f4a34b86acb6b65ac67e/src/lang/lexer/PowerShell.flex#L227
   */
  {EOL}({EOL}|{WHITE_SPACE})* { return ElixirTypes.EOL; }
  // This rule is only meant to match whitespace surrounded by other tokens as the above rule will handle blank lines.
  {WHITE_SPACE}+              { return TokenType.WHITE_SPACE; }

  {COMMENT}                   { return ElixirTypes.COMMENT; }

  {INTEGER}                   { return ElixirTypes.NUMBER; }

  {TRIPLE_SINGLE_QUOTE}       { callState(CHAR_LIST_HEREDOC_START);
                                return ElixirTypes.TRIPLE_SINGLE_QUOTE; }
  {SINGLE_QUOTE}              { callState(CHAR_LIST);
                                return ElixirTypes.SINGLE_QUOTE; }
  {TRIPLE_DOUBLE_QUOTES}      { callState(INTERPOLATED_HEREDOC_START);
                                return ElixirTypes.TRIPLE_DOUBLE_QUOTES; }
  {DOUBLE_QUOTES}             { callState(STRING);
                                return ElixirTypes.DOUBLE_QUOTES; }
}

// Only rules for <BODY>, but not <INTERPOLATION> go here.
// @note MUST be after <BODY, INTERPOLATION> so that BAD_CHARACTER doesn't match a single ' ' instead of {WHITE_SPACE}+.
<BODY> {
  .                           { return TokenType.BAD_CHARACTER; }
}

// Only rules for <INTERPOLATON>, but not <BODY> go here.
// @note must be after <BODY, INTERPOLATION> so that BAD_CHARACTER doesn't match a single ' ' instead of {WHITE_SPACE}+.
<INTERPOLATION> {
  {INTERPOLATION_END}         { returnFromState();
                                return ElixirTypes.INTERPOLATION_END; }

  .                           { return TokenType.BAD_CHARACTER; }
}

<CHAR_LIST_HEREDOC_START> {
  {EOL} { yybegin(CHAR_LIST_HEREDOC_LINE_START); return ElixirTypes.EOL; }
  .     { return TokenType.BAD_CHARACTER; }
}

<CHAR_LIST_HEREDOC_LINE_START> {
  {WHITE_SPACE}+ / {TRIPLE_SINGLE_QUOTE} { yybegin(CHAR_LIST_HEREDOC_END); return TokenType.WHITE_SPACE; }
  {TRIPLE_DOUBLE_QUOTES}                 { handleInState(CHAR_LIST_HEREDOC_END); }
  .                                      { handleInState(CHAR_LIST_HEREDOC_LINE_BODY); }
}

<CHAR_LIST_HEREDOC_LINE_BODY> {
  {EOL} { yybegin(CHAR_LIST_HEREDOC_LINE_START); return ElixirTypes.CHAR_LIST_FRAGMENT; }
  .     { return ElixirTypes.CHAR_LIST_FRAGMENT; }
}

<CHAR_LIST_HEREDOC_END> {
  {TRIPLE_SINGLE_QUOTE} { returnFromState();
                          return ElixirTypes.TRIPLE_SINGLE_QUOTE; }
}

/* The start of a heredoc is unique as we want to handle the error condition of having characters other than a newline
   after the {TRIPLE_DOUBLE_QUOTES}:

       iex> """a
            ** (SyntaxError) iex:6: heredoc start must be followed by a new line after """ */
<INTERPOLATED_HEREDOC_START> {
  {EOL} { yybegin(INTERPOLATED_HEREDOC_LINE_START); return ElixirTypes.EOL; }
  .     { return TokenType.BAD_CHARACTER; }
}

<INTERPOLATED_HEREDOC_LINE_START> {
  /* TRIPLE_DOUBLE_QUOTES only end the heredoc when preceeded ONLY by whitespace on the line
     iex(7)>     """
     ...(7)>  hi
     ...(7)>   there"""
     ...(7)>
     ...(7)> """
     " hi\n  there\"\"\"\n\n" */
  {WHITE_SPACE}+ / {TRIPLE_DOUBLE_QUOTES} { yybegin(INTERPOLATED_HEREDOC_END); return TokenType.WHITE_SPACE; }
  {TRIPLE_DOUBLE_QUOTES} { handleInState(INTERPOLATED_HEREDOC_END); }
  .                      { handleInState(INTERPOLATED_HEREDOC_LINE_BODY); }
}

<INTERPOLATED_HEREDOC_LINE_BODY> {
  {EOL} { yybegin(INTERPOLATED_HEREDOC_LINE_START); return ElixirTypes.STRING_FRAGMENT; }
  .     { return ElixirTypes.STRING_FRAGMENT; }
}

<INTERPOLATED_HEREDOC_END> {
  {TRIPLE_DOUBLE_QUOTES} { returnFromState();
                           return ElixirTypes.TRIPLE_DOUBLE_QUOTES; }
}

<CHAR_LIST> {
  {SINGLE_QUOTE}         { returnFromState();
                           return ElixirTypes.SINGLE_QUOTE; }
  {EOL}|.                { return ElixirTypes.CHAR_LIST_FRAGMENT; }
}