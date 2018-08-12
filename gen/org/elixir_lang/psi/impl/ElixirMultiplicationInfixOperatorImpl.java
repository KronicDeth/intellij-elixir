// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.psi.ElixirMultiplicationInfixOperator;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirMultiplicationInfixOperatorImpl extends ASTWrapperPsiElement implements ElixirMultiplicationInfixOperator {

  public ElixirMultiplicationInfixOperatorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitMultiplicationInfixOperator(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
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
