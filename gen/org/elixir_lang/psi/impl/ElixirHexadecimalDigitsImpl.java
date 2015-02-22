// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirHexadecimalDigits;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.ElixirTypes.VALID_HEXADECIMAL_DIGITS;

public class ElixirHexadecimalDigitsImpl extends ASTWrapperPsiElement implements ElixirHexadecimalDigits {

  public ElixirHexadecimalDigitsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitHexadecimalDigits(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getValidHexadecimalDigits() {
    return findChildByType(VALID_HEXADECIMAL_DIGITS);
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

  @NotNull
  public IElementType validElementType() {
    return ElixirPsiImplUtil.validElementType(this);
  }

}
