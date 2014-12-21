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

public class ElixirMatchedExpressionAdditionMatchedExpressionOperationImpl extends ElixirMatchedExpressionTwoMatchedExpressionOperationImpl implements ElixirMatchedExpressionAdditionMatchedExpressionOperation {

  public ElixirMatchedExpressionAdditionMatchedExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionAdditionMatchedExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirAdditionInfixOperator> getAdditionInfixOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirAdditionInfixOperator.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedExpressionMultiplicationMatchedExpressionOperation> getMatchedExpressionMultiplicationMatchedExpressionOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedExpressionMultiplicationMatchedExpressionOperation.class);
  }

}
