// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;

public class ElixirMatchedDotOperatorCallOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedDotOperatorCallOperation {

  public ElixirMatchedDotOperatorCallOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedDotOperatorCallOperation(this);
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
  public ElixirOperatorCallArguments getOperatorCallArguments() {
    return findNotNullChildByClass(ElixirOperatorCallArguments.class);
  }

  @Override
  @NotNull
  public ElixirOperatorIdentifier getOperatorIdentifier() {
    return findNotNullChildByClass(ElixirOperatorIdentifier.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
