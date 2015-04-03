// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirEnclosedHexadecimalEscapeSequence;
import org.elixir_lang.psi.ElixirHexadecimalEscapeSequence;
import org.elixir_lang.psi.ElixirOpenHexadecimalEscapeSequence;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirHexadecimalEscapeSequenceImpl extends ASTWrapperPsiElement implements ElixirHexadecimalEscapeSequence {

  public ElixirHexadecimalEscapeSequenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitHexadecimalEscapeSequence(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence() {
    return findChildByClass(ElixirEnclosedHexadecimalEscapeSequence.class);
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
