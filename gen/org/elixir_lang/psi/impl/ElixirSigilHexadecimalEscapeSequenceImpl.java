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
