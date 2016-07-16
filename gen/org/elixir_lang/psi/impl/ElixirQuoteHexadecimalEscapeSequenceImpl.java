// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirQuoteHexadecimalEscapeSequenceImpl extends ASTWrapperPsiElement implements ElixirQuoteHexadecimalEscapeSequence {

  public ElixirQuoteHexadecimalEscapeSequenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitQuoteHexadecimalEscapeSequence(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence() {
    return PsiTreeUtil.getChildOfType(this, ElixirEnclosedHexadecimalEscapeSequence.class);
  }

  @Override
  @NotNull
  public ElixirHexadecimalEscapePrefix getHexadecimalEscapePrefix() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirHexadecimalEscapePrefix.class));
  }

  @Override
  @Nullable
  public ElixirOpenHexadecimalEscapeSequence getOpenHexadecimalEscapeSequence() {
    return PsiTreeUtil.getChildOfType(this, ElixirOpenHexadecimalEscapeSequence.class);
  }

  public int codePoint() {
    return ElixirPsiImplUtil.codePoint(this);
  }

}
