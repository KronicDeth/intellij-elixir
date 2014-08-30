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
  // CHAR_LIST_PROMOTER
  //              interpolatedCharListBody
  //              CHAR_LIST_TERMINATOR
  public static boolean charList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "charList")) return false;
    if (!nextTokenIs(builder_, CHAR_LIST_PROMOTER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CHAR_LIST_PROMOTER);
    result_ = result_ && interpolatedCharListBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CHAR_LIST_TERMINATOR);
    exit_section_(builder_, marker_, CHAR_LIST, result_);
    return result_;
  }

  /* ********************************************************** */
  // CHAR_LIST_HEREDOC_PROMOTER EOL
  //                     interpolatedCharListBody
  //                     CHAR_LIST_HEREDOC_TERMINATOR
  public static boolean charListHeredoc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "charListHeredoc")) return false;
    if (!nextTokenIs(builder_, CHAR_LIST_HEREDOC_PROMOTER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, CHAR_LIST_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedCharListBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CHAR_LIST_HEREDOC_TERMINATOR);
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
  // TILDE INTERPOLATING_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER interpolatedCharListBody CHAR_LIST_SIGIL_TERMINATOR
  static boolean interpolatedCharListSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedCharListSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    result_ = result_ && interpolatedCharListBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CHAR_LIST_SIGIL_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_HEREDOC_PROMOTER EOL
  //                                              interpolatedCharListBody
  //                                              CHAR_LIST_SIGIL_HEREDOC_TERMINATOR
  static boolean interpolatedHeredocCharListSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocCharListSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedCharListBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CHAR_LIST_SIGIL_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_HEREDOC_PROMOTER EOL
  //                                      interpolatedRegexBody
  //                                      REGEX_HEREDOC_TERMINATOR
  static boolean interpolatedHeredocRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                                      interpolatedSigilBody
  //                                      SIGIL_HEREDOC_PROMOTER
  static boolean interpolatedHeredocSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_HEREDOC_PROMOTER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_HEREDOC_PROMOTER EOL
  //                                            interpolatedStringBody
  //                                            STRING_SIGIL_HEREDOC_TERMINATOR
  static boolean interpolatedHeredocStringSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocStringSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STRING_SIGIL_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_WORDS_SIGIL_NAME WORDS_HEREDOC_PROMOTER EOL
  //                                      interpolatedWordsBody
  //                                      WORDS_HEREDOC_TERMINATOR
  static boolean interpolatedHeredocWords(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocWords")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedWordsBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, WORDS_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_PROMOTER interpolatedRegexBody REGEX_TERMINATOR
  static boolean interpolatedRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_PROMOTER);
    result_ = result_ && interpolatedRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (interpolation | REGEX_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedRegexBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedRegexBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!interpolatedRegexBody_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedRegexBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // interpolation | REGEX_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedRegexBody_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedRegexBody_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = interpolation(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, REGEX_FRAGMENT);
    if (!result_) result_ = consumeToken(builder_, VALID_ESCAPE_SEQUENCE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_PROMOTER interpolatedSigilBody SIGIL_TERMINATOR
  static boolean interpolatedSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_PROMOTER);
    result_ = result_ && interpolatedSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_TERMINATOR);
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
  // TILDE INTERPOLATING_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER interpolatedStringBody STRING_SIGIL_TERMINATOR
  static boolean interpolatedStringSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedStringSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STRING_SIGIL_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (interpolation | WORDS_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedWordsBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedWordsBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!interpolatedWordsBody_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedWordsBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // interpolation | WORDS_FRAGMENT | VALID_ESCAPE_SEQUENCE
  private static boolean interpolatedWordsBody_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedWordsBody_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = interpolation(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, WORDS_FRAGMENT);
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
  // CHAR_LIST_FRAGMENT*
  static boolean literalCharListBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalCharListBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, CHAR_LIST_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(builder_, "literalCharListBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_CHAR_LIST_SIGIL_NAME CHAR_LIST_SIGIL_PROMOTER literalCharListBody CHAR_LIST_SIGIL_TERMINATOR
  static boolean literalCharListSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalCharListSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_CHAR_LIST_SIGIL_NAME, CHAR_LIST_SIGIL_PROMOTER);
    result_ = result_ && literalCharListBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CHAR_LIST_SIGIL_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE LITERAL_REGEX_SIGIL_NAME REGEX_HEREDOC_PROMOTER EOL
  //                                 literalRegexBody
  //                                 REGEX_HEREDOC_TERMINATOR
  static boolean literalHeredocRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    result_ = result_ && literalRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                                 literalSigilBody
  //                                 SIGIL_HEREDOC_TERMINATOR
  static boolean literalHeredocSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    result_ = result_ && literalSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE LITERAL_STRING_SIGIL_NAME STRING_SIGIL_HEREDOC_PROMOTER EOL
  //                                       literalStringBody
  //                                       STRING_SIGIL_HEREDOC_TERMINATOR
  static boolean literalHeredocStringSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocStringSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_HEREDOC_PROMOTER, EOL);
    result_ = result_ && literalStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STRING_SIGIL_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE LITERAL_WORDS_SIGIL_NAME WORDS_HEREDOC_PROMOTER EOL
  //                                 literalWordsBody
  //                                 WORDS_HEREDOC_TERMINATOR
  static boolean literalHeredocWords(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocWords")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    result_ = result_ && literalWordsBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, WORDS_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME REGEX_PROMOTER literalRegexBody REGEX_TERMINATOR
  static boolean literalRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, REGEX_PROMOTER);
    result_ = result_ && literalRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // REGEX_FRAGMENT*
  static boolean literalRegexBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalRegexBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, REGEX_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(builder_, "literalRegexBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_PROMOTER literalSigilBody SIGIL_TERMINATOR
  static boolean literalSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, SIGIL_PROMOTER);
    result_ = result_ && literalSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // SIGIL_FRAGMENT*
  static boolean literalSigilBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalSigilBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(builder_, "literalSigilBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // STRING_FRAGMENT*
  static boolean literalStringBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalStringBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, STRING_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(builder_, "literalStringBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_STRING_SIGIL_NAME STRING_SIGIL_PROMOTER literalStringBody STRING_SIGIL_TERMINATOR
  static boolean literalStringSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalStringSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_STRING_SIGIL_NAME, STRING_SIGIL_PROMOTER);
    result_ = result_ && literalStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STRING_SIGIL_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME WORDS_PROMOTER literal WORDS_TERMINATOR
  static boolean literalWords(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalWords")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, WORDS_PROMOTER, LITERAL, WORDS_TERMINATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // WORDS_FRAGMENT*
  static boolean literalWordsBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalWordsBody")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, WORDS_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(builder_, "literalWordsBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  public static boolean sigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = interpolatedCharListSigil(builder_, level_ + 1);
    if (!result_) result_ = interpolatedHeredocCharListSigil(builder_, level_ + 1);
    if (!result_) result_ = interpolatedHeredocRegex(builder_, level_ + 1);
    if (!result_) result_ = interpolatedHeredocSigil(builder_, level_ + 1);
    if (!result_) result_ = interpolatedHeredocStringSigil(builder_, level_ + 1);
    if (!result_) result_ = interpolatedHeredocWords(builder_, level_ + 1);
    if (!result_) result_ = interpolatedRegex(builder_, level_ + 1);
    if (!result_) result_ = interpolatedSigil(builder_, level_ + 1);
    if (!result_) result_ = interpolatedStringSigil(builder_, level_ + 1);
    if (!result_) result_ = literalCharListSigil(builder_, level_ + 1);
    if (!result_) result_ = literalHeredocRegex(builder_, level_ + 1);
    if (!result_) result_ = literalHeredocSigil(builder_, level_ + 1);
    if (!result_) result_ = literalHeredocStringSigil(builder_, level_ + 1);
    if (!result_) result_ = literalHeredocWords(builder_, level_ + 1);
    if (!result_) result_ = literalRegex(builder_, level_ + 1);
    if (!result_) result_ = literalSigil(builder_, level_ + 1);
    if (!result_) result_ = literalStringSigil(builder_, level_ + 1);
    if (!result_) result_ = literalWords(builder_, level_ + 1);
    exit_section_(builder_, marker_, SIGIL, result_);
    return result_;
  }

  /* ********************************************************** */
  // STRING_PROMOTER
  //            interpolatedStringBody
  //            STRING_TERMINATOR
  public static boolean string(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string")) return false;
    if (!nextTokenIs(builder_, STRING_PROMOTER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, STRING_PROMOTER);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STRING_TERMINATOR);
    exit_section_(builder_, marker_, STRING, result_);
    return result_;
  }

  /* ********************************************************** */
  // STRING_HEREDOC_PROMOTER EOL
  //                   interpolatedStringBody
  //                   STRING_HEREDOC_TERMINATOR
  public static boolean stringHeredoc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stringHeredoc")) return false;
    if (!nextTokenIs(builder_, STRING_HEREDOC_PROMOTER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, STRING_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedStringBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STRING_HEREDOC_TERMINATOR);
    exit_section_(builder_, marker_, STRING_HEREDOC, result_);
    return result_;
  }

}
