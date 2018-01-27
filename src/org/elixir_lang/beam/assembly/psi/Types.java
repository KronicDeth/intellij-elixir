// This is a generated file. Not intended for manual editing.
package org.elixir_lang.beam.assembly.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.beam.assembly.psi.impl.*;

public interface Types {

  IElementType FUNCTION_REFERENCE = new ElementType("FUNCTION_REFERENCE");
  IElementType OPERANDS = new ElementType("OPERANDS");
  IElementType OPERATION = new ElementType("OPERATION");
  IElementType QUALIFIER = new ElementType("QUALIFIER");
  IElementType RELATIVE = new ElementType("RELATIVE");
  IElementType TERM = new ElementType("TERM");
  IElementType VALUES = new ElementType("VALUES");

  IElementType ATOM = new TokenType("ATOM");
  IElementType CLOSING_PARENTHESIS = new TokenType("CLOSING_PARENTHESIS");
  IElementType COLON = new TokenType("COLON");
  IElementType COMMA = new TokenType("COMMA");
  IElementType DOT_OPERATOR = new TokenType("DOT_OPERATOR");
  IElementType INTEGER = new TokenType("INTEGER");
  IElementType NAME = new TokenType("NAME");
  IElementType NAME_ARITY_SEPARATOR = new TokenType("NAME_ARITY_SEPARATOR");
  IElementType OPENING_PARENTHESIS = new TokenType("OPENING_PARENTHESIS");
  IElementType QUALIFIED_ALIAS = new TokenType("QUALIFIED_ALIAS");
  IElementType REFERENCE_OPERATOR = new TokenType("REFERENCE_OPERATOR");
  IElementType SYMBOLIC_OPERATOR = new TokenType("SYMBOLIC_OPERATOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == FUNCTION_REFERENCE) {
        return new FunctionReferenceImpl(node);
      }
      else if (type == OPERANDS) {
        return new OperandsImpl(node);
      }
      else if (type == OPERATION) {
        return new OperationImpl(node);
      }
      else if (type == QUALIFIER) {
        return new QualifierImpl(node);
      }
      else if (type == RELATIVE) {
        return new RelativeImpl(node);
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
