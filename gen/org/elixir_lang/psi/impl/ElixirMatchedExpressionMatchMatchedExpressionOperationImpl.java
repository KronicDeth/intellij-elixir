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

public class ElixirMatchedExpressionMatchMatchedExpressionOperationImpl extends ElixirMatchedExpressionPipeMatchedExpressionOperationImpl implements ElixirMatchedExpressionMatchMatchedExpressionOperation {

  public ElixirMatchedExpressionMatchMatchedExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionMatchMatchedExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMatchInfixOperator getMatchInfixOperator() {
    return findChildByClass(ElixirMatchInfixOperator.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedExpressionMatchMatchedExpressionOperation> getMatchedExpressionMatchMatchedExpressionOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedExpressionMatchMatchedExpressionOperation.class);
  }

}
