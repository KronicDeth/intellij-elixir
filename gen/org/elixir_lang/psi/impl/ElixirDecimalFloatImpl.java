// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirDecimalFloatImpl extends ElixirMatchedExpressionImpl implements ElixirDecimalFloat {

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
