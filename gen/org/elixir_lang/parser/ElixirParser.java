// This is a generated file. Not intended for manual editing.
package org.elixir_lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.elixir_lang.psi.ElixirTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ElixirParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("org.elixir_lang.parser.ElixirParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == ADDITION_OPERATION) {
      result_ = expression(builder_, 0, 13);
    }
    else if (root_ == AND_OPERATION) {
      result_ = expression(builder_, 0, 8);
    }
    else if (root_ == ARROW_OPERATION) {
      result_ = expression(builder_, 0, 11);
    }
    else if (root_ == ASSOCIATION_OPERATION) {
      result_ = expression(builder_, 0, 5);
    }
    else if (root_ == AT_OPERATION) {
      result_ = atOperation(builder_, 0);
    }
    else if (root_ == ATOM) {
      result_ = atom(builder_, 0);
    }
    else if (root_ == CAPTURE_OPERATION) {
      result_ = captureOperation(builder_, 0);
    }
    else if (root_ == CHAR_LIST) {
      result_ = charList(builder_, 0);
    }
    else if (root_ == CHAR_LIST_HEREDOC) {
      result_ = charListHeredoc(builder_, 0);
    }
    else if (root_ == COMPARISON_OPERATION) {
      result_ = expression(builder_, 0, 9);
    }
    else if (root_ == EXPRESSION) {
      result_ = expression(builder_, 0, -1);
    }
    else if (root_ == HAT_OPERATION) {
      result_ = expression(builder_, 0, 15);
    }
    else if (root_ == IN_MATCH_OPERATION) {
      result_ = expression(builder_, 0, 1);
    }
    else if (root_ == INTERPOLATION) {
      result_ = interpolation(builder_, 0);
    }
    else if (root_ == KEYWORD_IDENTIFIER) {
      result_ = keywordIdentifier(builder_, 0);
    }
    else if (root_ == MATCH_OPERATION) {
      result_ = expression(builder_, 0, 6);
    }
    else if (root_ == MULTIPLICATION_OPERATION) {
      result_ = expression(builder_, 0, 14);
    }
    else if (root_ == OR_OPERATION) {
      result_ = expression(builder_, 0, 7);
    }
    else if (root_ == PIPE_OPERATION) {
      result_ = expression(builder_, 0, 4);
    }
    else if (root_ == RELATIONAL_OPERATION) {
      result_ = expression(builder_, 0, 10);
    }
    else if (root_ == SIGIL) {
      result_ = sigil(builder_, 0);
    }
    else if (root_ == STAB_OPERATION) {
      result_ = expression(builder_, 0, -1);
    }
    else if (root_ == STRING) {
      result_ = string(builder_, 0);
    }
    else if (root_ == STRING_HEREDOC) {
      result_ = stringHeredoc(builder_, 0);
    }
    else if (root_ == TWO_OPERATION) {
      result_ = expression(builder_, 0, 12);
    }
    else if (root_ == TYPE_OPERATION) {
      result_ = expression(builder_, 0, 3);
    }
    else if (root_ == UNARY_OPERATION) {
      result_ = unaryOperation(builder_, 0);
    }
    else if (root_ == VALUE) {
      result_ = value(builder_, 0);
    }
    else if (root_ == WHEN_OPERATION) {
      result_ = expression(builder_, 0, 2);
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

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ADDITION_OPERATION, AND_OPERATION, ARROW_OPERATION, ASSOCIATION_OPERATION,
      ATOM, AT_OPERATION, CAPTURE_OPERATION, COMPARISON_OPERATION,
      EXPRESSION, HAT_OPERATION, IN_MATCH_OPERATION, KEYWORD_IDENTIFIER,
      MATCH_OPERATION, MULTIPLICATION_OPERATION, OR_OPERATION, PIPE_OPERATION,
      RELATIONAL_OPERATION, STAB_OPERATION, TWO_OPERATION, TYPE_OPERATION,
      UNARY_OPERATION, VALUE, WHEN_OPERATION),
  };

  /* ********************************************************** */
  // COLON (ATOM_FRAGMENT | quote)
  public static boolean atom(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "atom")) return false;
    if (!nextTokenIs(builder_, COLON)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && atom_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, ATOM, result_);
    return result_;
  }

  // ATOM_FRAGMENT | quote
  private static boolean atom_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "atom_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ATOM_FRAGMENT);
    if (!result_) result_ = quote(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
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
  // EOL* (expressionList EOL*)?
  static boolean elixirFile(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = elixirFile_0(builder_, level_ + 1);
    result_ = result_ && elixirFile_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean elixirFile_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "elixirFile_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // (expressionList EOL*)?
  private static boolean elixirFile_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile_1")) return false;
    elixirFile_1_0(builder_, level_ + 1);
    return true;
  }

  // expressionList EOL*
  private static boolean elixirFile_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expressionList(builder_, level_ + 1);
    result_ = result_ && elixirFile_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean elixirFile_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elixirFile_1_0_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "elixirFile_1_0_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // expression (EOL+ expression)*
  static boolean expressionList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expression(builder_, level_ + 1, -1);
    result_ = result_ && expressionList_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (EOL+ expression)*
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

  // EOL+ expression
  private static boolean expressionList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expressionList_1_0_0(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL+
  private static boolean expressionList_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionList_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EOL);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!consumeToken(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "expressionList_1_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
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
  //                                      REGEX_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedHeredocRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_HEREDOC_TERMINATOR);
    result_ = result_ && interpolatedHeredocRegex_6(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedHeredocRegex_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocRegex_6")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedHeredocRegex_6", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                                      interpolatedSigilBody
  //                                      SIGIL_HEREDOC_PROMOTER SIGIL_MODIFIER*
  static boolean interpolatedHeredocSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_HEREDOC_PROMOTER);
    result_ = result_ && interpolatedHeredocSigil_6(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedHeredocSigil_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocSigil_6")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedHeredocSigil_6", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  //                                      WORDS_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedHeredocWords(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocWords")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    result_ = result_ && interpolatedWordsBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, WORDS_HEREDOC_TERMINATOR);
    result_ = result_ && interpolatedHeredocWords_6(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedHeredocWords_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedHeredocWords_6")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedHeredocWords_6", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE INTERPOLATING_REGEX_SIGIL_NAME REGEX_PROMOTER interpolatedRegexBody REGEX_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_REGEX_SIGIL_NAME, REGEX_PROMOTER);
    result_ = result_ && interpolatedRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_TERMINATOR);
    result_ = result_ && interpolatedRegex_5(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedRegex_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedRegex_5")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedRegex_5", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  // TILDE INTERPOLATING_SIGIL_NAME SIGIL_PROMOTER interpolatedSigilBody SIGIL_TERMINATOR SIGIL_MODIFIER*
  static boolean interpolatedSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, INTERPOLATING_SIGIL_NAME, SIGIL_PROMOTER);
    result_ = result_ && interpolatedSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_TERMINATOR);
    result_ = result_ && interpolatedSigil_5(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean interpolatedSigil_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interpolatedSigil_5")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "interpolatedSigil_5", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  //                                 REGEX_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean literalHeredocRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_REGEX_SIGIL_NAME, REGEX_HEREDOC_PROMOTER, EOL);
    result_ = result_ && literalRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_HEREDOC_TERMINATOR);
    result_ = result_ && literalHeredocRegex_6(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean literalHeredocRegex_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocRegex_6")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "literalHeredocRegex_6", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME SIGIL_HEREDOC_PROMOTER EOL
  //                                 literalSigilBody
  //                                 SIGIL_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean literalHeredocSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, SIGIL_HEREDOC_PROMOTER, EOL);
    result_ = result_ && literalSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_HEREDOC_TERMINATOR);
    result_ = result_ && literalHeredocSigil_6(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean literalHeredocSigil_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocSigil_6")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "literalHeredocSigil_6", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  //                                 WORDS_HEREDOC_TERMINATOR SIGIL_MODIFIER*
  static boolean literalHeredocWords(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocWords")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_WORDS_SIGIL_NAME, WORDS_HEREDOC_PROMOTER, EOL);
    result_ = result_ && literalWordsBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, WORDS_HEREDOC_TERMINATOR);
    result_ = result_ && literalHeredocWords_6(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean literalHeredocWords_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalHeredocWords_6")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "literalHeredocWords_6", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TILDE LITERAL_SIGIL_NAME REGEX_PROMOTER literalRegexBody REGEX_TERMINATOR SIGIL_MODIFIER*
  static boolean literalRegex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalRegex")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, REGEX_PROMOTER);
    result_ = result_ && literalRegexBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, REGEX_TERMINATOR);
    result_ = result_ && literalRegex_5(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean literalRegex_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalRegex_5")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "literalRegex_5", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  // TILDE LITERAL_SIGIL_NAME SIGIL_PROMOTER literalSigilBody SIGIL_TERMINATOR SIGIL_MODIFIER*
  static boolean literalSigil(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalSigil")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, SIGIL_PROMOTER);
    result_ = result_ && literalSigilBody(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SIGIL_TERMINATOR);
    result_ = result_ && literalSigil_5(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean literalSigil_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalSigil_5")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "literalSigil_5", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  // TILDE LITERAL_SIGIL_NAME WORDS_PROMOTER literal WORDS_TERMINATOR SIGIL_MODIFIER*
  static boolean literalWords(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalWords")) return false;
    if (!nextTokenIs(builder_, TILDE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TILDE, LITERAL_SIGIL_NAME, WORDS_PROMOTER, LITERAL, WORDS_TERMINATOR);
    result_ = result_ && literalWords_5(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SIGIL_MODIFIER*
  private static boolean literalWords_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalWords_5")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, SIGIL_MODIFIER)) break;
      if (!empty_element_parsed_guard_(builder_, "literalWords_5", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
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
  // charList | string
  static boolean quote(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quote")) return false;
    if (!nextTokenIs(builder_, "", CHAR_LIST_PROMOTER, STRING_PROMOTER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = charList(builder_, level_ + 1);
    if (!result_) result_ = string(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
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

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: BINARY(stabOperation)
  // 1: PREFIX(captureOperation)
  // 2: BINARY(inMatchOperation)
  // 3: BINARY(whenOperation)
  // 4: BINARY(typeOperation)
  // 5: BINARY(pipeOperation)
  // 6: BINARY(associationOperation)
  // 7: BINARY(matchOperation)
  // 8: BINARY(orOperation)
  // 9: BINARY(andOperation)
  // 10: BINARY(comparisonOperation)
  // 11: BINARY(relationalOperation)
  // 12: BINARY(arrowOperation)
  // 13: BINARY(twoOperation)
  // 14: BINARY(additionOperation)
  // 15: BINARY(multiplicationOperation)
  // 16: BINARY(hatOperation)
  // 17: PREFIX(unaryOperation)
  // 18: PREFIX(atOperation)
  // 19: ATOM(keywordIdentifier)
  // 20: ATOM(value)
  public static boolean expression(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    addVariant(builder_, "<expression>");
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression>");
    result_ = captureOperation(builder_, level_ + 1);
    if (!result_) result_ = unaryOperation(builder_, level_ + 1);
    if (!result_) result_ = atOperation(builder_, level_ + 1);
    if (!result_) result_ = keywordIdentifier(builder_, level_ + 1);
    if (!result_) result_ = value(builder_, level_ + 1);
    pinned_ = result_;
    result_ = result_ && expression_0(builder_, level_ + 1, priority_);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  public static boolean expression_0(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expression_0")) return false;
    boolean result_ = true;
    while (true) {
      Marker left_marker_ = (Marker) builder_.getLatestDoneMarker();
      if (!invalid_left_marker_guard_(builder_, left_marker_, "expression_0")) return false;
      Marker marker_ = builder_.mark();
      if (priority_ < 0 && stabOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, -1));
        marker_.drop();
        left_marker_.precede().done(STAB_OPERATION);
      }
      else if (priority_ < 2 && inMatchOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 2));
        marker_.drop();
        left_marker_.precede().done(IN_MATCH_OPERATION);
      }
      else if (priority_ < 3 && whenOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 2));
        marker_.drop();
        left_marker_.precede().done(WHEN_OPERATION);
      }
      else if (priority_ < 4 && typeOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 3));
        marker_.drop();
        left_marker_.precede().done(TYPE_OPERATION);
      }
      else if (priority_ < 5 && pipeOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 4));
        marker_.drop();
        left_marker_.precede().done(PIPE_OPERATION);
      }
      else if (priority_ < 6 && associationOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 5));
        marker_.drop();
        left_marker_.precede().done(ASSOCIATION_OPERATION);
      }
      else if (priority_ < 7 && matchOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 6));
        marker_.drop();
        left_marker_.precede().done(MATCH_OPERATION);
      }
      else if (priority_ < 8 && orOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 8));
        marker_.drop();
        left_marker_.precede().done(OR_OPERATION);
      }
      else if (priority_ < 9 && andOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 9));
        marker_.drop();
        left_marker_.precede().done(AND_OPERATION);
      }
      else if (priority_ < 10 && comparisonOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 10));
        marker_.drop();
        left_marker_.precede().done(COMPARISON_OPERATION);
      }
      else if (priority_ < 11 && relationalOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 11));
        marker_.drop();
        left_marker_.precede().done(RELATIONAL_OPERATION);
      }
      else if (priority_ < 12 && arrowOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 12));
        marker_.drop();
        left_marker_.precede().done(ARROW_OPERATION);
      }
      else if (priority_ < 13 && twoOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 12));
        marker_.drop();
        left_marker_.precede().done(TWO_OPERATION);
      }
      else if (priority_ < 14 && additionOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 14));
        marker_.drop();
        left_marker_.precede().done(ADDITION_OPERATION);
      }
      else if (priority_ < 15 && multiplicationOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 15));
        marker_.drop();
        left_marker_.precede().done(MULTIPLICATION_OPERATION);
      }
      else if (priority_ < 16 && hatOperation_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expression(builder_, level_, 16));
        marker_.drop();
        left_marker_.precede().done(HAT_OPERATION);
      }
      else {
        exit_section_(builder_, marker_, null, false);
        break;
      }
    }
    return result_;
  }

  // EOL* STAB_OPERATOR EOL*
  private static boolean stabOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = stabOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, STAB_OPERATOR);
    result_ = result_ && stabOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean stabOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "stabOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean stabOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stabOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "stabOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  public static boolean captureOperation(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "captureOperation")) return false;
    if (!nextTokenIsFast(builder_, CAPTURE_OPERATOR)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = captureOperation_0(builder_, level_ + 1);
    pinned_ = result_;
    result_ = pinned_ && expression(builder_, level_, 1);
    exit_section_(builder_, level_, marker_, CAPTURE_OPERATION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // CAPTURE_OPERATOR EOL*
  private static boolean captureOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "captureOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, CAPTURE_OPERATOR);
    result_ = result_ && captureOperation_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean captureOperation_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "captureOperation_0_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "captureOperation_0_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* IN_MATCH_OPERATOR EOL*
  private static boolean inMatchOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inMatchOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = inMatchOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IN_MATCH_OPERATOR);
    result_ = result_ && inMatchOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean inMatchOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inMatchOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "inMatchOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean inMatchOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inMatchOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "inMatchOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* WHEN_OPERATOR EOL*
  private static boolean whenOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "whenOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = whenOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, WHEN_OPERATOR);
    result_ = result_ && whenOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean whenOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "whenOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "whenOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean whenOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "whenOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "whenOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* TYPE_OPERATOR EOL*
  private static boolean typeOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = typeOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TYPE_OPERATOR);
    result_ = result_ && typeOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean typeOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "typeOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean typeOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "typeOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* PIPE_OPERATOR EOL*
  private static boolean pipeOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pipeOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = pipeOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, PIPE_OPERATOR);
    result_ = result_ && pipeOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean pipeOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pipeOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "pipeOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean pipeOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pipeOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "pipeOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* ASSOCIATION_OPERATOR EOL*
  private static boolean associationOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "associationOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = associationOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ASSOCIATION_OPERATOR);
    result_ = result_ && associationOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean associationOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "associationOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "associationOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean associationOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "associationOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "associationOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* MATCH_OPERATOR EOL*
  private static boolean matchOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = matchOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, MATCH_OPERATOR);
    result_ = result_ && matchOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean matchOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "matchOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean matchOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "matchOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* OR_OPERATOR EOL*
  private static boolean orOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = orOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OR_OPERATOR);
    result_ = result_ && orOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean orOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "orOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean orOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "orOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* AND_OPERATOR EOL*
  private static boolean andOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = andOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, AND_OPERATOR);
    result_ = result_ && andOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean andOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "andOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean andOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "andOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* COMPARISON_OPERATOR EOL*
  private static boolean comparisonOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comparisonOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, COMPARISON_OPERATOR);
    result_ = result_ && comparisonOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean comparisonOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "comparisonOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean comparisonOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "comparisonOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* RELATIONAL_OPERATOR EOL*
  private static boolean relationalOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "relationalOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = relationalOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RELATIONAL_OPERATOR);
    result_ = result_ && relationalOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean relationalOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "relationalOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "relationalOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean relationalOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "relationalOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "relationalOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* ARROW_OPERATOR EOL*
  private static boolean arrowOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrowOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = arrowOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ARROW_OPERATOR);
    result_ = result_ && arrowOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean arrowOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrowOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "arrowOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean arrowOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrowOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "arrowOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* TWO_OPERATOR EOL*
  private static boolean twoOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "twoOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = twoOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TWO_OPERATOR);
    result_ = result_ && twoOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean twoOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "twoOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "twoOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean twoOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "twoOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "twoOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // DUAL_OPERATOR EOL*
  private static boolean additionOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "additionOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, DUAL_OPERATOR);
    result_ = result_ && additionOperation_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean additionOperation_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "additionOperation_0_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "additionOperation_0_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* MULTIPLICATION_OPERATOR EOL*
  private static boolean multiplicationOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicationOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = multiplicationOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, MULTIPLICATION_OPERATOR);
    result_ = result_ && multiplicationOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean multiplicationOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicationOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "multiplicationOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean multiplicationOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicationOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "multiplicationOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL* HAT_OPERATOR EOL*
  private static boolean hatOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "hatOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = hatOperation_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, HAT_OPERATOR);
    result_ = result_ && hatOperation_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean hatOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "hatOperation_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "hatOperation_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // EOL*
  private static boolean hatOperation_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "hatOperation_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "hatOperation_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  public static boolean unaryOperation(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryOperation")) return false;
    if (!nextTokenIsFast(builder_, DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = unaryOperation_0(builder_, level_ + 1);
    pinned_ = result_;
    result_ = pinned_ && expression(builder_, level_, 17);
    exit_section_(builder_, level_, marker_, UNARY_OPERATION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (DUAL_OPERATOR | UNARY_OPERATOR) EOL*
  private static boolean unaryOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unaryOperation_0_0(builder_, level_ + 1);
    result_ = result_ && unaryOperation_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // DUAL_OPERATOR | UNARY_OPERATOR
  private static boolean unaryOperation_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryOperation_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, DUAL_OPERATOR);
    if (!result_) result_ = consumeTokenSmart(builder_, UNARY_OPERATOR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean unaryOperation_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryOperation_0_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "unaryOperation_0_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  public static boolean atOperation(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "atOperation")) return false;
    if (!nextTokenIsFast(builder_, AT_OPERATOR)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = atOperation_0(builder_, level_ + 1);
    pinned_ = result_;
    result_ = pinned_ && expression(builder_, level_, 18);
    exit_section_(builder_, level_, marker_, AT_OPERATION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // AT_OPERATOR EOL*
  private static boolean atOperation_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "atOperation_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, AT_OPERATOR);
    result_ = result_ && atOperation_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOL*
  private static boolean atOperation_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "atOperation_0_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeTokenSmart(builder_, EOL)) break;
      if (!empty_element_parsed_guard_(builder_, "atOperation_0_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ALIAS COLON
  public static boolean keywordIdentifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keywordIdentifier")) return false;
    if (!nextTokenIsFast(builder_, ALIAS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, ALIAS, COLON);
    exit_section_(builder_, marker_, KEYWORD_IDENTIFIER, result_);
    return result_;
  }

  // ALIAS | atom | CHAR_TOKEN | NUMBER | charListHeredoc | IDENTIFIER | quote | sigil | stringHeredoc
  public static boolean value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "value")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<value>");
    result_ = consumeTokenSmart(builder_, ALIAS);
    if (!result_) result_ = atom(builder_, level_ + 1);
    if (!result_) result_ = consumeTokenSmart(builder_, CHAR_TOKEN);
    if (!result_) result_ = consumeTokenSmart(builder_, NUMBER);
    if (!result_) result_ = charListHeredoc(builder_, level_ + 1);
    if (!result_) result_ = consumeTokenSmart(builder_, IDENTIFIER);
    if (!result_) result_ = quote(builder_, level_ + 1);
    if (!result_) result_ = sigil(builder_, level_ + 1);
    if (!result_) result_ = stringHeredoc(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, VALUE, result_, false, null);
    return result_;
  }

}
