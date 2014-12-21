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

public class ElixirMatchedExpressionAndTailExpressionOperationImpl extends ElixirMatchedExpressionOrTailExpressionOperationImpl implements ElixirMatchedExpressionAndTailExpressionOperation {

  public ElixirMatchedExpressionAndTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionAndTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAndInfixOperator getAndInfixOperator() {
    return findChildByClass(ElixirAndInfixOperator.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionAndMatchedExpressionOperation getMatchedExpressionAndMatchedExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionAndMatchedExpressionOperation.class);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionAndTailExpressionOperation getMatchedExpressionAndTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionAndTailExpressionOperation.class);
  }

}
