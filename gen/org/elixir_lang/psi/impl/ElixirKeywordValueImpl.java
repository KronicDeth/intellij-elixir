// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirEmptyParentheses;
import org.elixir_lang.psi.ElixirKeywordValue;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirKeywordValueImpl extends ASTWrapperPsiElement implements ElixirKeywordValue {

  public ElixirKeywordValueImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitKeywordValue(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findNotNullChildByClass(ElixirEmptyParentheses.class);
  }

}
