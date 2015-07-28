// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirMatchedQualifiedNoParenthesesCallImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedQualifiedNoParenthesesCall {

  public ElixirMatchedQualifiedNoParenthesesCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedQualifiedNoParenthesesCall(this);
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
  public ElixirNoParenthesesOneArgument getNoParenthesesOneArgument() {
    return findNotNullChildByClass(ElixirNoParenthesesOneArgument.class);
  }

  @Override
  @NotNull
  public ElixirRelativeIdentifier getRelativeIdentifier() {
    return findNotNullChildByClass(ElixirRelativeIdentifier.class);
  }

  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
