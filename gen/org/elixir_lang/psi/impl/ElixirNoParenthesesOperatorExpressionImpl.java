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

public class ElixirNoParenthesesOperatorExpressionImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesOperatorExpression {

  public ElixirNoParenthesesOperatorExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesOperatorExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCallArgumentsNoParenthesesKeyword getCallArgumentsNoParenthesesKeyword() {
    return findChildByClass(ElixirCallArgumentsNoParenthesesKeyword.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesExpression getNoParenthesesExpression() {
    return findChildByClass(ElixirNoParenthesesExpression.class);
  }

  @Override
  @Nullable
  public ElixirOperatorEOL getOperatorEOL() {
    return findChildByClass(ElixirOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirWhenOperatorEOL getWhenOperatorEOL() {
    return findChildByClass(ElixirWhenOperatorEOL.class);
  }

}
