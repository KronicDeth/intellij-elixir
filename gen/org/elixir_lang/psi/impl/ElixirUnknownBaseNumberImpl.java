// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirUnknownBaseNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.psi.ElixirTypes.UNKNOWN_INTEGER_BASE;

public class ElixirUnknownBaseNumberImpl extends ElixirAccessExpressionImpl implements ElixirUnknownBaseNumber {

  public ElixirUnknownBaseNumberImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnknownBaseNumber(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getUnknownIntegerBase() {
    return findNotNullChildByType(UNKNOWN_INTEGER_BASE);
  }

}
