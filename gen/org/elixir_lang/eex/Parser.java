// This is a generated file. Not intended for manual editing.
package org.elixir_lang.eex;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static org.elixir_lang.eex.psi.Types.*;

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
    if (t == TAG) {
      r = tag(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return eexFile(b, l + 1);
  }

  /* ********************************************************** */
  // COMMENT_MARKER COMMENT?
  static boolean commentBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commentBody")) return false;
    if (!nextTokenIs(b, COMMENT_MARKER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMENT_MARKER);
    p = r; // pin = 1
    r = r && commentBody_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // COMMENT?
  private static boolean commentBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commentBody_1")) return false;
    consumeToken(b, COMMENT);
    return true;
  }

  /* ********************************************************** */
  // DATA? (tags DATA?)?
  static boolean eexFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFile")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = eexFile_0(b, l + 1);
    r = r && eexFile_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DATA?
  private static boolean eexFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFile_0")) return false;
    consumeToken(b, DATA);
    return true;
  }

  // (tags DATA?)?
  private static boolean eexFile_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFile_1")) return false;
    eexFile_1_0(b, l + 1);
    return true;
  }

  // tags DATA?
  private static boolean eexFile_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFile_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tags(b, l + 1);
    r = r && eexFile_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DATA?
  private static boolean eexFile_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFile_1_0_1")) return false;
    consumeToken(b, DATA);
    return true;
  }

  /* ********************************************************** */
  // elixirMarker? ELIXIR?
  static boolean elixirBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirBody")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = elixirBody_0(b, l + 1);
    r = r && elixirBody_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // elixirMarker?
  private static boolean elixirBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirBody_0")) return false;
    elixirMarker(b, l + 1);
    return true;
  }

  // ELIXIR?
  private static boolean elixirBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirBody_1")) return false;
    consumeToken(b, ELIXIR);
    return true;
  }

  /* ********************************************************** */
  // EQUALS_MARKER | FORWARD_SLASH_MARKER | PIPE_MARKER
  static boolean elixirMarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirMarker")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUALS_MARKER);
    if (!r) r = consumeToken(b, FORWARD_SLASH_MARKER);
    if (!r) r = consumeToken(b, PIPE_MARKER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // QUOTATION_MARKER QUOTATION?
  static boolean quotationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quotationBody")) return false;
    if (!nextTokenIs(b, QUOTATION_MARKER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, QUOTATION_MARKER);
    p = r; // pin = 1
    r = r && quotationBody_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // QUOTATION?
  private static boolean quotationBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quotationBody_1")) return false;
    consumeToken(b, QUOTATION);
    return true;
  }

  /* ********************************************************** */
  // OPENING (commentBody | quotationBody | elixirBody) CLOSING
  public static boolean tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag")) return false;
    if (!nextTokenIs(b, OPENING)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TAG, null);
    r = consumeToken(b, OPENING);
    p = r; // pin = 1
    r = r && report_error_(b, tag_1(b, l + 1));
    r = p && consumeToken(b, CLOSING) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // commentBody | quotationBody | elixirBody
  private static boolean tag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = commentBody(b, l + 1);
    if (!r) r = quotationBody(b, l + 1);
    if (!r) r = elixirBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // tag (DATA? tag)*
  static boolean tags(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags")) return false;
    if (!nextTokenIs(b, OPENING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tag(b, l + 1);
    r = r && tags_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DATA? tag)*
  private static boolean tags_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!tags_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tags_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DATA? tag
  private static boolean tags_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tags_1_0_0(b, l + 1);
    r = r && tag(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DATA?
  private static boolean tags_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_1_0_0")) return false;
    consumeToken(b, DATA);
    return true;
  }

}
