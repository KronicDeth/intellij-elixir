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
    if (root_ == DOUBLE_QUOTED_STRING) {
      result_ = doubleQuotedString(builder_, 0);
    }
    else if (root_ == INTERPOLATION) {
      result_ = interpolation(builder_, 0);
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
  // DOUBLE_QUOTES (interpolation | STRING_FRAGMENT)* DOUBLE_QUOTES
  public static boolean doubleQuotedString(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doubleQuotedString")) return false;
    if (!nextTokenIs(builder_, DOUBLE_QUOTES)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DOUBLE_QUOTES);
    result_ = result_ && doubleQuotedString_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOUBLE_QUOTES);
    exit_section_(builder_, marker_, DOUBLE_QUOTED_STRING, result_);
    return result_;
  }

  // (interpolation | STRING_FRAGMENT)*
  private static boolean doubleQuotedString_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doubleQuotedString_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!doubleQuotedString_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "doubleQuotedString_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // interpolation | STRING_FRAGMENT
  private static boolean doubleQuotedString_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "doubleQuotedString_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = interpolation(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, STRING_FRAGMENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // expressionList
  static boolean elixirFile(PsiBuilder builder_, int level_) {
    return expressionList(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // NUMBER | string
  static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    if (!result_) result_ = string(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // expression (EOL expression)* EOL?
  static boolean expressionList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && expressionList_1(builder_, level_ + 1);
    result_ = result_ && expressionList_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (EOL expression)*
  private static boolean expressionList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!expressionList_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "expressionList_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL expression
  private static boolean expressionList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EOL);
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL?
  private static boolean expressionList_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList_2")) return false;
    consumeToken(builder_, EOL);
    return true;
  }

  /* ********************************************************** */
  // INTERPOLATION_START expressionList? INTERPOLATION_END
  public static boolean interpolation(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolation")) return false;
    if (!nextTokenIs(builder_, INTERPOLATION_START)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, INTERPOLATION_START);
    result_ = result_ && interpolation_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, INTERPOLATION_END);
    exit_section_(builder_, marker_, INTERPOLATION, result_);
    return result_;
  }

  // expressionList?
  private static boolean interpolation_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolation_1")) return false;
    expressionList(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // doubleQuotedString | SINGLE_QUOTED_STRING
  static boolean string(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string")) return false;
    if (!nextTokenIs(builder_, "", DOUBLE_QUOTES, SINGLE_QUOTED_STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = doubleQuotedString(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, SINGLE_QUOTED_STRING);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

}
