// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType DOUBLE_QUOTED_STRING = new ElixirElementType("DOUBLE_QUOTED_STRING");
  IElementType INTERPOLATION = new ElixirElementType("INTERPOLATION");

  IElementType COMMENT = new ElixirTokenType("COMMENT");
  IElementType DOUBLE_QUOTES = new ElixirTokenType("DOUBLE_QUOTES");
  IElementType EOL = new ElixirTokenType("EOL");
  IElementType INTERPOLATION_END = new ElixirTokenType("INTERPOLATION_END");
  IElementType INTERPOLATION_START = new ElixirTokenType("INTERPOLATION_START");
  IElementType NUMBER = new ElixirTokenType("NUMBER");
  IElementType SINGLE_QUOTED_STRING = new ElixirTokenType("SINGLE_QUOTED_STRING");
  IElementType STRING_FRAGMENT = new ElixirTokenType("STRING_FRAGMENT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == DOUBLE_QUOTED_STRING) {
        return new ElixirDoubleQuotedStringImpl(node);
      }
      else if (type == INTERPOLATION) {
        return new ElixirInterpolationImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
