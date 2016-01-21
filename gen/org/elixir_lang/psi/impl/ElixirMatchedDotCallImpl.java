// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirMatchedDotCallImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedDotCall {

  public ElixirMatchedDotCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedDotCall(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirDotInfixOperator getDotInfixOperator() {
    return findNotNullChildByClass(ElixirDotInfixOperator.class);
  }

  @Override
  @NotNull
  public ElixirMatchedExpression getMatchedExpression() {
    return findNotNullChildByClass(ElixirMatchedExpression.class);
  }

  @Override
  @NotNull
  public List<ElixirParenthesesArguments> getParenthesesArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirParenthesesArguments.class);
  }

  @Nullable
  public String functionName() {
    return ElixirPsiImplUtil.functionName(this);
  }

  @Nullable
  public PsiElement functionNameElement() {
    return ElixirPsiImplUtil.functionNameElement(this);
  }

  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return ElixirPsiImplUtil.getPresentation(this);
  }

  public boolean isCalling(String resolvedModuleName, String resolvedFunctionName) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, resolvedFunctionName);
  }

  public boolean isCalling(String resolvedModuleName, String resolvedFunctionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, resolvedFunctionName, resolvedFinalArity);
  }

  public boolean isCallingMacro(String resolvedModuleName, String resolvedFunctionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCallingMacro(this, resolvedModuleName, resolvedFunctionName, resolvedFinalArity);
  }

  @Nullable
  public String moduleName() {
    return ElixirPsiImplUtil.moduleName(this);
  }

  @NotNull
  public PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  @Nullable
  public Integer primaryArity() {
    return ElixirPsiImplUtil.primaryArity(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Nullable
  public Integer resolvedFinalArity() {
    return ElixirPsiImplUtil.resolvedFinalArity(this);
  }

  @Nullable
  public String resolvedFunctionName() {
    return ElixirPsiImplUtil.resolvedFunctionName(this);
  }

  @Nullable
  public String resolvedModuleName() {
    return ElixirPsiImplUtil.resolvedModuleName(this);
  }

  @Nullable
  public Integer resolvedPrimaryArity() {
    return ElixirPsiImplUtil.resolvedPrimaryArity(this);
  }

  @Nullable
  public Integer resolvedSecondaryArity() {
    return ElixirPsiImplUtil.resolvedSecondaryArity(this);
  }

  @Nullable
  public PsiElement[] secondaryArguments() {
    return ElixirPsiImplUtil.secondaryArguments(this);
  }

  @Nullable
  public Integer secondaryArity() {
    return ElixirPsiImplUtil.secondaryArity(this);
  }

}
