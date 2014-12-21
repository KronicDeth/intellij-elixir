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

public class ElixirCaptureTailExpressionOperationImpl extends ASTWrapperPsiElement implements ElixirCaptureTailExpressionOperation {

  public ElixirCaptureTailExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCaptureTailExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCapturePrefixOperator getCapturePrefixOperator() {
    return findChildByClass(ElixirCapturePrefixOperator.class);
  }

  @Override
  @NotNull
  public ElixirMatchedExpressionInMatchTailExpressionOperation getMatchedExpressionInMatchTailExpressionOperation() {
    return findNotNullChildByClass(ElixirMatchedExpressionInMatchTailExpressionOperation.class);
  }

}
