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
    if (t == ADJACENT_EXPRESSION) {
      r = adjacentExpression(b, 0);
    }
    else if (t == AT_CHAR_TOKEN_OR_NUMBER_OPERATION) {
      r = atCharTokenOrNumberOperation(b, 0);
    }
    else if (t == AT_PREFIX_OPERATOR) {
      r = atPrefixOperator(b, 0);
    }
    else if (t == ATOM) {
      r = atom(b, 0);
    }
    else if (t == BINARY_DIGITS) {
      r = binaryDigits(b, 0);
    }
    else if (t == BINARY_WHOLE_NUMBER) {
      r = binaryWholeNumber(b, 0);
    }
    else if (t == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS) {
      r = callArgumentsNoParenthesesKeywords(b, 0);
    }
    else if (t == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS_EXPRESSION) {
      r = callArgumentsNoParenthesesKeywordsExpression(b, 0);
    }
    else if (t == CALL_ARGUMENTS_NO_PARENTHESES_MANY) {
      r = callArgumentsNoParenthesesMany(b, 0);
    }
    else if (t == CAPTURE_CHAR_TOKEN_OR_NUMBER_OPERATION) {
      r = captureCharTokenOrNumberOperation(b, 0);
    }
    else if (t == CAPTURE_PREFIX_OPERATOR) {
      r = capturePrefixOperator(b, 0);
    }
    else if (t == CHAR_LIST) {
      r = charList(b, 0);
    }
    else if (t == CHAR_LIST_HEREDOC) {
      r = charListHeredoc(b, 0);
    }
    else if (t == DECIMAL_FLOAT) {
      r = decimalFloat(b, 0);
    }
    else if (t == DECIMAL_NUMBER) {
      r = decimalNumber(b, 0);
    }
    else if (t == DECIMAL_WHOLE_NUMBER) {
      r = decimalWholeNumber(b, 0);
    }
    else if (t == EMPTY_PARENTHESES) {
      r = emptyParentheses(b, 0);
    }
    else if (t == END_OF_EXPRESSION) {
      r = endOfExpression(b, 0);
    }
    else if (t == HAT_INFIX_OPERATOR) {
      r = hatInfixOperator(b, 0);
    }
    else if (t == HEXADECIMAL_WHOLE_NUMBER) {
      r = hexadecimalWholeNumber(b, 0);
    }
    else if (t == INFIX_DOT_OPERATOR) {
      r = infixDotOperator(b, 0);
    }
    else if (t == INTERPOLATED_CHAR_LIST_BODY) {
      r = interpolatedCharListBody(b, 0);
    }
    else if (t == INTERPOLATED_STRING_BODY) {
      r = interpolatedStringBody(b, 0);
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
    else if (t == MATCHED_AT_OPERATION) {
      r = matchedAtOperation(b, 0);
    }
    else if (t == MATCHED_HAT_OPERATION) {
      r = matchedHatOperation(b, 0);
    }
    else if (t == MATCHED_MULTIPLICATION_OPERATION) {
      r = matchedMultiplicationOperation(b, 0);
    }
    else if (t == MATCHED_NON_NUMERIC_CAPTURE_OPERATION) {
      r = matchedNonNumericCaptureOperation(b, 0);
    }
    else if (t == MATCHED_UNARY_OPERATION) {
      r = matchedUnaryOperation(b, 0);
    }
    else if (t == MAX_EXPRESSION) {
      r = maxExpression(b, 0);
    }
    else if (t == MULTIPLICATION_INFIX_OPERATOR) {
      r = multiplicationInfixOperator(b, 0);
    }
    else if (t == NO_PARENTHESES_CALL) {
      r = noParenthesesCall(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION) {
      r = noParenthesesManyStrictNoParenthesesExpression(b, 0);
    }
    else if (t == NO_PARENTHESES_ONE_EXPRESSION) {
      r = noParenthesesOneExpression(b, 0);
    }
    else if (t == NO_PARENTHESES_STRICT) {
      r = noParenthesesStrict(b, 0);
    }
    else if (t == NUMBER) {
      r = number(b, 0);
    }
    else if (t == OCTAL_WHOLE_NUMBER) {
      r = octalWholeNumber(b, 0);
    }
    else if (t == QUALIFIED_ALIAS) {
      r = qualifiedAlias(b, 0);
    }
    else if (t == QUALIFIED_IDENTIFIER) {
      r = qualifiedIdentifier(b, 0);
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
    else if (t == UNARY_CHAR_TOKEN_OR_NUMBER_OPERATION) {
      r = unaryCharTokenOrNumberOperation(b, 0);
    }
    else if (t == UNARY_PREFIX_OPERATOR) {
      r = unaryPrefixOperator(b, 0);
    }
    else if (t == UNKNOWN_BASE_WHOLE_NUMBER) {
      r = unknownBaseWholeNumber(b, 0);
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
    create_token_set_(DECIMAL_FLOAT, DECIMAL_NUMBER),
    create_token_set_(BINARY_WHOLE_NUMBER, DECIMAL_FLOAT, DECIMAL_NUMBER, DECIMAL_WHOLE_NUMBER,
      HEXADECIMAL_WHOLE_NUMBER, NUMBER, OCTAL_WHOLE_NUMBER, UNKNOWN_BASE_WHOLE_NUMBER),
  };

  /* ********************************************************** */
  // atCharTokenOrNumberOperation |
  //                              captureCharTokenOrNumberOperation |
  //                              unaryCharTokenOrNumberOperation |
  //                              OPENING_PARENTHESIS infixSemicolon CLOSING_PARENTHESIS |
  //                              numeric |
  //                              list |
  //                              binaryString |
  //                              listString |
  //                              sigil |
  //                              FALSE |
  //                              NIL |
  //                              TRUE |
  //                              maxExpression
  static boolean accessExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atCharTokenOrNumberOperation(b, l + 1);
    if (!r) r = captureCharTokenOrNumberOperation(b, l + 1);
    if (!r) r = unaryCharTokenOrNumberOperation(b, l + 1);
    if (!r) r = accessExpression_3(b, l + 1);
    if (!r) r = numeric(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = binaryString(b, l + 1);
    if (!r) r = listString(b, l + 1);
    if (!r) r = sigil(b, l + 1);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NIL);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = maxExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OPENING_PARENTHESIS infixSemicolon CLOSING_PARENTHESIS
  private static boolean accessExpression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_PARENTHESIS);
    r = r && infixSemicolon(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expression
  public static boolean adjacentExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "adjacentExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<adjacent expression>");
    r = expression(b, l + 1);
    exit_section_(b, l, m, ADJACENT_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // atPrefixOperator numeric
  public static boolean atCharTokenOrNumberOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atCharTokenOrNumberOperation")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, m, AT_CHAR_TOKEN_OR_NUMBER_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // AT_OPERATOR EOL*
  public static boolean atPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atPrefixOperator")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT_OPERATOR);
    r = r && atPrefixOperator_1(b, l + 1);
    exit_section_(b, m, AT_PREFIX_OPERATOR, r);
    return r;
  }

  // EOL*
  private static boolean atPrefixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atPrefixOperator_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "atPrefixOperator_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

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
  // INVALID_BINARY_DIGITS | VALID_BINARY_DIGITS
  public static boolean binaryDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryDigits")) return false;
    if (!nextTokenIs(b, "<binary digits>", INVALID_BINARY_DIGITS, VALID_BINARY_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<binary digits>");
    r = consumeToken(b, INVALID_BINARY_DIGITS);
    if (!r) r = consumeToken(b, VALID_BINARY_DIGITS);
    exit_section_(b, l, m, BINARY_DIGITS, r, false, null);
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
  // BASE_WHOLE_NUMBER_PREFIX (BINARY_WHOLE_NUMBER_BASE | OBSOLETE_BINARY_WHOLE_NUMBER_BASE) binaryDigits+
  public static boolean binaryWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber")) return false;
    if (!nextTokenIs(b, BASE_WHOLE_NUMBER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, BASE_WHOLE_NUMBER_PREFIX);
    r = r && binaryWholeNumber_1(b, l + 1);
    p = r; // pin = 2
    r = r && binaryWholeNumber_2(b, l + 1);
    exit_section_(b, l, m, BINARY_WHOLE_NUMBER, r, p, null);
    return r || p;
  }

  // BINARY_WHOLE_NUMBER_BASE | OBSOLETE_BINARY_WHOLE_NUMBER_BASE
  private static boolean binaryWholeNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BINARY_WHOLE_NUMBER_BASE);
    if (!r) r = consumeToken(b, OBSOLETE_BINARY_WHOLE_NUMBER_BASE);
    exit_section_(b, m, null, r);
    return r;
  }

  // binaryDigits+
  private static boolean binaryWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = binaryDigits(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!binaryDigits(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "binaryWholeNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureExpression (infixComma callArgumentsNoParenthesesExpression)+
  static boolean callArgumentsNoParenthesesCommaExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesCommaExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureExpression(b, l + 1);
    r = r && callArgumentsNoParenthesesCommaExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma callArgumentsNoParenthesesExpression)+
  private static boolean callArgumentsNoParenthesesCommaExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesCommaExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = callArgumentsNoParenthesesCommaExpression_1_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!callArgumentsNoParenthesesCommaExpression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "callArgumentsNoParenthesesCommaExpression_1", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // infixComma callArgumentsNoParenthesesExpression
  private static boolean callArgumentsNoParenthesesCommaExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesCommaExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && callArgumentsNoParenthesesExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                                                  /* Must be before matchedExpression because noParenthesesExpression is
  //                                                     `matchedExpressionDotIdentifier callArgumentsNoParenthesesManyStrict`
  //                                                     which is longer than `matchedExpressionDotIdentifier` in
  //                                                     matchedExpression. */
  //                                                  /* This will be marked as an error by
  //                                                     {@link org.elixir_lang.inspection.NoParenthesesManyStrict} */
  //                                                  noParenthesesManyStrictNoParenthesesExpression |
  //                                                  matchedNonNumericCaptureExpression !KEYWORD_PAIR_COLON
  static boolean callArgumentsNoParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = emptyParentheses(b, l + 1);
    if (!r) r = noParenthesesManyStrictNoParenthesesExpression(b, l + 1);
    if (!r) r = callArgumentsNoParenthesesExpression_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedNonNumericCaptureExpression !KEYWORD_PAIR_COLON
  private static boolean callArgumentsNoParenthesesExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesExpression_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureExpression(b, l + 1);
    r = r && callArgumentsNoParenthesesExpression_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !KEYWORD_PAIR_COLON
  private static boolean callArgumentsNoParenthesesExpression_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesExpression_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !consumeToken(b, KEYWORD_PAIR_COLON);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // callArgumentsNoParenthesesKeywordsExpression (infixComma callArgumentsNoParenthesesKeywordsExpression)*
  public static boolean callArgumentsNoParenthesesKeywords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesKeywords")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<call arguments no parentheses keywords>");
    r = callArgumentsNoParenthesesKeywordsExpression(b, l + 1);
    r = r && callArgumentsNoParenthesesKeywords_1(b, l + 1);
    exit_section_(b, l, m, CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS, r, false, null);
    return r;
  }

  // (infixComma callArgumentsNoParenthesesKeywordsExpression)*
  private static boolean callArgumentsNoParenthesesKeywords_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesKeywords_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!callArgumentsNoParenthesesKeywords_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "callArgumentsNoParenthesesKeywords_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // infixComma callArgumentsNoParenthesesKeywordsExpression
  private static boolean callArgumentsNoParenthesesKeywords_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesKeywords_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && callArgumentsNoParenthesesKeywordsExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keywordKeyColonEOL callArgumentsNoParenthesesExpression
  public static boolean callArgumentsNoParenthesesKeywordsExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesKeywordsExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<call arguments no parentheses keywords expression>");
    r = keywordKeyColonEOL(b, l + 1);
    r = r && callArgumentsNoParenthesesExpression(b, l + 1);
    exit_section_(b, l, m, CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureExpression infixComma callArgumentsNoParenthesesKeywords |
  //                                    callArgumentsNoParenthesesCommaExpression (infixComma callArgumentsNoParenthesesKeywords)?
  public static boolean callArgumentsNoParenthesesMany(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesMany")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<call arguments no parentheses many>");
    r = callArgumentsNoParenthesesMany_0(b, l + 1);
    if (!r) r = callArgumentsNoParenthesesMany_1(b, l + 1);
    exit_section_(b, l, m, CALL_ARGUMENTS_NO_PARENTHESES_MANY, r, false, null);
    return r;
  }

  // matchedNonNumericCaptureExpression infixComma callArgumentsNoParenthesesKeywords
  private static boolean callArgumentsNoParenthesesMany_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesMany_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureExpression(b, l + 1);
    r = r && infixComma(b, l + 1);
    r = r && callArgumentsNoParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // callArgumentsNoParenthesesCommaExpression (infixComma callArgumentsNoParenthesesKeywords)?
  private static boolean callArgumentsNoParenthesesMany_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesMany_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = callArgumentsNoParenthesesCommaExpression(b, l + 1);
    r = r && callArgumentsNoParenthesesMany_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma callArgumentsNoParenthesesKeywords)?
  private static boolean callArgumentsNoParenthesesMany_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesMany_1_1")) return false;
    callArgumentsNoParenthesesMany_1_1_0(b, l + 1);
    return true;
  }

  // infixComma callArgumentsNoParenthesesKeywords
  private static boolean callArgumentsNoParenthesesMany_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesMany_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && callArgumentsNoParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // callArgumentsNoParenthesesMany |
  //                                                  noParenthesesStrict
  static boolean callArgumentsNoParenthesesManyStrict(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callArgumentsNoParenthesesManyStrict")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = callArgumentsNoParenthesesMany(b, l + 1);
    if (!r) r = noParenthesesStrict(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // capturePrefixOperator numeric
  public static boolean captureCharTokenOrNumberOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "captureCharTokenOrNumberOperation")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = capturePrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, m, CAPTURE_CHAR_TOKEN_OR_NUMBER_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // CAPTURE_OPERATOR EOL*
  public static boolean capturePrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "capturePrefixOperator")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CAPTURE_OPERATOR);
    r = r && capturePrefixOperator_1(b, l + 1);
    exit_section_(b, m, CAPTURE_PREFIX_OPERATOR, r);
    return r;
  }

  // EOL*
  private static boolean capturePrefixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "capturePrefixOperator_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "capturePrefixOperator_1", c)) break;
      c = current_position_(b);
    }
    return true;
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
  // decimalWholeNumber DECIMAL_MARK decimalWholeNumber (EXPONENT_MARK DUAL_OPERATOR? decimalWholeNumber)?
  public static boolean decimalFloat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat")) return false;
    if (!nextTokenIs(b, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = decimalWholeNumber(b, l + 1);
    r = r && consumeToken(b, DECIMAL_MARK);
    r = r && decimalWholeNumber(b, l + 1);
    r = r && decimalFloat_3(b, l + 1);
    exit_section_(b, m, DECIMAL_FLOAT, r);
    return r;
  }

  // (EXPONENT_MARK DUAL_OPERATOR? decimalWholeNumber)?
  private static boolean decimalFloat_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat_3")) return false;
    decimalFloat_3_0(b, l + 1);
    return true;
  }

  // EXPONENT_MARK DUAL_OPERATOR? decimalWholeNumber
  private static boolean decimalFloat_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPONENT_MARK);
    r = r && decimalFloat_3_0_1(b, l + 1);
    r = r && decimalWholeNumber(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DUAL_OPERATOR?
  private static boolean decimalFloat_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat_3_0_1")) return false;
    consumeToken(b, DUAL_OPERATOR);
    return true;
  }

  /* ********************************************************** */
  // decimalFloat |
  //                   decimalWholeNumber
  public static boolean decimalNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalNumber")) return false;
    if (!nextTokenIs(b, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, null);
    r = decimalFloat(b, l + 1);
    if (!r) r = decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, DECIMAL_NUMBER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // {VALID_DECIMAL_DIGITS} ({DECIMAL_SEPARATOR}? (INVALID_DECIMAL_DIGITS | VALID_DECIMAL_DIGITS))*
  public static boolean decimalWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber")) return false;
    if (!nextTokenIs(b, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VALID_DECIMAL_DIGITS);
    r = r && decimalWholeNumber_1(b, l + 1);
    exit_section_(b, m, DECIMAL_WHOLE_NUMBER, r);
    return r;
  }

  // ({DECIMAL_SEPARATOR}? (INVALID_DECIMAL_DIGITS | VALID_DECIMAL_DIGITS))*
  private static boolean decimalWholeNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!decimalWholeNumber_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "decimalWholeNumber_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // {DECIMAL_SEPARATOR}? (INVALID_DECIMAL_DIGITS | VALID_DECIMAL_DIGITS)
  private static boolean decimalWholeNumber_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = decimalWholeNumber_1_0_0(b, l + 1);
    r = r && decimalWholeNumber_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // {DECIMAL_SEPARATOR}?
  private static boolean decimalWholeNumber_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1_0_0")) return false;
    consumeToken(b, DECIMAL_SEPARATOR);
    return true;
  }

  // INVALID_DECIMAL_DIGITS | VALID_DECIMAL_DIGITS
  private static boolean decimalWholeNumber_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_DECIMAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_DECIMAL_DIGITS);
    exit_section_(b, m, null, r);
    return r;
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
  // infixSemicolon | EOL
  public static boolean endOfExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression")) return false;
    if (!nextTokenIs(b, "<end of expression>", EOL, SEMICOLON)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<end of expression>");
    r = infixSemicolon(b, l + 1);
    if (!r) r = consumeToken(b, EOL);
    exit_section_(b, l, m, END_OF_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureExpression
  static boolean expression(PsiBuilder b, int l) {
    return matchedNonNumericCaptureExpression(b, l + 1);
  }

  /* ********************************************************** */
  // expression (endOfExpression+ expression | adjacentExpression)*
  static boolean expressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && expressionList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (endOfExpression+ expression | adjacentExpression)*
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

  // endOfExpression+ expression | adjacentExpression
  private static boolean expressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expressionList_1_0_0(b, l + 1);
    if (!r) r = adjacentExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // endOfExpression+ expression
  private static boolean expressionList_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expressionList_1_0_0_0(b, l + 1);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // endOfExpression+
  private static boolean expressionList_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = endOfExpression(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!endOfExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expressionList_1_0_0_0", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* HAT_OPERATOR EOL*
  public static boolean hatInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hatInfixOperator")) return false;
    if (!nextTokenIs(b, "<^^^>", EOL, HAT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<^^^>");
    r = hatInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, HAT_OPERATOR);
    r = r && hatInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, HAT_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean hatInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hatInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "hatInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean hatInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hatInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "hatInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // atPrefixOperator (ALIAS | IDENTIFIER) |
  //                           atCharTokenOrNumberOperation |
  //                           captureCharTokenOrNumberOperation |
  //                           unaryCharTokenOrNumberOperation |
  //                           OPENING_PARENTHESIS infixSemicolon CLOSING_PARENTHESIS |
  //                           numeric |
  //                           list |
  //                           sigil |
  //                           atom
  static boolean headQualifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headQualifier")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = headQualifier_0(b, l + 1);
    if (!r) r = atCharTokenOrNumberOperation(b, l + 1);
    if (!r) r = captureCharTokenOrNumberOperation(b, l + 1);
    if (!r) r = unaryCharTokenOrNumberOperation(b, l + 1);
    if (!r) r = headQualifier_4(b, l + 1);
    if (!r) r = numeric(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = sigil(b, l + 1);
    if (!r) r = atom(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // atPrefixOperator (ALIAS | IDENTIFIER)
  private static boolean headQualifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headQualifier_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && headQualifier_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ALIAS | IDENTIFIER
  private static boolean headQualifier_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headQualifier_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ALIAS);
    if (!r) r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // OPENING_PARENTHESIS infixSemicolon CLOSING_PARENTHESIS
  private static boolean headQualifier_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headQualifier_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_PARENTHESIS);
    r = r && infixSemicolon(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX (HEXADECIMAL_WHOLE_NUMBER_BASE | OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE) (INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS)+
  public static boolean hexadecimalWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber")) return false;
    if (!nextTokenIs(b, BASE_WHOLE_NUMBER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, BASE_WHOLE_NUMBER_PREFIX);
    r = r && hexadecimalWholeNumber_1(b, l + 1);
    p = r; // pin = 2
    r = r && hexadecimalWholeNumber_2(b, l + 1);
    exit_section_(b, l, m, HEXADECIMAL_WHOLE_NUMBER, r, p, null);
    return r || p;
  }

  // HEXADECIMAL_WHOLE_NUMBER_BASE | OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE
  private static boolean hexadecimalWholeNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HEXADECIMAL_WHOLE_NUMBER_BASE);
    if (!r) r = consumeToken(b, OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS)+
  private static boolean hexadecimalWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hexadecimalWholeNumber_2_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!hexadecimalWholeNumber_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "hexadecimalWholeNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS
  private static boolean hexadecimalWholeNumber_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_HEXADECIMAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_HEXADECIMAL_DIGITS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COMMA EOL*
  static boolean infixComma(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixComma")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && infixComma_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean infixComma_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixComma_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "infixComma_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // EOL* DOT_OPERATOR EOL*
  public static boolean infixDotOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixDotOperator")) return false;
    if (!nextTokenIs(b, "<.>", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<.>");
    r = infixDotOperator_0(b, l + 1);
    r = r && consumeToken(b, DOT_OPERATOR);
    r = r && infixDotOperator_2(b, l + 1);
    exit_section_(b, l, m, INFIX_DOT_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean infixDotOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixDotOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "infixDotOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean infixDotOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixDotOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "infixDotOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // EOL* SEMICOLON EOL*
  static boolean infixSemicolon(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixSemicolon")) return false;
    if (!nextTokenIs(b, "", EOL, SEMICOLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixSemicolon_0(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && infixSemicolon_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean infixSemicolon_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixSemicolon_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "infixSemicolon_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean infixSemicolon_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixSemicolon_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "infixSemicolon_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // (interpolation | CHAR_LIST_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  public static boolean interpolatedCharListBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated char list body>");
    int c = current_position_(b);
    while (true) {
      if (!interpolatedCharListBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedCharListBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, INTERPOLATED_CHAR_LIST_BODY, true, false, null);
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
  public static boolean interpolatedStringBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated string body>");
    int c = current_position_(b);
    while (true) {
      if (!interpolatedStringBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedStringBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, INTERPOLATED_STRING_BODY, true, false, null);
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
  // keywordKey KEYWORD_PAIR_COLON EOL*
  static boolean keywordKeyColonEOL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordKeyColonEOL")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keywordKey(b, l + 1);
    r = r && consumeToken(b, KEYWORD_PAIR_COLON);
    r = r && keywordKeyColonEOL_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean keywordKeyColonEOL_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordKeyColonEOL_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "keywordKeyColonEOL_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // keywordKeyColonEOL keywordValue
  public static boolean keywordPair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordPair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<keyword pair>");
    r = keywordKeyColonEOL(b, l + 1);
    r = r && keywordValue(b, l + 1);
    exit_section_(b, l, m, KEYWORD_PAIR, r, false, null);
    return r;
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
  // OPENING_BRACKET EOL* (keywordPair (infixComma keywordPair)* COMMA?)? CLOSING_BRACKET
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

  // (keywordPair (infixComma keywordPair)* COMMA?)?
  private static boolean list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2")) return false;
    list_2_0(b, l + 1);
    return true;
  }

  // keywordPair (infixComma keywordPair)* COMMA?
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

  // (infixComma keywordPair)*
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

  // infixComma keywordPair
  private static boolean list_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && keywordPair(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // matchedAtOperation | matchedAtOperand
  static boolean matchedAtExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedAtOperation(b, l + 1);
    if (!r) r = matchedAtOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureOperation | matchedUnaryOperation | tailExpression | matchedExpression
  static boolean matchedAtOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedUnaryOperation(b, l + 1);
    if (!r) r = tailExpression(b, l + 1);
    if (!r) r = matchedExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // atPrefixOperator matchedAtOperand
  public static boolean matchedAtOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtOperation")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && matchedAtOperand(b, l + 1);
    exit_section_(b, m, MATCHED_AT_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesOneExpression |
  //                               accessExpression
  static boolean matchedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesOneExpression(b, l + 1);
    if (!r) r = accessExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedHatOperand matchedHatOperation*
  static boolean matchedHatExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedHatOperand(b, l + 1);
    r = r && matchedHatExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedHatOperation*
  private static boolean matchedHatExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedHatOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedHatExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureOperation | matchedUnaryExpression
  static boolean matchedHatOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedUnaryExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // hatInfixOperator matchedHatOperand
  public static boolean matchedHatOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatOperation")) return false;
    if (!nextTokenIs(b, "<matched hat operation>", EOL, HAT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched hat operation>");
    r = hatInfixOperator(b, l + 1);
    r = r && matchedHatOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_HAT_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedMultiplicationOperand matchedMultiplicationOperation*
  static boolean matchedMultiplicationExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedMultiplicationOperand(b, l + 1);
    r = r && matchedMultiplicationExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedMultiplicationOperation*
  private static boolean matchedMultiplicationExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedMultiplicationOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedMultiplicationExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureOperation | matchedHatExpression
  static boolean matchedMultiplicationOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedHatExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // multiplicationInfixOperator matchedMultiplicationOperand
  public static boolean matchedMultiplicationOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationOperation")) return false;
    if (!nextTokenIs(b, "<matched multiplication operation>", EOL, MULTIPLICATION_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched multiplication operation>");
    r = multiplicationInfixOperator(b, l + 1);
    r = r && matchedMultiplicationOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_MULTIPLICATION_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureOperation | matchedNonNumericCaptureOperand
  static boolean matchedNonNumericCaptureExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericCaptureExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedNonNumericCaptureOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureOperation | matchedMultiplicationExpression
  static boolean matchedNonNumericCaptureOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericCaptureOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedMultiplicationExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // nonNumericCapturePrefixOperator matchedNonNumericCaptureOperand
  public static boolean matchedNonNumericCaptureOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericCaptureOperation")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nonNumericCapturePrefixOperator(b, l + 1);
    r = r && matchedNonNumericCaptureOperand(b, l + 1);
    exit_section_(b, m, MATCHED_NON_NUMERIC_CAPTURE_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // matchedUnaryOperation | matchedUnaryOperand
  static boolean matchedUnaryExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedUnaryOperation(b, l + 1);
    if (!r) r = matchedUnaryOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureOperation | matchedUnaryOperation | matchedAtExpression
  static boolean matchedUnaryOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedUnaryOperation(b, l + 1);
    if (!r) r = matchedAtExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // unaryPrefixOperator matchedUnaryOperand
  public static boolean matchedUnaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryOperation")) return false;
    if (!nextTokenIs(b, "<matched unary operation>", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<matched unary operation>");
    r = unaryPrefixOperator(b, l + 1);
    r = r && matchedUnaryOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_UNARY_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qualifiedAlias |
  //                   // Must be after qualifiedAlias because atom can start qualifiedAlias
  //                   atom
  public static boolean maxExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<max expression>");
    r = qualifiedAlias(b, l + 1);
    if (!r) r = atom(b, l + 1);
    exit_section_(b, l, m, MAX_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // binaryString |
  //                            listString |
  //                            FALSE |
  //                            NIL |
  //                            TRUE |
  //                            IDENTIFIER |
  //                            ALIAS
  static boolean middleQualifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "middleQualifier")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = binaryString(b, l + 1);
    if (!r) r = listString(b, l + 1);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NIL);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, ALIAS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* MULTIPLICATION_OPERATOR EOL*
  public static boolean multiplicationInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicationInfixOperator")) return false;
    if (!nextTokenIs(b, "<*, />", EOL, MULTIPLICATION_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<*, />");
    r = multiplicationInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, MULTIPLICATION_OPERATOR);
    r = r && multiplicationInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, MULTIPLICATION_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean multiplicationInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicationInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "multiplicationInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean multiplicationInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicationInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "multiplicationInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // qualifiedIdentifier callArgumentsNoParenthesesManyStrict
  public static boolean noParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesCall")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses call>");
    r = qualifiedIdentifier(b, l + 1);
    r = r && callArgumentsNoParenthesesManyStrict(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_CALL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesCall
  public static boolean noParenthesesManyStrictNoParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyStrictNoParenthesesExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses many strict no parentheses expression>");
    r = noParenthesesCall(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qualifiedIdentifier !callArgumentsNoParenthesesManyStrict
  public static boolean noParenthesesOneExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesOneExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses one expression>");
    r = qualifiedIdentifier(b, l + 1);
    r = r && noParenthesesOneExpression_1(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_ONE_EXPRESSION, r, false, null);
    return r;
  }

  // !callArgumentsNoParenthesesManyStrict
  private static boolean noParenthesesOneExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesOneExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !callArgumentsNoParenthesesManyStrict(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                         OPENING_PARENTHESIS (
  //                                              callArgumentsNoParenthesesKeywords |
  //                                              callArgumentsNoParenthesesMany
  //                                             ) CLOSING_PARENTHESIS
  public static boolean noParenthesesStrict(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesStrict")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = emptyParentheses(b, l + 1);
    if (!r) r = noParenthesesStrict_1(b, l + 1);
    exit_section_(b, m, NO_PARENTHESES_STRICT, r);
    return r;
  }

  // OPENING_PARENTHESIS (
  //                                              callArgumentsNoParenthesesKeywords |
  //                                              callArgumentsNoParenthesesMany
  //                                             ) CLOSING_PARENTHESIS
  private static boolean noParenthesesStrict_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesStrict_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_PARENTHESIS);
    r = r && noParenthesesStrict_1_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, null, r);
    return r;
  }

  // callArgumentsNoParenthesesKeywords |
  //                                              callArgumentsNoParenthesesMany
  private static boolean noParenthesesStrict_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesStrict_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = callArgumentsNoParenthesesKeywords(b, l + 1);
    if (!r) r = callArgumentsNoParenthesesMany(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // capturePrefixOperator !numeric
  static boolean nonNumericCapturePrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonNumericCapturePrefixOperator")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = capturePrefixOperator(b, l + 1);
    r = r && nonNumericCapturePrefixOperator_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !numeric
  private static boolean nonNumericCapturePrefixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonNumericCapturePrefixOperator_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !numeric(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // binaryWholeNumber |
  //            decimalNumber |
  //            hexadecimalWholeNumber |
  //            octalWholeNumber |
  //            unknownBaseWholeNumber
  public static boolean number(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "number")) return false;
    if (!nextTokenIs(b, "<number>", BASE_WHOLE_NUMBER_PREFIX, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<number>");
    r = binaryWholeNumber(b, l + 1);
    if (!r) r = decimalNumber(b, l + 1);
    if (!r) r = hexadecimalWholeNumber(b, l + 1);
    if (!r) r = octalWholeNumber(b, l + 1);
    if (!r) r = unknownBaseWholeNumber(b, l + 1);
    exit_section_(b, l, m, NUMBER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CHAR_TOKEN | number
  static boolean numeric(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numeric")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CHAR_TOKEN);
    if (!r) r = number(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX OCTAL_WHOLE_NUMBER_BASE (INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS)+
  public static boolean octalWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber")) return false;
    if (!nextTokenIs(b, BASE_WHOLE_NUMBER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 2, BASE_WHOLE_NUMBER_PREFIX, OCTAL_WHOLE_NUMBER_BASE);
    p = r; // pin = 2
    r = r && octalWholeNumber_2(b, l + 1);
    exit_section_(b, l, m, OCTAL_WHOLE_NUMBER, r, p, null);
    return r || p;
  }

  // (INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS)+
  private static boolean octalWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = octalWholeNumber_2_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!octalWholeNumber_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "octalWholeNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS
  private static boolean octalWholeNumber_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_OCTAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_OCTAL_DIGITS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qualifier ALIAS
  public static boolean qualifiedAlias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifiedAlias")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<qualified alias>");
    r = qualifier(b, l + 1);
    r = r && consumeToken(b, ALIAS);
    exit_section_(b, l, m, QUALIFIED_ALIAS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qualifier IDENTIFIER
  public static boolean qualifiedIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifiedIdentifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<qualified identifier>");
    r = qualifier(b, l + 1);
    r = r && consumeToken(b, IDENTIFIER);
    exit_section_(b, l, m, QUALIFIED_IDENTIFIER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (headQualifier infixDotOperator)? (middleQualifier infixDotOperator)*
  static boolean qualifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifier")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qualifier_0(b, l + 1);
    r = r && qualifier_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (headQualifier infixDotOperator)?
  private static boolean qualifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifier_0")) return false;
    qualifier_0_0(b, l + 1);
    return true;
  }

  // headQualifier infixDotOperator
  private static boolean qualifier_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifier_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = headQualifier(b, l + 1);
    r = r && infixDotOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (middleQualifier infixDotOperator)*
  private static boolean qualifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifier_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!qualifier_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "qualifier_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // middleQualifier infixDotOperator
  private static boolean qualifier_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifier_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = middleQualifier(b, l + 1);
    r = r && infixDotOperator(b, l + 1);
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
  // emptyParentheses |
  //                            noParenthesesCall
  static boolean tailExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tailExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = emptyParentheses(b, l + 1);
    if (!r) r = noParenthesesCall(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // unaryPrefixOperator numeric
  public static boolean unaryCharTokenOrNumberOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryCharTokenOrNumberOperation")) return false;
    if (!nextTokenIs(b, "<unary char token or number operation>", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<unary char token or number operation>");
    r = unaryPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, l, m, UNARY_CHAR_TOKEN_OR_NUMBER_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (DUAL_OPERATOR | UNARY_OPERATOR) EOL*
  public static boolean unaryPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator")) return false;
    if (!nextTokenIs(b, "<+, -, !, ^, not, ~~~>", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<+, -, !, ^, not, ~~~>");
    r = unaryPrefixOperator_0(b, l + 1);
    r = r && unaryPrefixOperator_1(b, l + 1);
    exit_section_(b, l, m, UNARY_PREFIX_OPERATOR, r, false, null);
    return r;
  }

  // DUAL_OPERATOR | UNARY_OPERATOR
  private static boolean unaryPrefixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DUAL_OPERATOR);
    if (!r) r = consumeToken(b, UNARY_OPERATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean unaryPrefixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "unaryPrefixOperator_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX UNKNOWN_WHOLE_NUMBER_BASE INVALID_UNKNOWN_BASE_DIGITS+
  public static boolean unknownBaseWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknownBaseWholeNumber")) return false;
    if (!nextTokenIs(b, BASE_WHOLE_NUMBER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 2, BASE_WHOLE_NUMBER_PREFIX, UNKNOWN_WHOLE_NUMBER_BASE);
    p = r; // pin = 2
    r = r && unknownBaseWholeNumber_2(b, l + 1);
    exit_section_(b, l, m, UNKNOWN_BASE_WHOLE_NUMBER, r, p, null);
    return r || p;
  }

  // INVALID_UNKNOWN_BASE_DIGITS+
  private static boolean unknownBaseWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknownBaseWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_UNKNOWN_BASE_DIGITS);
    int c = current_position_(b);
    while (r) {
      if (!consumeToken(b, INVALID_UNKNOWN_BASE_DIGITS)) break;
      if (!empty_element_parsed_guard_(b, "unknownBaseWholeNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

}
