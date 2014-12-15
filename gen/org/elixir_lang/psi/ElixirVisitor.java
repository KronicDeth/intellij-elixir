// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ElixirVisitor extends PsiElementVisitor {

  public void visitAdjacentExpression(@NotNull ElixirAdjacentExpression o) {
    visitPsiElement(o);
  }

  public void visitAtMatchedExpressionOperation(@NotNull ElixirAtMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitAtNoParenthesesExpressionOperation(@NotNull ElixirAtNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

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

  public void visitCaptureMatchedExpressionOperation(@NotNull ElixirCaptureMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitCaptureNoParenthesesExpressionOperation(@NotNull ElixirCaptureNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
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

  public void visitMatchedExpressionAdditionMatchedExpressionOperation(@NotNull ElixirMatchedExpressionAdditionMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionAdditionNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionAdditionNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionAndMatchedExpressionOperation(@NotNull ElixirMatchedExpressionAndMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionAndNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionAndNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionArrowMatchedExpressionOperation(@NotNull ElixirMatchedExpressionArrowMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionArrowNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionArrowNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionComparisonMatchedExpressionOperation(@NotNull ElixirMatchedExpressionComparisonMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionComparisonNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionComparisonNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionDotAlias(@NotNull ElixirMatchedExpressionDotAlias o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionDotIdentifier(@NotNull ElixirMatchedExpressionDotIdentifier o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionHatMatchedExpressionOperation(@NotNull ElixirMatchedExpressionHatMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionHatNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionHatNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionInMatchMatchedExpressionOperation(@NotNull ElixirMatchedExpressionInMatchMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionInMatchNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionInMatchNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionInMatchedExpressionOperation(@NotNull ElixirMatchedExpressionInMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionInNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionInNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionMatchMatchedExpressionOperation(@NotNull ElixirMatchedExpressionMatchMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionMatchNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionMatchNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionMaxExpression(@NotNull ElixirMatchedExpressionMaxExpression o) {
    visitMatchedExpressionAccessExpression(o);
  }

  public void visitMatchedExpressionMultiplicationMatchedExpressionOperation(@NotNull ElixirMatchedExpressionMultiplicationMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionMultiplicationNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionMultiplicationNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionOrMatchedExpressionOperation(@NotNull ElixirMatchedExpressionOrMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionOrNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionOrNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionPipeMatchedExpressionOperation(@NotNull ElixirMatchedExpressionPipeMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionPipeNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionPipeNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionRelationalMatchedExpressionOperation(@NotNull ElixirMatchedExpressionRelationalMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionRelationalNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionRelationalNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionTwoMatchedExpressionOperation(@NotNull ElixirMatchedExpressionTwoMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionTwoNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionTwoNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionTypeMatchedExpressionOperation(@NotNull ElixirMatchedExpressionTypeMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionTypeNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionTypeNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitMatchedExpressionWhenMatchedExpressionOperation(@NotNull ElixirMatchedExpressionWhenMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitMatchedExpressionWhenNoParenthesesExpressionOperation(@NotNull ElixirMatchedExpressionWhenNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
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

  public void visitUnaryMatchedExpressionOperation(@NotNull ElixirUnaryMatchedExpressionOperation o) {
    visitMatchedExpression(o);
  }

  public void visitUnaryNoParenthesesExpressionOperation(@NotNull ElixirUnaryNoParenthesesExpressionOperation o) {
    visitNoParenthesesExpression(o);
  }

  public void visitUnknownBaseWholeNumber(@NotNull ElixirUnknownBaseWholeNumber o) {
    visitNumber(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
