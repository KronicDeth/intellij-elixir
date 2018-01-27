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
ALIAS={ALIAS_HEAD} {IDENTIFIER_TOKEN_MIDDLE}*

DOUBLE_QUOTE="\""
ESCAPED_DOUBLE_QUOTE="\\\""
NOT_DOUBLE_QUOTE=[^\"]
QUOTED_ATOM_NAME={DOUBLE_QUOTE}({ESCAPED_DOUBLE_QUOTE}|{NOT_DOUBLE_QUOTE})+{DOUBLE_QUOTE}
ATOM={COLON} ({IDENTIFIER_TOKEN} | {QUOTED_ATOM_NAME})

CLOSING_PARENTHESIS=")"
COLON=":"
COMMA=","
DOT_OPERATOR="."
INTEGER=0|[1-9][0-9]*
NAME_ARITY_SEPARATOR="/"
OPENING_PARENTHESIS="("

THREE_SYMBOL_OPERATOR = "!==" |
                        "&&&" |
                        "<<<" |
                        "<<~" |
                        "<|>" |
                        "<~>" |
                        "===" |
                        ">>>" |
                        "^^^" |
                        "|||" |
                        "~>>" |
                        "~~~"

TWO_SYMBOL_OPERATOR = "!=" |
                      "&&" |
                      "++" |
                      "--" |
                      ".." |
                      "::" |
                      "<-" |
                      "<=" |
                      "<=" |
                      "<>" |
                      "<~" |
                      "==" |
                      "=~" |
                      ">=" |
                      "|>" |
                      "||" |
                      "~>"

ONE_SYMBOL_OPERATOR = "!" |
                      "*" |
                      "+" |
                      "-" |
                      "-" |
                      "<" |
                      "=" |
                      ">" |
                      "@" |
                      "^" |
                      "|"

SYMBOLIC_OPERATOR = {THREE_SYMBOL_OPERATOR} | {TWO_SYMBOL_OPERATOR} | {ONE_SYMBOL_OPERATOR}


QUALIFIED_ALIAS={ALIAS}(\.{ALIAS})*
REFERENCE_OPERATOR="&"

%%

<YYINITIAL> {
  {ATOM}                 { return Types.ATOM; }
  {DOT_OPERATOR}         { return Types.DOT_OPERATOR; }
  {QUALIFIED_ALIAS}      { return Types.QUALIFIED_ALIAS; }
  {CLOSING_PARENTHESIS}  { return Types.CLOSING_PARENTHESIS; }
  {COLON}                { return Types.COLON; }
  {COMMA}                { return Types.COMMA; }
  {INTEGER}              { return Types.INTEGER; }
  {IDENTIFIER_TOKEN}     { return Types.NAME; }
  {NAME_ARITY_SEPARATOR} { return Types.NAME_ARITY_SEPARATOR; }
  {OPENING_PARENTHESIS}  { return Types.OPENING_PARENTHESIS; }
  {REFERENCE_OPERATOR}   { return Types.REFERENCE_OPERATOR; }
  {SYMBOLIC_OPERATOR}    { return Types.SYMBOLIC_OPERATOR; }
  {WHITE_SPACE}          { return TokenType.WHITE_SPACE; }
}

. { return TokenType.BAD_CHARACTER; }
