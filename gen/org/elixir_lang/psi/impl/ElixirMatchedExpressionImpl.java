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

public class ElixirMatchedExpressionImpl extends ASTWrapperPsiElement implements ElixirMatchedExpression {

  public ElixirMatchedExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAccessExpression getAccessExpression() {
    return findChildByClass(ElixirAccessExpression.class);
  }

  @Override
  @Nullable
  public ElixirAtOperatorEOL getAtOperatorEOL() {
    return findChildByClass(ElixirAtOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirCaptureOperatorEOL getCaptureOperatorEOL() {
    return findChildByClass(ElixirCaptureOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return findChildByClass(ElixirMatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirMatchedOperatorExpression getMatchedOperatorExpression() {
    return findChildByClass(ElixirMatchedOperatorExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesExpression getNoParenthesesExpression() {
    return findChildByClass(ElixirNoParenthesesExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesOneExpression getNoParenthesesOneExpression() {
    return findChildByClass(ElixirNoParenthesesOneExpression.class);
  }

  @Override
  @Nullable
  public ElixirUnaryOperatorEOL getUnaryOperatorEOL() {
    return findChildByClass(ElixirUnaryOperatorEOL.class);
  }

}
