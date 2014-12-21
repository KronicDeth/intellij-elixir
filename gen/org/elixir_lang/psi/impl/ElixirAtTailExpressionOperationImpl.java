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

public class ElixirAtTailExpressionOperationImpl extends ElixirUnaryTailExpressionOperationImpl implements ElixirAtTailExpressionOperation {

  public ElixirAtTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAtTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return findChildByClass(ElixirAtPrefixOperator.class);
  }

  @Override
  @Nullable
  public ElixirCaptureTailExpressionPrefixOperation getCaptureTailExpressionPrefixOperation() {
    return findChildByClass(ElixirCaptureTailExpressionPrefixOperation.class);
  }

  @Override
  @Nullable
  public ElixirTailExpression getTailExpression() {
    return findChildByClass(ElixirTailExpression.class);
  }

  @Override
  @Nullable
  public ElixirUnaryTailExpressionPrefixOperation getUnaryTailExpressionPrefixOperation() {
    return findChildByClass(ElixirUnaryTailExpressionPrefixOperation.class);
  }

}
