// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirAtPrefixOperator;
import org.elixir_lang.psi.ElixirMatchedAtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirMatchedNoParenthesesArguments;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirMatchedAtUnqualifiedNoParenthesesCallImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedAtUnqualifiedNoParenthesesCall {

  public ElixirMatchedAtUnqualifiedNoParenthesesCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedAtUnqualifiedNoParenthesesCall(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return findNotNullChildByClass(ElixirAtPrefixOperator.class);
  }

  @Override
  @NotNull
  public ElixirMatchedNoParenthesesArguments getMatchedNoParenthesesArguments() {
    return findNotNullChildByClass(ElixirMatchedNoParenthesesArguments.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
