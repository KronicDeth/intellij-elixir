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

public class ElixirMatchedExpressionInMatchMatchedExpressionOperationImpl extends ElixirMatchedExpressionInMatchedExpressionOperationImpl implements ElixirMatchedExpressionInMatchMatchedExpressionOperation {

  public ElixirMatchedExpressionInMatchMatchedExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionInMatchMatchedExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirCaptureExpressionOperation> getCaptureExpressionOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCaptureExpressionOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirInMatchInfixOperator> getInMatchInfixOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInMatchInfixOperator.class);
  }

}
