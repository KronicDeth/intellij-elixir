// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirDecimalFloatImpl extends ASTWrapperPsiElement implements ElixirDecimalFloat {

  public ElixirDecimalFloatImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitDecimalFloat(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirDecimalFloatExponent getDecimalFloatExponent() {
    return PsiTreeUtil.getChildOfType(this, ElixirDecimalFloatExponent.class);
  }

  @Override
  @NotNull
  public ElixirDecimalFloatFractional getDecimalFloatFractional() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDecimalFloatFractional.class));
  }

  @Override
  @NotNull
  public ElixirDecimalFloatIntegral getDecimalFloatIntegral() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDecimalFloatIntegral.class));
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
