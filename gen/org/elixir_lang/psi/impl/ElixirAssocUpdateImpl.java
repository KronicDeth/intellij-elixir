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

public class ElixirAssocUpdateImpl extends ASTWrapperPsiElement implements ElixirAssocUpdate {

  public ElixirAssocUpdateImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAssocUpdate(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAssocOperatorEOL getAssocOperatorEOL() {
    return findChildByClass(ElixirAssocOperatorEOL.class);
  }

  @Override
  @NotNull
  public List<ElixirExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirExpression.class);
  }

  @Override
  @Nullable
  public ElixirMapExpression getMapExpression() {
    return findChildByClass(ElixirMapExpression.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedExpression> getMatchedExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedExpression.class);
  }

  @Override
  @NotNull
  public ElixirPipeOperatorEOL getPipeOperatorEOL() {
    return findNotNullChildByClass(ElixirPipeOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return findChildByClass(ElixirUnmatchedExpression.class);
  }

}
