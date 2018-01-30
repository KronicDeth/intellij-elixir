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
public class BEAMAssemblyParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == BIT_STRING) {
      r = bitString(b, 0);
    }
    else if (t == FUNCTION_REFERENCE) {
      r = functionReference(b, 0);
    }
    else if (t == LIST) {
      r = list(b, 0);
    }
    else if (t == MAP) {
      r = map(b, 0);
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
    else if (t == STRUCT) {
      r = struct(b, 0);
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
  // OPENING_BIT termList? CLOSING_BIT
  public static boolean bitString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitString")) return false;
    if (!nextTokenIs(b, OPENING_BIT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_BIT);
    r = r && bitString_1(b, l + 1);
    r = r && consumeToken(b, CLOSING_BIT);
    exit_section_(b, m, BIT_STRING, r);
    return r;
  }

  // termList?
  private static boolean bitString_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitString_1")) return false;
    termList(b, l + 1);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, KEY);
    p = r; // pin = 1
    r = r && term(b, l + 1);
    exit_section_(b, l, m, r, p, keywordRecoverWhile_parser_);
    return r || p;
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
  // COMMA | CLOSING_BRACKET | CLOSING_CURLY | CLOSING_PARENTHESIS
  static boolean keywordRecoverUntil(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordRecoverUntil")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, CLOSING_BRACKET);
    if (!r) r = consumeToken(b, CLOSING_CURLY);
    if (!r) r = consumeToken(b, CLOSING_PARENTHESIS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // !keywordRecoverUntil
  static boolean keywordRecoverWhile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywordRecoverWhile")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !keywordRecoverUntil(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OPENING_BRACKET termList? CLOSING_BRACKET
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

  // termList?
  private static boolean list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_1")) return false;
    termList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // MAP_OPERATOR OPENING_CURLY keywordList? CLOSING_CURLY
  public static boolean map(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "map")) return false;
    if (!nextTokenIs(b, MAP_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MAP, null);
    r = consumeTokens(b, 2, MAP_OPERATOR, OPENING_CURLY);
    p = r; // pin = 2
    r = r && report_error_(b, map_2(b, l + 1));
    r = p && consumeToken(b, CLOSING_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // keywordList?
  private static boolean map_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "map_2")) return false;
    keywordList(b, l + 1);
    return true;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OPERATION, "<operation>");
    r = consumeTokens(b, 2, NAME, OPENING_PARENTHESIS);
    p = r; // pin = 2
    r = r && report_error_(b, operation_2(b, l + 1));
    r = p && consumeToken(b, CLOSING_PARENTHESIS) && r;
    exit_section_(b, l, m, r, p, operationRecoverWhile_parser_);
    return r || p;
  }

  // operands?
  private static boolean operation_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operation_2")) return false;
    operands(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // NAME
  static boolean operationRecoverUntil(PsiBuilder b, int l) {
    return consumeToken(b, NAME);
  }

  /* ********************************************************** */
  // !operationRecoverUntil
  static boolean operationRecoverWhile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationRecoverWhile")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !operationRecoverUntil(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // ATOM_KEYWORD | NAME | STRING | SYMBOLIC_OPERATOR
  public static boolean relative(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relative")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RELATIVE, "<relative>");
    r = consumeToken(b, ATOM_KEYWORD);
    if (!r) r = consumeToken(b, NAME);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, SYMBOLIC_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MAP_OPERATOR QUALIFIED_ALIAS OPENING_CURLY keywordList? CLOSING_CURLY
  public static boolean struct(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct")) return false;
    if (!nextTokenIs(b, MAP_OPERATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT, null);
    r = consumeTokens(b, 3, MAP_OPERATOR, QUALIFIED_ALIAS, OPENING_CURLY);
    p = r; // pin = 3
    r = r && report_error_(b, struct_3(b, l + 1));
    r = p && consumeToken(b, CLOSING_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // keywordList?
  private static boolean struct_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_3")) return false;
    keywordList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ATOM | ATOM_KEYWORD | CHARLIST | INTEGER | QUALIFIED_ALIAS | STRING | 
  //          bitString | functionReference | list | map | tuple | struct | typedTerm
  public static boolean term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TERM, "<term>");
    r = consumeToken(b, ATOM);
    if (!r) r = consumeToken(b, ATOM_KEYWORD);
    if (!r) r = consumeToken(b, CHARLIST);
    if (!r) r = consumeToken(b, INTEGER);
    if (!r) r = consumeToken(b, QUALIFIED_ALIAS);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = bitString(b, l + 1);
    if (!r) r = functionReference(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = map(b, l + 1);
    if (!r) r = tuple(b, l + 1);
    if (!r) r = struct(b, l + 1);
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

  final static Parser keywordRecoverWhile_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return keywordRecoverWhile(b, l + 1);
    }
  };
  final static Parser operationRecoverWhile_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return operationRecoverWhile(b, l + 1);
    }
  };
}
