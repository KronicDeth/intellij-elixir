// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;

public class ElixirAtBracketOperationImpl extends ASTWrapperPsiElement implements ElixirAtBracketOperation {

  public ElixirAtBracketOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAtBracketOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAccessExpression getAccessExpression() {
    return findNotNullChildByClass(ElixirAccessExpression.class);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return findNotNullChildByClass(ElixirAtPrefixOperator.class);
  }

  @Override
  @NotNull
  public ElixirBracketArguments getBracketArguments() {
    return findNotNullChildByClass(ElixirBracketArguments.class);
  }

}
