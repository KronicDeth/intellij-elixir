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

public class ElixirUnmatchedExpressionImpl extends ASTWrapperPsiElement implements ElixirUnmatchedExpression {

  public ElixirUnmatchedExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAtOperatorEOL getAtOperatorEOL() {
    return findChildByClass(ElixirAtOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirBlockExpression getBlockExpression() {
    return findChildByClass(ElixirBlockExpression.class);
  }

  @Override
  @Nullable
  public ElixirCaptureOperatorEOL getCaptureOperatorEOL() {
    return findChildByClass(ElixirCaptureOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @Nullable
  public ElixirExpression getExpression() {
    return findChildByClass(ElixirExpression.class);
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return findChildByClass(ElixirMatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirOperatorExpression getOperatorExpression() {
    return findChildByClass(ElixirOperatorExpression.class);
  }

  @Override
  @Nullable
  public ElixirUnaryOperatorEOL getUnaryOperatorEOL() {
    return findChildByClass(ElixirUnaryOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return findChildByClass(ElixirUnmatchedExpression.class);
  }

}
