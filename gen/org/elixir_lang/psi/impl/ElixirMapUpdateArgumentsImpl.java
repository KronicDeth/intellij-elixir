// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirAssociationUpdate;
import org.elixir_lang.psi.ElixirMapUpdateArguments;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirMapUpdateArgumentsImpl extends ASTWrapperPsiElement implements ElixirMapUpdateArguments {

  public ElixirMapUpdateArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMapUpdateArguments(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAssociationUpdate getAssociationUpdate() {
    return findNotNullChildByClass(ElixirAssociationUpdate.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
