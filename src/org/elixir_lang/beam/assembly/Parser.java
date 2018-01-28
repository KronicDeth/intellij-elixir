// This is a generated file. Not intended for manual editing.
package org.elixir_lang.beam.assembly;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.elixir_lang.beam.assembly.psi.Types.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Parser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == FUNCTION_REFERENCE) {
      r = functionReference(b, 0);
    }
    else if (t == LIST) {
      r = list(b, 0);
    }
    else if (t == OPERANDS) {
      r = operands(b, 0);
    }
    else if (t == OPERATION) {
      r = operation(b, 0);
    }
    else if (t == QUALIFIER) {
      r = qualifier(b, 0);
    }
    else if (t == RELATIVE) {
      r = relative(b, 0);
    }
    else if (t == TERM) {
      r = term(b, 0);
    }
    else if (t == TUPLE) {
      r = tuple(b, 0);
    }
    else if (t == VALUES) {
      r = values(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return beamAssemblyFile(b, l + 1);
  }

  /* ********************************************************** */
  // operation*
  static boolean beamAssemblyFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "beamAssemblyFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!operation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "beamAssemblyFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // REFERENCE_OPERATOR qualifier DOT_OPERATOR relative NAME_ARITY_SEPARATOR INTEGER
  public static boolean functionReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionReference")) return false;
    if (!nextTokenIs(b, REFERENCE_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, REFERENCE_OPERATOR);
    r = r && qualifier(b, l + 1);
    r = r && consumeToken(b, DOT_OPERATOR);
    r = r && relative(b, l + 1);
    r = r && consumeTokens(b, 0, NAME_ARITY_SEPARATOR, INTEGER);
    exit_section_(b, m, FUNCTION_REFERENCE, r);
    return r;
  }

  /* ********************************************************** */
  // KEY term
  static boolean keyword(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword")) return false;
    if (!nextTokenIs(b, KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEY);
    r = r && term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keyword (COMMA keyword)*
  static boolean keywordList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordList")) return false;
    if (!nextTokenIs(b, KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyword(b, l + 1);
    r = r && keywordList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA keyword)*
  private static boolean keywordList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!keywordList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keywordList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA keyword
  private static boolean keywordList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && keyword(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // OPENING_BRACKET termList CLOSING_BRACKET
  public static boolean list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list")) return false;
    if (!nextTokenIs(b, OPENING_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_BRACKET);
    r = r && termList(b, l + 1);
    r = r && consumeToken(b, CLOSING_BRACKET);
    exit_section_(b, m, LIST, r);
    return r;
  }

  /* ********************************************************** */
  // keywordList | termList
  public static boolean operands(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operands")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPERANDS, "<operands>");
    r = keywordList(b, l + 1);
    if (!r) r = termList(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NAME OPENING_PARENTHESIS operands? CLOSING_PARENTHESIS
  public static boolean operation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operation")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OPERATION, null);
    r = consumeTokens(b, 2, NAME, OPENING_PARENTHESIS);
    p = r; // pin = 2
    r = r && report_error_(b, operation_2(b, l + 1));
    r = p && consumeToken(b, CLOSING_PARENTHESIS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operands?
  private static boolean operation_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operation_2")) return false;
    operands(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ATOM | QUALIFIED_ALIAS
  public static boolean qualifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualifier")) return false;
    if (!nextTokenIs(b, "<qualifier>", ATOM, QUALIFIED_ALIAS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, QUALIFIER, "<qualifier>");
    r = consumeToken(b, ATOM);
    if (!r) r = consumeToken(b, QUALIFIED_ALIAS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ATOM_KEYWORD | NAME | SYMBOLIC_OPERATOR
  public static boolean relative(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relative")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RELATIVE, "<relative>");
    r = consumeToken(b, ATOM_KEYWORD);
    if (!r) r = consumeToken(b, NAME);
    if (!r) r = consumeToken(b, SYMBOLIC_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ATOM | ATOM_KEYWORD | INTEGER | QUALIFIED_ALIAS | functionReference | list | tuple | typedTerm
  public static boolean term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TERM, "<term>");
    r = consumeToken(b, ATOM);
    if (!r) r = consumeToken(b, ATOM_KEYWORD);
    if (!r) r = consumeToken(b, INTEGER);
    if (!r) r = consumeToken(b, QUALIFIED_ALIAS);
    if (!r) r = functionReference(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = tuple(b, l + 1);
    if (!r) r = typedTerm(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // term (COMMA term)*
  static boolean termList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "termList")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = term(b, l + 1);
    r = r && termList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA term)*
  private static boolean termList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "termList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!termList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "termList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA term
  private static boolean termList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "termList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // OPENING_CURLY termList CLOSING_CURLY
  public static boolean tuple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple")) return false;
    if (!nextTokenIs(b, OPENING_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_CURLY);
    r = r && termList(b, l + 1);
    r = r && consumeToken(b, CLOSING_CURLY);
    exit_section_(b, m, TUPLE, r);
    return r;
  }

  /* ********************************************************** */
  // NAME OPENING_PARENTHESIS values? CLOSING_PARENTHESIS
  static boolean typedTerm(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedTerm")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 2, NAME, OPENING_PARENTHESIS);
    p = r; // pin = 2
    r = r && report_error_(b, typedTerm_2(b, l + 1));
    r = p && consumeToken(b, CLOSING_PARENTHESIS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // values?
  private static boolean typedTerm_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedTerm_2")) return false;
    values(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // keywordList | termList
  public static boolean values(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUES, "<values>");
    r = keywordList(b, l + 1);
    if (!r) r = termList(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
