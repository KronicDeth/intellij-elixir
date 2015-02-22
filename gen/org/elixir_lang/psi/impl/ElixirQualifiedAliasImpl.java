// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirQualifiedAliasImpl extends ASTWrapperPsiElement implements ElixirQualifiedAlias {

  public ElixirQualifiedAliasImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitQualifiedAlias(this);
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
  public ElixirCharList getCharList() {
    return findChildByClass(ElixirCharList.class);
  }

  @Override
  @Nullable
  public ElixirCharListHeredoc getCharListHeredoc() {
    return findChildByClass(ElixirCharListHeredoc.class);
  }

  @Override
  @NotNull
  public ElixirInfixDotOperator getInfixDotOperator() {
    return findNotNullChildByClass(ElixirInfixDotOperator.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedCharListSigilHeredoc getInterpolatedCharListSigilHeredoc() {
    return findChildByClass(ElixirInterpolatedCharListSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedRegexHeredoc getInterpolatedRegexHeredoc() {
    return findChildByClass(ElixirInterpolatedRegexHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirList getList() {
    return findChildByClass(ElixirList.class);
  }

  @Override
  @Nullable
  public ElixirMatchedAtOperation getMatchedAtOperation() {
    return findChildByClass(ElixirMatchedAtOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedDotOperation getMatchedDotOperation() {
    return findChildByClass(ElixirMatchedDotOperation.class);
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
  public ElixirString getString() {
    return findChildByClass(ElixirString.class);
  }

  @Override
  @Nullable
  public ElixirStringHeredoc getStringHeredoc() {
    return findChildByClass(ElixirStringHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation() {
    return findChildByClass(ElixirUnaryCharTokenOrNumberOperation.class);
  }

}
