// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirInterpolatedStringBody;
import org.elixir_lang.psi.ElixirStringHeredoc;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirStringHeredocImpl extends ASTWrapperPsiElement implements ElixirStringHeredoc {

  public ElixirStringHeredocImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStringHeredoc(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirInterpolatedStringBody getInterpolatedStringBody() {
    return findNotNullChildByClass(ElixirInterpolatedStringBody.class);
  }

}
