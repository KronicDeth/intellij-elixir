// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ElixirVisitor extends PsiElementVisitor {

  public void visitAtom(@NotNull ElixirAtom o) {
    visitExpression(o);
  }

  public void visitBinaryOperation(@NotNull ElixirBinaryOperation o) {
    visitExpression(o);
  }

  public void visitCharList(@NotNull ElixirCharList o) {
    visitPsiElement(o);
  }

  public void visitCharListHeredoc(@NotNull ElixirCharListHeredoc o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull ElixirExpression o) {
    visitPsiElement(o);
  }

  public void visitHatOperation(@NotNull ElixirHatOperation o) {
    visitBinaryOperation(o);
  }

  public void visitInterpolation(@NotNull ElixirInterpolation o) {
    visitPsiElement(o);
  }

  public void visitMultiplicationOperation(@NotNull ElixirMultiplicationOperation o) {
    visitBinaryOperation(o);
  }

  public void visitSigil(@NotNull ElixirSigil o) {
    visitPsiElement(o);
  }

  public void visitString(@NotNull ElixirString o) {
    visitPsiElement(o);
  }

  public void visitStringHeredoc(@NotNull ElixirStringHeredoc o) {
    visitPsiElement(o);
  }

  public void visitUnaryOperation(@NotNull ElixirUnaryOperation o) {
    visitExpression(o);
  }

  public void visitValue(@NotNull ElixirValue o) {
    visitExpression(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
