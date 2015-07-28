// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirUnknownBaseDigits;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnknownBaseDigitsImpl extends ASTWrapperPsiElement implements ElixirUnknownBaseDigits {

  public ElixirUnknownBaseDigitsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnknownBaseDigits(this);
    else super.accept(visitor);
  }

  @NotNull
  public int base() {
    return ElixirPsiImplUtil.base(this);
  }

  public boolean inBase() {
    return ElixirPsiImplUtil.inBase(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Nullable
  public IElementType validElementType() {
    return ElixirPsiImplUtil.validElementType(this);
  }

}
