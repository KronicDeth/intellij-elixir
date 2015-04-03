// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirLiteralCharListBody;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirLiteralCharListBodyImpl extends ASTWrapperPsiElement implements ElixirLiteralCharListBody {

  public ElixirLiteralCharListBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitLiteralCharListBody(this);
    else super.accept(visitor);
  }

}
