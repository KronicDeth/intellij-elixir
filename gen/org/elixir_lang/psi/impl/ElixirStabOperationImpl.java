// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

public class ElixirStabOperationImpl extends ASTWrapperPsiElement implements ElixirStabOperation {

  public ElixirStabOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitStabOperation(this);
  }

  @Override
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

  @Override
  public @Nullable Quotable leftOperand() {
    return ElixirPsiImplUtil.leftOperand(this);
  }

  @Override
  public @NotNull Operator operator() {
    return ElixirPsiImplUtil.operator(this);
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  public @Nullable Quotable rightOperand() {
    return ElixirPsiImplUtil.rightOperand(this);
  }

}
