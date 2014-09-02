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

  private void pushAndBegin(int lexicalState) {
    stack.push(yystate());
    yybegin(lexicalState);
  }
%}

/*
 * White Space
 */

EOL = \n|\r|\r\n
WHITE_SPACE=[\ \t\f]

/*
 *  Comments
 */

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
SIGIL_NAME = [A-Za-z]

/*
 * Sigil quotes
 */

SIGIL_BRACES_PROMOTER = "{"
SIGIL_BRACES_TERMINATOR = "}"
SIGIL_BRACKETS_PROMOTER = "["
SIGIL_BRACKETS_TERMINATOR = "]"
SIGIL_CHEVRONS_PROMOTER = "<"
SIGIL_CHEVRONS_TERMINATOR = ">"
SIGIL_DOUBLE_QUOTES_PROMOTER = "\""
SIGIL_DOUBLE_QUOTES_TERMINATOR = "\""
SIGIL_PARENTHESES_PROMOTER = "("
SIGIL_PARENTHESES_TERMINATOR = ")"
SIGIL_SINGLE_QUOTES_PROMOTER = "'"
SIGIL_SINGLE_QUOTES_TERMINATOR = "'"

SIGIL_PROMOTER = {SIGIL_BRACES_PROMOTER} |
                 {SIGIL_BRACKETS_PROMOTER} |
                 {SIGIL_CHEVRONS_PROMOTER} |
                 {SIGIL_DOUBLE_QUOTES_PROMOTER} |
                 {SIGIL_PARENTHESES_PROMOTER} |
                 {SIGIL_SINGLE_QUOTES_PROMOTER}


SIGIL_TERMINATOR = {SIGIL_BRACES_TERMINATOR} |
                   {SIGIL_BRACKETS_TERMINATOR} |
                   {SIGIL_CHEVRONS_TERMINATOR} |
                   {SIGIL_DOUBLE_QUOTES_TERMINATOR} |
                   {SIGIL_PARENTHESES_TERMINATOR} |
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
 * Escape Sequences
 */

ESCAPE = "\\"
ESCAPED_DOUBLE_QUOTES = {ESCAPE} "\""
ESCAPED_SINGLE_QUOTE = {ESCAPE} "'"
ESCAPED_INTERPOLATION_START = {ESCAPE} {INTERPOLATION_START}

VALID_ESCAPE_SEQUENCE = {ESCAPED_DOUBLE_QUOTES} |
                        {ESCAPED_SINGLE_QUOTE} |
                        {ESCAPED_INTERPOLATION_START}

/*
 *  States - Ordered lexigraphically
 */

// state after YYINITIAL has taken care of any white space prefix
%state BODY
%state GROUP
%state GROUP_HEREDOC_END
%state GROUP_HEREDOC_LINE_BODY
%state GROUP_HEREDOC_LINE_START
%state GROUP_HEREDOC_START
%state INTERPOLATION
%state NAMED_SIGIL
%state SIGIL

%%

// YYINITIAL is first even though it isn't lexicographically first because it is the first state.
<YYINITIAL> {
  /* Turn EOL and whitespace at beginning of file into a single {@link org.elixir_lang.psi.ElixirTypes.WHITE_SPACE} so
   * it is filtered out.
   */
  ({EOL}|{WHITE_SPACE})+ { yybegin(BODY);
                           return TokenType.WHITE_SPACE; }

  // Push back and left BODY handle normal actions so they don't need to be duplicated in YYINITIAL and BODY.
  .                      { handleInState(BODY); }
}

/*
 *  Lexical rules - Ordered alphabetically by state name except when the order of the internal rules need to be
 *  maintained to ensure precedence.
 */

// Rules that aren't dependent on detecting the end of INTERPOLATION can be shared between <BODY> and <INTERPOLATION>
<BODY, INTERPOLATION> {
  /* Compress {EOL} followed by more {EOL} or whitespace into {EOL} as only {EOL} is significant for Elixir's grammar.
   *
   * @see https://github.com/idavis/Innovatian.Idea.PowerShell/blob/80bbe5bbcb15f95d8b33f4a34b86acb6b65ac67e/src/lang/lexer/PowerShell.flex#L78
   * @see https://github.com/idavis/Innovatian.Idea.PowerShell/blob/80bbe5bbcb15f95d8b33f4a34b86acb6b65ac67e/src/lang/lexer/PowerShell.flex#L227
   */
  {EOL}({EOL}|{WHITE_SPACE})*               { return ElixirTypes.EOL; }
  // This rule is only meant to match whitespace surrounded by other tokens as the above rule will handle blank lines.
  {WHITE_SPACE}+                            { return TokenType.WHITE_SPACE; }

  {COMMENT}                                 { return ElixirTypes.COMMENT; }

  {INTEGER}                                 { return ElixirTypes.NUMBER; }

  {TILDE}                                   { pushAndBegin(SIGIL);
                                              return ElixirTypes.TILDE; }

  {QUOTE_HEREDOC_PROMOTER}                  { startQuote(yytext());
                                              return promoterType(); }

  {QUOTE_PROMOTER}                          { startQuote(yytext());
                                              return promoterType(); }
}

<NAMED_SIGIL> {
  {SIGIL_HEREDOC_PROMOTER} { setPromoter(yytext());
                             yybegin(GROUP_HEREDOC_START);
                             return promoterType(); }
  {SIGIL_PROMOTER}         { setPromoter(yytext());
                             yybegin(GROUP);
                             return promoterType(); }
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
  .                                           { handleInState(GROUP_HEREDOC_LINE_BODY); }
}

<GROUP_HEREDOC_END> {
    {GROUP_HEREDOC_TERMINATOR} {
                                   if (isTerminator(yytext())) {
                                      org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                      yybegin(stackFrame.getLastLexicalState());
                                      return stackFrame.terminatorType();
                                   } else {
                                      handleInState(GROUP_HEREDOC_LINE_BODY);
                                   }
                               }
}

<GROUP_HEREDOC_START> {
  {EOL} { yybegin(GROUP_HEREDOC_LINE_START);
          return ElixirTypes.EOL; }
}

// Only rules for <INTERPOLATON>, but not <BODY> go here.
// @note must be after <BODY, INTERPOLATION> so that BAD_CHARACTER doesn't match a single ' ' instead of {WHITE_SPACE}+.
<INTERPOLATION> {
  {INTERPOLATION_END}         { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                yybegin(stackFrame.getLastLexicalState());
                                return ElixirTypes.INTERPOLATION_END; }
}

<SIGIL> {
  {SIGIL_NAME}               { nameSigil(yytext());
                               yybegin(NAMED_SIGIL);
                               return sigilNameType(); }
}

// MUST go last so that . mapping to BAD_CHARACTER is the rule of last resort for the listed states
<BODY, GROUP_HEREDOC_START, INTERPOLATION, NAMED_SIGIL, SIGIL> {
  . { return TokenType.BAD_CHARACTER; }
}
