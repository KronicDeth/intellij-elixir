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
 * Curly / Tuple
 */

OPENING_CURLY = "{"
CLOSING_CURLY = "}"

/*
 * Operator
 *
 * Note: before Atom because operator prefixed by {COLON} are valid Atoms
 */

FOUR_TOKEN_BITSTRING_OPERATOR = "<<>>"
FOUR_TOKEN_WHEN_OPERATOR = "when"
FOUR_TOKEN_OPERATOR = {FOUR_TOKEN_BITSTRING_OPERATOR} |
                      {FOUR_TOKEN_WHEN_OPERATOR}

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
THREE_TOKEN_MAP_OPERATOR = "%" {OPENING_CURLY} {CLOSING_CURLY}
THREE_TOKEN_OR_OPERATOR = "|||"
// https://github.com/elixir-lang/elixir/commit/3487d00ddb5e90c7cf0e65d03717903b9b27eafd
THREE_TOKEN_THREE_OPERATOR = "^^^"
THREE_TOKEN_NOT_OPERATOR = "not"
THREE_TOKEN_UNARY_OPERATOR = "~~~"

THREE_TOKEN_OPERATOR = {THREE_TOKEN_AND_OPERATOR} |
                       {THREE_TOKEN_ARROW_OPERATOR} |
                       {THREE_TOKEN_COMPARISON_OPERATOR} |
                       {THREE_TOKEN_MAP_OPERATOR} |
                       {THREE_TOKEN_OR_OPERATOR} |
                       {THREE_TOKEN_THREE_OPERATOR} |
                       {THREE_TOKEN_UNARY_OPERATOR} |
                       {THREE_TOKEN_NOT_OPERATOR} |
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
TWO_TOKEN_TUPLE_OPERATOR = {OPENING_CURLY} {CLOSING_CURLY}
TWO_TOKEN_TWO_OPERATOR = "++" |
                         "--" |
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
                     {TWO_TOKEN_TUPLE_OPERATOR} |
                     {TWO_TOKEN_TWO_OPERATOR} |
                     {TWO_TOKEN_TYPE_OPERATOR}

ONE_TOKEN_AT_OPERATOR = "@"
ONE_TOKEN_CAPTURE_OPERATOR = "&"
ONE_TOKEN_DOT_OPERATOR = "."
/* Dual because they have a dual role as unary operators and binary operators
   @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L31-L32 */
ONE_TOKEN_DUAL_OPERATOR = "+" |
                          "-"
ONE_TOKEN_IN_OPERATOR = "in"
ONE_TOKEN_MATCH_OPERATOR = "="
ONE_TOKEN_MULTIPLICATION_OPERATOR = "*" |
                                    "/"
ONE_TOKEN_PIPE_OPERATOR = "|"
ONE_TOKEN_RELATIONAL_OPERATOR = "<" |
                                ">"
ONE_TOKEN_STRUCT_OPERATOR = "%"
ONE_TOKEN_UNARY_OPERATOR = "!" |
                           "^"
ONE_TOKEN_REFERENCABLE_OPERATOR = {ONE_TOKEN_AT_OPERATOR} |
                                  {ONE_TOKEN_CAPTURE_OPERATOR} |
                                  {ONE_TOKEN_DUAL_OPERATOR} |
                                  {ONE_TOKEN_IN_OPERATOR} |
                                  {ONE_TOKEN_MATCH_OPERATOR} |
                                  {ONE_TOKEN_MULTIPLICATION_OPERATOR} |
                                  {ONE_TOKEN_PIPE_OPERATOR} |
                                  {ONE_TOKEN_RELATIONAL_OPERATOR} |
                                  {ONE_TOKEN_UNARY_OPERATOR}
ONE_TOKEN_UNREFERENCABLE_OPERATOR = {ONE_TOKEN_DOT_OPERATOR} |
                                    {ONE_TOKEN_STRUCT_OPERATOR}
ONE_TOKEN_OPERATOR = {ONE_TOKEN_REFERENCABLE_OPERATOR} |
                     {ONE_TOKEN_UNREFERENCABLE_OPERATOR}

AND_OPERATOR = {THREE_TOKEN_AND_OPERATOR} |
               {TWO_TOKEN_AND_OPERATOR}
ARROW_OPERATOR = {THREE_TOKEN_ARROW_OPERATOR} |
                 {TWO_TOKEN_ARROW_OPERATOR}
ASSOCIATION_OPERATOR = {TWO_TOKEN_ASSOCIATION_OPERATOR}
AT_OPERATOR = {ONE_TOKEN_AT_OPERATOR}
BIT_STRING_OPERATOR = {FOUR_TOKEN_BITSTRING_OPERATOR}
CAPTURE_OPERATOR = {ONE_TOKEN_CAPTURE_OPERATOR}
DOT_OPERATOR = {ONE_TOKEN_DOT_OPERATOR}
// Dual because they have a dual role as unary operators and binary operators
DUAL_OPERATOR = {ONE_TOKEN_DUAL_OPERATOR}
COMPARISON_OPERATOR = {THREE_TOKEN_COMPARISON_OPERATOR} |
                      {TWO_TOKEN_COMPARISON_OPERATOR}
