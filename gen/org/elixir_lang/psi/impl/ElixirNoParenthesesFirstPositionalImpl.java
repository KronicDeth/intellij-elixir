// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirMatchedExpression;
import org.elixir_lang.psi.ElixirNoParenthesesFirstPositional;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirNoParenthesesFirstPositionalImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesFirstPositional {

  public ElixirNoParenthesesFirstPositionalImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesFirstPositional(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMatchedExpression getMatchedExpression() {
    return findNotNullChildByClass(ElixirMatchedExpression.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
