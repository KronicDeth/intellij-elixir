package org.elixir_lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.lexer.group.*;
import org.elixir_lang.psi.ElixirTypes;

%%

// public instead of package-local to make testing easier.
%public
%class ElixirFlexLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private org.elixir_lang.lexer.Stack stack = new org.elixir_lang.lexer.Stack();

  private void startQuote(CharSequence quotePromoterCharSequence) {
    String quotePromoter = quotePromoterCharSequence.toString();
    stack.push(quotePromoter, yystate());

    if (Base.isHeredocPromoter(quotePromoter)) {
      yybegin(GROUP_HEREDOC_START);
    } else {
      yybegin(GROUP);
    }
  }

  private IElementType fragmentType() {
    return stack.fragmentType();
  }

  private void handleInState(int nextLexicalState) {
    yypushback(yylength());
    yybegin(nextLexicalState);
  }

  private boolean isTerminator(CharSequence terminator) {
    return stack.terminator().equals(terminator.toString());
  }

  private boolean isInterpolating() {
    return stack.isInterpolating();
  }

  private boolean isInterpolatingSigil(CharSequence sigilName) {
    if (sigilName.length() != 1) {
      throw new IllegalArgumentException("sigil names can only be 1 character long");
    }

    return isInterpolatingSigil(sigilName.charAt(0));
  }

  private boolean isInterpolatingSigil(char sigilName) {
    return (sigilName >= 'a' && sigilName <= 'z');
  }

  private boolean isSigil() {
    return stack.isSigil();
  }

  private void nameSigil(CharSequence sigilName) {
    stack.nameSigil(sigilName.charAt(0));
  }

  private org.elixir_lang.lexer.StackFrame pop() {
    return stack.pop();
  }

  private org.elixir_lang.lexer.group.Quote promotedQuote(CharSequence promoterCharSequence) {
    // CharSequences don't look up correctly, so convert to String, which do.
    String promoter = promoterCharSequence.toString();
    org.elixir_lang.lexer.group.Quote quote = org.elixir_lang.lexer.group.Quote.fetch(promoter);

    return quote;
  }

  private IElementType promoterType() {
    return stack.promoterType();
  }

  private void setPromoter(CharSequence promoter) {
    stack.setPromoter(promoter.toString());
  }

  private IElementType sigilNameType() {
    return stack.sigilNameType();
  }

  // public for testing
  public void pushAndBegin(int lexicalState) {
    stack.push(yystate());
    yybegin(lexicalState);
  }

  private IElementType terminatorType() {
    return stack.terminatorType();
  }
%}

/*
 * Operator
 *
 * Note: before Atom because operator prefixed by {COLON} are valid Atoms
 */

FOUR_TOKEN_OPERATOR = "<<>>"

THREE_TOKEN_OPERATOR = "!==" |
                       "%{}" |
                       "&&&" |
                       "..." |
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

TWO_TOKEN_OPERATOR = "!=" |
                     "&&" |
                     "++" |
                     "--" |
                     "->" |
                     "::" |
                     "<-" |
                     "<=" |
                     "<>" |
                     "<~" |
                     "==" |
                     "=~" |
                     ">=" |
                     "\\\\" |
                     "{}" |
                     "|>" |
                     "||" |
                     "~>"

ONE_TOKEN_OPERATOR = "!" |
                     "%" |
                     "&" |
                     "*" |
                     "+" |
                     "-" |
                     "." |
                     "/" |
                     "<" |
                     "=" |
                     ">" |
                     "@" |
                     "^" |
                     "|"

// OPERATOR is from longest to shortest so longest match wins
OPERATOR = {FOUR_TOKEN_OPERATOR} |
           {THREE_TOKEN_OPERATOR} |
           {TWO_TOKEN_OPERATOR} |
           {ONE_TOKEN_OPERATOR}

/*
 * Atom
 */

ATOM_END = [?!]
ATOM_MIDDLE = [0-9a-zA-Z@_]
ATOM_START = [a-zA-Z_]
COLON = :

/*
 * Digits
 */

HEXADECIMAL_DIGIT = [A-Fa-f0-9]

/*
 * Escape Sequences
 */

ESCAPE = "\\"

ESCAPED_CHARACTER = {ESCAPE} .
ESCAPED_CHARACTER_CODE = {ESCAPE} "x{" {HEXADECIMAL_DIGIT}{1,6} "}" |
                         {ESCAPE} "x" {HEXADECIMAL_DIGIT}{1,2}

