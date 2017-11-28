// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirEexStab;
import org.elixir_lang.psi.ElixirEexStabBody;
import org.elixir_lang.psi.ElixirEexStabOperation;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirEexStabImpl extends ASTWrapperPsiElement implements ElixirEexStab {

  public ElixirEexStabImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitEexStab(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEexStabBody getEexStabBody() {
    return PsiTreeUtil.getChildOfType(this, ElixirEexStabBody.class);
  }

  @Override
  @NotNull
  public List<ElixirEexStabOperation> getEexStabOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEexStabOperation.class);
  }

}
