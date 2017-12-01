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
  // (DATA | ESCAPED_OPENING | tag)*
  static boolean eexFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!eexFile_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "eexFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DATA | ESCAPED_OPENING | tag
  private static boolean eexFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eexFile_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DATA);
    if (!r) r = consumeToken(b, ESCAPED_OPENING);
    if (!r) r = tag(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // EMPTY_MARKER | EQUALS_MARKER | FORWARD_SLASH_MARKER | PIPE_MARKER
  static boolean elixirMarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elixirMarker")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EMPTY_MARKER);
    if (!r) r = consumeToken(b, EQUALS_MARKER);
    if (!r) r = consumeToken(b, FORWARD_SLASH_MARKER);
    if (!r) r = consumeToken(b, PIPE_MARKER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // OPENING (commentBody | elixirBody) CLOSING
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

  // commentBody | elixirBody
  private static boolean tag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = commentBody(b, l + 1);
    if (!r) r = elixirBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
