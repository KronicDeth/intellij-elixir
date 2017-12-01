// This is a generated file. Not intended for manual editing.
package org.elixir_lang.eex.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.eex.psi.impl.EExTagImpl;

public interface Types {

  IElementType TAG = new ElementType("TAG");

  IElementType CLOSING = new TokenType("%>");
  IElementType COMMENT = new TokenType("Comment");
  IElementType COMMENT_MARKER = new TokenType("#");
  IElementType DATA = new TokenType("Data");
  IElementType ELIXIR = new TokenType("Elixir");
  IElementType EMPTY_MARKER = new TokenType("Empty Marker");
  IElementType EQUALS_MARKER = new TokenType("=");
  IElementType ESCAPED_OPENING = new TokenType("<%%");
  IElementType FORWARD_SLASH_MARKER = new TokenType("/");
  IElementType OPENING = new TokenType("<%");
  IElementType PIPE_MARKER = new TokenType("|");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == TAG) {
        return new EExTagImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
