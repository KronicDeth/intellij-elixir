// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirMatchedExpressionNoParenthesesExpressionAtOperation;
import org.elixir_lang.psi.ElixirNoParenthesesExpression;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirMatchedExpressionNoParenthesesExpressionAtOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedExpressionNoParenthesesExpressionAtOperation {

  public ElixirMatchedExpressionNoParenthesesExpressionAtOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionNoParenthesesExpressionAtOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesExpression getNoParenthesesExpression() {
    return findNotNullChildByClass(ElixirNoParenthesesExpression.class);
  }

}
