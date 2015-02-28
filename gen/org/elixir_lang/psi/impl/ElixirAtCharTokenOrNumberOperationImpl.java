// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirAtCharTokenOrNumberOperation;
import org.elixir_lang.psi.ElixirAtPrefixOperator;
import org.elixir_lang.psi.ElixirNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirAtCharTokenOrNumberOperationImpl extends ASTWrapperPsiElement implements ElixirAtCharTokenOrNumberOperation {

  public ElixirAtCharTokenOrNumberOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAtCharTokenOrNumberOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return findNotNullChildByClass(ElixirAtPrefixOperator.class);
  }

  @Override
  @Nullable
  public ElixirNumber getNumber() {
    return findChildByClass(ElixirNumber.class);
  }

}