IN_MATCH_OPERATOR = {TWO_TOKEN_IN_MATCH_OPERATOR}
IN_OPERATOR = {ONE_TOKEN_IN_OPERATOR}
MAP_OPERATOR = {THREE_TOKEN_MAP_OPERATOR}
MATCH_OPERATOR = {ONE_TOKEN_MATCH_OPERATOR}
MULTIPLICATION_OPERATOR = {ONE_TOKEN_MULTIPLICATION_OPERATOR}
NOT_OPERATOR = {THREE_TOKEN_NOT_OPERATOR}
OR_OPERATOR = {THREE_TOKEN_OR_OPERATOR} |
              {TWO_TOKEN_OR_OPERATOR}
PIPE_OPERATOR = {ONE_TOKEN_PIPE_OPERATOR}
RELATIONAL_OPERATOR = {TWO_TOKEN_RELATIONAL_OPERATOR} |
                      {ONE_TOKEN_RELATIONAL_OPERATOR}
STAB_OPERATOR = {TWO_TOKEN_STAB_OPERATOR}
STRUCT_OPERATOR = {ONE_TOKEN_STRUCT_OPERATOR}
// https://github.com/elixir-lang/elixir/commit/3487d00ddb5e90c7cf0e65d03717903b9b27eafd
THREE_OPERATOR = {THREE_TOKEN_THREE_OPERATOR}
TUPLE_OPERATOR = {TWO_TOKEN_TUPLE_OPERATOR}
TWO_OPERATOR = {TWO_TOKEN_TWO_OPERATOR}
TYPE_OPERATOR = {TWO_TOKEN_TYPE_OPERATOR}
UNARY_OPERATOR = {THREE_TOKEN_UNARY_OPERATOR} |
                 {ONE_TOKEN_UNARY_OPERATOR}
WHEN_OPERATOR = {FOUR_TOKEN_WHEN_OPERATOR}

REFERENCABLE_OPERATOR = {FOUR_TOKEN_OPERATOR} |
                        {THREE_TOKEN_OPERATOR} |
                        {TWO_TOKEN_OPERATOR} |
                        {ONE_TOKEN_REFERENCABLE_OPERATOR}
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
ATOM = {ATOM_START} {ATOM_MIDDLE}* {ATOM_END}?
COLON = :

/*
 * Block Identifiers
 */

AFTER = "after"
CATCH = "catch"
ELSE = "else"
RESCUE = "rescue"

/*
 * Containers
 */

COMMA = ","

/*
 * Digits
 */

HEXADECIMAL_DIGIT = [A-Fa-f0-9]

/*
 * EOE (End of Expression)
 */

SEMICOLON = ";"

/*
 * EOL
 */

EOL = \n|\r\n

/*
 * Escape Sequences
 */

ESCAPE = "\\"

UNICODE_ESCAPE_CHARACTER = "u"
ESCAPED_EOL = {ESCAPE} {EOL}

/*
 * Char tokens
 */

CHAR_TOKENIZER = "?"

/*
 * White Space
 */

