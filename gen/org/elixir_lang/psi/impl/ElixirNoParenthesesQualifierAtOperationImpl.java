// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;

public class ElixirNoParenthesesQualifierAtOperationImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesQualifierAtOperation {

  public ElixirNoParenthesesQualifierAtOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesQualifierAtOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionAccessExpression getMatchedExpressionAccessExpression() {
    return findChildByClass(ElixirMatchedExpressionAccessExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesQualifierAtOperation getNoParenthesesQualifierAtOperation() {
    return findChildByClass(ElixirNoParenthesesQualifierAtOperation.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesQualifierNumberAtOperation getNoParenthesesQualifierNumberAtOperation() {
    return findChildByClass(ElixirNoParenthesesQualifierNumberAtOperation.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesQualifierNumberCaptureOperation getNoParenthesesQualifierNumberCaptureOperation() {
    return findChildByClass(ElixirNoParenthesesQualifierNumberCaptureOperation.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesQualifierNumberUnaryOperation getNoParenthesesQualifierNumberUnaryOperation() {
    return findChildByClass(ElixirNoParenthesesQualifierNumberUnaryOperation.class);
  }

}
