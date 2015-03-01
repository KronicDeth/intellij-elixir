// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirMatchedMultiplicationOperationImpl extends ASTWrapperPsiElement implements ElixirMatchedMultiplicationOperation {

  public ElixirMatchedMultiplicationOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedMultiplicationOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirAlias> getAliasList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirAlias.class);
  }

  @Override
  @NotNull
  public List<ElixirAtCharTokenOrNumberOperation> getAtCharTokenOrNumberOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirAtCharTokenOrNumberOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirAtom> getAtomList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirAtom.class);
  }

  @Override
  @NotNull
  public List<ElixirCaptureCharTokenOrNumberOperation> getCaptureCharTokenOrNumberOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCaptureCharTokenOrNumberOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirCharListHeredoc> getCharListHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharListHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirCharListLine> getCharListLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharListLine.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedCharListSigilHeredoc> getInterpolatedCharListSigilHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedCharListSigilHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedCharListSigilLine> getInterpolatedCharListSigilLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedCharListSigilLine.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedRegexHeredoc> getInterpolatedRegexHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedRegexHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedRegexLine> getInterpolatedRegexLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedRegexLine.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedSigilHeredoc> getInterpolatedSigilHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedSigilHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedSigilLine> getInterpolatedSigilLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedSigilLine.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedStringSigilHeredoc> getInterpolatedStringSigilHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedStringSigilHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedStringSigilLine> getInterpolatedStringSigilLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedStringSigilLine.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedWordsHeredoc> getInterpolatedWordsHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedWordsHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedWordsLine> getInterpolatedWordsLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedWordsLine.class);
  }

  @Override
  @NotNull
  public List<ElixirList> getListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirList.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralCharListSigilHeredoc> getLiteralCharListSigilHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralCharListSigilHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralCharListSigilLine> getLiteralCharListSigilLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralCharListSigilLine.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralRegexHeredoc> getLiteralRegexHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralRegexHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralRegexLine> getLiteralRegexLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralRegexLine.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralSigilHeredoc> getLiteralSigilHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralSigilHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralSigilLine> getLiteralSigilLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralSigilLine.class);
  }

  @Override
  @Nullable
  public ElixirLiteralStringBody getLiteralStringBody() {
    return findChildByClass(ElixirLiteralStringBody.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralStringSigilHeredoc> getLiteralStringSigilHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralStringSigilHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirLiteralWordsHeredoc> getLiteralWordsHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirLiteralWordsHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedAtOperation> getMatchedAtOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedAtOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedHatOperation> getMatchedHatOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedHatOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedMultiplicationOperation getMatchedMultiplicationOperation() {
    return findChildByClass(ElixirMatchedMultiplicationOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedNonNumericCaptureOperation> getMatchedNonNumericCaptureOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedNonNumericCaptureOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedUnaryOperation> getMatchedUnaryOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedUnaryOperation.class);
  }

  @Override
  @NotNull
  public ElixirMultiplicationInfixOperator getMultiplicationInfixOperator() {
    return findNotNullChildByClass(ElixirMultiplicationInfixOperator.class);
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
  @NotNull
  public List<ElixirNoParenthesesNoArgumentsQualifiedCall> getNoParenthesesNoArgumentsQualifiedCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesNoArgumentsQualifiedCall.class);
  }

  @Override
  @NotNull
  public List<ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable> getNoParenthesesNoArgumentsUnqualifiedCallOrVariableList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable.class);
  }

  @Override
  @NotNull
  public List<ElixirNumber> getNumberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNumber.class);
  }

  @Override
  @NotNull
  public List<ElixirQualifiedAlias> getQualifiedAliasList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirQualifiedAlias.class);
  }

  @Override
  @NotNull
  public List<ElixirStringHeredoc> getStringHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirStringHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirStringLine> getStringLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirStringLine.class);
  }

  @Override
  @NotNull
  public List<ElixirUnaryCharTokenOrNumberOperation> getUnaryCharTokenOrNumberOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirUnaryCharTokenOrNumberOperation.class);
  }

}
