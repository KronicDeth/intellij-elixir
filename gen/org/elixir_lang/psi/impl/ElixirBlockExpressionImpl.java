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

public class ElixirBlockExpressionImpl extends ASTWrapperPsiElement implements ElixirBlockExpression {

  public ElixirBlockExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBlockExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCallArgumentsNoParenthesesAll getCallArgumentsNoParenthesesAll() {
    return findChildByClass(ElixirCallArgumentsNoParenthesesAll.class);
  }

  @Override
  @NotNull
  public List<ElixirCallArgumentsParentheses> getCallArgumentsParenthesesList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCallArgumentsParentheses.class);
  }

  @Override
  @NotNull
  public ElixirDoBlock getDoBlock() {
    return findNotNullChildByClass(ElixirDoBlock.class);
  }

  @Override
  @Nullable
  public ElixirDotDoIdentifier getDotDoIdentifier() {
    return findChildByClass(ElixirDotDoIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirDotIdentifier getDotIdentifier() {
    return findChildByClass(ElixirDotIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirParenthesesCall getParenthesesCall() {
    return findChildByClass(ElixirParenthesesCall.class);
  }

}