VALID_ESCAPE_SEQUENCE = {ESCAPED_CHARACTER_CODE} |
                        {ESCAPED_CHARACTER}

/*
 * Char tokens
 */

CHAR_TOKEN = "?" ({VALID_ESCAPE_SEQUENCE} | .)

/*
 * White Space
 */

EOL = \n|\r\n|;
WHITE_SPACE=[\ \t\f]

/*
 *  Comments
 */

COMMENT = "#" [^\r\n]*

/*
 *   Integers
 */

// Include deprecated '0B' so it can be corrected
BINARY_INTEGER = "0" [Bb][01]+
// Include deprecated '0X' so it can be corrected
HEXADECIMAL_INTEGER = "0" [Xx]{HEXADECIMAL_DIGIT}+
// Include deprecated '0' so it can be corrected
OCTAL_INTEGER = "0" o?[0-7]+
INTEGER = {BINARY_INTEGER} | {HEXADECIMAL_INTEGER} | {OCTAL_INTEGER}

/*
 * Interpolation
 */

INTERPOLATION_START = "#{"
INTERPOLATION_END = "}"

/*
 *
 *  Quotes
 *
 */

CHAR_LIST_PROMOTER = "'"
CHAR_LIST_TERMINATOR = "'"

STRING_PROMOTER = "\""
STRING_TERMINATOR = "\""

QUOTE_PROMOTER = {CHAR_LIST_PROMOTER} | {STRING_PROMOTER}
QUOTE_TERMINATOR = {CHAR_LIST_TERMINATOR} | {STRING_TERMINATOR}

/*
 * Quote Heredocs
 */

CHAR_LIST_HEREDOC_PROMOTER = {CHAR_LIST_PROMOTER}{3}
CHAR_LIST_HEREDOC_TERMINATOR = {CHAR_LIST_TERMINATOR}{3}

STRING_HEREDOC_PROMOTER = {STRING_PROMOTER}{3}
STRING_HEREDOC_TERMINATOR = {STRING_TERMINATOR}{3}

QUOTE_HEREDOC_PROMOTER = {CHAR_LIST_HEREDOC_PROMOTER} | {STRING_HEREDOC_PROMOTER}
QUOTE_HEREDOC_TERMINATOR = {CHAR_LIST_HEREDOC_TERMINATOR} | {STRING_HEREDOC_TERMINATOR}

/*
 *
 *  Sigils
 *
 */

TILDE = "~"
SIGIL_MODIFIER = [a-z]
SIGIL_NAME = [A-Za-z]

/*
 * Sigil quotes
 *
 * @see https://github.com/elixir-lang/elixir/blob/a4a23b9a937cb22b5c1a2487c02289817b991d8f/lib/elixir/src/elixir.hrl#L59-L60
 */

SIGIL_BRACES_PROMOTER = "{"
SIGIL_BRACES_TERMINATOR = "}"
SIGIL_BRACKETS_PROMOTER = "["
SIGIL_BRACKETS_TERMINATOR = "]"
SIGIL_CHEVRONS_PROMOTER = "<"
SIGIL_CHEVRONS_TERMINATOR = ">"
SIGIL_DOUBLE_QUOTES_PROMOTER = "\""
SIGIL_DOUBLE_QUOTES_TERMINATOR = "\""
SIGIL_FORWARD_SLASH_PROMOTER = "/"
SIGIL_FORWARD_SLASH_TERMINATOR = "/"
SIGIL_PARENTHESES_PROMOTER = "("
SIGIL_PARENTHESES_TERMINATOR = ")"
SIGIL_PIPE_PROMOTER = "|"
SIGIL_PIPE_TERMINATOR = "|"
SIGIL_SINGLE_QUOTES_PROMOTER = "'"
SIGIL_SINGLE_QUOTES_TERMINATOR = "'"

SIGIL_PROMOTER = {SIGIL_BRACES_PROMOTER} |
                 {SIGIL_BRACKETS_PROMOTER} |
                 {SIGIL_CHEVRONS_PROMOTER} |
                 {SIGIL_DOUBLE_QUOTES_PROMOTER} |
                 {SIGIL_FORWARD_SLASH_PROMOTER} |
                 {SIGIL_PARENTHESES_PROMOTER} |
                 {SIGIL_PIPE_PROMOTER} |
                 {SIGIL_SINGLE_QUOTES_PROMOTER}


