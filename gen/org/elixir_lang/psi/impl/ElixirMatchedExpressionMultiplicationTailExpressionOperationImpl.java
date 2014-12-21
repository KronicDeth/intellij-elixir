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

public class ElixirMatchedExpressionMultiplicationTailExpressionOperationImpl extends ElixirMatchedExpressionAdditionTailExpressionOperationImpl implements ElixirMatchedExpressionMultiplicationTailExpressionOperation {

  public ElixirMatchedExpressionMultiplicationTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionMultiplicationTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionAndTailExpressionOperation getMatchedExpressionAndTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionAndTailExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionMultiplicationMatchedExpressionOperation getMatchedExpressionMultiplicationMatchedExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionMultiplicationMatchedExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirMultiplicationInfixOperator getMultiplicationInfixOperator() {
    return findChildByClass(ElixirMultiplicationInfixOperator.class);
  }

}
