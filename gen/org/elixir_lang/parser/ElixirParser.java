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
    if (root_ == CHAR_LIST) {
      result_ = charList(builder_, 0);
    }
    else if (root_ == CHAR_LIST_HEREDOC) {
      result_ = charListHeredoc(builder_, 0);
    }
    else if (root_ == INTERPOLATION) {
      result_ = interpolation(builder_, 0);
    }
    else if (root_ == SIGIL) {
      result_ = sigil(builder_, 0);
    }
    else if (root_ == STRING) {
      result_ = string(builder_, 0);
    }
    else if (root_ == STRING_HEREDOC) {
      result_ = stringHeredoc(builder_, 0);
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
  // SINGLE_QUOTE
  //              interpolatedCharListBody
  //              SINGLE_QUOTE
  public static boolean charList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "charList")) return false;
    if (!nextTokenIs(builder_, SINGLE_QUOTE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, SINGLE_QUOTE);
    result_ = result_ && interpolatedCharListBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SINGLE_QUOTE);
    exit_section_(builder_, marker_, CHAR_LIST, result_);
    return result_;
  }

  /* ********************************************************** */
  // TRIPLE_SINGLE_QUOTE EOL
  //                     interpolatedCharListBody
  //                     TRIPLE_SINGLE_QUOTE
  public static boolean charListHeredoc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "charListHeredoc")) return false;
    if (!nextTokenIs(builder_, TRIPLE_SINGLE_QUOTE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TRIPLE_SINGLE_QUOTE, EOL);
    result_ = result_ && interpolatedCharListBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TRIPLE_SINGLE_QUOTE);
    exit_section_(builder_, marker_, CHAR_LIST_HEREDOC, result_);
    return result_;
  }

  /* ********************************************************** */
  // expressionList
  static boolean elixirFile(PsiBuilder builder_, int level_) {
    return expressionList(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // NUMBER | charList | charListHeredoc | sigil | string | stringHeredoc
  static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    if (!result_) result_ = charList(builder_, level_ + 1);
    if (!result_) result_ = charListHeredoc(builder_, level_ + 1);
    if (!result_) result_ = sigil(builder_, level_ + 1);
    if (!result_) result_ = string(builder_, level_ + 1);
    if (!result_) result_ = stringHeredoc(builder_, level_ + 1);
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
  // (interpolation | CHAR_LIST_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedCharListBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedCharListBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!interpolatedCharListBody_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedCharListBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // interpolation | CHAR_LIST_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedCharListBody_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedCharListBody_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = interpolation(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, CHAR_LIST_FRAGMENT);
    if (!result_) result_ = consumeToken(builder_, VALID_ESCAPE_SEQUENCE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (interpolation | SIGIL_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedSigilBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedSigilBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!interpolatedSigilBody_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedSigilBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // interpolation | SIGIL_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedSigilBody_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedSigilBody_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = interpolation(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, SIGIL_FRAGMENT);
    if (!result_) result_ = consumeToken(builder_, VALID_ESCAPE_SEQUENCE);
    exit_section_(builder_, marker_, null, result_);
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

  /* ********************************************************** */
  // TILDE SIGIL_INTERPOLATING_NAME TRIPLE_DOUBLE_QUOTES EOL
  //           interpolatedSigilBody
  //           TRIPLE_DOUBLE_QUOTES
  public static boolean sigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, SIGIL_INTERPOLATING_NAME, TRIPLE_DOUBLE_QUOTES, EOL);
    result_ = result_ && interpolatedSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TRIPLE_DOUBLE_QUOTES);
    exit_section_(builder_, marker_, SIGIL, result_);
    return result_;
  }

  /* ********************************************************** */
  // DOUBLE_QUOTES
  //            interpolatedStringBody
  //            DOUBLE_QUOTES
  public static boolean string(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string")) return false;
    if (!nextTokenIs(builder_, DOUBLE_QUOTES)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DOUBLE_QUOTES);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOUBLE_QUOTES);
    exit_section_(builder_, marker_, STRING, result_);
    return result_;
  }

  /* ********************************************************** */
  // TRIPLE_DOUBLE_QUOTES EOL
  //                   interpolatedStringBody
  //                   TRIPLE_DOUBLE_QUOTES
  public static boolean stringHeredoc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stringHeredoc")) return false;
    if (!nextTokenIs(builder_, TRIPLE_DOUBLE_QUOTES)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TRIPLE_DOUBLE_QUOTES, EOL);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TRIPLE_DOUBLE_QUOTES);
    exit_section_(builder_, marker_, STRING_HEREDOC, result_);
    return result_;
  }

}
