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

FOUR_TOKEN_WHEN_OPERATOR = "when"
FOUR_TOKEN_OPERATOR = {FOUR_TOKEN_WHEN_OPERATOR} |
                      "<<>>"

THREE_TOKEN_AND_OPERATOR = "&&&" |
                           "and"
THREE_TOKEN_ARROW_OPERATOR = "<<<" |
                             "<<~" |
                             "<|>" |
                             "<~>" |
                             ">>>" |
                             "~>>"
THREE_TOKEN_COMPARISON_OPERATOR = "!==" |
                                  "==="
THREE_TOKEN_OR_OPERATOR = "|||"
THREE_TOKEN_HAT_OPERATOR = "^^^"
THREE_TOKEN_UNARY_OPERATOR = "not" |
                             "~~~"

THREE_TOKEN_OPERATOR = {THREE_TOKEN_AND_OPERATOR} |
                       {THREE_TOKEN_ARROW_OPERATOR} |
                       {THREE_TOKEN_COMPARISON_OPERATOR} |
                       {THREE_TOKEN_OR_OPERATOR} |
                       {THREE_TOKEN_UNARY_OPERATOR} |
                       {THREE_TOKEN_HAT_OPERATOR} |
                       "%{}" |
                       "..."

TWO_TOKEN_AND_OPERATOR = "&&"
TWO_TOKEN_ARROW_OPERATOR = "<~" |
                           "|>" |
                           "~>"
TWO_TOKEN_ASSOCIATION_OPERATOR = "=>"
TWO_TOKEN_COMPARISON_OPERATOR = "!=" |
                                "==" |
                                "=~"
TWO_TOKEN_IN_MATCH_OPERATOR = "<-" |
                              "\\\\"
TWO_TOKEN_OR_OPERATOR = "or" |
                        "||"
TWO_TOKEN_RELATIONAL_OPERATOR = "<=" |
                                ">="
TWO_TOKEN_STAB_OPERATOR = "->"
TWO_TOKEN_TWO_OPERATOR = "++" |
                         "--" |
                         ".." |
                         "<>"
TWO_TOKEN_TYPE_OPERATOR = "::"

TWO_TOKEN_OPERATOR = {TWO_TOKEN_AND_OPERATOR} |
                     {TWO_TOKEN_ARROW_OPERATOR} |
                     {TWO_TOKEN_ASSOCIATION_OPERATOR} |
                     {TWO_TOKEN_COMPARISON_OPERATOR} |
                     {TWO_TOKEN_IN_MATCH_OPERATOR} |
                     {TWO_TOKEN_OR_OPERATOR} |
                     {TWO_TOKEN_RELATIONAL_OPERATOR} |
                     {TWO_TOKEN_STAB_OPERATOR} |
                     {TWO_TOKEN_TWO_OPERATOR} |
                     {TWO_TOKEN_TYPE_OPERATOR} |
                     "{}"

ONE_TOKEN_AT_OPERATOR = "@"
ONE_TOKEN_CAPTURE_OPERATOR = "&"
/* Dual because they have a dual role as unary operators and binary operators
   @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L31-L32 */
ONE_TOKEN_DUAL_OPERATOR = "+" |
                          "-"
ONE_TOKEN_MATCH_OPERATOR = "="
ONE_TOKEN_MULTIPLICATION_OPERATOR = "*" |
                                    "/"
ONE_TOKEN_PIPE_OPERATOR = "|"
ONE_TOKEN_RELATIONAL_OPERATOR = "<" |
                                ">"
ONE_TOKEN_UNARY_OPERATOR = "!" |
                           "^"

ONE_TOKEN_OPERATOR = {ONE_TOKEN_AT_OPERATOR} |
                     {ONE_TOKEN_CAPTURE_OPERATOR} |
                     {ONE_TOKEN_DUAL_OPERATOR} |
                     {ONE_TOKEN_MATCH_OPERATOR} |
                     {ONE_TOKEN_MULTIPLICATION_OPERATOR} |
                     {ONE_TOKEN_PIPE_OPERATOR} |
                     {ONE_TOKEN_RELATIONAL_OPERATOR} |
                     {ONE_TOKEN_UNARY_OPERATOR} |
                     "%" |
                     "."

