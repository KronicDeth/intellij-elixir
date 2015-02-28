// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirNoParenthesesKeywords;
import org.elixir_lang.psi.ElixirNoParenthesesKeywordsExpression;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirNoParenthesesKeywordsImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesKeywords {

  public ElixirNoParenthesesKeywordsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesKeywords(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirNoParenthesesKeywordsExpression> getNoParenthesesKeywordsExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesKeywordsExpression.class);
  }

}
