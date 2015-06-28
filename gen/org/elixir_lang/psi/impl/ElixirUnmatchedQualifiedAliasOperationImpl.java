// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;

public class ElixirUnmatchedQualifiedAliasOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedQualifiedAliasOperation {

  public ElixirUnmatchedQualifiedAliasOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedQualifiedAliasOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAlias getAlias() {
    return findNotNullChildByClass(ElixirAlias.class);
  }

  @Override
  @NotNull
  public ElixirDotInfixOperator getDotInfixOperator() {
    return findNotNullChildByClass(ElixirDotInfixOperator.class);
  }

  @Override
  @NotNull
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return findNotNullChildByClass(ElixirUnmatchedExpression.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
