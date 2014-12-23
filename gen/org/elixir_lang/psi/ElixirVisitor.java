// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ElixirVisitor extends PsiElementVisitor {

  public void visitAccessExpression(@NotNull ElixirAccessExpression o) {
    visitPsiElement(o);
  }

  public void visitAdditionInfixOperator(@NotNull ElixirAdditionInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitAdjacentExpression(@NotNull ElixirAdjacentExpression o) {
    visitPsiElement(o);
  }

  public void visitAndInfixOperator(@NotNull ElixirAndInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitArrowInfixOperator(@NotNull ElixirArrowInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitAtCharTokenOrNumberOperation(@NotNull ElixirAtCharTokenOrNumberOperation o) {
    visitPsiElement(o);
  }

  public void visitAtExpressionOperation(@NotNull ElixirAtExpressionOperation o) {
    visitUnaryExpressionOperation(o);
  }

  public void visitAtExpressionPrefixOperation(@NotNull ElixirAtExpressionPrefixOperation o) {
    visitAtExpressionOperation(o);
  }

  public void visitAtMatchedExpressionOperation(@NotNull ElixirAtMatchedExpressionOperation o) {
    visitAtExpressionOperation(o);
  }

  public void visitAtMatchedExpressionPrefixOperation(@NotNull ElixirAtMatchedExpressionPrefixOperation o) {
    visitAtMatchedExpressionOperation(o);
  }

  public void visitAtPrefixOperator(@NotNull ElixirAtPrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitAtom(@NotNull ElixirAtom o) {
    visitMaxExpression(o);
  }

  public void visitBinaryWholeNumber(@NotNull ElixirBinaryWholeNumber o) {
    visitNumber(o);
  }

  public void visitCallArgumentsNoParenthesesKeywords(@NotNull ElixirCallArgumentsNoParenthesesKeywords o) {
    visitPsiElement(o);
  }

  public void visitCallArgumentsNoParenthesesKeywordsExpression(@NotNull ElixirCallArgumentsNoParenthesesKeywordsExpression o) {
    visitPsiElement(o);
  }

  public void visitCallArgumentsNoParenthesesMany(@NotNull ElixirCallArgumentsNoParenthesesMany o) {
    visitPsiElement(o);
  }

  public void visitCaptureCharTokenOrNumberOperation(@NotNull ElixirCaptureCharTokenOrNumberOperation o) {
    visitPsiElement(o);
  }

  public void visitCaptureExpressionOperation(@NotNull ElixirCaptureExpressionOperation o) {
    visitPsiElement(o);
  }

  public void visitCaptureExpressionPrefixOperation(@NotNull ElixirCaptureExpressionPrefixOperation o) {
    visitPsiElement(o);
  }

  public void visitCaptureMatchedExpressionOperation(@NotNull ElixirCaptureMatchedExpressionOperation o) {
    visitCaptureExpressionOperation(o);
  }

  public void visitCaptureMatchedExpressionPrefixOperation(@NotNull ElixirCaptureMatchedExpressionPrefixOperation o) {
    visitCaptureMatchedExpressionOperation(o);
  }

  public void visitCapturePrefixOperator(@NotNull ElixirCapturePrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitCharList(@NotNull ElixirCharList o) {
    visitPsiElement(o);
  }

  public void visitCharListHeredoc(@NotNull ElixirCharListHeredoc o) {
    visitPsiElement(o);
  }

  public void visitComparisonInfixOperator(@NotNull ElixirComparisonInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitDecimalFloat(@NotNull ElixirDecimalFloat o) {
    visitDecimalNumber(o);
  }

  public void visitDecimalNumber(@NotNull ElixirDecimalNumber o) {
    visitNumber(o);
  }

  public void visitDecimalWholeNumber(@NotNull ElixirDecimalWholeNumber o) {
    visitNumber(o);
  }

  public void visitEmptyParentheses(@NotNull ElixirEmptyParentheses o) {
    visitPsiElement(o);
  }

  public void visitEndOfExpression(@NotNull ElixirEndOfExpression o) {
    visitPsiElement(o);
  }

  public void visitHatInfixOperator(@NotNull ElixirHatInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitHexadecimalWholeNumber(@NotNull ElixirHexadecimalWholeNumber o) {
    visitNumber(o);
  }

  public void visitInInfixOperator(@NotNull ElixirInInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitInMatchInfixOperator(@NotNull ElixirInMatchInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitInfixDotOperator(@NotNull ElixirInfixDotOperator o) {
    visitPsiElement(o);
  }

  public void visitInterpolation(@NotNull ElixirInterpolation o) {
    visitPsiElement(o);
  }

  public void visitKeywordKey(@NotNull ElixirKeywordKey o) {
    visitPsiElement(o);
  }

  public void visitKeywordPair(@NotNull ElixirKeywordPair o) {
    visitPsiElement(o);
  }

  public void visitKeywordValue(@NotNull ElixirKeywordValue o) {
    visitPsiElement(o);
  }

  public void visitList(@NotNull ElixirList o) {
    visitPsiElement(o);
  }

  public void visitMatchInfixOperator(@NotNull ElixirMatchInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitMatchedExpression(@NotNull ElixirMatchedExpression o) {
    visitAtMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionAdditionExpressionOperation(@NotNull ElixirMatchedExpressionAdditionExpressionOperation o) {
    visitMatchedExpressionTwoExpressionOperation(o);
  }

  public void visitMatchedExpressionAdditionMatchedExpressionOperation(@NotNull ElixirMatchedExpressionAdditionMatchedExpressionOperation o) {
    visitMatchedExpressionAdditionExpressionOperation(o);
  }

  public void visitMatchedExpressionAndExpressionOperation(@NotNull ElixirMatchedExpressionAndExpressionOperation o) {
    visitMatchedExpressionOrExpressionOperation(o);
  }

  public void visitMatchedExpressionAndMatchedExpressionOperation(@NotNull ElixirMatchedExpressionAndMatchedExpressionOperation o) {
    visitMatchedExpressionAndExpressionOperation(o);
  }

  public void visitMatchedExpressionArrowExpressionOperation(@NotNull ElixirMatchedExpressionArrowExpressionOperation o) {
    visitMatchedExpressionRelationalExpressionOperation(o);
  }

  public void visitMatchedExpressionArrowMatchedExpressionOperation(@NotNull ElixirMatchedExpressionArrowMatchedExpressionOperation o) {
    visitMatchedExpressionArrowExpressionOperation(o);
  }

  public void visitMatchedExpressionComparisonExpressionOperation(@NotNull ElixirMatchedExpressionComparisonExpressionOperation o) {
    visitMatchedExpressionAndExpressionOperation(o);
  }

  public void visitMatchedExpressionComparisonMatchedExpressionOperation(@NotNull ElixirMatchedExpressionComparisonMatchedExpressionOperation o) {
    visitMatchedExpressionComparisonExpressionOperation(o);
  }

  public void visitMatchedExpressionHatExpressionOperation(@NotNull ElixirMatchedExpressionHatExpressionOperation o) {
    visitMatchedExpressionMultiplicationExpressionOperation(o);
  }

  public void visitMatchedExpressionHatMatchedExpressionOperation(@NotNull ElixirMatchedExpressionHatMatchedExpressionOperation o) {
    visitMatchedExpressionHatExpressionOperation(o);
  }

  public void visitMatchedExpressionInExpressionOperation(@NotNull ElixirMatchedExpressionInExpressionOperation o) {
    visitMatchedExpressionArrowExpressionOperation(o);
  }

  public void visitMatchedExpressionInMatchExpressionOperation(@NotNull ElixirMatchedExpressionInMatchExpressionOperation o) {
    visitCaptureExpressionOperation(o);
  }

  public void visitMatchedExpressionInMatchMatchedExpressionOperation(@NotNull ElixirMatchedExpressionInMatchMatchedExpressionOperation o) {
    visitMatchedExpressionInMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionInMatchedExpressionOperation(@NotNull ElixirMatchedExpressionInMatchedExpressionOperation o) {
    visitMatchedExpressionInExpressionOperation(o);
  }

  public void visitMatchedExpressionMatchExpressionOperation(@NotNull ElixirMatchedExpressionMatchExpressionOperation o) {
    visitMatchedExpressionPipeExpressionOperation(o);
  }

  public void visitMatchedExpressionMatchMatchedExpressionOperation(@NotNull ElixirMatchedExpressionMatchMatchedExpressionOperation o) {
    visitMatchedExpressionMatchExpressionOperation(o);
  }

  public void visitMatchedExpressionMultiplicationExpressionOperation(@NotNull ElixirMatchedExpressionMultiplicationExpressionOperation o) {
    visitMatchedExpressionAdditionExpressionOperation(o);
  }

  public void visitMatchedExpressionMultiplicationMatchedExpressionOperation(@NotNull ElixirMatchedExpressionMultiplicationMatchedExpressionOperation o) {
    visitMatchedExpressionMultiplicationExpressionOperation(o);
  }

  public void visitMatchedExpressionOrExpressionOperation(@NotNull ElixirMatchedExpressionOrExpressionOperation o) {
    visitMatchedExpressionMatchExpressionOperation(o);
  }

  public void visitMatchedExpressionOrMatchedExpressionOperation(@NotNull ElixirMatchedExpressionOrMatchedExpressionOperation o) {
    visitMatchedExpressionOrExpressionOperation(o);
  }

  public void visitMatchedExpressionPipeExpressionOperation(@NotNull ElixirMatchedExpressionPipeExpressionOperation o) {
    visitMatchedExpressionTypeExpressionOperation(o);
  }

  public void visitMatchedExpressionPipeMatchedExpressionOperation(@NotNull ElixirMatchedExpressionPipeMatchedExpressionOperation o) {
    visitMatchedExpressionPipeExpressionOperation(o);
  }

  public void visitMatchedExpressionRelationalExpressionOperation(@NotNull ElixirMatchedExpressionRelationalExpressionOperation o) {
    visitMatchedExpressionComparisonExpressionOperation(o);
  }

  public void visitMatchedExpressionRelationalMatchedExpressionOperation(@NotNull ElixirMatchedExpressionRelationalMatchedExpressionOperation o) {
    visitMatchedExpressionRelationalExpressionOperation(o);
  }

  public void visitMatchedExpressionTwoExpressionOperation(@NotNull ElixirMatchedExpressionTwoExpressionOperation o) {
    visitMatchedExpressionInExpressionOperation(o);
  }

  public void visitMatchedExpressionTwoMatchedExpressionOperation(@NotNull ElixirMatchedExpressionTwoMatchedExpressionOperation o) {
    visitMatchedExpressionTwoExpressionOperation(o);
  }

  public void visitMatchedExpressionTypeExpressionOperation(@NotNull ElixirMatchedExpressionTypeExpressionOperation o) {
    visitMatchedExpressionWhenExpressionOperation(o);
  }

  public void visitMatchedExpressionTypeMatchedExpressionOperation(@NotNull ElixirMatchedExpressionTypeMatchedExpressionOperation o) {
    visitMatchedExpressionTypeExpressionOperation(o);
  }

  public void visitMatchedExpressionWhenExpressionOperation(@NotNull ElixirMatchedExpressionWhenExpressionOperation o) {
    visitMatchedExpressionInMatchExpressionOperation(o);
  }

  public void visitMatchedExpressionWhenMatchedExpressionOperation(@NotNull ElixirMatchedExpressionWhenMatchedExpressionOperation o) {
    visitMatchedExpressionWhenExpressionOperation(o);
  }

  public void visitMaxExpression(@NotNull ElixirMaxExpression o) {
    visitPsiElement(o);
  }

  public void visitMultiplicationInfixOperator(@NotNull ElixirMultiplicationInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesCall(@NotNull ElixirNoParenthesesCall o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesManyStrictNoParenthesesExpression(@NotNull ElixirNoParenthesesManyStrictNoParenthesesExpression o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesOneExpression(@NotNull ElixirNoParenthesesOneExpression o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesStrict(@NotNull ElixirNoParenthesesStrict o) {
    visitPsiElement(o);
  }

  public void visitNumber(@NotNull ElixirNumber o) {
    visitPsiElement(o);
  }

  public void visitOctalWholeNumber(@NotNull ElixirOctalWholeNumber o) {
    visitNumber(o);
  }

  public void visitOrInfixOperator(@NotNull ElixirOrInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitPipeInfixOperator(@NotNull ElixirPipeInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitQualifiedAlias(@NotNull ElixirQualifiedAlias o) {
    visitPsiElement(o);
  }

  public void visitQualifiedIdentifier(@NotNull ElixirQualifiedIdentifier o) {
    visitPsiElement(o);
  }

  public void visitRelationalInfixOperator(@NotNull ElixirRelationalInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitSigil(@NotNull ElixirSigil o) {
    visitPsiElement(o);
  }

  public void visitString(@NotNull ElixirString o) {
    visitPsiElement(o);
  }

  public void visitStringHeredoc(@NotNull ElixirStringHeredoc o) {
    visitPsiElement(o);
  }

  public void visitTailExpression(@NotNull ElixirTailExpression o) {
    visitAtExpressionOperation(o);
  }

  public void visitTwoInfixOperator(@NotNull ElixirTwoInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitTypeInfixOperator(@NotNull ElixirTypeInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitUnaryCharTokenOrNumberOperation(@NotNull ElixirUnaryCharTokenOrNumberOperation o) {
    visitPsiElement(o);
  }

  public void visitUnaryExpressionOperation(@NotNull ElixirUnaryExpressionOperation o) {
    visitMatchedExpressionHatExpressionOperation(o);
  }

  public void visitUnaryExpressionPrefixOperation(@NotNull ElixirUnaryExpressionPrefixOperation o) {
    visitUnaryExpressionOperation(o);
  }

  public void visitUnaryMatchedExpressionOperation(@NotNull ElixirUnaryMatchedExpressionOperation o) {
    visitUnaryExpressionOperation(o);
  }

  public void visitUnaryMatchedExpressionPrefixOperation(@NotNull ElixirUnaryMatchedExpressionPrefixOperation o) {
    visitUnaryMatchedExpressionOperation(o);
  }

  public void visitUnaryPrefixOperator(@NotNull ElixirUnaryPrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitUnknownBaseWholeNumber(@NotNull ElixirUnknownBaseWholeNumber o) {
    visitNumber(o);
  }

  public void visitWhenInfixOperator(@NotNull ElixirWhenInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
