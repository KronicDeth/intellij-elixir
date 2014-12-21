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

public class ElixirMatchedExpressionPipeTailExpressionOperationImpl extends ElixirMatchedExpressionTypeTailExpressionOperationImpl implements ElixirMatchedExpressionPipeTailExpressionOperation {

  public ElixirMatchedExpressionPipeTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionPipeTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionMatchMatchedExpressionOperation getMatchedExpressionMatchMatchedExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionMatchMatchedExpressionOperation.class);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionMatchTailExpressionOperation getMatchedExpressionMatchTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionMatchTailExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirPipeInfixOperator getPipeInfixOperator() {
    return findChildByClass(ElixirPipeInfixOperator.class);
  }

}