SIGIL_TERMINATOR = {SIGIL_BRACES_TERMINATOR} |
                   {SIGIL_BRACKETS_TERMINATOR} |
                   {SIGIL_CHEVRONS_TERMINATOR} |
                   {SIGIL_DOUBLE_QUOTES_TERMINATOR} |
                   {SIGIL_FORWARD_SLASH_TERMINATOR} |
                   {SIGIL_PARENTHESES_TERMINATOR} |
                   {SIGIL_PIPE_TERMINATOR} |
                   {SIGIL_SINGLE_QUOTES_TERMINATOR}

/*
 * Sigil heredocs
 */

SIGIL_HEREDOC_PROMOTER = ({SIGIL_DOUBLE_QUOTES_PROMOTER}|{SIGIL_SINGLE_QUOTES_PROMOTER}){3}
SIGIL_HEREDOC_TERMINATOR = ({SIGIL_DOUBLE_QUOTES_TERMINATOR}|{SIGIL_SINGLE_QUOTES_TERMINATOR}){3}

/*
 * Groups
 */

GROUP_TERMINATOR = {QUOTE_TERMINATOR}|{SIGIL_TERMINATOR}
GROUP_HEREDOC_TERMINATOR = {QUOTE_HEREDOC_TERMINATOR}|{SIGIL_HEREDOC_TERMINATOR}

/*
 *  States - Ordered lexigraphically
 */

%state ATOM_BODY
%state ATOM_START
%state GROUP
%state GROUP_HEREDOC_END
%state GROUP_HEREDOC_LINE_BODY
%state GROUP_HEREDOC_LINE_START
%state GROUP_HEREDOC_START
%state INTERPOLATION
%state NAMED_SIGIL
%state SIGIL
%state SIGIL_MODIFIERS

%%

/* <YYINITIAL> is first even though it isn't lexicographically first because it is the first state.
   Rules that aren't dependent on detecting the end of INTERPOLATION can be shared between <YYINITIAL> and
   <INTERPOLATION> */
<YYINITIAL, INTERPOLATION> {
  // Blank line
  ^{WHITE_SPACE}*{EOL}                      { return TokenType.WHITE_SPACE; }
  // EOL preceded by non-whitespace.  These EOLs are significant for Elixir's grammar for separating expressions.
  {EOL}                                     { return ElixirTypes.EOL; }
  // This rule is only meant to match whitespace surrounded by other tokens as the above rule will handle blank lines.
  {WHITE_SPACE}+                            { return TokenType.WHITE_SPACE; }

  {CHAR_TOKEN}                              { return ElixirTypes.CHAR_TOKEN; }
  
  {COLON}                                   { pushAndBegin(ATOM_START);
                                              return ElixirTypes.COLON; }

  {COMMENT}                                 { return ElixirTypes.COMMENT; }

  {INTEGER}                                 { return ElixirTypes.NUMBER; }

  {TILDE}                                   { pushAndBegin(SIGIL);
                                              return ElixirTypes.TILDE; }

  {QUOTE_HEREDOC_PROMOTER}                  { startQuote(yytext());
                                              return promoterType(); }
  /* MUST be after {QUOTE_HEREDOC_PROMOTER} for <BODY, INTERPOLATION> as {QUOTE_HEREDOC_PROMOTER} is prefixed by
     {QUOTE_PROMOTER} */
  {QUOTE_PROMOTER}                          { startQuote(yytext());
                                              return promoterType(); }
}

/*
 *  Lexical rules - Ordered alphabetically by state name except when the order of the internal rules need to be
 *  maintained to ensure precedence.
 */

<ATOM_BODY> {
  {ATOM_END}     { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                   yybegin(stackFrame.getLastLexicalState());
                   return ElixirTypes.ATOM_FRAGMENT; }
  {ATOM_MIDDLE}+ { return ElixirTypes.ATOM_FRAGMENT; }
  // any other character ends the atom
  {EOL}|.        { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                   handleInState(stackFrame.getLastLexicalState()); }
}

/// Must be after {QUOTE_PROMOTER} for <ATOM_START> so that
<ATOM_START> {
  {ATOM_START}     { yybegin(ATOM_BODY);
                     return ElixirTypes.ATOM_FRAGMENT; }
  {QUOTE_PROMOTER} { /* At the end of the quote, return the state (YYINITIAL or INTERPOLATION) before ATOM_START as
                        anything after the closing quote should be handle by the state prior to ATOM_START.  Without
                        this, EOL and WHITESPACE won't be handled correctly */
                     org.elixir_lang.lexer.StackFrame stackFrame = pop();
                     yybegin(stackFrame.getLastLexicalState());
                     startQuote(yytext());
                     return promoterType(); }
  {OPERATOR}       { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                     yybegin(stackFrame.getLastLexicalState());
                     return ElixirTypes.ATOM_FRAGMENT; }
  {EOL}            { return TokenType.BAD_CHARACTER; }
}