AND_OPERATOR = {THREE_TOKEN_AND_OPERATOR} |
               {TWO_TOKEN_AND_OPERATOR}
ARROW_OPERATOR = {THREE_TOKEN_ARROW_OPERATOR} |
                 {TWO_TOKEN_ARROW_OPERATOR}
ASSOCIATION_OPERATOR = {TWO_TOKEN_ASSOCIATION_OPERATOR}
AT_OPERATOR = {ONE_TOKEN_AT_OPERATOR}
CAPTURE_OPERATOR = {ONE_TOKEN_CAPTURE_OPERATOR}
// Dual because they have a dual role as unary operators and binary operators
DUAL_OPERATOR = {ONE_TOKEN_DUAL_OPERATOR}
COMPARISON_OPERATOR = {THREE_TOKEN_COMPARISON_OPERATOR} |
                      {TWO_TOKEN_COMPARISON_OPERATOR}
HAT_OPERATOR = {THREE_TOKEN_HAT_OPERATOR}
IN_MATCH_OPERATOR = {TWO_TOKEN_IN_MATCH_OPERATOR}
MATCH_OPERATOR = {ONE_TOKEN_MATCH_OPERATOR}
MULTIPLICATION_OPERATOR = {ONE_TOKEN_MULTIPLICATION_OPERATOR}
OR_OPERATOR = {THREE_TOKEN_OR_OPERATOR} |
              {TWO_TOKEN_OR_OPERATOR}
PIPE_OPERATOR = {ONE_TOKEN_PIPE_OPERATOR}
RELATIONAL_OPERATOR = {TWO_TOKEN_RELATIONAL_OPERATOR} |
                      {ONE_TOKEN_RELATIONAL_OPERATOR}
STAB_OPERATOR = {TWO_TOKEN_STAB_OPERATOR}
TWO_OPERATOR = {TWO_TOKEN_TWO_OPERATOR}
TYPE_OPERATOR = {TWO_TOKEN_TYPE_OPERATOR}
UNARY_OPERATOR = {THREE_TOKEN_UNARY_OPERATOR} |
                 {ONE_TOKEN_UNARY_OPERATOR}
WHEN_OPERATOR = {FOUR_TOKEN_WHEN_OPERATOR}

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
 * EOL
 */

CONTROL_EOL = \n|\r\n
EOL = {CONTROL_EOL} | ;

/*
 * Escape Sequences
 */

ESCAPE = "\\"

ESCAPED_CHARACTER = {ESCAPE} .
ESCAPED_CHARACTER_CODE = {ESCAPE} "x{" {HEXADECIMAL_DIGIT}{1,6} "}" |
                         {ESCAPE} "x" {HEXADECIMAL_DIGIT}{1,2}
ESCAPED_CONTROL_EOL = {ESCAPE} {CONTROL_EOL}

VALID_ESCAPE_SEQUENCE = {ESCAPED_CHARACTER_CODE} |
                        {ESCAPED_CHARACTER} |
                        {ESCAPED_CONTROL_EOL}

/*
 * Char tokens
 */

CHAR_TOKEN = "?" ({VALID_ESCAPE_SEQUENCE} | .)

/*
 * White Space
 */

WHITE_SPACE=[\ \t\f]

/*
 *  Comments
 */

COMMENT = "#" [^\r\n]*

/*
 *   Integers
 */

BASE_INTEGER_PREFIX = "0"

BINARY_INTEGER_PREFIX = {BASE_INTEGER_PREFIX} "b"
BINARY_INTEGER = {BINARY_INTEGER_PREFIX} [01]+

DIGIT = [0-9]
DECIMAL_INTEGER = {DIGIT}+

HEXADECIMAL_INTEGER_PREFIX = {BASE_INTEGER_PREFIX} "x"
HEXADECIMAL_INTEGER = {HEXADECIMAL_INTEGER_PREFIX} {HEXADECIMAL_DIGIT}+

