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

public class ElixirMatchedExpressionInMatchTailExpressionOperationImpl extends ElixirCaptureTailExpressionOperationImpl implements ElixirMatchedExpressionInMatchTailExpressionOperation {

  public ElixirMatchedExpressionInMatchTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionInMatchTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirInMatchInfixOperator getInMatchInfixOperator() {
    return findChildByClass(ElixirInMatchInfixOperator.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionInMatchMatchedExpressionOperation getMatchedExpressionInMatchMatchedExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionInMatchMatchedExpressionOperation.class);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionWhenTailExpressionOperation getMatchedExpressionWhenTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionWhenTailExpressionOperation.class);
  }

}
