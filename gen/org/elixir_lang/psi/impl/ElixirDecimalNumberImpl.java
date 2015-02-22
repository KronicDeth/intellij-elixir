// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirDecimalNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirDecimalNumberImpl extends ElixirNumberImpl implements ElixirDecimalNumber {

  public ElixirDecimalNumberImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitDecimalNumber(this);
    else super.accept(visitor);
  }

}
