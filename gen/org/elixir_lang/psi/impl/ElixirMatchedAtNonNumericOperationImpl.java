// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirMatchedAtNonNumericOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedAtNonNumericOperation {

  public ElixirMatchedAtNonNumericOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedAtNonNumericOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return findNotNullChildByClass(ElixirAtPrefixOperator.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return findChildByClass(ElixirMatchedExpression.class);
  }

  @Nullable
  public PsiReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
  }

  @NotNull
  public String moduleAttributeName() {
    return ElixirPsiImplUtil.moduleAttributeName(this);
  }

  @NotNull
  public Quotable operand() {
    return ElixirPsiImplUtil.operand(this);
  }

  @NotNull
  public Operator operator() {
    return ElixirPsiImplUtil.operator(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
