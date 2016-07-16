// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirDecimalFloatExponent;
import org.elixir_lang.psi.ElixirDecimalFloatExponentSign;
import org.elixir_lang.psi.ElixirDecimalWholeNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirDecimalFloatExponentImpl extends ASTWrapperPsiElement implements ElixirDecimalFloatExponent {

  public ElixirDecimalFloatExponentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitDecimalFloatExponent(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirDecimalFloatExponentSign getDecimalFloatExponentSign() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDecimalFloatExponentSign.class));
  }

  @Override
  @NotNull
  public ElixirDecimalWholeNumber getDecimalWholeNumber() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDecimalWholeNumber.class));
  }

}
