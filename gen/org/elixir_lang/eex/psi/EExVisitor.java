// This is a generated file. Not intended for manual editing.
package org.elixir_lang.eex.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class EExVisitor extends PsiElementVisitor {

  public void visitCommentTag(@NotNull EExCommentTag o) {
    visitPsiElement(o);
  }

  public void visitElixirTag(@NotNull EExElixirTag o) {
    visitPsiElement(o);
  }

  public void visitMarker(@NotNull EExMarker o) {
    visitPsiElement(o);
  }

  public void visitQuotationTag(@NotNull EExQuotationTag o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
