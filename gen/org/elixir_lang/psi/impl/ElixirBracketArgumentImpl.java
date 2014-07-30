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

public class ElixirBracketArgumentImpl extends ASTWrapperPsiElement implements ElixirBracketArgument {

  public ElixirBracketArgumentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBracketArgument(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirCloseBracket getCloseBracket() {
    return findNotNullChildByClass(ElixirCloseBracket.class);
  }

  @Override
  @Nullable
  public ElixirContainerExpression getContainerExpression() {
    return findChildByClass(ElixirContainerExpression.class);
  }

  @Override
  @Nullable
  public ElixirKeyword getKeyword() {
    return findChildByClass(ElixirKeyword.class);
  }

  @Override
  @NotNull
  public ElixirOpenBracket getOpenBracket() {
    return findNotNullChildByClass(ElixirOpenBracket.class);
  }

}
