// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirInterpolationImpl extends ASTWrapperPsiElement implements ElixirInterpolation {

  public ElixirInterpolationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitInterpolation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirAdjacentExpression> getAdjacentExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirAdjacentExpression.class);
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
  public List<ElixirCharList> getCharListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharList.class);
  }

  @Override
  @NotNull
  public List<ElixirCharListHeredoc> getCharListHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharListHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirEndOfExpression> getEndOfExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEndOfExpression.class);
  }

  @Override
  @NotNull
  public List<ElixirList> getListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirList.class);
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
  @NotNull
  public List<ElixirMatchedMultiplicationOperation> getMatchedMultiplicationOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedMultiplicationOperation.class);
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
  public List<ElixirNoParenthesesManyArgumentsQualifiedCall> getNoParenthesesManyArgumentsQualifiedCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesManyArgumentsQualifiedCall.class);
  }

  @Override
  @NotNull
  public List<ElixirNoParenthesesManyArgumentsUnqualifiedCall> getNoParenthesesManyArgumentsUnqualifiedCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesManyArgumentsUnqualifiedCall.class);
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
  public List<ElixirSigil> getSigilList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirSigil.class);
  }

  @Override
  @NotNull
  public List<ElixirString> getStringList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirString.class);
  }

  @Override
  @NotNull
  public List<ElixirStringHeredoc> getStringHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirStringHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirUnaryCharTokenOrNumberOperation> getUnaryCharTokenOrNumberOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirUnaryCharTokenOrNumberOperation.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
