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
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ADDITION_INFIX_OPERATOR) {
      r = additionInfixOperator(b, 0);
    }
    else if (t == ADJACENT_EXPRESSION) {
      r = adjacentExpression(b, 0);
    }
    else if (t == ALIAS) {
      r = alias(b, 0);
    }
    else if (t == AND_INFIX_OPERATOR) {
      r = andInfixOperator(b, 0);
    }
    else if (t == ARROW_INFIX_OPERATOR) {
      r = arrowInfixOperator(b, 0);
    }
    else if (t == AT_NUMERIC_OPERATION) {
      r = atNumericOperation(b, 0);
    }
    else if (t == AT_PREFIX_OPERATOR) {
      r = atPrefixOperator(b, 0);
    }
    else if (t == ATOM) {
      r = atom(b, 0);
    }
    else if (t == ATOM_KEYWORD) {
      r = atomKeyword(b, 0);
    }
    else if (t == BINARY_DIGITS) {
      r = binaryDigits(b, 0);
    }
    else if (t == BINARY_WHOLE_NUMBER) {
      r = binaryWholeNumber(b, 0);
    }
    else if (t == CAPTURE_NUMERIC_OPERATION) {
      r = captureNumericOperation(b, 0);
    }
    else if (t == CAPTURE_PREFIX_OPERATOR) {
      r = capturePrefixOperator(b, 0);
    }
    else if (t == CHAR_LIST_HEREDOC) {
      r = charListHeredoc(b, 0);
    }
    else if (t == CHAR_LIST_LINE) {
      r = charListLine(b, 0);
    }
    else if (t == CHAR_TOKEN) {
      r = charToken(b, 0);
    }
    else if (t == COMPARISON_INFIX_OPERATOR) {
      r = comparisonInfixOperator(b, 0);
    }
    else if (t == DECIMAL_DIGITS) {
      r = decimalDigits(b, 0);
    }
    else if (t == DECIMAL_FLOAT) {
      r = decimalFloat(b, 0);
    }
    else if (t == DECIMAL_FLOAT_EXPONENT) {
      r = decimalFloatExponent(b, 0);
    }
    else if (t == DECIMAL_FLOAT_EXPONENT_SIGN) {
      r = decimalFloatExponentSign(b, 0);
    }
    else if (t == DECIMAL_FLOAT_FRACTIONAL) {
      r = decimalFloatFractional(b, 0);
    }
    else if (t == DECIMAL_FLOAT_INTEGRAL) {
      r = decimalFloatIntegral(b, 0);
    }
    else if (t == DECIMAL_WHOLE_NUMBER) {
      r = decimalWholeNumber(b, 0);
    }
    else if (t == DOT_INFIX_OPERATOR) {
      r = dotInfixOperator(b, 0);
    }
    else if (t == EMPTY_BLOCK) {
      r = emptyBlock(b, 0);
    }
    else if (t == EMPTY_PARENTHESES) {
      r = emptyParentheses(b, 0);
    }
    else if (t == ENCLOSED_HEXADECIMAL_ESCAPE_SEQUENCE) {
      r = enclosedHexadecimalEscapeSequence(b, 0);
    }
    else if (t == END_OF_EXPRESSION) {
      r = endOfExpression(b, 0);
    }
    else if (t == ESCAPED_CHARACTER) {
      r = escapedCharacter(b, 0);
    }
    else if (t == ESCAPED_EOL) {
      r = escapedEOL(b, 0);
    }
    else if (t == HAT_INFIX_OPERATOR) {
      r = hatInfixOperator(b, 0);
    }
    else if (t == HEREDOC_LINE_PREFIX) {
      r = heredocLinePrefix(b, 0);
    }
    else if (t == HEREDOC_PREFIX) {
      r = heredocPrefix(b, 0);
    }
    else if (t == HEXADECIMAL_DIGITS) {
      r = hexadecimalDigits(b, 0);
    }
    else if (t == HEXADECIMAL_ESCAPE_SEQUENCE) {
      r = hexadecimalEscapeSequence(b, 0);
    }
    else if (t == HEXADECIMAL_WHOLE_NUMBER) {
      r = hexadecimalWholeNumber(b, 0);
    }
    else if (t == IN_INFIX_OPERATOR) {
      r = inInfixOperator(b, 0);
    }
    else if (t == IN_MATCH_INFIX_OPERATOR) {
      r = inMatchInfixOperator(b, 0);
    }
    else if (t == INTERPOLATED_CHAR_LIST_BODY) {
      r = interpolatedCharListBody(b, 0);
    }
    else if (t == INTERPOLATED_CHAR_LIST_HEREDOC_LINE) {
      r = interpolatedCharListHeredocLine(b, 0);
    }
    else if (t == INTERPOLATED_CHAR_LIST_SIGIL_HEREDOC) {
      r = interpolatedCharListSigilHeredoc(b, 0);
    }
    else if (t == INTERPOLATED_CHAR_LIST_SIGIL_LINE) {
      r = interpolatedCharListSigilLine(b, 0);
    }
    else if (t == INTERPOLATED_REGEX_BODY) {
      r = interpolatedRegexBody(b, 0);
    }
    else if (t == INTERPOLATED_REGEX_HEREDOC) {
      r = interpolatedRegexHeredoc(b, 0);
    }
    else if (t == INTERPOLATED_REGEX_HEREDOC_LINE) {
      r = interpolatedRegexHeredocLine(b, 0);
    }
    else if (t == INTERPOLATED_REGEX_LINE) {
      r = interpolatedRegexLine(b, 0);
    }
    else if (t == INTERPOLATED_SIGIL_BODY) {
      r = interpolatedSigilBody(b, 0);
    }
    else if (t == INTERPOLATED_SIGIL_HEREDOC) {
      r = interpolatedSigilHeredoc(b, 0);
    }
    else if (t == INTERPOLATED_SIGIL_HEREDOC_LINE) {
      r = interpolatedSigilHeredocLine(b, 0);
    }
    else if (t == INTERPOLATED_SIGIL_LINE) {
      r = interpolatedSigilLine(b, 0);
    }
    else if (t == INTERPOLATED_STRING_BODY) {
      r = interpolatedStringBody(b, 0);
    }
    else if (t == INTERPOLATED_STRING_HEREDOC_LINE) {
      r = interpolatedStringHeredocLine(b, 0);
    }
    else if (t == INTERPOLATED_STRING_SIGIL_HEREDOC) {
      r = interpolatedStringSigilHeredoc(b, 0);
    }
    else if (t == INTERPOLATED_STRING_SIGIL_LINE) {
      r = interpolatedStringSigilLine(b, 0);
    }
    else if (t == INTERPOLATED_WORDS_BODY) {
      r = interpolatedWordsBody(b, 0);
    }
    else if (t == INTERPOLATED_WORDS_HEREDOC) {
      r = interpolatedWordsHeredoc(b, 0);
    }
    else if (t == INTERPOLATED_WORDS_HEREDOC_LINE) {
      r = interpolatedWordsHeredocLine(b, 0);
    }
    else if (t == INTERPOLATED_WORDS_LINE) {
      r = interpolatedWordsLine(b, 0);
    }
    else if (t == INTERPOLATION) {
      r = interpolation(b, 0);
    }
    else if (t == KEYWORD_KEY) {
      r = keywordKey(b, 0);
    }
    else if (t == KEYWORD_VALUE) {
      r = keywordValue(b, 0);
    }
    else if (t == LIST) {
      r = list(b, 0);
    }
    else if (t == LIST_KEYWORD_PAIR) {
      r = listKeywordPair(b, 0);
    }
    else if (t == LITERAL_CHAR_LIST_BODY) {
      r = literalCharListBody(b, 0);
    }
    else if (t == LITERAL_CHAR_LIST_HEREDOC_LINE) {
      r = literalCharListHeredocLine(b, 0);
    }
    else if (t == LITERAL_CHAR_LIST_SIGIL_HEREDOC) {
      r = literalCharListSigilHeredoc(b, 0);
    }
    else if (t == LITERAL_CHAR_LIST_SIGIL_LINE) {
      r = literalCharListSigilLine(b, 0);
    }
    else if (t == LITERAL_REGEX_BODY) {
      r = literalRegexBody(b, 0);
    }
    else if (t == LITERAL_REGEX_HEREDOC) {
      r = literalRegexHeredoc(b, 0);
    }
    else if (t == LITERAL_REGEX_HEREDOC_LINE) {
      r = literalRegexHeredocLine(b, 0);
    }
    else if (t == LITERAL_REGEX_LINE) {
      r = literalRegexLine(b, 0);
    }
    else if (t == LITERAL_SIGIL_BODY) {
      r = literalSigilBody(b, 0);
    }
    else if (t == LITERAL_SIGIL_HEREDOC) {
      r = literalSigilHeredoc(b, 0);
    }
    else if (t == LITERAL_SIGIL_HEREDOC_LINE) {
      r = literalSigilHeredocLine(b, 0);
    }
    else if (t == LITERAL_SIGIL_LINE) {
      r = literalSigilLine(b, 0);
    }
    else if (t == LITERAL_STRING_BODY) {
      r = literalStringBody(b, 0);
    }
    else if (t == LITERAL_STRING_HEREDOC_LINE) {
      r = literalStringHeredocLine(b, 0);
    }
    else if (t == LITERAL_STRING_SIGIL_HEREDOC) {
      r = literalStringSigilHeredoc(b, 0);
    }
    else if (t == LITERAL_STRING_SIGIL_LINE) {
      r = literalStringSigilLine(b, 0);
    }
    else if (t == LITERAL_WORDS_BODY) {
      r = literalWordsBody(b, 0);
    }
    else if (t == LITERAL_WORDS_HEREDOC) {
      r = literalWordsHeredoc(b, 0);
    }
    else if (t == LITERAL_WORDS_HEREDOC_LINE) {
      r = literalWordsHeredocLine(b, 0);
    }
    else if (t == LITERAL_WORDS_LINE) {
      r = literalWordsLine(b, 0);
    }
    else if (t == MATCH_INFIX_OPERATOR) {
      r = matchInfixOperator(b, 0);
    }
    else if (t == MATCHED_ADDITION_OPERATION) {
      r = matchedAdditionOperation(b, 0);
    }
    else if (t == MATCHED_AND_OPERATION) {
      r = matchedAndOperation(b, 0);
    }
    else if (t == MATCHED_ARROW_OPERATION) {
      r = matchedArrowOperation(b, 0);
    }
    else if (t == MATCHED_CALL_OPERATION) {
      r = matchedCallOperation(b, 0);
    }
    else if (t == MATCHED_CAPTURE_NON_NUMERIC_OPERATION) {
      r = matchedCaptureNonNumericOperation(b, 0);
    }
    else if (t == MATCHED_COMPARISON_OPERATION) {
      r = matchedComparisonOperation(b, 0);
    }
    else if (t == MATCHED_DOT_OPERATION) {
      r = matchedDotOperation(b, 0);
    }
    else if (t == MATCHED_HAT_OPERATION) {
      r = matchedHatOperation(b, 0);
    }
    else if (t == MATCHED_IN_MATCH_OPERATION) {
      r = matchedInMatchOperation(b, 0);
    }
    else if (t == MATCHED_IN_OPERATION) {
      r = matchedInOperation(b, 0);
    }
    else if (t == MATCHED_MATCH_OPERATION) {
      r = matchedMatchOperation(b, 0);
    }
    else if (t == MATCHED_MULTIPLICATION_OPERATION) {
      r = matchedMultiplicationOperation(b, 0);
    }
    else if (t == MATCHED_NON_NUMERIC_AT_OPERATION) {
      r = matchedNonNumericAtOperation(b, 0);
    }
    else if (t == MATCHED_NON_NUMERIC_UNARY_OPERATION) {
      r = matchedNonNumericUnaryOperation(b, 0);
    }
    else if (t == MATCHED_OR_OPERATION) {
      r = matchedOrOperation(b, 0);
    }
    else if (t == MATCHED_PIPE_OPERATION) {
      r = matchedPipeOperation(b, 0);
    }
    else if (t == MATCHED_RELATIONAL_OPERATION) {
      r = matchedRelationalOperation(b, 0);
    }
    else if (t == MATCHED_TWO_OPERATION) {
      r = matchedTwoOperation(b, 0);
    }
    else if (t == MATCHED_TYPE_OPERATION) {
      r = matchedTypeOperation(b, 0);
    }
    else if (t == MATCHED_WHEN_OPERATION) {
      r = matchedWhenOperation(b, 0);
    }
    else if (t == MULTIPLICATION_INFIX_OPERATOR) {
      r = multiplicationInfixOperator(b, 0);
    }
    else if (t == NO_PARENTHESES_EXPRESSION) {
      r = noParenthesesExpression(b, 0);
    }
    else if (t == NO_PARENTHESES_FIRST_POSITIONAL) {
      r = noParenthesesFirstPositional(b, 0);
    }
    else if (t == NO_PARENTHESES_KEYWORD_PAIR) {
      r = noParenthesesKeywordPair(b, 0);
    }
    else if (t == NO_PARENTHESES_KEYWORDS) {
      r = noParenthesesKeywords(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_ARGUMENTS) {
      r = noParenthesesManyArguments(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_ARGUMENTS_UNQUALIFIED_IDENTIFIER) {
      r = noParenthesesManyArgumentsUnqualifiedIdentifier(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_POSITIONAL_AND_MAYBE_KEYWORDS_ARGUMENTS) {
      r = noParenthesesManyPositionalAndMaybeKeywordsArguments(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION) {
      r = noParenthesesManyStrictNoParenthesesExpression(b, 0);
    }
    else if (t == NO_PARENTHESES_NO_ARGUMENTS_UNQUALIFIED_CALL_OR_VARIABLE) {
      r = noParenthesesNoArgumentsUnqualifiedCallOrVariable(b, 0);
    }
    else if (t == NO_PARENTHESES_ONE_POSITIONAL_AND_KEYWORDS_ARGUMENTS) {
      r = noParenthesesOnePositionalAndKeywordsArguments(b, 0);
    }
    else if (t == NO_PARENTHESES_STRICT) {
      r = noParenthesesStrict(b, 0);
    }
    else if (t == OCTAL_DIGITS) {
      r = octalDigits(b, 0);
    }
    else if (t == OCTAL_WHOLE_NUMBER) {
      r = octalWholeNumber(b, 0);
    }
    else if (t == OPEN_HEXADECIMAL_ESCAPE_SEQUENCE) {
      r = openHexadecimalEscapeSequence(b, 0);
    }
    else if (t == OR_INFIX_OPERATOR) {
      r = orInfixOperator(b, 0);
    }
    else if (t == PIPE_INFIX_OPERATOR) {
      r = pipeInfixOperator(b, 0);
    }
    else if (t == RELATIONAL_INFIX_OPERATOR) {
      r = relationalInfixOperator(b, 0);
    }
    else if (t == SIGIL_MODIFIERS) {
      r = sigilModifiers(b, 0);
    }
    else if (t == STRING_HEREDOC) {
      r = stringHeredoc(b, 0);
    }
    else if (t == STRING_LINE) {
      r = stringLine(b, 0);
    }
    else if (t == TWO_INFIX_OPERATOR) {
      r = twoInfixOperator(b, 0);
    }
    else if (t == TYPE_INFIX_OPERATOR) {
      r = typeInfixOperator(b, 0);
    }
    else if (t == UNARY_NUMERIC_OPERATION) {
      r = unaryNumericOperation(b, 0);
    }
    else if (t == UNARY_PREFIX_OPERATOR) {
      r = unaryPrefixOperator(b, 0);
    }
    else if (t == UNKNOWN_BASE_DIGITS) {
      r = unknownBaseDigits(b, 0);
    }
    else if (t == UNKNOWN_BASE_WHOLE_NUMBER) {
      r = unknownBaseWholeNumber(b, 0);
    }
    else if (t == UNQUALIFIED_NO_PARENTHESES_MANY_ARGUMENTS_CALL) {
      r = unqualifiedNoParenthesesManyArgumentsCall(b, 0);
    }
    else if (t == WHEN_INFIX_OPERATOR) {
      r = whenInfixOperator(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return elixirFile(b, l + 1);
  }

  /* ********************************************************** */
  // atNumericOperation |
  //                              captureNumericOperation |
  //                              unaryNumericOperation |
  //                              emptyBlock |
  //                              numeric |
  //                              list |
  //                              binaryString |
  //                              listString |
  //                              interpolatedCharListSigilLine |
  //                              interpolatedCharListSigilHeredoc |
  //                              interpolatedRegexHeredoc |
  //                              interpolatedSigilHeredoc |
  //                              interpolatedStringSigilHeredoc |
  //                              interpolatedWordsHeredoc |
  //                              interpolatedWordsLine |
  //                              interpolatedRegexLine |
  //                              interpolatedSigilLine |
  //                              interpolatedStringSigilLine |
  //                              literalCharListSigilLine |
  //                              literalCharListSigilHeredoc |
  //                              literalRegexHeredoc |
  //                              literalSigilHeredoc |
  //                              literalStringSigilHeredoc |
  //                              literalWordsHeredoc |
  //                              literalRegexLine |
  //                              literalSigilLine |
  //                              literalStringSigilLine |
  //                              literalWordsLine |
  //                              atomKeyword |
  //                              atom |
  //                              alias
  static boolean accessExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atNumericOperation(b, l + 1);
    if (!r) r = captureNumericOperation(b, l + 1);
    if (!r) r = unaryNumericOperation(b, l + 1);
    if (!r) r = emptyBlock(b, l + 1);
    if (!r) r = numeric(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = binaryString(b, l + 1);
    if (!r) r = listString(b, l + 1);
    if (!r) r = interpolatedCharListSigilLine(b, l + 1);
    if (!r) r = interpolatedCharListSigilHeredoc(b, l + 1);
    if (!r) r = interpolatedRegexHeredoc(b, l + 1);
    if (!r) r = interpolatedSigilHeredoc(b, l + 1);
    if (!r) r = interpolatedStringSigilHeredoc(b, l + 1);
    if (!r) r = interpolatedWordsHeredoc(b, l + 1);
    if (!r) r = interpolatedWordsLine(b, l + 1);
    if (!r) r = interpolatedRegexLine(b, l + 1);
    if (!r) r = interpolatedSigilLine(b, l + 1);
    if (!r) r = interpolatedStringSigilLine(b, l + 1);
    if (!r) r = literalCharListSigilLine(b, l + 1);
    if (!r) r = literalCharListSigilHeredoc(b, l + 1);
    if (!r) r = literalRegexHeredoc(b, l + 1);
    if (!r) r = literalSigilHeredoc(b, l + 1);
    if (!r) r = literalStringSigilHeredoc(b, l + 1);
    if (!r) r = literalWordsHeredoc(b, l + 1);
    if (!r) r = literalRegexLine(b, l + 1);
    if (!r) r = literalSigilLine(b, l + 1);
    if (!r) r = literalStringSigilLine(b, l + 1);
    if (!r) r = literalWordsLine(b, l + 1);
    if (!r) r = atomKeyword(b, l + 1);
    if (!r) r = atom(b, l + 1);
    if (!r) r = alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DUAL_OPERATOR EOL*
  public static boolean additionInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additionInfixOperator")) return false;
    if (!nextTokenIs(b, DUAL_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DUAL_OPERATOR);
    r = r && additionInfixOperator_1(b, l + 1);
    exit_section_(b, m, ADDITION_INFIX_OPERATOR, r);
    return r;
  }

  // EOL*
  private static boolean additionInfixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additionInfixOperator_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "additionInfixOperator_1", c)) break;
      c = current_position_(b);
    }
    return true;
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
  // ALIAS_TOKEN
  public static boolean alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias")) return false;
    if (!nextTokenIs(b, ALIAS_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ALIAS_TOKEN);
    exit_section_(b, m, ALIAS, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* AND_OPERATOR EOL*
  public static boolean andInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andInfixOperator")) return false;
    if (!nextTokenIs(b, "<&&, &&&, and>", AND_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<&&, &&&, and>");
    r = andInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, AND_OPERATOR);
    r = r && andInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, AND_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean andInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "andInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean andInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "andInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // EOL* ARROW_OPERATOR EOL*
  public static boolean arrowInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrowInfixOperator")) return false;
    if (!nextTokenIs(b, "<<~, |>, ~>, <<<, <<~, <|>, <~>, >>>, ~>>>", ARROW_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<<~, |>, ~>, <<<, <<~, <|>, <~>, >>>, ~>>>");
    r = arrowInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, ARROW_OPERATOR);
    r = r && arrowInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, ARROW_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean arrowInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrowInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "arrowInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean arrowInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrowInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "arrowInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // atPrefixOperator numeric
  public static boolean atNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atNumericOperation")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, m, AT_NUMERIC_OPERATION, r);
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
  // FALSE | NIL | TRUE
  public static boolean atomKeyword(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atomKeyword")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<false, nil, true>");
    r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NIL);
    if (!r) r = consumeToken(b, TRUE);
    exit_section_(b, l, m, ATOM_KEYWORD, r, false, null);
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
  // stringLine | stringHeredoc
  static boolean binaryString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryString")) return false;
    if (!nextTokenIs(b, "", STRING_HEREDOC_PROMOTER, STRING_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stringLine(b, l + 1);
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
  // capturePrefixOperator numeric
  public static boolean captureNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "captureNumericOperation")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = capturePrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, m, CAPTURE_NUMERIC_OPERATION, r);
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
  // CHAR_LIST_HEREDOC_PROMOTER EOL
  //                     interpolatedCharListHeredocLine*
  //                     heredocPrefix CHAR_LIST_HEREDOC_TERMINATOR
  public static boolean charListHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredoc")) return false;
    if (!nextTokenIs(b, CHAR_LIST_HEREDOC_PROMOTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 1, CHAR_LIST_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 1
    r = r && report_error_(b, charListHeredoc_2(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && consumeToken(b, CHAR_LIST_HEREDOC_TERMINATOR) && r;
    exit_section_(b, l, m, CHAR_LIST_HEREDOC, r, p, null);
    return r || p;
  }

  // interpolatedCharListHeredocLine*
  private static boolean charListHeredoc_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredoc_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedCharListHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "charListHeredoc_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // CHAR_LIST_PROMOTER interpolatedCharListBody CHAR_LIST_TERMINATOR
  public static boolean charListLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListLine")) return false;
    if (!nextTokenIs(b, CHAR_LIST_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CHAR_LIST_PROMOTER);
    r = r && interpolatedCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_TERMINATOR);
    exit_section_(b, m, CHAR_LIST_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // CHAR_TOKENIZER (CHAR_LIST_FRAGMENT | escapeSequence)
  public static boolean charToken(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charToken")) return false;
    if (!nextTokenIs(b, CHAR_TOKENIZER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CHAR_TOKENIZER);
    r = r && charToken_1(b, l + 1);
    exit_section_(b, m, CHAR_TOKEN, r);
    return r;
  }

  // CHAR_LIST_FRAGMENT | escapeSequence
  private static boolean charToken_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charToken_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = escapeSequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* COMPARISON_OPERATOR EOL*
  public static boolean comparisonInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparisonInfixOperator")) return false;
    if (!nextTokenIs(b, "<!=, ==, =~, !==, ===>", COMPARISON_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<!=, ==, =~, !==, ===>");
    r = comparisonInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, COMPARISON_OPERATOR);
    r = r && comparisonInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, COMPARISON_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean comparisonInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparisonInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "comparisonInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean comparisonInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparisonInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "comparisonInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // emptyParentheses
  static boolean containerExpression(PsiBuilder b, int l) {
    return emptyParentheses(b, l + 1);
  }

  /* ********************************************************** */
  // INVALID_DECIMAL_DIGITS | VALID_DECIMAL_DIGITS
  public static boolean decimalDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalDigits")) return false;
    if (!nextTokenIs(b, "<decimal digits>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<decimal digits>");
    r = consumeToken(b, INVALID_DECIMAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_DECIMAL_DIGITS);
    exit_section_(b, l, m, DECIMAL_DIGITS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // decimalFloatIntegral DECIMAL_MARK decimalFloatFractional (EXPONENT_MARK decimalFloatExponent)?
  public static boolean decimalFloat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat")) return false;
    if (!nextTokenIs(b, "<decimal float>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<decimal float>");
    r = decimalFloatIntegral(b, l + 1);
    r = r && consumeToken(b, DECIMAL_MARK);
    r = r && decimalFloatFractional(b, l + 1);
    r = r && decimalFloat_3(b, l + 1);
    exit_section_(b, l, m, DECIMAL_FLOAT, r, false, null);
    return r;
  }

  // (EXPONENT_MARK decimalFloatExponent)?
  private static boolean decimalFloat_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat_3")) return false;
    decimalFloat_3_0(b, l + 1);
    return true;
  }

  // EXPONENT_MARK decimalFloatExponent
  private static boolean decimalFloat_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPONENT_MARK);
    r = r && decimalFloatExponent(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // decimalFloatExponentSign decimalWholeNumber
  public static boolean decimalFloatExponent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatExponent")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<decimal float exponent>");
    r = decimalFloatExponentSign(b, l + 1);
    r = r && decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, DECIMAL_FLOAT_EXPONENT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DUAL_OPERATOR?
  public static boolean decimalFloatExponentSign(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatExponentSign")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<decimal float exponent sign>");
    consumeToken(b, DUAL_OPERATOR);
    exit_section_(b, l, m, DECIMAL_FLOAT_EXPONENT_SIGN, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // decimalWholeNumber
  public static boolean decimalFloatFractional(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatFractional")) return false;
    if (!nextTokenIs(b, "<decimal float fractional>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<decimal float fractional>");
    r = decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, DECIMAL_FLOAT_FRACTIONAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // decimalWholeNumber
  public static boolean decimalFloatIntegral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatIntegral")) return false;
    if (!nextTokenIs(b, "<decimal float integral>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<decimal float integral>");
    r = decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, DECIMAL_FLOAT_INTEGRAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // decimalDigits (DECIMAL_SEPARATOR? decimalDigits)*
  public static boolean decimalWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber")) return false;
    if (!nextTokenIs(b, "<decimal whole number>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<decimal whole number>");
    r = decimalDigits(b, l + 1);
    r = r && decimalWholeNumber_1(b, l + 1);
    exit_section_(b, l, m, DECIMAL_WHOLE_NUMBER, r, false, null);
    return r;
  }

  // (DECIMAL_SEPARATOR? decimalDigits)*
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

  // DECIMAL_SEPARATOR? decimalDigits
  private static boolean decimalWholeNumber_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = decimalWholeNumber_1_0_0(b, l + 1);
    r = r && decimalDigits(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DECIMAL_SEPARATOR?
  private static boolean decimalWholeNumber_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1_0_0")) return false;
    consumeToken(b, DECIMAL_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // EOL* DOT_OPERATOR EOL*
  public static boolean dotInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dotInfixOperator")) return false;
    if (!nextTokenIs(b, "<.>", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<.>");
    r = dotInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, DOT_OPERATOR);
    r = r && dotInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, DOT_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean dotInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dotInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "dotInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean dotInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dotInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "dotInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
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
  // OPENING_PARENTHESIS infixSemicolon CLOSING_PARENTHESIS
  public static boolean emptyBlock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyBlock")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_PARENTHESIS);
    r = r && infixSemicolon(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, EMPTY_BLOCK, r);
    return r;
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
  // OPENING_CURLY VALID_HEXADECIMAL_DIGITS CLOSING_CURLY
  public static boolean enclosedHexadecimalEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enclosedHexadecimalEscapeSequence")) return false;
    if (!nextTokenIs(b, OPENING_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 1, OPENING_CURLY, VALID_HEXADECIMAL_DIGITS, CLOSING_CURLY);
    p = r; // pin = 1
    exit_section_(b, l, m, ENCLOSED_HEXADECIMAL_ESCAPE_SEQUENCE, r, p, null);
    return r || p;
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
  // hexadecimalEscapeSequence |
  //                            escapedEOL |
  //                            /* Must be last so that ESCAPE ('\') can be pinned in escapedCharacter without excluding
  //                               ("\x") in hexadecimalEscapeSequence  */
  //                            escapedCharacter
  static boolean escapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hexadecimalEscapeSequence(b, l + 1);
    if (!r) r = escapedEOL(b, l + 1);
    if (!r) r = escapedCharacter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ESCAPE ESCAPED_CHARACTER_TOKEN
  public static boolean escapedCharacter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escapedCharacter")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 1, ESCAPE, ESCAPED_CHARACTER_TOKEN);
    p = r; // pin = 1
    exit_section_(b, l, m, ESCAPED_CHARACTER, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ESCAPE EOL
  public static boolean escapedEOL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escapedEOL")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ESCAPE, EOL);
    exit_section_(b, m, ESCAPED_EOL, r);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                        unqualifiedNoParenthesesManyArgumentsCall |
  //                        matchedExpression
  static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = emptyParentheses(b, l + 1);
    if (!r) r = unqualifiedNoParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = matchedExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // HEREDOC_LINE_WHITE_SPACE_TOKEN?
  public static boolean heredocLinePrefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heredocLinePrefix")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<heredoc line prefix>");
    consumeToken(b, HEREDOC_LINE_WHITE_SPACE_TOKEN);
    exit_section_(b, l, m, HEREDOC_LINE_PREFIX, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // HEREDOC_PREFIX_WHITE_SPACE?
  public static boolean heredocPrefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heredocPrefix")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<heredoc prefix>");
    consumeToken(b, HEREDOC_PREFIX_WHITE_SPACE);
    exit_section_(b, l, m, HEREDOC_PREFIX, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS
  public static boolean hexadecimalDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalDigits")) return false;
    if (!nextTokenIs(b, "<hexadecimal digits>", INVALID_HEXADECIMAL_DIGITS, VALID_HEXADECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<hexadecimal digits>");
    r = consumeToken(b, INVALID_HEXADECIMAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_HEXADECIMAL_DIGITS);
    exit_section_(b, l, m, HEXADECIMAL_DIGITS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ESCAPE HEXADECIMAL_WHOLE_NUMBER_BASE (openHexadecimalEscapeSequence | enclosedHexadecimalEscapeSequence)
  public static boolean hexadecimalEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalEscapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 2, ESCAPE, HEXADECIMAL_WHOLE_NUMBER_BASE);
    p = r; // pin = 2
    r = r && hexadecimalEscapeSequence_2(b, l + 1);
    exit_section_(b, l, m, HEXADECIMAL_ESCAPE_SEQUENCE, r, p, null);
    return r || p;
  }

  // openHexadecimalEscapeSequence | enclosedHexadecimalEscapeSequence
  private static boolean hexadecimalEscapeSequence_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalEscapeSequence_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = openHexadecimalEscapeSequence(b, l + 1);
    if (!r) r = enclosedHexadecimalEscapeSequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX (HEXADECIMAL_WHOLE_NUMBER_BASE | OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE) hexadecimalDigits+
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

  // hexadecimalDigits+
  private static boolean hexadecimalWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hexadecimalDigits(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!hexadecimalDigits(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "hexadecimalWholeNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* IN_OPERATOR EOL*
  public static boolean inInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inInfixOperator")) return false;
    if (!nextTokenIs(b, "<in>", EOL, IN_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<in>");
    r = inInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, IN_OPERATOR);
    r = r && inInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, IN_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean inInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean inInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // EOL* IN_MATCH_OPERATOR EOL*
  public static boolean inMatchInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inMatchInfixOperator")) return false;
    if (!nextTokenIs(b, "<<-, \\\\>", EOL, IN_MATCH_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<<-, \\\\>");
    r = inMatchInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, IN_MATCH_OPERATOR);
    r = r && inMatchInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, IN_MATCH_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean inMatchInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inMatchInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inMatchInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean inMatchInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inMatchInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inMatchInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
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
  // (interpolation | CHAR_LIST_FRAGMENT | escapeSequence)*
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

  // interpolation | CHAR_LIST_FRAGMENT | escapeSequence
  private static boolean interpolatedCharListBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = escapeSequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedCharListBody EOL
  public static boolean interpolatedCharListHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated char list heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedCharListBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, INTERPOLATED_CHAR_LIST_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_HEREDOC_PROMOTER EOL
  //                                      interpolatedCharListHeredocLine*
  //                                      heredocPrefix CHAR_LIST_SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean interpolatedCharListSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, interpolatedCharListSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, CHAR_LIST_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, INTERPOLATED_CHAR_LIST_SIGIL_HEREDOC, r, p, null);
    return r || p;
  }

  // interpolatedCharListHeredocLine*
  private static boolean interpolatedCharListSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigilHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedCharListHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedCharListSigilHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER interpolatedCharListBody CHAR_LIST_SIGIL_TERMINATOR sigilModifiers
  public static boolean interpolatedCharListSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    r = r && interpolatedCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_SIGIL_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, INTERPOLATED_CHAR_LIST_SIGIL_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | REGEX_FRAGMENT | escapeSequence)*
  public static boolean interpolatedRegexBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated regex body>");
    int c = current_position_(b);
    while (true) {
      if (!interpolatedRegexBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedRegexBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, INTERPOLATED_REGEX_BODY, true, false, null);
    return true;
  }

  // interpolation | REGEX_FRAGMENT | escapeSequence
  private static boolean interpolatedRegexBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, REGEX_FRAGMENT);
    if (!r) r = escapeSequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_HEREDOC_PROMOTER EOL
  //                              interpolatedRegexHeredocLine*
  //                              heredocPrefix REGEX_HEREDOC_TERMINATOR sigilModifiers
  public static boolean interpolatedRegexHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, interpolatedRegexHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, REGEX_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, INTERPOLATED_REGEX_HEREDOC, r, p, null);
    return r || p;
  }

  // interpolatedRegexHeredocLine*
  private static boolean interpolatedRegexHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedRegexHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedRegexHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedRegexBody EOL
  public static boolean interpolatedRegexHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated regex heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedRegexBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, INTERPOLATED_REGEX_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_PROMOTER interpolatedRegexBody REGEX_TERMINATOR sigilModifiers
  public static boolean interpolatedRegexLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_PROMOTER);
    r = r && interpolatedRegexBody(b, l + 1);
    r = r && consumeToken(b, REGEX_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, INTERPOLATED_REGEX_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | SIGIL_FRAGMENT | escapeSequence)*
  public static boolean interpolatedSigilBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated sigil body>");
    int c = current_position_(b);
    while (true) {
      if (!interpolatedSigilBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedSigilBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, INTERPOLATED_SIGIL_BODY, true, false, null);
    return true;
  }

  // interpolation | SIGIL_FRAGMENT | escapeSequence
  private static boolean interpolatedSigilBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, SIGIL_FRAGMENT);
    if (!r) r = escapeSequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                              interpolatedSigilHeredocLine*
  //                              heredocPrefix SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean interpolatedSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, interpolatedSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, INTERPOLATED_SIGIL_HEREDOC, r, p, null);
    return r || p;
  }

  // interpolatedSigilHeredocLine*
  private static boolean interpolatedSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedSigilHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedSigilHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedSigilBody EOL
  public static boolean interpolatedSigilHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated sigil heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedSigilBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, INTERPOLATED_SIGIL_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_PROMOTER interpolatedSigilBody SIGIL_TERMINATOR sigilModifiers
  public static boolean interpolatedSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_PROMOTER);
    r = r && interpolatedSigilBody(b, l + 1);
    r = r && consumeToken(b, SIGIL_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, INTERPOLATED_SIGIL_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | STRING_FRAGMENT | escapeSequence)*
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

  // interpolation | STRING_FRAGMENT | escapeSequence
  private static boolean interpolatedStringBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = escapeSequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedStringBody EOL
  public static boolean interpolatedStringHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated string heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedStringBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, INTERPOLATED_STRING_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_HEREDOC_PROMOTER EOL
  //                                      interpolatedStringHeredocLine*
  //                                      heredocPrefix STRING_SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean interpolatedStringSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, interpolatedStringSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, STRING_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, INTERPOLATED_STRING_SIGIL_HEREDOC, r, p, null);
    return r || p;
  }

  // interpolatedStringHeredocLine*
  private static boolean interpolatedStringSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedStringHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedStringSigilHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER interpolatedStringBody STRING_SIGIL_TERMINATOR sigilModifiers
  public static boolean interpolatedStringSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    r = r && interpolatedStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_SIGIL_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, INTERPOLATED_STRING_SIGIL_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | WORDS_FRAGMENT | escapeSequence)*
  public static boolean interpolatedWordsBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated words body>");
    int c = current_position_(b);
    while (true) {
      if (!interpolatedWordsBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedWordsBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, INTERPOLATED_WORDS_BODY, true, false, null);
    return true;
  }

  // interpolation | WORDS_FRAGMENT | escapeSequence
  private static boolean interpolatedWordsBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, WORDS_FRAGMENT);
    if (!r) r = escapeSequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_WORDS_SIGIL_NAME WORDS_HEREDOC_PROMOTER EOL
  //                              interpolatedWordsHeredocLine*
  //                              heredocPrefix WORDS_HEREDOC_TERMINATOR sigilModifiers
  public static boolean interpolatedWordsHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, interpolatedWordsHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, WORDS_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, INTERPOLATED_WORDS_HEREDOC, r, p, null);
    return r || p;
  }

  // interpolatedWordsHeredocLine*
  private static boolean interpolatedWordsHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedWordsHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedWordsHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedWordsBody EOL
  public static boolean interpolatedWordsHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<interpolated words heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedWordsBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, INTERPOLATED_WORDS_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_WORDS_SIGIL_NAME WORDS_PROMOTER interpolatedWordsBody WORDS_TERMINATOR sigilModifiers
  public static boolean interpolatedWordsLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, INTERPOLATING_WORDS_SIGIL_NAME, WORDS_PROMOTER);
    r = r && interpolatedWordsBody(b, l + 1);
    r = r && consumeToken(b, WORDS_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, INTERPOLATED_WORDS_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // INTERPOLATION_START elixirFile INTERPOLATION_END
  public static boolean interpolation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolation")) return false;
    if (!nextTokenIs(b, INTERPOLATION_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INTERPOLATION_START);
    r = r && elixirFile(b, l + 1);
    r = r && consumeToken(b, INTERPOLATION_END);
    exit_section_(b, m, INTERPOLATION, r);
    return r;
  }

  /* ********************************************************** */
  // ALIAS_TOKEN |
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
    r = consumeToken(b, ALIAS_TOKEN);
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
  // OPENING_BRACKET EOL* (listKeywordPair (infixComma listKeywordPair)* COMMA?)? CLOSING_BRACKET
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

  // (listKeywordPair (infixComma listKeywordPair)* COMMA?)?
  private static boolean list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2")) return false;
    list_2_0(b, l + 1);
    return true;
  }

  // listKeywordPair (infixComma listKeywordPair)* COMMA?
  private static boolean list_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = listKeywordPair(b, l + 1);
    r = r && list_2_0_1(b, l + 1);
    r = r && list_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma listKeywordPair)*
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

  // infixComma listKeywordPair
  private static boolean list_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && listKeywordPair(b, l + 1);
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
  // keywordKeyColonEOL keywordValue
  public static boolean listKeywordPair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listKeywordPair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<list keyword pair>");
    r = keywordKeyColonEOL(b, l + 1);
    r = r && keywordValue(b, l + 1);
    exit_section_(b, l, m, LIST_KEYWORD_PAIR, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // charListLine | charListHeredoc
  static boolean listString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listString")) return false;
    if (!nextTokenIs(b, "", CHAR_LIST_HEREDOC_PROMOTER, CHAR_LIST_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = charListLine(b, l + 1);
    if (!r) r = charListHeredoc(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CHAR_LIST_FRAGMENT*
  public static boolean literalCharListBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<literal char list body>");
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, CHAR_LIST_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalCharListBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, LITERAL_CHAR_LIST_BODY, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalCharListBody EOL
  public static boolean literalCharListHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal char list heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalCharListBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, LITERAL_CHAR_LIST_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_HEREDOC_PROMOTER EOL
  //                                 literalCharListHeredocLine*
  //                                 heredocPrefix CHAR_LIST_SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalCharListSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, literalCharListSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, CHAR_LIST_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, LITERAL_CHAR_LIST_SIGIL_HEREDOC, r, p, null);
    return r || p;
  }

  // literalCharListHeredocLine*
  private static boolean literalCharListSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigilHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!literalCharListHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalCharListSigilHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER literalCharListBody CHAR_LIST_SIGIL_TERMINATOR sigilModifiers
  public static boolean literalCharListSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    r = r && literalCharListBody(b, l + 1);
    r = r && consumeToken(b, CHAR_LIST_SIGIL_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, LITERAL_CHAR_LIST_SIGIL_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // REGEX_FRAGMENT*
  public static boolean literalRegexBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<literal regex body>");
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, REGEX_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalRegexBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, LITERAL_REGEX_BODY, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_REGEX_SIGIL_NAME REGEX_HEREDOC_PROMOTER EOL
  //                         literalRegexHeredocLine*
  //                         heredocPrefix REGEX_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalRegexHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, literalRegexHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, REGEX_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, LITERAL_REGEX_HEREDOC, r, p, null);
    return r || p;
  }

  // literalRegexHeredocLine*
  private static boolean literalRegexHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!literalRegexHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalRegexHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalRegexBody EOL
  public static boolean literalRegexHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal regex heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalRegexBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, LITERAL_REGEX_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_REGEX_SIGIL_NAME REGEX_PROMOTER literalRegexBody REGEX_TERMINATOR sigilModifiers
  public static boolean literalRegexLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_REGEX_SIGIL_NAME, REGEX_PROMOTER);
    r = r && literalRegexBody(b, l + 1);
    r = r && consumeToken(b, REGEX_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, LITERAL_REGEX_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // SIGIL_FRAGMENT*
  public static boolean literalSigilBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<literal sigil body>");
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalSigilBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, LITERAL_SIGIL_BODY, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                         literalSigilHeredocLine*
  //                         heredocPrefix SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, literalSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, LITERAL_SIGIL_HEREDOC, r, p, null);
    return r || p;
  }

  // literalSigilHeredocLine*
  private static boolean literalSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!literalSigilHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalSigilHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalSigilBody EOL
  public static boolean literalSigilHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal sigil heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalSigilBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, LITERAL_SIGIL_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_PROMOTER literalSigilBody SIGIL_TERMINATOR sigilModifiers
  public static boolean literalSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_SIGIL_NAME, SIGIL_PROMOTER);
    r = r && literalSigilBody(b, l + 1);
    r = r && consumeToken(b, SIGIL_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, LITERAL_SIGIL_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // STRING_FRAGMENT*
  public static boolean literalStringBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<literal string body>");
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, STRING_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalStringBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, LITERAL_STRING_BODY, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalStringBody EOL
  public static boolean literalStringHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal string heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalStringBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, LITERAL_STRING_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_STRING_SIGIL_NAME STRING_SIGIL_HEREDOC_PROMOTER EOL
  //                               literalStringHeredocLine*
  //                               heredocPrefix STRING_SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalStringSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, literalStringSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, STRING_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, LITERAL_STRING_SIGIL_HEREDOC, r, p, null);
    return r || p;
  }

  // literalStringHeredocLine*
  private static boolean literalStringSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigilHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!literalStringHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalStringSigilHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER literalStringBody STRING_SIGIL_TERMINATOR sigilModifiers
  public static boolean literalStringSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    r = r && literalStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_SIGIL_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, LITERAL_STRING_SIGIL_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // WORDS_FRAGMENT*
  public static boolean literalWordsBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<literal words body>");
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, WORDS_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "literalWordsBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, LITERAL_WORDS_BODY, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_WORDS_SIGIL_NAME WORDS_HEREDOC_PROMOTER EOL
  //                         literalWordsHeredocLine*
  //                         heredocPrefix WORDS_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalWordsHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 3
    r = r && report_error_(b, literalWordsHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, WORDS_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, LITERAL_WORDS_HEREDOC, r, p, null);
    return r || p;
  }

  // literalWordsHeredocLine*
  private static boolean literalWordsHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredoc_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!literalWordsHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalWordsHeredoc_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalWordsBody EOL
  public static boolean literalWordsHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal words heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalWordsBody(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, LITERAL_WORDS_HEREDOC_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_WORDS_SIGIL_NAME WORDS_PROMOTER literalWordsBody WORDS_TERMINATOR sigilModifiers
  public static boolean literalWordsLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_WORDS_SIGIL_NAME, WORDS_PROMOTER);
    r = r && literalWordsBody(b, l + 1);
    r = r && consumeToken(b, WORDS_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, LITERAL_WORDS_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* MATCH_OPERATOR EOL*
  public static boolean matchInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchInfixOperator")) return false;
    if (!nextTokenIs(b, "<=>", EOL, MATCH_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<=>");
    r = matchInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, MATCH_OPERATOR);
    r = r && matchInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, MATCH_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean matchInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedAdditionOperand matchedAdditionOperation*
  static boolean matchedAdditionExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAdditionExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedAdditionOperand(b, l + 1);
    r = r && matchedAdditionExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedAdditionOperation*
  private static boolean matchedAdditionExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAdditionExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedAdditionOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedAdditionExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                    matchedMultiplicationExpression
  static boolean matchedAdditionOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAdditionOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedMultiplicationExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // additionInfixOperator matchedAdditionOperand
  public static boolean matchedAdditionOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAdditionOperation")) return false;
    if (!nextTokenIs(b, DUAL_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, null);
    r = additionInfixOperator(b, l + 1);
    r = r && matchedAdditionOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_ADDITION_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedAndOperand matchedAndOperation*
  static boolean matchedAndExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAndExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedAndOperand(b, l + 1);
    r = r && matchedAndExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedAndOperation*
  private static boolean matchedAndExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAndExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedAndOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedAndExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                               matchedComparisonExpression
  static boolean matchedAndOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAndOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedComparisonExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // andInfixOperator matchedAndOperand
  public static boolean matchedAndOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAndOperation")) return false;
    if (!nextTokenIs(b, "<matched and operation>", AND_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched and operation>");
    r = andInfixOperator(b, l + 1);
    r = r && matchedAndOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_AND_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedArrowOperand matchedArrowOperation*
  static boolean matchedArrowExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedArrowExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedArrowOperand(b, l + 1);
    r = r && matchedArrowExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedArrowOperation*
  private static boolean matchedArrowExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedArrowExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedArrowOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedArrowExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                 matchedInExpression
  static boolean matchedArrowOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedArrowOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedInExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // arrowInfixOperator matchedArrowOperand
  public static boolean matchedArrowOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedArrowOperation")) return false;
    if (!nextTokenIs(b, "<matched arrow operation>", ARROW_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched arrow operation>");
    r = arrowInfixOperator(b, l + 1);
    r = r && matchedArrowOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_ARROW_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedCallOperand matchedCallOperation?
  static boolean matchedCallExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCallExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCallOperand(b, l + 1);
    r = r && matchedCallExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedCallOperation?
  private static boolean matchedCallExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCallExpression_1")) return false;
    matchedCallOperation(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                matchedNonNumericUnaryOperation |
  //                                matchedNonNumericAtOperation |
  //                                noParenthesesNoArgumentsUnqualifiedCallOrVariable |
  //                                accessExpression
  static boolean matchedCallOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCallOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedNonNumericUnaryOperation(b, l + 1);
    if (!r) r = matchedNonNumericAtOperation(b, l + 1);
    if (!r) r = noParenthesesNoArgumentsUnqualifiedCallOrVariable(b, l + 1);
    if (!r) r = accessExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArgumentsStrict
  public static boolean matchedCallOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCallOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched call operation>");
    r = noParenthesesManyArgumentsStrict(b, l + 1);
    exit_section_(b, l, m, MATCHED_CALL_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                                matchedCaptureNonNumericOperand
  static boolean matchedCaptureNonNumericExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCaptureNonNumericExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedCaptureNonNumericOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                             matchedInMatchExpression
  static boolean matchedCaptureNonNumericOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCaptureNonNumericOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedInMatchExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // capturePrefixOperator !numeric matchedCaptureNonNumericOperand
  public static boolean matchedCaptureNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCaptureNonNumericOperation")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = capturePrefixOperator(b, l + 1);
    r = r && matchedCaptureNonNumericOperation_1(b, l + 1);
    r = r && matchedCaptureNonNumericOperand(b, l + 1);
    exit_section_(b, m, MATCHED_CAPTURE_NON_NUMERIC_OPERATION, r);
    return r;
  }

  // !numeric
  private static boolean matchedCaptureNonNumericOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedCaptureNonNumericOperation_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !numeric(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedComparisonOperand matchedComparisonOperation*
  static boolean matchedComparisonExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedComparisonExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedComparisonOperand(b, l + 1);
    r = r && matchedComparisonExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedComparisonOperation*
  private static boolean matchedComparisonExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedComparisonExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedComparisonOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedComparisonExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                      matchedRelationalExpression
  static boolean matchedComparisonOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedComparisonOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedRelationalExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // comparisonInfixOperator matchedComparisonOperand
  public static boolean matchedComparisonOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedComparisonOperation")) return false;
    if (!nextTokenIs(b, "<matched comparison operation>", COMPARISON_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched comparison operation>");
    r = comparisonInfixOperator(b, l + 1);
    r = r && matchedComparisonOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_COMPARISON_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedDotOperand matchedDotOperation*
  static boolean matchedDotExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedDotOperand(b, l + 1);
    r = r && matchedDotExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedDotOperation*
  private static boolean matchedDotExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedDotOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedDotExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                               matchedNonNumericUnaryOperation |
  //                               matchedNonNumericAtExpression
  static boolean matchedDotOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedNonNumericUnaryOperation(b, l + 1);
    if (!r) r = matchedNonNumericAtExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // dotInfixOperator matchedDotOperand
  public static boolean matchedDotOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotOperation")) return false;
    if (!nextTokenIs(b, "<matched dot operation>", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched dot operation>");
    r = dotInfixOperator(b, l + 1);
    r = r && matchedDotOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_DOT_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericExpression
  static boolean matchedExpression(PsiBuilder b, int l) {
    return matchedCaptureNonNumericExpression(b, l + 1);
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
  // matchedCaptureNonNumericOperation |
  //                               matchedNonNumericUnaryExpression
  static boolean matchedHatOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedNonNumericUnaryExpression(b, l + 1);
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
  // matchedInOperand matchedInOperation*
  static boolean matchedInExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedInOperand(b, l + 1);
    r = r && matchedInExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedInOperation*
  private static boolean matchedInExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedInOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedInExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedInMatchOperand matchedInMatchOperation*
  static boolean matchedInMatchExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInMatchExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedInMatchOperand(b, l + 1);
    r = r && matchedInMatchExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedInMatchOperation*
  private static boolean matchedInMatchExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInMatchExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedInMatchOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedInMatchExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                   matchedWhenExpression
  static boolean matchedInMatchOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInMatchOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedWhenExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // inMatchInfixOperator matchedInMatchOperand
  public static boolean matchedInMatchOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInMatchOperation")) return false;
    if (!nextTokenIs(b, "<matched in match operation>", EOL, IN_MATCH_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched in match operation>");
    r = inMatchInfixOperator(b, l + 1);
    r = r && matchedInMatchOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_IN_MATCH_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                              matchedTwoExpression
  static boolean matchedInOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedTwoExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // inInfixOperator matchedInOperand
  public static boolean matchedInOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedInOperation")) return false;
    if (!nextTokenIs(b, "<matched in operation>", EOL, IN_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched in operation>");
    r = inInfixOperator(b, l + 1);
    r = r && matchedInOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_IN_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedMatchOperand matchedMatchOperation?
  static boolean matchedMatchExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMatchExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedMatchOperand(b, l + 1);
    r = r && matchedMatchExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedMatchOperation?
  private static boolean matchedMatchExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMatchExpression_1")) return false;
    matchedMatchOperation(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                 matchedOrExpression
  static boolean matchedMatchOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMatchOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedOrExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchInfixOperator matchedMatchExpression
  public static boolean matchedMatchOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMatchOperation")) return false;
    if (!nextTokenIs(b, "<matched match operation>", EOL, MATCH_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched match operation>");
    r = matchInfixOperator(b, l + 1);
    r = r && matchedMatchExpression(b, l + 1);
    exit_section_(b, l, m, MATCHED_MATCH_OPERATION, r, false, null);
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
  // matchedCaptureNonNumericOperation |
  //                                          matchedHatExpression
  static boolean matchedMultiplicationOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
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
  // matchedNonNumericAtOperation |
  //                                           matchedNonNumericAtOperand
  static boolean matchedNonNumericAtExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericAtExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericAtOperation(b, l + 1);
    if (!r) r = matchedNonNumericAtOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                        matchedNonNumericUnaryOperation |
  //                                        matchedNonNumericAtOperation |
  //                                        matchedCallExpression
  static boolean matchedNonNumericAtOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericAtOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedNonNumericUnaryOperation(b, l + 1);
    if (!r) r = matchedNonNumericAtOperation(b, l + 1);
    if (!r) r = matchedCallExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // nonNumericAtPrefixOperator matchedNonNumericAtOperand
  public static boolean matchedNonNumericAtOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericAtOperation")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nonNumericAtPrefixOperator(b, l + 1);
    r = r && matchedNonNumericAtOperand(b, l + 1);
    exit_section_(b, m, MATCHED_NON_NUMERIC_AT_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericUnaryOperation |
  //                                              matchedNonNumericUnaryOperand
  static boolean matchedNonNumericUnaryExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericUnaryExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericUnaryOperation(b, l + 1);
    if (!r) r = matchedNonNumericUnaryOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                           matchedNonNumericUnaryOperation |
  //                                           matchedDotExpression
  static boolean matchedNonNumericUnaryOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericUnaryOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedNonNumericUnaryOperation(b, l + 1);
    if (!r) r = matchedDotExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // nonNumericUnaryPrefixOperator matchedNonNumericUnaryOperand
  public static boolean matchedNonNumericUnaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericUnaryOperation")) return false;
    if (!nextTokenIs(b, "<matched non numeric unary operation>", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<matched non numeric unary operation>");
    r = nonNumericUnaryPrefixOperator(b, l + 1);
    r = r && matchedNonNumericUnaryOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_NON_NUMERIC_UNARY_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedOrOperand matchedOrOperation*
  static boolean matchedOrExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedOrExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedOrOperand(b, l + 1);
    r = r && matchedOrExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedOrOperation*
  private static boolean matchedOrExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedOrExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedOrOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedOrExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                              matchedAndExpression
  static boolean matchedOrOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedOrOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedAndExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // orInfixOperator matchedOrOperand
  public static boolean matchedOrOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedOrOperation")) return false;
    if (!nextTokenIs(b, "<matched or operation>", EOL, OR_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched or operation>");
    r = orInfixOperator(b, l + 1);
    r = r && matchedOrOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_OR_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedPipeOperand matchedPipeOperation?
  static boolean matchedPipeExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedPipeExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedPipeOperand(b, l + 1);
    r = r && matchedPipeExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedPipeOperation?
  private static boolean matchedPipeExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedPipeExpression_1")) return false;
    matchedPipeOperation(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                matchedMatchExpression
  static boolean matchedPipeOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedPipeOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedMatchExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // pipeInfixOperator matchedPipeExpression
  public static boolean matchedPipeOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedPipeOperation")) return false;
    if (!nextTokenIs(b, "<matched pipe operation>", EOL, PIPE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched pipe operation>");
    r = pipeInfixOperator(b, l + 1);
    r = r && matchedPipeExpression(b, l + 1);
    exit_section_(b, l, m, MATCHED_PIPE_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedRelationalOperand matchedRelationalOperation*
  static boolean matchedRelationalExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedRelationalExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedRelationalOperand(b, l + 1);
    r = r && matchedRelationalExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedRelationalOperation*
  private static boolean matchedRelationalExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedRelationalExpression_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedRelationalOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedRelationalExpression_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                      matchedArrowExpression
  static boolean matchedRelationalOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedRelationalOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedArrowExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // relationalInfixOperator matchedRelationalOperand
  public static boolean matchedRelationalOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedRelationalOperation")) return false;
    if (!nextTokenIs(b, "<matched relational operation>", EOL, RELATIONAL_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched relational operation>");
    r = relationalInfixOperator(b, l + 1);
    r = r && matchedRelationalOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_RELATIONAL_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedTwoOperand matchedTwoOperation?
  static boolean matchedTwoExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTwoExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedTwoOperand(b, l + 1);
    r = r && matchedTwoExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedTwoOperation?
  private static boolean matchedTwoExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTwoExpression_1")) return false;
    matchedTwoOperation(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                               matchedAdditionExpression
  static boolean matchedTwoOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTwoOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedAdditionExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // twoInfixOperator matchedTwoExpression
  public static boolean matchedTwoOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTwoOperation")) return false;
    if (!nextTokenIs(b, "<matched two operation>", EOL, TWO_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched two operation>");
    r = twoInfixOperator(b, l + 1);
    r = r && matchedTwoExpression(b, l + 1);
    exit_section_(b, l, m, MATCHED_TWO_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedTypeOperand matchedTypeOperation?
  static boolean matchedTypeExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTypeExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedTypeOperand(b, l + 1);
    r = r && matchedTypeExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedTypeOperation?
  private static boolean matchedTypeExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTypeExpression_1")) return false;
    matchedTypeOperation(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                matchedPipeExpression
  static boolean matchedTypeOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTypeOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedPipeExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeInfixOperator matchedTypeExpression
  public static boolean matchedTypeOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedTypeOperation")) return false;
    if (!nextTokenIs(b, "<matched type operation>", EOL, TYPE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched type operation>");
    r = typeInfixOperator(b, l + 1);
    r = r && matchedTypeExpression(b, l + 1);
    exit_section_(b, l, m, MATCHED_TYPE_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedWhenOperand matchedWhenOperation?
  static boolean matchedWhenExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedWhenExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedWhenOperand(b, l + 1);
    r = r && matchedWhenExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedWhenOperation?
  private static boolean matchedWhenExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedWhenExpression_1")) return false;
    matchedWhenOperation(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // matchedCaptureNonNumericOperation |
  //                                // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L173
  //                                noParenthesesKeywords |
  //                                matchedTypeExpression
  static boolean matchedWhenOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedWhenOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedCaptureNonNumericOperation(b, l + 1);
    if (!r) r = noParenthesesKeywords(b, l + 1);
    if (!r) r = matchedTypeExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // whenInfixOperator matchedWhenExpression
  public static boolean matchedWhenOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedWhenOperation")) return false;
    if (!nextTokenIs(b, "<matched when operation>", EOL, WHEN_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched when operation>");
    r = whenInfixOperator(b, l + 1);
    r = r && matchedWhenExpression(b, l + 1);
    exit_section_(b, l, m, MATCHED_WHEN_OPERATION, r, false, null);
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
  // matchedExpression (infixComma noParenthesesExpression)+
  static boolean noParenthesesCommaExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesCommaExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1);
    r = r && noParenthesesCommaExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma noParenthesesExpression)+
  private static boolean noParenthesesCommaExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesCommaExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesCommaExpression_1_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!noParenthesesCommaExpression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "noParenthesesCommaExpression_1", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // infixComma noParenthesesExpression
  private static boolean noParenthesesCommaExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesCommaExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && noParenthesesExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                             /* Must be before matchedExpression because noParenthesesExpression is
  //                                `matchedExpressionDotIdentifier callArgumentsNoParenthesesManyStrict` which is longer
  //                                than `matchedExpressionDotIdentifier` in matchedExpression. */
  //                             /* This will be marked as an error by
  //                                {@link org.elixir_lang.inspection.NoParenthesesManyStrict} */
  //                             noParenthesesManyStrictNoParenthesesExpression |
  //                             matchedExpression !KEYWORD_PAIR_COLON
  public static boolean noParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses expression>");
    r = emptyParentheses(b, l + 1);
    if (!r) r = noParenthesesManyStrictNoParenthesesExpression(b, l + 1);
    if (!r) r = noParenthesesExpression_2(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_EXPRESSION, r, false, null);
    return r;
  }

  // matchedExpression !KEYWORD_PAIR_COLON
  private static boolean noParenthesesExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesExpression_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1);
    r = r && noParenthesesExpression_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !KEYWORD_PAIR_COLON
  private static boolean noParenthesesExpression_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesExpression_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !consumeToken(b, KEYWORD_PAIR_COLON);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedExpression
  public static boolean noParenthesesFirstPositional(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesFirstPositional")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses first positional>");
    r = matchedExpression(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_FIRST_POSITIONAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keywordKeyColonEOL noParenthesesExpression
  public static boolean noParenthesesKeywordPair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywordPair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses keyword pair>");
    r = keywordKeyColonEOL(b, l + 1);
    r = r && noParenthesesExpression(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_KEYWORD_PAIR, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesKeywordPair (infixComma noParenthesesKeywordPair)*
  public static boolean noParenthesesKeywords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywords")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses keywords>");
    r = noParenthesesKeywordPair(b, l + 1);
    r = r && noParenthesesKeywords_1(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_KEYWORDS, r, false, null);
    return r;
  }

  // (infixComma noParenthesesKeywordPair)*
  private static boolean noParenthesesKeywords_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywords_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!noParenthesesKeywords_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "noParenthesesKeywords_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // infixComma noParenthesesKeywordPair
  private static boolean noParenthesesKeywords_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywords_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && noParenthesesKeywordPair(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesOnePositionalAndKeywordsArguments |
  //                                noParenthesesManyPositionalAndMaybeKeywordsArguments
  public static boolean noParenthesesManyArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses many arguments>");
    r = noParenthesesOnePositionalAndKeywordsArguments(b, l + 1);
    if (!r) r = noParenthesesManyPositionalAndMaybeKeywordsArguments(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_MANY_ARGUMENTS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArguments |
  //                                              noParenthesesStrict
  static boolean noParenthesesManyArgumentsStrict(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArgumentsStrict")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArguments(b, l + 1);
    if (!r) r = noParenthesesStrict(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean noParenthesesManyArgumentsUnqualifiedIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArgumentsUnqualifiedIdentifier")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, NO_PARENTHESES_MANY_ARGUMENTS_UNQUALIFIED_IDENTIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesCommaExpression (infixComma noParenthesesKeywords)?
  public static boolean noParenthesesManyPositionalAndMaybeKeywordsArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyPositionalAndMaybeKeywordsArguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses many positional and maybe keywords arguments>");
    r = noParenthesesCommaExpression(b, l + 1);
    r = r && noParenthesesManyPositionalAndMaybeKeywordsArguments_1(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_MANY_POSITIONAL_AND_MAYBE_KEYWORDS_ARGUMENTS, r, false, null);
    return r;
  }

  // (infixComma noParenthesesKeywords)?
  private static boolean noParenthesesManyPositionalAndMaybeKeywordsArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyPositionalAndMaybeKeywordsArguments_1")) return false;
    noParenthesesManyPositionalAndMaybeKeywordsArguments_1_0(b, l + 1);
    return true;
  }

  // infixComma noParenthesesKeywords
  private static boolean noParenthesesManyPositionalAndMaybeKeywordsArguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyPositionalAndMaybeKeywordsArguments_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // unqualifiedNoParenthesesManyArgumentsCall
  public static boolean noParenthesesManyStrictNoParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyStrictNoParenthesesExpression")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unqualifiedNoParenthesesManyArgumentsCall(b, l + 1);
    exit_section_(b, m, NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean noParenthesesNoArgumentsUnqualifiedCallOrVariable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesNoArgumentsUnqualifiedCallOrVariable")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, NO_PARENTHESES_NO_ARGUMENTS_UNQUALIFIED_CALL_OR_VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesFirstPositional infixComma noParenthesesKeywords
  public static boolean noParenthesesOnePositionalAndKeywordsArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesOnePositionalAndKeywordsArguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses one positional and keywords arguments>");
    r = noParenthesesFirstPositional(b, l + 1);
    r = r && infixComma(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_ONE_POSITIONAL_AND_KEYWORDS_ARGUMENTS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                         OPENING_PARENTHESIS (
  //                                              noParenthesesKeywords |
  //                                              noParenthesesManyArguments
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
  //                                              noParenthesesKeywords |
  //                                              noParenthesesManyArguments
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

  // noParenthesesKeywords |
  //                                              noParenthesesManyArguments
  private static boolean noParenthesesStrict_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesStrict_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesKeywords(b, l + 1);
    if (!r) r = noParenthesesManyArguments(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // atPrefixOperator !numeric
  static boolean nonNumericAtPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonNumericAtPrefixOperator")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && nonNumericAtPrefixOperator_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !numeric
  private static boolean nonNumericAtPrefixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonNumericAtPrefixOperator_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !numeric(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // unaryPrefixOperator !numeric
  static boolean nonNumericUnaryPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonNumericUnaryPrefixOperator")) return false;
    if (!nextTokenIs(b, "", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unaryPrefixOperator(b, l + 1);
    r = r && nonNumericUnaryPrefixOperator_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !numeric
  private static boolean nonNumericUnaryPrefixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonNumericUnaryPrefixOperator_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !numeric(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // charToken |
  //                     binaryWholeNumber |
  //                     // decimalFloat starts with decimalWholeNumber, so decimalFloat needs to be first
  //                     decimalFloat |
  //                     decimalWholeNumber |
  //                     hexadecimalWholeNumber |
  //                     octalWholeNumber |
  //                     unknownBaseWholeNumber
  static boolean numeric(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numeric")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = charToken(b, l + 1);
    if (!r) r = binaryWholeNumber(b, l + 1);
    if (!r) r = decimalFloat(b, l + 1);
    if (!r) r = decimalWholeNumber(b, l + 1);
    if (!r) r = hexadecimalWholeNumber(b, l + 1);
    if (!r) r = octalWholeNumber(b, l + 1);
    if (!r) r = unknownBaseWholeNumber(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS
  public static boolean octalDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalDigits")) return false;
    if (!nextTokenIs(b, "<octal digits>", INVALID_OCTAL_DIGITS, VALID_OCTAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<octal digits>");
    r = consumeToken(b, INVALID_OCTAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_OCTAL_DIGITS);
    exit_section_(b, l, m, OCTAL_DIGITS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX OCTAL_WHOLE_NUMBER_BASE octalDigits+
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

  // octalDigits+
  private static boolean octalWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = octalDigits(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!octalDigits(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "octalWholeNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // VALID_HEXADECIMAL_DIGITS
  public static boolean openHexadecimalEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "openHexadecimalEscapeSequence")) return false;
    if (!nextTokenIs(b, VALID_HEXADECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VALID_HEXADECIMAL_DIGITS);
    exit_section_(b, m, OPEN_HEXADECIMAL_ESCAPE_SEQUENCE, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* OR_OPERATOR EOL*
  public static boolean orInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orInfixOperator")) return false;
    if (!nextTokenIs(b, "<||, |||, or>", EOL, OR_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<||, |||, or>");
    r = orInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, OR_OPERATOR);
    r = r && orInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, OR_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean orInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "orInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean orInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "orInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // EOL* PIPE_OPERATOR EOL*
  public static boolean pipeInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipeInfixOperator")) return false;
    if (!nextTokenIs(b, "<|>", EOL, PIPE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<|>");
    r = pipeInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, PIPE_OPERATOR);
    r = r && pipeInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, PIPE_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean pipeInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipeInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "pipeInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean pipeInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipeInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "pipeInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // charListLine | stringLine
  static boolean quote(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quote")) return false;
    if (!nextTokenIs(b, "", CHAR_LIST_PROMOTER, STRING_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = charListLine(b, l + 1);
    if (!r) r = stringLine(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* RELATIONAL_OPERATOR EOL*
  public static boolean relationalInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalInfixOperator")) return false;
    if (!nextTokenIs(b, "<<, >, <=, >=>", EOL, RELATIONAL_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<<, >, <=, >=>");
    r = relationalInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, RELATIONAL_OPERATOR);
    r = r && relationalInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, RELATIONAL_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean relationalInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "relationalInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean relationalInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "relationalInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // SIGIL_MODIFIER*
  public static boolean sigilModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigilModifiers")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<sigil modifiers>");
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "sigilModifiers", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, SIGIL_MODIFIERS, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // STRING_HEREDOC_PROMOTER EOL
  //                   interpolatedStringHeredocLine*
  //                   heredocPrefix STRING_HEREDOC_TERMINATOR
  public static boolean stringHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredoc")) return false;
    if (!nextTokenIs(b, STRING_HEREDOC_PROMOTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 1, STRING_HEREDOC_PROMOTER, EOL);
    p = r; // pin = 1
    r = r && report_error_(b, stringHeredoc_2(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && consumeToken(b, STRING_HEREDOC_TERMINATOR) && r;
    exit_section_(b, l, m, STRING_HEREDOC, r, p, null);
    return r || p;
  }

  // interpolatedStringHeredocLine*
  private static boolean stringHeredoc_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredoc_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedStringHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stringHeredoc_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // STRING_PROMOTER
  //                interpolatedStringBody
  //                STRING_TERMINATOR
  public static boolean stringLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringLine")) return false;
    if (!nextTokenIs(b, STRING_PROMOTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING_PROMOTER);
    r = r && interpolatedStringBody(b, l + 1);
    r = r && consumeToken(b, STRING_TERMINATOR);
    exit_section_(b, m, STRING_LINE, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* TWO_OPERATOR EOL*
  public static boolean twoInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "twoInfixOperator")) return false;
    if (!nextTokenIs(b, "<++, --, .., <>>", EOL, TWO_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<++, --, .., <>>");
    r = twoInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, TWO_OPERATOR);
    r = r && twoInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, TWO_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean twoInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "twoInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "twoInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean twoInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "twoInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "twoInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // EOL* TYPE_OPERATOR EOL*
  public static boolean typeInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeInfixOperator")) return false;
    if (!nextTokenIs(b, "<::>", EOL, TYPE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<::>");
    r = typeInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, TYPE_OPERATOR);
    r = r && typeInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, TYPE_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean typeInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "typeInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean typeInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "typeInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // unaryPrefixOperator numeric
  public static boolean unaryNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryNumericOperation")) return false;
    if (!nextTokenIs(b, "<unary numeric operation>", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<unary numeric operation>");
    r = unaryPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, l, m, UNARY_NUMERIC_OPERATION, r, false, null);
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
  // INVALID_UNKNOWN_BASE_DIGITS
  public static boolean unknownBaseDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknownBaseDigits")) return false;
    if (!nextTokenIs(b, INVALID_UNKNOWN_BASE_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INVALID_UNKNOWN_BASE_DIGITS);
    exit_section_(b, m, UNKNOWN_BASE_DIGITS, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX UNKNOWN_WHOLE_NUMBER_BASE unknownBaseDigits+
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

  // unknownBaseDigits+
  private static boolean unknownBaseWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknownBaseWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unknownBaseDigits(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!unknownBaseDigits(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unknownBaseWholeNumber_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArgumentsUnqualifiedIdentifier
  //                                               noParenthesesManyArgumentsStrict
  public static boolean unqualifiedNoParenthesesManyArgumentsCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unqualifiedNoParenthesesManyArgumentsCall")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArgumentsUnqualifiedIdentifier(b, l + 1);
    r = r && noParenthesesManyArgumentsStrict(b, l + 1);
    exit_section_(b, m, UNQUALIFIED_NO_PARENTHESES_MANY_ARGUMENTS_CALL, r);
    return r;
  }

  /* ********************************************************** */
  // EOL* WHEN_OPERATOR EOL*
  public static boolean whenInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whenInfixOperator")) return false;
    if (!nextTokenIs(b, "<when>", EOL, WHEN_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<when>");
    r = whenInfixOperator_0(b, l + 1);
    r = r && consumeToken(b, WHEN_OPERATOR);
    r = r && whenInfixOperator_2(b, l + 1);
    exit_section_(b, l, m, WHEN_INFIX_OPERATOR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean whenInfixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whenInfixOperator_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "whenInfixOperator_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean whenInfixOperator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whenInfixOperator_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "whenInfixOperator_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

}
