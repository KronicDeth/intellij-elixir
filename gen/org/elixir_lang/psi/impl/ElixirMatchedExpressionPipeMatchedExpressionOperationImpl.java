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

public class ElixirMatchedExpressionPipeMatchedExpressionOperationImpl extends ElixirMatchedExpressionTypeMatchedExpressionOperationImpl implements ElixirMatchedExpressionPipeMatchedExpressionOperation {

  public ElixirMatchedExpressionPipeMatchedExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionPipeMatchedExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirMatchedExpressionPipeMatchedExpressionOperation> getMatchedExpressionPipeMatchedExpressionOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedExpressionPipeMatchedExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirPipeInfixOperator getPipeInfixOperator() {
    return findChildByClass(ElixirPipeInfixOperator.class);
  }

}