HORIZONTAL_SPACE = [ \t]
VERTICAL_SPACE = [\n\r]
SPACE = {HORIZONTAL_SPACE} | {VERTICAL_SPACE}
WHITE_SPACE=[\ \t\f]
// see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L609-L610
SPACE_SENSITIVE={DUAL_OPERATOR}[^(\[<{%+-/>:]

/*
 *  Comments
 */

COMMENT = "#" [^\r\n]*

/*
 *
 *   Whole Numbers
 *
 */

VALID_DECIMAL_DIGITS = [0-9]+
INVALID_DECIMAL_DIGITS = [A-Za-z]+
DECIMAL_SEPARATOR = "_"

/*
 * Non-Base-10
 */

BASE_WHOLE_NUMBER_PREFIX = "0"
BASE_WHOLE_NUMBER_BASE = [A-Za-z]

BINARY_WHOLE_NUMBER_BASE = "b"
OBSOLETE_BINARY_WHOLE_NUMBER_BASE = "B"
VALID_BINARY_DIGITS = [01]+
INVALID_BINARY_DIGITS = [A-Za-z2-9]+

HEXADECIMAL_WHOLE_NUMBER_BASE = "x"
OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE = "X"
VALID_HEXADECIMAL_DIGITS = {HEXADECIMAL_DIGIT}+
INVALID_HEXADECIMAL_DIGITS = [G-Zg-z]+

OCTAL_WHOLE_NUMBER_BASE = "o"
VALID_OCTAL_DIGITS = [0-7]+
INVALID_OCTAL_DIGITS = [A-Za-z8-9]+

INVALID_UNKNOWN_BASE_DIGITS = [A-Za-z0-9]+

/*
 * Identifiers
 */

IDENTIFIER_TOKEN_END = [?!]
IDENTIFIER_TOKEN_MIDDLE = [0-9a-zA-Z_]
IDENTIFIER_TOKEN_START = [a-z_]
IDENTIFIER_TOKEN_HEAD = {IDENTIFIER_TOKEN_START}
IDENTIFIER_TOKEN_TAIL = {IDENTIFIER_TOKEN_MIDDLE}* {IDENTIFIER_TOKEN_END}?
IDENTIFIER_TOKEN = ({IDENTIFIER_TOKEN_HEAD} {IDENTIFIER_TOKEN_TAIL}  | "...")

/*
 * Aliases
 */

ALIAS_HEAD = [A-Z]
ALIAS = {ALIAS_HEAD} {IDENTIFIER_TOKEN_TAIL}

/*
 * Bit Strings
 */

CLOSING_BIT = ">>"
OPENING_BIT = "<<"

/*
 * Parent
 */

INTERPOLATION_START = "#{"
INTERPOLATION_END = "}"

/*
 * Floats
 */

DECIMAL_MARK = "."
EXPONENT_MARK = [Ee]

/*
 * List
 */

CLOSING_BRACKET = "]"
OPENING_BRACKET = "["

/*
 * Parentheses
 */

CLOSING_PARENTHESIS = ")"
OPENING_PARENTHESIS = "("

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
 * Function References
 */

REFERENCE_OPERATOR = "/"
REFERENCE_INFIX_OPERATOR = ({WHITE_SPACE}|{EOL})*{REFERENCE_OPERATOR}

/*
 * Regular Keywords
 */

DO = "do"
END = "end"
FALSE = "false"
FN = "fn"
NIL = "nil"
TRUE = "true"

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

%state ATOM_START
%state BASE_WHOLE_NUMBER_BASE
%state BINARY_WHOLE_NUMBER
%state CALL_MAYBE
%state CALL_OR_KEYWORD_PAIR_MAYBE
%state CHAR_TOKENIZATION
%state DECIMAL_EXPONENT
%state DECIMAL_EXPONENT_SIGN
%state DECIMAL_FRACTION
%state DECIMAL_WHOLE_NUMBER
%state DOT_OPERATION
%state DUAL_OPERATION
%state ESCAPE_IN_LITERAL_GROUP
%state ESCAPE_SEQUENCE
%state EXTENDED_HEXADECIMAL_ESCAPE_SEQUENCE
%state GROUP
%state GROUP_HEREDOC_END
%state GROUP_HEREDOC_LINE_BODY
%state GROUP_HEREDOC_LINE_ESCAPED_EOL
%state GROUP_HEREDOC_LINE_START
%state GROUP_HEREDOC_START
%state HEXADECIMAL_ESCAPE_SEQUENCE
%state HEXADECIMAL_WHOLE_NUMBER
%state INTERPOLATION
%state KEYWORD_PAIR_MAYBE
%state NAMED_SIGIL
%state OCTAL_WHOLE_NUMBER
%state REFERENCE_OPERATION
%state SIGIL
%state SIGIL_MODIFIERS
%state UNICODE_ESCAPE_SEQUENCE
%state UNKNOWN_BASE_WHOLE_NUMBER

%%

/* <YYINITIAL> is first even though it isn't lexicographically first because it is the first state.
   Rules that aren't dependent on detecting the end of INTERPOLATION can be shared between <YYINITIAL> and
   <INTERPOLATION> */
<YYINITIAL, INTERPOLATION> {
  {AFTER}                                    { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.AFTER; }
  // Must be before any single operator's match
  {REFERENCABLE_OPERATOR} / {REFERENCE_INFIX_OPERATOR} { pushAndBegin(REFERENCE_OPERATION);
                                                         return ElixirTypes.IDENTIFIER_TOKEN; }
  {AND_OPERATOR}                             { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.AND_OPERATOR; }
  {ARROW_OPERATOR}                           { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.ARROW_OPERATOR; }
  {ASSOCIATION_OPERATOR}                     { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.ASSOCIATION_OPERATOR; }
  {ALIAS}                                    { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.ALIAS_TOKEN; }
  {AT_OPERATOR}                              { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.AT_OPERATOR; }
  {BASE_WHOLE_NUMBER_PREFIX} / {BASE_WHOLE_NUMBER_BASE} { pushAndBegin(BASE_WHOLE_NUMBER_BASE);
                                                          return ElixirTypes.BASE_WHOLE_NUMBER_PREFIX; }
  /* For bitString rule, OPENING_BIT will be lexed.  This is just for when the operator needs to be one token for
     keywords key */
  {BIT_STRING_OPERATOR} / {COLON}{SPACE}     { // Definitely a Keyword pair, but KEYWORD_PAIR_MAYBE state will still work.
                                               pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.BIT_STRING_OPERATOR; }
  {CATCH}                                    { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.CATCH; }
  {CAPTURE_OPERATOR}                         { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.CAPTURE_OPERATOR; }
  /* Must be after {BIT_STRING_OPERATOR} as {BIT_STRING_OPERATOR} combines {OPENING_BIT} {CLOSING_BIT} and must be
     before {COMPARISON_OPERATOR} as {COMPARISON_OPERATOR} includes ">" which would match the begining of {CLOSING_BIT}
   */
  {CLOSING_BIT}                              { return ElixirTypes.CLOSING_BIT; }
  {CLOSING_BRACKET}                          { return ElixirTypes.CLOSING_BRACKET; }
  {CLOSING_PARENTHESIS}                      { return ElixirTypes.CLOSING_PARENTHESIS; }
  {DO}                                       { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.DO; }
  {ELSE}                                     { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.ELSE; }
  {EOL}                                      { return ElixirTypes.EOL; }
  {END}                                      { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.END; }
  // see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L605-L613
  {ESCAPED_EOL}|{WHITE_SPACE}+ / {SPACE_SENSITIVE} { return ElixirTypes.SIGNIFICANT_WHITE_SPACE; }
  {ESCAPED_EOL}|{WHITE_SPACE}+                     { return TokenType.WHITE_SPACE; }
  {CHAR_TOKENIZER}                                      { pushAndBegin(CHAR_TOKENIZATION);
                                                          return ElixirTypes.CHAR_TOKENIZER; }
  /* So that that atom of comparison operator consumes all 3 ':' instead of {TYPE_OPERATOR} consuming '::'
     and ':' being leftover */
  {COLON} / {TYPE_OPERATOR}                  { pushAndBegin(ATOM_START);
                                               return ElixirTypes.COLON; }
  {COLON} / {SPACE}                          { return ElixirTypes.COLON; }
  // Must be after `{COLON} / {TYPE_OPERATOR}`, so that 3 ':' are consumed before 1.
  {TYPE_OPERATOR}                            { return ElixirTypes.TYPE_OPERATOR; }
  // Must be after {TYPE_OPERATOR}, so that 1 ':' is consumed after 2
  {COLON}                                    { pushAndBegin(ATOM_START);
                                               return ElixirTypes.COLON; }
  {COMMA}                                    { return ElixirTypes.COMMA; }
  {COMMENT}                                  { return ElixirTypes.COMMENT; }
  /* Must be after {BIT_STRING_OPERATOR} as {BIT_STRING_OPERATOR} combines {OPENING_BIT} {CLOSING_BIT}; must be after
     {ARROW_OPERATOR} because {ARROW_OPERATOR} includes "<<<", which is a longer match than {OPENING_BIT}'s "<<"; and
     must be before {COMPARISON_OPERATOR} as {COMPARISON_OPERATOR} includes "<" which would match the begining of
     {OPENING_BIT}. */
  {OPENING_BIT}                              { return ElixirTypes.OPENING_BIT; }
  {COMPARISON_OPERATOR}                      { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.COMPARISON_OPERATOR; }
  // DOT_OPERATOR is not a valid keywordKey, so no need to go to KEYWORD_PAIR_MAYBE
  {DOT_OPERATOR}                             { pushAndBegin(DOT_OPERATION);
                                               return ElixirTypes.DOT_OPERATOR; }
  {DUAL_OPERATOR}                            { pushAndBegin(DUAL_OPERATION);
                                               return ElixirTypes.DUAL_OPERATOR; }
  {FALSE}                                    { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.FALSE; }
  {FN}                                       { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.FN; }
  {OPENING_BRACKET}                          { return ElixirTypes.OPENING_BRACKET; }
  /* Must be before {OPENING_CURLY} so entire "{}" is matched instead of just "{"

     For tuple rule, OPENING_CURLY will be lexed.  This is just for when the operator needs to be one token for
     keywords key */
  {TUPLE_OPERATOR} / {COLON}{SPACE}          { // Definitely a Keyword pair, but KEYWORD_PAIR_MAYBE state will still work.
                                               pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.TUPLE_OPERATOR; }
  {OPENING_CURLY}                            { // use stack to match up nested OPENING_CURLY and CLOSING_CURLY
                                               pushAndBegin(YYINITIAL);
                                               return ElixirTypes.OPENING_CURLY; }
  {OPENING_PARENTHESIS}                      { return ElixirTypes.OPENING_PARENTHESIS; }
  // Must be before {IDENTIFIER_TOKEN} as "in" would be parsed as an identifier since it's a lowercase alphanumeric.
  {IN_OPERATOR}                              { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.IN_OPERATOR; }
  // Must be before {IDENTIFIER_TOKEN} as "nil" would be parsed as an identifier since it's a lowercase alphanumeric.
  {NIL}                                      { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.NIL; }
  // Must be before {IDENTIFIER_TOKEN} as "not" would be parsed as an identifier since it's a lowercase alphanumeric.
  {NOT_OPERATOR}                             { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.NOT_OPERATOR; }
  // Must be before {IDENTIFIER_TOKEN} as "or" would be parsed as an identifier since it's a lowercase alphanumeric.
  {OR_OPERATOR}                              { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.OR_OPERATOR; }
  // Must be before {IDENTIFIER_TOKEN} as "rescue" would be parsed as an identifier since it's a lowercase alphanumeric.
  {RESCUE}                                   { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.RESCUE; }
  // Must be before {IDENTIFIER_TOKEN} as "true" would be parsed as an identifier since it's a lowercase alphanumeric.
  {TRUE}                                     { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.TRUE; }
  // Must be before {UNARY_OPERATOR} as "^^^" is longer than "^" in {UNARY_OPERATOR}
  {THREE_OPERATOR}                           { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.THREE_OPERATOR; }
  // Must be before {IDENTIFIER_TOKEN} as "when" would be parsed as an identifier since it's a lowercase alphanumeric.
  {WHEN_OPERATOR}                            { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.WHEN_OPERATOR; }
  {IDENTIFIER_TOKEN}                               { pushAndBegin(CALL_OR_KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.IDENTIFIER_TOKEN; }
  {IN_MATCH_OPERATOR}                        { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.IN_MATCH_OPERATOR; }
  /* For map rule, STRUCT_OPERATOR EOL* OPENING_CURLY will be lexed.  This is just for when the operator needs to be one
     token for keywords key */
  {MAP_OPERATOR} / {COLON}{SPACE}            { // Definitely a Keyword pair, but KEYWORD_PAIR_MAYBE state will still work.
                                               pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.MAP_OPERATOR; }
  {MATCH_OPERATOR}                           { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.MATCH_OPERATOR; }
  {MULTIPLICATION_OPERATOR}                  { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.MULTIPLICATION_OPERATOR; }
  {PIPE_OPERATOR}                            { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.PIPE_OPERATOR; }
  {RELATIONAL_OPERATOR}                      { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.RELATIONAL_OPERATOR; }
  {SEMICOLON}                                { return ElixirTypes.SEMICOLON; }
  {STAB_OPERATOR}                            { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.STAB_OPERATOR; }
  {STRUCT_OPERATOR}                          { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.STRUCT_OPERATOR; }
  {TILDE}                                    { pushAndBegin(SIGIL);
                                               return ElixirTypes.TILDE; }
  {TWO_OPERATOR}                             { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.TWO_OPERATOR; }
  {UNARY_OPERATOR}                           { pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               return ElixirTypes.UNARY_OPERATOR; }
  {VALID_DECIMAL_DIGITS}                     { pushAndBegin(DECIMAL_WHOLE_NUMBER);
                                               return ElixirTypes.VALID_DECIMAL_DIGITS; }
  {QUOTE_HEREDOC_PROMOTER}                   { startQuote(yytext());
                                               return promoterType(); }
  /* MUST be after {QUOTE_HEREDOC_PROMOTER} for <BODY, INTERPOLATION> as {QUOTE_HEREDOC_PROMOTER} is prefixed by
     {QUOTE_PROMOTER} */
  {QUOTE_PROMOTER}                           { /* return to KEYWORD_PAIR_MAYBE so that COLON after quote can be parsed
                                                  as KEYWORD_PAIR_COLON to differentiate between valid `<quote><colon>`
                                                  and invalid `<quote><space><colon>`. */
                                               pushAndBegin(KEYWORD_PAIR_MAYBE);
                                               startQuote(yytext());
                                               return promoterType(); }
}

/*
 *  Lexical rules - Ordered alphabetically by state name except when the order of the internal rules need to be
 *  maintained to ensure precedence.
 */

/// Must be after {QUOTE_PROMOTER} for <ATOM_START> so that
<ATOM_START> {
  {ATOM}           { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                     yybegin(stackFrame.getLastLexicalState());
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
  {EOL}|.          { return TokenType.BAD_CHARACTER; }
}

<BASE_WHOLE_NUMBER_BASE> {
  {BINARY_WHOLE_NUMBER_BASE}               { yybegin(BINARY_WHOLE_NUMBER);
                                             return ElixirTypes.BINARY_WHOLE_NUMBER_BASE; }
  {HEXADECIMAL_WHOLE_NUMBER_BASE}          { yybegin(HEXADECIMAL_WHOLE_NUMBER);
                                             return ElixirTypes.HEXADECIMAL_WHOLE_NUMBER_BASE; }
  {OBSOLETE_BINARY_WHOLE_NUMBER_BASE}      { yybegin(BINARY_WHOLE_NUMBER);
                                             return ElixirTypes.OBSOLETE_BINARY_WHOLE_NUMBER_BASE; }
  {OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE} { yybegin(HEXADECIMAL_WHOLE_NUMBER);
                                             return ElixirTypes.OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE; }
  {OCTAL_WHOLE_NUMBER_BASE}                { yybegin(OCTAL_WHOLE_NUMBER);
                                             return ElixirTypes.OCTAL_WHOLE_NUMBER_BASE; }
  // Must be after any specific integer bases
  {BASE_WHOLE_NUMBER_BASE}                 { yybegin(UNKNOWN_BASE_WHOLE_NUMBER);
                                             return ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE; }
}

<BINARY_WHOLE_NUMBER> {
  {INVALID_BINARY_DIGITS} { return ElixirTypes.INVALID_BINARY_DIGITS; }
  {VALID_BINARY_DIGITS}   { return ElixirTypes.VALID_BINARY_DIGITS; }
  {EOL}|.                 { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                            handleInState(stackFrame.getLastLexicalState()); }
}

<CALL_MAYBE, CALL_OR_KEYWORD_PAIR_MAYBE> {
  {OPENING_BRACKET}|{OPENING_PARENTHESIS} { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                            handleInState(stackFrame.getLastLexicalState());
                                            // zero-width token
                                            return ElixirTypes.CALL; }
}

<CALL_MAYBE> {
  {EOL}|.                                 { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                            handleInState(stackFrame.getLastLexicalState()); }
}

<CALL_OR_KEYWORD_PAIR_MAYBE> {
  {EOL}|.                                 { handleInState(KEYWORD_PAIR_MAYBE); }
}

<CHAR_TOKENIZATION> {
  {ESCAPE} { yybegin(ESCAPE_SEQUENCE);
             return ElixirTypes.ESCAPE; }
  {EOL}|.  { org.elixir_lang.lexer.StackFrame stackFrame = pop();
             yybegin(stackFrame.getLastLexicalState());
             return ElixirTypes.CHAR_LIST_FRAGMENT; }
}

<DECIMAL_EXPONENT_SIGN> {
  {DUAL_OPERATOR} { yybegin(DECIMAL_EXPONENT);
                    return ElixirTypes.DUAL_OPERATOR; }
  {EOL}|.         { handleInState(DECIMAL_EXPONENT); }
}

<DECIMAL_FRACTION> {
  {EXPONENT_MARK} { yybegin(DECIMAL_EXPONENT_SIGN);
                    return ElixirTypes.EXPONENT_MARK; }
}

<DECIMAL_WHOLE_NUMBER> {
  /*
   Error handling with {INVALID_DECIMAL_DIGITS} and {DECIMAL_SEPARATOR} can only occur after at least one valid decimal
   digit after the decimal mark because {INVALID_DECIMAL_DIGITS} and {DECIMAL_SEPARATOR} will be parsed as an
   identifier immediately after `.`

   ```
   iex> Code.string_to_quoted("1._")
   {:ok, {{:., [line: 1], [1, :_]}, [line: 1], []}}
   iex> Code.string_to_quoted("1.a")
   {:ok, {{:., [line: 1], [1, :a]}, [line: 1], []}}
   ```
  */
  {DECIMAL_MARK} / {VALID_DECIMAL_DIGITS} { yybegin(DECIMAL_FRACTION);
                                            return ElixirTypes.DECIMAL_MARK; }
}

<DECIMAL_EXPONENT,
 DECIMAL_FRACTION,
 DECIMAL_WHOLE_NUMBER> {
  {DECIMAL_SEPARATOR}      { return ElixirTypes.DECIMAL_SEPARATOR; }
  {INVALID_DECIMAL_DIGITS} { return ElixirTypes.INVALID_DECIMAL_DIGITS; }
  {VALID_DECIMAL_DIGITS}   { return ElixirTypes.VALID_DECIMAL_DIGITS; }
  {EOL}|.                  { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                             handleInState(stackFrame.getLastLexicalState()); }
}

<DOT_OPERATION> {
  {AFTER}                                           { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.AFTER; }
  {AND_OPERATOR}                                    { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.AND_OPERATOR; }
  {ARROW_OPERATOR}                                  { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.ARROW_OPERATOR; }
  {AT_OPERATOR}                                     { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.AT_OPERATOR; }
  {CATCH}                                           { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.CATCH; }
  {CAPTURE_OPERATOR}                                { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.CAPTURE_OPERATOR; }
  {COMPARISON_OPERATOR}                             { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.COMPARISON_OPERATOR; }
  {DO}                                              { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.DO; }
  {DUAL_OPERATOR}                                   { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.DUAL_OPERATOR; }
  {END}                                             { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.END; }
  {ELSE}                                            { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.ELSE; }
  {FALSE}                                           { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.FALSE; }
  {IN_MATCH_OPERATOR}                               { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.IN_MATCH_OPERATOR; }
  {IN_OPERATOR}                                     { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.IN_OPERATOR; }
  {MATCH_OPERATOR}                                  { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.MATCH_OPERATOR; }
  {MULTIPLICATION_OPERATOR}                         { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.MULTIPLICATION_OPERATOR; }
  {NIL}                                             { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.NIL; }
  {OR_OPERATOR}                                     { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.OR_OPERATOR; }
  {PIPE_OPERATOR}                                   { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.PIPE_OPERATOR; }
  {RELATIONAL_OPERATOR}                             { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.RELATIONAL_OPERATOR; }
  {RESCUE}                                          { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.RESCUE; }
  {STAB_OPERATOR}                                   { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.STAB_OPERATOR; }
  {STRUCT_OPERATOR}                                 { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.STRUCT_OPERATOR; }
  {THREE_OPERATOR}                                  { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.THREE_OPERATOR; }
  {TRUE}                                            { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.TRUE; }
  {TWO_OPERATOR}                                    { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.TWO_OPERATOR; }
  {UNARY_OPERATOR}                                  { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.UNARY_OPERATOR; }
  {WHEN_OPERATOR}                                   { yybegin(CALL_MAYBE);
                                                      return ElixirTypes.WHEN_OPERATOR; }

  /* Must be after {AFTER}, {CATCH}, {DO}, {END}, {ELSE}, {IN_OPERATOR}, {OR_OPERATOR} (for 'or'), and {WHEN_OPERATOR}
     as all those keywords would match {IDENTIFIER_TOKEN} */
  {IDENTIFIER_TOKEN}                                { yybegin(CALL_OR_KEYWORD_PAIR_MAYBE);
                                                      return ElixirTypes.IDENTIFIER_TOKEN; }

  /*
   * Emulates strip_space in elixir_tokenizer.erl
   */

  // see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L605-L613
  {ESCAPED_EOL}|{WHITE_SPACE}+ / {SPACE_SENSITIVE}  { return ElixirTypes.SIGNIFICANT_WHITE_SPACE; }
  {ESCAPED_EOL}|{WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
  {EOL}                                             { return ElixirTypes.EOL; }

  /* Be better than strip_space and handle_dot and ignore comments so that IDENTIFIER_TOKEN and operators are parsed the
     same after dots.

     @see https://groups.google.com/forum/#!topic/elixir-lang-core/nnI4oUB-63U
   */
  {COMMENT}                                         { return ElixirTypes.COMMENT; }

  /* Must be before {QUOTE_PROMOTER} as {QUOTE_PROMOTER} is a prefix of {QUOTE_HEREDOC_PROMOTER} */
  {QUOTE_HEREDOC_PROMOTER}                          { /* Does NOT return to CALL_MAYBE because heredocs aren't valid
                                                         relative identifiers.  This clauses is only here to prevent a
                                                         prefix match on {QUOTE_PROMOTER}. */
                                                      org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                                      handleInState(stackFrame.getLastLexicalState()); }
  {QUOTE_PROMOTER}                                  { /* return to CALL_MAYBE so that OPENING_BRACKET or
                                                         OPENING_PARENTHESES after quote can be parsed
                                                         with CALL so parser doesn't think call is no parentheses with
                                                         parenthetical or list argument. */
                                                      yybegin(CALL_MAYBE);
                                                      startQuote(yytext());
                                                      return promoterType(); }

  .                                                 { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                                      handleInState(stackFrame.getLastLexicalState()); }
}

<DUAL_OPERATION> {
  {WHITE_SPACE}+ { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                   yybegin(stackFrame.getLastLexicalState());
                   return ElixirTypes.SIGNIFICANT_WHITE_SPACE; }
  {EOL}|.        { handleInState(KEYWORD_PAIR_MAYBE); }
}

<ESCAPE_IN_LITERAL_GROUP> {
  {EOL} {
          yybegin(GROUP);
          return ElixirTypes.EOL;
        }
  .     {
          yybegin(GROUP);
          return fragmentType();
        }
}

<ESCAPE_SEQUENCE> {
  {EOL}                           { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                    yybegin(stackFrame.getLastLexicalState());
                                    return ElixirTypes.EOL; }
  {HEXADECIMAL_WHOLE_NUMBER_BASE} { yybegin(HEXADECIMAL_ESCAPE_SEQUENCE);
                                    return ElixirTypes.HEXADECIMAL_WHOLE_NUMBER_BASE; }
  {UNICODE_ESCAPE_CHARACTER}      { yybegin(UNICODE_ESCAPE_SEQUENCE);
                                    return ElixirTypes.UNICODE_ESCAPE_CHARACTER; }
  .                               { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                    yybegin(stackFrame.getLastLexicalState());
                                    return ElixirTypes.ESCAPED_CHARACTER_TOKEN; }
}

<EXTENDED_HEXADECIMAL_ESCAPE_SEQUENCE> {
  {CLOSING_CURLY}          { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                             yybegin(stackFrame.getLastLexicalState());
                             return ElixirTypes.CLOSING_CURLY; }
  {HEXADECIMAL_DIGIT}{1,6} { return ElixirTypes.VALID_HEXADECIMAL_DIGITS; }
  .                        { return TokenType.BAD_CHARACTER; }
}

<GROUP,
 GROUP_HEREDOC_LINE_BODY> {
  {INTERPOLATION_START} {
                          if (isInterpolating()) {
                           pushAndBegin(INTERPOLATION);
                           return ElixirTypes.INTERPOLATION_START;
                          } else {
                           return fragmentType();
                          }
                        }
}

// Rules in GROUP, but not GROUP_HEREDOC_LINE_BODY
<GROUP> {
  {ESCAPE}{GROUP_TERMINATOR} {
                               CharSequence groupTerminator = yytext().subSequence(1, yytext().length());

                               // manual lookahread pushes terminator back
                               yypushback(groupTerminator.length());

                               /* even literal groups have escape sequences because escaping the terminator is still
                                  allowed */
                               if (isTerminator(groupTerminator) || isInterpolating()) {
                                 // matches interpolating behavior from `{ESCAPE}` rule below
                                 pushAndBegin(ESCAPE_SEQUENCE);
                                 return ElixirTypes.ESCAPE;
                               } else {
                                 // matches non-interpolating behavior from `{ESCAPE}` rule below
                                 yybegin(ESCAPE_IN_LITERAL_GROUP);
                                 return fragmentType();
                               }
                             }
  {ESCAPE} / {EOL}           {
                               if (isInterpolating()) {
                                 pushAndBegin(ESCAPE_SEQUENCE);
                               } else {
                                 yybegin(ESCAPE_IN_LITERAL_GROUP);
                               }

                               return ElixirTypes.ESCAPE;
                             }
  {ESCAPE}                   {
                               if (isInterpolating()) {
                                 pushAndBegin(ESCAPE_SEQUENCE);
                                 return ElixirTypes.ESCAPE;
                               } else {
                                 yybegin(ESCAPE_IN_LITERAL_GROUP);
                                 return fragmentType();
                               }
                             }
  {GROUP_TERMINATOR}         {
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
  {EOL}|.                    { return fragmentType(); }
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
  // See https://github.com/elixir-lang/elixir/pull/4341
  {ESCAPE} / {EOL} {
                     if (isInterpolating()) {
                       pushAndBegin(ESCAPE_SEQUENCE);
                       return ElixirTypes.ESCAPE;
                     } else {
                       yybegin(GROUP_HEREDOC_LINE_ESCAPED_EOL);
                       return ElixirTypes.ESCAPE;
                     }
                   }
  {ESCAPE}         {
                     if (isInterpolating()) {
                       pushAndBegin(ESCAPE_SEQUENCE);
                       return ElixirTypes.ESCAPE;
                     } else {
                       return fragmentType();
                     }
                   }
  {EOL}            {
                     yybegin(GROUP_HEREDOC_LINE_START);
                     return ElixirTypes.EOL;
                   }
  .                { return fragmentType(); }
}

// See https://github.com/elixir-lang/elixir/pull/4341
<GROUP_HEREDOC_LINE_ESCAPED_EOL> {
  {EOL} {
          /* The EOL after the escape is also needed to end the Heredoc line.  It functions as both, so arbitarily I'm
             choosing the escaped version to be a zero-width token. */
          yypushback(yylength());
          yybegin(GROUP_HEREDOC_LINE_BODY);
          return ElixirTypes.EOL;
        }
}

<GROUP_HEREDOC_LINE_START> {
  {WHITE_SPACE}+{GROUP_HEREDOC_TERMINATOR}    {
                                                String groupHeredocTerminator = yytext().toString().trim();

                                                // manual lookahead pushes terminator back
                                                yypushback(3);

                                                if (isTerminator(groupHeredocTerminator)) {
                                                  yybegin(GROUP_HEREDOC_END);
                                                  return ElixirTypes.HEREDOC_PREFIX_WHITE_SPACE;
                                                } else {
                                                  yybegin(GROUP_HEREDOC_LINE_BODY);
                                                  return ElixirTypes.HEREDOC_LINE_WHITE_SPACE_TOKEN;
                                                }
                                              }
  {WHITE_SPACE}+                              {
                                                yybegin(GROUP_HEREDOC_LINE_BODY);
                                                return ElixirTypes.HEREDOC_LINE_WHITE_SPACE_TOKEN;
                                              }
  {GROUP_HEREDOC_TERMINATOR}                  { handleInState(GROUP_HEREDOC_END); }
  {EOL}|.                                     { handleInState(GROUP_HEREDOC_LINE_BODY); }
}

<GROUP_HEREDOC_START> {
  // parse immediate terminator as a terminator and let parser handle errors for missing EOL
  {GROUP_HEREDOC_TERMINATOR} {
                               // Similar to GROUP_HEREDOC_END's GROUP_HEREDOC_TERMINATOR rule, but...
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
                                 /* ...returns BAD_CHARACTER instead of going to GROUP_HEREDOC_LINE_BODY when the wrong
                                    type of terminator */
                                 return TokenType.BAD_CHARACTER;
                               }
                             }
  {EOL}                      { yybegin(GROUP_HEREDOC_LINE_START);
                               return ElixirTypes.EOL; }
}

<HEXADECIMAL_ESCAPE_SEQUENCE> {
  {OPENING_CURLY}          { yybegin(EXTENDED_HEXADECIMAL_ESCAPE_SEQUENCE);
                             return ElixirTypes.OPENING_CURLY; }
  {HEXADECIMAL_DIGIT}{1,2} { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                             yybegin(stackFrame.getLastLexicalState());
                             return ElixirTypes.VALID_HEXADECIMAL_DIGITS; }
  {EOL}|.                  { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                             handleInState(stackFrame.getLastLexicalState()); }
}

<HEXADECIMAL_WHOLE_NUMBER> {
  {INVALID_HEXADECIMAL_DIGITS} { return ElixirTypes.INVALID_HEXADECIMAL_DIGITS; }
  {VALID_HEXADECIMAL_DIGITS}   { return ElixirTypes.VALID_HEXADECIMAL_DIGITS; }
  {EOL}|.                      { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                 handleInState(stackFrame.getLastLexicalState()); }
}

/* Only rules for <INTERPOLATON>, but not <YYINITIAL> go here.
   @note must be after <YYINITIAL, INTERPOLATION> so that BAD_CHARACTER doesn't match a single ' ' instead of
     {WHITE_SPACE}+. */
<INTERPOLATION> {
  {INTERPOLATION_END}         { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                yybegin(stackFrame.getLastLexicalState());
                                return ElixirTypes.INTERPOLATION_END; }
}

<KEYWORD_PAIR_MAYBE> {
  {COLON} / {SPACE}         { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                              yybegin(stackFrame.getLastLexicalState());
                              return ElixirTypes.KEYWORD_PAIR_COLON; }
  {EOL}|.                   { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                              handleInState(stackFrame.getLastLexicalState()); }
}

<NAMED_SIGIL> {
  {SIGIL_HEREDOC_PROMOTER} { setPromoter(yytext());
                             yybegin(GROUP_HEREDOC_START);
                             return promoterType(); }
  {SIGIL_PROMOTER}         { setPromoter(yytext());
                             yybegin(GROUP);
                             return promoterType(); }
  {EOL}                    { return TokenType.BAD_CHARACTER; }
}

<OCTAL_WHOLE_NUMBER> {
  {INVALID_OCTAL_DIGITS} { return ElixirTypes.INVALID_OCTAL_DIGITS; }
  {VALID_OCTAL_DIGITS}   { return ElixirTypes.VALID_OCTAL_DIGITS; }
  {EOL}|.                { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                           handleInState(stackFrame.getLastLexicalState()); }
}

<REFERENCE_OPERATION> {
  {ESCAPED_EOL}|{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }
  {EOL}                        { return ElixirTypes.EOL; }
  {REFERENCE_OPERATOR}         { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                 yybegin(stackFrame.getLastLexicalState());
                                 return ElixirTypes.MULTIPLICATION_OPERATOR; }
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

<UNICODE_ESCAPE_SEQUENCE> {
  {OPENING_CURLY}          { yybegin(EXTENDED_HEXADECIMAL_ESCAPE_SEQUENCE);
                             return ElixirTypes.OPENING_CURLY; }
  {HEXADECIMAL_DIGIT}{1,4} { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                             yybegin(stackFrame.getLastLexicalState());
                             return ElixirTypes.VALID_HEXADECIMAL_DIGITS; }
  {EOL}|.                  { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                             handleInState(stackFrame.getLastLexicalState()); }
}

<UNKNOWN_BASE_WHOLE_NUMBER> {
  {INVALID_UNKNOWN_BASE_DIGITS} { return ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS; }
  {EOL}|.                       { org.elixir_lang.lexer.StackFrame stackFrame = pop();
                                  handleInState(stackFrame.getLastLexicalState()); }
}

/* Only rules for <YYINITIAL>, but not <INTERPOLATION> go here. */
<YYINITIAL> {
  {CLOSING_CURLY} { // protect from too many "}"
                    if (!stack.empty()) {
                      org.elixir_lang.lexer.StackFrame stackFrame = pop();
                      yybegin(stackFrame.getLastLexicalState());
                    }

                    return ElixirTypes.CLOSING_CURLY; }
}

// MUST go last so that . mapping to BAD_CHARACTER is the rule of last resort for the listed states
<ATOM_START, GROUP_HEREDOC_START, INTERPOLATION, NAMED_SIGIL, SIGIL, YYINITIAL> {
  . { return TokenType.BAD_CHARACTER; }
}
