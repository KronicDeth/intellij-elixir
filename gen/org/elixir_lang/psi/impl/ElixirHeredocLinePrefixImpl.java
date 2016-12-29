// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirHeredocLinePrefix;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirHeredocLinePrefixImpl extends ASTWrapperPsiElement implements ElixirHeredocLinePrefix {

  public ElixirHeredocLinePrefixImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitHeredocLinePrefix(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Nullable
  public ASTNode excessWhitespace(IElementType type, int prefixLength) {
    return ElixirPsiImplUtil.excessWhitespace(this, type, prefixLength);
  }

}
