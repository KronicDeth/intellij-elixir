// This is a generated file. Not intended for manual editing.
package org.elixir_lang.eex.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.eex.psi.EExMarker;
import org.elixir_lang.eex.psi.EExVisitor;
import org.jetbrains.annotations.NotNull;

public class EExMarkerImpl extends ASTWrapperPsiElement implements EExMarker {

  public EExMarkerImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EExVisitor visitor) {
    visitor.visitMarker(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EExVisitor) accept((EExVisitor)visitor);
    else super.accept(visitor);
  }

}
