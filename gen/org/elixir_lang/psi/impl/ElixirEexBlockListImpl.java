// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirEexBlockItem;
import org.elixir_lang.psi.ElixirEexBlockList;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirEexBlockListImpl extends ASTWrapperPsiElement implements ElixirEexBlockList {

  public ElixirEexBlockListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitEexBlockList(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirEexBlockItem> getEexBlockItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEexBlockItem.class);
  }

}
