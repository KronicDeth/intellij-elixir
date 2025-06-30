package org.elixir_lang.heex.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import kotlinx.html.SCRIPT;import org.elixir_lang.heex.psi.Types;

%%

// public instead of package-local to make testing easier.
%public
%class Flex
%implements com.intellij.lexer.FlexLexer
%unicode
%ignorecase
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private int openBraceCount = 0;

  private void handleInState(int nextLexicalState) {
    yypushback(yylength());
    yybegin(nextLexicalState);
  }
%}

OPENING = "<%"
CLOSING = "%>"

BRACE_OPENING = "{"
BRACE_CLOSING = "}"

COMMENT_MARKER = "#"
EQUALS_MARKER = "="
// See https://github.com/elixir-lang/elixir/pull/6281
FORWARD_SLASH_MARKER = "/"
PIPE_MARKER = "|"
ESCAPED_OPENING = "<%%"
PROCEDURAL_OPENING = {OPENING} " "

WHITE_SPACE = [\ \t\f\r\n]+
ANY = [^]

START_SCRIPT_TAG = "<script"
END_SCRIPT_TAG = "</script>"
START_STYLE_TAG = "<style"
END_STYLE_TAG = "</style>"

%state WHITESPACE_MAYBE
%state COMMENT
%state ELIXIR
%state MARKER_MAYBE
%state BEGIN_MATCHED_BRACES, MATCHED_BRACES
%state STYLE_TAG,SCRIPT_TAG

%%

<YYINITIAL> {
  {BRACE_OPENING}   { yybegin(BEGIN_MATCHED_BRACES);
                      return Types.BRACE_OPENING; }
  {START_SCRIPT_TAG} { yybegin(SCRIPT_TAG); return Types.DATA; }
  {START_STYLE_TAG} { yybegin(STYLE_TAG); return Types.DATA; }
}

<SCRIPT_TAG> {
  {END_SCRIPT_TAG}  { yybegin(YYINITIAL); return Types.DATA; }
}

<STYLE_TAG> {
  {END_STYLE_TAG}  { yybegin(YYINITIAL); return Types.DATA; }
}

<YYINITIAL,SCRIPT_TAG,STYLE_TAG> {
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
                           return Types.EMPTY_MARKER; }
}

<BEGIN_MATCHED_BRACES> {
  // We pretend there is an equals marker so it looks like a <%= tag to the Elixir parser
  {ANY}                  { handleInState(MATCHED_BRACES);
                           return Types.EQUALS_MARKER; }
}

<MATCHED_BRACES> {
  {BRACE_OPENING}        { openBraceCount++;
                           return Types.ELIXIR; }
  {BRACE_CLOSING}        {
                           if (openBraceCount > 0) {
                             openBraceCount--;
                             return Types.ELIXIR;
                           } else {
                             yybegin(YYINITIAL);
                             return Types.BRACE_CLOSING;
                           }
                         }
  {ANY}                  { return Types.ELIXIR; }
}

<COMMENT, ELIXIR> {
  {CLOSING} { yybegin(WHITESPACE_MAYBE);
              return Types.CLOSING; }
}

<COMMENT> {
  {ANY} { return Types.COMMENT; }
}

<ELIXIR> {
  {ANY} { return Types.ELIXIR; }
}

<WHITESPACE_MAYBE> {
  // Only completely whitespace before a procedural tag counts as whitespace
  {WHITE_SPACE} / {PROCEDURAL_OPENING} { yybegin(YYINITIAL);
                                         return TokenType.WHITE_SPACE; }
  {ANY}                                { handleInState(YYINITIAL); }
}

