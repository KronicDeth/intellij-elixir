// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirNoParenthesesExpressionImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesExpression {

  public ElixirNoParenthesesExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return findChildByClass(ElixirMatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyStrictNoParenthesesExpression getNoParenthesesManyStrictNoParenthesesExpression() {
    return findChildByClass(ElixirNoParenthesesManyStrictNoParenthesesExpression.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
