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

  public void visitAtMatchedExpressionOperation(@NotNull ElixirAtMatchedExpressionOperation o) {
    visitUnaryMatchedExpressionOperation(o);
  }

  public void visitAtPrefixOperator(@NotNull ElixirAtPrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitAtTailExpressionOperation(@NotNull ElixirAtTailExpressionOperation o) {
    visitUnaryTailExpressionOperation(o);
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

  public void visitCaptureMatchedExpressionOperation(@NotNull ElixirCaptureMatchedExpressionOperation o) {
    visitPsiElement(o);
  }

  public void visitCaptureMatchedExpressionPrefixOperation(@NotNull ElixirCaptureMatchedExpressionPrefixOperation o) {
    visitPsiElement(o);
  }

  public void visitCapturePrefixOperator(@NotNull ElixirCapturePrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitCaptureTailExpressionOperation(@NotNull ElixirCaptureTailExpressionOperation o) {
    visitPsiElement(o);
  }

  public void visitCaptureTailExpressionPrefixOperation(@NotNull ElixirCaptureTailExpressionPrefixOperation o) {
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

  public void visitMatchedExpressionAdditionMatchedExpressionOperation(@NotNull ElixirMatchedExpressionAdditionMatchedExpressionOperation o) {
    visitMatchedExpressionTwoMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionAdditionTailExpressionOperation(@NotNull ElixirMatchedExpressionAdditionTailExpressionOperation o) {
    visitMatchedExpressionTwoTailExpressionOperation(o);
  }

  public void visitMatchedExpressionAndMatchedExpressionOperation(@NotNull ElixirMatchedExpressionAndMatchedExpressionOperation o) {
    visitMatchedExpressionOrMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionAndTailExpressionOperation(@NotNull ElixirMatchedExpressionAndTailExpressionOperation o) {
    visitMatchedExpressionOrTailExpressionOperation(o);
  }

  public void visitMatchedExpressionArrowMatchedExpressionOperation(@NotNull ElixirMatchedExpressionArrowMatchedExpressionOperation o) {
    visitMatchedExpressionRelationalMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionArrowTailExpressionOperation(@NotNull ElixirMatchedExpressionArrowTailExpressionOperation o) {
    visitMatchedExpressionRelationalTailExpressionOperation(o);
  }

  public void visitMatchedExpressionComparisonMatchedExpressionOperation(@NotNull ElixirMatchedExpressionComparisonMatchedExpressionOperation o) {
    visitMatchedExpressionAndMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionComparisonTailExpressionOperation(@NotNull ElixirMatchedExpressionComparisonTailExpressionOperation o) {
    visitMatchedExpressionAndTailExpressionOperation(o);
  }

  public void visitMatchedExpressionHatMatchedExpressionOperation(@NotNull ElixirMatchedExpressionHatMatchedExpressionOperation o) {
    visitMatchedExpressionMultiplicationMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionHatTailExpressionOperation(@NotNull ElixirMatchedExpressionHatTailExpressionOperation o) {
    visitMatchedExpressionMultiplicationTailExpressionOperation(o);
  }

  public void visitMatchedExpressionInMatchMatchedExpressionOperation(@NotNull ElixirMatchedExpressionInMatchMatchedExpressionOperation o) {
    visitCaptureMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionInMatchTailExpressionOperation(@NotNull ElixirMatchedExpressionInMatchTailExpressionOperation o) {
    visitCaptureTailExpressionOperation(o);
  }

  public void visitMatchedExpressionInMatchedExpressionOperation(@NotNull ElixirMatchedExpressionInMatchedExpressionOperation o) {
    visitMatchedExpressionArrowMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionInTailExpressionOperation(@NotNull ElixirMatchedExpressionInTailExpressionOperation o) {
    visitMatchedExpressionArrowTailExpressionOperation(o);
  }

  public void visitMatchedExpressionMatchMatchedExpressionOperation(@NotNull ElixirMatchedExpressionMatchMatchedExpressionOperation o) {
    visitMatchedExpressionPipeMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionMatchTailExpressionOperation(@NotNull ElixirMatchedExpressionMatchTailExpressionOperation o) {
    visitMatchedExpressionPipeTailExpressionOperation(o);
  }

  public void visitMatchedExpressionMultiplicationMatchedExpressionOperation(@NotNull ElixirMatchedExpressionMultiplicationMatchedExpressionOperation o) {
    visitMatchedExpressionAdditionMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionMultiplicationTailExpressionOperation(@NotNull ElixirMatchedExpressionMultiplicationTailExpressionOperation o) {
    visitMatchedExpressionAdditionTailExpressionOperation(o);
  }

  public void visitMatchedExpressionOrMatchedExpressionOperation(@NotNull ElixirMatchedExpressionOrMatchedExpressionOperation o) {
    visitMatchedExpressionMatchMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionOrTailExpressionOperation(@NotNull ElixirMatchedExpressionOrTailExpressionOperation o) {
    visitMatchedExpressionMatchTailExpressionOperation(o);
  }

  public void visitMatchedExpressionPipeMatchedExpressionOperation(@NotNull ElixirMatchedExpressionPipeMatchedExpressionOperation o) {
    visitMatchedExpressionTypeMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionPipeTailExpressionOperation(@NotNull ElixirMatchedExpressionPipeTailExpressionOperation o) {
    visitMatchedExpressionTypeTailExpressionOperation(o);
  }

  public void visitMatchedExpressionRelationalMatchedExpressionOperation(@NotNull ElixirMatchedExpressionRelationalMatchedExpressionOperation o) {
    visitMatchedExpressionComparisonMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionRelationalTailExpressionOperation(@NotNull ElixirMatchedExpressionRelationalTailExpressionOperation o) {
    visitMatchedExpressionComparisonTailExpressionOperation(o);
  }

  public void visitMatchedExpressionTwoMatchedExpressionOperation(@NotNull ElixirMatchedExpressionTwoMatchedExpressionOperation o) {
    visitMatchedExpressionInMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionTwoTailExpressionOperation(@NotNull ElixirMatchedExpressionTwoTailExpressionOperation o) {
    visitMatchedExpressionInTailExpressionOperation(o);
  }

  public void visitMatchedExpressionTypeMatchedExpressionOperation(@NotNull ElixirMatchedExpressionTypeMatchedExpressionOperation o) {
    visitMatchedExpressionWhenMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionTypeTailExpressionOperation(@NotNull ElixirMatchedExpressionTypeTailExpressionOperation o) {
    visitMatchedExpressionWhenTailExpressionOperation(o);
  }

  public void visitMatchedExpressionWhenMatchedExpressionOperation(@NotNull ElixirMatchedExpressionWhenMatchedExpressionOperation o) {
    visitMatchedExpressionInMatchMatchedExpressionOperation(o);
  }

  public void visitMatchedExpressionWhenTailExpressionOperation(@NotNull ElixirMatchedExpressionWhenTailExpressionOperation o) {
    visitMatchedExpressionInMatchTailExpressionOperation(o);
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
    visitAtTailExpressionOperation(o);
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

  public void visitUnaryMatchedExpressionOperation(@NotNull ElixirUnaryMatchedExpressionOperation o) {
    visitMatchedExpressionHatMatchedExpressionOperation(o);
  }

  public void visitUnaryMatchedExpressionPrefixOperation(@NotNull ElixirUnaryMatchedExpressionPrefixOperation o) {
    visitPsiElement(o);
  }

  public void visitUnaryPrefixOperator(@NotNull ElixirUnaryPrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitUnaryTailExpressionOperation(@NotNull ElixirUnaryTailExpressionOperation o) {
    visitMatchedExpressionHatTailExpressionOperation(o);
  }

  public void visitUnaryTailExpressionPrefixOperation(@NotNull ElixirUnaryTailExpressionPrefixOperation o) {
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
