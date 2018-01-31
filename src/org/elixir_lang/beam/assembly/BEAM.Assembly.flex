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

SINGLE_QUOTE="'"
ESCAPED_SINGLE_QUOTE="\\'"
NOT_SINGLE_QUOTE=[^']
CHARLIST = {SINGLE_QUOTE} ({ESCAPED_SINGLE_QUOTE} | {NOT_SINGLE_QUOTE})* {SINGLE_QUOTE}

DOUBLE_QUOTE="\""
ESCAPED_DOUBLE_QUOTE="\\\""
NOT_DOUBLE_QUOTE=[^\"]
STRING={DOUBLE_QUOTE} ({ESCAPED_DOUBLE_QUOTE} | {NOT_DOUBLE_QUOTE})* {DOUBLE_QUOTE}

COLON=":"
ATOM={COLON} ({IDENTIFIER_TOKEN} | {SYMBOLIC_OPERATOR} | {STRING})

ATOM_KEYWORD = "false" | "nil" | "true"

CLOSING_BIT = ">>"
CLOSING_BRACKET="]"
CLOSING_CURLY="}"
CLOSING_PARENTHESIS=")"
COMMA=","
DOT_OPERATOR="."
INTEGER=0|[1-9][0-9]*
KEY=({IDENTIFIER_TOKEN} | {SYMBOLIC_OPERATOR}){COLON}
MAP_OPERATOR="%"
NAME_ARITY_SEPARATOR="/"
OPENING_BIT = "<<"
OPENING_BRACKET="["
OPENING_CURLY="{"
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
                      "&" |
                      "*" |
                      "+" |
                      "-" |
                      "/" |
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
  {ATOM_KEYWORD}         { return Types.ATOM_KEYWORD; }
  {CHARLIST}             { return Types.CHARLIST; }
  {DOT_OPERATOR}         { return Types.DOT_OPERATOR; }
  {QUALIFIED_ALIAS}      { return Types.QUALIFIED_ALIAS; }
  {CLOSING_BIT}          { return Types.CLOSING_BIT; }
  {CLOSING_BRACKET}      { return Types.CLOSING_BRACKET; }
  {CLOSING_CURLY}        { return Types.CLOSING_CURLY; }
  {CLOSING_PARENTHESIS}  { return Types.CLOSING_PARENTHESIS; }
  {COMMA}                { return Types.COMMA; }
  {INTEGER}              { return Types.INTEGER; }
  {IDENTIFIER_TOKEN}     { return Types.NAME; }
  {KEY} / {WHITE_SPACE}  { return Types.KEY; }
  {MAP_OPERATOR}         { return Types.MAP_OPERATOR; }
  {NAME_ARITY_SEPARATOR} { return Types.NAME_ARITY_SEPARATOR; }
  {OPENING_BIT}          { return Types.OPENING_BIT; }
  {OPENING_BRACKET}      { return Types.OPENING_BRACKET; }
  {OPENING_CURLY}        { return Types.OPENING_CURLY; }
  {OPENING_PARENTHESIS}  { return Types.OPENING_PARENTHESIS; }
  {REFERENCE_OPERATOR}   { return Types.REFERENCE_OPERATOR; }
  {STRING}               { return Types.STRING; }
  {SYMBOLIC_OPERATOR}    { return Types.SYMBOLIC_OPERATOR; }
  {WHITE_SPACE}          { return TokenType.WHITE_SPACE; }
}

. { return TokenType.BAD_CHARACTER; }
