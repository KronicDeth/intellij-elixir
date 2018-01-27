// This is a generated file. Not intended for manual editing.
package org.elixir_lang.beam.assembly.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.beam.assembly.psi.impl.*;

public interface Types {

  IElementType OPERANDS = new ElementType("OPERANDS");
  IElementType OPERATION = new ElementType("OPERATION");
  IElementType TERM = new ElementType("TERM");
  IElementType VALUES = new ElementType("VALUES");

  IElementType CLOSING_PARENTHESIS = new TokenType("CLOSING_PARENTHESIS");
  IElementType COLON = new TokenType("COLON");
  IElementType COMMA = new TokenType("COMMA");
  IElementType INTEGER = new TokenType("INTEGER");
  IElementType NAME = new TokenType("NAME");
  IElementType OPENING_PARENTHESIS = new TokenType("OPENING_PARENTHESIS");
  IElementType QUALIFIED_ALIAS = new TokenType("QUALIFIED_ALIAS");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == OPERANDS) {
        return new OperandsImpl(node);
      }
      else if (type == OPERATION) {
        return new OperationImpl(node);
      }
      else if (type == TERM) {
        return new TermImpl(node);
      }
      else if (type == VALUES) {
        return new ValuesImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
