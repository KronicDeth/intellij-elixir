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

public class ElixirUnaryTailExpressionPrefixOperationImpl extends ASTWrapperPsiElement implements ElixirUnaryTailExpressionPrefixOperation {

  public ElixirUnaryTailExpressionPrefixOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnaryTailExpressionPrefixOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCaptureTailExpressionPrefixOperation getCaptureTailExpressionPrefixOperation() {
    return findChildByClass(ElixirCaptureTailExpressionPrefixOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpressionHatTailExpressionOperation getMatchedExpressionHatTailExpressionOperation() {
    return findChildByClass(ElixirMatchedExpressionHatTailExpressionOperation.class);
  }

  @Override
  @NotNull
  public ElixirUnaryPrefixOperator getUnaryPrefixOperator() {
    return findNotNullChildByClass(ElixirUnaryPrefixOperator.class);
  }

}
