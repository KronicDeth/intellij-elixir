// This is a generated file. Not intended for manual editing.
package org.elixir_lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static org.elixir_lang.psi.ElixirTypes.*;

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
    else if (t == ALIAS) {
      r = alias(b, 0);
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
    else if (t == CAPTURE_CHAR_TOKEN_OR_NUMBER_OPERATION) {
      r = captureCharTokenOrNumberOperation(b, 0);
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
    else if (t == DECIMAL_DIGITS) {
      r = decimalDigits(b, 0);
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
    else if (t == INFIX_DOT_OPERATOR) {
      r = infixDotOperator(b, 0);
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
    else if (t == KEYWORD_PAIR) {
      r = keywordPair(b, 0);
    }
    else if (t == KEYWORD_VALUE) {
      r = keywordValue(b, 0);
    }
    else if (t == LIST) {
      r = list(b, 0);
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
    else if (t == MATCHED_AT_OPERATION) {
      r = matchedAtOperation(b, 0);
    }
    else if (t == MATCHED_DOT_OPERATION) {
      r = matchedDotOperation(b, 0);
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
    else if (t == MULTIPLICATION_INFIX_OPERATOR) {
      r = multiplicationInfixOperator(b, 0);
    }
    else if (t == NO_PARENTHESES_KEYWORDS) {
      r = noParenthesesKeywords(b, 0);
    }
    else if (t == NO_PARENTHESES_KEYWORDS_EXPRESSION) {
      r = noParenthesesKeywordsExpression(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_ARGUMENTS) {
      r = noParenthesesManyArguments(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_ARGUMENTS_QUALIFIED_CALL) {
      r = noParenthesesManyArgumentsQualifiedCall(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_ARGUMENTS_UNQUALIFIED_CALL) {
      r = noParenthesesManyArgumentsUnqualifiedCall(b, 0);
    }
    else if (t == NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION) {
      r = noParenthesesManyStrictNoParenthesesExpression(b, 0);
    }
    else if (t == NO_PARENTHESES_NO_ARGUMENTS_QUALIFIED_CALL) {
      r = noParenthesesNoArgumentsQualifiedCall(b, 0);
    }
    else if (t == NO_PARENTHESES_NO_ARGUMENTS_UNQUALIFIED_CALL_OR_VARIABLE) {
      r = noParenthesesNoArgumentsUnqualifiedCallOrVariable(b, 0);
    }
    else if (t == NO_PARENTHESES_STRICT) {
      r = noParenthesesStrict(b, 0);
    }
    else if (t == NUMBER) {
      r = number(b, 0);
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
    else if (t == QUALIFIED_ALIAS) {
      r = qualifiedAlias(b, 0);
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
  //                              atom |
  //                              alias
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
    if (!r) r = atom(b, l + 1);
    if (!r) r = alias(b, l + 1);
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
  // decimalWholeNumber DECIMAL_MARK decimalWholeNumber (EXPONENT_MARK DUAL_OPERATOR? decimalWholeNumber)?
  public static boolean decimalFloat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat")) return false;
    if (!nextTokenIs(b, "<decimal float>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<decimal float>");
    r = decimalWholeNumber(b, l + 1);
    r = r && consumeToken(b, DECIMAL_MARK);
    r = r && decimalWholeNumber(b, l + 1);
    r = r && decimalFloat_3(b, l + 1);
    exit_section_(b, l, m, DECIMAL_FLOAT, r, false, null);
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
    if (!nextTokenIs(b, "<decimal number>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<decimal number>");
    r = decimalFloat(b, l + 1);
    if (!r) r = decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, DECIMAL_NUMBER, r, false, null);
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
  //                        noParenthesesManyArgumentsCall |
  //                        matchedExpression
  static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = emptyParentheses(b, l + 1);
    if (!r) r = noParenthesesManyArgumentsCall(b, l + 1);
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
  // TILDE LITERAL_SIGIL_NAME WORDS_PROMOTER literalWordsBody WORDS_TERMINATOR sigilModifiers
  public static boolean literalWordsLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TILDE, LITERAL_SIGIL_NAME, WORDS_PROMOTER);
    r = r && literalWordsBody(b, l + 1);
    r = r && consumeToken(b, WORDS_TERMINATOR);
    r = r && sigilModifiers(b, l + 1);
    exit_section_(b, m, LITERAL_WORDS_LINE, r);
    return r;
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
  // matchedNonNumericCaptureOperation | matchedUnaryOperation | noParenthesesNoArgumentsUnqualifiedCallOrVariable | accessExpression
  static boolean matchedAtOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedUnaryOperation(b, l + 1);
    if (!r) r = noParenthesesNoArgumentsUnqualifiedCallOrVariable(b, l + 1);
    if (!r) r = accessExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // atPrefixOperator (noParenthesesManyArgumentsCall | matchedAtOperand)
  public static boolean matchedAtOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtOperation")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && matchedAtOperation_1(b, l + 1);
    exit_section_(b, m, MATCHED_AT_OPERATION, r);
    return r;
  }

  // noParenthesesManyArgumentsCall | matchedAtOperand
  private static boolean matchedAtOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtOperation_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = matchedAtOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedDotLeftOperand (matchedDotOperation* matchedDotRightMostOperation)?
  static boolean matchedDotExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedDotLeftOperand(b, l + 1);
    r = r && matchedDotExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (matchedDotOperation* matchedDotRightMostOperation)?
  private static boolean matchedDotExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotExpression_1")) return false;
    matchedDotExpression_1_0(b, l + 1);
    return true;
  }

  // matchedDotOperation* matchedDotRightMostOperation
  private static boolean matchedDotExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedDotExpression_1_0_0(b, l + 1);
    r = r && matchedDotRightMostOperation(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedDotOperation*
  private static boolean matchedDotExpression_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotExpression_1_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!matchedDotOperation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "matchedDotExpression_1_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER |
  //                                         FALSE |
  //                                         NIL |
  //                                         TRUE |
  //                                         binaryString |
  //                                         listString
  static boolean matchedDotIdentifierOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotIdentifierOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NIL);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = binaryString(b, l + 1);
    if (!r) r = listString(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedAtExpression
  static boolean matchedDotLeftOperand(PsiBuilder b, int l) {
    return matchedAtExpression(b, l + 1);
  }

  /* ********************************************************** */
  // infixDotOperator matchedDotRightOperand
  public static boolean matchedDotOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotOperation")) return false;
    if (!nextTokenIs(b, "<matched dot operation>", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched dot operation>");
    r = infixDotOperator(b, l + 1);
    r = r && matchedDotRightOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_DOT_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesNoArgumentsQualifiedCall | qualifiedAlias
  static boolean matchedDotRightMostOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotRightMostOperation")) return false;
    if (!nextTokenIs(b, "", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesNoArgumentsQualifiedCall(b, l + 1);
    if (!r) r = qualifiedAlias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (
  //                                     ALIAS_TOKEN |
  //                                     IDENTIFIER |
  //                                     FALSE |
  //                                     NIL |
  //                                     TRUE |
  //                                     binaryString |
  //                                     listString
  //                                    ) &infixDotOperator
  static boolean matchedDotRightOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotRightOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedDotRightOperand_0(b, l + 1);
    r = r && matchedDotRightOperand_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ALIAS_TOKEN |
  //                                     IDENTIFIER |
  //                                     FALSE |
  //                                     NIL |
  //                                     TRUE |
  //                                     binaryString |
  //                                     listString
  private static boolean matchedDotRightOperand_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotRightOperand_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ALIAS_TOKEN);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NIL);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = binaryString(b, l + 1);
    if (!r) r = listString(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &infixDotOperator
  private static boolean matchedDotRightOperand_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotRightOperand_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = infixDotOperator(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedNonNumericCaptureExpression
  static boolean matchedExpression(PsiBuilder b, int l) {
    return matchedNonNumericCaptureExpression(b, l + 1);
  }

  /* ********************************************************** */
  // matchedHatLeftOperand matchedHatOperation*
  static boolean matchedHatExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedHatLeftOperand(b, l + 1);
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
  static boolean matchedHatLeftOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatLeftOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedUnaryExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // hatInfixOperator matchedHatRightOperand
  public static boolean matchedHatOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatOperation")) return false;
    if (!nextTokenIs(b, "<matched hat operation>", EOL, HAT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched hat operation>");
    r = hatInfixOperator(b, l + 1);
    r = r && matchedHatRightOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_HAT_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArgumentsCall | matchedHatLeftOperand
  static boolean matchedHatRightOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedHatRightOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = matchedHatLeftOperand(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedMultiplicationLeftOperand matchedMultiplicationOperation*
  static boolean matchedMultiplicationExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedMultiplicationLeftOperand(b, l + 1);
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
  static boolean matchedMultiplicationLeftOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationLeftOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedHatExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // multiplicationInfixOperator matchedMultiplicationRightOperand
  public static boolean matchedMultiplicationOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationOperation")) return false;
    if (!nextTokenIs(b, "<matched multiplication operation>", EOL, MULTIPLICATION_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<matched multiplication operation>");
    r = multiplicationInfixOperator(b, l + 1);
    r = r && matchedMultiplicationRightOperand(b, l + 1);
    exit_section_(b, l, m, MATCHED_MULTIPLICATION_OPERATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArgumentsCall | matchedMultiplicationLeftOperand
  static boolean matchedMultiplicationRightOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedMultiplicationRightOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = matchedMultiplicationLeftOperand(b, l + 1);
    exit_section_(b, m, null, r);
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
  // nonNumericCapturePrefixOperator (noParenthesesManyArgumentsCall | matchedNonNumericCaptureOperand)
  public static boolean matchedNonNumericCaptureOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericCaptureOperation")) return false;
    if (!nextTokenIs(b, CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nonNumericCapturePrefixOperator(b, l + 1);
    r = r && matchedNonNumericCaptureOperation_1(b, l + 1);
    exit_section_(b, m, MATCHED_NON_NUMERIC_CAPTURE_OPERATION, r);
    return r;
  }

  // noParenthesesManyArgumentsCall | matchedNonNumericCaptureOperand
  private static boolean matchedNonNumericCaptureOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNonNumericCaptureOperation_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = matchedNonNumericCaptureOperand(b, l + 1);
    exit_section_(b, m, null, r);
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
  // matchedNonNumericCaptureOperation | matchedUnaryOperation | matchedDotExpression
  static boolean matchedUnaryOperand(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryOperand")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedNonNumericCaptureOperation(b, l + 1);
    if (!r) r = matchedUnaryOperation(b, l + 1);
    if (!r) r = matchedDotExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // unaryPrefixOperator (noParenthesesManyArgumentsCall | matchedUnaryOperand)
  public static boolean matchedUnaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryOperation")) return false;
    if (!nextTokenIs(b, "<matched unary operation>", DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<matched unary operation>");
    r = unaryPrefixOperator(b, l + 1);
    r = r && matchedUnaryOperation_1(b, l + 1);
    exit_section_(b, l, m, MATCHED_UNARY_OPERATION, r, false, null);
    return r;
  }

  // noParenthesesManyArgumentsCall | matchedUnaryOperand
  private static boolean matchedUnaryOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryOperation_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = matchedUnaryOperand(b, l + 1);
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
  //                                     /* Must be before matchedExpression because noParenthesesExpression is
  //                                        `matchedExpressionDotIdentifier callArgumentsNoParenthesesManyStrict`
  //                                        which is longer than `matchedExpressionDotIdentifier` in
  //                                        matchedExpression. */
  //                                     /* This will be marked as an error by
  //                                        {@link org.elixir_lang.inspection.NoParenthesesManyStrict} */
  //                                     noParenthesesManyStrictNoParenthesesExpression |
  //                                     matchedExpression !KEYWORD_PAIR_COLON
  static boolean noParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = emptyParentheses(b, l + 1);
    if (!r) r = noParenthesesManyStrictNoParenthesesExpression(b, l + 1);
    if (!r) r = noParenthesesExpression_2(b, l + 1);
    exit_section_(b, m, null, r);
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
  // noParenthesesKeywordsExpression (infixComma noParenthesesKeywordsExpression)*
  public static boolean noParenthesesKeywords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywords")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses keywords>");
    r = noParenthesesKeywordsExpression(b, l + 1);
    r = r && noParenthesesKeywords_1(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_KEYWORDS, r, false, null);
    return r;
  }

  // (infixComma noParenthesesKeywordsExpression)*
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

  // infixComma noParenthesesKeywordsExpression
  private static boolean noParenthesesKeywords_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywords_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && noParenthesesKeywordsExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keywordKeyColonEOL noParenthesesExpression
  public static boolean noParenthesesKeywordsExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywordsExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses keywords expression>");
    r = keywordKeyColonEOL(b, l + 1);
    r = r && noParenthesesExpression(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_KEYWORDS_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedExpression infixComma noParenthesesKeywords |
  //                                noParenthesesCommaExpression (infixComma noParenthesesKeywords)?
  public static boolean noParenthesesManyArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses many arguments>");
    r = noParenthesesManyArguments_0(b, l + 1);
    if (!r) r = noParenthesesManyArguments_1(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_MANY_ARGUMENTS, r, false, null);
    return r;
  }

  // matchedExpression infixComma noParenthesesKeywords
  private static boolean noParenthesesManyArguments_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1);
    r = r && infixComma(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // noParenthesesCommaExpression (infixComma noParenthesesKeywords)?
  private static boolean noParenthesesManyArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesCommaExpression(b, l + 1);
    r = r && noParenthesesManyArguments_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma noParenthesesKeywords)?
  private static boolean noParenthesesManyArguments_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1_1")) return false;
    noParenthesesManyArguments_1_1_0(b, l + 1);
    return true;
  }

  // infixComma noParenthesesKeywords
  private static boolean noParenthesesManyArguments_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArgumentsQualifiedExpression | noParenthesesManyArgumentsUnqualifiedCall
  static boolean noParenthesesManyArgumentsCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArgumentsCall")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArgumentsQualifiedExpression(b, l + 1);
    if (!r) r = noParenthesesManyArgumentsUnqualifiedCall(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // infixDotOperator matchedDotIdentifierOperand noParenthesesManyArgumentsStrict
  public static boolean noParenthesesManyArgumentsQualifiedCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArgumentsQualifiedCall")) return false;
    if (!nextTokenIs(b, "<no parentheses many arguments qualified call>", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<no parentheses many arguments qualified call>");
    r = infixDotOperator(b, l + 1);
    r = r && matchedDotIdentifierOperand(b, l + 1);
    r = r && noParenthesesManyArgumentsStrict(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_MANY_ARGUMENTS_QUALIFIED_CALL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // matchedDotExpression noParenthesesManyArgumentsQualifiedCall
  static boolean noParenthesesManyArgumentsQualifiedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArgumentsQualifiedExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedDotExpression(b, l + 1);
    r = r && noParenthesesManyArgumentsQualifiedCall(b, l + 1);
    exit_section_(b, m, null, r);
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
  // IDENTIFIER noParenthesesManyArgumentsStrict
  public static boolean noParenthesesManyArgumentsUnqualifiedCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArgumentsUnqualifiedCall")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && noParenthesesManyArgumentsStrict(b, l + 1);
    exit_section_(b, m, NO_PARENTHESES_MANY_ARGUMENTS_UNQUALIFIED_CALL, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArgumentsCall
  public static boolean noParenthesesManyStrictNoParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyStrictNoParenthesesExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<no parentheses many strict no parentheses expression>");
    r = noParenthesesManyArgumentsCall(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // infixDotOperator matchedDotIdentifierOperand
  public static boolean noParenthesesNoArgumentsQualifiedCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesNoArgumentsQualifiedCall")) return false;
    if (!nextTokenIs(b, "<no parentheses no arguments qualified call>", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<no parentheses no arguments qualified call>");
    r = infixDotOperator(b, l + 1);
    r = r && matchedDotIdentifierOperand(b, l + 1);
    exit_section_(b, l, m, NO_PARENTHESES_NO_ARGUMENTS_QUALIFIED_CALL, r, false, null);
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
  // infixDotOperator ALIAS_TOKEN
  public static boolean qualifiedAlias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifiedAlias")) return false;
    if (!nextTokenIs(b, "<qualified alias>", DOT_OPERATOR, EOL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, "<qualified alias>");
    r = infixDotOperator(b, l + 1);
    r = r && consumeToken(b, ALIAS_TOKEN);
    exit_section_(b, l, m, QUALIFIED_ALIAS, r, false, null);
    return r;
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
  // interpolatedCharListSigilLine |
  //                   interpolatedCharListSigilHeredoc |
  //                   interpolatedRegexHeredoc |
  //                   interpolatedSigilHeredoc |
  //                   interpolatedStringSigilHeredoc |
  //                   interpolatedWordsHeredoc |
  //                   interpolatedWordsLine |
  //                   interpolatedRegexLine |
  //                   interpolatedSigilLine |
  //                   interpolatedStringSigilLine |
  //                   literalCharListSigilLine |
  //                   literalCharListSigilHeredoc |
  //                   literalRegexHeredoc |
  //                   literalSigilHeredoc |
  //                   literalStringSigilHeredoc |
  //                   literalWordsHeredoc |
  //                   literalRegexLine |
  //                   literalSigilLine |
  //                   literalStringSigilLine |
  //                   literalWordsLine
  static boolean sigil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigil")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = interpolatedCharListSigilLine(b, l + 1);
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
    exit_section_(b, m, null, r);
    return r;
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
