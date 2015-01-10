// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirBinaryDigits;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.ElixirTypes.INVALID_BINARY_DIGITS;
import static org.elixir_lang.psi.ElixirTypes.VALID_BINARY_DIGITS;

public class ElixirBinaryDigitsImpl extends ASTWrapperPsiElement implements ElixirBinaryDigits {

  public ElixirBinaryDigitsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBinaryDigits(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getInvalidBinaryDigits() {
    return findChildByType(INVALID_BINARY_DIGITS);
  }

  @Override
  @Nullable
  public PsiElement getValidBinaryDigits() {
    return findChildByType(VALID_BINARY_DIGITS);
  }

  public boolean inBase() {
    return ElixirPsiImplUtil.inBase(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
