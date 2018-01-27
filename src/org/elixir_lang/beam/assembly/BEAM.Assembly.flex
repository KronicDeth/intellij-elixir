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

IDENTIFIER_TOKEN_END = [?!]
IDENTIFIER_TOKEN_MIDDLE = [0-9a-zA-Z_]
IDENTIFIER_TOKEN_START = [a-z_]
IDENTIFIER_TOKEN_HEAD = {IDENTIFIER_TOKEN_START}
IDENTIFIER_TOKEN_TAIL = {IDENTIFIER_TOKEN_MIDDLE}* {IDENTIFIER_TOKEN_END}?
IDENTIFIER_TOKEN = ({IDENTIFIER_TOKEN_HEAD} {IDENTIFIER_TOKEN_TAIL}  | "...")

ALIAS_HEAD=[A-Z]
ALIAS= {ALIAS_HEAD} {IDENTIFIER_TOKEN_MIDDLE}*

CLOSING_PARENTHESIS=")"
COLON=":"
COMMA=","
INTEGER=0|[1-9][0-9]*
OPENING_PARENTHESIS="("
QUALIFIED_ALIAS={ALIAS}(\.{ALIAS})*

%%

<YYINITIAL> {
  {QUALIFIED_ALIAS}     { return Types.QUALIFIED_ALIAS; }
  {CLOSING_PARENTHESIS} { return Types.CLOSING_PARENTHESIS; }
  {COLON}               { return Types.COLON; }
  {COMMA}               { return Types.COMMA; }
  {INTEGER}             { return Types.INTEGER; }
  {IDENTIFIER_TOKEN}    { return Types.NAME; }
  {OPENING_PARENTHESIS} { return Types.OPENING_PARENTHESIS; }
  {WHITE_SPACE}         { return TokenType.WHITE_SPACE; }
}

. { return TokenType.BAD_CHARACTER; }
