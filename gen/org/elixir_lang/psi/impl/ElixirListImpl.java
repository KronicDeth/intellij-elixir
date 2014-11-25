// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirKeywordPair;
import org.elixir_lang.psi.ElixirList;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirListImpl extends ElixirValueImpl implements ElixirList {

  public ElixirListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitList(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirKeywordPair> getKeywordPairList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirKeywordPair.class);
  }

}
