// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;

public class ElixirQuoteHexadecimalEscapeSequenceImpl extends ASTWrapperPsiElement implements ElixirQuoteHexadecimalEscapeSequence {

  public ElixirQuoteHexadecimalEscapeSequenceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitQuoteHexadecimalEscapeSequence(this);
  }

  @Override
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

  @Override
  public int codePoint() {
    return ElixirPsiImplUtil.codePoint(this);
  }

}
