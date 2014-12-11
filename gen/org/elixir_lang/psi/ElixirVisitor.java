// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ElixirVisitor extends PsiElementVisitor {

  public void visitAtom(@NotNull ElixirAtom o) {
    visitMatchedExpressionMaxExpression(o);
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

  public void visitCharList(@NotNull ElixirCharList o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitCharListHeredoc(@NotNull ElixirCharListHeredoc o) {
    visitMatchedExpressionAccessExpression(o);
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
    visitExpression(o);
  }

  public void visitEndOfExpression(@NotNull ElixirEndOfExpression o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull ElixirExpression o) {
    visitPsiElement(o);
  }

  public void visitHexadecimalWholeNumber(@NotNull ElixirHexadecimalWholeNumber o) {
    visitNumber(o);
  }

  public void visitIdentifierExpression(@NotNull ElixirIdentifierExpression o) {
    visitMatchedExpression(o);
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
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitMatchedExpression(@NotNull ElixirMatchedExpression o) {
    visitExpression(o);
  }

  public void visitMatchedExpressionAccessExpression(@NotNull ElixirMatchedExpressionAccessExpression o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionAdditionOperation(@NotNull ElixirMatchedExpressionAdditionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionAndOperation(@NotNull ElixirMatchedExpressionAndOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionArrowOperation(@NotNull ElixirMatchedExpressionArrowOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionAtOperation(@NotNull ElixirMatchedExpressionAtOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionCaptureOperation(@NotNull ElixirMatchedExpressionCaptureOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionComparisonOperation(@NotNull ElixirMatchedExpressionComparisonOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionDotAlias(@NotNull ElixirMatchedExpressionDotAlias o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionDotIdentifier(@NotNull ElixirMatchedExpressionDotIdentifier o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionHatOperation(@NotNull ElixirMatchedExpressionHatOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionInMatchOperation(@NotNull ElixirMatchedExpressionInMatchOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionInOperation(@NotNull ElixirMatchedExpressionInOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionMatchOperation(@NotNull ElixirMatchedExpressionMatchOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionMaxExpression(@NotNull ElixirMatchedExpressionMaxExpression o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionMultiplicationOperation(@NotNull ElixirMatchedExpressionMultiplicationOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionOrOperation(@NotNull ElixirMatchedExpressionOrOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionPipeOperation(@NotNull ElixirMatchedExpressionPipeOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionRelationalOperation(@NotNull ElixirMatchedExpressionRelationalOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionTwoOperation(@NotNull ElixirMatchedExpressionTwoOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionTypeOperation(@NotNull ElixirMatchedExpressionTypeOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionUnaryOperation(@NotNull ElixirMatchedExpressionUnaryOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionWhenOperation(@NotNull ElixirMatchedExpressionWhenOperation o) {
    visitMatchedExpression(o);
  }

  public void visitNoParenthesesExpression(@NotNull ElixirNoParenthesesExpression o) {
    visitExpression(o);
  }

  public void visitNoParenthesesManyStrictNoParenthesesExpression(@NotNull ElixirNoParenthesesManyStrictNoParenthesesExpression o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesMaybeQualifiedIdentifier(@NotNull ElixirNoParenthesesMaybeQualifiedIdentifier o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesQualifier(@NotNull ElixirNoParenthesesQualifier o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesQualifierAtOperation(@NotNull ElixirNoParenthesesQualifierAtOperation o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesQualifierNumberAtOperation(@NotNull ElixirNoParenthesesQualifierNumberAtOperation o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesQualifierNumberCaptureOperation(@NotNull ElixirNoParenthesesQualifierNumberCaptureOperation o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesQualifierNumberUnaryOperation(@NotNull ElixirNoParenthesesQualifierNumberUnaryOperation o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesStrict(@NotNull ElixirNoParenthesesStrict o) {
    visitPsiElement(o);
  }

  public void visitNumber(@NotNull ElixirNumber o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitNumberAtOperation(@NotNull ElixirNumberAtOperation o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitNumberUnaryOperation(@NotNull ElixirNumberUnaryOperation o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitOctalWholeNumber(@NotNull ElixirOctalWholeNumber o) {
    visitNumber(o);
  }

  public void visitSigil(@NotNull ElixirSigil o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitString(@NotNull ElixirString o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitStringHeredoc(@NotNull ElixirStringHeredoc o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitUnknownBaseWholeNumber(@NotNull ElixirUnknownBaseWholeNumber o) {
    visitNumber(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
