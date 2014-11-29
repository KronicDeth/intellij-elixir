// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirBinaryNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirBinaryNumberImpl extends ElixirAccessExpressionImpl implements ElixirBinaryNumber {

  public ElixirBinaryNumberImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBinaryNumber(this);
    else super.accept(visitor);
  }

}
