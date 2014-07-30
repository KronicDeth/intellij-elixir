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

public class ElixirBracketExpressionImpl extends ASTWrapperPsiElement implements ElixirBracketExpression {

  public ElixirBracketExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBracketExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAccessExpression getAccessExpression() {
    return findChildByClass(ElixirAccessExpression.class);
  }

  @Override
  @NotNull
  public ElixirBracketArgument getBracketArgument() {
    return findNotNullChildByClass(ElixirBracketArgument.class);
  }

  @Override
  @Nullable
  public ElixirDotBracketIdentifier getDotBracketIdentifier() {
    return findChildByClass(ElixirDotBracketIdentifier.class);
  }

}
