// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirMatchedParenthesesArguments;
import org.elixir_lang.psi.ElixirParenthesesArguments;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirMatchedParenthesesArgumentsImpl extends ASTWrapperPsiElement implements ElixirMatchedParenthesesArguments {

  public ElixirMatchedParenthesesArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedParenthesesArguments(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirParenthesesArguments> getParenthesesArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirParenthesesArguments.class);
  }

}
