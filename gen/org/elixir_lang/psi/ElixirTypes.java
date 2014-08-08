// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;

public interface ElixirTypes {


  IElementType COMMENT = new ElixirTokenType("COMMENT");
  IElementType EOL = new ElixirTokenType("EOL");
  IElementType NUMBER = new ElixirTokenType("NUMBER");
  IElementType SINGLE_QUOTED_STRING = new ElixirTokenType("SINGLE_QUOTED_STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
