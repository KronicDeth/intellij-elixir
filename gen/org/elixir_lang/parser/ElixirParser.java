// This is a generated file. Not intended for manual editing.
package org.elixir_lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.elixir_lang.psi.ElixirTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ElixirParser implements PsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ACCESS_EXPRESSION) {
      r = accessExpression(b, 0);
    }
    else if (t == ATOM) {
      r = atom(b, 0);
    }
    else if (t == BINARY_NUMBER) {
      r = binaryNumber(b, 0);
    }
    else if (t == CHAR_LIST) {
      r = charList(b, 0);
    }
    else if (t == CHAR_LIST_HEREDOC) {
      r = charListHeredoc(b, 0);
    }
    else if (t == EMPTY_PARENTHESES) {
      r = emptyParentheses(b, 0);
    }
    else if (t == END_OF_EXPRESSION) {
      r = endOfExpression(b, 0);
    }
    else if (t == EXPRESSION) {
      r = expression(b, 0);
    }
    else if (t == HEXADECIMAL_NUMBER) {
      r = hexadecimalNumber(b, 0);
    }
    else if (t == IDENTIFIER_EXPRESSION) {
      r = identifierExpression(b, 0);
    }
    else if (t == INTERPOLATION) {
      r = interpolation(b, 0);
    }
    else if (t == KEYWORD_KEY) {
      r = keywordKey(b, 0);
    }
    else if (t == KEYWORD_PAIR) {
      r = keywordPair(b, 0);
    }
    else if (t == KEYWORD_VALUE) {
      r = keywordValue(b, 0);
    }
    else if (t == LIST) {
      r = list(b, 0);
    }
    else if (t == MATCHED_EXPRESSION) {
      r = matchedExpression(b, 0, -1);
    }
    else if (t == MATCHED_EXPRESSION_ADDITION_OPERATION) {
      r = matchedExpression(b, 0, 12);
    }
    else if (t == MATCHED_EXPRESSION_AND_OPERATION) {
      r = matchedExpression(b, 0, 6);
    }
    else if (t == MATCHED_EXPRESSION_ARROW_OPERATION) {
      r = matchedExpression(b, 0, 9);
    }
    else if (t == MATCHED_EXPRESSION_AT_OPERATION) {
      r = matchedExpressionAtOperation(b, 0);
    }
    else if (t == MATCHED_EXPRESSION_CAPTURE_OPERATION) {
      r = matchedExpressionCaptureOperation(b, 0);
    }
    else if (t == MATCHED_EXPRESSION_COMPARISON_OPERATION) {
      r = matchedExpression(b, 0, 7);
    }
    else if (t == MATCHED_EXPRESSION_DOT_OPERATION) {
      r = matchedExpression(b, 0, 16);
    }
    else if (t == MATCHED_EXPRESSION_HAT_OPERATION) {
      r = matchedExpression(b, 0, 14);
    }
    else if (t == MATCHED_EXPRESSION_IN_MATCH_OPERATION) {
      r = matchedExpression(b, 0, 0);
    }
    else if (t == MATCHED_EXPRESSION_IN_OPERATION) {
      r = matchedExpression(b, 0, 10);
    }
    else if (t == MATCHED_EXPRESSION_MATCH_OPERATION) {
      r = matchedExpression(b, 0, 4);
    }
    else if (t == MATCHED_EXPRESSION_MULTIPLICATION_OPERATION) {
      r = matchedExpression(b, 0, 13);
    }
    else if (t == MATCHED_EXPRESSION_OR_OPERATION) {
      r = matchedExpression(b, 0, 5);
    }
    else if (t == MATCHED_EXPRESSION_PIPE_OPERATION) {
      r = matchedExpression(b, 0, 3);
    }
    else if (t == MATCHED_EXPRESSION_RELATIONAL_OPERATION) {
      r = matchedExpression(b, 0, 8);
    }
    else if (t == MATCHED_EXPRESSION_TWO_OPERATION) {
      r = matchedExpression(b, 0, 11);
    }
    else if (t == MATCHED_EXPRESSION_TYPE_OPERATION) {
      r = matchedExpression(b, 0, 2);
    }
    else if (t == MATCHED_EXPRESSION_UNARY_OPERATION) {
      r = matchedExpressionUnaryOperation(b, 0);
    }
    else if (t == MATCHED_EXPRESSION_WHEN_OPERATION) {
      r = matchedExpression(b, 0, 1);
    }
    else if (t == MAX_EXPRESSION) {
      r = maxExpression(b, 0);
    }
    else if (t == NUMBER_AT_OPERATION) {
      r = numberAtOperation(b, 0);
    }
    else if (t == NUMBER_CAPTURE_OPERATION) {
      r = numberCaptureOperation(b, 0);
    }
    else if (t == NUMBER_UNARY_OPERATION) {
      r = numberUnaryOperation(b, 0);
    }
    else if (t == OCTAL_NUMBER) {
      r = octalNumber(b, 0);
    }
    else if (t == SIGIL) {
      r = sigil(b, 0);
    }
    else if (t == STRING) {
      r = string(b, 0);
    }
    else if (t == STRING_HEREDOC) {
      r = stringHeredoc(b, 0);
    }
    else if (t == UNKNOWN_BASE_NUMBER) {
      r = unknownBaseNumber(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return elixirFile(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ATOM, MAX_EXPRESSION),
    create_token_set_(ACCESS_EXPRESSION, ATOM, BINARY_NUMBER, CHAR_LIST,
      CHAR_LIST_HEREDOC, HEXADECIMAL_NUMBER, LIST, MAX_EXPRESSION,
      NUMBER_AT_OPERATION, NUMBER_CAPTURE_OPERATION, NUMBER_UNARY_OPERATION, OCTAL_NUMBER,
      SIGIL, STRING, STRING_HEREDOC, UNKNOWN_BASE_NUMBER),
    create_token_set_(ACCESS_EXPRESSION, ATOM, BINARY_NUMBER, CHAR_LIST,
      CHAR_LIST_HEREDOC, HEXADECIMAL_NUMBER, IDENTIFIER_EXPRESSION, LIST,
      MATCHED_EXPRESSION, MATCHED_EXPRESSION_ADDITION_OPERATION, MATCHED_EXPRESSION_AND_OPERATION, MATCHED_EXPRESSION_ARROW_OPERATION,
      MATCHED_EXPRESSION_AT_OPERATION, MATCHED_EXPRESSION_CAPTURE_OPERATION, MATCHED_EXPRESSION_COMPARISON_OPERATION, MATCHED_EXPRESSION_DOT_OPERATION,
      MATCHED_EXPRESSION_HAT_OPERATION, MATCHED_EXPRESSION_IN_MATCH_OPERATION, MATCHED_EXPRESSION_IN_OPERATION, MATCHED_EXPRESSION_MATCH_OPERATION,
      MATCHED_EXPRESSION_MULTIPLICATION_OPERATION, MATCHED_EXPRESSION_OR_OPERATION, MATCHED_EXPRESSION_PIPE_OPERATION, MATCHED_EXPRESSION_RELATIONAL_OPERATION,
      MATCHED_EXPRESSION_TWO_OPERATION, MATCHED_EXPRESSION_TYPE_OPERATION, MATCHED_EXPRESSION_UNARY_OPERATION, MATCHED_EXPRESSION_WHEN_OPERATION,
      MAX_EXPRESSION, NUMBER_AT_OPERATION, NUMBER_CAPTURE_OPERATION, NUMBER_UNARY_OPERATION,
      OCTAL_NUMBER, SIGIL, STRING, STRING_HEREDOC,
      UNKNOWN_BASE_NUMBER),
    create_token_set_(ACCESS_EXPRESSION, ATOM, BINARY_NUMBER, CHAR_LIST,
      CHAR_LIST_HEREDOC, EMPTY_PARENTHESES, EXPRESSION, HEXADECIMAL_NUMBER,
      IDENTIFIER_EXPRESSION, LIST, MATCHED_EXPRESSION, MATCHED_EXPRESSION_ADDITION_OPERATION,
      MATCHED_EXPRESSION_AND_OPERATION, MATCHED_EXPRESSION_ARROW_OPERATION, MATCHED_EXPRESSION_AT_OPERATION, MATCHED_EXPRESSION_CAPTURE_OPERATION,
      MATCHED_EXPRESSION_COMPARISON_OPERATION, MATCHED_EXPRESSION_DOT_OPERATION, MATCHED_EXPRESSION_HAT_OPERATION, MATCHED_EXPRESSION_IN_MATCH_OPERATION,
      MATCHED_EXPRESSION_IN_OPERATION, MATCHED_EXPRESSION_MATCH_OPERATION, MATCHED_EXPRESSION_MULTIPLICATION_OPERATION, MATCHED_EXPRESSION_OR_OPERATION,
      MATCHED_EXPRESSION_PIPE_OPERATION, MATCHED_EXPRESSION_RELATIONAL_OPERATION, MATCHED_EXPRESSION_TWO_OPERATION, MATCHED_EXPRESSION_TYPE_OPERATION,
      MATCHED_EXPRESSION_UNARY_OPERATION, MATCHED_EXPRESSION_WHEN_OPERATION, MAX_EXPRESSION, NUMBER_AT_OPERATION,
      NUMBER_CAPTURE_OPERATION, NUMBER_UNARY_OPERATION, OCTAL_NUMBER, SIGIL,
      STRING, STRING_HEREDOC, UNKNOWN_BASE_NUMBER),
  };

  /* ********************************************************** */
  // COLON (ATOM_FRAGMENT | quote)
  public static boolean atom(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atom")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && atom_1(b, l + 1);
    exit_section_(b, m, ATOM, r);
    return r;
  }

  // ATOM_FRAGMENT | quote
  private static boolean atom_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atom_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ATOM_FRAGMENT);
    if (!r) r = quote(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_INTEGER_PREFIX (BINARY_INTEGER_BASE | OBSOLETE_BINARY_INTEGER_BASE) (INVALID_BINARY_DIGITS | VALID_BINARY_DIGITS)+
  public static boolean binaryNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryNumber")) return false;
    if (!nextTokenIs(b, BASE_INTEGER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, BASE_INTEGER_PREFIX);
    r = r && binaryNumber_1(b, l + 1);
    p = r; // pin = 2
    r = r && binaryNumber_2(b, l + 1);
    exit_section_(b, l, m, BINARY_NUMBER, r, p, null);
    return r || p;
  }

  // BINARY_INTEGER_BASE | OBSOLETE_BINARY_INTEGER_BASE
  private static boolean binaryNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryNumber_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BINARY_INTEGER_BASE);
    if (!r) r = consumeToken(b, OBSOLETE_BINARY_INTEGER_BASE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (INVALID_BINARY_DIGITS | VALID_BINARY_DIGITS)+
  private static boolean binaryNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = binaryNumber_2_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!binaryNumber_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "binaryNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // INVALID_BINARY_DIGITS | VALID_BINARY_DIGITS
  private static boolean binaryNumber_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryNumber_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_BINARY_DIGITS);
    if (!r) r = consumeToken(b, VALID_BINARY_DIGITS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // string | stringHeredoc
  static boolean binaryString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryString")) return false;
    if (!nextTokenIs(b, "", STRING_HEREDOC_PROMOTER, STRING_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = string(b, l + 1);
    if (!r) r = stringHeredoc(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CHAR_LIST_PROMOTER
  //              interpolatedCharListBody
  //              CHAR_LIST_TERMINATOR
  public static boolean charList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charList")) return false;
    if (!nextTokenIs(b, CHAR_LIST_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CHAR_LIST_PROMOTER);
    r = r && interpolatedCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_TERMINATOR);
    exit_section_(b, m, CHAR_LIST, r);
    return r;
  }

  /* ********************************************************** */
  // CHAR_LIST_HEREDOC_PROMOTER EOL
  //                     interpolatedCharListBody
  //                     CHAR_LIST_HEREDOC_TERMINATOR
  public static boolean charListHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredoc")) return false;
    if (!nextTokenIs(b, CHAR_LIST_HEREDOC_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CHAR_LIST_HEREDOC_PROMOTER, EOL);
    r = r && interpolatedCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_HEREDOC_TERMINATOR);
    exit_section_(b, m, CHAR_LIST_HEREDOC, r);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses
  static boolean containerExpression(PsiBuilder b, int l) {
    return emptyParentheses(b, l + 1);
  }

  /* ********************************************************** */
  // endOfExpression* (expressionList endOfExpression*)?
  static boolean elixirFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = elixirFile_0(b, l + 1);
    r = r && elixirFile_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // endOfExpression*
  private static boolean elixirFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!endOfExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "elixirFile_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (expressionList endOfExpression*)?
  private static boolean elixirFile_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile_1")) return false;
    elixirFile_1_0(b, l + 1);
    return true;
  }

  // expressionList endOfExpression*
  private static boolean elixirFile_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expressionList(b, l + 1);
    r = r && elixirFile_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // endOfExpression*
  private static boolean elixirFile_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!endOfExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "elixirFile_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // OPENING_PARENTHESIS EOL* CLOSING_PARENTHESIS
  public static boolean emptyParentheses(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyParentheses")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_PARENTHESIS);
    r = r && emptyParentheses_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, EMPTY_PARENTHESES, r);
    return r;
  }

  // EOL*
  private static boolean emptyParentheses_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyParentheses_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "emptyParentheses_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // EOL* SEMICOLON EOL* | EOL
  public static boolean endOfExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression")) return false;
    if (!nextTokenIs(b, "<end of expression>", EOL, SEMICOLON)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<end of expression>");
    r = endOfExpression_0(b, l + 1);
    if (!r) r = consumeToken(b, EOL);
    exit_section_(b, l, m, END_OF_EXPRESSION, r, false, null);
    return r;
  }

  // EOL* SEMICOLON EOL*
  private static boolean endOfExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = endOfExpression_0_0(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && endOfExpression_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean endOfExpression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "endOfExpression_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean endOfExpression_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "endOfExpression_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                matchedExpression
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<expression>");
    r = emptyParentheses(b, l + 1);
    if (!r) r = matchedExpression(b, l + 1, -1);
    exit_section_(b, l, m, EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expression (endOfExpression+ expression)*
  static boolean expressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && expressionList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (endOfExpression+ expression)*
  private static boolean expressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!expressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expressionList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // endOfExpression+ expression
  private static boolean expressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expressionList_1_0_0(b, l + 1);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // endOfExpression+
  private static boolean expressionList_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = endOfExpression(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!endOfExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expressionList_1_0_0", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_INTEGER_PREFIX (HEXADECIMAL_INTEGER_BASE | OBSOLETE_HEXADECIMAL_INTEGER_BASE) (INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS)+
  public static boolean hexadecimalNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalNumber")) return false;
    if (!nextTokenIs(b, BASE_INTEGER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, BASE_INTEGER_PREFIX);
    r = r && hexadecimalNumber_1(b, l + 1);
    p = r; // pin = 2
    r = r && hexadecimalNumber_2(b, l + 1);
    exit_section_(b, l, m, HEXADECIMAL_NUMBER, r, p, null);
    return r || p;
  }

  // HEXADECIMAL_INTEGER_BASE | OBSOLETE_HEXADECIMAL_INTEGER_BASE
  private static boolean hexadecimalNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalNumber_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HEXADECIMAL_INTEGER_BASE);
    if (!r) r = consumeToken(b, OBSOLETE_HEXADECIMAL_INTEGER_BASE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS)+
  private static boolean hexadecimalNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hexadecimalNumber_2_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!hexadecimalNumber_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "hexadecimalNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS
  private static boolean hexadecimalNumber_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalNumber_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_HEXADECIMAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_HEXADECIMAL_DIGITS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | CHAR_LIST_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedCharListBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedCharListBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedCharListBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // interpolation | CHAR_LIST_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedCharListBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = consumeToken(b, VALID_ESCAPE_SEQUENCE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER interpolatedCharListBody CHAR_LIST_SIGIL_TERMINATOR
  static boolean interpolatedCharListSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    r = r && interpolatedCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_SIGIL_TERMINATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_HEREDOC_PROMOTER EOL
  //                                              interpolatedCharListBody
  //                                              CHAR_LIST_SIGIL_HEREDOC_TERMINATOR
  static boolean interpolatedHeredocCharListSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocCharListSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_HEREDOC_PROMOTER, EOL);
    r = r && interpolatedCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_SIGIL_HEREDOC_TERMINATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_HEREDOC_PROMOTER EOL
  //                                      interpolatedRegexBody
  //                                      REGEX_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedHeredocRegex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocRegex")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    r = r && interpolatedRegexBody(b, l + 1);
    r = r && consumeToken(b, REGEX_HEREDOC_TERMINATOR);
    r = r && interpolatedHeredocRegex_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedHeredocRegex_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocRegex_6")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedHeredocRegex_6", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                                      interpolatedSigilBody
  //                                      SIGIL_HEREDOC_PROMOTER SIGIL_MODIFIER*
  static boolean interpolatedHeredocSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    r = r && interpolatedSigilBody(b, l + 1);
    r = r && consumeToken(b, SIGIL_HEREDOC_PROMOTER);
    r = r && interpolatedHeredocSigil_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedHeredocSigil_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocSigil_6")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedHeredocSigil_6", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_HEREDOC_PROMOTER EOL
  //                                            interpolatedStringBody
  //                                            STRING_SIGIL_HEREDOC_TERMINATOR
  static boolean interpolatedHeredocStringSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocStringSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    r = r && interpolatedStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_SIGIL_HEREDOC_TERMINATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_WORDS_SIGIL_NAME WORDS_HEREDOC_PROMOTER EOL
  //                                      interpolatedWordsBody
  //                                      WORDS_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedHeredocWords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocWords")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    r = r && interpolatedWordsBody(b, l + 1);
    r = r && consumeToken(b, WORDS_HEREDOC_TERMINATOR);
    r = r && interpolatedHeredocWords_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedHeredocWords_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedHeredocWords_6")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedHeredocWords_6", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_PROMOTER interpolatedRegexBody REGEX_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedRegex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegex")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_PROMOTER);
    r = r && interpolatedRegexBody(b, l + 1);
    r = r && consumeToken(b, REGEX_TERMINATOR);
    r = r && interpolatedRegex_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedRegex_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegex_5")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedRegex_5", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // (interpolation | REGEX_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedRegexBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedRegexBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedRegexBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // interpolation | REGEX_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedRegexBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, REGEX_FRAGMENT);
    if (!r) r = consumeToken(b, VALID_ESCAPE_SEQUENCE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_PROMOTER interpolatedSigilBody SIGIL_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_PROMOTER);
    r = r && interpolatedSigilBody(b, l + 1);
    r = r && consumeToken(b, SIGIL_TERMINATOR);
    r = r && interpolatedSigil_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedSigil_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigil_5")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedSigil_5", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // (interpolation | SIGIL_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedSigilBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedSigilBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedSigilBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // interpolation | SIGIL_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedSigilBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, SIGIL_FRAGMENT);
    if (!r) r = consumeToken(b, VALID_ESCAPE_SEQUENCE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | STRING_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedStringBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedStringBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedStringBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // interpolation | STRING_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedStringBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = consumeToken(b, VALID_ESCAPE_SEQUENCE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER interpolatedStringBody STRING_SIGIL_TERMINATOR
  static boolean interpolatedStringSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    r = r && interpolatedStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_SIGIL_TERMINATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | WORDS_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedWordsBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedWordsBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedWordsBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // interpolation | WORDS_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedWordsBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, WORDS_FRAGMENT);
    if (!r) r = consumeToken(b, VALID_ESCAPE_SEQUENCE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INTERPOLATION_START expressionList? INTERPOLATION_END
  public static boolean interpolation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolation")) return false;
    if (!nextTokenIs(b, INTERPOLATION_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INTERPOLATION_START);
    r = r && interpolation_1(b, l + 1);
    r = r && consumeToken(b, INTERPOLATION_END);
    exit_section_(b, m, INTERPOLATION, r);
    return r;
  }

  // expressionList?
  private static boolean interpolation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolation_1")) return false;
    expressionList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ALIAS |
  //                AND_OPERATOR |
  //                ARROW_OPERATOR |
  //                ASSOCIATION_OPERATOR |
  //                AT_OPERATOR |
  //                BIT_STRING_OPERATOR |
  //                CAPTURE_OPERATOR |
  //                COMPARISON_OPERATOR |
  //                DUAL_OPERATOR |
  //                HAT_OPERATOR |
  //                IDENTIFIER |
  //                IN_MATCH_OPERATOR |
  //                IN_OPERATOR |
  //                MAP_OPERATOR |
  //                MATCH_OPERATOR |
  //                MULTIPLICATION_OPERATOR |
  //                OR_OPERATOR |
  //                PIPE_OPERATOR |
  //                RELATIONAL_OPERATOR |
  //                STAB_OPERATOR |
  //                STRUCT_OPERATOR |
  //                TUPLE_OPERATOR |
  //                TWO_OPERATOR |
  //                UNARY_OPERATOR |
  //                WHEN_OPERATOR |
  //                quote
  public static boolean keywordKey(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordKey")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<keyword key>");
    r = consumeToken(b, ALIAS);
    if (!r) r = consumeToken(b, AND_OPERATOR);
    if (!r) r = consumeToken(b, ARROW_OPERATOR);
    if (!r) r = consumeToken(b, ASSOCIATION_OPERATOR);
    if (!r) r = consumeToken(b, AT_OPERATOR);
    if (!r) r = consumeToken(b, BIT_STRING_OPERATOR);
    if (!r) r = consumeToken(b, CAPTURE_OPERATOR);
    if (!r) r = consumeToken(b, COMPARISON_OPERATOR);
    if (!r) r = consumeToken(b, DUAL_OPERATOR);
    if (!r) r = consumeToken(b, HAT_OPERATOR);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, IN_MATCH_OPERATOR);
    if (!r) r = consumeToken(b, IN_OPERATOR);
    if (!r) r = consumeToken(b, MAP_OPERATOR);
    if (!r) r = consumeToken(b, MATCH_OPERATOR);
    if (!r) r = consumeToken(b, MULTIPLICATION_OPERATOR);
    if (!r) r = consumeToken(b, OR_OPERATOR);
    if (!r) r = consumeToken(b, PIPE_OPERATOR);
    if (!r) r = consumeToken(b, RELATIONAL_OPERATOR);
    if (!r) r = consumeToken(b, STAB_OPERATOR);
    if (!r) r = consumeToken(b, STRUCT_OPERATOR);
    if (!r) r = consumeToken(b, TUPLE_OPERATOR);
    if (!r) r = consumeToken(b, TWO_OPERATOR);
    if (!r) r = consumeToken(b, UNARY_OPERATOR);
    if (!r) r = consumeToken(b, WHEN_OPERATOR);
    if (!r) r = quote(b, l + 1);
    exit_section_(b, l, m, KEYWORD_KEY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keywordKey KEYWORD_PAIR_COLON EOL* keywordValue
  public static boolean keywordPair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordPair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<keyword pair>");
    r = keywordKey(b, l + 1);
    r = r && consumeToken(b, KEYWORD_PAIR_COLON);
    r = r && keywordPair_2(b, l + 1);
    r = r && keywordValue(b, l + 1);
    exit_section_(b, l, m, KEYWORD_PAIR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean keywordPair_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordPair_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "keywordPair_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // containerExpression
  public static boolean keywordValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordValue")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = containerExpression(b, l + 1);
    exit_section_(b, m, KEYWORD_VALUE, r);
    return r;
  }

  /* ********************************************************** */
  // OPENING_BRACKET EOL* (keywordPair (COMMA EOL* keywordPair)* COMMA?)? CLOSING_BRACKET
  public static boolean list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list")) return false;
    if (!nextTokenIs(b, OPENING_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_BRACKET);
    r = r && list_1(b, l + 1);
    r = r && list_2(b, l + 1);
    r = r && consumeToken(b, CLOSING_BRACKET);
    exit_section_(b, m, LIST, r);
    return r;
  }

  // EOL*
  private static boolean list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "list_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (keywordPair (COMMA EOL* keywordPair)* COMMA?)?
  private static boolean list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2")) return false;
    list_2_0(b, l + 1);
    return true;
  }

  // keywordPair (COMMA EOL* keywordPair)* COMMA?
  private static boolean list_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keywordPair(b, l + 1);
    r = r && list_2_0_1(b, l + 1);
    r = r && list_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA EOL* keywordPair)*
  private static boolean list_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!list_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "list_2_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA EOL* keywordPair
  private static boolean list_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && list_2_0_1_0_1(b, l + 1);
    r = r && keywordPair(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean list_2_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "list_2_0_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA?
  private static boolean list_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // charList | charListHeredoc
  static boolean listString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listString")) return false;
    if (!nextTokenIs(b, "", CHAR_LIST_HEREDOC_PROMOTER, CHAR_LIST_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = charList(b, l + 1);
    if (!r) r = charListHeredoc(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CHAR_LIST_FRAGMENT*
  static boolean literalCharListBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, CHAR_LIST_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalCharListBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER literalCharListBody CHAR_LIST_SIGIL_TERMINATOR
  static boolean literalCharListSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    r = r && literalCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_SIGIL_TERMINATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_REGEX_SIGIL_NAME REGEX_HEREDOC_PROMOTER EOL
  //                                 literalRegexBody
  //                                 REGEX_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean literalHeredocRegex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalHeredocRegex")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    r = r && literalRegexBody(b, l + 1);
    r = r && consumeToken(b, REGEX_HEREDOC_TERMINATOR);
    r = r && literalHeredocRegex_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean literalHeredocRegex_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalHeredocRegex_6")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "literalHeredocRegex_6", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                                 literalSigilBody
  //                                 SIGIL_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean literalHeredocSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalHeredocSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    r = r && literalSigilBody(b, l + 1);
    r = r && consumeToken(b, SIGIL_HEREDOC_TERMINATOR);
    r = r && literalHeredocSigil_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean literalHeredocSigil_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalHeredocSigil_6")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "literalHeredocSigil_6", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_STRING_SIGIL_NAME STRING_SIGIL_HEREDOC_PROMOTER EOL
  //                                       literalStringBody
  //                                       STRING_SIGIL_HEREDOC_TERMINATOR
  static boolean literalHeredocStringSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalHeredocStringSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    r = r && literalStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_SIGIL_HEREDOC_TERMINATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_WORDS_SIGIL_NAME WORDS_HEREDOC_PROMOTER EOL
  //                                 literalWordsBody
  //                                 WORDS_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean literalHeredocWords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalHeredocWords")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    r = r && literalWordsBody(b, l + 1);
    r = r && consumeToken(b, WORDS_HEREDOC_TERMINATOR);
    r = r && literalHeredocWords_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean literalHeredocWords_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalHeredocWords_6")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "literalHeredocWords_6", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME REGEX_PROMOTER literalRegexBody REGEX_TERMINATOR SIGIL_MODIFIER*
  static boolean literalRegex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegex")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_SIGIL_NAME, REGEX_PROMOTER);
    r = r && literalRegexBody(b, l + 1);
    r = r && consumeToken(b, REGEX_TERMINATOR);
    r = r && literalRegex_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean literalRegex_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegex_5")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "literalRegex_5", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // REGEX_FRAGMENT*
  static boolean literalRegexBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, REGEX_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalRegexBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_PROMOTER literalSigilBody SIGIL_TERMINATOR SIGIL_MODIFIER*
  static boolean literalSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_SIGIL_NAME, SIGIL_PROMOTER);
    r = r && literalSigilBody(b, l + 1);
    r = r && consumeToken(b, SIGIL_TERMINATOR);
    r = r && literalSigil_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean literalSigil_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigil_5")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "literalSigil_5", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // SIGIL_FRAGMENT*
  static boolean literalSigilBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalSigilBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // STRING_FRAGMENT*
  static boolean literalStringBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, STRING_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalStringBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER literalStringBody STRING_SIGIL_TERMINATOR
  static boolean literalStringSigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    r = r && literalStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_SIGIL_TERMINATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME WORDS_PROMOTER literal WORDS_TERMINATOR SIGIL_MODIFIER*
  static boolean literalWords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWords")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_SIGIL_NAME, WORDS_PROMOTER, LITERAL, WORDS_TERMINATOR);
    r = r && literalWords_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SIGIL_MODIFIER*
  private static boolean literalWords_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWords_5")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "literalWords_5", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // WORDS_FRAGMENT*
  static boolean literalWordsBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, WORDS_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalWordsBody", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // atom |
  //                   ALIAS
  public static boolean maxExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression")) return false;
    if (!nextTokenIs(b, "<max expression>", ALIAS, COLON)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<max expression>");
    r = atom(b, l + 1);
    if (!r) r = consumeToken(b, ALIAS);
    exit_section_(b, l, m, MAX_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AT_OPERATOR EOL* NUMBER
  public static boolean numberAtOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberAtOperation")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT_OPERATOR);
    r = r && numberAtOperation_1(b, l + 1);
    r = r && consumeToken(b, NUMBER);
    exit_section_(b, m, NUMBER_AT_OPERATION, r);
    return r;
  }

  // EOL*
  private static boolean numberAtOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberAtOperation_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "numberAtOperation_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // CAPTURE_OPERATOR EOL* NUMBER
  public static boolean numberCaptureOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberCaptureOperation")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CAPTURE_OPERATOR);
    r = r && numberCaptureOperation_1(b, l + 1);
    r = r && consumeToken(b, NUMBER);
    exit_section_(b, m, NUMBER_CAPTURE_OPERATION, r);
    return r;
  }

  // EOL*
  private static boolean numberCaptureOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberCaptureOperation_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "numberCaptureOperation_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // (DUAL_OPERATOR | UNARY_OPERATOR) EOL* NUMBER
  public static boolean numberUnaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberUnaryOperation")) return false;
    if (!nextTokenIs(b, "<number unary operation>", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<number unary operation>");
    r = numberUnaryOperation_0(b, l + 1);
    r = r && numberUnaryOperation_1(b, l + 1);
    r = r && consumeToken(b, NUMBER);
    exit_section_(b, l, m, NUMBER_UNARY_OPERATION, r, false, null);
    return r;
  }

  // DUAL_OPERATOR | UNARY_OPERATOR
  private static boolean numberUnaryOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberUnaryOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DUAL_OPERATOR);
    if (!r) r = consumeToken(b, UNARY_OPERATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean numberUnaryOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberUnaryOperation_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "numberUnaryOperation_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // BASE_INTEGER_PREFIX OCTAL_INTEGER_BASE (INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS)+
  public static boolean octalNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalNumber")) return false;
    if (!nextTokenIs(b, BASE_INTEGER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 2, BASE_INTEGER_PREFIX, OCTAL_INTEGER_BASE);
    p = r; // pin = 2
    r = r && octalNumber_2(b, l + 1);
    exit_section_(b, l, m, OCTAL_NUMBER, r, p, null);
    return r || p;
  }

  // (INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS)+
  private static boolean octalNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = octalNumber_2_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!octalNumber_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "octalNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS
  private static boolean octalNumber_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalNumber_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_OCTAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_OCTAL_DIGITS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // charList | string
  static boolean quote(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quote")) return false;
    if (!nextTokenIs(b, "", CHAR_LIST_PROMOTER, STRING_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = charList(b, l + 1);
    if (!r) r = string(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // interpolatedCharListSigil |
  //           interpolatedHeredocCharListSigil |
  //           interpolatedHeredocRegex |
  //           interpolatedHeredocSigil |
  //           interpolatedHeredocStringSigil |
  //           interpolatedHeredocWords |
  //           interpolatedRegex |
  //           interpolatedSigil |
  //           interpolatedStringSigil |
  //           literalCharListSigil |
  //           literalHeredocRegex |
  //           literalHeredocSigil |
  //           literalHeredocStringSigil |
  //           literalHeredocWords |
  //           literalRegex |
  //           literalSigil |
  //           literalStringSigil |
  //           literalWords
  public static boolean sigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolatedCharListSigil(b, l + 1);
    if (!r) r = interpolatedHeredocCharListSigil(b, l + 1);
    if (!r) r = interpolatedHeredocRegex(b, l + 1);
    if (!r) r = interpolatedHeredocSigil(b, l + 1);
    if (!r) r = interpolatedHeredocStringSigil(b, l + 1);
    if (!r) r = interpolatedHeredocWords(b, l + 1);
    if (!r) r = interpolatedRegex(b, l + 1);
    if (!r) r = interpolatedSigil(b, l + 1);
    if (!r) r = interpolatedStringSigil(b, l + 1);
    if (!r) r = literalCharListSigil(b, l + 1);
    if (!r) r = literalHeredocRegex(b, l + 1);
    if (!r) r = literalHeredocSigil(b, l + 1);
    if (!r) r = literalHeredocStringSigil(b, l + 1);
    if (!r) r = literalHeredocWords(b, l + 1);
    if (!r) r = literalRegex(b, l + 1);
    if (!r) r = literalSigil(b, l + 1);
    if (!r) r = literalStringSigil(b, l + 1);
    if (!r) r = literalWords(b, l + 1);
    exit_section_(b, m, SIGIL, r);
    return r;
  }

  /* ********************************************************** */
  // STRING_PROMOTER
  //            interpolatedStringBody
  //            STRING_TERMINATOR
  public static boolean string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string")) return false;
    if (!nextTokenIs(b, STRING_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING_PROMOTER);
    r = r && interpolatedStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_TERMINATOR);
    exit_section_(b, m, STRING, r);
    return r;
  }

  /* ********************************************************** */
  // STRING_HEREDOC_PROMOTER EOL
  //                   interpolatedStringBody
  //                   STRING_HEREDOC_TERMINATOR
  public static boolean stringHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredoc")) return false;
    if (!nextTokenIs(b, STRING_HEREDOC_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, STRING_HEREDOC_PROMOTER, EOL);
    r = r && interpolatedStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_HEREDOC_TERMINATOR);
    exit_section_(b, m, STRING_HEREDOC, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_INTEGER_PREFIX UNKNOWN_INTEGER_BASE INVALID_UNKNOWN_BASE_DIGITS+
  public static boolean unknownBaseNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknownBaseNumber")) return false;
    if (!nextTokenIs(b, BASE_INTEGER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 2, BASE_INTEGER_PREFIX, UNKNOWN_INTEGER_BASE);
    p = r; // pin = 2
    r = r && unknownBaseNumber_2(b, l + 1);
    exit_section_(b, l, m, UNKNOWN_BASE_NUMBER, r, p, null);
    return r || p;
  }

  // INVALID_UNKNOWN_BASE_DIGITS+
  private static boolean unknownBaseNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknownBaseNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_UNKNOWN_BASE_DIGITS);
    int c = current_position_(b);
    while (r) {
      if (!consumeToken(b, INVALID_UNKNOWN_BASE_DIGITS)) break;
      if (!empty_element_parsed_guard_(b, "unknownBaseNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Expression root: matchedExpression
  // Operator priority table:
  // 0: PREFIX(matchedExpressionCaptureOperation)
  // 1: BINARY(matchedExpressionInMatchOperation)
  // 2: BINARY(matchedExpressionWhenOperation)
  // 3: BINARY(matchedExpressionTypeOperation)
  // 4: BINARY(matchedExpressionPipeOperation)
  // 5: BINARY(matchedExpressionMatchOperation)
  // 6: BINARY(matchedExpressionOrOperation)
  // 7: BINARY(matchedExpressionAndOperation)
  // 8: BINARY(matchedExpressionComparisonOperation)
  // 9: BINARY(matchedExpressionRelationalOperation)
  // 10: BINARY(matchedExpressionArrowOperation)
  // 11: BINARY(matchedExpressionInOperation)
  // 12: BINARY(matchedExpressionTwoOperation)
  // 13: BINARY(matchedExpressionAdditionOperation)
  // 14: BINARY(matchedExpressionMultiplicationOperation)
  // 15: BINARY(matchedExpressionHatOperation)
  // 16: PREFIX(matchedExpressionUnaryOperation)
  // 17: BINARY(matchedExpressionDotOperation)
  // 18: PREFIX(matchedExpressionAtOperation)
  // 19: ATOM(identifierExpression)
  // 20: ATOM(accessExpression)
  public static boolean matchedExpression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "matchedExpression")) return false;
    addVariant(b, "<matched expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<matched expression>");
    r = matchedExpressionCaptureOperation(b, l + 1);
    if (!r) r = matchedExpressionUnaryOperation(b, l + 1);
    if (!r) r = matchedExpressionAtOperation(b, l + 1);
    if (!r) r = identifierExpression(b, l + 1);
    if (!r) r = accessExpression(b, l + 1);
    p = r;
    r = r && matchedExpression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean matchedExpression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "matchedExpression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 1 && matchedExpressionInMatchOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 1);
        exit_section_(b, l, m, MATCHED_EXPRESSION_IN_MATCH_OPERATION, r, true, null);
      }
      else if (g < 2 && matchedExpressionWhenOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 1);
        exit_section_(b, l, m, MATCHED_EXPRESSION_WHEN_OPERATION, r, true, null);
      }
      else if (g < 3 && matchedExpressionTypeOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 2);
        exit_section_(b, l, m, MATCHED_EXPRESSION_TYPE_OPERATION, r, true, null);
      }
      else if (g < 4 && matchedExpressionPipeOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 3);
        exit_section_(b, l, m, MATCHED_EXPRESSION_PIPE_OPERATION, r, true, null);
      }
      else if (g < 5 && matchedExpressionMatchOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 4);
        exit_section_(b, l, m, MATCHED_EXPRESSION_MATCH_OPERATION, r, true, null);
      }
      else if (g < 6 && matchedExpressionOrOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 6);
        exit_section_(b, l, m, MATCHED_EXPRESSION_OR_OPERATION, r, true, null);
      }
      else if (g < 7 && matchedExpressionAndOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 7);
        exit_section_(b, l, m, MATCHED_EXPRESSION_AND_OPERATION, r, true, null);
      }
      else if (g < 8 && matchedExpressionComparisonOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 8);
        exit_section_(b, l, m, MATCHED_EXPRESSION_COMPARISON_OPERATION, r, true, null);
      }
      else if (g < 9 && matchedExpressionRelationalOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 9);
        exit_section_(b, l, m, MATCHED_EXPRESSION_RELATIONAL_OPERATION, r, true, null);
      }
      else if (g < 10 && matchedExpressionArrowOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 10);
        exit_section_(b, l, m, MATCHED_EXPRESSION_ARROW_OPERATION, r, true, null);
      }
      else if (g < 11 && matchedExpressionInOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 11);
        exit_section_(b, l, m, MATCHED_EXPRESSION_IN_OPERATION, r, true, null);
      }
      else if (g < 12 && matchedExpressionTwoOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 11);
        exit_section_(b, l, m, MATCHED_EXPRESSION_TWO_OPERATION, r, true, null);
      }
      else if (g < 13 && matchedExpressionAdditionOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 13);
        exit_section_(b, l, m, MATCHED_EXPRESSION_ADDITION_OPERATION, r, true, null);
      }
      else if (g < 14 && matchedExpressionMultiplicationOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 14);
        exit_section_(b, l, m, MATCHED_EXPRESSION_MULTIPLICATION_OPERATION, r, true, null);
      }
      else if (g < 15 && matchedExpressionHatOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 15);
        exit_section_(b, l, m, MATCHED_EXPRESSION_HAT_OPERATION, r, true, null);
      }
      else if (g < 17 && matchedExpressionDotOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 17);
        exit_section_(b, l, m, MATCHED_EXPRESSION_DOT_OPERATION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  public static boolean matchedExpressionCaptureOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionCaptureOperation")) return false;
    if (!nextTokenIsFast(b, CAPTURE_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedExpressionCaptureOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 0);
    exit_section_(b, l, m, MATCHED_EXPRESSION_CAPTURE_OPERATION, r, p, null);
    return r || p;
  }

  // CAPTURE_OPERATOR EOL*
  private static boolean matchedExpressionCaptureOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionCaptureOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, CAPTURE_OPERATOR);
    r = r && matchedExpressionCaptureOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionCaptureOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionCaptureOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionCaptureOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* IN_MATCH_OPERATOR EOL*
  private static boolean matchedExpressionInMatchOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionInMatchOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionInMatchOperation_0_0(b, l + 1);
    r = r && consumeToken(b, IN_MATCH_OPERATOR);
    r = r && matchedExpressionInMatchOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionInMatchOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionInMatchOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionInMatchOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionInMatchOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionInMatchOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionInMatchOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* WHEN_OPERATOR EOL*
  private static boolean matchedExpressionWhenOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionWhenOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionWhenOperation_0_0(b, l + 1);
    r = r && consumeToken(b, WHEN_OPERATOR);
    r = r && matchedExpressionWhenOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionWhenOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionWhenOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionWhenOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionWhenOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionWhenOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionWhenOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* TYPE_OPERATOR EOL*
  private static boolean matchedExpressionTypeOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionTypeOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionTypeOperation_0_0(b, l + 1);
    r = r && consumeToken(b, TYPE_OPERATOR);
    r = r && matchedExpressionTypeOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionTypeOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionTypeOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionTypeOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionTypeOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionTypeOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionTypeOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* PIPE_OPERATOR EOL*
  private static boolean matchedExpressionPipeOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionPipeOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionPipeOperation_0_0(b, l + 1);
    r = r && consumeToken(b, PIPE_OPERATOR);
    r = r && matchedExpressionPipeOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionPipeOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionPipeOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionPipeOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionPipeOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionPipeOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionPipeOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* MATCH_OPERATOR EOL*
  private static boolean matchedExpressionMatchOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionMatchOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionMatchOperation_0_0(b, l + 1);
    r = r && consumeToken(b, MATCH_OPERATOR);
    r = r && matchedExpressionMatchOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionMatchOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionMatchOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionMatchOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionMatchOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionMatchOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionMatchOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* OR_OPERATOR EOL*
  private static boolean matchedExpressionOrOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionOrOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionOrOperation_0_0(b, l + 1);
    r = r && consumeToken(b, OR_OPERATOR);
    r = r && matchedExpressionOrOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionOrOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionOrOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionOrOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionOrOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionOrOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionOrOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* AND_OPERATOR EOL*
  private static boolean matchedExpressionAndOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAndOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionAndOperation_0_0(b, l + 1);
    r = r && consumeToken(b, AND_OPERATOR);
    r = r && matchedExpressionAndOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionAndOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAndOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionAndOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionAndOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAndOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionAndOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* COMPARISON_OPERATOR EOL*
  private static boolean matchedExpressionComparisonOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionComparisonOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionComparisonOperation_0_0(b, l + 1);
    r = r && consumeToken(b, COMPARISON_OPERATOR);
    r = r && matchedExpressionComparisonOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionComparisonOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionComparisonOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionComparisonOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionComparisonOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionComparisonOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionComparisonOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* RELATIONAL_OPERATOR EOL*
  private static boolean matchedExpressionRelationalOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionRelationalOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionRelationalOperation_0_0(b, l + 1);
    r = r && consumeToken(b, RELATIONAL_OPERATOR);
    r = r && matchedExpressionRelationalOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionRelationalOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionRelationalOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionRelationalOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionRelationalOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionRelationalOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionRelationalOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* ARROW_OPERATOR EOL*
  private static boolean matchedExpressionArrowOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionArrowOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionArrowOperation_0_0(b, l + 1);
    r = r && consumeToken(b, ARROW_OPERATOR);
    r = r && matchedExpressionArrowOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionArrowOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionArrowOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionArrowOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionArrowOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionArrowOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionArrowOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* IN_OPERATOR EOL*
  private static boolean matchedExpressionInOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionInOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionInOperation_0_0(b, l + 1);
    r = r && consumeToken(b, IN_OPERATOR);
    r = r && matchedExpressionInOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionInOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionInOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionInOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionInOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionInOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionInOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* TWO_OPERATOR EOL*
  private static boolean matchedExpressionTwoOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionTwoOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionTwoOperation_0_0(b, l + 1);
    r = r && consumeToken(b, TWO_OPERATOR);
    r = r && matchedExpressionTwoOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionTwoOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionTwoOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionTwoOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionTwoOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionTwoOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionTwoOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DUAL_OPERATOR EOL*
  private static boolean matchedExpressionAdditionOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAdditionOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DUAL_OPERATOR);
    r = r && matchedExpressionAdditionOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionAdditionOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAdditionOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionAdditionOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* MULTIPLICATION_OPERATOR EOL*
  private static boolean matchedExpressionMultiplicationOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionMultiplicationOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionMultiplicationOperation_0_0(b, l + 1);
    r = r && consumeToken(b, MULTIPLICATION_OPERATOR);
    r = r && matchedExpressionMultiplicationOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionMultiplicationOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionMultiplicationOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionMultiplicationOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionMultiplicationOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionMultiplicationOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionMultiplicationOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* HAT_OPERATOR EOL*
  private static boolean matchedExpressionHatOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionHatOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionHatOperation_0_0(b, l + 1);
    r = r && consumeToken(b, HAT_OPERATOR);
    r = r && matchedExpressionHatOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionHatOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionHatOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionHatOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionHatOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionHatOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionHatOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  public static boolean matchedExpressionUnaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionUnaryOperation")) return false;
    if (!nextTokenIsFast(b, DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedExpressionUnaryOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 16);
    exit_section_(b, l, m, MATCHED_EXPRESSION_UNARY_OPERATION, r, p, null);
    return r || p;
  }

  // (DUAL_OPERATOR | UNARY_OPERATOR) EOL*
  private static boolean matchedExpressionUnaryOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionUnaryOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionUnaryOperation_0_0(b, l + 1);
    r = r && matchedExpressionUnaryOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DUAL_OPERATOR | UNARY_OPERATOR
  private static boolean matchedExpressionUnaryOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionUnaryOperation_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DUAL_OPERATOR);
    if (!r) r = consumeTokenSmart(b, UNARY_OPERATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionUnaryOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionUnaryOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionUnaryOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* DOT_OPERATOR EOL*
  private static boolean matchedExpressionDotOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionDotOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpressionDotOperation_0_0(b, l + 1);
    r = r && consumeToken(b, DOT_OPERATOR);
    r = r && matchedExpressionDotOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionDotOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionDotOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionDotOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchedExpressionDotOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionDotOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionDotOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  public static boolean matchedExpressionAtOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAtOperation")) return false;
    if (!nextTokenIsFast(b, AT_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedExpressionAtOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 18);
    exit_section_(b, l, m, MATCHED_EXPRESSION_AT_OPERATION, r, p, null);
    return r || p;
  }

  // AT_OPERATOR EOL*
  private static boolean matchedExpressionAtOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAtOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, AT_OPERATOR);
    r = r && matchedExpressionAtOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchedExpressionAtOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpressionAtOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchedExpressionAtOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // IDENTIFIER
  public static boolean identifierExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifierExpression")) return false;
    if (!nextTokenIsFast(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, IDENTIFIER);
    exit_section_(b, m, IDENTIFIER_EXPRESSION, r);
    return r;
  }

  // numberCaptureOperation |
  //                      numberUnaryOperation |
  //                      numberAtOperation |
  //                      OPENING_PARENTHESIS EOL* SEMICOLON EOL* CLOSING_PARENTHESIS |
  //                      /* elixir_tokenizer.erl converts CHAR_TOKENs to their number representation, so `number` in
  //                         elixir_parser.yrl matches Elixir.flex's NUMBER and CHAR_TOKEN. */
  //                      CHAR_TOKEN |
  //                      NUMBER |
  //                      binaryNumber |
  //                      hexadecimalNumber |
  //                      octalNumber |
  //                      unknownBaseNumber |
  //                      list |
  //                      binaryString |
  //                      listString |
  //                      sigil |
  //                      FALSE |
  //                      NIL |
  //                      TRUE |
  //                      maxExpression
  public static boolean accessExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<access expression>");
    r = numberCaptureOperation(b, l + 1);
    if (!r) r = numberUnaryOperation(b, l + 1);
    if (!r) r = numberAtOperation(b, l + 1);
    if (!r) r = accessExpression_3(b, l + 1);
    if (!r) r = consumeTokenSmart(b, CHAR_TOKEN);
    if (!r) r = consumeTokenSmart(b, NUMBER);
    if (!r) r = binaryNumber(b, l + 1);
    if (!r) r = hexadecimalNumber(b, l + 1);
    if (!r) r = octalNumber(b, l + 1);
    if (!r) r = unknownBaseNumber(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = binaryString(b, l + 1);
    if (!r) r = listString(b, l + 1);
    if (!r) r = sigil(b, l + 1);
    if (!r) r = consumeTokenSmart(b, FALSE);
    if (!r) r = consumeTokenSmart(b, NIL);
    if (!r) r = consumeTokenSmart(b, TRUE);
    if (!r) r = maxExpression(b, l + 1);
    exit_section_(b, l, m, ACCESS_EXPRESSION, r, false, null);
    return r;
  }

  // OPENING_PARENTHESIS EOL* SEMICOLON EOL* CLOSING_PARENTHESIS
  private static boolean accessExpression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, OPENING_PARENTHESIS);
    r = r && accessExpression_3_1(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && accessExpression_3_3(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean accessExpression_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression_3_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "accessExpression_3_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean accessExpression_3_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression_3_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "accessExpression_3_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

}
