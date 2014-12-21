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

public class ElixirMatchedExpressionWhenTailExpressionOperationImpl extends ElixirMatchedExpressionInMatchTailExpressionOperationImpl implements ElixirMatchedExpressionWhenTailExpressionOperation {

  public ElixirMatchedExpressionWhenTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionWhenTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionTypeMatchedExpressionOperation getMatchedExpressionTypeMatchedExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionTypeMatchedExpressionOperation.class);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionTypeTailExpressionOperation getMatchedExpressionTypeTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionTypeTailExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirWhenInfixOperator getWhenInfixOperator() {
    return findChildByClass(ElixirWhenInfixOperator.class);
  }

}
