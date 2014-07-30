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

public class ElixirListImpl extends ASTWrapperPsiElement implements ElixirList {

  public ElixirListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitList(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCloseBracket getCloseBracket() {
    return findChildByClass(ElixirCloseBracket.class);
  }

  @Override
  @Nullable
  public ElixirListArguments getListArguments() {
    return findChildByClass(ElixirListArguments.class);
  }

  @Override
  @NotNull
  public ElixirOpenBracket getOpenBracket() {
    return findNotNullChildByClass(ElixirOpenBracket.class);
  }

}
