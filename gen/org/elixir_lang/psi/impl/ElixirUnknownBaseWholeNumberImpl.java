// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirUnknownBaseWholeNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.psi.ElixirTypes.UNKNOWN_WHOLE_NUMBER_BASE;

public class ElixirUnknownBaseWholeNumberImpl extends ElixirNumberImpl implements ElixirUnknownBaseWholeNumber {

  public ElixirUnknownBaseWholeNumberImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnknownBaseWholeNumber(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getUnknownWholeNumberBase() {
    return findNotNullChildByType(UNKNOWN_WHOLE_NUMBER_BASE);
  }

}
