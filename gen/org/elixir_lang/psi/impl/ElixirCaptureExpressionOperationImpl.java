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

public class ElixirCaptureExpressionOperationImpl extends ASTWrapperPsiElement implements ElixirCaptureExpressionOperation {

  public ElixirCaptureExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCaptureExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCaptureExpressionOperation getCaptureExpressionOperation() {
    return findChildByClass(ElixirCaptureExpressionOperation.class);
  }

  @Override
  @Nullable
  public ElixirCaptureExpressionPrefixOperation getCaptureExpressionPrefixOperation() {
    return findChildByClass(ElixirCaptureExpressionPrefixOperation.class);
  }

  @Override
  @Nullable
  public ElixirCapturePrefixOperator getCapturePrefixOperator() {
    return findChildByClass(ElixirCapturePrefixOperator.class);
  }

}
