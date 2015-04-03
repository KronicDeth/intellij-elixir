// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.psi.ElixirPipeInfixOperator;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirPipeInfixOperatorImpl extends ASTWrapperPsiElement implements ElixirPipeInfixOperator {

  public ElixirPipeInfixOperatorImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitPipeInfixOperator(this);
    else super.accept(visitor);
  }

  @NotNull
  public TokenSet operatorTokenSet() {
    return ElixirPsiImplUtil.operatorTokenSet(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
