// This is a generated file. Not intended for manual editing.
package org.elixir_lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.elixir_lang.psi.ElixirTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ElixirParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("org.elixir_lang.parser.ElixirParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == ACCESS_EXPRESSION) {
      result_ = accessExpression(builder_, 0);
    }
    else if (root_ == ADD_OPERATOR_EOL) {
      result_ = addOperatorEOL(builder_, 0);
    }
    else if (root_ == AND_OPERATOR_EOL) {
      result_ = andOperatorEOL(builder_, 0);
    }
    else if (root_ == ARROW_OPERATOR_EOL) {
      result_ = arrowOperatorEOL(builder_, 0);
    }
    else if (root_ == ASSOC) {
      result_ = assoc(builder_, 0);
    }
    else if (root_ == ASSOC_BASE) {
      result_ = assocBase(builder_, 0);
    }
    else if (root_ == ASSOC_EXPRESSION) {
      result_ = assocExpression(builder_, 0);
    }
    else if (root_ == ASSOC_OPERATOR_EOL) {
      result_ = assocOperatorEOL(builder_, 0);
    }
    else if (root_ == ASSOC_UPDATE) {
      result_ = assocUpdate(builder_, 0);
    }
    else if (root_ == ASSOC_UPDATE_KEYWORD) {
      result_ = assocUpdateKeyword(builder_, 0);
    }
    else if (root_ == AT_OPERATOR_EOL) {
      result_ = atOperatorEOL(builder_, 0);
    }
    else if (root_ == BIT_STRING) {
      result_ = bitString(builder_, 0);
    }
    else if (root_ == BLOCK_EOL) {
      result_ = blockEOL(builder_, 0);
    }
    else if (root_ == BLOCK_EXPRESSION) {
      result_ = blockExpression(builder_, 0);
    }
    else if (root_ == BLOCK_ITEM) {
      result_ = blockItem(builder_, 0);
    }
    else if (root_ == BLOCK_LIST) {
      result_ = blockList(builder_, 0);
    }
    else if (root_ == BRACKET_ARGUMENT) {
      result_ = bracketArgument(builder_, 0);
    }
    else if (root_ == BRACKET_AT_EXPRESSION) {
      result_ = bracketAtExpression(builder_, 0);
    }
    else if (root_ == BRACKET_EXPRESSION) {
      result_ = bracketExpression(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_NO_PARENTHESES_ALL) {
      result_ = callArgumentsNoParenthesesAll(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_NO_PARENTHESES_COMMA_EXPRESSION) {
      result_ = callArgumentsNoParenthesesCommaExpression(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD) {
      result_ = callArgumentsNoParenthesesKeyword(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD_EXPRESSION) {
      result_ = callArgumentsNoParenthesesKeywordExpression(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_NO_PARENTHESES_MANY) {
      result_ = callArgumentsNoParenthesesMany(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_NO_PARENTHESES_MANY_STRICT) {
      result_ = callArgumentsNoParenthesesManyStrict(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_NO_PARENTHESES_ONE) {
      result_ = callArgumentsNoParenthesesOne(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_PARENTHESES) {
      result_ = callArgumentsParentheses(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_PARENTHESES_BASE) {
      result_ = callArgumentsParenthesesBase(builder_, 0);
    }
    else if (root_ == CALL_ARGUMENTS_PARENTHESES_EXPRESSION) {
      result_ = callArgumentsParenthesesExpression(builder_, 0);
    }
    else if (root_ == CAPTURE_OPERATOR_EOL) {
      result_ = captureOperatorEOL(builder_, 0);
    }
    else if (root_ == CLOSE_BIT) {
      result_ = closeBit(builder_, 0);
    }
    else if (root_ == CLOSE_BRACKET) {
      result_ = closeBracket(builder_, 0);
    }
    else if (root_ == CLOSE_CURLY) {
      result_ = closeCurly(builder_, 0);
    }
    else if (root_ == CLOSE_PARENTHESIS) {
      result_ = closeParenthesis(builder_, 0);
    }
    else if (root_ == COMP_OPERATOR_EOL) {
      result_ = compOperatorEOL(builder_, 0);
    }
    else if (root_ == CONTAINER_ARGUMENTS) {
      result_ = containerArguments(builder_, 0);
    }
    else if (root_ == CONTAINER_ARGUMENTS_BASE) {
      result_ = containerArgumentsBase(builder_, 0);
    }
    else if (root_ == CONTAINER_EXPRESSION) {
      result_ = containerExpression(builder_, 0);
    }
    else if (root_ == DO_BLOCK) {
      result_ = doBlock(builder_, 0);
    }
    else if (root_ == DO_EOL) {
      result_ = doEOL(builder_, 0);
    }
    else if (root_ == DOT_ALIAS) {
      result_ = dotAlias(builder_, 0);
    }
    else if (root_ == DOT_BRACKET_IDENTIFIER) {
      result_ = dotBracketIdentifier(builder_, 0);
    }
    else if (root_ == DOT_DO_IDENTIFIER) {
      result_ = dotDoIdentifier(builder_, 0);
    }
    else if (root_ == DOT_IDENTIFIER) {
      result_ = dotIdentifier(builder_, 0);
    }
    else if (root_ == DOT_OPERATOR) {
      result_ = dotOperator(builder_, 0);
    }
    else if (root_ == DOT_OPERATOR_IDENTIFIER) {
      result_ = dotOperatorIdentifier(builder_, 0);
    }
    else if (root_ == DOT_PARENTHESES_IDENTIFIER) {
      result_ = dotParenthesesIdentifier(builder_, 0);
    }
    else if (root_ == EMPTY_PARENTHESES) {
      result_ = emptyParentheses(builder_, 0);
    }
    else if (root_ == END_EOL) {
      result_ = endEOL(builder_, 0);
    }
    else if (root_ == EXPRESSION) {
      result_ = expression(builder_, 0);
    }
    else if (root_ == EXPRESSION_LIST) {
      result_ = expressionList(builder_, 0);
    }
    else if (root_ == FN_EOL) {
      result_ = fnEOL(builder_, 0);
    }
    else if (root_ == HAT_OPERATOR_EOL) {
      result_ = hatOperatorEOL(builder_, 0);
    }
    else if (root_ == IN_MATCH_OPERATOR_EOL) {
      result_ = inMatchOperatorEOL(builder_, 0);
    }
    else if (root_ == IN_OPERATOR_EOL) {
      result_ = inOperatorEOL(builder_, 0);
    }
    else if (root_ == KEYWORD) {
      result_ = keyword(builder_, 0);
    }
    else if (root_ == KEYWORD_BASE) {
      result_ = keywordBase(builder_, 0);
    }
    else if (root_ == KEYWORD_EOL) {
      result_ = keywordEOL(builder_, 0);
    }
    else if (root_ == LIST) {
      result_ = list(builder_, 0);
    }
    else if (root_ == LIST_ARGUMENTS) {
      result_ = listArguments(builder_, 0);
    }
    else if (root_ == MAP) {
      result_ = map(builder_, 0);
    }
    else if (root_ == MAP_ARGUMENTS) {
      result_ = mapArguments(builder_, 0);
    }
    else if (root_ == MAP_CLOSE) {
      result_ = mapClose(builder_, 0);
    }
    else if (root_ == MAP_EXPRESSION) {
      result_ = mapExpression(builder_, 0);
    }
    else if (root_ == MAP_OPERATOR) {
      result_ = mapOperator(builder_, 0);
    }
    else if (root_ == MATCH_OPERATOR_EOL) {
      result_ = matchOperatorEOL(builder_, 0);
    }
    else if (root_ == MATCHED_EXPRESSION) {
      result_ = matchedExpression(builder_, 0);
    }
    else if (root_ == MATCHED_OPERATOR_EXPRESSION) {
      result_ = matchedOperatorExpression(builder_, 0);
    }
    else if (root_ == MAX_EXPRESSION) {
      result_ = maxExpression(builder_, 0);
    }
    else if (root_ == MULTIPLY_OPERATOR_EOL) {
      result_ = multiplyOperatorEOL(builder_, 0);
    }
    else if (root_ == NO_PARENTHESES_EXPRESSION) {
      result_ = noParenthesesExpression(builder_, 0);
    }
    else if (root_ == NO_PARENTHESES_ONE_EXPRESSION) {
      result_ = noParenthesesOneExpression(builder_, 0);
    }
    else if (root_ == NO_PARENTHESES_OPERATOR_EXPRESSION) {
      result_ = noParenthesesOperatorExpression(builder_, 0);
    }
    else if (root_ == OPEN_BIT) {
      result_ = openBit(builder_, 0);
    }
    else if (root_ == OPEN_BRACKET) {
      result_ = openBracket(builder_, 0);
    }
    else if (root_ == OPEN_CURLY) {
      result_ = openCurly(builder_, 0);
    }
    else if (root_ == OPEN_PARENTHESIS) {
      result_ = openParenthesis(builder_, 0);
    }
    else if (root_ == OPERATOR_EOL) {
      result_ = operatorEOL(builder_, 0);
    }
    else if (root_ == OPERATOR_EXPRESSION) {
      result_ = operatorExpression(builder_, 0);
    }
    else if (root_ == OR_OPERATOR_EOL) {
      result_ = orOperatorEOL(builder_, 0);
    }
    else if (root_ == PARENTHESES_CALL) {
      result_ = parenthesesCall(builder_, 0);
    }
    else if (root_ == PIPE_OPERATOR_EOL) {
      result_ = pipeOperatorEOL(builder_, 0);
    }
    else if (root_ == REL_OPERATOR_EOL) {
      result_ = relOperatorEOL(builder_, 0);
    }
    else if (root_ == STAB) {
      result_ = stab(builder_, 0);
    }
    else if (root_ == STAB_EOL) {
      result_ = stabEOL(builder_, 0);
    }
    else if (root_ == STAB_MAYBE_EXPRESSION) {
      result_ = stabMaybeExpression(builder_, 0);
    }
    else if (root_ == STAB_OPERATOR_EOL) {
      result_ = stabOperatorEOL(builder_, 0);
    }
    else if (root_ == STAB_PARENTHESES_MANY) {
      result_ = stabParenthesesMany(builder_, 0);
    }
    else if (root_ == STRUCT_OPERATOR) {
      result_ = structOperator(builder_, 0);
    }
    else if (root_ == TUPLE) {
      result_ = tuple(builder_, 0);
    }
    else if (root_ == TWO_OPERATOR_EOL) {
      result_ = twoOperatorEOL(builder_, 0);
    }
    else if (root_ == TYPE_OPERATOR_EOL) {
      result_ = typeOperatorEOL(builder_, 0);
    }
    else if (root_ == UNARY_OPERATOR_EOL) {
      result_ = unaryOperatorEOL(builder_, 0);
    }
    else if (root_ == UNMATCHED_EXPRESSION) {
      result_ = unmatchedExpression(builder_, 0);
    }
    else if (root_ == WHEN_OPERATOR_EOL) {
      result_ = whenOperatorEOL(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return elixirFile(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // bracketAtExpression |
  //   bracketExpression |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L202-L204
  //   ((atOperatorEOL | unaryOperatorEOL | captureOperatorEOL) number) |
  //   (fnEOL stab endEOL) |
  //   (openParenthesis stab closeParenthesis) |
  //   number |
  //   signed_number |
  //   list |
  //   map |
  //   tuple |
  //   'true' |
  //   'false' |
  //   'nil' |
  //   binaryString |
  //   listString |
  //   bitString |
  //   sigil |
  //   maxExpression
  public static boolean accessExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "accessExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<access expression>");
    result_ = bracketAtExpression(builder_, level_ + 1);
    if (!result_) result_ = bracketExpression(builder_, level_ + 1);
    if (!result_) result_ = accessExpression_2(builder_, level_ + 1);
    if (!result_) result_ = accessExpression_3(builder_, level_ + 1);
    if (!result_) result_ = accessExpression_4(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, NUMBER);
    if (!result_) result_ = consumeToken(builder_, SIGNED_NUMBER);
    if (!result_) result_ = list(builder_, level_ + 1);
    if (!result_) result_ = map(builder_, level_ + 1);
    if (!result_) result_ = tuple(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "true");
    if (!result_) result_ = consumeToken(builder_, "false");
    if (!result_) result_ = consumeToken(builder_, "nil");
    if (!result_) result_ = consumeToken(builder_, BINARYSTRING);
    if (!result_) result_ = consumeToken(builder_, LISTSTRING);
    if (!result_) result_ = bitString(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, SIGIL);
    if (!result_) result_ = maxExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ACCESS_EXPRESSION, result_, false, null);
    return result_;
  }

  // (atOperatorEOL | unaryOperatorEOL | captureOperatorEOL) number
  private static boolean accessExpression_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "accessExpression_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = accessExpression_2_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, NUMBER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // atOperatorEOL | unaryOperatorEOL | captureOperatorEOL
  private static boolean accessExpression_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "accessExpression_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = atOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = unaryOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = captureOperatorEOL(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // fnEOL stab endEOL
  private static boolean accessExpression_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "accessExpression_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = fnEOL(builder_, level_ + 1);
    result_ = result_ && stab(builder_, level_ + 1);
    result_ = result_ && endEOL(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // openParenthesis stab closeParenthesis
  private static boolean accessExpression_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "accessExpression_4")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = openParenthesis(builder_, level_ + 1);
    result_ = result_ && stab(builder_, level_ + 1);
    result_ = result_ && closeParenthesis(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (addOperator | dualOperator) EOL?
  public static boolean addOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "addOperatorEOL")) return false;
    if (!nextTokenIs(builder_, "<add operator eol>", ADDOPERATOR, DUALOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<add operator eol>");
    result_ = addOperatorEOL_0(builder_, level_ + 1);
    result_ = result_ && addOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ADD_OPERATOR_EOL, result_, false, null);
    return result_;
  }

  // addOperator | dualOperator
  private static boolean addOperatorEOL_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "addOperatorEOL_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ADDOPERATOR);
    if (!result_) result_ = consumeToken(builder_, DUALOPERATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL?
  private static boolean addOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "addOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // andOperator EOL?
  public static boolean andOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andOperatorEOL")) return false;
    if (!nextTokenIs(builder_, ANDOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ANDOPERATOR);
    result_ = result_ && andOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, AND_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean andOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // arrowOperator EOL?
  public static boolean arrowOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrowOperatorEOL")) return false;
    if (!nextTokenIs(builder_, ARROWOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ARROWOPERATOR);
    result_ = result_ && arrowOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, ARROW_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean arrowOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrowOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // assocBase ','?
  public static boolean assoc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assoc")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<assoc>");
    result_ = assocBase(builder_, level_ + 1);
    result_ = result_ && assoc_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ASSOC, result_, false, null);
    return result_;
  }

  // ','?
  private static boolean assoc_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assoc_1")) return false;
    consumeToken(builder_, ",");
    return true;
  }

  /* ********************************************************** */
  // assocExpression (',' assocExpression)?
  public static boolean assocBase(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocBase")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<assoc base>");
    result_ = assocExpression(builder_, level_ + 1);
    result_ = result_ && assocBase_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ASSOC_BASE, result_, false, null);
    return result_;
  }

  // (',' assocExpression)?
  private static boolean assocBase_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocBase_1")) return false;
    assocBase_1_0(builder_, level_ + 1);
    return true;
  }

  // ',' assocExpression
  private static boolean assocBase_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocBase_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && assocExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (containerExpression assocOperatorEOL containerExpression) |
  //   mapExpression
  public static boolean assocExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<assoc expression>");
    result_ = assocExpression_0(builder_, level_ + 1);
    if (!result_) result_ = mapExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ASSOC_EXPRESSION, result_, false, null);
    return result_;
  }

  // containerExpression assocOperatorEOL containerExpression
  private static boolean assocExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = containerExpression(builder_, level_ + 1);
    result_ = result_ && assocOperatorEOL(builder_, level_ + 1);
    result_ = result_ && containerExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // assocOperator EOL?
  public static boolean assocOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocOperatorEOL")) return false;
    if (!nextTokenIs(builder_, ASSOCOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ASSOCOPERATOR);
    result_ = result_ && assocOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, ASSOC_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean assocOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (matchedExpression pipeOperatorEOL matchedExpression assocOperatorEOL matchedExpression) |
  //   (unmatchedExpression pipeOperatorEOL expression assocOperatorEOL expression) |
  //   (matchedExpression pipeOperatorEOL mapExpression)
  public static boolean assocUpdate(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocUpdate")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<assoc update>");
    result_ = assocUpdate_0(builder_, level_ + 1);
    if (!result_) result_ = assocUpdate_1(builder_, level_ + 1);
    if (!result_) result_ = assocUpdate_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ASSOC_UPDATE, result_, false, null);
    return result_;
  }

  // matchedExpression pipeOperatorEOL matchedExpression assocOperatorEOL matchedExpression
  private static boolean assocUpdate_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocUpdate_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && pipeOperatorEOL(builder_, level_ + 1);
    result_ = result_ && matchedExpression(builder_, level_ + 1);
    result_ = result_ && assocOperatorEOL(builder_, level_ + 1);
    result_ = result_ && matchedExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // unmatchedExpression pipeOperatorEOL expression assocOperatorEOL expression
  private static boolean assocUpdate_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocUpdate_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unmatchedExpression(builder_, level_ + 1);
    result_ = result_ && pipeOperatorEOL(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && assocOperatorEOL(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // matchedExpression pipeOperatorEOL mapExpression
  private static boolean assocUpdate_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocUpdate_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && pipeOperatorEOL(builder_, level_ + 1);
    result_ = result_ && mapExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (matchedExpression | unmatchedExpression) pipeOperatorEOL keyword
  public static boolean assocUpdateKeyword(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocUpdateKeyword")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<assoc update keyword>");
    result_ = assocUpdateKeyword_0(builder_, level_ + 1);
    result_ = result_ && pipeOperatorEOL(builder_, level_ + 1);
    result_ = result_ && keyword(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ASSOC_UPDATE_KEYWORD, result_, false, null);
    return result_;
  }

  // matchedExpression | unmatchedExpression
  private static boolean assocUpdateKeyword_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assocUpdateKeyword_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = unmatchedExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // atOperator EOL?
  public static boolean atOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "atOperatorEOL")) return false;
    if (!nextTokenIs(builder_, ATOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ATOPERATOR);
    result_ = result_ && atOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, AT_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean atOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "atOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // openBit ('>>' | (containerArguments closeBit))
  public static boolean bitString(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitString")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<bit string>");
    result_ = openBit(builder_, level_ + 1);
    result_ = result_ && bitString_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BIT_STRING, result_, false, null);
    return result_;
  }

  // '>>' | (containerArguments closeBit)
  private static boolean bitString_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitString_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ">>");
    if (!result_) result_ = bitString_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // containerArguments closeBit
  private static boolean bitString_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitString_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = containerArguments(builder_, level_ + 1);
    result_ = result_ && closeBit(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // blockIdentifier EOL?
  public static boolean blockEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockEOL")) return false;
    if (!nextTokenIs(builder_, BLOCKIDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BLOCKIDENTIFIER);
    result_ = result_ && blockEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, BLOCK_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean blockEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L135-L136
  //     (parenthesesCall callArgumentsParentheses callArgumentsParentheses?) |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L137
  //     dotDoIdentifier |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L138
  //     (dotIdentifier callArgumentsNoParenthesesAll)
  //   ) doBlock
  public static boolean blockExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<block expression>");
    result_ = blockExpression_0(builder_, level_ + 1);
    result_ = result_ && doBlock(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BLOCK_EXPRESSION, result_, false, null);
    return result_;
  }

  // (parenthesesCall callArgumentsParentheses callArgumentsParentheses?) |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L137
  //     dotDoIdentifier |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L138
  //     (dotIdentifier callArgumentsNoParenthesesAll)
  private static boolean blockExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = blockExpression_0_0(builder_, level_ + 1);
    if (!result_) result_ = dotDoIdentifier(builder_, level_ + 1);
    if (!result_) result_ = blockExpression_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // parenthesesCall callArgumentsParentheses callArgumentsParentheses?
  private static boolean blockExpression_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockExpression_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parenthesesCall(builder_, level_ + 1);
    result_ = result_ && callArgumentsParentheses(builder_, level_ + 1);
    result_ = result_ && blockExpression_0_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // callArgumentsParentheses?
  private static boolean blockExpression_0_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockExpression_0_0_2")) return false;
    callArgumentsParentheses(builder_, level_ + 1);
    return true;
  }

  // dotIdentifier callArgumentsNoParenthesesAll
  private static boolean blockExpression_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockExpression_0_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = dotIdentifier(builder_, level_ + 1);
    result_ = result_ && callArgumentsNoParenthesesAll(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // blockEOL stabEOL?
  public static boolean blockItem(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockItem")) return false;
    if (!nextTokenIs(builder_, BLOCKIDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = blockEOL(builder_, level_ + 1);
    result_ = result_ && blockItem_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, BLOCK_ITEM, result_);
    return result_;
  }

  // stabEOL?
  private static boolean blockItem_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockItem_1")) return false;
    stabEOL(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // blockItem blockList?
  public static boolean blockList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockList")) return false;
    if (!nextTokenIs(builder_, BLOCKIDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = blockItem(builder_, level_ + 1);
    result_ = result_ && blockList_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, BLOCK_LIST, result_);
    return result_;
  }

  // blockList?
  private static boolean blockList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "blockList_1")) return false;
    blockList(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // openBracket
  //   (
  //     keyword |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L230-L231
  //     (containerExpression ','?)
  //   )
  //   closeBracket
  public static boolean bracketArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketArgument")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<bracket argument>");
    result_ = openBracket(builder_, level_ + 1);
    result_ = result_ && bracketArgument_1(builder_, level_ + 1);
    result_ = result_ && closeBracket(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BRACKET_ARGUMENT, result_, false, null);
    return result_;
  }

  // keyword |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L230-L231
  //     (containerExpression ','?)
  private static boolean bracketArgument_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketArgument_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = keyword(builder_, level_ + 1);
    if (!result_) result_ = bracketArgument_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // containerExpression ','?
  private static boolean bracketArgument_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketArgument_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = containerExpression(builder_, level_ + 1);
    result_ = result_ && bracketArgument_1_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ','?
  private static boolean bracketArgument_1_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketArgument_1_1_1")) return false;
    consumeToken(builder_, ",");
    return true;
  }

  /* ********************************************************** */
  // atOperatorEOL (dotBracketIdentifier | accessExpression) bracketArgument
  public static boolean bracketAtExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketAtExpression")) return false;
    if (!nextTokenIs(builder_, ATOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = atOperatorEOL(builder_, level_ + 1);
    result_ = result_ && bracketAtExpression_1(builder_, level_ + 1);
    result_ = result_ && bracketArgument(builder_, level_ + 1);
    exit_section_(builder_, marker_, BRACKET_AT_EXPRESSION, result_);
    return result_;
  }

  // dotBracketIdentifier | accessExpression
  private static boolean bracketAtExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketAtExpression_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = dotBracketIdentifier(builder_, level_ + 1);
    if (!result_) result_ = accessExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (dotBracketIdentifier | accessExpression) bracketArgument
  public static boolean bracketExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<bracket expression>");
    result_ = bracketExpression_0(builder_, level_ + 1);
    result_ = result_ && bracketArgument(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BRACKET_EXPRESSION, result_, false, null);
    return result_;
  }

  // dotBracketIdentifier | accessExpression
  private static boolean bracketExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bracketExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = dotBracketIdentifier(builder_, level_ + 1);
    if (!result_) result_ = accessExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // callArgumentsNoParenthesesOne | callArgumentsNoParenthesesMany
  public static boolean callArgumentsNoParenthesesAll(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesAll")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments no parentheses all>");
    result_ = callArgumentsNoParenthesesOne(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesMany(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_NO_PARENTHESES_ALL, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // (matchedExpression | callArgumentsNoParenthesesCommaExpression)
  //   ','
  //   callArgumentsNoParenthesesExpression
  public static boolean callArgumentsNoParenthesesCommaExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesCommaExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments no parentheses comma expression>");
    result_ = callArgumentsNoParenthesesCommaExpression_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ",");
    result_ = result_ && consumeToken(builder_, CALLARGUMENTSNOPARENTHESESEXPRESSION);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_NO_PARENTHESES_COMMA_EXPRESSION, result_, false, null);
    return result_;
  }

  // matchedExpression | callArgumentsNoParenthesesCommaExpression
  private static boolean callArgumentsNoParenthesesCommaExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesCommaExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesCommaExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // callArgumentsNoParenthesesKeywordExpression (',' callArgumentsNoParenthesesKeyword)?
  public static boolean callArgumentsNoParenthesesKeyword(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesKeyword")) return false;
    if (!nextTokenIs(builder_, "<call arguments no parentheses keyword>", KEYWORDIDENTIFIER, KEYWORDIDENTIFIERSAFE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments no parentheses keyword>");
    result_ = callArgumentsNoParenthesesKeywordExpression(builder_, level_ + 1);
    result_ = result_ && callArgumentsNoParenthesesKeyword_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD, result_, false, null);
    return result_;
  }

  // (',' callArgumentsNoParenthesesKeyword)?
  private static boolean callArgumentsNoParenthesesKeyword_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesKeyword_1")) return false;
    callArgumentsNoParenthesesKeyword_1_0(builder_, level_ + 1);
    return true;
  }

  // ',' callArgumentsNoParenthesesKeyword
  private static boolean callArgumentsNoParenthesesKeyword_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesKeyword_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && callArgumentsNoParenthesesKeyword(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // keywordEOL callArgumentsNoParenthesesExpression
  public static boolean callArgumentsNoParenthesesKeywordExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesKeywordExpression")) return false;
    if (!nextTokenIs(builder_, "<call arguments no parentheses keyword expression>", KEYWORDIDENTIFIER, KEYWORDIDENTIFIERSAFE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments no parentheses keyword expression>");
    result_ = keywordEOL(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CALLARGUMENTSNOPARENTHESESEXPRESSION);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD_EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // (matchedExpression ',' callArgumentsNoParenthesesKeyword) |
  //   callArgumentsNoParenthesesCommaExpression |
  //   (callArgumentsNoParenthesesCommaExpression ',' callArgumentsNoParenthesesKeyword)
  public static boolean callArgumentsNoParenthesesMany(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesMany")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments no parentheses many>");
    result_ = callArgumentsNoParenthesesMany_0(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesCommaExpression(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesMany_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_NO_PARENTHESES_MANY, result_, false, null);
    return result_;
  }

  // matchedExpression ',' callArgumentsNoParenthesesKeyword
  private static boolean callArgumentsNoParenthesesMany_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesMany_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ",");
    result_ = result_ && callArgumentsNoParenthesesKeyword(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // callArgumentsNoParenthesesCommaExpression ',' callArgumentsNoParenthesesKeyword
  private static boolean callArgumentsNoParenthesesMany_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesMany_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = callArgumentsNoParenthesesCommaExpression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ",");
    result_ = result_ && callArgumentsNoParenthesesKeyword(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // callArgumentsNoParenthesesMany |
  //   emptyParentheses |
  //   (openParenthesis  callArgumentsNoParenthesesKeyword closeParenthesis) |
  //   (openParenthesis  callArgumentsNoParenthesesMany closeParenthesis)
  public static boolean callArgumentsNoParenthesesManyStrict(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesManyStrict")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments no parentheses many strict>");
    result_ = callArgumentsNoParenthesesMany(builder_, level_ + 1);
    if (!result_) result_ = emptyParentheses(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesManyStrict_2(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesManyStrict_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_NO_PARENTHESES_MANY_STRICT, result_, false, null);
    return result_;
  }

  // openParenthesis  callArgumentsNoParenthesesKeyword closeParenthesis
  private static boolean callArgumentsNoParenthesesManyStrict_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesManyStrict_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = openParenthesis(builder_, level_ + 1);
    result_ = result_ && callArgumentsNoParenthesesKeyword(builder_, level_ + 1);
    result_ = result_ && closeParenthesis(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // openParenthesis  callArgumentsNoParenthesesMany closeParenthesis
  private static boolean callArgumentsNoParenthesesManyStrict_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesManyStrict_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = openParenthesis(builder_, level_ + 1);
    result_ = result_ && callArgumentsNoParenthesesMany(builder_, level_ + 1);
    result_ = result_ && closeParenthesis(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // callArgumentsNoParenthesesKeyword | matchedExpression | noParenthesesExpression
  public static boolean callArgumentsNoParenthesesOne(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsNoParenthesesOne")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments no parentheses one>");
    result_ = callArgumentsNoParenthesesKeyword(builder_, level_ + 1);
    if (!result_) result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = noParenthesesExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_NO_PARENTHESES_ONE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //   (
  //     openParenthesis
  //     (
  //       noParenthesesExpression |
  //       keyword |
  //       (callArgumentsParenthesesBase (',' keyword)?)
  //     )
  //     closeParenthesis
  //   )
  public static boolean callArgumentsParentheses(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParentheses")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments parentheses>");
    result_ = emptyParentheses(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsParentheses_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_PARENTHESES, result_, false, null);
    return result_;
  }

  // openParenthesis
  //     (
  //       noParenthesesExpression |
  //       keyword |
  //       (callArgumentsParenthesesBase (',' keyword)?)
  //     )
  //     closeParenthesis
  private static boolean callArgumentsParentheses_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParentheses_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = openParenthesis(builder_, level_ + 1);
    result_ = result_ && callArgumentsParentheses_1_1(builder_, level_ + 1);
    result_ = result_ && closeParenthesis(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // noParenthesesExpression |
  //       keyword |
  //       (callArgumentsParenthesesBase (',' keyword)?)
  private static boolean callArgumentsParentheses_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParentheses_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = noParenthesesExpression(builder_, level_ + 1);
    if (!result_) result_ = keyword(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsParentheses_1_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // callArgumentsParenthesesBase (',' keyword)?
  private static boolean callArgumentsParentheses_1_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParentheses_1_1_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = callArgumentsParenthesesBase(builder_, level_ + 1);
    result_ = result_ && callArgumentsParentheses_1_1_2_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (',' keyword)?
  private static boolean callArgumentsParentheses_1_1_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParentheses_1_1_2_1")) return false;
    callArgumentsParentheses_1_1_2_1_0(builder_, level_ + 1);
    return true;
  }

  // ',' keyword
  private static boolean callArgumentsParentheses_1_1_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParentheses_1_1_2_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && keyword(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (callArgumentsParenthesesBase ',')? containerExpression
  public static boolean callArgumentsParenthesesBase(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParenthesesBase")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments parentheses base>");
    result_ = callArgumentsParenthesesBase_0(builder_, level_ + 1);
    result_ = result_ && containerExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_PARENTHESES_BASE, result_, false, null);
    return result_;
  }

  // (callArgumentsParenthesesBase ',')?
  private static boolean callArgumentsParenthesesBase_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParenthesesBase_0")) return false;
    callArgumentsParenthesesBase_0_0(builder_, level_ + 1);
    return true;
  }

  // callArgumentsParenthesesBase ','
  private static boolean callArgumentsParenthesesBase_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParenthesesBase_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = callArgumentsParenthesesBase(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ",");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // matchedExpression | emptyParentheses | noParenthesesExpression
  public static boolean callArgumentsParenthesesExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "callArgumentsParenthesesExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<call arguments parentheses expression>");
    result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = emptyParentheses(builder_, level_ + 1);
    if (!result_) result_ = noParenthesesExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CALL_ARGUMENTS_PARENTHESES_EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // captureOperator EOL?
  public static boolean captureOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "captureOperatorEOL")) return false;
    if (!nextTokenIs(builder_, CAPTUREOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CAPTUREOPERATOR);
    result_ = result_ && captureOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, CAPTURE_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean captureOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "captureOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // EOL? '>>'
  public static boolean closeBit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeBit")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<close bit>");
    result_ = closeBit_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ">>");
    exit_section_(builder_, level_, marker_, CLOSE_BIT, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean closeBit_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeBit_0")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // EOL? ']'
  public static boolean closeBracket(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeBracket")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<close bracket>");
    result_ = closeBracket_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, "]");
    exit_section_(builder_, level_, marker_, CLOSE_BRACKET, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean closeBracket_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeBracket_0")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // EOL? '}'
  public static boolean closeCurly(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeCurly")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<close curly>");
    result_ = closeCurly_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, level_, marker_, CLOSE_CURLY, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean closeCurly_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeCurly_0")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // EOL? ')'
  public static boolean closeParenthesis(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeParenthesis")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<close parenthesis>");
    result_ = closeParenthesis_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ")");
    exit_section_(builder_, level_, marker_, CLOSE_PARENTHESIS, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean closeParenthesis_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "closeParenthesis_0")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // compOperator EOL?
  public static boolean compOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compOperatorEOL")) return false;
    if (!nextTokenIs(builder_, COMPOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMPOPERATOR);
    result_ = result_ && compOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, COMP_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean compOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // containerArgumentsBase (',' keyword?)?
  public static boolean containerArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerArguments")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<container arguments>");
    result_ = containerArgumentsBase(builder_, level_ + 1);
    result_ = result_ && containerArguments_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CONTAINER_ARGUMENTS, result_, false, null);
    return result_;
  }

  // (',' keyword?)?
  private static boolean containerArguments_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerArguments_1")) return false;
    containerArguments_1_0(builder_, level_ + 1);
    return true;
  }

  // ',' keyword?
  private static boolean containerArguments_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerArguments_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && containerArguments_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // keyword?
  private static boolean containerArguments_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerArguments_1_0_1")) return false;
    keyword(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (containerArgumentsBase ',')? containerExpression
  public static boolean containerArgumentsBase(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerArgumentsBase")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<container arguments base>");
    result_ = containerArgumentsBase_0(builder_, level_ + 1);
    result_ = result_ && containerExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CONTAINER_ARGUMENTS_BASE, result_, false, null);
    return result_;
  }

  // (containerArgumentsBase ',')?
  private static boolean containerArgumentsBase_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerArgumentsBase_0")) return false;
    containerArgumentsBase_0_0(builder_, level_ + 1);
    return true;
  }

  // containerArgumentsBase ','
  private static boolean containerArgumentsBase_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerArgumentsBase_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = containerArgumentsBase(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ",");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // emptyParentheses | matchedExpression | unmatchedExpression | noParenthesesExpression
  public static boolean containerExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containerExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<container expression>");
    result_ = emptyParentheses(builder_, level_ + 1);
    if (!result_) result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = unmatchedExpression(builder_, level_ + 1);
    if (!result_) result_ = noParenthesesExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, CONTAINER_EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // doEOL (
  //     'end' |
  //     (stab endEOL) |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L245-L246
  //     (stabEOL? blockList 'end')
  //   )
  public static boolean doBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doBlock")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<do block>");
    result_ = doEOL(builder_, level_ + 1);
    result_ = result_ && doBlock_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, DO_BLOCK, result_, false, null);
    return result_;
  }

  // 'end' |
  //     (stab endEOL) |
  //     // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L245-L246
  //     (stabEOL? blockList 'end')
  private static boolean doBlock_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doBlock_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "end");
    if (!result_) result_ = doBlock_1_1(builder_, level_ + 1);
    if (!result_) result_ = doBlock_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // stab endEOL
  private static boolean doBlock_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doBlock_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = stab(builder_, level_ + 1);
    result_ = result_ && endEOL(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // stabEOL? blockList 'end'
  private static boolean doBlock_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doBlock_1_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = doBlock_1_2_0(builder_, level_ + 1);
    result_ = result_ && blockList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, "end");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // stabEOL?
  private static boolean doBlock_1_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doBlock_1_2_0")) return false;
    stabEOL(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // 'do' EOL?
  public static boolean doEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doEOL")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<do eol>");
    result_ = consumeToken(builder_, "do");
    result_ = result_ && doEOL_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, DO_EOL, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean doEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (matchedExpression dotOperator)? identifier
  public static boolean dotAlias(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotAlias")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dot alias>");
    result_ = dotAlias_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENTIFIER);
    exit_section_(builder_, level_, marker_, DOT_ALIAS, result_, false, null);
    return result_;
  }

  // (matchedExpression dotOperator)?
  private static boolean dotAlias_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotAlias_0")) return false;
    dotAlias_0_0(builder_, level_ + 1);
    return true;
  }

  // matchedExpression dotOperator
  private static boolean dotAlias_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotAlias_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && dotOperator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (matchedExpression dotOperator)? bracketIdentifier
  public static boolean dotBracketIdentifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotBracketIdentifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dot bracket identifier>");
    result_ = dotBracketIdentifier_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BRACKETIDENTIFIER);
    exit_section_(builder_, level_, marker_, DOT_BRACKET_IDENTIFIER, result_, false, null);
    return result_;
  }

  // (matchedExpression dotOperator)?
  private static boolean dotBracketIdentifier_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotBracketIdentifier_0")) return false;
    dotBracketIdentifier_0_0(builder_, level_ + 1);
    return true;
  }

  // matchedExpression dotOperator
  private static boolean dotBracketIdentifier_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotBracketIdentifier_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && dotOperator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (matchedExpression dotOperator)? doIdentifier
  public static boolean dotDoIdentifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotDoIdentifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dot do identifier>");
    result_ = dotDoIdentifier_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOIDENTIFIER);
    exit_section_(builder_, level_, marker_, DOT_DO_IDENTIFIER, result_, false, null);
    return result_;
  }

  // (matchedExpression dotOperator)?
  private static boolean dotDoIdentifier_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotDoIdentifier_0")) return false;
    dotDoIdentifier_0_0(builder_, level_ + 1);
    return true;
  }

  // matchedExpression dotOperator
  private static boolean dotDoIdentifier_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotDoIdentifier_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && dotOperator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (matchedExpression dotOperator)? identifier
  public static boolean dotIdentifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotIdentifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dot identifier>");
    result_ = dotIdentifier_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENTIFIER);
    exit_section_(builder_, level_, marker_, DOT_IDENTIFIER, result_, false, null);
    return result_;
  }

  // (matchedExpression dotOperator)?
  private static boolean dotIdentifier_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotIdentifier_0")) return false;
    dotIdentifier_0_0(builder_, level_ + 1);
    return true;
  }

  // matchedExpression dotOperator
  private static boolean dotIdentifier_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotIdentifier_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && dotOperator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '.' EOL?
  public static boolean dotOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotOperator")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dot operator>");
    result_ = consumeToken(builder_, ".");
    result_ = result_ && dotOperator_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, DOT_OPERATOR, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean dotOperator_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotOperator_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (matchedExpression dotOperator)? operatorIdentifier
  public static boolean dotOperatorIdentifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotOperatorIdentifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dot operator identifier>");
    result_ = dotOperatorIdentifier_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OPERATORIDENTIFIER);
    exit_section_(builder_, level_, marker_, DOT_OPERATOR_IDENTIFIER, result_, false, null);
    return result_;
  }

  // (matchedExpression dotOperator)?
  private static boolean dotOperatorIdentifier_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotOperatorIdentifier_0")) return false;
    dotOperatorIdentifier_0_0(builder_, level_ + 1);
    return true;
  }

  // matchedExpression dotOperator
  private static boolean dotOperatorIdentifier_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotOperatorIdentifier_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && dotOperator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (matchedExpression dotOperator)? parenthesesIdentifier
  public static boolean dotParenthesesIdentifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotParenthesesIdentifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dot parentheses identifier>");
    result_ = dotParenthesesIdentifier_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, PARENTHESESIDENTIFIER);
    exit_section_(builder_, level_, marker_, DOT_PARENTHESES_IDENTIFIER, result_, false, null);
    return result_;
  }

  // (matchedExpression dotOperator)?
  private static boolean dotParenthesesIdentifier_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotParenthesesIdentifier_0")) return false;
    dotParenthesesIdentifier_0_0(builder_, level_ + 1);
    return true;
  }

  // matchedExpression dotOperator
  private static boolean dotParenthesesIdentifier_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dotParenthesesIdentifier_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && dotOperator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // EOL | expressionList | (EOL expressionList) | (EOL expressionList EOL) | ''
  static boolean elixirFile(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EOL);
    if (!result_) result_ = expressionList(builder_, level_ + 1);
    if (!result_) result_ = elixirFile_2(builder_, level_ + 1);
    if (!result_) result_ = elixirFile_3(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL expressionList
  private static boolean elixirFile_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EOL);
    result_ = result_ && expressionList(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL expressionList EOL
  private static boolean elixirFile_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EOL);
    result_ = result_ && expressionList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOL);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // openParenthesis ')'
  public static boolean emptyParentheses(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "emptyParentheses")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<empty parentheses>");
    result_ = openParenthesis(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ")");
    exit_section_(builder_, level_, marker_, EMPTY_PARENTHESES, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // EOL? 'end'
  public static boolean endEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "endEOL")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<end eol>");
    result_ = endEOL_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, "end");
    exit_section_(builder_, level_, marker_, END_EOL, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean endEOL_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "endEOL_0")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // emptyParentheses | matchedExpression | noParenthesesExpression | unmatchedExpression
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression>");
    result_ = emptyParentheses(builder_, level_ + 1);
    if (!result_) result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = noParenthesesExpression(builder_, level_ + 1);
    if (!result_) result_ = unmatchedExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // expression | (expressionList EOL expression)
  public static boolean expressionList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression list>");
    result_ = expression(builder_, level_ + 1);
    if (!result_) result_ = expressionList_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, EXPRESSION_LIST, result_, false, null);
    return result_;
  }

  // expressionList EOL expression
  private static boolean expressionList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expressionList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOL);
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // 'fn' EOL?
  public static boolean fnEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fnEOL")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<fn eol>");
    result_ = consumeToken(builder_, "fn");
    result_ = result_ && fnEOL_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FN_EOL, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean fnEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fnEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // hatOperator EOL?
  public static boolean hatOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "hatOperatorEOL")) return false;
    if (!nextTokenIs(builder_, HATOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, HATOPERATOR);
    result_ = result_ && hatOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, HAT_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean hatOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "hatOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // inMatchOperator EOL?
  public static boolean inMatchOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inMatchOperatorEOL")) return false;
    if (!nextTokenIs(builder_, INMATCHOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, INMATCHOPERATOR);
    result_ = result_ && inMatchOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, IN_MATCH_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean inMatchOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inMatchOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // inOperator EOL?
  public static boolean inOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inOperatorEOL")) return false;
    if (!nextTokenIs(builder_, INOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, INOPERATOR);
    result_ = result_ && inOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, IN_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean inOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // keywordBase ','?
  public static boolean keyword(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keyword")) return false;
    if (!nextTokenIs(builder_, "<keyword>", KEYWORDIDENTIFIER, KEYWORDIDENTIFIERSAFE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<keyword>");
    result_ = keywordBase(builder_, level_ + 1);
    result_ = result_ && keyword_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, KEYWORD, result_, false, null);
    return result_;
  }

  // ','?
  private static boolean keyword_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keyword_1")) return false;
    consumeToken(builder_, ",");
    return true;
  }

  /* ********************************************************** */
  // ((keywordBase ',')? keywordEOL) containerExpression
  public static boolean keywordBase(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordBase")) return false;
    if (!nextTokenIs(builder_, "<keyword base>", KEYWORDIDENTIFIER, KEYWORDIDENTIFIERSAFE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<keyword base>");
    result_ = keywordBase_0(builder_, level_ + 1);
    result_ = result_ && containerExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, KEYWORD_BASE, result_, false, null);
    return result_;
  }

  // (keywordBase ',')? keywordEOL
  private static boolean keywordBase_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordBase_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = keywordBase_0_0(builder_, level_ + 1);
    result_ = result_ && keywordEOL(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (keywordBase ',')?
  private static boolean keywordBase_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordBase_0_0")) return false;
    keywordBase_0_0_0(builder_, level_ + 1);
    return true;
  }

  // keywordBase ','
  private static boolean keywordBase_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordBase_0_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = keywordBase(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ",");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (keywordIdentifier EOL?) |
  //   (keywordIdentifierSafe EOL?) |
  //   (keywordIdentifierSafe EOL?)
  public static boolean keywordEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordEOL")) return false;
    if (!nextTokenIs(builder_, "<keyword eol>", KEYWORDIDENTIFIER, KEYWORDIDENTIFIERSAFE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<keyword eol>");
    result_ = keywordEOL_0(builder_, level_ + 1);
    if (!result_) result_ = keywordEOL_1(builder_, level_ + 1);
    if (!result_) result_ = keywordEOL_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, KEYWORD_EOL, result_, false, null);
    return result_;
  }

  // keywordIdentifier EOL?
  private static boolean keywordEOL_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordEOL_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KEYWORDIDENTIFIER);
    result_ = result_ && keywordEOL_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL?
  private static boolean keywordEOL_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordEOL_0_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  // keywordIdentifierSafe EOL?
  private static boolean keywordEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordEOL_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KEYWORDIDENTIFIERSAFE);
    result_ = result_ && keywordEOL_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL?
  private static boolean keywordEOL_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordEOL_1_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  // keywordIdentifierSafe EOL?
  private static boolean keywordEOL_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordEOL_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KEYWORDIDENTIFIERSAFE);
    result_ = result_ && keywordEOL_2_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL?
  private static boolean keywordEOL_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordEOL_2_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // openBracket (']' | (listArguments closeBracket))
  public static boolean list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<list>");
    result_ = openBracket(builder_, level_ + 1);
    result_ = result_ && list_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, LIST, result_, false, null);
    return result_;
  }

  // ']' | (listArguments closeBracket)
  private static boolean list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "]");
    if (!result_) result_ = list_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // listArguments closeBracket
  private static boolean list_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = listArguments(builder_, level_ + 1);
    result_ = result_ && closeBracket(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // keyword |
  //   (containerArgumentsBase (',' keyword?)?)
  public static boolean listArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "listArguments")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<list arguments>");
    result_ = keyword(builder_, level_ + 1);
    if (!result_) result_ = listArguments_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, LIST_ARGUMENTS, result_, false, null);
    return result_;
  }

  // containerArgumentsBase (',' keyword?)?
  private static boolean listArguments_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "listArguments_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = containerArgumentsBase(builder_, level_ + 1);
    result_ = result_ && listArguments_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (',' keyword?)?
  private static boolean listArguments_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "listArguments_1_1")) return false;
    listArguments_1_1_0(builder_, level_ + 1);
    return true;
  }

  // ',' keyword?
  private static boolean listArguments_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "listArguments_1_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && listArguments_1_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // keyword?
  private static boolean listArguments_1_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "listArguments_1_1_0_1")) return false;
    keyword(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (
  //     mapOperator |
  //     (structOperator mapExpression EOL?)
  //   )
  //   mapArguments
  public static boolean map(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "map")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<map>");
    result_ = map_0(builder_, level_ + 1);
    result_ = result_ && mapArguments(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MAP, result_, false, null);
    return result_;
  }

  // mapOperator |
  //     (structOperator mapExpression EOL?)
  private static boolean map_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "map_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = mapOperator(builder_, level_ + 1);
    if (!result_) result_ = map_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // structOperator mapExpression EOL?
  private static boolean map_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "map_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = structOperator(builder_, level_ + 1);
    result_ = result_ && mapExpression(builder_, level_ + 1);
    result_ = result_ && map_0_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL?
  private static boolean map_0_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "map_0_1_2")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // openCurly
  //   (
  //     '}' |
  //     mapClose |
  //     (assocUpdate ','? (closeCurly | mapClose)) |
  //     (assocUpdateKeyword closeCurly)
  //   )
  public static boolean mapArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapArguments")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<map arguments>");
    result_ = openCurly(builder_, level_ + 1);
    result_ = result_ && mapArguments_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MAP_ARGUMENTS, result_, false, null);
    return result_;
  }

  // '}' |
  //     mapClose |
  //     (assocUpdate ','? (closeCurly | mapClose)) |
  //     (assocUpdateKeyword closeCurly)
  private static boolean mapArguments_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapArguments_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "}");
    if (!result_) result_ = mapClose(builder_, level_ + 1);
    if (!result_) result_ = mapArguments_1_2(builder_, level_ + 1);
    if (!result_) result_ = mapArguments_1_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // assocUpdate ','? (closeCurly | mapClose)
  private static boolean mapArguments_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapArguments_1_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = assocUpdate(builder_, level_ + 1);
    result_ = result_ && mapArguments_1_2_1(builder_, level_ + 1);
    result_ = result_ && mapArguments_1_2_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ','?
  private static boolean mapArguments_1_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapArguments_1_2_1")) return false;
    consumeToken(builder_, ",");
    return true;
  }

  // closeCurly | mapClose
  private static boolean mapArguments_1_2_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapArguments_1_2_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = closeCurly(builder_, level_ + 1);
    if (!result_) result_ = mapClose(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // assocUpdateKeyword closeCurly
  private static boolean mapArguments_1_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapArguments_1_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = assocUpdateKeyword(builder_, level_ + 1);
    result_ = result_ && closeCurly(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (
  //     keyword |
  //     assoc |
  //     (assocBase ',' keyword)
  //   )
  //   closeCurly
  public static boolean mapClose(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapClose")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<map close>");
    result_ = mapClose_0(builder_, level_ + 1);
    result_ = result_ && closeCurly(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MAP_CLOSE, result_, false, null);
    return result_;
  }

  // keyword |
  //     assoc |
  //     (assocBase ',' keyword)
  private static boolean mapClose_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapClose_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = keyword(builder_, level_ + 1);
    if (!result_) result_ = assoc(builder_, level_ + 1);
    if (!result_) result_ = mapClose_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // assocBase ',' keyword
  private static boolean mapClose_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapClose_0_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = assocBase(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ",");
    result_ = result_ && keyword(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // maxExpression |
  //   dotIdentifier |
  //   (atOperatorEOL mapExpression)
  public static boolean mapExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<map expression>");
    result_ = maxExpression(builder_, level_ + 1);
    if (!result_) result_ = dotIdentifier(builder_, level_ + 1);
    if (!result_) result_ = mapExpression_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MAP_EXPRESSION, result_, false, null);
    return result_;
  }

  // atOperatorEOL mapExpression
  private static boolean mapExpression_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapExpression_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = atOperatorEOL(builder_, level_ + 1);
    result_ = result_ && mapExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '%{}' EOL?
  public static boolean mapOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapOperator")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<map operator>");
    result_ = consumeToken(builder_, "%{}");
    result_ = result_ && mapOperator_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MAP_OPERATOR, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean mapOperator_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mapOperator_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // matchOperator EOL?
  public static boolean matchOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchOperatorEOL")) return false;
    if (!nextTokenIs(builder_, MATCHOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, MATCHOPERATOR);
    result_ = result_ && matchOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, MATCH_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean matchOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (matchedExpression (matchedOperatorExpression | noParenthesesExpression)) |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L115-L120
  //   ((unaryOperatorEOL | atOperatorEOL | captureOperatorEOL) (matchedExpression | noParenthesesExpression))
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L121
  //   noParenthesesOneExpression |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L122
  //   accessExpression
  public static boolean matchedExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<matched expression>");
    result_ = matchedExpression_0(builder_, level_ + 1);
    if (!result_) result_ = matchedExpression_1(builder_, level_ + 1);
    if (!result_) result_ = accessExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MATCHED_EXPRESSION, result_, false, null);
    return result_;
  }

  // matchedExpression (matchedOperatorExpression | noParenthesesExpression)
  private static boolean matchedExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && matchedExpression_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // matchedOperatorExpression | noParenthesesExpression
  private static boolean matchedExpression_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedExpression_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedOperatorExpression(builder_, level_ + 1);
    if (!result_) result_ = noParenthesesExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ((unaryOperatorEOL | atOperatorEOL | captureOperatorEOL) (matchedExpression | noParenthesesExpression))
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L121
  //   noParenthesesOneExpression
  private static boolean matchedExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedExpression_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression_1_0(builder_, level_ + 1);
    result_ = result_ && noParenthesesOneExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (unaryOperatorEOL | atOperatorEOL | captureOperatorEOL) (matchedExpression | noParenthesesExpression)
  private static boolean matchedExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedExpression_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression_1_0_0(builder_, level_ + 1);
    result_ = result_ && matchedExpression_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // unaryOperatorEOL | atOperatorEOL | captureOperatorEOL
  private static boolean matchedExpression_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedExpression_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unaryOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = atOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = captureOperatorEOL(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // matchedExpression | noParenthesesExpression
  private static boolean matchedExpression_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedExpression_1_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = noParenthesesExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // operatorEOL matchedExpression
  public static boolean matchedOperatorExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchedOperatorExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<matched operator expression>");
    result_ = operatorEOL(builder_, level_ + 1);
    result_ = result_ && matchedExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MATCHED_OPERATOR_EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // atom |
  //   atom_safe |
  //   atom_unsafe |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L225-L226
  //   (parenthesesCall callArgumentsParentheses callArgumentsParentheses?) |
  //   dotAlias
  public static boolean maxExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "maxExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<max expression>");
    result_ = consumeToken(builder_, ATOM);
    if (!result_) result_ = consumeToken(builder_, ATOM_SAFE);
    if (!result_) result_ = consumeToken(builder_, ATOM_UNSAFE);
    if (!result_) result_ = maxExpression_3(builder_, level_ + 1);
    if (!result_) result_ = dotAlias(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MAX_EXPRESSION, result_, false, null);
    return result_;
  }

  // parenthesesCall callArgumentsParentheses callArgumentsParentheses?
  private static boolean maxExpression_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "maxExpression_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parenthesesCall(builder_, level_ + 1);
    result_ = result_ && callArgumentsParentheses(builder_, level_ + 1);
    result_ = result_ && maxExpression_3_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // callArgumentsParentheses?
  private static boolean maxExpression_3_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "maxExpression_3_2")) return false;
    callArgumentsParentheses(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // multiplyOperator EOL?
  public static boolean multiplyOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplyOperatorEOL")) return false;
    if (!nextTokenIs(builder_, MULTIPLYOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, MULTIPLYOPERATOR);
    result_ = result_ && multiplyOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, MULTIPLY_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean multiplyOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplyOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (dotOperatorIdentifier | dotIdentifier) | callArgumentsNoParenthesesManyStrict
  public static boolean noParenthesesExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<no parentheses expression>");
    result_ = noParenthesesExpression_0(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesManyStrict(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, NO_PARENTHESES_EXPRESSION, result_, false, null);
    return result_;
  }

  // dotOperatorIdentifier | dotIdentifier
  private static boolean noParenthesesExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = dotOperatorIdentifier(builder_, level_ + 1);
    if (!result_) result_ = dotIdentifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (dotDoIdentifier | dotIdentifier) callArgumentsNoParenthesesOne?
  public static boolean noParenthesesOneExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesOneExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<no parentheses one expression>");
    result_ = noParenthesesOneExpression_0(builder_, level_ + 1);
    result_ = result_ && noParenthesesOneExpression_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, NO_PARENTHESES_ONE_EXPRESSION, result_, false, null);
    return result_;
  }

  // dotDoIdentifier | dotIdentifier
  private static boolean noParenthesesOneExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesOneExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = dotDoIdentifier(builder_, level_ + 1);
    if (!result_) result_ = dotIdentifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // callArgumentsNoParenthesesOne?
  private static boolean noParenthesesOneExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesOneExpression_1")) return false;
    callArgumentsNoParenthesesOne(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (operatorEOL noParenthesesExpression) |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L173
  //   (whenOperatorEOL callArgumentsNoParenthesesKeyword)
  public static boolean noParenthesesOperatorExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesOperatorExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<no parentheses operator expression>");
    result_ = noParenthesesOperatorExpression_0(builder_, level_ + 1);
    if (!result_) result_ = noParenthesesOperatorExpression_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, NO_PARENTHESES_OPERATOR_EXPRESSION, result_, false, null);
    return result_;
  }

  // operatorEOL noParenthesesExpression
  private static boolean noParenthesesOperatorExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesOperatorExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = operatorEOL(builder_, level_ + 1);
    result_ = result_ && noParenthesesExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // whenOperatorEOL callArgumentsNoParenthesesKeyword
  private static boolean noParenthesesOperatorExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "noParenthesesOperatorExpression_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = whenOperatorEOL(builder_, level_ + 1);
    result_ = result_ && callArgumentsNoParenthesesKeyword(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '<<' EOL?
  public static boolean openBit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openBit")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<open bit>");
    result_ = consumeToken(builder_, "<<");
    result_ = result_ && openBit_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, OPEN_BIT, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean openBit_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openBit_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // '[' EOL?
  public static boolean openBracket(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openBracket")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<open bracket>");
    result_ = consumeToken(builder_, "[");
    result_ = result_ && openBracket_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, OPEN_BRACKET, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean openBracket_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openBracket_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // '{' EOL?
  public static boolean openCurly(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openCurly")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<open curly>");
    result_ = consumeToken(builder_, "{");
    result_ = result_ && openCurly_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, OPEN_CURLY, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean openCurly_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openCurly_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // '(' EOL?
  public static boolean openParenthesis(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openParenthesis")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<open parenthesis>");
    result_ = consumeToken(builder_, "(");
    result_ = result_ && openParenthesis_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, OPEN_PARENTHESIS, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean openParenthesis_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "openParenthesis_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // matchOperatorEOL |
  //   addOperatorEOL |
  //   multiplyOperatorEOL |
  //   hatOperatorEOL |
  //   twoOperatorEOL |
  //   andOperatorEOL |
  //   orOperatorEOL |
  //   inOperatorEOL |
  //   inMatchOperatorEOL |
  //   typeOperatorEOL |
  //   whenOperatorEOL |
  //   pipeOperatorEOL |
  //   compOperatorEOL |
  //   relOperatorEOL |
  //   arrowOperatorEOL
  public static boolean operatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "operatorEOL")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<operator eol>");
    result_ = matchOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = addOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = multiplyOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = hatOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = twoOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = andOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = orOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = inOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = inMatchOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = typeOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = whenOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = pipeOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = compOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = relOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = arrowOperatorEOL(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, OPERATOR_EOL, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // operatorEOL expression
  public static boolean operatorExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "operatorExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<operator expression>");
    result_ = operatorEOL(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, OPERATOR_EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // orOperator EOL?
  public static boolean orOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orOperatorEOL")) return false;
    if (!nextTokenIs(builder_, OROPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OROPERATOR);
    result_ = result_ && orOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, OR_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean orOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // dotParenthesesIdentifier | (matchedExpression dotCallOperator)
  public static boolean parenthesesCall(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parenthesesCall")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<parentheses call>");
    result_ = dotParenthesesIdentifier(builder_, level_ + 1);
    if (!result_) result_ = parenthesesCall_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, PARENTHESES_CALL, result_, false, null);
    return result_;
  }

  // matchedExpression dotCallOperator
  private static boolean parenthesesCall_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parenthesesCall_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchedExpression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOTCALLOPERATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // pipeOperator EOL?
  public static boolean pipeOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pipeOperatorEOL")) return false;
    if (!nextTokenIs(builder_, PIPEOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, PIPEOPERATOR);
    result_ = result_ && pipeOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, PIPE_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean pipeOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pipeOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // relOperator EOL?
  public static boolean relOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "relOperatorEOL")) return false;
    if (!nextTokenIs(builder_, RELOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, RELOPERATOR);
    result_ = result_ && relOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, REL_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean relOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "relOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (stab EOL)? stabExpression
  public static boolean stab(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stab")) return false;
    if (!nextTokenIs(builder_, STABEXPRESSION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = stab_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STABEXPRESSION);
    exit_section_(builder_, marker_, STAB, result_);
    return result_;
  }

  // (stab EOL)?
  private static boolean stab_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stab_0")) return false;
    stab_0_0(builder_, level_ + 1);
    return true;
  }

  // stab EOL
  private static boolean stab_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stab_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = stab(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOL);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // expression |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L267-L269
  //   (callArgumentsNoParenthesesAll? stabOperatorEOL stabMaybeExpression) |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L270-L273
  //   (stabParenthesesMany (whenOperator expression)? stabOperatorEOL stabMaybeExpression)
  public static boolean stabEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabEOL")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<stab eol>");
    result_ = expression(builder_, level_ + 1);
    if (!result_) result_ = stabEOL_1(builder_, level_ + 1);
    if (!result_) result_ = stabEOL_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, STAB_EOL, result_, false, null);
    return result_;
  }

  // callArgumentsNoParenthesesAll? stabOperatorEOL stabMaybeExpression
  private static boolean stabEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabEOL_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = stabEOL_1_0(builder_, level_ + 1);
    result_ = result_ && stabOperatorEOL(builder_, level_ + 1);
    result_ = result_ && stabMaybeExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // callArgumentsNoParenthesesAll?
  private static boolean stabEOL_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabEOL_1_0")) return false;
    callArgumentsNoParenthesesAll(builder_, level_ + 1);
    return true;
  }

  // stabParenthesesMany (whenOperator expression)? stabOperatorEOL stabMaybeExpression
  private static boolean stabEOL_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabEOL_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = stabParenthesesMany(builder_, level_ + 1);
    result_ = result_ && stabEOL_2_1(builder_, level_ + 1);
    result_ = result_ && stabOperatorEOL(builder_, level_ + 1);
    result_ = result_ && stabMaybeExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (whenOperator expression)?
  private static boolean stabEOL_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabEOL_2_1")) return false;
    stabEOL_2_1_0(builder_, level_ + 1);
    return true;
  }

  // whenOperator expression
  private static boolean stabEOL_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabEOL_2_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, WHENOPERATOR);
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // 'expr' | ''
  public static boolean stabMaybeExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabMaybeExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<stab maybe expression>");
    result_ = consumeToken(builder_, "expr");
    if (!result_) result_ = consumeToken(builder_, "");
    exit_section_(builder_, level_, marker_, STAB_MAYBE_EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // stabOperator EOL?
  public static boolean stabOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabOperatorEOL")) return false;
    if (!nextTokenIs(builder_, STABOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, STABOPERATOR);
    result_ = result_ && stabOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, STAB_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean stabOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // emptyParentheses |
  //   (
  //     openParenthesis
  //     (callArgumentsNoParenthesesKeyword | callArgumentsNoParenthesesMany)
  //     closeParenthesis
  //   )
  public static boolean stabParenthesesMany(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabParenthesesMany")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<stab parentheses many>");
    result_ = emptyParentheses(builder_, level_ + 1);
    if (!result_) result_ = stabParenthesesMany_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, STAB_PARENTHESES_MANY, result_, false, null);
    return result_;
  }

  // openParenthesis
  //     (callArgumentsNoParenthesesKeyword | callArgumentsNoParenthesesMany)
  //     closeParenthesis
  private static boolean stabParenthesesMany_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabParenthesesMany_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = openParenthesis(builder_, level_ + 1);
    result_ = result_ && stabParenthesesMany_1_1(builder_, level_ + 1);
    result_ = result_ && closeParenthesis(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // callArgumentsNoParenthesesKeyword | callArgumentsNoParenthesesMany
  private static boolean stabParenthesesMany_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabParenthesesMany_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = callArgumentsNoParenthesesKeyword(builder_, level_ + 1);
    if (!result_) result_ = callArgumentsNoParenthesesMany(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '%' EOL?
  public static boolean structOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "structOperator")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<struct operator>");
    result_ = consumeToken(builder_, "%");
    result_ = result_ && structOperator_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, STRUCT_OPERATOR, result_, false, null);
    return result_;
  }

  // EOL?
  private static boolean structOperator_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "structOperator_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // openCurly ('}' | (containerArguments closeCurly))
  public static boolean tuple(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tuple")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<tuple>");
    result_ = openCurly(builder_, level_ + 1);
    result_ = result_ && tuple_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, TUPLE, result_, false, null);
    return result_;
  }

  // '}' | (containerArguments closeCurly)
  private static boolean tuple_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tuple_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "}");
    if (!result_) result_ = tuple_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // containerArguments closeCurly
  private static boolean tuple_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tuple_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = containerArguments(builder_, level_ + 1);
    result_ = result_ && closeCurly(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // twoOperator EOL?
  public static boolean twoOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "twoOperatorEOL")) return false;
    if (!nextTokenIs(builder_, TWOOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TWOOPERATOR);
    result_ = result_ && twoOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, TWO_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean twoOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "twoOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // twoOperator EOL?
  public static boolean typeOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeOperatorEOL")) return false;
    if (!nextTokenIs(builder_, TWOOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TWOOPERATOR);
    result_ = result_ && typeOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, TYPE_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean typeOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // (unaryOperator | dualOperator) EOL?
  public static boolean unaryOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryOperatorEOL")) return false;
    if (!nextTokenIs(builder_, "<unary operator eol>", DUALOPERATOR, UNARYOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<unary operator eol>");
    result_ = unaryOperatorEOL_0(builder_, level_ + 1);
    result_ = result_ && unaryOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, UNARY_OPERATOR_EOL, result_, false, null);
    return result_;
  }

  // unaryOperator | dualOperator
  private static boolean unaryOperatorEOL_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryOperatorEOL_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, UNARYOPERATOR);
    if (!result_) result_ = consumeToken(builder_, DUALOPERATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL?
  private static boolean unaryOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // ((emptyParentheses | matchedExpression | unmatchedExpression) operatorExpression) |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L130-L132
  //   ((unaryOperatorEOL | atOperatorEOL | captureOperatorEOL) expression) |
  //   // https://github.com/elixir-lang/elixir/blob/a47751f4de72322118e35e1cfd3aa6b4d9c27c13/lib/elixir/src/elixir_parser.yrl#L133
  //   blockExpression
  public static boolean unmatchedExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unmatchedExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<unmatched expression>");
    result_ = unmatchedExpression_0(builder_, level_ + 1);
    if (!result_) result_ = unmatchedExpression_1(builder_, level_ + 1);
    if (!result_) result_ = blockExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, UNMATCHED_EXPRESSION, result_, false, null);
    return result_;
  }

  // (emptyParentheses | matchedExpression | unmatchedExpression) operatorExpression
  private static boolean unmatchedExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unmatchedExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unmatchedExpression_0_0(builder_, level_ + 1);
    result_ = result_ && operatorExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // emptyParentheses | matchedExpression | unmatchedExpression
  private static boolean unmatchedExpression_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unmatchedExpression_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = emptyParentheses(builder_, level_ + 1);
    if (!result_) result_ = matchedExpression(builder_, level_ + 1);
    if (!result_) result_ = unmatchedExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (unaryOperatorEOL | atOperatorEOL | captureOperatorEOL) expression
  private static boolean unmatchedExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unmatchedExpression_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unmatchedExpression_1_0(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // unaryOperatorEOL | atOperatorEOL | captureOperatorEOL
  private static boolean unmatchedExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unmatchedExpression_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unaryOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = atOperatorEOL(builder_, level_ + 1);
    if (!result_) result_ = captureOperatorEOL(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // whenOperator EOL?
  public static boolean whenOperatorEOL(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "whenOperatorEOL")) return false;
    if (!nextTokenIs(builder_, WHENOPERATOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, WHENOPERATOR);
    result_ = result_ && whenOperatorEOL_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, WHEN_OPERATOR_EOL, result_);
    return result_;
  }

  // EOL?
  private static boolean whenOperatorEOL_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "whenOperatorEOL_1")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

}
