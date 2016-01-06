// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import org.elixir_lang.psi.ElixirAtPrefixOperator;
import org.elixir_lang.psi.ElixirUnmatchedAtNonNumericOperation;
import org.elixir_lang.psi.ElixirUnmatchedExpression;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnmatchedAtNonNumericOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedAtNonNumericOperation {

  public ElixirUnmatchedAtNonNumericOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedAtNonNumericOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return findNotNullChildByClass(ElixirAtPrefixOperator.class);
  }

  @Override
  @Nullable
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return findChildByClass(ElixirUnmatchedExpression.class);
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
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
