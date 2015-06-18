// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirBlockNoParenthesesCall;
import org.elixir_lang.psi.ElixirMatchedBracketOperation;
import org.elixir_lang.psi.ElixirNoParenthesesArguments;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirBlockNoParenthesesCallImpl extends ASTWrapperPsiElement implements ElixirBlockNoParenthesesCall {

  public ElixirBlockNoParenthesesCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBlockNoParenthesesCall(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMatchedBracketOperation getMatchedBracketOperation() {
    return findNotNullChildByClass(ElixirMatchedBracketOperation.class);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesArguments getNoParenthesesArguments() {
    return findNotNullChildByClass(ElixirNoParenthesesArguments.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
