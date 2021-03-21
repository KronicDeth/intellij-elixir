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
import com.intellij.lang.LightPsiParser;
import static org.elixir_lang.Level.*;
import static org.elixir_lang.parser.ExternalRules.*;
import static org.elixir_lang.parser.ExternalRules.Operator.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ElixirParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return eexOrElixirFile(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ACCESS_EXPRESSION, MATCHED_ADDITION_OPERATION, MATCHED_AND_OPERATION, MATCHED_ARROW_OPERATION,
      MATCHED_AT_NON_NUMERIC_OPERATION, MATCHED_AT_NUMERIC_BRACKET_OPERATION, MATCHED_AT_OPERATION, MATCHED_AT_UNQUALIFIED_BRACKET_OPERATION,
      MATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL, MATCHED_BRACKET_OPERATION, MATCHED_COMPARISON_OPERATION, MATCHED_DOT_CALL,
      MATCHED_EXPRESSION, MATCHED_GREATER_THAN_OR_EQUAL_TO_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, MATCHED_IN_MATCH_OPERATION, MATCHED_IN_OPERATION,
      MATCHED_LESS_THAN_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, MATCHED_MATCH_OPERATION, MATCHED_MULTIPLICATION_OPERATION, MATCHED_NOT_IN_OPERATION,
      MATCHED_OR_OPERATION, MATCHED_PIPE_OPERATION, MATCHED_QUALIFIED_ALIAS, MATCHED_QUALIFIED_BRACKET_OPERATION,
      MATCHED_QUALIFIED_MULTIPLE_ALIASES, MATCHED_QUALIFIED_NO_ARGUMENTS_CALL, MATCHED_QUALIFIED_NO_PARENTHESES_CALL, MATCHED_QUALIFIED_PARENTHESES_CALL,
      MATCHED_RELATIONAL_OPERATION, MATCHED_THREE_OPERATION, MATCHED_TWO_OPERATION, MATCHED_TYPE_OPERATION,
      MATCHED_UNARY_NON_NUMERIC_OPERATION, MATCHED_UNARY_OPERATION, MATCHED_UNQUALIFIED_BRACKET_OPERATION, MATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL,
      MATCHED_UNQUALIFIED_NO_PARENTHESES_CALL, MATCHED_UNQUALIFIED_PARENTHESES_CALL, MATCHED_WHEN_OPERATION),
    create_token_set_(ACCESS_EXPRESSION, UNMATCHED_ADDITION_OPERATION, UNMATCHED_AND_OPERATION, UNMATCHED_ARROW_OPERATION,
      UNMATCHED_AT_NON_NUMERIC_OPERATION, UNMATCHED_AT_NUMERIC_BRACKET_OPERATION, UNMATCHED_AT_OPERATION, UNMATCHED_AT_UNQUALIFIED_BRACKET_OPERATION,
      UNMATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL, UNMATCHED_BRACKET_OPERATION, UNMATCHED_COMPARISON_OPERATION, UNMATCHED_DOT_CALL,
      UNMATCHED_EXPRESSION, UNMATCHED_GREATER_THAN_OR_EQUAL_TO_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, UNMATCHED_IN_MATCH_OPERATION, UNMATCHED_IN_OPERATION,
      UNMATCHED_LESS_THAN_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, UNMATCHED_MATCH_OPERATION, UNMATCHED_MULTIPLICATION_OPERATION, UNMATCHED_NOT_IN_OPERATION,
      UNMATCHED_OR_OPERATION, UNMATCHED_PIPE_OPERATION, UNMATCHED_QUALIFIED_ALIAS, UNMATCHED_QUALIFIED_BRACKET_OPERATION,
      UNMATCHED_QUALIFIED_MULTIPLE_ALIASES, UNMATCHED_QUALIFIED_NO_ARGUMENTS_CALL, UNMATCHED_QUALIFIED_NO_PARENTHESES_CALL, UNMATCHED_QUALIFIED_PARENTHESES_CALL,
      UNMATCHED_RELATIONAL_OPERATION, UNMATCHED_THREE_OPERATION, UNMATCHED_TWO_OPERATION, UNMATCHED_TYPE_OPERATION,
      UNMATCHED_UNARY_NON_NUMERIC_OPERATION, UNMATCHED_UNARY_OPERATION, UNMATCHED_UNQUALIFIED_BRACKET_OPERATION, UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL,
      UNMATCHED_UNQUALIFIED_NO_PARENTHESES_CALL, UNMATCHED_UNQUALIFIED_PARENTHESES_CALL, UNMATCHED_WHEN_OPERATION),
  };

  /* ********************************************************** */
  // atNumericOperation |
  //                      captureNumericOperation |
  //                      unaryNumericOperation |
  //                      anonymousFunction |
  //                      parentheticalStab |
  //                      numeric |
  //                      list |
  //                      map |
  //                      tuple |
  //                      bitString |
  //                      stringLine notKeywordPairColon |
  //                      stringHeredoc |
  //                      charListLine notKeywordPairColon |
  //                      charListHeredoc |
  //                      interpolatedCharListSigilLine |
  //                      interpolatedCharListSigilHeredoc |
  //                      interpolatedRegexHeredoc |
  //                      interpolatedSigilHeredoc |
  //                      interpolatedStringSigilHeredoc |
  //                      interpolatedWordsHeredoc |
  //                      interpolatedWordsLine |
  //                      interpolatedRegexLine |
  //                      interpolatedSigilLine |
  //                      interpolatedStringSigilLine |
  //                      literalCharListSigilLine |
  //                      literalCharListSigilHeredoc |
  //                      literalRegexHeredoc |
  //                      literalSigilHeredoc |
  //                      literalStringSigilHeredoc |
  //                      literalWordsHeredoc |
  //                      literalRegexLine |
  //                      literalSigilLine |
  //                      literalStringSigilLine |
  //                      literalWordsLine |
  //                      atomKeyword |
  //                      atom |
  //                      alias
  public static boolean accessExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACCESS_EXPRESSION, "<access expression>");
    r = atNumericOperation(b, l + 1);
    if (!r) r = captureNumericOperation(b, l + 1);
    if (!r) r = unaryNumericOperation(b, l + 1);
    if (!r) r = anonymousFunction(b, l + 1);
    if (!r) r = parentheticalStab(b, l + 1);
    if (!r) r = numeric(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = map(b, l + 1);
    if (!r) r = tuple(b, l + 1);
    if (!r) r = bitString(b, l + 1);
    if (!r) r = accessExpression_10(b, l + 1);
    if (!r) r = stringHeredoc(b, l + 1);
    if (!r) r = accessExpression_12(b, l + 1);
    if (!r) r = charListHeredoc(b, l + 1);
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
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // stringLine notKeywordPairColon
  private static boolean accessExpression_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression_10")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stringLine(b, l + 1);
    r = r && notKeywordPairColon(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // charListLine notKeywordPairColon
  private static boolean accessExpression_12(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessExpression_12")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = charListLine(b, l + 1);
    r = r && notKeywordPairColon(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ADDITION_OPERATOR | SUBTRACTION_OPERATOR
  public static boolean additionInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additionInfixOperator")) return false;
    if (!nextTokenIs(b, "<+, ->", ADDITION_OPERATOR, SUBTRACTION_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ADDITION_INFIX_OPERATOR, "<+, ->");
    r = consumeToken(b, ADDITION_OPERATOR);
    if (!r) r = consumeToken(b, SUBTRACTION_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ALIAS_TOKEN notKeywordPairColon
  public static boolean alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias")) return false;
    if (!nextTokenIs(b, ALIAS_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ALIAS_TOKEN);
    r = r && notKeywordPairColon(b, l + 1);
    exit_section_(b, m, ALIAS, r);
    return r;
  }

  /* ********************************************************** */
  // AND_SYMBOL_OPERATOR | AND_WORD_OPERATOR
  public static boolean andInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andInfixOperator")) return false;
    if (!nextTokenIs(b, "<&&, &&&, and>", AND_SYMBOL_OPERATOR, AND_WORD_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AND_INFIX_OPERATOR, "<&&, &&&, and>");
    r = consumeToken(b, AND_SYMBOL_OPERATOR);
    if (!r) r = consumeToken(b, AND_WORD_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FN endOfExpressionMaybe
  //                       // -> is required, so use stabOperations directly and not stab as would be used used in `doBlock`
  //                       stab stabBodyExpressionSeparatorMaybe
  //                       END
  public static boolean anonymousFunction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonymousFunction")) return false;
    if (!nextTokenIs(b, FN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FN);
    r = r && endOfExpressionMaybe(b, l + 1);
    r = r && stab(b, l + 1);
    r = r && stabBodyExpressionSeparatorMaybe(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, m, ANONYMOUS_FUNCTION, r);
    return r;
  }

  /* ********************************************************** */
  // ARROW_OPERATOR
  public static boolean arrowInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrowInfixOperator")) return false;
    if (!nextTokenIs(b, "<<~, |>, ~>, <<<, <<~, <|>, <~>, >>>, ~>>>", ARROW_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARROW_INFIX_OPERATOR, "<<~, |>, ~>, <<<, <<~, <|>, <~>, >>>, ~>>>");
    r = consumeToken(b, ARROW_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ASSOCIATION_OPERATOR
  static boolean associationInfixOperator(PsiBuilder b, int l) {
    return consumeToken(b, ASSOCIATION_OPERATOR);
  }

  /* ********************************************************** */
  // associationsBase infixCommaMaybe
  public static boolean associations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associations")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSOCIATIONS, "<associations>");
    r = associationsBase(b, l + 1);
    r = r && infixCommaMaybe(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // associationsExpression (infixComma associationsExpression)*
  public static boolean associationsBase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associationsBase")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSOCIATIONS_BASE, "<associations base>");
    r = associationsExpression(b, l + 1);
    r = r && associationsBase_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (infixComma associationsExpression)*
  private static boolean associationsBase_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associationsBase_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!associationsBase_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "associationsBase_1", c)) break;
    }
    return true;
  }

  // infixComma associationsExpression
  private static boolean associationsBase_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associationsBase_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && associationsExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // containerAssociationOperation | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L505
  //                                    mapExpression
  static boolean associationsExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associationsExpression")) return false;
    boolean r;
    r = containerAssociationOperation(b, l + 1);
    if (!r) r = mapExpression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // atPrefixOperator IDENTIFIER_TOKEN
  public static boolean atIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atIdentifier")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && consumeToken(b, IDENTIFIER_TOKEN);
    exit_section_(b, m, AT_IDENTIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // atPrefixOperator maxExpression
  public static boolean atMaxExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atMaxExpression")) return false;
    if (!nextTokenIs(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && maxExpression(b, l + 1);
    exit_section_(b, m, MATCHED_AT_NON_NUMERIC_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // <<ifVersion 'LT' 'V_1_5'>> atPrefixOperator numeric
  public static boolean atNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atNumericOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AT_NUMERIC_OPERATION, "<at numeric operation>");
    r = ifVersion(b, l + 1, LT, V_1_5);
    r = r && atPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AT_OPERATOR
  public static boolean atPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atPrefixOperator")) return false;
    if (!nextTokenIs(b, "<@>", AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AT_PREFIX_OPERATOR, "<@>");
    r = consumeToken(b, AT_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
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
    r = consumeToken(b, ATOM_FRAGMENT);
    if (!r) r = quote(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // FALSE | NIL | TRUE
  public static boolean atomKeyword(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atomKeyword")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATOM_KEYWORD, "<false, nil, true>");
    r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NIL);
    if (!r) r = consumeToken(b, TRUE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // INVALID_BINARY_DIGITS | VALID_BINARY_DIGITS
  public static boolean binaryDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryDigits")) return false;
    if (!nextTokenIs(b, "<binary digits>", INVALID_BINARY_DIGITS, VALID_BINARY_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_DIGITS, "<binary digits>");
    r = consumeToken(b, INVALID_BINARY_DIGITS);
    if (!r) r = consumeToken(b, VALID_BINARY_DIGITS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX (BINARY_WHOLE_NUMBER_BASE | OBSOLETE_BINARY_WHOLE_NUMBER_BASE) binaryDigits (NUMBER_SEPARATOR? binaryDigits)*
  public static boolean binaryWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber")) return false;
    if (!nextTokenIs(b, BASE_WHOLE_NUMBER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BINARY_WHOLE_NUMBER, null);
    r = consumeToken(b, BASE_WHOLE_NUMBER_PREFIX);
    r = r && binaryWholeNumber_1(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, binaryDigits(b, l + 1));
    r = p && binaryWholeNumber_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // BINARY_WHOLE_NUMBER_BASE | OBSOLETE_BINARY_WHOLE_NUMBER_BASE
  private static boolean binaryWholeNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber_1")) return false;
    boolean r;
    r = consumeToken(b, BINARY_WHOLE_NUMBER_BASE);
    if (!r) r = consumeToken(b, OBSOLETE_BINARY_WHOLE_NUMBER_BASE);
    return r;
  }

  // (NUMBER_SEPARATOR? binaryDigits)*
  private static boolean binaryWholeNumber_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!binaryWholeNumber_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "binaryWholeNumber_3", c)) break;
    }
    return true;
  }

  // NUMBER_SEPARATOR? binaryDigits
  private static boolean binaryWholeNumber_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = binaryWholeNumber_3_0_0(b, l + 1);
    r = r && binaryDigits(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER_SEPARATOR?
  private static boolean binaryWholeNumber_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryWholeNumber_3_0_0")) return false;
    consumeToken(b, NUMBER_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // OPENING_BIT
  //               containerArgumentsMaybe
  //               CLOSING_BIT
  public static boolean bitString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitString")) return false;
    if (!nextTokenIs(b, OPENING_BIT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_BIT);
    r = r && containerArgumentsMaybe(b, l + 1);
    r = r && consumeToken(b, CLOSING_BIT);
    exit_section_(b, m, BIT_STRING, r);
    return r;
  }

  /* ********************************************************** */
  // AFTER | CATCH | ELSE | RESCUE
  public static boolean blockIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "blockIdentifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_IDENTIFIER, "<block identifier>");
    r = consumeToken(b, AFTER);
    if (!r) r = consumeToken(b, CATCH);
    if (!r) r = consumeToken(b, ELSE);
    if (!r) r = consumeToken(b, RESCUE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // blockIdentifier endOfExpressionMaybe // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L290-L291
  //               (stab endOfExpressionMaybe)?
  public static boolean blockItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "blockItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_ITEM, "<block item>");
    r = blockIdentifier(b, l + 1);
    r = r && endOfExpressionMaybe(b, l + 1);
    r = r && blockItem_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (stab endOfExpressionMaybe)?
  private static boolean blockItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "blockItem_2")) return false;
    blockItem_2_0(b, l + 1);
    return true;
  }

  // stab endOfExpressionMaybe
  private static boolean blockItem_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "blockItem_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stab(b, l + 1);
    r = r && endOfExpressionMaybe(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // blockItem+
  public static boolean blockList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "blockList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_LIST, "<block list>");
    r = blockItem(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!blockItem(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "blockList", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OPENING_BRACKET
  //                      (
  //                       keywords |
  //                       containerExpression infixCommaMaybe
  //                      )
  //                      CLOSING_BRACKET
  public static boolean bracketArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracketArguments")) return false;
    if (!nextTokenIs(b, OPENING_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_BRACKET);
    r = r && bracketArguments_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_BRACKET);
    exit_section_(b, m, BRACKET_ARGUMENTS, r);
    return r;
  }

  // keywords |
  //                       containerExpression infixCommaMaybe
  private static boolean bracketArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracketArguments_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keywords(b, l + 1);
    if (!r) r = bracketArguments_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // containerExpression infixCommaMaybe
  private static boolean bracketArguments_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracketArguments_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = containerExpression(b, l + 1);
    r = r && infixCommaMaybe(b, l + 1);
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
  // CAPTURE_OPERATOR
  public static boolean capturePrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "capturePrefixOperator")) return false;
    if (!nextTokenIs(b, "<&>", CAPTURE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CAPTURE_PREFIX_OPERATOR, "<&>");
    r = consumeToken(b, CAPTURE_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CHAR_LIST_HEREDOC_PROMOTER EOL
  //                     charListHeredocLine*
  //                     heredocPrefix CHAR_LIST_HEREDOC_TERMINATOR
  public static boolean charListHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredoc")) return false;
    if (!nextTokenIs(b, CHAR_LIST_HEREDOC_PROMOTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CHAR_LIST_HEREDOC, null);
    r = consumeTokens(b, 1, CHAR_LIST_HEREDOC_PROMOTER, EOL);
    p = r; // pin = CHAR_LIST_HEREDOC_PROMOTER
    r = r && report_error_(b, charListHeredoc_2(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && consumeToken(b, CHAR_LIST_HEREDOC_TERMINATOR) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // charListHeredocLine*
  private static boolean charListHeredoc_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredoc_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!charListHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "charListHeredoc_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix charListHeredocLineBody heredocLineEnd
  public static boolean charListHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CHAR_LIST_HEREDOC_LINE, "<char list heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && charListHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | CHAR_LIST_FRAGMENT | heredocEscapeSequence)*
  public static boolean charListHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, CHAR_LIST_HEREDOC_LINE_BODY, "<char list heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!charListHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "charListHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | CHAR_LIST_FRAGMENT | heredocEscapeSequence
  private static boolean charListHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListHeredocLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = heredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // CHAR_LIST_PROMOTER charListLineBody CHAR_LIST_TERMINATOR
  public static boolean charListLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListLine")) return false;
    if (!nextTokenIs(b, CHAR_LIST_PROMOTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CHAR_LIST_LINE, null);
    r = consumeToken(b, CHAR_LIST_PROMOTER);
    p = r; // pin = CHAR_LIST_PROMOTER
    r = r && report_error_(b, charListLineBody(b, l + 1));
    r = p && consumeToken(b, CHAR_LIST_TERMINATOR) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (interpolation | CHAR_LIST_FRAGMENT | lineEscapeSequence)*
  public static boolean charListLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, CHAR_LIST_LINE_BODY, "<char list line body>");
    while (true) {
      int c = current_position_(b);
      if (!charListLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "charListLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | CHAR_LIST_FRAGMENT | lineEscapeSequence
  private static boolean charListLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charListLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = lineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // CHAR_TOKENIZER (CHAR_LIST_FRAGMENT | lineEscapeSequence)
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

  // CHAR_LIST_FRAGMENT | lineEscapeSequence
  private static boolean charToken_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charToken_1")) return false;
    boolean r;
    r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = lineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // COMPARISON_OPERATOR
  public static boolean comparisonInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparisonInfixOperator")) return false;
    if (!nextTokenIs(b, "<!=, ==, =~, !==, ===>", COMPARISON_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPARISON_INFIX_OPERATOR, "<!=, ==, =~, !==, ===>");
    r = consumeToken(b, COMPARISON_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // containerArgumentsBase (infixComma keywords | infixComma)?
  static boolean containerArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArguments")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = containerArgumentsBase(b, l + 1);
    r = r && containerArguments_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma keywords | infixComma)?
  private static boolean containerArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArguments_1")) return false;
    containerArguments_1_0(b, l + 1);
    return true;
  }

  // infixComma keywords | infixComma
  private static boolean containerArguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArguments_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = containerArguments_1_0_0(b, l + 1);
    if (!r) r = infixComma(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // infixComma keywords
  private static boolean containerArguments_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArguments_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && keywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // containerExpression (infixComma containerExpression)*
  static boolean containerArgumentsBase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArgumentsBase")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = containerExpression(b, l + 1);
    r = r && containerArgumentsBase_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma containerExpression)*
  private static boolean containerArgumentsBase_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArgumentsBase_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!containerArgumentsBase_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "containerArgumentsBase_1", c)) break;
    }
    return true;
  }

  // infixComma containerExpression
  private static boolean containerArgumentsBase_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArgumentsBase_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && containerExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // containerArguments?
  static boolean containerArgumentsMaybe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerArgumentsMaybe")) return false;
    containerArguments(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // containerExpression associationInfixOperator containerExpression
  public static boolean containerAssociationOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerAssociationOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONTAINER_ASSOCIATION_OPERATION, "<container association operation>");
    r = containerExpression(b, l + 1);
    r = r && associationInfixOperator(b, l + 1);
    r = r && containerExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                                 unmatchedExpression
  static boolean containerExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerExpression")) return false;
    boolean r;
    r = emptyParentheses(b, l + 1);
    if (!r) r = unmatchedExpression(b, l + 1, -1);
    return r;
  }

  /* ********************************************************** */
  // INVALID_DECIMAL_DIGITS | VALID_DECIMAL_DIGITS
  public static boolean decimalDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalDigits")) return false;
    if (!nextTokenIs(b, "<decimal digits>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECIMAL_DIGITS, "<decimal digits>");
    r = consumeToken(b, INVALID_DECIMAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_DECIMAL_DIGITS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // decimalFloatIntegral DECIMAL_MARK decimalFloatFractional (EXPONENT_MARK decimalFloatExponent)?
  public static boolean decimalFloat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloat")) return false;
    if (!nextTokenIs(b, "<decimal float>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECIMAL_FLOAT, "<decimal float>");
    r = decimalFloatIntegral(b, l + 1);
    r = r && consumeToken(b, DECIMAL_MARK);
    r = r && decimalFloatFractional(b, l + 1);
    r = r && decimalFloat_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // decimalFloatExponentSign? decimalWholeNumber
  public static boolean decimalFloatExponent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatExponent")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECIMAL_FLOAT_EXPONENT, "<decimal float exponent>");
    r = decimalFloatExponent_0(b, l + 1);
    r = r && decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // decimalFloatExponentSign?
  private static boolean decimalFloatExponent_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatExponent_0")) return false;
    decimalFloatExponentSign(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // SIGN_OPERATOR
  public static boolean decimalFloatExponentSign(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatExponentSign")) return false;
    if (!nextTokenIs(b, SIGN_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SIGN_OPERATOR);
    exit_section_(b, m, DECIMAL_FLOAT_EXPONENT_SIGN, r);
    return r;
  }

  /* ********************************************************** */
  // decimalWholeNumber
  public static boolean decimalFloatFractional(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatFractional")) return false;
    if (!nextTokenIs(b, "<decimal float fractional>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECIMAL_FLOAT_FRACTIONAL, "<decimal float fractional>");
    r = decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // decimalWholeNumber
  public static boolean decimalFloatIntegral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalFloatIntegral")) return false;
    if (!nextTokenIs(b, "<decimal float integral>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECIMAL_FLOAT_INTEGRAL, "<decimal float integral>");
    r = decimalWholeNumber(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // decimalDigits (NUMBER_SEPARATOR? decimalDigits)*
  public static boolean decimalWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber")) return false;
    if (!nextTokenIs(b, "<decimal whole number>", INVALID_DECIMAL_DIGITS, VALID_DECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECIMAL_WHOLE_NUMBER, "<decimal whole number>");
    r = decimalDigits(b, l + 1);
    r = r && decimalWholeNumber_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (NUMBER_SEPARATOR? decimalDigits)*
  private static boolean decimalWholeNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!decimalWholeNumber_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "decimalWholeNumber_1", c)) break;
    }
    return true;
  }

  // NUMBER_SEPARATOR? decimalDigits
  private static boolean decimalWholeNumber_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = decimalWholeNumber_1_0_0(b, l + 1);
    r = r && decimalDigits(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER_SEPARATOR?
  private static boolean decimalWholeNumber_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimalWholeNumber_1_0_0")) return false;
    consumeToken(b, NUMBER_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // DO doStabSeparatorMaybe
  //             stab? stabBodyExpressionSeparatorMaybe // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L273
  //             blockList? stabBodyExpressionSeparatorMaybe // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L274
  //             END
  public static boolean doBlock(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doBlock")) return false;
    if (!nextTokenIs(b, DO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_BLOCK, null);
    r = consumeToken(b, DO);
    p = r; // pin = DO
    r = r && report_error_(b, doStabSeparatorMaybe(b, l + 1));
    r = p && report_error_(b, doBlock_2(b, l + 1)) && r;
    r = p && report_error_(b, stabBodyExpressionSeparatorMaybe(b, l + 1)) && r;
    r = p && report_error_(b, doBlock_4(b, l + 1)) && r;
    r = p && report_error_(b, stabBodyExpressionSeparatorMaybe(b, l + 1)) && r;
    r = p && consumeToken(b, END) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // stab?
  private static boolean doBlock_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doBlock_2")) return false;
    stab(b, l + 1);
    return true;
  }

  // blockList?
  private static boolean doBlock_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doBlock_4")) return false;
    blockList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // doBlock?
  static boolean doBlockMaybe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doBlockMaybe")) return false;
    doBlock(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // endOfExpression | eexWhitespace
  static boolean doStabSeparator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doStabSeparator")) return false;
    boolean r;
    r = endOfExpression(b, l + 1);
    if (!r) r = eexWhitespace(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // doStabSeparator?
  static boolean doStabSeparatorMaybe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doStabSeparatorMaybe")) return false;
    doStabSeparator(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // DOT_OPERATOR
  public static boolean dotInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dotInfixOperator")) return false;
    if (!nextTokenIs(b, "<.>", DOT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DOT_INFIX_OPERATOR, "<.>");
    r = consumeToken(b, DOT_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (EEX_DATA | EEX_ESCAPED_OPENING | eexTag)+
  public static boolean eex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EEX, "<eex>");
    r = eex_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!eex_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "eex", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // EEX_DATA | EEX_ESCAPED_OPENING | eexTag
  private static boolean eex_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eex_0")) return false;
    boolean r;
    r = consumeToken(b, EEX_DATA);
    if (!r) r = consumeToken(b, EEX_ESCAPED_OPENING);
    if (!r) r = eexTag(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // EEX_COMMENT_MARKER EEX_COMMENT?
  static boolean eexCommentBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexCommentBody")) return false;
    if (!nextTokenIs(b, EEX_COMMENT_MARKER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, EEX_COMMENT_MARKER);
    p = r; // pin = 1
    r = r && eexCommentBody_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // EEX_COMMENT?
  private static boolean eexCommentBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexCommentBody_1")) return false;
    consumeToken(b, EEX_COMMENT);
    return true;
  }

  /* ********************************************************** */
  // eexFunctionalElixirBody | eexProceduralElixirBody
  static boolean eexElixirBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexElixirBody")) return false;
    boolean r;
    r = eexFunctionalElixirBody(b, l + 1);
    if (!r) r = eexProceduralElixirBody(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // eexFunctionalMarker elixirFile
  static boolean eexFunctionalElixirBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFunctionalElixirBody")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = eexFunctionalMarker(b, l + 1);
    p = r; // pin = 1
    r = r && elixirFile(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EEX_EQUALS_MARKER | EEX_FORWARD_SLASH_MARKER | EEX_PIPE_MARKER
  static boolean eexFunctionalMarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFunctionalMarker")) return false;
    boolean r;
    r = consumeToken(b, EEX_EQUALS_MARKER);
    if (!r) r = consumeToken(b, EEX_FORWARD_SLASH_MARKER);
    if (!r) r = consumeToken(b, EEX_PIPE_MARKER);
    return r;
  }

  /* ********************************************************** */
  // eex | elixirFile
  static boolean eexOrElixirFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexOrElixirFile")) return false;
    boolean r;
    r = eex(b, l + 1);
    if (!r) r = elixirFile(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // EEX_EMPTY_MARKER elixirFile
  static boolean eexProceduralElixirBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexProceduralElixirBody")) return false;
    if (!nextTokenIs(b, EEX_EMPTY_MARKER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EEX_EMPTY_MARKER);
    r = r && elixirFile(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EEX_OPENING (eexCommentBody | eexElixirBody) EEX_CLOSING
  public static boolean eexTag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexTag")) return false;
    if (!nextTokenIs(b, EEX_OPENING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EEX_OPENING);
    r = r && eexTag_1(b, l + 1);
    r = r && consumeToken(b, EEX_CLOSING);
    exit_section_(b, m, EEX_TAG, r);
    return r;
  }

  // eexCommentBody | eexElixirBody
  private static boolean eexTag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexTag_1")) return false;
    boolean r;
    r = eexCommentBody(b, l + 1);
    if (!r) r = eexElixirBody(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // EEX_CLOSING EEX_OPENING EEX_EMPTY_MARKER
  static boolean eexWhitespace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexWhitespace")) return false;
    if (!nextTokenIs(b, EEX_CLOSING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, EEX_CLOSING, EEX_OPENING, EEX_EMPTY_MARKER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // endOfExpressionMaybe (expressionList endOfExpressionMaybe)?
  static boolean elixirFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = endOfExpressionMaybe(b, l + 1);
    r = r && elixirFile_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (expressionList endOfExpressionMaybe)?
  private static boolean elixirFile_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile_1")) return false;
    elixirFile_1_0(b, l + 1);
    return true;
  }

  // expressionList endOfExpressionMaybe
  private static boolean elixirFile_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirFile_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expressionList(b, l + 1);
    r = r && endOfExpressionMaybe(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // OPENING_PARENTHESIS CLOSING_PARENTHESIS
  public static boolean emptyParentheses(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyParentheses")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, OPENING_PARENTHESIS, CLOSING_PARENTHESIS);
    exit_section_(b, m, EMPTY_PARENTHESES, r);
    return r;
  }

  /* ********************************************************** */
  // OPENING_CURLY VALID_HEXADECIMAL_DIGITS CLOSING_CURLY
  public static boolean enclosedHexadecimalEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enclosedHexadecimalEscapeSequence")) return false;
    if (!nextTokenIs(b, OPENING_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENCLOSED_HEXADECIMAL_ESCAPE_SEQUENCE, null);
    r = consumeTokens(b, 1, OPENING_CURLY, VALID_HEXADECIMAL_DIGITS, CLOSING_CURLY);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // SEMICOLON | EOL+
  public static boolean endOfExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression")) return false;
    if (!nextTokenIs(b, "<end of expression>", EOL, SEMICOLON)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, END_OF_EXPRESSION, "<end of expression>");
    r = consumeToken(b, SEMICOLON);
    if (!r) r = endOfExpression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // EOL+
  private static boolean endOfExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EOL);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "endOfExpression_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // endOfExpression?
  static boolean endOfExpressionMaybe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpressionMaybe")) return false;
    endOfExpression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // EOL*
  static boolean eolStar(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eolStar")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "eolStar", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ESCAPE ESCAPED_CHARACTER_TOKEN
  public static boolean escapedCharacter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escapedCharacter")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ESCAPE, ESCAPED_CHARACTER_TOKEN);
    exit_section_(b, m, ESCAPED_CHARACTER, r);
    return r;
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
  //                        unmatchedExpression |
  //                        unqualifiedNoParenthesesManyArgumentsCall
  static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = emptyParentheses(b, l + 1);
    if (!r) r = unmatchedExpression(b, l + 1, -1);
    if (!r) r = unqualifiedNoParenthesesManyArgumentsCall(b, l + 1);
    exit_section_(b, l, m, r, false, ElixirParser::expressionRecoverWhile);
    return r;
  }

  /* ********************************************************** */
  // expression (endOfExpression expression)*
  static boolean expressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && expressionList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (endOfExpression expression)*
  private static boolean expressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expressionList_1", c)) break;
    }
    return true;
  }

  // endOfExpression expression
  private static boolean expressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = endOfExpression(b, l + 1);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EOL | CLOSING_BIT | CLOSING_BRACKET | CLOSING_CURLY | CLOSING_PARENTHESIS | COMMA | INTERPOLATION_END | SEMICOLON | STAB_OPERATOR | END | blockIdentifier | EEX_CLOSING
  static boolean expressionRecoverUntil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionRecoverUntil")) return false;
    boolean r;
    r = consumeToken(b, EOL);
    if (!r) r = consumeToken(b, CLOSING_BIT);
    if (!r) r = consumeToken(b, CLOSING_BRACKET);
    if (!r) r = consumeToken(b, CLOSING_CURLY);
    if (!r) r = consumeToken(b, CLOSING_PARENTHESIS);
    if (!r) r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, INTERPOLATION_END);
    if (!r) r = consumeToken(b, SEMICOLON);
    if (!r) r = consumeToken(b, STAB_OPERATOR);
    if (!r) r = consumeToken(b, END);
    if (!r) r = blockIdentifier(b, l + 1);
    if (!r) r = consumeToken(b, EEX_CLOSING);
    return r;
  }

  /* ********************************************************** */
  // !expressionRecoverUntil
  static boolean expressionRecoverWhile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionRecoverWhile")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !expressionRecoverUntil(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // quoteHexadecimalEscapeSequence |
  //                                   escapedCharacter
  static boolean heredocEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heredocEscapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    r = quoteHexadecimalEscapeSequence(b, l + 1);
    if (!r) r = escapedCharacter(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // escapedEOL | EOL
  static boolean heredocLineEnd(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heredocLineEnd")) return false;
    if (!nextTokenIs(b, "", EOL, ESCAPE)) return false;
    boolean r;
    r = escapedEOL(b, l + 1);
    if (!r) r = consumeToken(b, EOL);
    return r;
  }

  /* ********************************************************** */
  // HEREDOC_LINE_WHITE_SPACE_TOKEN?
  public static boolean heredocLinePrefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heredocLinePrefix")) return false;
    Marker m = enter_section_(b, l, _NONE_, HEREDOC_LINE_PREFIX, "<heredoc line prefix>");
    consumeToken(b, HEREDOC_LINE_WHITE_SPACE_TOKEN);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // HEREDOC_PREFIX_WHITE_SPACE?
  public static boolean heredocPrefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heredocPrefix")) return false;
    Marker m = enter_section_(b, l, _NONE_, HEREDOC_PREFIX, "<heredoc prefix>");
    consumeToken(b, HEREDOC_PREFIX_WHITE_SPACE);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // INVALID_HEXADECIMAL_DIGITS | VALID_HEXADECIMAL_DIGITS
  public static boolean hexadecimalDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalDigits")) return false;
    if (!nextTokenIs(b, "<hexadecimal digits>", INVALID_HEXADECIMAL_DIGITS, VALID_HEXADECIMAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEXADECIMAL_DIGITS, "<hexadecimal digits>");
    r = consumeToken(b, INVALID_HEXADECIMAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_HEXADECIMAL_DIGITS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ESCAPE (HEXADECIMAL_WHOLE_NUMBER_BASE | UNICODE_ESCAPE_CHARACTER)
  public static boolean hexadecimalEscapePrefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalEscapePrefix")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ESCAPE);
    r = r && hexadecimalEscapePrefix_1(b, l + 1);
    exit_section_(b, m, HEXADECIMAL_ESCAPE_PREFIX, r);
    return r;
  }

  // HEXADECIMAL_WHOLE_NUMBER_BASE | UNICODE_ESCAPE_CHARACTER
  private static boolean hexadecimalEscapePrefix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalEscapePrefix_1")) return false;
    boolean r;
    r = consumeToken(b, HEXADECIMAL_WHOLE_NUMBER_BASE);
    if (!r) r = consumeToken(b, UNICODE_ESCAPE_CHARACTER);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX (HEXADECIMAL_WHOLE_NUMBER_BASE | OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE) hexadecimalDigits (NUMBER_SEPARATOR? hexadecimalDigits)*
  public static boolean hexadecimalWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber")) return false;
    if (!nextTokenIs(b, BASE_WHOLE_NUMBER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, HEXADECIMAL_WHOLE_NUMBER, null);
    r = consumeToken(b, BASE_WHOLE_NUMBER_PREFIX);
    r = r && hexadecimalWholeNumber_1(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, hexadecimalDigits(b, l + 1));
    r = p && hexadecimalWholeNumber_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // HEXADECIMAL_WHOLE_NUMBER_BASE | OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE
  private static boolean hexadecimalWholeNumber_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_1")) return false;
    boolean r;
    r = consumeToken(b, HEXADECIMAL_WHOLE_NUMBER_BASE);
    if (!r) r = consumeToken(b, OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE);
    return r;
  }

  // (NUMBER_SEPARATOR? hexadecimalDigits)*
  private static boolean hexadecimalWholeNumber_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!hexadecimalWholeNumber_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "hexadecimalWholeNumber_3", c)) break;
    }
    return true;
  }

  // NUMBER_SEPARATOR? hexadecimalDigits
  private static boolean hexadecimalWholeNumber_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hexadecimalWholeNumber_3_0_0(b, l + 1);
    r = r && hexadecimalDigits(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER_SEPARATOR?
  private static boolean hexadecimalWholeNumber_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hexadecimalWholeNumber_3_0_0")) return false;
    consumeToken(b, NUMBER_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER_TOKEN
  public static boolean identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier")) return false;
    if (!nextTokenIs(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER_TOKEN);
    exit_section_(b, m, IDENTIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // IN_OPERATOR
  public static boolean inInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inInfixOperator")) return false;
    if (!nextTokenIs(b, "<in>", IN_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IN_INFIX_OPERATOR, "<in>");
    r = consumeToken(b, IN_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IN_MATCH_OPERATOR
  public static boolean inMatchInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inMatchInfixOperator")) return false;
    if (!nextTokenIs(b, "<<-, \\\\>", IN_MATCH_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IN_MATCH_INFIX_OPERATOR, "<<-, \\\\>");
    r = consumeToken(b, IN_MATCH_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COMMA eolStar
  static boolean infixComma(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixComma")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && eolStar(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // infixComma?
  static boolean infixCommaMaybe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infixCommaMaybe")) return false;
    infixComma(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedCharListHeredocLineBody heredocLineEnd
  public static boolean interpolatedCharListHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_CHAR_LIST_HEREDOC_LINE, "<interpolated char list heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedCharListHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | CHAR_LIST_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean interpolatedCharListHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_CHAR_LIST_HEREDOC_LINE_BODY, "<interpolated char list heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedCharListHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedCharListHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | CHAR_LIST_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean interpolatedCharListHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListHeredocLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
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
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_CHAR_LIST_SIGIL_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = CHAR_LIST_SIGIL_HEREDOC_PROMOTER
    r = r && report_error_(b, interpolatedCharListSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, CHAR_LIST_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interpolatedCharListHeredocLine*
  private static boolean interpolatedCharListSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigilHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interpolatedCharListHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedCharListSigilHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER interpolatedCharListSigilLineBody CHAR_LIST_SIGIL_TERMINATOR sigilModifiers
  public static boolean interpolatedCharListSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_CHAR_LIST_SIGIL_LINE, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    p = r; // pin = CHAR_LIST_SIGIL_PROMOTER
    r = r && report_error_(b, interpolatedCharListSigilLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, CHAR_LIST_SIGIL_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (interpolation | CHAR_LIST_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean interpolatedCharListSigilLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigilLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_CHAR_LIST_SIGIL_LINE_BODY, "<interpolated char list sigil line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedCharListSigilLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedCharListSigilLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | CHAR_LIST_FRAGMENT | sigilLineEscapeSequence
  private static boolean interpolatedCharListSigilLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListSigilLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
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
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_REGEX_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    p = r; // pin = REGEX_HEREDOC_PROMOTER
    r = r && report_error_(b, interpolatedRegexHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, REGEX_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interpolatedRegexHeredocLine*
  private static boolean interpolatedRegexHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interpolatedRegexHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedRegexHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedRegexHeredocLineBody heredocLineEnd
  public static boolean interpolatedRegexHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_REGEX_HEREDOC_LINE, "<interpolated regex heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedRegexHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | REGEX_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean interpolatedRegexHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_REGEX_HEREDOC_LINE_BODY, "<interpolated regex heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedRegexHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedRegexHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | REGEX_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean interpolatedRegexHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexHeredocLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, REGEX_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_PROMOTER interpolatedRegexLineBody REGEX_TERMINATOR sigilModifiers
  public static boolean interpolatedRegexLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_REGEX_LINE, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_PROMOTER);
    p = r; // pin = REGEX_PROMOTER
    r = r && report_error_(b, interpolatedRegexLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, REGEX_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (interpolation | REGEX_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean interpolatedRegexLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_REGEX_LINE_BODY, "<interpolated regex line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedRegexLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedRegexLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | REGEX_FRAGMENT | sigilLineEscapeSequence
  private static boolean interpolatedRegexLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedRegexLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, REGEX_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
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
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_SIGIL_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = SIGIL_HEREDOC_PROMOTER
    r = r && report_error_(b, interpolatedSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interpolatedSigilHeredocLine*
  private static boolean interpolatedSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interpolatedSigilHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedSigilHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedSigilHeredocLineBody heredocLineEnd
  public static boolean interpolatedSigilHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_SIGIL_HEREDOC_LINE, "<interpolated sigil heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedSigilHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | SIGIL_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean interpolatedSigilHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_SIGIL_HEREDOC_LINE_BODY, "<interpolated sigil heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedSigilHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedSigilHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | SIGIL_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean interpolatedSigilHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilHeredocLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, SIGIL_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_PROMOTER interpolatedSigilLineBody SIGIL_TERMINATOR sigilModifiers
  public static boolean interpolatedSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_SIGIL_LINE, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_PROMOTER);
    p = r; // pin = SIGIL_PROMOTER
    r = r && report_error_(b, interpolatedSigilLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, SIGIL_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (interpolation | SIGIL_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean interpolatedSigilLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_SIGIL_LINE_BODY, "<interpolated sigil line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedSigilLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedSigilLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | SIGIL_FRAGMENT | sigilLineEscapeSequence
  private static boolean interpolatedSigilLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedSigilLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, SIGIL_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedStringHeredocLineBody heredocLineEnd
  public static boolean interpolatedStringHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_STRING_HEREDOC_LINE, "<interpolated string heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedStringHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | STRING_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean interpolatedStringHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_STRING_HEREDOC_LINE_BODY, "<interpolated string heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedStringHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedStringHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | STRING_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean interpolatedStringHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringHeredocLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_HEREDOC_PROMOTER EOL
  //                                    interpolatedStringHeredocLine*
  //                                    heredocPrefix STRING_SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean interpolatedStringSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_STRING_SIGIL_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = STRING_SIGIL_HEREDOC_PROMOTER
    r = r && report_error_(b, interpolatedStringSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, STRING_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interpolatedStringHeredocLine*
  private static boolean interpolatedStringSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interpolatedStringHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedStringSigilHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER interpolatedStringSigilLineBody STRING_SIGIL_TERMINATOR sigilModifiers
  public static boolean interpolatedStringSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_STRING_SIGIL_LINE, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    p = r; // pin = STRING_SIGIL_PROMOTER
    r = r && report_error_(b, interpolatedStringSigilLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, STRING_SIGIL_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (interpolation | STRING_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean interpolatedStringSigilLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_STRING_SIGIL_LINE_BODY, "<interpolated string sigil line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedStringSigilLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedStringSigilLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | STRING_FRAGMENT | sigilLineEscapeSequence
  private static boolean interpolatedStringSigilLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringSigilLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
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
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_WORDS_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    p = r; // pin = WORDS_HEREDOC_PROMOTER
    r = r && report_error_(b, interpolatedWordsHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, WORDS_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interpolatedWordsHeredocLine*
  private static boolean interpolatedWordsHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interpolatedWordsHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedWordsHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix interpolatedWordsHeredocLineBody heredocLineEnd
  public static boolean interpolatedWordsHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_WORDS_HEREDOC_LINE, "<interpolated words heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && interpolatedWordsHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | WORDS_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean interpolatedWordsHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_WORDS_HEREDOC_LINE_BODY, "<interpolated words heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedWordsHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedWordsHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | WORDS_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean interpolatedWordsHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsHeredocLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, WORDS_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_WORDS_SIGIL_NAME WORDS_PROMOTER interpolatedWordsLineBody WORDS_TERMINATOR sigilModifiers
  public static boolean interpolatedWordsLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_WORDS_LINE, null);
    r = consumeTokens(b, 3, TILDE, INTERPOLATING_WORDS_SIGIL_NAME, WORDS_PROMOTER);
    p = r; // pin = WORDS_PROMOTER
    r = r && report_error_(b, interpolatedWordsLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, WORDS_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (interpolation | WORDS_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean interpolatedWordsLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, INTERPOLATED_WORDS_LINE_BODY, "<interpolated words line body>");
    while (true) {
      int c = current_position_(b);
      if (!interpolatedWordsLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedWordsLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | WORDS_FRAGMENT | sigilLineEscapeSequence
  private static boolean interpolatedWordsLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedWordsLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, WORDS_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
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
  // AFTER |
  //                ALIAS_TOKEN |
  //                AND_SYMBOL_OPERATOR |
  //                AND_WORD_OPERATOR |
  //                ARROW_OPERATOR |
  //                ASSOCIATION_OPERATOR |
  //                ATOM_FRAGMENT |
  //                AT_OPERATOR |
  //                BIT_STRING_OPERATOR |
  //                CAPTURE_OPERATOR |
  //                CATCH |
  //                COMPARISON_OPERATOR |
  //                DO |
  //                DIVISION_OPERATOR |
  //                ELSE |
  //                END |
  //                FALSE |
  //                IN_MATCH_OPERATOR |
  //                IN_OPERATOR |
  //                MAP_OPERATOR |
  //                MATCH_OPERATOR |
  //                MINUS_OPERATOR |
  //                MULTIPLICATION_OPERATOR |
  //                NIL |
  //                NOT_OPERATOR |
  //                OR_SYMBOL_OPERATOR |
  //                OR_WORD_OPERATOR |
  //                PIPE_OPERATOR |
  //                PLUS_OPERATOR |
  //                RANGE_OPERATOR |
  //                RESCUE |
  //                RELATIONAL_OPERATOR |
  //                SIGN_OPERATOR |
  //                STAB_OPERATOR |
  //                STRUCT_OPERATOR |
  //                THREE_OPERATOR |
  //                TRUE |
  //                TUPLE_OPERATOR |
  //                TWO_OPERATOR |
  //                UNARY_OPERATOR |
  //                WHEN_OPERATOR |
  //                quote
  public static boolean keywordKey(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordKey")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWORD_KEY, "<keyword key>");
    r = consumeToken(b, AFTER);
    if (!r) r = consumeToken(b, ALIAS_TOKEN);
    if (!r) r = consumeToken(b, AND_SYMBOL_OPERATOR);
    if (!r) r = consumeToken(b, AND_WORD_OPERATOR);
    if (!r) r = consumeToken(b, ARROW_OPERATOR);
    if (!r) r = consumeToken(b, ASSOCIATION_OPERATOR);
    if (!r) r = consumeToken(b, ATOM_FRAGMENT);
    if (!r) r = consumeToken(b, AT_OPERATOR);
    if (!r) r = consumeToken(b, BIT_STRING_OPERATOR);
    if (!r) r = consumeToken(b, CAPTURE_OPERATOR);
    if (!r) r = consumeToken(b, CATCH);
    if (!r) r = consumeToken(b, COMPARISON_OPERATOR);
    if (!r) r = consumeToken(b, DO);
    if (!r) r = consumeToken(b, DIVISION_OPERATOR);
    if (!r) r = consumeToken(b, ELSE);
    if (!r) r = consumeToken(b, END);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, IN_MATCH_OPERATOR);
    if (!r) r = consumeToken(b, IN_OPERATOR);
    if (!r) r = consumeToken(b, MAP_OPERATOR);
    if (!r) r = consumeToken(b, MATCH_OPERATOR);
    if (!r) r = consumeToken(b, MINUS_OPERATOR);
    if (!r) r = consumeToken(b, MULTIPLICATION_OPERATOR);
    if (!r) r = consumeToken(b, NIL);
    if (!r) r = consumeToken(b, NOT_OPERATOR);
    if (!r) r = consumeToken(b, OR_SYMBOL_OPERATOR);
    if (!r) r = consumeToken(b, OR_WORD_OPERATOR);
    if (!r) r = consumeToken(b, PIPE_OPERATOR);
    if (!r) r = consumeToken(b, PLUS_OPERATOR);
    if (!r) r = consumeToken(b, RANGE_OPERATOR);
    if (!r) r = consumeToken(b, RESCUE);
    if (!r) r = consumeToken(b, RELATIONAL_OPERATOR);
    if (!r) r = consumeToken(b, SIGN_OPERATOR);
    if (!r) r = consumeToken(b, STAB_OPERATOR);
    if (!r) r = consumeToken(b, STRUCT_OPERATOR);
    if (!r) r = consumeToken(b, THREE_OPERATOR);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, TUPLE_OPERATOR);
    if (!r) r = consumeToken(b, TWO_OPERATOR);
    if (!r) r = consumeToken(b, UNARY_OPERATOR);
    if (!r) r = consumeToken(b, WHEN_OPERATOR);
    if (!r) r = quote(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keywordKey KEYWORD_PAIR_COLON
  static boolean keywordKeyColon(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordKeyColon")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keywordKey(b, l + 1);
    r = r && consumeToken(b, KEYWORD_PAIR_COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keywordKeyColon containerExpression
  public static boolean keywordPair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordPair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWORD_PAIR, "<keyword pair>");
    r = keywordKeyColon(b, l + 1);
    r = r && containerExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keywordPair (infixComma keywordPair)* COMMA?
  public static boolean keywords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWORDS, "<keywords>");
    r = keywordPair(b, l + 1);
    r = r && keywords_1(b, l + 1);
    r = r && keywords_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (infixComma keywordPair)*
  private static boolean keywords_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keywords_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keywords_1", c)) break;
    }
    return true;
  }

  // infixComma keywordPair
  private static boolean keywords_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && keywordPair(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean keywords_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // quoteHexadecimalEscapeSequence |
  //                                escapedEOL |
  //                                /* Must be last so that ESCAPE ('\') can be pinned in escapedCharacter without excluding
  //                                   ("\x") in hexadecimalEscapeSequence  */
  //                                escapedCharacter
  static boolean lineEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lineEscapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    r = quoteHexadecimalEscapeSequence(b, l + 1);
    if (!r) r = escapedEOL(b, l + 1);
    if (!r) r = escapedCharacter(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // OPENING_BRACKET
  //          listArguments?
  //          CLOSING_BRACKET
  public static boolean list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list")) return false;
    if (!nextTokenIs(b, OPENING_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_BRACKET);
    r = r && list_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_BRACKET);
    exit_section_(b, m, LIST, r);
    return r;
  }

  // listArguments?
  private static boolean list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_1")) return false;
    listArguments(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // keywords |
  //                           containerArguments
  static boolean listArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listArguments")) return false;
    boolean r;
    r = keywords(b, l + 1);
    if (!r) r = containerArguments(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalCharListHeredocLineBody heredocLineEnd
  public static boolean literalCharListHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_CHAR_LIST_HEREDOC_LINE, "<literal char list heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalCharListHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (CHAR_LIST_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean literalCharListHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_CHAR_LIST_HEREDOC_LINE_BODY, "<literal char list heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalCharListHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalCharListHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // CHAR_LIST_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean literalCharListHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListHeredocLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
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
    Marker m = enter_section_(b, l, _NONE_, LITERAL_CHAR_LIST_SIGIL_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = CHAR_LIST_SIGIL_HEREDOC_PROMOTER
    r = r && report_error_(b, literalCharListSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, CHAR_LIST_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // literalCharListHeredocLine*
  private static boolean literalCharListSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigilHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!literalCharListHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalCharListSigilHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER literalCharListSigilLineBody CHAR_LIST_SIGIL_TERMINATOR sigilModifiers
  public static boolean literalCharListSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_CHAR_LIST_SIGIL_LINE, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    p = r; // pin = CHAR_LIST_SIGIL_PROMOTER
    r = r && report_error_(b, literalCharListSigilLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, CHAR_LIST_SIGIL_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (CHAR_LIST_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean literalCharListSigilLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigilLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_CHAR_LIST_SIGIL_LINE_BODY, "<literal char list sigil line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalCharListSigilLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalCharListSigilLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // CHAR_LIST_FRAGMENT | sigilLineEscapeSequence
  private static boolean literalCharListSigilLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalCharListSigilLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, CHAR_LIST_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_REGEX_SIGIL_NAME REGEX_HEREDOC_PROMOTER EOL
  //                         literalRegexHeredocLine*
  //                         heredocPrefix REGEX_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalRegexHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_REGEX_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    p = r; // pin = REGEX_HEREDOC_PROMOTER
    r = r && report_error_(b, literalRegexHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, REGEX_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // literalRegexHeredocLine*
  private static boolean literalRegexHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!literalRegexHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalRegexHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalRegexHeredocLineBody heredocLineEnd
  public static boolean literalRegexHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_REGEX_HEREDOC_LINE, "<literal regex heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalRegexHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (REGEX_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean literalRegexHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_REGEX_HEREDOC_LINE_BODY, "<literal regex heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalRegexHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalRegexHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // REGEX_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean literalRegexHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexHeredocLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, REGEX_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_REGEX_SIGIL_NAME REGEX_PROMOTER literalRegexLineBody REGEX_TERMINATOR sigilModifiers
  public static boolean literalRegexLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_REGEX_LINE, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_REGEX_SIGIL_NAME, REGEX_PROMOTER);
    p = r; // pin = REGEX_PROMOTER
    r = r && report_error_(b, literalRegexLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, REGEX_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (REGEX_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean literalRegexLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_REGEX_LINE_BODY, "<literal regex line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalRegexLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalRegexLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // REGEX_FRAGMENT | sigilLineEscapeSequence
  private static boolean literalRegexLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalRegexLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, REGEX_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                         literalSigilHeredocLine*
  //                         heredocPrefix SIGIL_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalSigilHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_SIGIL_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = SIGIL_HEREDOC_PROMOTER
    r = r && report_error_(b, literalSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // literalSigilHeredocLine*
  private static boolean literalSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!literalSigilHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalSigilHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalSigilHeredocLineBody heredocLineEnd
  public static boolean literalSigilHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_SIGIL_HEREDOC_LINE, "<literal sigil heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalSigilHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (SIGIL_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean literalSigilHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_SIGIL_HEREDOC_LINE_BODY, "<literal sigil heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalSigilHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalSigilHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // SIGIL_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean literalSigilHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilHeredocLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, SIGIL_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_PROMOTER literalSigilLineBody SIGIL_TERMINATOR sigilModifiers
  public static boolean literalSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_SIGIL_LINE, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_SIGIL_NAME, SIGIL_PROMOTER);
    p = r; // pin = SIGIL_PROMOTER
    r = r && report_error_(b, literalSigilLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, SIGIL_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (SIGIL_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean literalSigilLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_SIGIL_LINE_BODY, "<literal sigil line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalSigilLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalSigilLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // SIGIL_FRAGMENT | sigilLineEscapeSequence
  private static boolean literalSigilLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalSigilLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, SIGIL_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalStringHeredocLineBody heredocLineEnd
  public static boolean literalStringHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_STRING_HEREDOC_LINE, "<literal string heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalStringHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (STRING_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean literalStringHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_STRING_HEREDOC_LINE_BODY, "<literal string heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalStringHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalStringHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // STRING_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean literalStringHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringHeredocLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
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
    Marker m = enter_section_(b, l, _NONE_, LITERAL_STRING_SIGIL_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    p = r; // pin = STRING_SIGIL_HEREDOC_PROMOTER
    r = r && report_error_(b, literalStringSigilHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, STRING_SIGIL_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // literalStringHeredocLine*
  private static boolean literalStringSigilHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigilHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!literalStringHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalStringSigilHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER literalStringSigilLineBody STRING_SIGIL_TERMINATOR sigilModifiers
  public static boolean literalStringSigilLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigilLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_STRING_SIGIL_LINE, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    p = r; // pin = STRING_SIGIL_PROMOTER
    r = r && report_error_(b, literalStringSigilLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, STRING_SIGIL_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (STRING_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean literalStringSigilLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigilLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_STRING_SIGIL_LINE_BODY, "<literal string sigil line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalStringSigilLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalStringSigilLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // STRING_FRAGMENT | sigilLineEscapeSequence
  private static boolean literalStringSigilLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalStringSigilLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_WORDS_SIGIL_NAME WORDS_HEREDOC_PROMOTER EOL
  //                         literalWordsHeredocLine*
  //                         heredocPrefix WORDS_HEREDOC_TERMINATOR sigilModifiers
  public static boolean literalWordsHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredoc")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_WORDS_HEREDOC, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    p = r; // pin = WORDS_HEREDOC_PROMOTER
    r = r && report_error_(b, literalWordsHeredoc_4(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, WORDS_HEREDOC_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // literalWordsHeredocLine*
  private static boolean literalWordsHeredoc_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredoc_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!literalWordsHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalWordsHeredoc_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix literalWordsHeredocLineBody heredocLineEnd
  public static boolean literalWordsHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_WORDS_HEREDOC_LINE, "<literal words heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && literalWordsHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (WORDS_FRAGMENT | sigilHeredocEscapeSequence)*
  public static boolean literalWordsHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_WORDS_HEREDOC_LINE_BODY, "<literal words heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalWordsHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalWordsHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // WORDS_FRAGMENT | sigilHeredocEscapeSequence
  private static boolean literalWordsHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsHeredocLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, WORDS_FRAGMENT);
    if (!r) r = sigilHeredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TILDE LITERAL_WORDS_SIGIL_NAME WORDS_PROMOTER literalWordsLineBody WORDS_TERMINATOR sigilModifiers
  public static boolean literalWordsLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsLine")) return false;
    if (!nextTokenIs(b, TILDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_WORDS_LINE, null);
    r = consumeTokens(b, 3, TILDE, LITERAL_WORDS_SIGIL_NAME, WORDS_PROMOTER);
    p = r; // pin = WORDS_PROMOTER
    r = r && report_error_(b, literalWordsLineBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, WORDS_TERMINATOR)) && r;
    r = p && sigilModifiers(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (WORDS_FRAGMENT | sigilLineEscapeSequence)*
  public static boolean literalWordsLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_WORDS_LINE_BODY, "<literal words line body>");
    while (true) {
      int c = current_position_(b);
      if (!literalWordsLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "literalWordsLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // WORDS_FRAGMENT | sigilLineEscapeSequence
  private static boolean literalWordsLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalWordsLineBody_0")) return false;
    boolean r;
    r = consumeToken(b, WORDS_FRAGMENT);
    if (!r) r = sigilLineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // mapOperation |
  //                 structOperation
  static boolean map(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "map")) return false;
    if (!nextTokenIs(b, STRUCT_OPERATOR)) return false;
    boolean r;
    r = mapOperation(b, l + 1);
    if (!r) r = structOperation(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // OPENING_CURLY
  //                  (
  //                   // Must be before mapConstructionArguments, so that PIPE_OPERATOR is used for updates and not matchedExpression.
  //                   mapUpdateArguments |
  //                   mapConstructionArguments
  //                  )?
  //                  CLOSING_CURLY
  public static boolean mapArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapArguments")) return false;
    if (!nextTokenIs(b, OPENING_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_CURLY);
    r = r && mapArguments_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_CURLY);
    exit_section_(b, m, MAP_ARGUMENTS, r);
    return r;
  }

  // (
  //                   // Must be before mapConstructionArguments, so that PIPE_OPERATOR is used for updates and not matchedExpression.
  //                   mapUpdateArguments |
  //                   mapConstructionArguments
  //                  )?
  private static boolean mapArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapArguments_1")) return false;
    mapArguments_1_0(b, l + 1);
    return true;
  }

  // mapUpdateArguments |
  //                   mapConstructionArguments
  private static boolean mapArguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapArguments_1_0")) return false;
    boolean r;
    r = mapUpdateArguments(b, l + 1);
    if (!r) r = mapConstructionArguments(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // mapTailArguments
  public static boolean mapConstructionArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapConstructionArguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAP_CONSTRUCTION_ARGUMENTS, "<map construction arguments>");
    r = mapTailArguments(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // maxExpression | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L498-L499
  //                           atMaxExpression
  static boolean mapExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapExpression")) return false;
    boolean r;
    r = maxExpression(b, l + 1);
    if (!r) r = atMaxExpression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // mapPrefixOperator mapArguments
  public static boolean mapOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapOperation")) return false;
    if (!nextTokenIs(b, STRUCT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = mapPrefixOperator(b, l + 1);
    r = r && mapArguments(b, l + 1);
    exit_section_(b, m, MAP_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // STRUCT_OPERATOR
  public static boolean mapPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapPrefixOperator")) return false;
    if (!nextTokenIs(b, "<%>", STRUCT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAP_PREFIX_OPERATOR, "<%>");
    r = consumeToken(b, STRUCT_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keywords | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L524
  //                              associationsBase infixComma keywords | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L526
  //                              associations
  static boolean mapTailArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTailArguments")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keywords(b, l + 1);
    if (!r) r = mapTailArguments_1(b, l + 1);
    if (!r) r = associations(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // associationsBase infixComma keywords
  private static boolean mapTailArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTailArguments_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = associationsBase(b, l + 1);
    r = r && infixComma(b, l + 1);
    r = r && keywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // matchedMatchOperation pipeInfixOperator mapTailArguments
  public static boolean mapUpdateArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapUpdateArguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAP_UPDATE_ARGUMENTS, "<map update arguments>");
    r = matchedExpression(b, l + 1, 6);
    r = r && pipeInfixOperator(b, l + 1);
    r = r && mapTailArguments(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MATCH_OPERATOR
  public static boolean matchInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchInfixOperator")) return false;
    if (!nextTokenIs(b, "<=>", MATCH_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MATCH_INFIX_OPERATOR, "<=>");
    r = consumeToken(b, MATCH_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CALL parenthesesArguments parenthesesArguments?
  public static boolean matchedParenthesesArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedParenthesesArguments")) return false;
    if (!nextTokenIs(b, CALL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CALL);
    r = r && parenthesesArguments(b, l + 1);
    r = r && matchedParenthesesArguments_2(b, l + 1);
    exit_section_(b, m, MATCHED_PARENTHESES_ARGUMENTS, r);
    return r;
  }

  // parenthesesArguments?
  private static boolean matchedParenthesesArguments_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedParenthesesArguments_2")) return false;
    parenthesesArguments(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // dotInfixOperator parenthesesArguments parenthesesArguments?
  public static boolean maxDotCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxDotCall")) return false;
    if (!nextTokenIs(b, DOT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, MATCHED_DOT_CALL, null);
    r = dotInfixOperator(b, l + 1);
    r = r && parenthesesArguments(b, l + 1);
    r = r && maxDotCall_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // parenthesesArguments?
  private static boolean maxDotCall_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxDotCall_2")) return false;
    parenthesesArguments(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // matchedBracketOperation maxDotCall |
  //                           /* matchedQualifiedBracketOperation because it is in the Pratt-parsing table for
  //                              matchedExpression will match matchedQualifiedBracketOperation or anything after it in
  //                              matchedExpression.  matchedQualifiedBracketOperation is used because it is the next rule
  //                              after matchedQualifiedAliasOperation. maxQualifiedAlias needs to be `left` and `+` to
  //                              emulate the POSTFIX behavior for matchedQualifiedAliasOperation.
  //                              matchedQualifiedAliasOperation cannot be used because the Pratt-parsing table will allow
  //                              matchedQualifiedAliasOperation to match it or any lower rule. */
  //                           matchedQualifiedBracketOperation maxQualifiedAlias+ | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L231
  //                           /* matchedQualifiedNoArgumentsCall because it is first rule after
  //                             matchedQualifiedParenthesesCall */
  //                           matchedQualifiedNoArgumentsCall maxQualifiedParenthesesCall | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L231
  //                           /* matchedAtUnqualifiedBracketOperation and all rules thrugh accessExpression are necessary
  //                              because all those rules are ATOM or PREFIX so they won't also match lower rules */
  //                           (
  //                            matchedAtUnqualifiedBracketOperation |
  //                            matchedAtNumericBracketOperation |
  //                            matchedAtNonNumericOperation |
  //                            matchedAtOperation |
  //                            matchedUnqualifiedParenthesesCall |
  //                            matchedUnqualifiedBracketOperation |
  //                            variable |
  //                            accessExpression
  //                           ) maxQualifiedNoArgumentsCall | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L499
  //                           matchedUnqualifiedParenthesesCall | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L231
  //                           variable | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L499
  //                           atom | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L226-L228
  //                           alias
  static boolean maxExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = maxExpression_0(b, l + 1);
    if (!r) r = maxExpression_1(b, l + 1);
    if (!r) r = maxExpression_2(b, l + 1);
    if (!r) r = maxExpression_3(b, l + 1);
    if (!r) r = matchedUnqualifiedParenthesesCall(b, l + 1);
    if (!r) r = variable(b, l + 1);
    if (!r) r = atom(b, l + 1);
    if (!r) r = alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedBracketOperation maxDotCall
  private static boolean maxExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1, 25);
    r = r && maxDotCall(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedQualifiedBracketOperation maxQualifiedAlias+
  private static boolean maxExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1, 28);
    r = r && maxExpression_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // maxQualifiedAlias+
  private static boolean maxExpression_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = maxQualifiedAlias(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!maxQualifiedAlias(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "maxExpression_1_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedQualifiedNoArgumentsCall maxQualifiedParenthesesCall
  private static boolean maxExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1, 30);
    r = r && maxQualifiedParenthesesCall(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (
  //                            matchedAtUnqualifiedBracketOperation |
  //                            matchedAtNumericBracketOperation |
  //                            matchedAtNonNumericOperation |
  //                            matchedAtOperation |
  //                            matchedUnqualifiedParenthesesCall |
  //                            matchedUnqualifiedBracketOperation |
  //                            variable |
  //                            accessExpression
  //                           ) maxQualifiedNoArgumentsCall
  private static boolean maxExpression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = maxExpression_3_0(b, l + 1);
    r = r && maxQualifiedNoArgumentsCall(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedAtUnqualifiedBracketOperation |
  //                            matchedAtNumericBracketOperation |
  //                            matchedAtNonNumericOperation |
  //                            matchedAtOperation |
  //                            matchedUnqualifiedParenthesesCall |
  //                            matchedUnqualifiedBracketOperation |
  //                            variable |
  //                            accessExpression
  private static boolean maxExpression_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxExpression_3_0")) return false;
    boolean r;
    r = matchedAtUnqualifiedBracketOperation(b, l + 1);
    if (!r) r = matchedAtNumericBracketOperation(b, l + 1);
    if (!r) r = matchedAtNonNumericOperation(b, l + 1);
    if (!r) r = matchedAtOperation(b, l + 1);
    if (!r) r = matchedUnqualifiedParenthesesCall(b, l + 1);
    if (!r) r = matchedUnqualifiedBracketOperation(b, l + 1);
    if (!r) r = variable(b, l + 1);
    if (!r) r = accessExpression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // dotInfixOperator alias
  public static boolean maxQualifiedAlias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxQualifiedAlias")) return false;
    if (!nextTokenIs(b, DOT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, MATCHED_QUALIFIED_ALIAS, null);
    r = dotInfixOperator(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // dotInfixOperator relativeIdentifier !CALL
  public static boolean maxQualifiedNoArgumentsCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxQualifiedNoArgumentsCall")) return false;
    if (!nextTokenIs(b, DOT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, MATCHED_QUALIFIED_NO_ARGUMENTS_CALL, null);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && maxQualifiedNoArgumentsCall_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // !CALL
  private static boolean maxQualifiedNoArgumentsCall_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxQualifiedNoArgumentsCall_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, CALL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // dotInfixOperator relativeIdentifier matchedParenthesesArguments
  public static boolean maxQualifiedParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxQualifiedParenthesesCall")) return false;
    if (!nextTokenIs(b, DOT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, MATCHED_QUALIFIED_PARENTHESES_CALL, null);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && matchedParenthesesArguments(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OPENING_CURLY
  //                     containerArgumentsMaybe
  //                     CLOSING_CURLY
  public static boolean multipleAliases(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multipleAliases")) return false;
    if (!nextTokenIs(b, OPENING_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_CURLY);
    r = r && containerArgumentsMaybe(b, l + 1);
    r = r && consumeToken(b, CLOSING_CURLY);
    exit_section_(b, m, MULTIPLE_ALIASES, r);
    return r;
  }

  /* ********************************************************** */
  // DIVISION_OPERATOR | MULTIPLICATION_OPERATOR
  public static boolean multiplicationInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicationInfixOperator")) return false;
    if (!nextTokenIs(b, "<*, />", DIVISION_OPERATOR, MULTIPLICATION_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MULTIPLICATION_INFIX_OPERATOR, "<*, />");
    r = consumeToken(b, DIVISION_OPERATOR);
    if (!r) r = consumeToken(b, MULTIPLICATION_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // stabInfixOperator stabBody?
  public static boolean noArgumentStabOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noArgumentStabOperation")) return false;
    if (!nextTokenIs(b, STAB_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabInfixOperator(b, l + 1);
    r = r && noArgumentStabOperation_1(b, l + 1);
    exit_section_(b, m, STAB_OPERATION, r);
    return r;
  }

  // stabBody?
  private static boolean noArgumentStabOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noArgumentStabOperation_1")) return false;
    stabBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // noParenthesesOneArgument |
  //                            noParenthesesManyArguments
  public static boolean noParenthesesArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesArguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_PARENTHESES_ARGUMENTS, "<no parentheses arguments>");
    r = noParenthesesOneArgument(b, l + 1);
    if (!r) r = noParenthesesManyArguments(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //                                     /* Must be before matchedExpression because noParenthesesExpression is
  //                                        `matchedExpressionDotIdentifier callArgumentsNoParenthesesManyStrict` which is
  //                                        longer than `matchedExpressionDotIdentifier` in matchedExpression. */
  //                                     /* This will be marked as an error by
  //                                        {@link org.elixir_lang.inspection.NoParenthesesManyStrict} */
  //                                     noParenthesesManyStrictNoParenthesesExpression |
  //                                     matchedExpression
  static boolean noParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesExpression")) return false;
    boolean r;
    r = emptyParentheses(b, l + 1);
    if (!r) r = noParenthesesManyStrictNoParenthesesExpression(b, l + 1);
    if (!r) r = matchedExpression(b, l + 1, -1);
    return r;
  }

  /* ********************************************************** */
  // keywordKeyColon noParenthesesExpression
  public static boolean noParenthesesKeywordPair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywordPair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_PARENTHESES_KEYWORD_PAIR, "<no parentheses keyword pair>");
    r = keywordKeyColon(b, l + 1);
    r = r && noParenthesesExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesKeywordPair (infixComma noParenthesesKeywordPair)*
  public static boolean noParenthesesKeywords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywords")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_PARENTHESES_KEYWORDS, "<no parentheses keywords>");
    r = noParenthesesKeywordPair(b, l + 1);
    r = r && noParenthesesKeywords_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (infixComma noParenthesesKeywordPair)*
  private static boolean noParenthesesKeywords_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesKeywords_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!noParenthesesKeywords_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "noParenthesesKeywords_1", c)) break;
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
  // matchedExpression infixComma noParenthesesKeywords |
  //                                        matchedExpression (infixComma noParenthesesExpression)+ (infixComma noParenthesesKeywords)?
  static boolean noParenthesesManyArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArguments_0(b, l + 1);
    if (!r) r = noParenthesesManyArguments_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedExpression infixComma noParenthesesKeywords
  private static boolean noParenthesesManyArguments_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1, -1);
    r = r && infixComma(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // matchedExpression (infixComma noParenthesesExpression)+ (infixComma noParenthesesKeywords)?
  private static boolean noParenthesesManyArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchedExpression(b, l + 1, -1);
    r = r && noParenthesesManyArguments_1_1(b, l + 1);
    r = r && noParenthesesManyArguments_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma noParenthesesExpression)+
  private static boolean noParenthesesManyArguments_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noParenthesesManyArguments_1_1_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!noParenthesesManyArguments_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "noParenthesesManyArguments_1_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // infixComma noParenthesesExpression
  private static boolean noParenthesesManyArguments_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && noParenthesesExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma noParenthesesKeywords)?
  private static boolean noParenthesesManyArguments_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1_2")) return false;
    noParenthesesManyArguments_1_2_0(b, l + 1);
    return true;
  }

  // infixComma noParenthesesKeywords
  private static boolean noParenthesesManyArguments_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArguments_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesManyArguments |
  //                                              noParenthesesStrict
  static boolean noParenthesesManyArgumentsStrict(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyArgumentsStrict")) return false;
    boolean r;
    r = noParenthesesManyArguments(b, l + 1);
    if (!r) r = noParenthesesStrict(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // unqualifiedNoParenthesesManyArgumentsCall
  public static boolean noParenthesesManyStrictNoParenthesesExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesManyStrictNoParenthesesExpression")) return false;
    if (!nextTokenIs(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unqualifiedNoParenthesesManyArgumentsCall(b, l + 1);
    exit_section_(b, m, NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesKeywords | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L417
  //                              unqualifiedNoParenthesesManyArgumentsCall | // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L419
  //                              /* This should NOT be in matchedExpression as it's not in matched_expr, but in no_parens_expr,
  //                                 but having a rule that starts with matchedExpression is only legal in a rule that extends
  //                                 matchedExpression.
  //                                 @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L124-L125 */
  //                              noParenthesesManyArgumentsStrict |
  //                              /* MUST be after noParenthesesManyArgumentsStrict so that matchedExpression's inbuilt error handling doesn't match with error.
  //                                 NOTE this is used in both unmatchedExpression and matchedExpression.  Using
  //                                 matchedExpression here ensures the `do` block is only consumed by the left-most
  //                                 unmatchedExpression call and not any of the middle matchedExpression calls.
  //                                 @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L418
  //                                 @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L609-L610 */
  //                              matchedExpression
  public static boolean noParenthesesOneArgument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParenthesesOneArgument")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_PARENTHESES_ONE_ARGUMENT, "<no parentheses one argument>");
    r = noParenthesesKeywords(b, l + 1);
    if (!r) r = unqualifiedNoParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = noParenthesesManyArgumentsStrict(b, l + 1);
    if (!r) r = matchedExpression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
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
    r = noParenthesesKeywords(b, l + 1);
    if (!r) r = noParenthesesManyArguments(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // !numeric
  static boolean nonNumeric(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonNumeric")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !numeric(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NOT_OPERATOR
  public static boolean notInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notInfixOperator")) return false;
    if (!nextTokenIs(b, "<not>", NOT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NOT_INFIX_OPERATOR, "<not>");
    r = consumeToken(b, NOT_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // !KEYWORD_PAIR_COLON
  static boolean notKeywordPairColon(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notKeywordPairColon")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, KEYWORD_PAIR_COLON);
    exit_section_(b, l, m, r, false, null);
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
    r = charToken(b, l + 1);
    if (!r) r = binaryWholeNumber(b, l + 1);
    if (!r) r = decimalFloat(b, l + 1);
    if (!r) r = decimalWholeNumber(b, l + 1);
    if (!r) r = hexadecimalWholeNumber(b, l + 1);
    if (!r) r = octalWholeNumber(b, l + 1);
    if (!r) r = unknownBaseWholeNumber(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // INVALID_OCTAL_DIGITS | VALID_OCTAL_DIGITS
  public static boolean octalDigits(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalDigits")) return false;
    if (!nextTokenIs(b, "<octal digits>", INVALID_OCTAL_DIGITS, VALID_OCTAL_DIGITS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OCTAL_DIGITS, "<octal digits>");
    r = consumeToken(b, INVALID_OCTAL_DIGITS);
    if (!r) r = consumeToken(b, VALID_OCTAL_DIGITS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BASE_WHOLE_NUMBER_PREFIX OCTAL_WHOLE_NUMBER_BASE octalDigits (NUMBER_SEPARATOR? octalDigits)*
  public static boolean octalWholeNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber")) return false;
    if (!nextTokenIs(b, BASE_WHOLE_NUMBER_PREFIX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OCTAL_WHOLE_NUMBER, null);
    r = consumeTokens(b, 2, BASE_WHOLE_NUMBER_PREFIX, OCTAL_WHOLE_NUMBER_BASE);
    p = r; // pin = 2
    r = r && report_error_(b, octalDigits(b, l + 1));
    r = p && octalWholeNumber_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (NUMBER_SEPARATOR? octalDigits)*
  private static boolean octalWholeNumber_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!octalWholeNumber_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "octalWholeNumber_3", c)) break;
    }
    return true;
  }

  // NUMBER_SEPARATOR? octalDigits
  private static boolean octalWholeNumber_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = octalWholeNumber_3_0_0(b, l + 1);
    r = r && octalDigits(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER_SEPARATOR?
  private static boolean octalWholeNumber_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "octalWholeNumber_3_0_0")) return false;
    consumeToken(b, NUMBER_SEPARATOR);
    return true;
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
  // OR_SYMBOL_OPERATOR | OR_WORD_OPERATOR
  public static boolean orInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orInfixOperator")) return false;
    if (!nextTokenIs(b, "<||, |||, or>", OR_SYMBOL_OPERATOR, OR_WORD_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OR_INFIX_OPERATOR, "<||, |||, or>");
    r = consumeToken(b, OR_SYMBOL_OPERATOR);
    if (!r) r = consumeToken(b, OR_WORD_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OPENING_PARENTHESIS
  //                          (
  //                           unqualifiedNoParenthesesManyArgumentsCall | // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L485
  //                           keywords | // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L486
  //                           parenthesesPositionalArguments (infixComma keywords)?)? // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L487-L488
  //                          CLOSING_PARENTHESIS
  public static boolean parenthesesArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesesArguments")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_PARENTHESIS);
    r = r && parenthesesArguments_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, PARENTHESES_ARGUMENTS, r);
    return r;
  }

  // (
  //                           unqualifiedNoParenthesesManyArgumentsCall | // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L485
  //                           keywords | // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L486
  //                           parenthesesPositionalArguments (infixComma keywords)?)?
  private static boolean parenthesesArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesesArguments_1")) return false;
    parenthesesArguments_1_0(b, l + 1);
    return true;
  }

  // unqualifiedNoParenthesesManyArgumentsCall | // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L485
  //                           keywords | // @see https://github.com/elixir-lang/elixir/blob/39b6789a8625071e149f0a7347ca7a2111f7c8f2/lib/elixir/src/elixir_parser.yrl#L486
  //                           parenthesesPositionalArguments (infixComma keywords)?
  private static boolean parenthesesArguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesesArguments_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unqualifiedNoParenthesesManyArgumentsCall(b, l + 1);
    if (!r) r = keywords(b, l + 1);
    if (!r) r = parenthesesArguments_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parenthesesPositionalArguments (infixComma keywords)?
  private static boolean parenthesesArguments_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesesArguments_1_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parenthesesPositionalArguments(b, l + 1);
    r = r && parenthesesArguments_1_0_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (infixComma keywords)?
  private static boolean parenthesesArguments_1_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesesArguments_1_0_2_1")) return false;
    parenthesesArguments_1_0_2_1_0(b, l + 1);
    return true;
  }

  // infixComma keywords
  private static boolean parenthesesArguments_1_0_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesesArguments_1_0_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = infixComma(b, l + 1);
    r = r && keywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // containerArgumentsBase
  static boolean parenthesesPositionalArguments(PsiBuilder b, int l) {
    return containerArgumentsBase(b, l + 1);
  }

  /* ********************************************************** */
  // OPENING_PARENTHESIS
  //                       (semicolonMaybe stab semicolonMaybe | SEMICOLON)
  //                       CLOSING_PARENTHESIS
  public static boolean parentheticalStab(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentheticalStab")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_PARENTHESIS);
    r = r && parentheticalStab_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, PARENTHETICAL_STAB, r);
    return r;
  }

  // semicolonMaybe stab semicolonMaybe | SEMICOLON
  private static boolean parentheticalStab_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentheticalStab_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parentheticalStab_1_0(b, l + 1);
    if (!r) r = consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // semicolonMaybe stab semicolonMaybe
  private static boolean parentheticalStab_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parentheticalStab_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semicolonMaybe(b, l + 1);
    r = r && stab(b, l + 1);
    r = r && semicolonMaybe(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PIPE_OPERATOR
  public static boolean pipeInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipeInfixOperator")) return false;
    if (!nextTokenIs(b, "<|>", PIPE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PIPE_INFIX_OPERATOR, "<|>");
    r = consumeToken(b, PIPE_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // charListLine | stringLine
  static boolean quote(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quote")) return false;
    if (!nextTokenIs(b, "", CHAR_LIST_PROMOTER, STRING_PROMOTER)) return false;
    boolean r;
    r = charListLine(b, l + 1);
    if (!r) r = stringLine(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // hexadecimalEscapePrefix (openHexadecimalEscapeSequence | enclosedHexadecimalEscapeSequence)
  public static boolean quoteHexadecimalEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quoteHexadecimalEscapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, QUOTE_HEXADECIMAL_ESCAPE_SEQUENCE, null);
    r = hexadecimalEscapePrefix(b, l + 1);
    p = r; // pin = 1
    r = r && quoteHexadecimalEscapeSequence_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // openHexadecimalEscapeSequence | enclosedHexadecimalEscapeSequence
  private static boolean quoteHexadecimalEscapeSequence_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quoteHexadecimalEscapeSequence_1")) return false;
    boolean r;
    r = openHexadecimalEscapeSequence(b, l + 1);
    if (!r) r = enclosedHexadecimalEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // RELATIONAL_OPERATOR
  public static boolean relationalInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalInfixOperator")) return false;
    if (!nextTokenIs(b, "<<, >, <=, >=>", RELATIONAL_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RELATIONAL_INFIX_OPERATOR, "<<, >, <=, >=>");
    r = consumeToken(b, RELATIONAL_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER_TOKEN |
  //                        AFTER |
  //                        AND_SYMBOL_OPERATOR |
  //                        AND_WORD_OPERATOR |
  //                        ARROW_OPERATOR |
  //                        // NOT ASSOCIATION_OPERATOR
  //                        AT_OPERATOR |
  //                        // NOT BIT_STRING_OPERATOR because it is a special form
  //                        CAPTURE_OPERATOR |
  //                        CATCH |
  //                        COMPARISON_OPERATOR |
  //                        DO |
  //                        DIVISION_OPERATOR |
  //                        PLUS_OPERATOR |
  //                        ELSE |
  //                        END |
  //                        IN_MATCH_OPERATOR |
  //                        IN_OPERATOR |
  //                        // NOT MAP_OPERATOR because it is a special form
  //                        MATCH_OPERATOR |
  //                        MINUS_OPERATOR |
  //                        MULTIPLICATION_OPERATOR |
  //                        OR_SYMBOL_OPERATOR |
  //                        OR_WORD_OPERATOR |
  //                        PIPE_OPERATOR |
  //                        RANGE_OPERATOR |
  //                        RELATIONAL_OPERATOR |
  //                        RESCUE |
  //                        STAB_OPERATOR |
  //                        STRUCT_OPERATOR |
  //                        THREE_OPERATOR |
  //                        // NOT TUPLE_OPERATOR because it is a special form
  //                        TWO_OPERATOR |
  //                        UNARY_OPERATOR |
  //                        WHEN_OPERATOR |
  //                        atomKeyword |
  //                        charListLine |
  //                        stringLine
  public static boolean relativeIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relativeIdentifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RELATIVE_IDENTIFIER, "<relative identifier>");
    r = consumeToken(b, IDENTIFIER_TOKEN);
    if (!r) r = consumeToken(b, AFTER);
    if (!r) r = consumeToken(b, AND_SYMBOL_OPERATOR);
    if (!r) r = consumeToken(b, AND_WORD_OPERATOR);
    if (!r) r = consumeToken(b, ARROW_OPERATOR);
    if (!r) r = consumeToken(b, AT_OPERATOR);
    if (!r) r = consumeToken(b, CAPTURE_OPERATOR);
    if (!r) r = consumeToken(b, CATCH);
    if (!r) r = consumeToken(b, COMPARISON_OPERATOR);
    if (!r) r = consumeToken(b, DO);
    if (!r) r = consumeToken(b, DIVISION_OPERATOR);
    if (!r) r = consumeToken(b, PLUS_OPERATOR);
    if (!r) r = consumeToken(b, ELSE);
    if (!r) r = consumeToken(b, END);
    if (!r) r = consumeToken(b, IN_MATCH_OPERATOR);
    if (!r) r = consumeToken(b, IN_OPERATOR);
    if (!r) r = consumeToken(b, MATCH_OPERATOR);
    if (!r) r = consumeToken(b, MINUS_OPERATOR);
    if (!r) r = consumeToken(b, MULTIPLICATION_OPERATOR);
    if (!r) r = consumeToken(b, OR_SYMBOL_OPERATOR);
    if (!r) r = consumeToken(b, OR_WORD_OPERATOR);
    if (!r) r = consumeToken(b, PIPE_OPERATOR);
    if (!r) r = consumeToken(b, RANGE_OPERATOR);
    if (!r) r = consumeToken(b, RELATIONAL_OPERATOR);
    if (!r) r = consumeToken(b, RESCUE);
    if (!r) r = consumeToken(b, STAB_OPERATOR);
    if (!r) r = consumeToken(b, STRUCT_OPERATOR);
    if (!r) r = consumeToken(b, THREE_OPERATOR);
    if (!r) r = consumeToken(b, TWO_OPERATOR);
    if (!r) r = consumeToken(b, UNARY_OPERATOR);
    if (!r) r = consumeToken(b, WHEN_OPERATOR);
    if (!r) r = atomKeyword(b, l + 1);
    if (!r) r = charListLine(b, l + 1);
    if (!r) r = stringLine(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SEMICOLON?
  static boolean semicolonMaybe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semicolonMaybe")) return false;
    consumeToken(b, SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // sigilHexadecimalEscapeSequence |
  //                                        hexadecimalEscapePrefix |
  //                                        escapedCharacter
  static boolean sigilHeredocEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigilHeredocEscapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    r = sigilHexadecimalEscapeSequence(b, l + 1);
    if (!r) r = hexadecimalEscapePrefix(b, l + 1);
    if (!r) r = escapedCharacter(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // hexadecimalEscapePrefix (openHexadecimalEscapeSequence | enclosedHexadecimalEscapeSequence)
  public static boolean sigilHexadecimalEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigilHexadecimalEscapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hexadecimalEscapePrefix(b, l + 1);
    r = r && sigilHexadecimalEscapeSequence_1(b, l + 1);
    exit_section_(b, m, SIGIL_HEXADECIMAL_ESCAPE_SEQUENCE, r);
    return r;
  }

  // openHexadecimalEscapeSequence | enclosedHexadecimalEscapeSequence
  private static boolean sigilHexadecimalEscapeSequence_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigilHexadecimalEscapeSequence_1")) return false;
    boolean r;
    r = openHexadecimalEscapeSequence(b, l + 1);
    if (!r) r = enclosedHexadecimalEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // sigilHexadecimalEscapeSequence |
  //                                     hexadecimalEscapePrefix |
  //                                     escapedEOL |
  //                                     escapedCharacter
  static boolean sigilLineEscapeSequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigilLineEscapeSequence")) return false;
    if (!nextTokenIs(b, ESCAPE)) return false;
    boolean r;
    r = sigilHexadecimalEscapeSequence(b, l + 1);
    if (!r) r = hexadecimalEscapePrefix(b, l + 1);
    if (!r) r = escapedEOL(b, l + 1);
    if (!r) r = escapedCharacter(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SIGIL_MODIFIER*
  public static boolean sigilModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sigilModifiers")) return false;
    Marker m = enter_section_(b, l, _NONE_, SIGIL_MODIFIERS, "<sigil modifiers>");
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(b, "sigilModifiers", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // stabOperations | stabBody
  public static boolean stab(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stab")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STAB, "<stab>");
    r = stabOperations(b, l + 1);
    if (!r) r = stabBody(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // stabBodyExpression (stabBodyExpressionSeparator stabBodyExpression)*
  public static boolean stabBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STAB_BODY, "<stab body>");
    r = stabBodyExpression(b, l + 1);
    r = r && stabBody_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (stabBodyExpressionSeparator stabBodyExpression)*
  private static boolean stabBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!stabBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stabBody_1", c)) break;
    }
    return true;
  }

  // stabBodyExpressionSeparator stabBodyExpression
  private static boolean stabBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabBodyExpressionSeparator(b, l + 1);
    r = r && stabBodyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EEX_CLOSING eex EEX_OPENING |
  //                                expression !(infixComma | stabInfixOperator)
  static boolean stabBodyExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBodyExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabBodyExpression_0(b, l + 1);
    if (!r) r = stabBodyExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EEX_CLOSING eex EEX_OPENING
  private static boolean stabBodyExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBodyExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EEX_CLOSING);
    r = r && eex(b, l + 1);
    r = r && consumeToken(b, EEX_OPENING);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression !(infixComma | stabInfixOperator)
  private static boolean stabBodyExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBodyExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && stabBodyExpression_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !(infixComma | stabInfixOperator)
  private static boolean stabBodyExpression_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBodyExpression_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !stabBodyExpression_1_1_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // infixComma | stabInfixOperator
  private static boolean stabBodyExpression_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBodyExpression_1_1_0")) return false;
    boolean r;
    r = infixComma(b, l + 1);
    if (!r) r = stabInfixOperator(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // EEX_EMPTY_MARKER | endOfExpression
  static boolean stabBodyExpressionSeparator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBodyExpressionSeparator")) return false;
    boolean r;
    r = consumeToken(b, EEX_EMPTY_MARKER);
    if (!r) r = endOfExpression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // stabBodyExpressionSeparator?
  static boolean stabBodyExpressionSeparatorMaybe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabBodyExpressionSeparatorMaybe")) return false;
    stabBodyExpressionSeparator(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // STAB_OPERATOR
  public static boolean stabInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabInfixOperator")) return false;
    if (!nextTokenIs(b, "<->>", STAB_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STAB_INFIX_OPERATOR, "<->>");
    r = consumeToken(b, STAB_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // noParenthesesArguments
  public static boolean stabNoParenthesesSignature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabNoParenthesesSignature")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STAB_NO_PARENTHESES_SIGNATURE, "<stab no parentheses signature>");
    r = noParenthesesArguments(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // stabOperationPrefix stabBody?
  public static boolean stabOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STAB_OPERATION, "<stab operation>");
    r = stabOperationPrefix(b, l + 1);
    r = r && stabOperation_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // stabBody?
  private static boolean stabOperation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperation_1")) return false;
    stabBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // stabParenthesesSignature stabInfixOperator |
  //                                 stabNoParenthesesSignature stabInfixOperator
  static boolean stabOperationPrefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperationPrefix")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabOperationPrefix_0(b, l + 1);
    if (!r) r = stabOperationPrefix_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // stabParenthesesSignature stabInfixOperator
  private static boolean stabOperationPrefix_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperationPrefix_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabParenthesesSignature(b, l + 1);
    r = r && stabInfixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // stabNoParenthesesSignature stabInfixOperator
  private static boolean stabOperationPrefix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperationPrefix_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabNoParenthesesSignature(b, l + 1);
    r = r && stabInfixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // noArgumentStabOperation |
  //                            stabOperation (stabBodyExpressionSeparator stabOperation)*
  static boolean stabOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperations")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = noArgumentStabOperation(b, l + 1);
    if (!r) r = stabOperations_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // stabOperation (stabBodyExpressionSeparator stabOperation)*
  private static boolean stabOperations_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperations_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabOperation(b, l + 1);
    r = r && stabOperations_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (stabBodyExpressionSeparator stabOperation)*
  private static boolean stabOperations_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperations_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!stabOperations_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stabOperations_1_1", c)) break;
    }
    return true;
  }

  // stabBodyExpressionSeparator stabOperation
  private static boolean stabOperations_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperations_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabBodyExpressionSeparator(b, l + 1);
    r = r && stabOperation(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // parenthesesArguments (whenInfixOperator expression)?
  public static boolean stabParenthesesSignature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabParenthesesSignature")) return false;
    if (!nextTokenIs(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parenthesesArguments(b, l + 1);
    r = r && stabParenthesesSignature_1(b, l + 1);
    exit_section_(b, m, STAB_PARENTHESES_SIGNATURE, r);
    return r;
  }

  // (whenInfixOperator expression)?
  private static boolean stabParenthesesSignature_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabParenthesesSignature_1")) return false;
    stabParenthesesSignature_1_0(b, l + 1);
    return true;
  }

  // whenInfixOperator expression
  private static boolean stabParenthesesSignature_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabParenthesesSignature_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whenInfixOperator(b, l + 1);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STRING_HEREDOC_PROMOTER EOL
  //                   stringHeredocLine*
  //                   heredocPrefix STRING_HEREDOC_TERMINATOR
  public static boolean stringHeredoc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredoc")) return false;
    if (!nextTokenIs(b, STRING_HEREDOC_PROMOTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRING_HEREDOC, null);
    r = consumeTokens(b, 1, STRING_HEREDOC_PROMOTER, EOL);
    p = r; // pin = STRING_HEREDOC_PROMOTER
    r = r && report_error_(b, stringHeredoc_2(b, l + 1));
    r = p && report_error_(b, heredocPrefix(b, l + 1)) && r;
    r = p && consumeToken(b, STRING_HEREDOC_TERMINATOR) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // stringHeredocLine*
  private static boolean stringHeredoc_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredoc_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!stringHeredocLine(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stringHeredoc_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // heredocLinePrefix stringHeredocLineBody heredocLineEnd
  public static boolean stringHeredocLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredocLine")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_HEREDOC_LINE, "<string heredoc line>");
    r = heredocLinePrefix(b, l + 1);
    r = r && stringHeredocLineBody(b, l + 1);
    r = r && heredocLineEnd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | STRING_FRAGMENT | heredocEscapeSequence)*
  public static boolean stringHeredocLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredocLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, STRING_HEREDOC_LINE_BODY, "<string heredoc line body>");
    while (true) {
      int c = current_position_(b);
      if (!stringHeredocLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stringHeredocLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | STRING_FRAGMENT | heredocEscapeSequence
  private static boolean stringHeredocLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringHeredocLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = heredocEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // STRING_PROMOTER stringLineBody STRING_TERMINATOR
  public static boolean stringLine(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringLine")) return false;
    if (!nextTokenIs(b, STRING_PROMOTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRING_LINE, null);
    r = consumeToken(b, STRING_PROMOTER);
    p = r; // pin = STRING_PROMOTER
    r = r && report_error_(b, stringLineBody(b, l + 1));
    r = p && consumeToken(b, STRING_TERMINATOR) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (interpolation | STRING_FRAGMENT | lineEscapeSequence)*
  public static boolean stringLineBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringLineBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, STRING_LINE_BODY, "<string line body>");
    while (true) {
      int c = current_position_(b);
      if (!stringLineBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stringLineBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // interpolation | STRING_FRAGMENT | lineEscapeSequence
  private static boolean stringLineBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringLineBody_0")) return false;
    boolean r;
    r = interpolation(b, l + 1);
    if (!r) r = consumeToken(b, STRING_FRAGMENT);
    if (!r) r = lineEscapeSequence(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // mapPrefixOperator mapExpression eolStar mapArguments
  public static boolean structOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structOperation")) return false;
    if (!nextTokenIs(b, STRUCT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = mapPrefixOperator(b, l + 1);
    r = r && mapExpression(b, l + 1);
    r = r && eolStar(b, l + 1);
    r = r && mapArguments(b, l + 1);
    exit_section_(b, m, STRUCT_OPERATION, r);
    return r;
  }

  /* ********************************************************** */
  // THREE_OPERATOR
  public static boolean threeInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "threeInfixOperator")) return false;
    if (!nextTokenIs(b, "<^^^>", THREE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, THREE_INFIX_OPERATOR, "<^^^>");
    r = consumeToken(b, THREE_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OPENING_CURLY
  //           containerArgumentsMaybe
  //           CLOSING_CURLY
  public static boolean tuple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple")) return false;
    if (!nextTokenIs(b, OPENING_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_CURLY);
    r = r && containerArgumentsMaybe(b, l + 1);
    r = r && consumeToken(b, CLOSING_CURLY);
    exit_section_(b, m, TUPLE, r);
    return r;
  }

  /* ********************************************************** */
  // RANGE_OPERATOR | TWO_OPERATOR
  public static boolean twoInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "twoInfixOperator")) return false;
    if (!nextTokenIs(b, "<++, --, .., <>>", RANGE_OPERATOR, TWO_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TWO_INFIX_OPERATOR, "<++, --, .., <>>");
    r = consumeToken(b, RANGE_OPERATOR);
    if (!r) r = consumeToken(b, TWO_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TYPE_OPERATOR
  public static boolean typeInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeInfixOperator")) return false;
    if (!nextTokenIs(b, "<::>", TYPE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_INFIX_OPERATOR, "<::>");
    r = consumeToken(b, TYPE_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // <<ifVersion 'LT' 'V_1_5'>> unaryPrefixOperator numeric
  public static boolean unaryNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryNumericOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNARY_NUMERIC_OPERATION, "<unary numeric operation>");
    r = ifVersion(b, l + 1, LT, V_1_5);
    r = r && unaryPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (NEGATE_OPERATOR | NUMBER_OR_BADARITH_OPERATOR | UNARY_OPERATOR) eolStar |
  //                         NOT_OPERATOR !IN_OPERATOR
  public static boolean unaryPrefixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNARY_PREFIX_OPERATOR, "<+, -, !, ^, not, ~~~>");
    r = unaryPrefixOperator_0(b, l + 1);
    if (!r) r = unaryPrefixOperator_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (NEGATE_OPERATOR | NUMBER_OR_BADARITH_OPERATOR | UNARY_OPERATOR) eolStar
  private static boolean unaryPrefixOperator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unaryPrefixOperator_0_0(b, l + 1);
    r = r && eolStar(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NEGATE_OPERATOR | NUMBER_OR_BADARITH_OPERATOR | UNARY_OPERATOR
  private static boolean unaryPrefixOperator_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator_0_0")) return false;
    boolean r;
    r = consumeToken(b, NEGATE_OPERATOR);
    if (!r) r = consumeToken(b, NUMBER_OR_BADARITH_OPERATOR);
    if (!r) r = consumeToken(b, UNARY_OPERATOR);
    return r;
  }

  // NOT_OPERATOR !IN_OPERATOR
  private static boolean unaryPrefixOperator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT_OPERATOR);
    r = r && unaryPrefixOperator_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !IN_OPERATOR
  private static boolean unaryPrefixOperator_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryPrefixOperator_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, IN_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
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
    Marker m = enter_section_(b, l, _NONE_, UNKNOWN_BASE_WHOLE_NUMBER, null);
    r = consumeTokens(b, 2, BASE_WHOLE_NUMBER_PREFIX, UNKNOWN_WHOLE_NUMBER_BASE);
    p = r; // pin = 2
    r = r && unknownBaseWholeNumber_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // unknownBaseDigits+
  private static boolean unknownBaseWholeNumber_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknownBaseWholeNumber_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unknownBaseDigits(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!unknownBaseDigits(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unknownBaseWholeNumber_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier notKeywordPairColon
  //                                               noParenthesesManyArgumentsStrict
  public static boolean unqualifiedNoParenthesesManyArgumentsCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unqualifiedNoParenthesesManyArgumentsCall")) return false;
    if (!nextTokenIs(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && notKeywordPairColon(b, l + 1);
    r = r && noParenthesesManyArgumentsStrict(b, l + 1);
    exit_section_(b, m, UNQUALIFIED_NO_PARENTHESES_MANY_ARGUMENTS_CALL, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER_TOKEN notKeywordPairColon
  public static boolean variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable")) return false;
    if (!nextTokenIs(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER_TOKEN);
    r = r && notKeywordPairColon(b, l + 1);
    exit_section_(b, m, VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // WHEN_OPERATOR
  public static boolean whenInfixOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whenInfixOperator")) return false;
    if (!nextTokenIs(b, "<when>", WHEN_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHEN_INFIX_OPERATOR, "<when>");
    r = consumeToken(b, WHEN_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Expression root: matchedExpression
  // Operator priority table:
  // 0: PREFIX(matchedLessThanOnePointSixCaptureNonNumericOperation)
  // 1: BINARY(matchedInMatchOperation)
  // 2: POSTFIX(matchedWhenNoParenthesesKeywordsOperation)
  // 3: BINARY(matchedWhenOperation)
  // 4: BINARY(matchedTypeOperation)
  // 5: BINARY(matchedPipeOperation)
  // 6: PREFIX(matchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation)
  // 7: BINARY(matchedMatchOperation)
  // 8: BINARY(matchedOrOperation)
  // 9: BINARY(matchedAndOperation)
  // 10: BINARY(matchedComparisonOperation)
  // 11: BINARY(matchedRelationalOperation)
  // 12: BINARY(matchedArrowOperation)
  // 13: BINARY(matchedInOperation)
  // 14: BINARY(matchedNotInOperation)
  // 15: BINARY(matchedThreeOperation)
  // 16: BINARY(matchedTwoOperation)
  // 17: BINARY(matchedAdditionOperation)
  // 18: BINARY(matchedMultiplicationOperation)
  // 19: PREFIX(matchedUnaryNonNumericOperation)
  // 20: PREFIX(matchedUnaryOperation)
  // 21: POSTFIX(matchedDotCall)
  // 22: POSTFIX(matchedQualifiedNoParenthesesCall)
  // 23: ATOM(matchedAtUnqualifiedNoParenthesesCall)
  // 24: ATOM(matchedUnqualifiedNoParenthesesCall)
  // 25: ATOM(matchedAtNumericBracketOperation)
  // 26: POSTFIX(matchedBracketOperation)
  // 27: POSTFIX(matchedQualifiedAlias)
  // 28: POSTFIX(matchedQualifiedMultipleAliases)
  // 29: POSTFIX(matchedQualifiedBracketOperation)
  // 30: POSTFIX(matchedQualifiedParenthesesCall)
  // 31: POSTFIX(matchedQualifiedNoArgumentsCall)
  // 32: ATOM(matchedAtUnqualifiedBracketOperation)
  // 33: PREFIX(matchedAtNonNumericOperation)
  // 34: PREFIX(matchedAtOperation)
  // 35: ATOM(matchedUnqualifiedParenthesesCall)
  // 36: ATOM(matchedUnqualifiedBracketOperation)
  // 37: ATOM(matchedUnqualifiedNoArgumentsCall)
  // 38: ATOM(matchedAccessExpression)
  public static boolean matchedExpression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "matchedExpression")) return false;
    addVariant(b, "<matched expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<matched expression>");
    r = matchedLessThanOnePointSixCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation(b, l + 1);
    if (!r) r = matchedUnaryNonNumericOperation(b, l + 1);
    if (!r) r = matchedUnaryOperation(b, l + 1);
    if (!r) r = matchedAtUnqualifiedNoParenthesesCall(b, l + 1);
    if (!r) r = matchedUnqualifiedNoParenthesesCall(b, l + 1);
    if (!r) r = matchedAtNumericBracketOperation(b, l + 1);
    if (!r) r = matchedAtUnqualifiedBracketOperation(b, l + 1);
    if (!r) r = matchedAtNonNumericOperation(b, l + 1);
    if (!r) r = matchedAtOperation(b, l + 1);
    if (!r) r = matchedUnqualifiedParenthesesCall(b, l + 1);
    if (!r) r = matchedUnqualifiedBracketOperation(b, l + 1);
    if (!r) r = matchedUnqualifiedNoArgumentsCall(b, l + 1);
    if (!r) r = matchedAccessExpression(b, l + 1);
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
      if (g < 1 && inMatchInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 1);
        exit_section_(b, l, m, MATCHED_IN_MATCH_OPERATION, r, true, null);
      }
      else if (g < 2 && matchedWhenNoParenthesesKeywordsOperation_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_WHEN_OPERATION, r, true, null);
      }
      else if (g < 3 && whenInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 2);
        exit_section_(b, l, m, MATCHED_WHEN_OPERATION, r, true, null);
      }
      else if (g < 4 && typeInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 3);
        exit_section_(b, l, m, MATCHED_TYPE_OPERATION, r, true, null);
      }
      else if (g < 5 && pipeInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 4);
        exit_section_(b, l, m, MATCHED_PIPE_OPERATION, r, true, null);
      }
      else if (g < 7 && matchInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 6);
        exit_section_(b, l, m, MATCHED_MATCH_OPERATION, r, true, null);
      }
      else if (g < 8 && orInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 8);
        exit_section_(b, l, m, MATCHED_OR_OPERATION, r, true, null);
      }
      else if (g < 9 && andInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 9);
        exit_section_(b, l, m, MATCHED_AND_OPERATION, r, true, null);
      }
      else if (g < 10 && comparisonInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 10);
        exit_section_(b, l, m, MATCHED_COMPARISON_OPERATION, r, true, null);
      }
      else if (g < 11 && relationalInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 11);
        exit_section_(b, l, m, MATCHED_RELATIONAL_OPERATION, r, true, null);
      }
      else if (g < 12 && arrowInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 12);
        exit_section_(b, l, m, MATCHED_ARROW_OPERATION, r, true, null);
      }
      else if (g < 13 && inInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 13);
        exit_section_(b, l, m, MATCHED_IN_OPERATION, r, true, null);
      }
      else if (g < 14 && matchedNotInOperation_0(b, l + 1)) {
        r = matchedExpression(b, l, 14);
        exit_section_(b, l, m, MATCHED_NOT_IN_OPERATION, r, true, null);
      }
      else if (g < 15 && threeInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 15);
        exit_section_(b, l, m, MATCHED_THREE_OPERATION, r, true, null);
      }
      else if (g < 16 && twoInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 15);
        exit_section_(b, l, m, MATCHED_TWO_OPERATION, r, true, null);
      }
      else if (g < 17 && additionInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 17);
        exit_section_(b, l, m, MATCHED_ADDITION_OPERATION, r, true, null);
      }
      else if (g < 18 && multiplicationInfixOperator(b, l + 1)) {
        r = matchedExpression(b, l, 18);
        exit_section_(b, l, m, MATCHED_MULTIPLICATION_OPERATION, r, true, null);
      }
      else if (g < 21 && matchedDotCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_DOT_CALL, r, true, null);
      }
      else if (g < 22 && matchedQualifiedNoParenthesesCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_QUALIFIED_NO_PARENTHESES_CALL, r, true, null);
      }
      else if (g < 26 && bracketArguments(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_BRACKET_OPERATION, r, true, null);
      }
      else if (g < 27 && matchedQualifiedAlias_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_QUALIFIED_ALIAS, r, true, null);
      }
      else if (g < 28 && matchedQualifiedMultipleAliases_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_QUALIFIED_MULTIPLE_ALIASES, r, true, null);
      }
      else if (g < 29 && matchedQualifiedBracketOperation_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_QUALIFIED_BRACKET_OPERATION, r, true, null);
      }
      else if (g < 30 && matchedQualifiedParenthesesCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_QUALIFIED_PARENTHESES_CALL, r, true, null);
      }
      else if (g < 31 && matchedQualifiedNoArgumentsCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCHED_QUALIFIED_NO_ARGUMENTS_CALL, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  public static boolean matchedLessThanOnePointSixCaptureNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedLessThanOnePointSixCaptureNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedLessThanOnePointSixCaptureNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 0);
    exit_section_(b, l, m, MATCHED_LESS_THAN_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'LT' 'V_1_6'>> capturePrefixOperator nonNumeric
  private static boolean matchedLessThanOnePointSixCaptureNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedLessThanOnePointSixCaptureNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, LT, V_1_6);
    r = r && capturePrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whenInfixOperator noParenthesesKeywords
  private static boolean matchedWhenNoParenthesesKeywordsOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedWhenNoParenthesesKeywordsOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whenInfixOperator(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean matchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 6);
    exit_section_(b, l, m, MATCHED_GREATER_THAN_OR_EQUAL_TO_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'GE' 'V_1_6'>> capturePrefixOperator nonNumeric
  private static boolean matchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_6);
    r = r && capturePrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // <<ifVersion 'GE' 'V_1_5'>> notInfixOperator inInfixOperator
  private static boolean matchedNotInOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedNotInOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && notInfixOperator(b, l + 1);
    r = r && inInfixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean matchedUnaryNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedUnaryNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 19);
    exit_section_(b, l, m, MATCHED_UNARY_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'LT' 'V_1_5'>> unaryPrefixOperator nonNumeric
  private static boolean matchedUnaryNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, LT, V_1_5);
    r = r && unaryPrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean matchedUnaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedUnaryOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 20);
    exit_section_(b, l, m, MATCHED_UNARY_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'GE' 'V_1_5'>> unaryPrefixOperator
  private static boolean matchedUnaryOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnaryOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && unaryPrefixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator parenthesesArguments parenthesesArguments?
  private static boolean matchedDotCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && parenthesesArguments(b, l + 1);
    r = r && matchedDotCall_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parenthesesArguments?
  private static boolean matchedDotCall_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedDotCall_0_2")) return false;
    parenthesesArguments(b, l + 1);
    return true;
  }

  // dotInfixOperator relativeIdentifier noParenthesesOneArgument
  private static boolean matchedQualifiedNoParenthesesCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedQualifiedNoParenthesesCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && noParenthesesOneArgument(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // atIdentifier noParenthesesOneArgument
  public static boolean matchedAtUnqualifiedNoParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtUnqualifiedNoParenthesesCall")) return false;
    if (!nextTokenIsSmart(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atIdentifier(b, l + 1);
    r = r && noParenthesesOneArgument(b, l + 1);
    exit_section_(b, m, MATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL, r);
    return r;
  }

  // identifier noParenthesesOneArgument
  public static boolean matchedUnqualifiedNoParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnqualifiedNoParenthesesCall")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && noParenthesesOneArgument(b, l + 1);
    exit_section_(b, m, MATCHED_UNQUALIFIED_NO_PARENTHESES_CALL, r);
    return r;
  }

  // <<ifVersion 'GE' 'V_1_5'>> atPrefixOperator numeric bracketArguments
  public static boolean matchedAtNumericBracketOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtNumericBracketOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MATCHED_AT_NUMERIC_BRACKET_OPERATION, "<matched at numeric bracket operation>");
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && atPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // dotInfixOperator alias
  private static boolean matchedQualifiedAlias_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedQualifiedAlias_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator multipleAliases
  private static boolean matchedQualifiedMultipleAliases_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedQualifiedMultipleAliases_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && multipleAliases(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator relativeIdentifier CALL bracketArguments
  private static boolean matchedQualifiedBracketOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedQualifiedBracketOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && consumeToken(b, CALL);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator relativeIdentifier matchedParenthesesArguments
  private static boolean matchedQualifiedParenthesesCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedQualifiedParenthesesCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && matchedParenthesesArguments(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator relativeIdentifier !CALL
  private static boolean matchedQualifiedNoArgumentsCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedQualifiedNoArgumentsCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && matchedQualifiedNoArgumentsCall_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !CALL
  private static boolean matchedQualifiedNoArgumentsCall_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedQualifiedNoArgumentsCall_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeTokenSmart(b, CALL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // atPrefixOperator IDENTIFIER_TOKEN CALL bracketArguments
  public static boolean matchedAtUnqualifiedBracketOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtUnqualifiedBracketOperation")) return false;
    if (!nextTokenIsSmart(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && consumeTokensSmart(b, 0, IDENTIFIER_TOKEN, CALL);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, m, MATCHED_AT_UNQUALIFIED_BRACKET_OPERATION, r);
    return r;
  }

  public static boolean matchedAtNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedAtNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 33);
    exit_section_(b, l, m, MATCHED_AT_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'LT' 'V_1_5'>> atPrefixOperator nonNumeric
  private static boolean matchedAtNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, LT, V_1_5);
    r = r && atPrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean matchedAtOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = matchedAtOperation_0(b, l + 1);
    p = r;
    r = p && matchedExpression(b, l, 34);
    exit_section_(b, l, m, MATCHED_AT_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'GE' 'V_1_5'>> atPrefixOperator
  private static boolean matchedAtOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAtOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && atPrefixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // identifier matchedParenthesesArguments
  public static boolean matchedUnqualifiedParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnqualifiedParenthesesCall")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && matchedParenthesesArguments(b, l + 1);
    exit_section_(b, m, MATCHED_UNQUALIFIED_PARENTHESES_CALL, r);
    return r;
  }

  // identifier CALL bracketArguments
  public static boolean matchedUnqualifiedBracketOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnqualifiedBracketOperation")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && consumeToken(b, CALL);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, m, MATCHED_UNQUALIFIED_BRACKET_OPERATION, r);
    return r;
  }

  // identifier notKeywordPairColon
  public static boolean matchedUnqualifiedNoArgumentsCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedUnqualifiedNoArgumentsCall")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && notKeywordPairColon(b, l + 1);
    exit_section_(b, m, MATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL, r);
    return r;
  }

  // accessExpression
  public static boolean matchedAccessExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchedAccessExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, ACCESS_EXPRESSION, "<matched access expression>");
    r = accessExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Expression root: unmatchedExpression
  // Operator priority table:
  // 0: PREFIX(unmatchedLessThanOnePointSixCaptureNonNumericOperation)
  // 1: BINARY(unmatchedInMatchOperation)
  // 2: POSTFIX(unmatchedWhenNoParenthesesKeywordsOperation)
  // 3: BINARY(unmatchedWhenOperation)
  // 4: BINARY(unmatchedTypeOperation)
  // 5: BINARY(unmatchedPipeOperation)
  // 6: PREFIX(unmatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation)
  // 7: BINARY(unmatchedMatchOperation)
  // 8: BINARY(unmatchedOrOperation)
  // 9: BINARY(unmatchedAndOperation)
  // 10: BINARY(unmatchedComparisonOperation)
  // 11: BINARY(unmatchedRelationalOperation)
  // 12: BINARY(unmatchedArrowOperation)
  // 13: BINARY(unmatchedInOperation)
  // 14: BINARY(unmatchedNotInOperation)
  // 15: BINARY(unmatchedThreeOperation)
  // 16: BINARY(unmatchedTwoOperation)
  // 17: BINARY(unmatchedAdditionOperation)
  // 18: BINARY(unmatchedMultiplicationOperation)
  // 19: PREFIX(unmatchedUnaryNonNumericOperation)
  // 20: PREFIX(unmatchedUnaryOperation)
  // 21: POSTFIX(unmatchedDotCall)
  // 22: POSTFIX(unmatchedQualifiedNoParenthesesCall)
  // 23: ATOM(unmatchedAtUnqualifiedNoParenthesesCall)
  // 24: ATOM(unmatchedUnqualifiedNoParenthesesCall)
  // 25: ATOM(unmatchedAtNumericBracketOperation)
  // 26: POSTFIX(unmatchedBracketOperation)
  // 27: POSTFIX(unmatchedQualifiedAlias)
  // 28: POSTFIX(unmatchedQualifiedMultipleAliases)
  // 29: POSTFIX(unmatchedQualifiedBracketOperation)
  // 30: POSTFIX(unmatchedQualifiedParenthesesCall)
  // 31: POSTFIX(unmatchedQualifiedNoArgumentsCall)
  // 32: ATOM(unmatchedAtUnqualifiedBracketOperation)
  // 33: PREFIX(unmatchedAtNonNumericOperation)
  // 34: PREFIX(unmatchedAtOperation)
  // 35: ATOM(unmatchedUnqualifiedParenthesesCall)
  // 36: ATOM(unmatchedUnqualifiedBracketOperation)
  // 37: ATOM(unmatchedUnqualifiedNoArgumentsCall)
  // 38: ATOM(unmatchedAccessExpression)
  public static boolean unmatchedExpression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "unmatchedExpression")) return false;
    addVariant(b, "<unmatched expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<unmatched expression>");
    r = unmatchedLessThanOnePointSixCaptureNonNumericOperation(b, l + 1);
    if (!r) r = unmatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation(b, l + 1);
    if (!r) r = unmatchedUnaryNonNumericOperation(b, l + 1);
    if (!r) r = unmatchedUnaryOperation(b, l + 1);
    if (!r) r = unmatchedAtUnqualifiedNoParenthesesCall(b, l + 1);
    if (!r) r = unmatchedUnqualifiedNoParenthesesCall(b, l + 1);
    if (!r) r = unmatchedAtNumericBracketOperation(b, l + 1);
    if (!r) r = unmatchedAtUnqualifiedBracketOperation(b, l + 1);
    if (!r) r = unmatchedAtNonNumericOperation(b, l + 1);
    if (!r) r = unmatchedAtOperation(b, l + 1);
    if (!r) r = unmatchedUnqualifiedParenthesesCall(b, l + 1);
    if (!r) r = unmatchedUnqualifiedBracketOperation(b, l + 1);
    if (!r) r = unmatchedUnqualifiedNoArgumentsCall(b, l + 1);
    if (!r) r = unmatchedAccessExpression(b, l + 1);
    p = r;
    r = r && unmatchedExpression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean unmatchedExpression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "unmatchedExpression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 1 && inMatchInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 1);
        exit_section_(b, l, m, UNMATCHED_IN_MATCH_OPERATION, r, true, null);
      }
      else if (g < 2 && unmatchedWhenNoParenthesesKeywordsOperation_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_WHEN_OPERATION, r, true, null);
      }
      else if (g < 3 && whenInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 2);
        exit_section_(b, l, m, UNMATCHED_WHEN_OPERATION, r, true, null);
      }
      else if (g < 4 && typeInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 3);
        exit_section_(b, l, m, UNMATCHED_TYPE_OPERATION, r, true, null);
      }
      else if (g < 5 && pipeInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 4);
        exit_section_(b, l, m, UNMATCHED_PIPE_OPERATION, r, true, null);
      }
      else if (g < 7 && matchInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 6);
        exit_section_(b, l, m, UNMATCHED_MATCH_OPERATION, r, true, null);
      }
      else if (g < 8 && orInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 8);
        exit_section_(b, l, m, UNMATCHED_OR_OPERATION, r, true, null);
      }
      else if (g < 9 && andInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 9);
        exit_section_(b, l, m, UNMATCHED_AND_OPERATION, r, true, null);
      }
      else if (g < 10 && comparisonInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 10);
        exit_section_(b, l, m, UNMATCHED_COMPARISON_OPERATION, r, true, null);
      }
      else if (g < 11 && relationalInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 11);
        exit_section_(b, l, m, UNMATCHED_RELATIONAL_OPERATION, r, true, null);
      }
      else if (g < 12 && arrowInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 12);
        exit_section_(b, l, m, UNMATCHED_ARROW_OPERATION, r, true, null);
      }
      else if (g < 13 && inInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 13);
        exit_section_(b, l, m, UNMATCHED_IN_OPERATION, r, true, null);
      }
      else if (g < 14 && unmatchedNotInOperation_0(b, l + 1)) {
        r = unmatchedExpression(b, l, 14);
        exit_section_(b, l, m, UNMATCHED_NOT_IN_OPERATION, r, true, null);
      }
      else if (g < 15 && threeInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 15);
        exit_section_(b, l, m, UNMATCHED_THREE_OPERATION, r, true, null);
      }
      else if (g < 16 && twoInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 15);
        exit_section_(b, l, m, UNMATCHED_TWO_OPERATION, r, true, null);
      }
      else if (g < 17 && additionInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 17);
        exit_section_(b, l, m, UNMATCHED_ADDITION_OPERATION, r, true, null);
      }
      else if (g < 18 && multiplicationInfixOperator(b, l + 1)) {
        r = unmatchedExpression(b, l, 18);
        exit_section_(b, l, m, UNMATCHED_MULTIPLICATION_OPERATION, r, true, null);
      }
      else if (g < 21 && unmatchedDotCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_DOT_CALL, r, true, null);
      }
      else if (g < 22 && unmatchedQualifiedNoParenthesesCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_QUALIFIED_NO_PARENTHESES_CALL, r, true, null);
      }
      else if (g < 26 && bracketArguments(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_BRACKET_OPERATION, r, true, null);
      }
      else if (g < 27 && unmatchedQualifiedAlias_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_QUALIFIED_ALIAS, r, true, null);
      }
      else if (g < 28 && unmatchedQualifiedMultipleAliases_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_QUALIFIED_MULTIPLE_ALIASES, r, true, null);
      }
      else if (g < 29 && unmatchedQualifiedBracketOperation_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_QUALIFIED_BRACKET_OPERATION, r, true, null);
      }
      else if (g < 30 && unmatchedQualifiedParenthesesCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_QUALIFIED_PARENTHESES_CALL, r, true, null);
      }
      else if (g < 31 && unmatchedQualifiedNoArgumentsCall_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UNMATCHED_QUALIFIED_NO_ARGUMENTS_CALL, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  public static boolean unmatchedLessThanOnePointSixCaptureNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedLessThanOnePointSixCaptureNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unmatchedLessThanOnePointSixCaptureNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && unmatchedExpression(b, l, 0);
    exit_section_(b, l, m, UNMATCHED_LESS_THAN_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'LT' 'V_1_6'>> capturePrefixOperator nonNumeric
  private static boolean unmatchedLessThanOnePointSixCaptureNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedLessThanOnePointSixCaptureNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, LT, V_1_6);
    r = r && capturePrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whenInfixOperator noParenthesesKeywords
  private static boolean unmatchedWhenNoParenthesesKeywordsOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedWhenNoParenthesesKeywordsOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whenInfixOperator(b, l + 1);
    r = r && noParenthesesKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean unmatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unmatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && unmatchedExpression(b, l, 6);
    exit_section_(b, l, m, UNMATCHED_GREATER_THAN_OR_EQUAL_TO_ONE_POINT_SIX_CAPTURE_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'GE' 'V_1_6'>> capturePrefixOperator nonNumeric
  private static boolean unmatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_6);
    r = r && capturePrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // <<ifVersion 'GE' 'V_1_5'>> notInfixOperator inInfixOperator
  private static boolean unmatchedNotInOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedNotInOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && notInfixOperator(b, l + 1);
    r = r && inInfixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean unmatchedUnaryNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnaryNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unmatchedUnaryNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && unmatchedExpression(b, l, 19);
    exit_section_(b, l, m, UNMATCHED_UNARY_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'LT' 'V_1_5'>> unaryPrefixOperator nonNumeric
  private static boolean unmatchedUnaryNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnaryNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, LT, V_1_5);
    r = r && unaryPrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean unmatchedUnaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnaryOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unmatchedUnaryOperation_0(b, l + 1);
    p = r;
    r = p && unmatchedExpression(b, l, 20);
    exit_section_(b, l, m, UNMATCHED_UNARY_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'GE' 'V_1_5'>> unaryPrefixOperator
  private static boolean unmatchedUnaryOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnaryOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && unaryPrefixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator parenthesesArguments parenthesesArguments? doBlockMaybe
  private static boolean unmatchedDotCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedDotCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && parenthesesArguments(b, l + 1);
    r = r && unmatchedDotCall_0_2(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parenthesesArguments?
  private static boolean unmatchedDotCall_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedDotCall_0_2")) return false;
    parenthesesArguments(b, l + 1);
    return true;
  }

  // dotInfixOperator relativeIdentifier noParenthesesOneArgument doBlockMaybe
  private static boolean unmatchedQualifiedNoParenthesesCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedQualifiedNoParenthesesCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && noParenthesesOneArgument(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // atIdentifier noParenthesesOneArgument doBlockMaybe
  public static boolean unmatchedAtUnqualifiedNoParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAtUnqualifiedNoParenthesesCall")) return false;
    if (!nextTokenIsSmart(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atIdentifier(b, l + 1);
    r = r && noParenthesesOneArgument(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, UNMATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL, r);
    return r;
  }

  // identifier noParenthesesOneArgument doBlockMaybe
  public static boolean unmatchedUnqualifiedNoParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnqualifiedNoParenthesesCall")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && noParenthesesOneArgument(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, UNMATCHED_UNQUALIFIED_NO_PARENTHESES_CALL, r);
    return r;
  }

  // <<ifVersion 'GE' 'V_1_5'>> atPrefixOperator numeric bracketArguments
  public static boolean unmatchedAtNumericBracketOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAtNumericBracketOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNMATCHED_AT_NUMERIC_BRACKET_OPERATION, "<unmatched at numeric bracket operation>");
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && atPrefixOperator(b, l + 1);
    r = r && numeric(b, l + 1);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // dotInfixOperator alias
  private static boolean unmatchedQualifiedAlias_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedQualifiedAlias_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator multipleAliases
  private static boolean unmatchedQualifiedMultipleAliases_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedQualifiedMultipleAliases_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && multipleAliases(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator relativeIdentifier CALL bracketArguments
  private static boolean unmatchedQualifiedBracketOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedQualifiedBracketOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && consumeToken(b, CALL);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator relativeIdentifier matchedParenthesesArguments doBlockMaybe
  private static boolean unmatchedQualifiedParenthesesCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedQualifiedParenthesesCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && matchedParenthesesArguments(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dotInfixOperator relativeIdentifier !CALL doBlockMaybe
  private static boolean unmatchedQualifiedNoArgumentsCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedQualifiedNoArgumentsCall_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotInfixOperator(b, l + 1);
    r = r && relativeIdentifier(b, l + 1);
    r = r && unmatchedQualifiedNoArgumentsCall_0_2(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !CALL
  private static boolean unmatchedQualifiedNoArgumentsCall_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedQualifiedNoArgumentsCall_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeTokenSmart(b, CALL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // atPrefixOperator IDENTIFIER_TOKEN CALL bracketArguments
  public static boolean unmatchedAtUnqualifiedBracketOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAtUnqualifiedBracketOperation")) return false;
    if (!nextTokenIsSmart(b, AT_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atPrefixOperator(b, l + 1);
    r = r && consumeTokensSmart(b, 0, IDENTIFIER_TOKEN, CALL);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, m, UNMATCHED_AT_UNQUALIFIED_BRACKET_OPERATION, r);
    return r;
  }

  public static boolean unmatchedAtNonNumericOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAtNonNumericOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unmatchedAtNonNumericOperation_0(b, l + 1);
    p = r;
    r = p && unmatchedExpression(b, l, 33);
    exit_section_(b, l, m, UNMATCHED_AT_NON_NUMERIC_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'LT' 'V_1_5'>> atPrefixOperator nonNumeric
  private static boolean unmatchedAtNonNumericOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAtNonNumericOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, LT, V_1_5);
    r = r && atPrefixOperator(b, l + 1);
    r = r && nonNumeric(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean unmatchedAtOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAtOperation")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unmatchedAtOperation_0(b, l + 1);
    p = r;
    r = p && unmatchedExpression(b, l, 34);
    exit_section_(b, l, m, UNMATCHED_AT_OPERATION, r, p, null);
    return r || p;
  }

  // <<ifVersion 'GE' 'V_1_5'>> atPrefixOperator
  private static boolean unmatchedAtOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAtOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifVersion(b, l + 1, GE, V_1_5);
    r = r && atPrefixOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // identifier matchedParenthesesArguments doBlockMaybe
  public static boolean unmatchedUnqualifiedParenthesesCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnqualifiedParenthesesCall")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && matchedParenthesesArguments(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, UNMATCHED_UNQUALIFIED_PARENTHESES_CALL, r);
    return r;
  }

  // identifier CALL bracketArguments
  public static boolean unmatchedUnqualifiedBracketOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnqualifiedBracketOperation")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && consumeToken(b, CALL);
    r = r && bracketArguments(b, l + 1);
    exit_section_(b, m, UNMATCHED_UNQUALIFIED_BRACKET_OPERATION, r);
    return r;
  }

  // identifier notKeywordPairColon doBlockMaybe
  public static boolean unmatchedUnqualifiedNoArgumentsCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedUnqualifiedNoArgumentsCall")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && notKeywordPairColon(b, l + 1);
    r = r && doBlockMaybe(b, l + 1);
    exit_section_(b, m, UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL, r);
    return r;
  }

  // accessExpression
  public static boolean unmatchedAccessExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unmatchedAccessExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, ACCESS_EXPRESSION, "<unmatched access expression>");
    r = accessExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
