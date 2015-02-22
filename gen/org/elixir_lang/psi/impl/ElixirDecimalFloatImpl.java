// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirDecimalFloat;
import org.elixir_lang.psi.ElixirDecimalWholeNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirDecimalFloatImpl extends ElixirDecimalNumberImpl implements ElixirDecimalFloat {

  public ElixirDecimalFloatImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitDecimalFloat(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirDecimalWholeNumber> getDecimalWholeNumberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirDecimalWholeNumber.class);
  }

}
