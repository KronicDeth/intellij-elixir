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

COMMENT_MARKER = "#"
EQUALS_MARKER = "="
// See https://github.com/elixir-lang/elixir/pull/6281
FORWARD_SLASH_MARKER = "/"
PIPE_MARKER = "|"
ESCAPED_OPENING = "<%%"

ANY = [^]

%state COMMENT
%state ELIXIR
%state MARKER_MAYBE

%%

<YYINITIAL> {
  {ESCAPED_OPENING} { return Types.ESCAPED_OPENING; }
  {OPENING}         { yybegin(MARKER_MAYBE);
                      return Types.OPENING; }
  {ANY}             { return Types.DATA; }
}

<MARKER_MAYBE> {
  {COMMENT_MARKER}       { yybegin(COMMENT);
                           return Types.COMMENT_MARKER; }
  {EQUALS_MARKER}        { yybegin(ELIXIR);
                           return Types.EQUALS_MARKER; }
  {FORWARD_SLASH_MARKER} { yybegin(ELIXIR);
                           return Types.FORWARD_SLASH_MARKER; }
  {PIPE_MARKER}          { yybegin(ELIXIR);
                           return Types.PIPE_MARKER; }
  {ANY}                  { handleInState(ELIXIR);
                           return Types.NO_MARKER; }
}

<COMMENT, ELIXIR> {
  {CLOSING} { yybegin(YYINITIAL);
              return Types.CLOSING; }
}

<COMMENT> {
  {ANY} { return Types.COMMENT; }
}

<ELIXIR> {
  {ANY} { return Types.ELIXIR; }
}
