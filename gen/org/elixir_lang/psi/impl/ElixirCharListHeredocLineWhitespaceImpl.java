// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirCharListHeredocLineWhitespace;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirCharListHeredocLineWhitespaceImpl extends ASTWrapperPsiElement implements ElixirCharListHeredocLineWhitespace {

  public ElixirCharListHeredocLineWhitespaceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCharListHeredocLineWhitespace(this);
    else super.accept(visitor);
  }

  @NotNull
  public ASTNode excessWhitespace(int prefixLength) {
    return ElixirPsiImplUtil.excessWhitespace(this, prefixLength);
  }

}
