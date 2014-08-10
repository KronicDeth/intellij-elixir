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
    if (root_ == INTERPOLATED_HEREDOC) {
      result_ = interpolatedHeredoc(builder_, 0);
    }
    else if (root_ == INTERPOLATED_STRING) {
      result_ = interpolatedString(builder_, 0);
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
  // expressionList
  static boolean elixirFile(PsiBuilder builder_, int level_) {
    return expressionList(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // NUMBER | interpolatedString | interpolatedHeredoc | STRING
  static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    if (!result_) result_ = interpolatedString(builder_, level_ + 1);
    if (!result_) result_ = interpolatedHeredoc(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, STRING);
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
  // TRIPLE_DOUBLE_QUOTES EOL
  //                         interpolatedStringBody
  //                         TRIPLE_DOUBLE_QUOTES
  public static boolean interpolatedHeredoc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredoc")) return false;
    if (!nextTokenIs(builder_, TRIPLE_DOUBLE_QUOTES)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TRIPLE_DOUBLE_QUOTES, EOL);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TRIPLE_DOUBLE_QUOTES);
    exit_section_(builder_, marker_, INTERPOLATED_HEREDOC, result_);
    return result_;
  }

  /* ********************************************************** */
  // DOUBLE_QUOTES
  //                        interpolatedStringBody
  //                        DOUBLE_QUOTES
  public static boolean interpolatedString(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedString")) return false;
    if (!nextTokenIs(builder_, DOUBLE_QUOTES)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DOUBLE_QUOTES);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOUBLE_QUOTES);
    exit_section_(builder_, marker_, INTERPOLATED_STRING, result_);
    return result_;
  }

  /* ********************************************************** */
  // (interpolation | STRING_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedStringBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedStringBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!interpolatedStringBody_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedStringBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // interpolation | STRING_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedStringBody_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedStringBody_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = interpolation(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, STRING_FRAGMENT);
    if (!result_) result_ = consumeToken(builder_, VALID_ESCAPE_SEQUENCE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
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

}
