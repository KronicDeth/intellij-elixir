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

public class ElixirAtMatchedExpressionOperationImpl extends ElixirUnaryMatchedExpressionOperationImpl implements ElixirAtMatchedExpressionOperation {

  public ElixirAtMatchedExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAtMatchedExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return findChildByClass(ElixirAtPrefixOperator.class);
  }

  @Override
  @Nullable
  public ElixirCaptureMatchedExpressionPrefixOperation getCaptureMatchedExpressionPrefixOperation() {
    return findChildByClass(ElixirCaptureMatchedExpressionPrefixOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return findChildByClass(ElixirMatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirUnaryMatchedExpressionPrefixOperation getUnaryMatchedExpressionPrefixOperation() {
    return findChildByClass(ElixirUnaryMatchedExpressionPrefixOperation.class);
  }

}
