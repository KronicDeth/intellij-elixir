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

%{
  private void handleInState(int nextLexicalState) {
    yypushback(yylength());
    yybegin(nextLexicalState);
  }
%}

OPENING = "<%"
CLOSING = "%>"

OPENING_COMMENT = "<%#"
OPENING_QUOTATION = "<%%"

EQUALS_MARKER = "="
// See https://github.com/elixir-lang/elixir/pull/6281
FORWARD_SLASH_MARKER = "/"
PIPE_MARKER = "|"

ANY = [^]

%state COMMENT
%state ELIXIR
%state QUOTATION
%state MARKER_MAYBE

%%

<YYINITIAL> {
  {OPENING}           { yybegin(MARKER_MAYBE);
                        return Types.OPENING; }
  {OPENING_COMMENT}   { yybegin(COMMENT);
                        return Types.OPENING_COMMENT; }
  {OPENING_QUOTATION} { yybegin(QUOTATION);
                        return Types.OPENING_QUOTATION; }
  {ANY}               { return Types.DATA; }
}

<MARKER_MAYBE> {
  {EQUALS_MARKER}         { yybegin(ELIXIR);
                            return Types.EQUALS_MARKER; }
  {FORWARD_SLASH_MARKER}  { yybegin(ELIXIR);
                            return Types.FORWARD_SLASH_MARKER; }
  {PIPE_MARKER}           { yybegin(ELIXIR);
                            return Types.PIPE_MARKER; }
  {ANY}                   { handleInState(ELIXIR); }
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
