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
    if (t == ADDITION_OPERATION) {
      r = expression(b, 0, 14);
    }
    else if (t == AND_OPERATION) {
      r = expression(b, 0, 8);
    }
    else if (t == ARROW_OPERATION) {
      r = expression(b, 0, 11);
    }
    else if (t == ASSOCIATION_OPERATION) {
      r = expression(b, 0, 5);
    }
    else if (t == AT_OPERATION) {
      r = atOperation(b, 0);
    }
    else if (t == ATOM) {
      r = atom(b, 0);
    }
    else if (t == CAPTURE_OPERATION) {
      r = captureOperation(b, 0);
    }
    else if (t == CHAR_LIST) {
      r = charList(b, 0);
    }
    else if (t == CHAR_LIST_HEREDOC) {
      r = charListHeredoc(b, 0);
    }
    else if (t == COMPARISON_OPERATION) {
      r = expression(b, 0, 9);
    }
    else if (t == DOT_OPERATION) {
      r = expression(b, 0, 18);
    }
    else if (t == EMPTY_PARENTHESES) {
      r = emptyParentheses(b, 0);
    }
    else if (t == END_OF_EXPRESSION) {
      r = endOfExpression(b, 0);
    }
    else if (t == EXPRESSION) {
      r = expression(b, 0, -1);
    }
    else if (t == HAT_OPERATION) {
      r = expression(b, 0, 16);
    }
    else if (t == IN_MATCH_OPERATION) {
      r = expression(b, 0, 1);
    }
    else if (t == IN_OPERATION) {
      r = expression(b, 0, 12);
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
    else if (t == MATCH_OPERATION) {
      r = expression(b, 0, 6);
    }
    else if (t == MULTIPLICATION_OPERATION) {
      r = expression(b, 0, 15);
    }
    else if (t == OR_OPERATION) {
      r = expression(b, 0, 7);
    }
    else if (t == PIPE_OPERATION) {
      r = expression(b, 0, 4);
    }
    else if (t == RELATIONAL_OPERATION) {
      r = expression(b, 0, 10);
    }
    else if (t == SIGIL) {
      r = sigil(b, 0);
    }
    else if (t == STAB_OPERATION) {
      r = expression(b, 0, -1);
    }
    else if (t == STRING) {
      r = string(b, 0);
    }
    else if (t == STRING_HEREDOC) {
      r = stringHeredoc(b, 0);
    }
    else if (t == TWO_OPERATION) {
      r = expression(b, 0, 13);
    }
    else if (t == TYPE_OPERATION) {
      r = expression(b, 0, 3);
    }
    else if (t == UNARY_OPERATION) {
      r = unaryOperation(b, 0);
    }
    else if (t == VALUE) {
      r = value(b, 0);
    }
    else if (t == WHEN_OPERATION) {
      r = expression(b, 0, 2);
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
    create_token_set_(ATOM, KEYWORD_KEY),
    create_token_set_(LIST, VALUE),
    create_token_set_(ADDITION_OPERATION, AND_OPERATION, ARROW_OPERATION, ASSOCIATION_OPERATION,
      COMPARISON_OPERATION, DOT_OPERATION, HAT_OPERATION, IN_MATCH_OPERATION,
      IN_OPERATION, MATCH_OPERATION, MULTIPLICATION_OPERATION, OR_OPERATION,
      PIPE_OPERATION, RELATIONAL_OPERATION, STAB_OPERATION, TWO_OPERATION,
      TYPE_OPERATION, WHEN_OPERATION),
    create_token_set_(ADDITION_OPERATION, AND_OPERATION, ARROW_OPERATION, ASSOCIATION_OPERATION,
      ATOM, AT_OPERATION, CAPTURE_OPERATION, COMPARISON_OPERATION,
      DOT_OPERATION, EMPTY_PARENTHESES, EXPRESSION, HAT_OPERATION,
      IN_MATCH_OPERATION, IN_OPERATION, KEYWORD_KEY, KEYWORD_PAIR,
      KEYWORD_VALUE, LIST, MATCH_OPERATION, MULTIPLICATION_OPERATION,
      OR_OPERATION, PIPE_OPERATION, RELATIONAL_OPERATION, STAB_OPERATION,
      TWO_OPERATION, TYPE_OPERATION, UNARY_OPERATION, VALUE,
      WHEN_OPERATION),
  };

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
  // EOL* SEMICOLON EOL* | EOL
  public static boolean endOfExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression")) return false;
    if (!nextTokenIs(b, "<end of expression>", EOL, SEMICOLON)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<end of expression>");
    r = endOfExpression_0(b, l + 1);
    if (!r) r = consumeToken(b, EOL);
    exit_section_(b, l, m, END_OF_EXPRESSION, r, false, null);
    return r;
  }

  // EOL* SEMICOLON EOL*
  private static boolean endOfExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = endOfExpression_0_0(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && endOfExpression_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean endOfExpression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "endOfExpression_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean endOfExpression_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endOfExpression_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "endOfExpression_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // expression (endOfExpression+ expression)*
  static boolean expressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1, -1);
    r = r && expressionList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (endOfExpression+ expression)*
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

  // endOfExpression+ expression
  private static boolean expressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expressionList_1_0_0(b, l + 1);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // endOfExpression+
  private static boolean expressionList_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionList_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = endOfExpression(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!endOfExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expressionList_1_0_0", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (interpolation | CHAR_LIST_FRAGMENT | VALID_ESCAPE_SEQUENCE)*
  static boolean interpolatedCharListBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedCharListBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedCharListBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedCharListBody", c)) break;
      c = current_position_(b);
    }
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
  static boolean interpolatedStringBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interpolatedStringBody")) return false;
    int c = current_position_(b);
    while (true) {
      if (!interpolatedStringBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interpolatedStringBody", c)) break;
      c = current_position_(b);
    }
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
  // keywordKey KEYWORD_PAIR_COLON EOL* keywordValue
  public static boolean keywordPair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordPair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<keyword pair>");
    r = keywordKey(b, l + 1);
    r = r && consumeToken(b, KEYWORD_PAIR_COLON);
    r = r && keywordPair_2(b, l + 1);
    r = r && keywordValue(b, l + 1);
    exit_section_(b, l, m, KEYWORD_PAIR, r, false, null);
    return r;
  }

  // EOL*
  private static boolean keywordPair_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordPair_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "keywordPair_2", c)) break;
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
    Marker m = enter_section_(b, l, _COLLAPSE_, null);
    r = containerExpression(b, l + 1);
    exit_section_(b, l, m, KEYWORD_VALUE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OPENING_BRACKET EOL* (keywordPair (COMMA EOL* keywordPair)* COMMA?)? CLOSING_BRACKET
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

  // (keywordPair (COMMA EOL* keywordPair)* COMMA?)?
  private static boolean list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2")) return false;
    list_2_0(b, l + 1);
    return true;
  }

  // keywordPair (COMMA EOL* keywordPair)* COMMA?
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

  // (COMMA EOL* keywordPair)*
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

  // COMMA EOL* keywordPair
  private static boolean list_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && list_2_0_1_0_1(b, l + 1);
    r = r && keywordPair(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean list_2_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "list_2_0_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA?
  private static boolean list_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
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
  // 13: BINARY(inOperation)
  // 14: BINARY(twoOperation)
  // 15: BINARY(additionOperation)
  // 16: BINARY(multiplicationOperation)
  // 17: BINARY(hatOperation)
  // 18: PREFIX(unaryOperation)
  // 19: BINARY(dotOperation)
  // 20: PREFIX(atOperation)
  // 21: ATOM(value)
  // 22: ATOM(emptyParentheses)
  public static boolean expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression")) return false;
    addVariant(b, "<expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = captureOperation(b, l + 1);
    if (!r) r = unaryOperation(b, l + 1);
    if (!r) r = atOperation(b, l + 1);
    if (!r) r = value(b, l + 1);
    if (!r) r = emptyParentheses(b, l + 1);
    p = r;
    r = r && expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && stabOperation_0(b, l + 1)) {
        r = expression(b, l, -1);
        exit_section_(b, l, m, STAB_OPERATION, r, true, null);
      }
      else if (g < 2 && inMatchOperation_0(b, l + 1)) {
        r = expression(b, l, 2);
        exit_section_(b, l, m, IN_MATCH_OPERATION, r, true, null);
      }
      else if (g < 3 && whenOperation_0(b, l + 1)) {
        r = expression(b, l, 2);
        exit_section_(b, l, m, WHEN_OPERATION, r, true, null);
      }
      else if (g < 4 && typeOperation_0(b, l + 1)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, TYPE_OPERATION, r, true, null);
      }
      else if (g < 5 && pipeOperation_0(b, l + 1)) {
        r = expression(b, l, 4);
        exit_section_(b, l, m, PIPE_OPERATION, r, true, null);
      }
      else if (g < 6 && associationOperation_0(b, l + 1)) {
        r = expression(b, l, 5);
        exit_section_(b, l, m, ASSOCIATION_OPERATION, r, true, null);
      }
      else if (g < 7 && matchOperation_0(b, l + 1)) {
        r = expression(b, l, 6);
        exit_section_(b, l, m, MATCH_OPERATION, r, true, null);
      }
      else if (g < 8 && orOperation_0(b, l + 1)) {
        r = expression(b, l, 8);
        exit_section_(b, l, m, OR_OPERATION, r, true, null);
      }
      else if (g < 9 && andOperation_0(b, l + 1)) {
        r = expression(b, l, 9);
        exit_section_(b, l, m, AND_OPERATION, r, true, null);
      }
      else if (g < 10 && comparisonOperation_0(b, l + 1)) {
        r = expression(b, l, 10);
        exit_section_(b, l, m, COMPARISON_OPERATION, r, true, null);
      }
      else if (g < 11 && relationalOperation_0(b, l + 1)) {
        r = expression(b, l, 11);
        exit_section_(b, l, m, RELATIONAL_OPERATION, r, true, null);
      }
      else if (g < 12 && arrowOperation_0(b, l + 1)) {
        r = expression(b, l, 12);
        exit_section_(b, l, m, ARROW_OPERATION, r, true, null);
      }
      else if (g < 13 && inOperation_0(b, l + 1)) {
        r = expression(b, l, 13);
        exit_section_(b, l, m, IN_OPERATION, r, true, null);
      }
      else if (g < 14 && twoOperation_0(b, l + 1)) {
        r = expression(b, l, 13);
        exit_section_(b, l, m, TWO_OPERATION, r, true, null);
      }
      else if (g < 15 && additionOperation_0(b, l + 1)) {
        r = expression(b, l, 15);
        exit_section_(b, l, m, ADDITION_OPERATION, r, true, null);
      }
      else if (g < 16 && multiplicationOperation_0(b, l + 1)) {
        r = expression(b, l, 16);
        exit_section_(b, l, m, MULTIPLICATION_OPERATION, r, true, null);
      }
      else if (g < 17 && hatOperation_0(b, l + 1)) {
        r = expression(b, l, 17);
        exit_section_(b, l, m, HAT_OPERATION, r, true, null);
      }
      else if (g < 19 && dotOperation_0(b, l + 1)) {
        r = expression(b, l, 19);
        exit_section_(b, l, m, DOT_OPERATION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // EOL* STAB_OPERATOR EOL*
  private static boolean stabOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stabOperation_0_0(b, l + 1);
    r = r && consumeToken(b, STAB_OPERATOR);
    r = r && stabOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean stabOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "stabOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean stabOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stabOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "stabOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  public static boolean captureOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "captureOperation")) return false;
    if (!nextTokenIsFast(b, CAPTURE_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = captureOperation_0(b, l + 1);
    p = r;
    r = p && expression(b, l, 1);
    exit_section_(b, l, m, CAPTURE_OPERATION, r, p, null);
    return r || p;
  }

  // CAPTURE_OPERATOR EOL*
  private static boolean captureOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "captureOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, CAPTURE_OPERATOR);
    r = r && captureOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean captureOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "captureOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "captureOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* IN_MATCH_OPERATOR EOL*
  private static boolean inMatchOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inMatchOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = inMatchOperation_0_0(b, l + 1);
    r = r && consumeToken(b, IN_MATCH_OPERATOR);
    r = r && inMatchOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean inMatchOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inMatchOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inMatchOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean inMatchOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inMatchOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inMatchOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* WHEN_OPERATOR EOL*
  private static boolean whenOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whenOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whenOperation_0_0(b, l + 1);
    r = r && consumeToken(b, WHEN_OPERATOR);
    r = r && whenOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean whenOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whenOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "whenOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean whenOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whenOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "whenOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* TYPE_OPERATOR EOL*
  private static boolean typeOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = typeOperation_0_0(b, l + 1);
    r = r && consumeToken(b, TYPE_OPERATOR);
    r = r && typeOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean typeOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "typeOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean typeOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "typeOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* PIPE_OPERATOR EOL*
  private static boolean pipeOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipeOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pipeOperation_0_0(b, l + 1);
    r = r && consumeToken(b, PIPE_OPERATOR);
    r = r && pipeOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean pipeOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipeOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "pipeOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean pipeOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipeOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "pipeOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* ASSOCIATION_OPERATOR EOL*
  private static boolean associationOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associationOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = associationOperation_0_0(b, l + 1);
    r = r && consumeToken(b, ASSOCIATION_OPERATOR);
    r = r && associationOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean associationOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associationOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "associationOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean associationOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "associationOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "associationOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* MATCH_OPERATOR EOL*
  private static boolean matchOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = matchOperation_0_0(b, l + 1);
    r = r && consumeToken(b, MATCH_OPERATOR);
    r = r && matchOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean matchOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean matchOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "matchOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* OR_OPERATOR EOL*
  private static boolean orOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = orOperation_0_0(b, l + 1);
    r = r && consumeToken(b, OR_OPERATOR);
    r = r && orOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean orOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "orOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean orOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "orOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* AND_OPERATOR EOL*
  private static boolean andOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = andOperation_0_0(b, l + 1);
    r = r && consumeToken(b, AND_OPERATOR);
    r = r && andOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean andOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "andOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean andOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "andOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* COMPARISON_OPERATOR EOL*
  private static boolean comparisonOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparisonOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = comparisonOperation_0_0(b, l + 1);
    r = r && consumeToken(b, COMPARISON_OPERATOR);
    r = r && comparisonOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean comparisonOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparisonOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "comparisonOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean comparisonOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparisonOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "comparisonOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* RELATIONAL_OPERATOR EOL*
  private static boolean relationalOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = relationalOperation_0_0(b, l + 1);
    r = r && consumeToken(b, RELATIONAL_OPERATOR);
    r = r && relationalOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean relationalOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "relationalOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean relationalOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "relationalOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* ARROW_OPERATOR EOL*
  private static boolean arrowOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrowOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = arrowOperation_0_0(b, l + 1);
    r = r && consumeToken(b, ARROW_OPERATOR);
    r = r && arrowOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean arrowOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrowOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "arrowOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean arrowOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrowOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "arrowOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* IN_OPERATOR EOL*
  private static boolean inOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = inOperation_0_0(b, l + 1);
    r = r && consumeToken(b, IN_OPERATOR);
    r = r && inOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean inOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean inOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "inOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* TWO_OPERATOR EOL*
  private static boolean twoOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "twoOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = twoOperation_0_0(b, l + 1);
    r = r && consumeToken(b, TWO_OPERATOR);
    r = r && twoOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean twoOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "twoOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "twoOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean twoOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "twoOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "twoOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DUAL_OPERATOR EOL*
  private static boolean additionOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additionOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DUAL_OPERATOR);
    r = r && additionOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean additionOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additionOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "additionOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* MULTIPLICATION_OPERATOR EOL*
  private static boolean multiplicationOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicationOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = multiplicationOperation_0_0(b, l + 1);
    r = r && consumeToken(b, MULTIPLICATION_OPERATOR);
    r = r && multiplicationOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean multiplicationOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicationOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "multiplicationOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean multiplicationOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicationOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "multiplicationOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* HAT_OPERATOR EOL*
  private static boolean hatOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hatOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hatOperation_0_0(b, l + 1);
    r = r && consumeToken(b, HAT_OPERATOR);
    r = r && hatOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean hatOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hatOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "hatOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean hatOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hatOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "hatOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  public static boolean unaryOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryOperation")) return false;
    if (!nextTokenIsFast(b, DUAL_OPERATOR, UNARY_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unaryOperation_0(b, l + 1);
    p = r;
    r = p && expression(b, l, 18);
    exit_section_(b, l, m, UNARY_OPERATION, r, p, null);
    return r || p;
  }

  // (DUAL_OPERATOR | UNARY_OPERATOR) EOL*
  private static boolean unaryOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unaryOperation_0_0(b, l + 1);
    r = r && unaryOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DUAL_OPERATOR | UNARY_OPERATOR
  private static boolean unaryOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryOperation_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DUAL_OPERATOR);
    if (!r) r = consumeTokenSmart(b, UNARY_OPERATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean unaryOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "unaryOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL* DOT_OPERATOR EOL*
  private static boolean dotOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dotOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dotOperation_0_0(b, l + 1);
    r = r && consumeToken(b, DOT_OPERATOR);
    r = r && dotOperation_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean dotOperation_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dotOperation_0_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "dotOperation_0_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // EOL*
  private static boolean dotOperation_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dotOperation_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "dotOperation_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  public static boolean atOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atOperation")) return false;
    if (!nextTokenIsFast(b, AT_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = atOperation_0(b, l + 1);
    p = r;
    r = p && expression(b, l, 20);
    exit_section_(b, l, m, AT_OPERATION, r, p, null);
    return r || p;
  }

  // AT_OPERATOR EOL*
  private static boolean atOperation_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atOperation_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, AT_OPERATOR);
    r = r && atOperation_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL*
  private static boolean atOperation_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "atOperation_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "atOperation_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ALIAS | atom | BIT_STRING_OPERATOR | CHAR_TOKEN | list| NUMBER | charListHeredoc | IDENTIFIER | MAP_OPERATOR | quote | sigil | stringHeredoc | TUPLE_OPERATOR
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<value>");
    r = consumeTokenSmart(b, ALIAS);
    if (!r) r = atom(b, l + 1);
    if (!r) r = consumeTokenSmart(b, BIT_STRING_OPERATOR);
    if (!r) r = consumeTokenSmart(b, CHAR_TOKEN);
    if (!r) r = list(b, l + 1);
    if (!r) r = consumeTokenSmart(b, NUMBER);
    if (!r) r = charListHeredoc(b, l + 1);
    if (!r) r = consumeTokenSmart(b, IDENTIFIER);
    if (!r) r = consumeTokenSmart(b, MAP_OPERATOR);
    if (!r) r = quote(b, l + 1);
    if (!r) r = sigil(b, l + 1);
    if (!r) r = stringHeredoc(b, l + 1);
    if (!r) r = consumeTokenSmart(b, TUPLE_OPERATOR);
    exit_section_(b, l, m, VALUE, r, false, null);
    return r;
  }

  // OPENING_PARENTHESIS EOL* CLOSING_PARENTHESIS
  public static boolean emptyParentheses(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyParentheses")) return false;
    if (!nextTokenIsFast(b, OPENING_PARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, OPENING_PARENTHESIS);
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
      if (!consumeTokenSmart(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "emptyParentheses_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

}
