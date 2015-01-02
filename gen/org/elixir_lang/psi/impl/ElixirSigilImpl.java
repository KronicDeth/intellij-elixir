// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirInterpolatedStringBody;
import org.elixir_lang.psi.ElixirInterpolation;
import org.elixir_lang.psi.ElixirSigil;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirSigilImpl extends ASTWrapperPsiElement implements ElixirSigil {

  public ElixirSigilImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitSigil(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirInterpolatedStringBody getInterpolatedStringBody() {
    return findChildByClass(ElixirInterpolatedStringBody.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolation> getInterpolationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolation.class);
  }

}
