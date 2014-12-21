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

public class ElixirMatchedExpressionRelationalTailExpressionOperationImpl extends ElixirMatchedExpressionComparisonTailExpressionOperationImpl implements ElixirMatchedExpressionRelationalTailExpressionOperation {

  public ElixirMatchedExpressionRelationalTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionRelationalTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionAndTailExpressionOperation getMatchedExpressionAndTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionAndTailExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionRelationalMatchedExpressionOperation getMatchedExpressionRelationalMatchedExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionRelationalMatchedExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirRelationalInfixOperator getRelationalInfixOperator() {
    return findChildByClass(ElixirRelationalInfixOperator.class);
  }

}
