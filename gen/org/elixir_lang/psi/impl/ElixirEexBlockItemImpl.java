// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirEexBlockIdentifier;
import org.elixir_lang.psi.ElixirEexBlockItem;
import org.elixir_lang.psi.ElixirEexStab;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirEexBlockItemImpl extends ASTWrapperPsiElement implements ElixirEexBlockItem {

  public ElixirEexBlockItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitEexBlockItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirEexBlockIdentifier getEexBlockIdentifier() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirEexBlockIdentifier.class));
  }

  @Override
  @NotNull
  public ElixirEexStab getEexStab() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirEexStab.class));
  }

}
