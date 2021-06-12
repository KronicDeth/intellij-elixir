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

  public ElixirDecimalFloatImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitDecimalFloat(this);
  }

  @Override
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
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
