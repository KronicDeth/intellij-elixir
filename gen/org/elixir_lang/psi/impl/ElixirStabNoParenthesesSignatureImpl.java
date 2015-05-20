// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirNoParenthesesArguments;
import org.elixir_lang.psi.ElixirStabNoParenthesesSignature;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirStabNoParenthesesSignatureImpl extends ASTWrapperPsiElement implements ElixirStabNoParenthesesSignature {

  public ElixirStabNoParenthesesSignatureImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStabNoParenthesesSignature(this);
    else super.accept(visitor);
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
