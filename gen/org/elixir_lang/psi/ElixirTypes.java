// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType ADDITION_OPERATION = new ElixirElementType("ADDITION_OPERATION");
  IElementType AND_OPERATION = new ElixirElementType("AND_OPERATION");
  IElementType ARROW_OPERATION = new ElixirElementType("ARROW_OPERATION");
  IElementType ASSOCIATION_OPERATION = new ElixirElementType("ASSOCIATION_OPERATION");
  IElementType ATOM = new ElixirElementType("ATOM");
  IElementType AT_OPERATION = new ElixirElementType("AT_OPERATION");
  IElementType CAPTURE_OPERATION = new ElixirElementType("CAPTURE_OPERATION");
  IElementType CHAR_LIST = new ElixirElementType("CHAR_LIST");
  IElementType CHAR_LIST_HEREDOC = new ElixirElementType("CHAR_LIST_HEREDOC");
  IElementType COMPARISON_OPERATION = new ElixirElementType("COMPARISON_OPERATION");
  IElementType DOT_OPERATION = new ElixirElementType("DOT_OPERATION");
  IElementType EMPTY_PARENTHESES = new ElixirElementType("EMPTY_PARENTHESES");
  IElementType END_OF_EXPRESSION = new ElixirElementType("END_OF_EXPRESSION");
  IElementType EXPRESSION = new ElixirElementType("EXPRESSION");
  IElementType HAT_OPERATION = new ElixirElementType("HAT_OPERATION");
  IElementType INTERPOLATION = new ElixirElementType("INTERPOLATION");
  IElementType IN_MATCH_OPERATION = new ElixirElementType("IN_MATCH_OPERATION");
  IElementType IN_OPERATION = new ElixirElementType("IN_OPERATION");
  IElementType KEYWORD_KEY = new ElixirElementType("KEYWORD_KEY");
  IElementType KEYWORD_PAIR = new ElixirElementType("KEYWORD_PAIR");
  IElementType KEYWORD_VALUE = new ElixirElementType("KEYWORD_VALUE");
  IElementType LIST = new ElixirElementType("LIST");
  IElementType MATCH_OPERATION = new ElixirElementType("MATCH_OPERATION");
  IElementType MULTIPLICATION_OPERATION = new ElixirElementType("MULTIPLICATION_OPERATION");
  IElementType OR_OPERATION = new ElixirElementType("OR_OPERATION");
  IElementType PIPE_OPERATION = new ElixirElementType("PIPE_OPERATION");
  IElementType RELATIONAL_OPERATION = new ElixirElementType("RELATIONAL_OPERATION");
  IElementType SIGIL = new ElixirElementType("SIGIL");
  IElementType STAB_OPERATION = new ElixirElementType("STAB_OPERATION");
  IElementType STRING = new ElixirElementType("STRING");
  IElementType STRING_HEREDOC = new ElixirElementType("STRING_HEREDOC");
  IElementType TWO_OPERATION = new ElixirElementType("TWO_OPERATION");
  IElementType TYPE_OPERATION = new ElixirElementType("TYPE_OPERATION");
  IElementType UNARY_OPERATION = new ElixirElementType("UNARY_OPERATION");
  IElementType VALUE = new ElixirElementType("VALUE");
  IElementType WHEN_OPERATION = new ElixirElementType("WHEN_OPERATION");

  IElementType ALIAS = new ElixirTokenType("ALIAS");
  IElementType AND_OPERATOR = new ElixirTokenType("AND_OPERATOR");
  IElementType ARROW_OPERATOR = new ElixirTokenType("ARROW_OPERATOR");
  IElementType ASSOCIATION_OPERATOR = new ElixirTokenType("ASSOCIATION_OPERATOR");
  IElementType ATOM_FRAGMENT = new ElixirTokenType("ATOM_FRAGMENT");
  IElementType AT_OPERATOR = new ElixirTokenType("AT_OPERATOR");
  IElementType BIT_STRING_OPERATOR = new ElixirTokenType("BIT_STRING_OPERATOR");
  IElementType CAPTURE_OPERATOR = new ElixirTokenType("CAPTURE_OPERATOR");
  IElementType CHAR_LIST_FRAGMENT = new ElixirTokenType("CHAR_LIST_FRAGMENT");
  IElementType CHAR_LIST_HEREDOC_PROMOTER = new ElixirTokenType("CHAR_LIST_HEREDOC_PROMOTER");
  IElementType CHAR_LIST_HEREDOC_TERMINATOR = new ElixirTokenType("CHAR_LIST_HEREDOC_TERMINATOR");
  IElementType CHAR_LIST_PROMOTER = new ElixirTokenType("CHAR_LIST_PROMOTER");
  IElementType CHAR_LIST_SIGIL_HEREDOC_PROMOTER = new ElixirTokenType("CHAR_LIST_SIGIL_HEREDOC_PROMOTER");
  IElementType CHAR_LIST_SIGIL_HEREDOC_TERMINATOR = new ElixirTokenType("CHAR_LIST_SIGIL_HEREDOC_TERMINATOR");
  IElementType CHAR_LIST_SIGIL_PROMOTER = new ElixirTokenType("CHAR_LIST_SIGIL_PROMOTER");
  IElementType CHAR_LIST_SIGIL_TERMINATOR = new ElixirTokenType("CHAR_LIST_SIGIL_TERMINATOR");
  IElementType CHAR_LIST_TERMINATOR = new ElixirTokenType("CHAR_LIST_TERMINATOR");
  IElementType CHAR_TOKEN = new ElixirTokenType("CHAR_TOKEN");
  IElementType CLOSING_BRACKET = new ElixirTokenType("]");
  IElementType CLOSING_PARENTHESIS = new ElixirTokenType(")");
  IElementType COLON = new ElixirTokenType("COLON");
  IElementType COMMA = new ElixirTokenType(",");
  IElementType COMMENT = new ElixirTokenType("COMMENT");
  IElementType COMPARISON_OPERATOR = new ElixirTokenType("COMPARISON_OPERATOR");
  IElementType DOT_OPERATOR = new ElixirTokenType(".");
  IElementType DUAL_OPERATOR = new ElixirTokenType("DUAL_OPERATOR");
  IElementType EOL = new ElixirTokenType("EOL");
  IElementType HAT_OPERATOR = new ElixirTokenType("HAT_OPERATOR");
  IElementType IDENTIFIER = new ElixirTokenType("IDENTIFIER");
  IElementType INTERPOLATING_CHAR_LIST_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_CHAR_LIST_SIGIL_NAME");
  IElementType INTERPOLATING_REGEX_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_REGEX_SIGIL_NAME");
  IElementType INTERPOLATING_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_SIGIL_NAME");
  IElementType INTERPOLATING_STRING_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_STRING_SIGIL_NAME");
  IElementType INTERPOLATING_WORDS_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_WORDS_SIGIL_NAME");
  IElementType INTERPOLATION_END = new ElixirTokenType("INTERPOLATION_END");
  IElementType INTERPOLATION_START = new ElixirTokenType("INTERPOLATION_START");
  IElementType IN_MATCH_OPERATOR = new ElixirTokenType("IN_MATCH_OPERATOR");
  IElementType IN_OPERATOR = new ElixirTokenType("in");
  IElementType LITERAL = new ElixirTokenType("literal");
  IElementType LITERAL_CHAR_LIST_SIGIL_NAME = new ElixirTokenType("LITERAL_CHAR_LIST_SIGIL_NAME");
  IElementType LITERAL_REGEX_SIGIL_NAME = new ElixirTokenType("LITERAL_REGEX_SIGIL_NAME");
  IElementType LITERAL_SIGIL_NAME = new ElixirTokenType("LITERAL_SIGIL_NAME");
  IElementType LITERAL_STRING_SIGIL_NAME = new ElixirTokenType("LITERAL_STRING_SIGIL_NAME");
  IElementType LITERAL_WORDS_SIGIL_NAME = new ElixirTokenType("LITERAL_WORDS_SIGIL_NAME");
  IElementType MAP_OPERATOR = new ElixirTokenType("MAP_OPERATOR");
  IElementType MATCH_OPERATOR = new ElixirTokenType("MATCH_OPERATOR");
  IElementType MULTIPLICATION_OPERATOR = new ElixirTokenType("MULTIPLICATION_OPERATOR");
  IElementType NUMBER = new ElixirTokenType("NUMBER");
  IElementType OPENING_BRACKET = new ElixirTokenType("[");
  IElementType OPENING_PARENTHESIS = new ElixirTokenType("(");
  IElementType OPERATOR_KEYWORD = new ElixirTokenType("OPERATOR_KEYWORD");
  IElementType OR_OPERATOR = new ElixirTokenType("OR_OPERATOR");
  IElementType PIPE_OPERATOR = new ElixirTokenType("PIPE_OPERATOR");
  IElementType REGEX_FRAGMENT = new ElixirTokenType("REGEX_FRAGMENT");
  IElementType REGEX_HEREDOC_PROMOTER = new ElixirTokenType("REGEX_HEREDOC_PROMOTER");
  IElementType REGEX_HEREDOC_TERMINATOR = new ElixirTokenType("REGEX_HEREDOC_TERMINATOR");
  IElementType REGEX_PROMOTER = new ElixirTokenType("REGEX_PROMOTER");
  IElementType REGEX_TERMINATOR = new ElixirTokenType("REGEX_TERMINATOR");
  IElementType RELATIONAL_OPERATOR = new ElixirTokenType("RELATIONAL_OPERATOR");
  IElementType SEMICOLON = new ElixirTokenType(";");
  IElementType SIGIL_FRAGMENT = new ElixirTokenType("SIGIL_FRAGMENT");
  IElementType SIGIL_HEREDOC_PROMOTER = new ElixirTokenType("SIGIL_HEREDOC_PROMOTER");
  IElementType SIGIL_HEREDOC_TERMINATOR = new ElixirTokenType("SIGIL_HEREDOC_TERMINATOR");
  IElementType SIGIL_MODIFIER = new ElixirTokenType("SIGIL_MODIFIER");
  IElementType SIGIL_PROMOTER = new ElixirTokenType("SIGIL_PROMOTER");
  IElementType SIGIL_TERMINATOR = new ElixirTokenType("SIGIL_TERMINATOR");
  IElementType STAB_OPERATOR = new ElixirTokenType("STAB_OPERATOR");
  IElementType STRING_FRAGMENT = new ElixirTokenType("STRING_FRAGMENT");
  IElementType STRING_HEREDOC_PROMOTER = new ElixirTokenType("STRING_HEREDOC_PROMOTER");
  IElementType STRING_HEREDOC_TERMINATOR = new ElixirTokenType("STRING_HEREDOC_TERMINATOR");
  IElementType STRING_PROMOTER = new ElixirTokenType("STRING_PROMOTER");
  IElementType STRING_SIGIL_HEREDOC_PROMOTER = new ElixirTokenType("STRING_SIGIL_HEREDOC_PROMOTER");
  IElementType STRING_SIGIL_HEREDOC_TERMINATOR = new ElixirTokenType("STRING_SIGIL_HEREDOC_TERMINATOR");
  IElementType STRING_SIGIL_PROMOTER = new ElixirTokenType("STRING_SIGIL_PROMOTER");
  IElementType STRING_SIGIL_TERMINATOR = new ElixirTokenType("STRING_SIGIL_TERMINATOR");
  IElementType STRING_TERMINATOR = new ElixirTokenType("STRING_TERMINATOR");
  IElementType STRUCT_OPERATOR = new ElixirTokenType("%");
  IElementType TILDE = new ElixirTokenType("TILDE");
  IElementType TUPLE_OPERATOR = new ElixirTokenType("TUPLE_OPERATOR");
  IElementType TWO_OPERATOR = new ElixirTokenType("TWO_OPERATOR");
  IElementType TYPE_OPERATOR = new ElixirTokenType("TYPE_OPERATOR");
  IElementType UNARY_OPERATOR = new ElixirTokenType("UNARY_OPERATOR");
  IElementType VALID_ESCAPE_SEQUENCE = new ElixirTokenType("VALID_ESCAPE_SEQUENCE");
  IElementType WHEN_OPERATOR = new ElixirTokenType("WHEN_OPERATOR");
  IElementType WORDS_FRAGMENT = new ElixirTokenType("WORDS_FRAGMENT");
  IElementType WORDS_HEREDOC_PROMOTER = new ElixirTokenType("WORDS_HEREDOC_PROMOTER");
  IElementType WORDS_HEREDOC_TERMINATOR = new ElixirTokenType("WORDS_HEREDOC_TERMINATOR");
  IElementType WORDS_PROMOTER = new ElixirTokenType("WORDS_PROMOTER");
  IElementType WORDS_TERMINATOR = new ElixirTokenType("WORDS_TERMINATOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ADDITION_OPERATION) {
        return new ElixirAdditionOperationImpl(node);
      }
      else if (type == AND_OPERATION) {
        return new ElixirAndOperationImpl(node);
      }
      else if (type == ARROW_OPERATION) {
        return new ElixirArrowOperationImpl(node);
      }
      else if (type == ASSOCIATION_OPERATION) {
        return new ElixirAssociationOperationImpl(node);
      }
      else if (type == ATOM) {
        return new ElixirAtomImpl(node);
      }
      else if (type == AT_OPERATION) {
        return new ElixirAtOperationImpl(node);
      }
      else if (type == CAPTURE_OPERATION) {
        return new ElixirCaptureOperationImpl(node);
      }
      else if (type == CHAR_LIST) {
        return new ElixirCharListImpl(node);
      }
      else if (type == CHAR_LIST_HEREDOC) {
        return new ElixirCharListHeredocImpl(node);
      }
      else if (type == COMPARISON_OPERATION) {
        return new ElixirComparisonOperationImpl(node);
      }
      else if (type == DOT_OPERATION) {
        return new ElixirDotOperationImpl(node);
      }
      else if (type == EMPTY_PARENTHESES) {
        return new ElixirEmptyParenthesesImpl(node);
      }
      else if (type == END_OF_EXPRESSION) {
        return new ElixirEndOfExpressionImpl(node);
      }
      else if (type == EXPRESSION) {
        return new ElixirExpressionImpl(node);
      }
      else if (type == HAT_OPERATION) {
        return new ElixirHatOperationImpl(node);
      }
      else if (type == INTERPOLATION) {
        return new ElixirInterpolationImpl(node);
      }
      else if (type == IN_MATCH_OPERATION) {
        return new ElixirInMatchOperationImpl(node);
      }
      else if (type == IN_OPERATION) {
        return new ElixirInOperationImpl(node);
      }
      else if (type == KEYWORD_KEY) {
        return new ElixirKeywordKeyImpl(node);
      }
      else if (type == KEYWORD_PAIR) {
        return new ElixirKeywordPairImpl(node);
      }
      else if (type == KEYWORD_VALUE) {
        return new ElixirKeywordValueImpl(node);
      }
      else if (type == LIST) {
        return new ElixirListImpl(node);
      }
      else if (type == MATCH_OPERATION) {
        return new ElixirMatchOperationImpl(node);
      }
      else if (type == MULTIPLICATION_OPERATION) {
        return new ElixirMultiplicationOperationImpl(node);
      }
      else if (type == OR_OPERATION) {
        return new ElixirOrOperationImpl(node);
      }
      else if (type == PIPE_OPERATION) {
        return new ElixirPipeOperationImpl(node);
      }
      else if (type == RELATIONAL_OPERATION) {
        return new ElixirRelationalOperationImpl(node);
      }
      else if (type == SIGIL) {
        return new ElixirSigilImpl(node);
      }
      else if (type == STAB_OPERATION) {
        return new ElixirStabOperationImpl(node);
      }
      else if (type == STRING) {
        return new ElixirStringImpl(node);
      }
      else if (type == STRING_HEREDOC) {
        return new ElixirStringHeredocImpl(node);
      }
      else if (type == TWO_OPERATION) {
        return new ElixirTwoOperationImpl(node);
      }
      else if (type == TYPE_OPERATION) {
        return new ElixirTypeOperationImpl(node);
      }
      else if (type == UNARY_OPERATION) {
        return new ElixirUnaryOperationImpl(node);
      }
      else if (type == VALUE) {
        return new ElixirValueImpl(node);
      }
      else if (type == WHEN_OPERATION) {
        return new ElixirWhenOperationImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
