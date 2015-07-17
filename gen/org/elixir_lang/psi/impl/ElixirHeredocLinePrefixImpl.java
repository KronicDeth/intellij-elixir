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
import com.intellij.psi.tree.IElementType;

public class ElixirHeredocLinePrefixImpl extends ASTWrapperPsiElement implements ElixirHeredocLinePrefix {

  public ElixirHeredocLinePrefixImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitHeredocLinePrefix(this);
    else super.accept(visitor);
  }

  @Nullable
  public ASTNode excessWhitespace(IElementType fragmentType, int prefixLength) {
    return ElixirPsiImplUtil.excessWhitespace(this, fragmentType, prefixLength);
  }

}
