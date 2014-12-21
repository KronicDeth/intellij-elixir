// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import org.elixir_lang.psi.*;

public class ElixirMatchedExpressionTypeTailExpressionOperationImpl extends ElixirMatchedExpressionWhenTailExpressionOperationImpl implements ElixirMatchedExpressionTypeTailExpressionOperation {

  public ElixirMatchedExpressionTypeTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionTypeTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionPipeMatchedExpressionOperation getMatchedExpressionPipeMatchedExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionPipeMatchedExpressionOperation.class);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionPipeTailExpressionOperation getMatchedExpressionPipeTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionPipeTailExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirTypeInfixOperator getTypeInfixOperator() {
    return findChildByClass(ElixirTypeInfixOperator.class);
  }

}
