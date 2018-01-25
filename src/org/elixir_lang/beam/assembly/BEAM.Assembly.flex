package org.elixir_lang.beam.assembly;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.beam.assembly.psi.Types;
import com.intellij.psi.TokenType;

%%

%class FlexLexer
%implements com.intellij.lexer.FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE=([\ ]|\R)+

CLOSING_PARENTHESIS=")"
COLON=":"
COMMA=","
INTEGER=0|[1-9][0-9]*
NAME=[a-z][a-z0-9_]*
OPENING_PARENTHESIS="("

%%

<YYINITIAL> {
  {CLOSING_PARENTHESIS} { return Types.CLOSING_PARENTHESIS; }
  {COLON}               { return Types.COLON; }
  {COMMA}               { return Types.COMMA; }
  {INTEGER}             { return Types.INTEGER; }
  {NAME}                { return Types.NAME; }
  {OPENING_PARENTHESIS} { return Types.OPENING_PARENTHESIS; }
  {WHITE_SPACE}         { return TokenType.WHITE_SPACE; }
}

. { return TokenType.BAD_CHARACTER; }
