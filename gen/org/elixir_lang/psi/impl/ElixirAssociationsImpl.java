// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirAssociations;
import org.elixir_lang.psi.ElixirAssociationsBase;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirAssociationsImpl extends ASTWrapperPsiElement implements ElixirAssociations {

  public ElixirAssociationsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAssociations(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAssociationsBase getAssociationsBase() {
    return findNotNullChildByClass(ElixirAssociationsBase.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
