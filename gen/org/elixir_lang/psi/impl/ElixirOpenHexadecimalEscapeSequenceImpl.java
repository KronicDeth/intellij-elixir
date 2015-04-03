// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirOpenHexadecimalEscapeSequence;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.psi.ElixirTypes.VALID_HEXADECIMAL_DIGITS;

public class ElixirOpenHexadecimalEscapeSequenceImpl extends ASTWrapperPsiElement implements ElixirOpenHexadecimalEscapeSequence {

  public ElixirOpenHexadecimalEscapeSequenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitOpenHexadecimalEscapeSequence(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getValidHexadecimalDigits() {
    return findNotNullChildByType(VALID_HEXADECIMAL_DIGITS);
  }

  public int codePoint() {
    return ElixirPsiImplUtil.codePoint(this);
  }

}
