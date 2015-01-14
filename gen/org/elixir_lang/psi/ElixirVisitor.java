// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ElixirVisitor extends PsiElementVisitor {

  public void visitAdjacentExpression(@NotNull ElixirAdjacentExpression o) {
    visitPsiElement(o);
  }

  public void visitAtCharTokenOrNumberOperation(@NotNull ElixirAtCharTokenOrNumberOperation o) {
    visitPsiElement(o);
  }

  public void visitAtPrefixOperator(@NotNull ElixirAtPrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitAtom(@NotNull ElixirAtom o) {
    visitQuotable(o);
  }

  public void visitBinaryDigits(@NotNull ElixirBinaryDigits o) {
    visitDigits(o);
  }

  public void visitBinaryWholeNumber(@NotNull ElixirBinaryWholeNumber o) {
    visitNumber(o);
    // visitWholeNumber(o);
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

  public void visitCapturePrefixOperator(@NotNull ElixirCapturePrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitCharList(@NotNull ElixirCharList o) {
    visitQuotable(o);
  }

  public void visitCharListHeredoc(@NotNull ElixirCharListHeredoc o) {
    visitQuotable(o);
  }

  public void visitCharListHeredocLine(@NotNull ElixirCharListHeredocLine o) {
    visitPsiElement(o);
  }

  public void visitCharListHeredocLineWhitespace(@NotNull ElixirCharListHeredocLineWhitespace o) {
    visitPsiElement(o);
  }

  public void visitCharListHeredocPrefix(@NotNull ElixirCharListHeredocPrefix o) {
    visitPsiElement(o);
  }

  public void visitDecimalDigits(@NotNull ElixirDecimalDigits o) {
    visitDigits(o);
  }

  public void visitDecimalFloat(@NotNull ElixirDecimalFloat o) {
    visitDecimalNumber(o);
  }

  public void visitDecimalNumber(@NotNull ElixirDecimalNumber o) {
    visitNumber(o);
  }

  public void visitDecimalWholeNumber(@NotNull ElixirDecimalWholeNumber o) {
    visitNumber(o);
    // visitWholeNumber(o);
  }

  public void visitEmptyParentheses(@NotNull ElixirEmptyParentheses o) {
    visitPsiElement(o);
  }

  public void visitEnclosedHexadecimalEscapeSequence(@NotNull ElixirEnclosedHexadecimalEscapeSequence o) {
    visitEscapedHexadecimalDigits(o);
  }

  public void visitEndOfExpression(@NotNull ElixirEndOfExpression o) {
    visitPsiElement(o);
  }

  public void visitEscapedCharacter(@NotNull ElixirEscapedCharacter o) {
    visitEscapeSequence(o);
  }

  public void visitHatInfixOperator(@NotNull ElixirHatInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitHexadecimalDigits(@NotNull ElixirHexadecimalDigits o) {
    visitDigits(o);
  }

  public void visitHexadecimalEscapeSequence(@NotNull ElixirHexadecimalEscapeSequence o) {
    visitPsiElement(o);
  }

  public void visitHexadecimalWholeNumber(@NotNull ElixirHexadecimalWholeNumber o) {
    visitNumber(o);
    // visitWholeNumber(o);
  }

  public void visitInfixDotOperator(@NotNull ElixirInfixDotOperator o) {
    visitPsiElement(o);
  }

  public void visitInterpolatedCharListBody(@NotNull ElixirInterpolatedCharListBody o) {
    visitQuotable(o);
  }

  public void visitInterpolatedStringBody(@NotNull ElixirInterpolatedStringBody o) {
    visitQuotable(o);
  }

  public void visitInterpolation(@NotNull ElixirInterpolation o) {
    visitQuotable(o);
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

  public void visitMatchedAtOperation(@NotNull ElixirMatchedAtOperation o) {
    visitPsiElement(o);
  }

  public void visitMatchedDotOperation(@NotNull ElixirMatchedDotOperation o) {
    visitPsiElement(o);
  }

  public void visitMatchedHatOperation(@NotNull ElixirMatchedHatOperation o) {
    visitPsiElement(o);
  }

  public void visitMatchedMultiplicationOperation(@NotNull ElixirMatchedMultiplicationOperation o) {
    visitPsiElement(o);
  }

  public void visitMatchedNonNumericCaptureOperation(@NotNull ElixirMatchedNonNumericCaptureOperation o) {
    visitPsiElement(o);
  }

  public void visitMatchedUnaryOperation(@NotNull ElixirMatchedUnaryOperation o) {
    visitPsiElement(o);
  }

  public void visitMultiplicationInfixOperator(@NotNull ElixirMultiplicationInfixOperator o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesManyArgumentsQualifiedCall(@NotNull ElixirNoParenthesesManyArgumentsQualifiedCall o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesManyArgumentsUnqualifiedCall(@NotNull ElixirNoParenthesesManyArgumentsUnqualifiedCall o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesManyStrictNoParenthesesExpression(@NotNull ElixirNoParenthesesManyStrictNoParenthesesExpression o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesNoArgumentsQualifiedCall(@NotNull ElixirNoParenthesesNoArgumentsQualifiedCall o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesNoArgumentsUnqualifiedCallOrVariable(@NotNull ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesStrict(@NotNull ElixirNoParenthesesStrict o) {
    visitPsiElement(o);
  }

  public void visitNumber(@NotNull ElixirNumber o) {
    visitPsiElement(o);
  }

  public void visitOctalDigits(@NotNull ElixirOctalDigits o) {
    visitDigits(o);
  }

  public void visitOctalWholeNumber(@NotNull ElixirOctalWholeNumber o) {
    visitNumber(o);
    // visitWholeNumber(o);
  }

  public void visitOpenHexadecimalEscapeSequence(@NotNull ElixirOpenHexadecimalEscapeSequence o) {
    visitEscapedHexadecimalDigits(o);
  }

  public void visitQualifiedAlias(@NotNull ElixirQualifiedAlias o) {
    visitPsiElement(o);
  }

  public void visitSigil(@NotNull ElixirSigil o) {
    visitPsiElement(o);
  }

  public void visitString(@NotNull ElixirString o) {
    visitQuotable(o);
  }

  public void visitStringHeredoc(@NotNull ElixirStringHeredoc o) {
    visitPsiElement(o);
  }

  public void visitUnaryCharTokenOrNumberOperation(@NotNull ElixirUnaryCharTokenOrNumberOperation o) {
    visitPsiElement(o);
  }

  public void visitUnaryPrefixOperator(@NotNull ElixirUnaryPrefixOperator o) {
    visitPsiElement(o);
  }

  public void visitUnknownBaseWholeNumber(@NotNull ElixirUnknownBaseWholeNumber o) {
    visitNumber(o);
  }

  public void visitDigits(@NotNull Digits o) {
    visitElement(o);
  }

  public void visitEscapeSequence(@NotNull EscapeSequence o) {
    visitElement(o);
  }

  public void visitEscapedHexadecimalDigits(@NotNull EscapedHexadecimalDigits o) {
    visitElement(o);
  }

  public void visitQuotable(@NotNull Quotable o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
