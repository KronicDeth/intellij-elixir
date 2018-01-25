// This is a generated file. Not intended for manual editing.
package org.elixir_lang.beam.assembly.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitOperands(@NotNull Operands o) {
    visitPsiElement(o);
  }

  public void visitOperation(@NotNull Operation o) {
    visitPsiElement(o);
  }

  public void visitTerm(@NotNull Term o) {
    visitPsiElement(o);
  }

  public void visitValues(@NotNull Values o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
