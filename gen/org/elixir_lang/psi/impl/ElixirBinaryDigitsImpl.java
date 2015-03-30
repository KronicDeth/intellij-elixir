// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.tree.IElementType;

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
