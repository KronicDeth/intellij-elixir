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

public class ElixirMatchedExpressionAndNoParenthesesExpressionOperationImpl extends ElixirNoParenthesesExpressionImpl implements ElixirMatchedExpressionAndNoParenthesesExpressionOperation {

  public ElixirMatchedExpressionAndNoParenthesesExpressionOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpressionAndNoParenthesesExpressionOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirExpression.class);
  }

}
