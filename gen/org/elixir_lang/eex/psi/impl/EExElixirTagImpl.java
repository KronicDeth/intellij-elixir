// This is a generated file. Not intended for manual editing.
package org.elixir_lang.eex.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.eex.psi.EExElixirTag;
import org.elixir_lang.eex.psi.EExMarker;
import org.elixir_lang.eex.psi.EExVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EExElixirTagImpl extends ASTWrapperPsiElement implements EExElixirTag {

  public EExElixirTagImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EExVisitor visitor) {
    visitor.visitElixirTag(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EExVisitor) accept((EExVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EExMarker getMarker() {
    return findChildByClass(EExMarker.class);
  }

}
