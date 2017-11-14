// This is a generated file. Not intended for manual editing.
package org.elixir_lang.eex.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.eex.psi.impl.*;

public interface Types {

  IElementType COMMENT_TAG = new ElementType("COMMENT_TAG");
  IElementType ELIXIR_TAG = new ElementType("ELIXIR_TAG");
  IElementType MARKER = new ElementType("MARKER");
  IElementType QUOTATION_TAG = new ElementType("QUOTATION_TAG");

  IElementType CLOSING = new TokenType("%>");
  IElementType COMMENT = new TokenType("Comment");
  IElementType DATA = new TokenType("Data");
  IElementType ELIXIR = new TokenType("Elixir");
  IElementType EQUALS_MARKER = new TokenType("=");
  IElementType FORWARD_SLASH_MARKER = new TokenType("/");
  IElementType OPENING = new TokenType("<%");
  IElementType OPENING_COMMENT = new TokenType("<%#");
  IElementType OPENING_QUOTATION = new TokenType("<%%");
  IElementType PIPE_MARKER = new TokenType("|");
  IElementType QUOTATION = new TokenType("Quotation");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == COMMENT_TAG) {
        return new EExCommentTagImpl(node);
      }
      else if (type == ELIXIR_TAG) {
        return new EExElixirTagImpl(node);
      }
      else if (type == MARKER) {
        return new EExMarkerImpl(node);
      }
      else if (type == QUOTATION_TAG) {
        return new EExQuotationTagImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
