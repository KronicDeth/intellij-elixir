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

public class ElixirMatchedExpressionArrowExpressionOperationImpl extends ElixirMatchedExpressionRelationalExpressionOperationImpl implements ElixirMatchedExpressionArrowExpressionOperation {

  public ElixirMatchedExpressionArrowExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionArrowExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirArrowInfixOperator getArrowInfixOperator() {
    return findChildByClass(ElixirArrowInfixOperator.class);
  }

  @Override
  @NotNull
  public List<ElixirCaptureExpressionOperation> getCaptureExpressionOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCaptureExpressionOperation.class);
  }

}
