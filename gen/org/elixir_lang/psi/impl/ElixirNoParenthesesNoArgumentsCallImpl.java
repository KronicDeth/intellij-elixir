// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirNoParenthesesNoArgumentsCall;
import org.elixir_lang.psi.ElixirNoParenthesesNoArgumentsQualifiedIdentifier;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirNoParenthesesNoArgumentsCallImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesNoArgumentsCall {

  public ElixirNoParenthesesNoArgumentsCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesNoArgumentsCall(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesNoArgumentsQualifiedIdentifier getNoParenthesesNoArgumentsQualifiedIdentifier() {
    return findNotNullChildByClass(ElixirNoParenthesesNoArgumentsQualifiedIdentifier.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
