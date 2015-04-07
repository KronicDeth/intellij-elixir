// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirSigilHexadecimalEscapeSequenceImpl extends ASTWrapperPsiElement implements ElixirSigilHexadecimalEscapeSequence {

  public ElixirSigilHexadecimalEscapeSequenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitSigilHexadecimalEscapeSequence(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence() {
    return findChildByClass(ElixirEnclosedHexadecimalEscapeSequence.class);
  }

  @Override
  @NotNull
  public ElixirHexadecimalEscapePrefix getHexadecimalEscapePrefix() {
    return findNotNullChildByClass(ElixirHexadecimalEscapePrefix.class);
  }

  @Override
  @Nullable
  public ElixirOpenHexadecimalEscapeSequence getOpenHexadecimalEscapeSequence() {
    return findChildByClass(ElixirOpenHexadecimalEscapeSequence.class);
  }

  public int codePoint() {
    return ElixirPsiImplUtil.codePoint(this);
  }

}
