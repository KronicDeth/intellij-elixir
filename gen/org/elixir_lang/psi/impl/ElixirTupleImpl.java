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

public class ElixirTupleImpl extends ASTWrapperPsiElement implements ElixirTuple {

  public ElixirTupleImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitTuple(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCloseCurly getCloseCurly() {
    return findChildByClass(ElixirCloseCurly.class);
  }

  @Override
  @Nullable
  public ElixirContainerArguments getContainerArguments() {
    return findChildByClass(ElixirContainerArguments.class);
  }

  @Override
  @NotNull
  public ElixirOpenCurly getOpenCurly() {
    return findNotNullChildByClass(ElixirOpenCurly.class);
  }

}
