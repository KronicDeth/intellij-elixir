// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirStabOperationImpl extends ASTWrapperPsiElement implements ElixirStabOperation {

  public ElixirStabOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStabOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirStabBody getStabBody() {
    return findChildByClass(ElixirStabBody.class);
  }

  @Override
  @NotNull
  public ElixirStabInfixOperator getStabInfixOperator() {
    return findNotNullChildByClass(ElixirStabInfixOperator.class);
  }

  @Override
  @Nullable
  public ElixirStabNoParenthesesSignature getStabNoParenthesesSignature() {
    return findChildByClass(ElixirStabNoParenthesesSignature.class);
  }

  @Override
  @Nullable
  public ElixirStabParenthesesSignature getStabParenthesesSignature() {
    return findChildByClass(ElixirStabParenthesesSignature.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
