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

public class ElixirStabEOLImpl extends ASTWrapperPsiElement implements ElixirStabEOL {

  public ElixirStabEOLImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStabEOL(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCallArgumentsNoParenthesesAll getCallArgumentsNoParenthesesAll() {
    return findChildByClass(ElixirCallArgumentsNoParenthesesAll.class);
  }

  @Override
  @Nullable
  public ElixirExpression getExpression() {
    return findChildByClass(ElixirExpression.class);
  }

  @Override
  @Nullable
  public ElixirStabMaybeExpression getStabMaybeExpression() {
    return findChildByClass(ElixirStabMaybeExpression.class);
  }

  @Override
  @Nullable
  public ElixirStabOperatorEOL getStabOperatorEOL() {
    return findChildByClass(ElixirStabOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirStabParenthesesMany getStabParenthesesMany() {
    return findChildByClass(ElixirStabParenthesesMany.class);
  }

}
