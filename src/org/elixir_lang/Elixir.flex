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

SINGLE_QUOTE = "'"
SINGLE_QUOTED_STRING = {SINGLE_QUOTE} ("\\'" | [^'])* {SINGLE_QUOTE}

DOUBLE_QUOTES = "\""
ESCAPED_DOUBLE_QUOTES = "\\" {DOUBLE_QUOTES}
ESCAPED_INTERPOLATION_START = "\\#"
// ESCAPED_DOUBLE_QUOTES and INTERPOLATION_START are longer, so they win over this single character
DOUBLE_QUOTED_STRING_CHARACTER = [^\"]

INTERPOLATION_START = "#{"
INTERPOLATION_END = "}"

// state after YYINITIAL has taken care of any white space prefix
%state BODY
%state DOUBLE_QUOTED_STRING
%state INTERPOLATION

%%

<YYINITIAL> {
  /* Turn EOL and whitespace at beginning of file into a single {@link org.elixir_lang.psi.ElixirTypes.WHITE_SPACE} so
   * it is filtered out.
   */
  ({EOL}|{WHITE_SPACE})+      { yybegin(BODY); return TokenType.WHITE_SPACE; }

  {COMMENT}                   { yybegin(BODY); return ElixirTypes.COMMENT; }

  {INTEGER}                   { yybegin(BODY); return ElixirTypes.NUMBER; }

  {SINGLE_QUOTED_STRING}      { yybegin(BODY); return ElixirTypes.SINGLE_QUOTED_STRING; }
  {DOUBLE_QUOTES}             { // return to BODY instead of YYINITIAL since beginning of file error handling has
                                // finished.
                                lexicalStateStack.push(BODY);
                                yybegin(DOUBLE_QUOTED_STRING);
                                return ElixirTypes.DOUBLE_QUOTES; }

  .                           { yybegin(BODY); return TokenType.BAD_CHARACTER; }
}


<DOUBLE_QUOTED_STRING> {
  {INTERPOLATION_START}            { lexicalStateStack.push(yystate());
                                     yybegin(INTERPOLATION);
                                     return ElixirTypes.INTERPOLATION_START; }
  {ESCAPED_DOUBLE_QUOTES}          { return ElixirTypes.STRING_FRAGMENT; }
  {ESCAPED_INTERPOLATION_START}    { return ElixirTypes.STRING_FRAGMENT; }
  {DOUBLE_QUOTED_STRING_CHARACTER} { return ElixirTypes.STRING_FRAGMENT; }
  {DOUBLE_QUOTES}                  { int previousLexicalState = lexicalStateStack.pop();
                                     yybegin(previousLexicalState);
                                     return ElixirTypes.DOUBLE_QUOTES; }
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

  {SINGLE_QUOTED_STRING}      { return ElixirTypes.SINGLE_QUOTED_STRING; }
  {DOUBLE_QUOTES}             { lexicalStateStack.push(yystate());
                                yybegin(DOUBLE_QUOTED_STRING);
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
  {INTERPOLATION_END}         { int previousLexicalState = lexicalStateStack.pop();
                                yybegin(previousLexicalState);
                                return ElixirTypes.INTERPOLATION_END;
                              }

  .                           { return TokenType.BAD_CHARACTER; }
}
