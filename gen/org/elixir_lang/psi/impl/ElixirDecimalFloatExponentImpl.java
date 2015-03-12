// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirDecimalFloatExponent;
import org.elixir_lang.psi.ElixirDecimalFloatExponentSign;
import org.elixir_lang.psi.ElixirDecimalWholeNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirDecimalFloatExponentImpl extends ASTWrapperPsiElement implements ElixirDecimalFloatExponent {

  public ElixirDecimalFloatExponentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitDecimalFloatExponent(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirDecimalFloatExponentSign getDecimalFloatExponentSign() {
    return findNotNullChildByClass(ElixirDecimalFloatExponentSign.class);
  }

  @Override
  @NotNull
  public ElixirDecimalWholeNumber getDecimalWholeNumber() {
    return findNotNullChildByClass(ElixirDecimalWholeNumber.class);
  }

}
