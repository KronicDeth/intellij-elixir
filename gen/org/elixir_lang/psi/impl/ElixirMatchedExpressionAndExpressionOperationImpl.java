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

public class ElixirMatchedExpressionAndExpressionOperationImpl extends ElixirMatchedExpressionOrExpressionOperationImpl implements ElixirMatchedExpressionAndExpressionOperation {

  public ElixirMatchedExpressionAndExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionAndExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAndInfixOperator getAndInfixOperator() {
    return findChildByClass(ElixirAndInfixOperator.class);
  }

  @Override
  @NotNull
  public List<ElixirCaptureExpressionOperation> getCaptureExpressionOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCaptureExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirCaptureExpressionPrefixOperation getCaptureExpressionPrefixOperation() {
    return findChildByClass(ElixirCaptureExpressionPrefixOperation.class);
  }

}
