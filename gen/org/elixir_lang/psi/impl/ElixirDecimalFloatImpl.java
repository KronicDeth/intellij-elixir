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

public class ElixirDecimalFloatImpl extends ASTWrapperPsiElement implements ElixirDecimalFloat {

  public ElixirDecimalFloatImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitDecimalFloat(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirDecimalFloatExponent getDecimalFloatExponent() {
    return findChildByClass(ElixirDecimalFloatExponent.class);
  }

  @Override
  @NotNull
  public ElixirDecimalFloatFractional getDecimalFloatFractional() {
    return findNotNullChildByClass(ElixirDecimalFloatFractional.class);
  }

  @Override
  @NotNull
  public ElixirDecimalFloatIntegral getDecimalFloatIntegral() {
    return findNotNullChildByClass(ElixirDecimalFloatIntegral.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
