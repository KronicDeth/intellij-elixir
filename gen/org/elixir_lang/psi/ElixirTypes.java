// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType ACCESS_EXPRESSION = new ElixirElementType("ACCESS_EXPRESSION");
  IElementType ATOM = new ElixirElementType("ATOM");
  IElementType CHAR_LIST = new ElixirElementType("CHAR_LIST");
  IElementType CHAR_LIST_HEREDOC = new ElixirElementType("CHAR_LIST_HEREDOC");
  IElementType DOT_ALIAS = new ElixirElementType("DOT_ALIAS");
  IElementType DOT_IDENTIFIER = new ElixirElementType("DOT_IDENTIFIER");
  IElementType EMPTY_PARENTHESES = new ElixirElementType("EMPTY_PARENTHESES");
  IElementType END_OF_EXPRESSION = new ElixirElementType("END_OF_EXPRESSION");
  IElementType EXPRESSION = new ElixirElementType("EXPRESSION");
  IElementType INTERPOLATION = new ElixirElementType("INTERPOLATION");
  IElementType KEYWORD_KEY = new ElixirElementType("KEYWORD_KEY");
  IElementType KEYWORD_PAIR = new ElixirElementType("KEYWORD_PAIR");
  IElementType KEYWORD_VALUE = new ElixirElementType("KEYWORD_VALUE");
  IElementType LIST = new ElixirElementType("LIST");
  IElementType MATCHED_EXPRESSION = new ElixirElementType("MATCHED_EXPRESSION");
  IElementType MATCHED_EXPRESSION_ADDITION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ADDITION_OPERATION");
  IElementType MATCHED_EXPRESSION_AND_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_AND_OPERATION");
  IElementType MATCHED_EXPRESSION_ARROW_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ARROW_OPERATION");
  IElementType MATCHED_EXPRESSION_AT_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_AT_OPERATION");
  IElementType MATCHED_EXPRESSION_CAPTURE_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_CAPTURE_OPERATION");
  IElementType MATCHED_EXPRESSION_COMPARISON_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_COMPARISON_OPERATION");
  IElementType MATCHED_EXPRESSION_HAT_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_HAT_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_MATCH_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_MATCH_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_OPERATION");
  IElementType MATCHED_EXPRESSION_MATCH_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MATCH_OPERATION");
  IElementType MATCHED_EXPRESSION_MULTIPLICATION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MULTIPLICATION_OPERATION");
  IElementType MATCHED_EXPRESSION_OR_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_OR_OPERATION");
  IElementType MATCHED_EXPRESSION_PIPE_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_PIPE_OPERATION");
  IElementType MATCHED_EXPRESSION_RELATIONAL_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_RELATIONAL_OPERATION");
  IElementType MATCHED_EXPRESSION_TWO_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TWO_OPERATION");
  IElementType MATCHED_EXPRESSION_TYPE_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TYPE_OPERATION");
  IElementType MATCHED_EXPRESSION_UNARY_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_UNARY_OPERATION");
  IElementType MATCHED_EXPRESSION_WHEN_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_WHEN_OPERATION");
  IElementType MAX_EXPRESSION = new ElixirElementType("MAX_EXPRESSION");
  IElementType NO_PARENTHESES_ONE_EXPRESSION = new ElixirElementType("NO_PARENTHESES_ONE_EXPRESSION");
  IElementType NUMBER_AT_OPERATION = new ElixirElementType("NUMBER_AT_OPERATION");
  IElementType NUMBER_CAPTURE_OPERATION = new ElixirElementType("NUMBER_CAPTURE_OPERATION");
  IElementType NUMBER_UNARY_OPERATION = new ElixirElementType("NUMBER_UNARY_OPERATION");
  IElementType SIGIL = new ElixirElementType("SIGIL");
  IElementType STRING = new ElixirElementType("STRING");
  IElementType STRING_HEREDOC = new ElixirElementType("STRING_HEREDOC");

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
  IElementType KEYWORD_PAIR_COLON = new ElixirTokenType("KEYWORD_PAIR_COLON");
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
       if (type == ACCESS_EXPRESSION) {
        return new ElixirAccessExpressionImpl(node);
      }
      else if (type == ATOM) {
        return new ElixirAtomImpl(node);
      }
      else if (type == CHAR_LIST) {
        return new ElixirCharListImpl(node);
      }
      else if (type == CHAR_LIST_HEREDOC) {
        return new ElixirCharListHeredocImpl(node);
      }
      else if (type == DOT_ALIAS) {
        return new ElixirDotAliasImpl(node);
      }
      else if (type == DOT_IDENTIFIER) {
        return new ElixirDotIdentifierImpl(node);
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
      else if (type == INTERPOLATION) {
        return new ElixirInterpolationImpl(node);
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
      else if (type == MATCHED_EXPRESSION) {
        return new ElixirMatchedExpressionImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ADDITION_OPERATION) {
        return new ElixirMatchedExpressionAdditionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_AND_OPERATION) {
        return new ElixirMatchedExpressionAndOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ARROW_OPERATION) {
        return new ElixirMatchedExpressionArrowOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_AT_OPERATION) {
        return new ElixirMatchedExpressionAtOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_CAPTURE_OPERATION) {
        return new ElixirMatchedExpressionCaptureOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_COMPARISON_OPERATION) {
        return new ElixirMatchedExpressionComparisonOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_HAT_OPERATION) {
        return new ElixirMatchedExpressionHatOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_MATCH_OPERATION) {
        return new ElixirMatchedExpressionInMatchOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_OPERATION) {
        return new ElixirMatchedExpressionInOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MATCH_OPERATION) {
        return new ElixirMatchedExpressionMatchOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MULTIPLICATION_OPERATION) {
        return new ElixirMatchedExpressionMultiplicationOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_OR_OPERATION) {
        return new ElixirMatchedExpressionOrOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_PIPE_OPERATION) {
        return new ElixirMatchedExpressionPipeOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_RELATIONAL_OPERATION) {
        return new ElixirMatchedExpressionRelationalOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TWO_OPERATION) {
        return new ElixirMatchedExpressionTwoOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TYPE_OPERATION) {
        return new ElixirMatchedExpressionTypeOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_UNARY_OPERATION) {
        return new ElixirMatchedExpressionUnaryOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_WHEN_OPERATION) {
        return new ElixirMatchedExpressionWhenOperationImpl(node);
      }
      else if (type == MAX_EXPRESSION) {
        return new ElixirMaxExpressionImpl(node);
      }
      else if (type == NO_PARENTHESES_ONE_EXPRESSION) {
        return new ElixirNoParenthesesOneExpressionImpl(node);
      }
      else if (type == NUMBER_AT_OPERATION) {
        return new ElixirNumberAtOperationImpl(node);
      }
      else if (type == NUMBER_CAPTURE_OPERATION) {
        return new ElixirNumberCaptureOperationImpl(node);
      }
      else if (type == NUMBER_UNARY_OPERATION) {
        return new ElixirNumberUnaryOperationImpl(node);
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
