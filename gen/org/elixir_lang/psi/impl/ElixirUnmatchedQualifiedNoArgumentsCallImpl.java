// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnmatchedQualifiedNoArgumentsCallImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedQualifiedNoArgumentsCall {

  public ElixirUnmatchedQualifiedNoArgumentsCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedQualifiedNoArgumentsCall(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirDoBlock getDoBlock() {
    return findChildByClass(ElixirDoBlock.class);
  }

  @Override
  @NotNull
  public ElixirDotInfixOperator getDotInfixOperator() {
    return findNotNullChildByClass(ElixirDotInfixOperator.class);
  }

  @Override
  @NotNull
  public ElixirRelativeIdentifier getRelativeIdentifier() {
    return findNotNullChildByClass(ElixirRelativeIdentifier.class);
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
