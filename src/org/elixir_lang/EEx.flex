package org.elixir_lang.eex.lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.eex.psi.Types;

%%

// public instead of package-local to make testing easier.
%public
%class Flex
%implements com.intellij.lexer.FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

OPENING = "<%"
CLOSING = "%>"

OPENING_COMMENT = "<%#"
OPENING_EQUALS = "<%="
OPENING_QUOTATION = "<%%"

ANY = [^]

%state COMMENT
%state ELIXIR
%state QUOTATION

%%

<YYINITIAL> {
  {OPENING}           { yybegin(ELIXIR);
                        return Types.OPENING; }
  {OPENING_COMMENT}   { yybegin(COMMENT);
                        return Types.OPENING_COMMENT; }
  {OPENING_EQUALS}    { yybegin(ELIXIR);
                        return Types.OPENING_EQUALS; }
  {OPENING_QUOTATION} { yybegin(QUOTATION);
                        return Types.OPENING_QUOTATION; }
  {ANY}               { return Types.DATA; }
}

<COMMENT, ELIXIR, QUOTATION> {
  {CLOSING}  { yybegin(YYINITIAL);
               return Types.CLOSING; }
}

<COMMENT> {
  {ANY} { return Types.COMMENT; }
}

<ELIXIR> {
  {ANY} { return Types.ELIXIR; }
}

<QUOTATION> {
  {ANY} { return Types.QUOTATION; }
}
