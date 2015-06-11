// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirDoBlock;
import org.elixir_lang.psi.ElixirUnqualifiedNoArgumentsBlock;
import org.elixir_lang.psi.ElixirVariable;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirUnqualifiedNoArgumentsBlockImpl extends ASTWrapperPsiElement implements ElixirUnqualifiedNoArgumentsBlock {

  public ElixirUnqualifiedNoArgumentsBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnqualifiedNoArgumentsBlock(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirDoBlock getDoBlock() {
    return findNotNullChildByClass(ElixirDoBlock.class);
  }

  @Override
  @NotNull
  public ElixirVariable getVariable() {
    return findNotNullChildByClass(ElixirVariable.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
