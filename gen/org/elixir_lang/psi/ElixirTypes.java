// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.impl.*;

public interface ElixirTypes {

  IElementType ACCESS_EXPRESSION = new ElixirElementType("ACCESS_EXPRESSION");
  IElementType ADD_OPERATOR_EOL = new ElixirElementType("ADD_OPERATOR_EOL");
  IElementType AND_OPERATOR_EOL = new ElixirElementType("AND_OPERATOR_EOL");
  IElementType ARROW_OPERATOR_EOL = new ElixirElementType("ARROW_OPERATOR_EOL");
  IElementType ASSOC = new ElixirElementType("ASSOC");
  IElementType ASSOC_BASE = new ElixirElementType("ASSOC_BASE");
  IElementType ASSOC_EXPRESSION = new ElixirElementType("ASSOC_EXPRESSION");
  IElementType ASSOC_OPERATOR_EOL = new ElixirElementType("ASSOC_OPERATOR_EOL");
  IElementType ASSOC_UPDATE = new ElixirElementType("ASSOC_UPDATE");
  IElementType ASSOC_UPDATE_KEYWORD = new ElixirElementType("ASSOC_UPDATE_KEYWORD");
  IElementType AT_OPERATOR_EOL = new ElixirElementType("AT_OPERATOR_EOL");
  IElementType BIT_STRING = new ElixirElementType("BIT_STRING");
  IElementType BLOCK_EOL = new ElixirElementType("BLOCK_EOL");
  IElementType BLOCK_EXPRESSION = new ElixirElementType("BLOCK_EXPRESSION");
  IElementType BLOCK_ITEM = new ElixirElementType("BLOCK_ITEM");
  IElementType BLOCK_LIST = new ElixirElementType("BLOCK_LIST");
  IElementType BRACKET_ARGUMENT = new ElixirElementType("BRACKET_ARGUMENT");
  IElementType BRACKET_AT_EXPRESSION = new ElixirElementType("BRACKET_AT_EXPRESSION");
  IElementType BRACKET_EXPRESSION = new ElixirElementType("BRACKET_EXPRESSION");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_ALL = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_ALL");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_COMMA_EXPRESSION = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_COMMA_EXPRESSION");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD_EXPRESSION = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD_EXPRESSION");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_MANY = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_MANY");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_MANY_STRICT = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_MANY_STRICT");
  IElementType CALL_ARGUMENTS_NO_PARENTHESES_ONE = new ElixirElementType("CALL_ARGUMENTS_NO_PARENTHESES_ONE");
  IElementType CALL_ARGUMENTS_PARENTHESES = new ElixirElementType("CALL_ARGUMENTS_PARENTHESES");
  IElementType CALL_ARGUMENTS_PARENTHESES_BASE = new ElixirElementType("CALL_ARGUMENTS_PARENTHESES_BASE");
  IElementType CALL_ARGUMENTS_PARENTHESES_EXPRESSION = new ElixirElementType("CALL_ARGUMENTS_PARENTHESES_EXPRESSION");
  IElementType CAPTURE_OPERATOR_EOL = new ElixirElementType("CAPTURE_OPERATOR_EOL");
  IElementType CLOSE_BIT = new ElixirElementType("CLOSE_BIT");
  IElementType CLOSE_BRACKET = new ElixirElementType("CLOSE_BRACKET");
  IElementType CLOSE_CURLY = new ElixirElementType("CLOSE_CURLY");
  IElementType CLOSE_PARENTHESIS = new ElixirElementType("CLOSE_PARENTHESIS");
  IElementType COMP_OPERATOR_EOL = new ElixirElementType("COMP_OPERATOR_EOL");
  IElementType CONTAINER_ARGUMENTS = new ElixirElementType("CONTAINER_ARGUMENTS");
  IElementType CONTAINER_ARGUMENTS_BASE = new ElixirElementType("CONTAINER_ARGUMENTS_BASE");
  IElementType CONTAINER_EXPRESSION = new ElixirElementType("CONTAINER_EXPRESSION");
  IElementType DOT_ALIAS = new ElixirElementType("DOT_ALIAS");
  IElementType DOT_BRACKET_IDENTIFIER = new ElixirElementType("DOT_BRACKET_IDENTIFIER");
  IElementType DOT_DO_IDENTIFIER = new ElixirElementType("DOT_DO_IDENTIFIER");
  IElementType DOT_IDENTIFIER = new ElixirElementType("DOT_IDENTIFIER");
  IElementType DOT_OPERATOR = new ElixirElementType("DOT_OPERATOR");
  IElementType DOT_OPERATOR_IDENTIFIER = new ElixirElementType("DOT_OPERATOR_IDENTIFIER");
  IElementType DOT_PARENTHESES_IDENTIFIER = new ElixirElementType("DOT_PARENTHESES_IDENTIFIER");
  IElementType DO_BLOCK = new ElixirElementType("DO_BLOCK");
  IElementType DO_EOL = new ElixirElementType("DO_EOL");
  IElementType EMPTY_PARENTHESES = new ElixirElementType("EMPTY_PARENTHESES");
  IElementType END_EOL = new ElixirElementType("END_EOL");
  IElementType EXPRESSION = new ElixirElementType("EXPRESSION");
  IElementType EXPRESSION_LIST = new ElixirElementType("EXPRESSION_LIST");
  IElementType FN_EOL = new ElixirElementType("FN_EOL");
  IElementType HAT_OPERATOR_EOL = new ElixirElementType("HAT_OPERATOR_EOL");
  IElementType IN_MATCH_OPERATOR_EOL = new ElixirElementType("IN_MATCH_OPERATOR_EOL");
  IElementType IN_OPERATOR_EOL = new ElixirElementType("IN_OPERATOR_EOL");
  IElementType KEYWORD = new ElixirElementType("KEYWORD");
  IElementType KEYWORD_BASE = new ElixirElementType("KEYWORD_BASE");
  IElementType KEYWORD_EOL = new ElixirElementType("KEYWORD_EOL");
  IElementType LIST = new ElixirElementType("LIST");
  IElementType LIST_ARGUMENTS = new ElixirElementType("LIST_ARGUMENTS");
  IElementType MAP = new ElixirElementType("MAP");
  IElementType MAP_ARGUMENTS = new ElixirElementType("MAP_ARGUMENTS");
  IElementType MAP_CLOSE = new ElixirElementType("MAP_CLOSE");
  IElementType MAP_EXPRESSION = new ElixirElementType("MAP_EXPRESSION");
  IElementType MAP_OPERATOR = new ElixirElementType("MAP_OPERATOR");
  IElementType MATCHED_EXPRESSION = new ElixirElementType("MATCHED_EXPRESSION");
  IElementType MATCHED_OPERATOR_EXPRESSION = new ElixirElementType("MATCHED_OPERATOR_EXPRESSION");
  IElementType MATCH_OPERATOR_EOL = new ElixirElementType("MATCH_OPERATOR_EOL");
  IElementType MAX_EXPRESSION = new ElixirElementType("MAX_EXPRESSION");
  IElementType MULTIPLY_OPERATOR_EOL = new ElixirElementType("MULTIPLY_OPERATOR_EOL");
  IElementType NO_PARENTHESES_EXPRESSION = new ElixirElementType("NO_PARENTHESES_EXPRESSION");
  IElementType NO_PARENTHESES_ONE_EXPRESSION = new ElixirElementType("NO_PARENTHESES_ONE_EXPRESSION");
  IElementType NO_PARENTHESES_OPERATOR_EXPRESSION = new ElixirElementType("NO_PARENTHESES_OPERATOR_EXPRESSION");
  IElementType OPEN_BIT = new ElixirElementType("OPEN_BIT");
  IElementType OPEN_BRACKET = new ElixirElementType("OPEN_BRACKET");
  IElementType OPEN_CURLY = new ElixirElementType("OPEN_CURLY");
  IElementType OPEN_PARENTHESIS = new ElixirElementType("OPEN_PARENTHESIS");
  IElementType OPERATOR_EOL = new ElixirElementType("OPERATOR_EOL");
  IElementType OPERATOR_EXPRESSION = new ElixirElementType("OPERATOR_EXPRESSION");
  IElementType OR_OPERATOR_EOL = new ElixirElementType("OR_OPERATOR_EOL");
  IElementType PARENTHESES_CALL = new ElixirElementType("PARENTHESES_CALL");
  IElementType PIPE_OPERATOR_EOL = new ElixirElementType("PIPE_OPERATOR_EOL");
  IElementType REL_OPERATOR_EOL = new ElixirElementType("REL_OPERATOR_EOL");
  IElementType STAB = new ElixirElementType("STAB");
  IElementType STAB_EOL = new ElixirElementType("STAB_EOL");
  IElementType STAB_MAYBE_EXPRESSION = new ElixirElementType("STAB_MAYBE_EXPRESSION");
  IElementType STAB_OPERATOR_EOL = new ElixirElementType("STAB_OPERATOR_EOL");
  IElementType STAB_PARENTHESES_MANY = new ElixirElementType("STAB_PARENTHESES_MANY");
  IElementType STRUCT_OPERATOR = new ElixirElementType("STRUCT_OPERATOR");
  IElementType TUPLE = new ElixirElementType("TUPLE");
  IElementType TWO_OPERATOR_EOL = new ElixirElementType("TWO_OPERATOR_EOL");
  IElementType TYPE_OPERATOR_EOL = new ElixirElementType("TYPE_OPERATOR_EOL");
  IElementType UNARY_OPERATOR_EOL = new ElixirElementType("UNARY_OPERATOR_EOL");
  IElementType UNMATCHED_EXPRESSION = new ElixirElementType("UNMATCHED_EXPRESSION");
  IElementType WHEN_OPERATOR_EOL = new ElixirElementType("WHEN_OPERATOR_EOL");

  IElementType ADDOPERATOR = new ElixirTokenType("addOperator");
  IElementType ANDOPERATOR = new ElixirTokenType("andOperator");
  IElementType ARROWOPERATOR = new ElixirTokenType("arrowOperator");
  IElementType ASSOCOPERATOR = new ElixirTokenType("assocOperator");
  IElementType ATOM = new ElixirTokenType("atom");
  IElementType ATOM_SAFE = new ElixirTokenType("atom_safe");
  IElementType ATOM_UNSAFE = new ElixirTokenType("atom_unsafe");
  IElementType ATOPERATOR = new ElixirTokenType("atOperator");
  IElementType BINARYSTRING = new ElixirTokenType("binaryString");
  IElementType BLOCKIDENTIFIER = new ElixirTokenType("blockIdentifier");
  IElementType BRACKETIDENTIFIER = new ElixirTokenType("bracketIdentifier");
  IElementType CALLARGUMENTSNOPARENTHESESEXPRESSION = new ElixirTokenType("callArgumentsNoParenthesesExpression");
  IElementType CAPTUREOPERATOR = new ElixirTokenType("captureOperator");
  IElementType COMPOPERATOR = new ElixirTokenType("compOperator");
  IElementType DOIDENTIFIER = new ElixirTokenType("doIdentifier");
  IElementType DOTCALLOPERATOR = new ElixirTokenType("dotCallOperator");
  IElementType DUALOPERATOR = new ElixirTokenType("dualOperator");
  IElementType EOL = new ElixirTokenType("EOL");
  IElementType HATOPERATOR = new ElixirTokenType("hatOperator");
  IElementType IDENTIFIER = new ElixirTokenType("identifier");
  IElementType INMATCHOPERATOR = new ElixirTokenType("inMatchOperator");
  IElementType INOPERATOR = new ElixirTokenType("inOperator");
  IElementType KEYWORDIDENTIFIER = new ElixirTokenType("keywordIdentifier");
  IElementType KEYWORDIDENTIFIERSAFE = new ElixirTokenType("keywordIdentifierSafe");
  IElementType LISTSTRING = new ElixirTokenType("listString");
  IElementType MATCHOPERATOR = new ElixirTokenType("matchOperator");
  IElementType MULTIPLYOPERATOR = new ElixirTokenType("multiplyOperator");
  IElementType NUMBER = new ElixirTokenType("number");
  IElementType OPERATORIDENTIFIER = new ElixirTokenType("operatorIdentifier");
  IElementType OROPERATOR = new ElixirTokenType("orOperator");
  IElementType PARENTHESESIDENTIFIER = new ElixirTokenType("parenthesesIdentifier");
  IElementType PIPEOPERATOR = new ElixirTokenType("pipeOperator");
  IElementType RELOPERATOR = new ElixirTokenType("relOperator");
  IElementType SIGIL = new ElixirTokenType("sigil");
  IElementType SIGNED_NUMBER = new ElixirTokenType("signed_number");
  IElementType STABEXPRESSION = new ElixirTokenType("stabExpression");
  IElementType STABOPERATOR = new ElixirTokenType("stabOperator");
  IElementType TWOOPERATOR = new ElixirTokenType("twoOperator");
  IElementType UNARYOPERATOR = new ElixirTokenType("unaryOperator");
  IElementType WHENOPERATOR = new ElixirTokenType("whenOperator");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ACCESS_EXPRESSION) {
        return new ElixirAccessExpressionImpl(node);
      }
      else if (type == ADD_OPERATOR_EOL) {
        return new ElixirAddOperatorEOLImpl(node);
      }
      else if (type == AND_OPERATOR_EOL) {
        return new ElixirAndOperatorEOLImpl(node);
      }
      else if (type == ARROW_OPERATOR_EOL) {
        return new ElixirArrowOperatorEOLImpl(node);
      }
      else if (type == ASSOC) {
        return new ElixirAssocImpl(node);
      }
      else if (type == ASSOC_BASE) {
        return new ElixirAssocBaseImpl(node);
      }
      else if (type == ASSOC_EXPRESSION) {
        return new ElixirAssocExpressionImpl(node);
      }
      else if (type == ASSOC_OPERATOR_EOL) {
        return new ElixirAssocOperatorEOLImpl(node);
      }
      else if (type == ASSOC_UPDATE) {
        return new ElixirAssocUpdateImpl(node);
      }
      else if (type == ASSOC_UPDATE_KEYWORD) {
        return new ElixirAssocUpdateKeywordImpl(node);
      }
      else if (type == AT_OPERATOR_EOL) {
        return new ElixirAtOperatorEOLImpl(node);
      }
      else if (type == BIT_STRING) {
        return new ElixirBitStringImpl(node);
      }
      else if (type == BLOCK_EOL) {
        return new ElixirBlockEOLImpl(node);
      }
      else if (type == BLOCK_EXPRESSION) {
        return new ElixirBlockExpressionImpl(node);
      }
      else if (type == BLOCK_ITEM) {
        return new ElixirBlockItemImpl(node);
      }
      else if (type == BLOCK_LIST) {
        return new ElixirBlockListImpl(node);
      }
      else if (type == BRACKET_ARGUMENT) {
        return new ElixirBracketArgumentImpl(node);
      }
      else if (type == BRACKET_AT_EXPRESSION) {
        return new ElixirBracketAtExpressionImpl(node);
      }
      else if (type == BRACKET_EXPRESSION) {
        return new ElixirBracketExpressionImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_ALL) {
        return new ElixirCallArgumentsNoParenthesesAllImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_COMMA_EXPRESSION) {
        return new ElixirCallArgumentsNoParenthesesCommaExpressionImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD) {
        return new ElixirCallArgumentsNoParenthesesKeywordImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_KEYWORD_EXPRESSION) {
        return new ElixirCallArgumentsNoParenthesesKeywordExpressionImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_MANY) {
        return new ElixirCallArgumentsNoParenthesesManyImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_MANY_STRICT) {
        return new ElixirCallArgumentsNoParenthesesManyStrictImpl(node);
      }
      else if (type == CALL_ARGUMENTS_NO_PARENTHESES_ONE) {
        return new ElixirCallArgumentsNoParenthesesOneImpl(node);
      }
      else if (type == CALL_ARGUMENTS_PARENTHESES) {
        return new ElixirCallArgumentsParenthesesImpl(node);
      }
      else if (type == CALL_ARGUMENTS_PARENTHESES_BASE) {
        return new ElixirCallArgumentsParenthesesBaseImpl(node);
      }
      else if (type == CALL_ARGUMENTS_PARENTHESES_EXPRESSION) {
        return new ElixirCallArgumentsParenthesesExpressionImpl(node);
      }
      else if (type == CAPTURE_OPERATOR_EOL) {
        return new ElixirCaptureOperatorEOLImpl(node);
      }
      else if (type == CLOSE_BIT) {
        return new ElixirCloseBitImpl(node);
      }
      else if (type == CLOSE_BRACKET) {
        return new ElixirCloseBracketImpl(node);
      }
      else if (type == CLOSE_CURLY) {
        return new ElixirCloseCurlyImpl(node);
      }
      else if (type == CLOSE_PARENTHESIS) {
        return new ElixirCloseParenthesisImpl(node);
      }
      else if (type == COMP_OPERATOR_EOL) {
        return new ElixirCompOperatorEOLImpl(node);
      }
      else if (type == CONTAINER_ARGUMENTS) {
        return new ElixirContainerArgumentsImpl(node);
      }
      else if (type == CONTAINER_ARGUMENTS_BASE) {
        return new ElixirContainerArgumentsBaseImpl(node);
      }
      else if (type == CONTAINER_EXPRESSION) {
        return new ElixirContainerExpressionImpl(node);
      }
      else if (type == DOT_ALIAS) {
        return new ElixirDotAliasImpl(node);
      }
      else if (type == DOT_BRACKET_IDENTIFIER) {
        return new ElixirDotBracketIdentifierImpl(node);
      }
      else if (type == DOT_DO_IDENTIFIER) {
        return new ElixirDotDoIdentifierImpl(node);
      }
      else if (type == DOT_IDENTIFIER) {
        return new ElixirDotIdentifierImpl(node);
      }
      else if (type == DOT_OPERATOR) {
        return new ElixirDotOperatorImpl(node);
      }
      else if (type == DOT_OPERATOR_IDENTIFIER) {
        return new ElixirDotOperatorIdentifierImpl(node);
      }
      else if (type == DOT_PARENTHESES_IDENTIFIER) {
        return new ElixirDotParenthesesIdentifierImpl(node);
      }
      else if (type == DO_BLOCK) {
        return new ElixirDoBlockImpl(node);
      }
      else if (type == DO_EOL) {
        return new ElixirDoEOLImpl(node);
      }
      else if (type == EMPTY_PARENTHESES) {
        return new ElixirEmptyParenthesesImpl(node);
      }
      else if (type == END_EOL) {
        return new ElixirEndEOLImpl(node);
      }
      else if (type == EXPRESSION) {
        return new ElixirExpressionImpl(node);
      }
      else if (type == EXPRESSION_LIST) {
        return new ElixirExpressionListImpl(node);
      }
      else if (type == FN_EOL) {
        return new ElixirFnEOLImpl(node);
      }
      else if (type == HAT_OPERATOR_EOL) {
        return new ElixirHatOperatorEOLImpl(node);
      }
      else if (type == IN_MATCH_OPERATOR_EOL) {
        return new ElixirInMatchOperatorEOLImpl(node);
      }
      else if (type == IN_OPERATOR_EOL) {
        return new ElixirInOperatorEOLImpl(node);
      }
      else if (type == KEYWORD) {
        return new ElixirKeywordImpl(node);
      }
      else if (type == KEYWORD_BASE) {
        return new ElixirKeywordBaseImpl(node);
      }
      else if (type == KEYWORD_EOL) {
        return new ElixirKeywordEOLImpl(node);
      }
      else if (type == LIST) {
        return new ElixirListImpl(node);
      }
      else if (type == LIST_ARGUMENTS) {
        return new ElixirListArgumentsImpl(node);
      }
      else if (type == MAP) {
        return new ElixirMapImpl(node);
      }
      else if (type == MAP_ARGUMENTS) {
        return new ElixirMapArgumentsImpl(node);
      }
      else if (type == MAP_CLOSE) {
        return new ElixirMapCloseImpl(node);
      }
      else if (type == MAP_EXPRESSION) {
        return new ElixirMapExpressionImpl(node);
      }
      else if (type == MAP_OPERATOR) {
        return new ElixirMapOperatorImpl(node);
      }
      else if (type == MATCHED_EXPRESSION) {
        return new ElixirMatchedExpressionImpl(node);
      }
      else if (type == MATCHED_OPERATOR_EXPRESSION) {
        return new ElixirMatchedOperatorExpressionImpl(node);
      }
      else if (type == MATCH_OPERATOR_EOL) {
        return new ElixirMatchOperatorEOLImpl(node);
      }
      else if (type == MAX_EXPRESSION) {
        return new ElixirMaxExpressionImpl(node);
      }
      else if (type == MULTIPLY_OPERATOR_EOL) {
        return new ElixirMultiplyOperatorEOLImpl(node);
      }
      else if (type == NO_PARENTHESES_EXPRESSION) {
        return new ElixirNoParenthesesExpressionImpl(node);
      }
      else if (type == NO_PARENTHESES_ONE_EXPRESSION) {
        return new ElixirNoParenthesesOneExpressionImpl(node);
      }
      else if (type == NO_PARENTHESES_OPERATOR_EXPRESSION) {
        return new ElixirNoParenthesesOperatorExpressionImpl(node);
      }
      else if (type == OPEN_BIT) {
        return new ElixirOpenBitImpl(node);
      }
      else if (type == OPEN_BRACKET) {
        return new ElixirOpenBracketImpl(node);
      }
      else if (type == OPEN_CURLY) {
        return new ElixirOpenCurlyImpl(node);
      }
      else if (type == OPEN_PARENTHESIS) {
        return new ElixirOpenParenthesisImpl(node);
      }
      else if (type == OPERATOR_EOL) {
        return new ElixirOperatorEOLImpl(node);
      }
      else if (type == OPERATOR_EXPRESSION) {
        return new ElixirOperatorExpressionImpl(node);
      }
      else if (type == OR_OPERATOR_EOL) {
        return new ElixirOrOperatorEOLImpl(node);
      }
      else if (type == PARENTHESES_CALL) {
        return new ElixirParenthesesCallImpl(node);
      }
      else if (type == PIPE_OPERATOR_EOL) {
        return new ElixirPipeOperatorEOLImpl(node);
      }
      else if (type == REL_OPERATOR_EOL) {
        return new ElixirRelOperatorEOLImpl(node);
      }
      else if (type == STAB) {
        return new ElixirStabImpl(node);
      }
      else if (type == STAB_EOL) {
        return new ElixirStabEOLImpl(node);
      }
      else if (type == STAB_MAYBE_EXPRESSION) {
        return new ElixirStabMaybeExpressionImpl(node);
      }
      else if (type == STAB_OPERATOR_EOL) {
        return new ElixirStabOperatorEOLImpl(node);
      }
      else if (type == STAB_PARENTHESES_MANY) {
        return new ElixirStabParenthesesManyImpl(node);
      }
      else if (type == STRUCT_OPERATOR) {
        return new ElixirStructOperatorImpl(node);
      }
      else if (type == TUPLE) {
        return new ElixirTupleImpl(node);
      }
      else if (type == TWO_OPERATOR_EOL) {
        return new ElixirTwoOperatorEOLImpl(node);
      }
      else if (type == TYPE_OPERATOR_EOL) {
        return new ElixirTypeOperatorEOLImpl(node);
      }
      else if (type == UNARY_OPERATOR_EOL) {
        return new ElixirUnaryOperatorEOLImpl(node);
      }
      else if (type == UNMATCHED_EXPRESSION) {
        return new ElixirUnmatchedExpressionImpl(node);
      }
      else if (type == WHEN_OPERATOR_EOL) {
        return new ElixirWhenOperatorEOLImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
