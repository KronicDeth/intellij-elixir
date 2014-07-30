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

public class ElixirMapExpressionImpl extends ASTWrapperPsiElement implements ElixirMapExpression {

  public ElixirMapExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMapExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAtOperatorEOL getAtOperatorEOL() {
    return findChildByClass(ElixirAtOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirDotIdentifier getDotIdentifier() {
    return findChildByClass(ElixirDotIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirMapExpression getMapExpression() {
    return findChildByClass(ElixirMapExpression.class);
  }

  @Override
  @Nullable
  public ElixirMaxExpression getMaxExpression() {
    return findChildByClass(ElixirMaxExpression.class);
  }

}
