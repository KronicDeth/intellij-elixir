// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ElixirVisitor extends PsiElementVisitor {

  public void visitAccessExpression(@NotNull ElixirAccessExpression o) {
    visitMatchedExpression(o);
  }

  public void visitAtom(@NotNull ElixirAtom o) {
    visitMaxExpression(o);
  }

  public void visitCharList(@NotNull ElixirCharList o) {
    visitAccessExpression(o);
  }

  public void visitCharListHeredoc(@NotNull ElixirCharListHeredoc o) {
    visitAccessExpression(o);
  }

  public void visitDotAlias(@NotNull ElixirDotAlias o) {
    visitMaxExpression(o);
  }

  public void visitDotIdentifier(@NotNull ElixirDotIdentifier o) {
    visitMatchedExpression(o);
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
    visitAccessExpression(o);
  }

  public void visitMatchedExpression(@NotNull ElixirMatchedExpression o) {
    visitExpression(o);
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

  public void visitMaxExpression(@NotNull ElixirMaxExpression o) {
    visitAccessExpression(o);
  }

  public void visitNoParenthesesOneExpression(@NotNull ElixirNoParenthesesOneExpression o) {
    visitMatchedExpression(o);
  }

  public void visitNumberAtOperation(@NotNull ElixirNumberAtOperation o) {
    visitAccessExpression(o);
  }

  public void visitNumberCaptureOperation(@NotNull ElixirNumberCaptureOperation o) {
    visitAccessExpression(o);
  }

  public void visitNumberUnaryOperation(@NotNull ElixirNumberUnaryOperation o) {
    visitAccessExpression(o);
  }

  public void visitSigil(@NotNull ElixirSigil o) {
    visitAccessExpression(o);
  }

  public void visitString(@NotNull ElixirString o) {
    visitAccessExpression(o);
  }

  public void visitStringHeredoc(@NotNull ElixirStringHeredoc o) {
    visitAccessExpression(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
