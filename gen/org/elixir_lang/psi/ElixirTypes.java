// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType INTERPOLATED_HEREDOC = new ElixirElementType("INTERPOLATED_HEREDOC");
  IElementType INTERPOLATED_STRING = new ElixirElementType("INTERPOLATED_STRING");
  IElementType INTERPOLATION = new ElixirElementType("INTERPOLATION");
  IElementType STRING = new ElixirElementType("STRING");

  IElementType COMMENT = new ElixirTokenType("COMMENT");
  IElementType DOUBLE_QUOTES = new ElixirTokenType("DOUBLE_QUOTES");
  IElementType EOL = new ElixirTokenType("EOL");
  IElementType INTERPOLATION_END = new ElixirTokenType("INTERPOLATION_END");
  IElementType INTERPOLATION_START = new ElixirTokenType("INTERPOLATION_START");
  IElementType NUMBER = new ElixirTokenType("NUMBER");
  IElementType SINGLE_QUOTE = new ElixirTokenType("SINGLE_QUOTE");
  IElementType STRING_FRAGMENT = new ElixirTokenType("STRING_FRAGMENT");
  IElementType TRIPLE_DOUBLE_QUOTES = new ElixirTokenType("TRIPLE_DOUBLE_QUOTES");
  IElementType VALID_ESCAPE_SEQUENCE = new ElixirTokenType("VALID_ESCAPE_SEQUENCE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == INTERPOLATED_HEREDOC) {
        return new ElixirInterpolatedHeredocImpl(node);
      }
      else if (type == INTERPOLATED_STRING) {
        return new ElixirInterpolatedStringImpl(node);
      }
      else if (type == INTERPOLATION) {
        return new ElixirInterpolationImpl(node);
      }
      else if (type == STRING) {
        return new ElixirStringImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
