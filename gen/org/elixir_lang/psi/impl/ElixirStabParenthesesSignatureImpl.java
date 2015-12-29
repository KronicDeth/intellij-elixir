// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirStabParenthesesSignatureImpl extends ASTWrapperPsiElement implements ElixirStabParenthesesSignature {

  public ElixirStabParenthesesSignatureImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStabParenthesesSignature(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @NotNull
  public ElixirParenthesesArguments getParenthesesArguments() {
    return findNotNullChildByClass(ElixirParenthesesArguments.class);
  }

  @Override
  @Nullable
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return findChildByClass(ElixirUnmatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall() {
    return findChildByClass(ElixirUnqualifiedNoParenthesesManyArgumentsCall.class);
  }

  @Override
  @Nullable
  public ElixirWhenInfixOperator getWhenInfixOperator() {
    return findChildByClass(ElixirWhenInfixOperator.class);
  }

  @NotNull
  public Quotable leftOperand() {
    return ElixirPsiImplUtil.leftOperand(this);
  }

  @NotNull
  public Operator operator() {
    return ElixirPsiImplUtil.operator(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @NotNull
  public Quotable rightOperand() {
    return ElixirPsiImplUtil.rightOperand(this);
  }

}
