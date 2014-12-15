// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType ADJACENT_EXPRESSION = new ElixirElementType("ADJACENT_EXPRESSION");
  IElementType ATOM = new ElixirElementType("ATOM");
  IElementType AT_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("AT_MATCHED_EXPRESSION_OPERATION");
  IElementType AT_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("AT_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType BINARY_WHOLE_NUMBER = new ElixirElementType("BINARY_WHOLE_NUMBER");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS_EXPRESSION = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS_EXPRESSION");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_MANY = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_MANY");
  IElementType CAPTURE_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("CAPTURE_MATCHED_EXPRESSION_OPERATION");
  IElementType CAPTURE_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("CAPTURE_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType CHAR_LIST = new ElixirElementType("CHAR_LIST");
  IElementType CHAR_LIST_HEREDOC = new ElixirElementType("CHAR_LIST_HEREDOC");
  IElementType DECIMAL_FLOAT = new ElixirElementType("DECIMAL_FLOAT");
  IElementType DECIMAL_NUMBER = new ElixirElementType("DECIMAL_NUMBER");
  IElementType DECIMAL_WHOLE_NUMBER = new ElixirElementType("DECIMAL_WHOLE_NUMBER");
  IElementType EMPTY_PARENTHESES = new ElixirElementType("EMPTY_PARENTHESES");
  IElementType END_OF_EXPRESSION = new ElixirElementType("END_OF_EXPRESSION");
  IElementType EXPRESSION = new ElixirElementType("EXPRESSION");
  IElementType HEXADECIMAL_WHOLE_NUMBER = new ElixirElementType("HEXADECIMAL_WHOLE_NUMBER");
  IElementType IDENTIFIER_EXPRESSION = new ElixirElementType("IDENTIFIER_EXPRESSION");
  IElementType INTERPOLATION = new ElixirElementType("INTERPOLATION");
  IElementType KEYWORD_KEY = new ElixirElementType("KEYWORD_KEY");
  IElementType KEYWORD_PAIR = new ElixirElementType("KEYWORD_PAIR");
  IElementType KEYWORD_VALUE = new ElixirElementType("KEYWORD_VALUE");
  IElementType LIST = new ElixirElementType("LIST");
  IElementType MATCHED_EXPRESSION = new ElixirElementType("MATCHED_EXPRESSION");
  IElementType MATCHED_EXPRESSION_ACCESS_EXPRESSION = new ElixirElementType("MATCHED_EXPRESSION_ACCESS_EXPRESSION");
  IElementType MATCHED_EXPRESSION_ADDITION_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ADDITION_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_ADDITION_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ADDITION_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_AND_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_AND_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_AND_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_AND_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_ARROW_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ARROW_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_ARROW_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ARROW_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_COMPARISON_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_COMPARISON_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_COMPARISON_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_COMPARISON_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_DOT_ALIAS = new ElixirElementType("MATCHED_EXPRESSION_DOT_ALIAS");
  IElementType MATCHED_EXPRESSION_DOT_IDENTIFIER = new ElixirElementType("MATCHED_EXPRESSION_DOT_IDENTIFIER");
  IElementType MATCHED_EXPRESSION_HAT_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_HAT_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_HAT_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_HAT_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_MATCH_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_MATCH_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_MATCH_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_MATCH_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MATCH_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MATCH_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MATCH_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MATCH_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MAX_EXPRESSION = new ElixirElementType("MATCHED_EXPRESSION_MAX_EXPRESSION");
  IElementType MATCHED_EXPRESSION_MULTIPLICATION_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MULTIPLICATION_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MULTIPLICATION_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MULTIPLICATION_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_OR_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_OR_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_OR_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_OR_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_PIPE_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_PIPE_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_PIPE_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_PIPE_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_RELATIONAL_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_RELATIONAL_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_RELATIONAL_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_RELATIONAL_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TWO_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TWO_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TWO_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TWO_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TYPE_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TYPE_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TYPE_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TYPE_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_WHEN_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_WHEN_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_WHEN_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_WHEN_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType NO_PARENTHESES_EXPRESSION = new ElixirElementType("NO_PARENTHESES_EXPRESSION");
  IElementType NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION = new ElixirElementType("NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION");
  IElementType NO_PARENTHESES_MAYBE_QUALIFIED_IDENTIFIER = new ElixirElementType("NO_PARENTHESES_MAYBE_QUALIFIED_IDENTIFIER");
  IElementType NO_PARENTHESES_QUALIFIER = new ElixirElementType("NO_PARENTHESES_QUALIFIER");
  IElementType NO_PARENTHESES_QUALIFIER_AT_OPERATION = new ElixirElementType("NO_PARENTHESES_QUALIFIER_AT_OPERATION");
  IElementType NO_PARENTHESES_QUALIFIER_NUMBER_AT_OPERATION = new ElixirElementType("NO_PARENTHESES_QUALIFIER_NUMBER_AT_OPERATION");
  IElementType NO_PARENTHESES_QUALIFIER_NUMBER_CAPTURE_OPERATION = new ElixirElementType("NO_PARENTHESES_QUALIFIER_NUMBER_CAPTURE_OPERATION");
  IElementType NO_PARENTHESES_QUALIFIER_NUMBER_UNARY_OPERATION = new ElixirElementType("NO_PARENTHESES_QUALIFIER_NUMBER_UNARY_OPERATION");
  IElementType NO_PARENTHESES_STRICT = new ElixirElementType("NO_PARENTHESES_STRICT");
  IElementType NUMBER = new ElixirElementType("NUMBER");
  IElementType NUMBER_UNARY_OPERATION = new ElixirElementType("NUMBER_UNARY_OPERATION");
  IElementType OCTAL_WHOLE_NUMBER = new ElixirElementType("OCTAL_WHOLE_NUMBER");
  IElementType SIGIL = new ElixirElementType("SIGIL");
  IElementType STRING = new ElixirElementType("STRING");
  IElementType STRING_HEREDOC = new ElixirElementType("STRING_HEREDOC");
  IElementType UNARY_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("UNARY_MATCHED_EXPRESSION_OPERATION");
  IElementType UNARY_NO_PARENTHESES_EXPRESSION_OPERATION = new ElixirElementType("UNARY_NO_PARENTHESES_EXPRESSION_OPERATION");
  IElementType UNKNOWN_BASE_WHOLE_NUMBER = new ElixirElementType("UNKNOWN_BASE_WHOLE_NUMBER");

  IElementType ALIAS = new ElixirTokenType("ALIAS");
  IElementType AND_OPERATOR = new ElixirTokenType("AND_OPERATOR");
  IElementType ARROW_OPERATOR = new ElixirTokenType("ARROW_OPERATOR");
  IElementType ASSOCIATION_OPERATOR = new ElixirTokenType("ASSOCIATION_OPERATOR");
  IElementType ATOM_FRAGMENT = new ElixirTokenType("ATOM_FRAGMENT");
  IElementType AT_OPERATOR = new ElixirTokenType("AT_OPERATOR");
  IElementType BASE_WHOLE_NUMBER_PREFIX = new ElixirTokenType("0");
  IElementType BINARY_WHOLE_NUMBER_BASE = new ElixirTokenType("b");
  IElementType BIT_STRING_OPERATOR = new ElixirTokenType("BIT_STRING_OPERATOR");
  IElementType CALL = new ElixirTokenType("");
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
  IElementType DECIMAL_MARK = new ElixirTokenType("DECIMAL_MARK");
  IElementType DECIMAL_SEPARATOR = new ElixirTokenType("_");
  IElementType DOT_OPERATOR = new ElixirTokenType(".");
  IElementType DUAL_OPERATOR = new ElixirTokenType("DUAL_OPERATOR");
  IElementType END = new ElixirTokenType("end");
  IElementType EOL = new ElixirTokenType("EOL");
  IElementType EXPONENT_MARK = new ElixirTokenType("EXPONENT_MARK");
  IElementType FALSE = new ElixirTokenType("false");
  IElementType FN = new ElixirTokenType("fn");
  IElementType HAT_OPERATOR = new ElixirTokenType("HAT_OPERATOR");
  IElementType HEXADECIMAL_WHOLE_NUMBER_BASE = new ElixirTokenType("x");
  IElementType IDENTIFER = new ElixirTokenType("IDENTIFER");
  IElementType IDENTIFIER = new ElixirTokenType("IDENTIFIER");
  IElementType INTERPOLATING_CHAR_LIST_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_CHAR_LIST_SIGIL_NAME");
  IElementType INTERPOLATING_REGEX_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_REGEX_SIGIL_NAME");
  IElementType INTERPOLATING_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_SIGIL_NAME");
  IElementType INTERPOLATING_STRING_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_STRING_SIGIL_NAME");
  IElementType INTERPOLATING_WORDS_SIGIL_NAME = new ElixirTokenType("INTERPOLATING_WORDS_SIGIL_NAME");
  IElementType INTERPOLATION_END = new ElixirTokenType("INTERPOLATION_END");
  IElementType INTERPOLATION_START = new ElixirTokenType("INTERPOLATION_START");
  IElementType INVALID_BINARY_DIGITS = new ElixirTokenType("INVALID_BINARY_DIGITS");
  IElementType INVALID_DECIMAL_DIGITS = new ElixirTokenType("INVALID_DECIMAL_DIGITS");
  IElementType INVALID_HEXADECIMAL_DIGITS = new ElixirTokenType("INVALID_HEXADECIMAL_DIGITS");
  IElementType INVALID_OCTAL_DIGITS = new ElixirTokenType("INVALID_OCTAL_DIGITS");
  IElementType INVALID_UNKNOWN_BASE_DIGITS = new ElixirTokenType("INVALID_UNKNOWN_BASE_DIGITS");
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
  IElementType NIL = new ElixirTokenType("nil");
  IElementType OBSOLETE_BINARY_WHOLE_NUMBER_BASE = new ElixirTokenType("B");
  IElementType OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE = new ElixirTokenType("X");
  IElementType OCTAL_WHOLE_NUMBER_BASE = new ElixirTokenType("o");
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
  IElementType TRUE = new ElixirTokenType("true");
  IElementType TUPLE_OPERATOR = new ElixirTokenType("TUPLE_OPERATOR");
  IElementType TWO_OPERATOR = new ElixirTokenType("TWO_OPERATOR");
  IElementType TYPE_OPERATOR = new ElixirTokenType("TYPE_OPERATOR");
  IElementType UNARY_OPERATOR = new ElixirTokenType("UNARY_OPERATOR");
  IElementType UNKNOWN_WHOLE_NUMBER_BASE = new ElixirTokenType("UNKNOWN_WHOLE_NUMBER_BASE");
  IElementType VALID_BINARY_DIGITS = new ElixirTokenType("VALID_BINARY_DIGITS");
  IElementType VALID_DECIMAL_DIGITS = new ElixirTokenType("VALID_DECIMAL_DIGITS");
  IElementType VALID_ESCAPE_SEQUENCE = new ElixirTokenType("VALID_ESCAPE_SEQUENCE");
  IElementType VALID_HEXADECIMAL_DIGITS = new ElixirTokenType("VALID_HEXADECIMAL_DIGITS");
  IElementType VALID_OCTAL_DIGITS = new ElixirTokenType("VALID_OCTAL_DIGITS");
  IElementType WHEN_OPERATOR = new ElixirTokenType("WHEN_OPERATOR");
  IElementType WORDS_FRAGMENT = new ElixirTokenType("WORDS_FRAGMENT");
  IElementType WORDS_HEREDOC_PROMOTER = new ElixirTokenType("WORDS_HEREDOC_PROMOTER");
  IElementType WORDS_HEREDOC_TERMINATOR = new ElixirTokenType("WORDS_HEREDOC_TERMINATOR");
  IElementType WORDS_PROMOTER = new ElixirTokenType("WORDS_PROMOTER");
  IElementType WORDS_TERMINATOR = new ElixirTokenType("WORDS_TERMINATOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ADJACENT_EXPRESSION) {
        return new ElixirAdjacentExpressionImpl(node);
      }
      else if (type == ATOM) {
        return new ElixirAtomImpl(node);
      }
      else if (type == AT_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirAtMatchedExpressionOperationImpl(node);
      }
      else if (type == AT_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirAtNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == BINARY_WHOLE_NUMBER) {
        return new ElixirBinaryWholeNumberImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS) {
        return new ElixirCallArgumentsNoParenthesesKeywordsImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS_EXPRESSION) {
        return new ElixirCallArgumentsNoParenthesesKeywordsExpressionImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_MANY) {
        return new ElixirCallArgumentsNoParenthesesManyImpl(node);
      }
      else if (type == CAPTURE_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirCaptureMatchedExpressionOperationImpl(node);
      }
      else if (type == CAPTURE_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirCaptureNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == CHAR_LIST) {
        return new ElixirCharListImpl(node);
      }
      else if (type == CHAR_LIST_HEREDOC) {
        return new ElixirCharListHeredocImpl(node);
      }
      else if (type == DECIMAL_FLOAT) {
        return new ElixirDecimalFloatImpl(node);
      }
      else if (type == DECIMAL_NUMBER) {
        return new ElixirDecimalNumberImpl(node);
      }
      else if (type == DECIMAL_WHOLE_NUMBER) {
        return new ElixirDecimalWholeNumberImpl(node);
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
      else if (type == HEXADECIMAL_WHOLE_NUMBER) {
        return new ElixirHexadecimalWholeNumberImpl(node);
      }
      else if (type == IDENTIFIER_EXPRESSION) {
        return new ElixirIdentifierExpressionImpl(node);
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
      else if (type == MATCHED_EXPRESSION_ACCESS_EXPRESSION) {
        return new ElixirMatchedExpressionAccessExpressionImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ADDITION_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAdditionMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ADDITION_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAdditionNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_AND_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAndMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_AND_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAndNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ARROW_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionArrowMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ARROW_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionArrowNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_COMPARISON_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionComparisonMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_COMPARISON_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionComparisonNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_DOT_ALIAS) {
        return new ElixirMatchedExpressionDotAliasImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_DOT_IDENTIFIER) {
        return new ElixirMatchedExpressionDotIdentifierImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_HAT_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionHatMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_HAT_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionHatNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_MATCH_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInMatchMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_MATCH_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInMatchNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MATCH_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMatchMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MATCH_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMatchNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MAX_EXPRESSION) {
        return new ElixirMatchedExpressionMaxExpressionImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MULTIPLICATION_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMultiplicationMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MULTIPLICATION_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMultiplicationNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_OR_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionOrMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_OR_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionOrNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_PIPE_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionPipeMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_PIPE_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionPipeNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_RELATIONAL_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionRelationalMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_RELATIONAL_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionRelationalNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TWO_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTwoMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TWO_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTwoNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TYPE_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTypeMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TYPE_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTypeNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_WHEN_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionWhenMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_WHEN_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionWhenNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == NO_PARENTHESES_EXPRESSION) {
        return new ElixirNoParenthesesExpressionImpl(node);
      }
      else if (type == NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION) {
        return new ElixirNoParenthesesManyStrictNoParenthesesExpressionImpl(node);
      }
      else if (type == NO_PARENTHESES_MAYBE_QUALIFIED_IDENTIFIER) {
        return new ElixirNoParenthesesMaybeQualifiedIdentifierImpl(node);
      }
      else if (type == NO_PARENTHESES_QUALIFIER) {
        return new ElixirNoParenthesesQualifierImpl(node);
      }
      else if (type == NO_PARENTHESES_QUALIFIER_AT_OPERATION) {
        return new ElixirNoParenthesesQualifierAtOperationImpl(node);
      }
      else if (type == NO_PARENTHESES_QUALIFIER_NUMBER_AT_OPERATION) {
        return new ElixirNoParenthesesQualifierNumberAtOperationImpl(node);
      }
      else if (type == NO_PARENTHESES_QUALIFIER_NUMBER_CAPTURE_OPERATION) {
        return new ElixirNoParenthesesQualifierNumberCaptureOperationImpl(node);
      }
      else if (type == NO_PARENTHESES_QUALIFIER_NUMBER_UNARY_OPERATION) {
        return new ElixirNoParenthesesQualifierNumberUnaryOperationImpl(node);
      }
      else if (type == NO_PARENTHESES_STRICT) {
        return new ElixirNoParenthesesStrictImpl(node);
      }
      else if (type == NUMBER) {
        return new ElixirNumberImpl(node);
      }
      else if (type == NUMBER_UNARY_OPERATION) {
        return new ElixirNumberUnaryOperationImpl(node);
      }
      else if (type == OCTAL_WHOLE_NUMBER) {
        return new ElixirOctalWholeNumberImpl(node);
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
      else if (type == UNARY_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirUnaryMatchedExpressionOperationImpl(node);
      }
      else if (type == UNARY_NO_PARENTHESES_EXPRESSION_OPERATION) {
        return new ElixirUnaryNoParenthesesExpressionOperationImpl(node);
      }
      else if (type == UNKNOWN_BASE_WHOLE_NUMBER) {
        return new ElixirUnknownBaseWholeNumberImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