<GROUP,
 GROUP_HEREDOC_LINE_BODY> {
  {INTERPOLATION_START}   {
                            if (isInterpolating()) {
                             pushAndBegin(INTERPOLATION);
                             return ElixirTypes.INTERPOLATION_START;
                            } else {
                             return fragmentType();
                            }
                          }
  {VALID_ESCAPE_SEQUENCE} {
                            if (isInterpolating()) {
                              return ElixirTypes.VALID_ESCAPE_SEQUENCE;
                            } else {
                              return fragmentType();
                            }
                          }
}

// Rules in GROUP, but not GROUP_HEREDOC_LINE_BODY
<GROUP> {
  {GROUP_TERMINATOR} {
                       if (isTerminator(yytext())) {
                         org.elixir_lang.lexer.StackFrame stackFrame = pop();
                         yybegin(stackFrame.getLastLexicalState());
                         return stackFrame.terminatorType();
                       } else {
                         return fragmentType();
                       }
                     }
  {EOL}|.            { return fragmentType(); }

}

<GROUP_HEREDOC_END> {
    {GROUP_HEREDOC_TERMINATOR} {
                                   if (isTerminator(yytext())) {
                                      if (isSigil()) {
                                        yybegin(SIGIL_MODIFIERS);
                                        return terminatorType();
                                      } else {
                                        org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                        yybegin(stackFrame.getLastLexicalState());
                                        return stackFrame.terminatorType();
                                      }
                                   } else {
                                      handleInState(GROUP_HEREDOC_LINE_BODY);
                                   }
                               }
}

// Rules in GROUP_HEREDOC_LINE_BODY, but not GROUP
<GROUP_HEREDOC_LINE_BODY> {
  {EOL} {
          yybegin(GROUP_HEREDOC_LINE_START);
          return fragmentType();
        }
  .     { return fragmentType(); }
}

<GROUP_HEREDOC_LINE_START> {
  {WHITE_SPACE}+ / {GROUP_HEREDOC_TERMINATOR} {
                                                  yybegin(GROUP_HEREDOC_END);
                                                  return TokenType.WHITE_SPACE;
                                              }
  {GROUP_HEREDOC_TERMINATOR}                  { handleInState(GROUP_HEREDOC_END); }
  {EOL}|.                                     { handleInState(GROUP_HEREDOC_LINE_BODY); }
}

<GROUP_HEREDOC_START> {
  {EOL} { yybegin(GROUP_HEREDOC_LINE_START);
          return ElixirTypes.EOL; }
}

/* Only rules for <INTERPOLATON>, but not <YYINITIAL> go here.
   @note must be after <YYINITIAL, INTERPOLATION> so that BAD_CHARACTER doesn't match a single ' ' instead of
     {WHITE_SPACE}+. */
<INTERPOLATION> {
  {INTERPOLATION_END}         { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                yybegin(stackFrame.getLastLexicalState());
                                return ElixirTypes.INTERPOLATION_END; }
}

<NAMED_SIGIL> {
  {SIGIL_HEREDOC_PROMOTER} { setPromoter(yytext());
                             yybegin(GROUP_HEREDOC_START);
                             return promoterType(); }
  {SIGIL_PROMOTER}         { setPromoter(yytext());
                             yybegin(GROUP);
                             return promoterType(); }
}

<SIGIL> {
  {SIGIL_NAME}               { nameSigil(yytext());
                               yybegin(NAMED_SIGIL);
                               return sigilNameType(); }
  {EOL}                      { return TokenType.BAD_CHARACTER; }
}

<SIGIL_MODIFIERS> {
  {SIGIL_MODIFIER} { return ElixirTypes.SIGIL_MODIFIER; }
  {EOL}|.          { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                     handleInState(stackFrame.getLastLexicalState()); }
}

// MUST go last so that . mapping to BAD_CHARACTER is the rule of last resort for the listed states
<ATOM_START, GROUP_HEREDOC_START, INTERPOLATION, NAMED_SIGIL, SIGIL, YYINITIAL> {
  . { return TokenType.BAD_CHARACTER; }
}