OCTAL_INTEGER_PREFIX = {BASE_INTEGER_PREFIX} "o"
OCTAL_INTEGER = {OCTAL_INTEGER_PREFIX} [0-7]+

INTEGER = {BINARY_INTEGER} | {DECIMAL_INTEGER} | {HEXADECIMAL_INTEGER} | {OCTAL_INTEGER}

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
  {AND_OPERATOR}                       { return ElixirTypes.AND_OPERATOR; }
  {ARROW_OPERATOR}                     { return ElixirTypes.ARROW_OPERATOR; }
  {ASSOCIATION_OPERATOR}               { return ElixirTypes.ASSOCIATION_OPERATOR; }
  {AT_OPERATOR}                        { return ElixirTypes.AT_OPERATOR; }
  {CAPTURE_OPERATOR}                   { return ElixirTypes.CAPTURE_OPERATOR; }
  {EOL}                                { return ElixirTypes.EOL; }
  {ESCAPED_CONTROL_EOL}|{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }
  {CHAR_TOKEN}                         { return ElixirTypes.CHAR_TOKEN; }
  /* So that that atom of comparison operator consumes all 3 ':' instead of {TYPE_OPERATOR} consuming '::'
     and ':' being leftover */
  {COLON} / {TYPE_OPERATOR}            { pushAndBegin(ATOM_START);
                                         return ElixirTypes.COLON; }
  // Must be after `{COLON} / {TYPE_OPERATOR}`, so that 3 ':' are consumed before 1.
  {TYPE_OPERATOR}                      { return ElixirTypes.TYPE_OPERATOR; }
  // Must be after {TYPE_OPERATOR}, so that 1 ':' is consumed after 2
  {COLON}                              { pushAndBegin(ATOM_START);
                                         return ElixirTypes.COLON; }
  {COMMENT}                            { return ElixirTypes.COMMENT; }
  {COMPARISON_OPERATOR}                { return ElixirTypes.COMPARISON_OPERATOR; }
  {DUAL_OPERATOR}                      { return ElixirTypes.DUAL_OPERATOR; }
  {HAT_OPERATOR}                       { return ElixirTypes.HAT_OPERATOR; }
  {INTEGER}                            { return ElixirTypes.NUMBER; }
  {IN_MATCH_OPERATOR}                  { return ElixirTypes.IN_MATCH_OPERATOR; }
  {MATCH_OPERATOR}                     { return ElixirTypes.MATCH_OPERATOR; }
  {MULTIPLICATION_OPERATOR}            { return ElixirTypes.MULTIPLICATION_OPERATOR; }
  {OR_OPERATOR}                        { return ElixirTypes.OR_OPERATOR; }
  {PIPE_OPERATOR}                      { return ElixirTypes.PIPE_OPERATOR; }
  {RELATIONAL_OPERATOR}                { return ElixirTypes.RELATIONAL_OPERATOR; }
  {STAB_OPERATOR}                      { return ElixirTypes.STAB_OPERATOR; }
  {UNARY_OPERATOR}                     { return ElixirTypes.UNARY_OPERATOR; }
  {TILDE}                              { pushAndBegin(SIGIL);
                                         return ElixirTypes.TILDE; }
  {TWO_OPERATOR}                       { return ElixirTypes.TWO_OPERATOR; }
  {WHEN_OPERATOR}                      { return ElixirTypes.WHEN_OPERATOR; }
  {QUOTE_HEREDOC_PROMOTER}             { startQuote(yytext());
                                         return promoterType(); }
  /* MUST be after {QUOTE_HEREDOC_PROMOTER} for <BODY, INTERPOLATION> as {QUOTE_HEREDOC_PROMOTER} is prefixed by
     {QUOTE_PROMOTER} */
  {QUOTE_PROMOTER}                     { startQuote(yytext());
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
                         if (isSigil()) {
                           yybegin(SIGIL_MODIFIERS);
                           return terminatorType();
                         } else {
                           org.elixir_lang.lexer.StackFrame stackFrame = pop();
                           yybegin(stackFrame.getLastLexicalState());
                           return stackFrame.terminatorType();
                         }
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
