// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType CHAR_LIST = new ElixirElementType("CHAR_LIST");
  IElementType CHAR_LIST_HEREDOC = new ElixirElementType("CHAR_LIST_HEREDOC");
  IElementType INTERPOLATION = new ElixirElementType("INTERPOLATION");
  IElementType SIGIL = new ElixirElementType("SIGIL");
  IElementType STRING = new ElixirElementType("STRING");
  IElementType STRING_HEREDOC = new ElixirElementType("STRING_HEREDOC");

  IElementType CHAR_LIST_FRAGMENT = new ElixirTokenType("CHAR_LIST_FRAGMENT");
  IElementType COMMENT = new ElixirTokenType("COMMENT");
  IElementType DOUBLE_QUOTES = new ElixirTokenType("DOUBLE_QUOTES");
  IElementType EOL = new ElixirTokenType("EOL");
  IElementType INTERPOLATION_END = new ElixirTokenType("INTERPOLATION_END");
  IElementType INTERPOLATION_START = new ElixirTokenType("INTERPOLATION_START");
  IElementType NUMBER = new ElixirTokenType("NUMBER");
  IElementType SIGIL_FRAGMENT = new ElixirTokenType("SIGIL_FRAGMENT");
  IElementType SIGIL_INTERPOLATING_NAME = new ElixirTokenType("SIGIL_INTERPOLATING_NAME");
  IElementType SINGLE_QUOTE = new ElixirTokenType("SINGLE_QUOTE");
  IElementType STRING_FRAGMENT = new ElixirTokenType("STRING_FRAGMENT");
  IElementType TILDE = new ElixirTokenType("TILDE");
  IElementType TRIPLE_DOUBLE_QUOTES = new ElixirTokenType("TRIPLE_DOUBLE_QUOTES");
  IElementType TRIPLE_SINGLE_QUOTE = new ElixirTokenType("TRIPLE_SINGLE_QUOTE");
  IElementType VALID_ESCAPE_SEQUENCE = new ElixirTokenType("VALID_ESCAPE_SEQUENCE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == CHAR_LIST) {
        return new ElixirCharListImpl(node);
      }
      else if (type == CHAR_LIST_HEREDOC) {
        return new ElixirCharListHeredocImpl(node);
      }
      else if (type == INTERPOLATION) {
        return new ElixirInterpolationImpl(node);
      }
      else if (type == SIGIL) {
        return new ElixirSigilImpl(node);
      }
      else if (type == STRING) {
        return new ElixirStringImpl(node);
      }
      else if (type == STRING_HEREDOC) {
        return new ElixirStringHeredocImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
