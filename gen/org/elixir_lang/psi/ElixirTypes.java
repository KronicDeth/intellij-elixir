// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType ACCESS_EXPRESSION = new ElixirElementType("ACCESS_EXPRESSION");
  IElementType ADDITION_INFIX_OPERATOR = new ElixirElementType("ADDITION_INFIX_OPERATOR");
  IElementType ADJACENT_EXPRESSION = new ElixirElementType("ADJACENT_EXPRESSION");
  IElementType AND_INFIX_OPERATOR = new ElixirElementType("AND_INFIX_OPERATOR");
  IElementType ARROW_INFIX_OPERATOR = new ElixirElementType("ARROW_INFIX_OPERATOR");
  IElementType ATOM = new ElixirElementType("ATOM");
  IElementType AT_CHAR_TOKEN_OR_NUMBER_OPERATION = new ElixirElementType("AT_CHAR_TOKEN_OR_NUMBER_OPERATION");
  IElementType AT_EXPRESSION_OPERATION = new ElixirElementType("AT_EXPRESSION_OPERATION");
  IElementType AT_EXPRESSION_PREFIX_OPERATION = new ElixirElementType("AT_EXPRESSION_PREFIX_OPERATION");
  IElementType AT_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("AT_MATCHED_EXPRESSION_OPERATION");
  IElementType AT_MATCHED_EXPRESSION_PREFIX_OPERATION = new ElixirElementType("AT_MATCHED_EXPRESSION_PREFIX_OPERATION");
  IElementType AT_PREFIX_OPERATOR = new ElixirElementType("AT_PREFIX_OPERATOR");
  IElementType BINARY_WHOLE_NUMBER = new ElixirElementType("BINARY_WHOLE_NUMBER");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS_EXPRESSION = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_KEYWORDS_EXPRESSION");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_MANY = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_MANY");
  IElementType CAPTURE_CHAR_TOKEN_OR_NUMBER_OPERATION = new ElixirElementType("CAPTURE_CHAR_TOKEN_OR_NUMBER_OPERATION");
  IElementType CAPTURE_EXPRESSION_OPERATION = new ElixirElementType("CAPTURE_EXPRESSION_OPERATION");
  IElementType CAPTURE_EXPRESSION_PREFIX_OPERATION = new ElixirElementType("CAPTURE_EXPRESSION_PREFIX_OPERATION");
  IElementType CAPTURE_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("CAPTURE_MATCHED_EXPRESSION_OPERATION");
  IElementType CAPTURE_MATCHED_EXPRESSION_PREFIX_OPERATION = new ElixirElementType("CAPTURE_MATCHED_EXPRESSION_PREFIX_OPERATION");
  IElementType CAPTURE_PREFIX_OPERATOR = new ElixirElementType("CAPTURE_PREFIX_OPERATOR");
  IElementType CHAR_LIST = new ElixirElementType("CHAR_LIST");
  IElementType CHAR_LIST_HEREDOC = new ElixirElementType("CHAR_LIST_HEREDOC");
  IElementType COMPARISON_INFIX_OPERATOR = new ElixirElementType("COMPARISON_INFIX_OPERATOR");
  IElementType DECIMAL_FLOAT = new ElixirElementType("DECIMAL_FLOAT");
  IElementType DECIMAL_NUMBER = new ElixirElementType("DECIMAL_NUMBER");
  IElementType DECIMAL_WHOLE_NUMBER = new ElixirElementType("DECIMAL_WHOLE_NUMBER");
  IElementType EMPTY_PARENTHESES = new ElixirElementType("EMPTY_PARENTHESES");
  IElementType END_OF_EXPRESSION = new ElixirElementType("END_OF_EXPRESSION");
  IElementType HAT_INFIX_OPERATOR = new ElixirElementType("HAT_INFIX_OPERATOR");
  IElementType HEXADECIMAL_WHOLE_NUMBER = new ElixirElementType("HEXADECIMAL_WHOLE_NUMBER");
  IElementType INFIX_DOT_OPERATOR = new ElixirElementType("INFIX_DOT_OPERATOR");
  IElementType INTERPOLATION = new ElixirElementType("INTERPOLATION");
  IElementType IN_INFIX_OPERATOR = new ElixirElementType("IN_INFIX_OPERATOR");
  IElementType IN_MATCH_INFIX_OPERATOR = new ElixirElementType("IN_MATCH_INFIX_OPERATOR");
  IElementType KEYWORD_KEY = new ElixirElementType("KEYWORD_KEY");
  IElementType KEYWORD_PAIR = new ElixirElementType("KEYWORD_PAIR");
  IElementType KEYWORD_VALUE = new ElixirElementType("KEYWORD_VALUE");
  IElementType LIST = new ElixirElementType("LIST");
  IElementType MATCHED_EXPRESSION = new ElixirElementType("MATCHED_EXPRESSION");
  IElementType MATCHED_EXPRESSION_ADDITION_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ADDITION_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_ADDITION_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ADDITION_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_AND_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_AND_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_AND_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_AND_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_ARROW_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ARROW_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_ARROW_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_ARROW_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_COMPARISON_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_COMPARISON_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_COMPARISON_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_COMPARISON_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_HAT_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_HAT_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_HAT_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_HAT_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_MATCH_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_MATCH_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_IN_MATCH_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_IN_MATCH_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MATCH_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MATCH_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MATCH_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MATCH_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MULTIPLICATION_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MULTIPLICATION_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_MULTIPLICATION_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_MULTIPLICATION_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_OR_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_OR_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_OR_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_OR_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_PIPE_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_PIPE_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_PIPE_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_PIPE_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_RELATIONAL_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_RELATIONAL_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_RELATIONAL_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_RELATIONAL_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TWO_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TWO_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TWO_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TWO_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TYPE_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TYPE_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_TYPE_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_TYPE_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_WHEN_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_WHEN_EXPRESSION_OPERATION");
  IElementType MATCHED_EXPRESSION_WHEN_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("MATCHED_EXPRESSION_WHEN_MATCHED_EXPRESSION_OPERATION");
  IElementType MATCH_INFIX_OPERATOR = new ElixirElementType("MATCH_INFIX_OPERATOR");
  IElementType MAX_EXPRESSION = new ElixirElementType("MAX_EXPRESSION");
  IElementType MULTIPLICATION_INFIX_OPERATOR = new ElixirElementType("MULTIPLICATION_INFIX_OPERATOR");
  IElementType NO_PARENTHESES_CALL = new ElixirElementType("NO_PARENTHESES_CALL");
  IElementType NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION = new ElixirElementType("NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION");
  IElementType NO_PARENTHESES_ONE_EXPRESSION = new ElixirElementType("NO_PARENTHESES_ONE_EXPRESSION");
  IElementType NO_PARENTHESES_STRICT = new ElixirElementType("NO_PARENTHESES_STRICT");
  IElementType NUMBER = new ElixirElementType("NUMBER");
  IElementType OCTAL_WHOLE_NUMBER = new ElixirElementType("OCTAL_WHOLE_NUMBER");
  IElementType OR_INFIX_OPERATOR = new ElixirElementType("OR_INFIX_OPERATOR");
  IElementType PIPE_INFIX_OPERATOR = new ElixirElementType("PIPE_INFIX_OPERATOR");
  IElementType QUALIFIED_ALIAS = new ElixirElementType("QUALIFIED_ALIAS");
  IElementType QUALIFIED_IDENTIFIER = new ElixirElementType("QUALIFIED_IDENTIFIER");
  IElementType RELATIONAL_INFIX_OPERATOR = new ElixirElementType("RELATIONAL_INFIX_OPERATOR");
  IElementType SIGIL = new ElixirElementType("SIGIL");
  IElementType STRING = new ElixirElementType("STRING");
  IElementType STRING_HEREDOC = new ElixirElementType("STRING_HEREDOC");
  IElementType TAIL_EXPRESSION = new ElixirElementType("TAIL_EXPRESSION");
  IElementType TWO_INFIX_OPERATOR = new ElixirElementType("TWO_INFIX_OPERATOR");
  IElementType TYPE_INFIX_OPERATOR = new ElixirElementType("TYPE_INFIX_OPERATOR");
  IElementType UNARY_CHAR_TOKEN_OR_NUMBER_OPERATION = new ElixirElementType("UNARY_CHAR_TOKEN_OR_NUMBER_OPERATION");
  IElementType UNARY_EXPRESSION_OPERATION = new ElixirElementType("UNARY_EXPRESSION_OPERATION");
  IElementType UNARY_EXPRESSION_PREFIX_OPERATION = new ElixirElementType("UNARY_EXPRESSION_PREFIX_OPERATION");
  IElementType UNARY_MATCHED_EXPRESSION_OPERATION = new ElixirElementType("UNARY_MATCHED_EXPRESSION_OPERATION");
  IElementType UNARY_MATCHED_EXPRESSION_PREFIX_OPERATION = new ElixirElementType("UNARY_MATCHED_EXPRESSION_PREFIX_OPERATION");
  IElementType UNARY_PREFIX_OPERATOR = new ElixirElementType("UNARY_PREFIX_OPERATOR");
  IElementType UNKNOWN_BASE_WHOLE_NUMBER = new ElixirElementType("UNKNOWN_BASE_WHOLE_NUMBER");
  IElementType WHEN_INFIX_OPERATOR = new ElixirElementType("WHEN_INFIX_OPERATOR");

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
       if (type == ACCESS_EXPRESSION) {
        return new ElixirAccessExpressionImpl(node);
      }
      else if (type == ADDITION_INFIX_OPERATOR) {
        return new ElixirAdditionInfixOperatorImpl(node);
      }
      else if (type == ADJACENT_EXPRESSION) {
        return new ElixirAdjacentExpressionImpl(node);
      }
      else if (type == AND_INFIX_OPERATOR) {
        return new ElixirAndInfixOperatorImpl(node);
      }
      else if (type == ARROW_INFIX_OPERATOR) {
        return new ElixirArrowInfixOperatorImpl(node);
      }
      else if (type == ATOM) {
        return new ElixirAtomImpl(node);
      }
      else if (type == AT_CHAR_TOKEN_OR_NUMBER_OPERATION) {
        return new ElixirAtCharTokenOrNumberOperationImpl(node);
      }
      else if (type == AT_EXPRESSION_OPERATION) {
        return new ElixirAtExpressionOperationImpl(node);
      }
      else if (type == AT_EXPRESSION_PREFIX_OPERATION) {
        return new ElixirAtExpressionPrefixOperationImpl(node);
      }
      else if (type == AT_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirAtMatchedExpressionOperationImpl(node);
      }
      else if (type == AT_MATCHED_EXPRESSION_PREFIX_OPERATION) {
        return new ElixirAtMatchedExpressionPrefixOperationImpl(node);
      }
      else if (type == AT_PREFIX_OPERATOR) {
        return new ElixirAtPrefixOperatorImpl(node);
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
      else if (type == CAPTURE_CHAR_TOKEN_OR_NUMBER_OPERATION) {
        return new ElixirCaptureCharTokenOrNumberOperationImpl(node);
      }
      else if (type == CAPTURE_EXPRESSION_OPERATION) {
        return new ElixirCaptureExpressionOperationImpl(node);
      }
      else if (type == CAPTURE_EXPRESSION_PREFIX_OPERATION) {
        return new ElixirCaptureExpressionPrefixOperationImpl(node);
      }
      else if (type == CAPTURE_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirCaptureMatchedExpressionOperationImpl(node);
      }
      else if (type == CAPTURE_MATCHED_EXPRESSION_PREFIX_OPERATION) {
        return new ElixirCaptureMatchedExpressionPrefixOperationImpl(node);
      }
      else if (type == CAPTURE_PREFIX_OPERATOR) {
        return new ElixirCapturePrefixOperatorImpl(node);
      }
      else if (type == CHAR_LIST) {
        return new ElixirCharListImpl(node);
      }
      else if (type == CHAR_LIST_HEREDOC) {
        return new ElixirCharListHeredocImpl(node);
      }
      else if (type == COMPARISON_INFIX_OPERATOR) {
        return new ElixirComparisonInfixOperatorImpl(node);
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
      else if (type == HAT_INFIX_OPERATOR) {
        return new ElixirHatInfixOperatorImpl(node);
      }
      else if (type == HEXADECIMAL_WHOLE_NUMBER) {
        return new ElixirHexadecimalWholeNumberImpl(node);
      }
      else if (type == INFIX_DOT_OPERATOR) {
        return new ElixirInfixDotOperatorImpl(node);
      }
      else if (type == INTERPOLATION) {
        return new ElixirInterpolationImpl(node);
      }
      else if (type == IN_INFIX_OPERATOR) {
        return new ElixirInInfixOperatorImpl(node);
      }
      else if (type == IN_MATCH_INFIX_OPERATOR) {
        return new ElixirInMatchInfixOperatorImpl(node);
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
      else if (type == MATCHED_EXPRESSION_ADDITION_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAdditionExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ADDITION_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAdditionMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_AND_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAndExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_AND_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionAndMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ARROW_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionArrowExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_ARROW_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionArrowMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_COMPARISON_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionComparisonExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_COMPARISON_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionComparisonMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_HAT_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionHatExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_HAT_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionHatMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_MATCH_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInMatchExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_IN_MATCH_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionInMatchMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MATCH_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMatchExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MATCH_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMatchMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MULTIPLICATION_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMultiplicationExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_MULTIPLICATION_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionMultiplicationMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_OR_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionOrExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_OR_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionOrMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_PIPE_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionPipeExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_PIPE_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionPipeMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_RELATIONAL_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionRelationalExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_RELATIONAL_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionRelationalMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TWO_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTwoExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TWO_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTwoMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TYPE_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTypeExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_TYPE_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionTypeMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_WHEN_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionWhenExpressionOperationImpl(node);
      }
      else if (type == MATCHED_EXPRESSION_WHEN_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirMatchedExpressionWhenMatchedExpressionOperationImpl(node);
      }
      else if (type == MATCH_INFIX_OPERATOR) {
        return new ElixirMatchInfixOperatorImpl(node);
      }
      else if (type == MAX_EXPRESSION) {
        return new ElixirMaxExpressionImpl(node);
      }
      else if (type == MULTIPLICATION_INFIX_OPERATOR) {
        return new ElixirMultiplicationInfixOperatorImpl(node);
      }
      else if (type == NO_PARENTHESES_CALL) {
        return new ElixirNoParenthesesCallImpl(node);
      }
      else if (type == NO_PARENTHESES_MANY_STRICT_NO_PARENTHESES_EXPRESSION) {
        return new ElixirNoParenthesesManyStrictNoParenthesesExpressionImpl(node);
      }
      else if (type == NO_PARENTHESES_ONE_EXPRESSION) {
        return new ElixirNoParenthesesOneExpressionImpl(node);
      }
      else if (type == NO_PARENTHESES_STRICT) {
        return new ElixirNoParenthesesStrictImpl(node);
      }
      else if (type == NUMBER) {
        return new ElixirNumberImpl(node);
      }
      else if (type == OCTAL_WHOLE_NUMBER) {
        return new ElixirOctalWholeNumberImpl(node);
      }
      else if (type == OR_INFIX_OPERATOR) {
        return new ElixirOrInfixOperatorImpl(node);
      }
      else if (type == PIPE_INFIX_OPERATOR) {
        return new ElixirPipeInfixOperatorImpl(node);
      }
      else if (type == QUALIFIED_ALIAS) {
        return new ElixirQualifiedAliasImpl(node);
      }
      else if (type == QUALIFIED_IDENTIFIER) {
        return new ElixirQualifiedIdentifierImpl(node);
      }
      else if (type == RELATIONAL_INFIX_OPERATOR) {
        return new ElixirRelationalInfixOperatorImpl(node);
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
      else if (type == TAIL_EXPRESSION) {
        return new ElixirTailExpressionImpl(node);
      }
      else if (type == TWO_INFIX_OPERATOR) {
        return new ElixirTwoInfixOperatorImpl(node);
      }
      else if (type == TYPE_INFIX_OPERATOR) {
        return new ElixirTypeInfixOperatorImpl(node);
      }
      else if (type == UNARY_CHAR_TOKEN_OR_NUMBER_OPERATION) {
        return new ElixirUnaryCharTokenOrNumberOperationImpl(node);
      }
      else if (type == UNARY_EXPRESSION_OPERATION) {
        return new ElixirUnaryExpressionOperationImpl(node);
      }
      else if (type == UNARY_EXPRESSION_PREFIX_OPERATION) {
        return new ElixirUnaryExpressionPrefixOperationImpl(node);
      }
      else if (type == UNARY_MATCHED_EXPRESSION_OPERATION) {
        return new ElixirUnaryMatchedExpressionOperationImpl(node);
      }
      else if (type == UNARY_MATCHED_EXPRESSION_PREFIX_OPERATION) {
        return new ElixirUnaryMatchedExpressionPrefixOperationImpl(node);
      }
      else if (type == UNARY_PREFIX_OPERATOR) {
        return new ElixirUnaryPrefixOperatorImpl(node);
      }
      else if (type == UNKNOWN_BASE_WHOLE_NUMBER) {
        return new ElixirUnknownBaseWholeNumberImpl(node);
      }
      else if (type == WHEN_INFIX_OPERATOR) {
        return new ElixirWhenInfixOperatorImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
