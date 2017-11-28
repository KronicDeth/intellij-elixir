// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirEexStabOperationImpl extends ASTWrapperPsiElement implements ElixirEexStabOperation {

  public ElixirEexStabOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitEexStabOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEexStabBody getEexStabBody() {
    return PsiTreeUtil.getChildOfType(this, ElixirEexStabBody.class);
  }

  @Override
  @NotNull
  public ElixirStabInfixOperator getStabInfixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirStabInfixOperator.class));
  }

  @Override
  @Nullable
  public ElixirStabNoParenthesesSignature getStabNoParenthesesSignature() {
    return PsiTreeUtil.getChildOfType(this, ElixirStabNoParenthesesSignature.class);
  }

  @Override
  @Nullable
  public ElixirStabParenthesesSignature getStabParenthesesSignature() {
    return PsiTreeUtil.getChildOfType(this, ElixirStabParenthesesSignature.class);
  }

}
