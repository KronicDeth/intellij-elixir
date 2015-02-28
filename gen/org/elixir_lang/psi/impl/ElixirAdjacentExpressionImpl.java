// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirAdjacentExpressionImpl extends ASTWrapperPsiElement implements ElixirAdjacentExpression {

  public ElixirAdjacentExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAdjacentExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAlias getAlias() {
    return findChildByClass(ElixirAlias.class);
  }

  @Override
  @Nullable
  public ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation() {
    return findChildByClass(ElixirAtCharTokenOrNumberOperation.class);
  }

  @Override
  @Nullable
  public ElixirAtom getAtom() {
    return findChildByClass(ElixirAtom.class);
  }

  @Override
  @Nullable
  public ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation() {
    return findChildByClass(ElixirCaptureCharTokenOrNumberOperation.class);
  }

  @Override
  @Nullable
  public ElixirCharListHeredoc getCharListHeredoc() {
    return findChildByClass(ElixirCharListHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirCharListLine getCharListLine() {
    return findChildByClass(ElixirCharListLine.class);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedCharListBody getInterpolatedCharListBody() {
    return findChildByClass(ElixirInterpolatedCharListBody.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedCharListSigilHeredoc getInterpolatedCharListSigilHeredoc() {
    return findChildByClass(ElixirInterpolatedCharListSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedRegexBody getInterpolatedRegexBody() {
    return findChildByClass(ElixirInterpolatedRegexBody.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedRegexHeredoc getInterpolatedRegexHeredoc() {
    return findChildByClass(ElixirInterpolatedRegexHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedSigilBody getInterpolatedSigilBody() {
    return findChildByClass(ElixirInterpolatedSigilBody.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedSigilHeredoc getInterpolatedSigilHeredoc() {
    return findChildByClass(ElixirInterpolatedSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedStringBody getInterpolatedStringBody() {
    return findChildByClass(ElixirInterpolatedStringBody.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedStringSigilHeredoc getInterpolatedStringSigilHeredoc() {
    return findChildByClass(ElixirInterpolatedStringSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedWordsHeredoc getInterpolatedWordsHeredoc() {
    return findChildByClass(ElixirInterpolatedWordsHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirList getList() {
    return findChildByClass(ElixirList.class);
  }

  @Override
  @Nullable
  public ElixirLiteralCharListBody getLiteralCharListBody() {
    return findChildByClass(ElixirLiteralCharListBody.class);
  }

  @Override
  @Nullable
  public ElixirLiteralCharListSigilHeredoc getLiteralCharListSigilHeredoc() {
    return findChildByClass(ElixirLiteralCharListSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirLiteralRegexBody getLiteralRegexBody() {
    return findChildByClass(ElixirLiteralRegexBody.class);
  }

  @Override
  @Nullable
  public ElixirLiteralRegexHeredoc getLiteralRegexHeredoc() {
    return findChildByClass(ElixirLiteralRegexHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirLiteralSigilBody getLiteralSigilBody() {
    return findChildByClass(ElixirLiteralSigilBody.class);
  }

  @Override
  @Nullable
  public ElixirLiteralSigilHeredoc getLiteralSigilHeredoc() {
    return findChildByClass(ElixirLiteralSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirLiteralStringBody getLiteralStringBody() {
    return findChildByClass(ElixirLiteralStringBody.class);
  }

  @Override
  @Nullable
  public ElixirLiteralStringSigilHeredoc getLiteralStringSigilHeredoc() {
    return findChildByClass(ElixirLiteralStringSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirLiteralWordsHeredoc getLiteralWordsHeredoc() {
    return findChildByClass(ElixirLiteralWordsHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirMatchedAtOperation getMatchedAtOperation() {
    return findChildByClass(ElixirMatchedAtOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedHatOperation getMatchedHatOperation() {
    return findChildByClass(ElixirMatchedHatOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedMultiplicationOperation getMatchedMultiplicationOperation() {
    return findChildByClass(ElixirMatchedMultiplicationOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedNonNumericCaptureOperation getMatchedNonNumericCaptureOperation() {
    return findChildByClass(ElixirMatchedNonNumericCaptureOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedUnaryOperation getMatchedUnaryOperation() {
    return findChildByClass(ElixirMatchedUnaryOperation.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArgumentsQualifiedCall getNoParenthesesManyArgumentsQualifiedCall() {
    return findChildByClass(ElixirNoParenthesesManyArgumentsQualifiedCall.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArgumentsUnqualifiedCall getNoParenthesesManyArgumentsUnqualifiedCall() {
    return findChildByClass(ElixirNoParenthesesManyArgumentsUnqualifiedCall.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesNoArgumentsQualifiedCall getNoParenthesesNoArgumentsQualifiedCall() {
    return findChildByClass(ElixirNoParenthesesNoArgumentsQualifiedCall.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable getNoParenthesesNoArgumentsUnqualifiedCallOrVariable() {
    return findChildByClass(ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable.class);
  }

  @Override
  @Nullable
  public ElixirNumber getNumber() {
    return findChildByClass(ElixirNumber.class);
  }

  @Override
  @Nullable
  public ElixirQualifiedAlias getQualifiedAlias() {
    return findChildByClass(ElixirQualifiedAlias.class);
  }

  @Override
  @Nullable
  public ElixirStringHeredoc getStringHeredoc() {
    return findChildByClass(ElixirStringHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirStringLine getStringLine() {
    return findChildByClass(ElixirStringLine.class);
  }

  @Override
  @Nullable
  public ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation() {
    return findChildByClass(ElixirUnaryCharTokenOrNumberOperation.class);
  }

}
