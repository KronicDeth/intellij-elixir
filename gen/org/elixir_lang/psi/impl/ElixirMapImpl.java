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

public class ElixirMapImpl extends ASTWrapperPsiElement implements ElixirMap {

  public ElixirMapImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMap(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMapArguments getMapArguments() {
    return findNotNullChildByClass(ElixirMapArguments.class);
  }

  @Override
  @Nullable
  public ElixirMapExpression getMapExpression() {
    return findChildByClass(ElixirMapExpression.class);
  }

  @Override
  @Nullable
  public ElixirMapOperator getMapOperator() {
    return findChildByClass(ElixirMapOperator.class);
  }

  @Override
  @Nullable
  public ElixirStructOperator getStructOperator() {
    return findChildByClass(ElixirStructOperator.class);
  }

}
