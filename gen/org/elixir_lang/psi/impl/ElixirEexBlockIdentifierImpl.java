// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirBlockIdentifier;
import org.elixir_lang.psi.ElixirEexBlockIdentifier;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirEexBlockIdentifierImpl extends ASTWrapperPsiElement implements ElixirEexBlockIdentifier {

  public ElixirEexBlockIdentifierImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitEexBlockIdentifier(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirBlockIdentifier getBlockIdentifier() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirBlockIdentifier.class));
  }

}
