// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirStabOperationImpl extends ASTWrapperPsiElement implements ElixirStabOperation {

  public ElixirStabOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitStabOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirStabBody getStabBody() {
    return PsiTreeUtil.getChildOfType(this, ElixirStabBody.class);
  }

  @Override
  @NotNull
  public ElixirStabInfixOperator getStabInfixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirStabInfixOperator.class));
  }

  @Override
  @Nullable
  public ElixirStabNoParenthesesSignature getStabNoParenthesesSignature() {
    return PsiTreeUtil.getChildOfType(this, ElixirStabNoParenthesesSignature.class);
  }

  @Override
  @Nullable
  public ElixirStabParenthesesSignature getStabParenthesesSignature() {
    return PsiTreeUtil.getChildOfType(this, ElixirStabParenthesesSignature.class);
  }

  @Nullable
  public Quotable leftOperand() {
    return ElixirPsiImplUtil.leftOperand(this);
  }

  @NotNull
  public Operator operator() {
    return ElixirPsiImplUtil.operator(this);
  }

  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Nullable
  public Quotable rightOperand() {
    return ElixirPsiImplUtil.rightOperand(this);
  }

}
