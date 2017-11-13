// This is a generated file. Not intended for manual editing.
package org.elixir_lang.eex;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.elixir_lang.eex.psi.Types.*;
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
    if (t == COMMENT_TAG) {
      r = commentTag(b, 0);
    }
    else if (t == EQUALS_TAG) {
      r = equalsTag(b, 0);
    }
    else if (t == NORMAL_TAG) {
      r = normalTag(b, 0);
    }
    else if (t == QUOTATION_TAG) {
      r = quotationTag(b, 0);
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
  // OPENING_COMMENT COMMENT? CLOSING
  public static boolean commentTag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commentTag")) return false;
    if (!nextTokenIs(b, OPENING_COMMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_COMMENT);
    r = r && commentTag_1(b, l + 1);
    r = r && consumeToken(b, CLOSING);
    exit_section_(b, m, COMMENT_TAG, r);
    return r;
  }

  // COMMENT?
  private static boolean commentTag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commentTag_1")) return false;
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
  // OPENING_EQUALS ELIXIR? CLOSING
  public static boolean equalsTag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalsTag")) return false;
    if (!nextTokenIs(b, OPENING_EQUALS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_EQUALS);
    r = r && equalsTag_1(b, l + 1);
    r = r && consumeToken(b, CLOSING);
    exit_section_(b, m, EQUALS_TAG, r);
    return r;
  }

  // ELIXIR?
  private static boolean equalsTag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalsTag_1")) return false;
    consumeToken(b, ELIXIR);
    return true;
  }

  /* ********************************************************** */
  // OPENING ELIXIR? CLOSING
  public static boolean normalTag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "normalTag")) return false;
    if (!nextTokenIs(b, OPENING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING);
    r = r && normalTag_1(b, l + 1);
    r = r && consumeToken(b, CLOSING);
    exit_section_(b, m, NORMAL_TAG, r);
    return r;
  }

  // ELIXIR?
  private static boolean normalTag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "normalTag_1")) return false;
    consumeToken(b, ELIXIR);
    return true;
  }

  /* ********************************************************** */
  // OPENING_QUOTATION QUOTATION? CLOSING
  public static boolean quotationTag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quotationTag")) return false;
    if (!nextTokenIs(b, OPENING_QUOTATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPENING_QUOTATION);
    r = r && quotationTag_1(b, l + 1);
    r = r && consumeToken(b, CLOSING);
    exit_section_(b, m, QUOTATION_TAG, r);
    return r;
  }

  // QUOTATION?
  private static boolean quotationTag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quotationTag_1")) return false;
    consumeToken(b, QUOTATION);
    return true;
  }

  /* ********************************************************** */
  // normalTag | commentTag | equalsTag | quotationTag
  static boolean tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = normalTag(b, l + 1);
    if (!r) r = commentTag(b, l + 1);
    if (!r) r = equalsTag(b, l + 1);
    if (!r) r = quotationTag(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // tag (DATA? tag)*
  static boolean tags(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags")) return false;
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
