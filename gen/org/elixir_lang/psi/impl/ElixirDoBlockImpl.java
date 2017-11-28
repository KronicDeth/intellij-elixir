// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirDoBlockImpl extends ASTWrapperPsiElement implements ElixirDoBlock {

  public ElixirDoBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitDoBlock(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirBlockList getBlockList() {
    return PsiTreeUtil.getChildOfType(this, ElixirBlockList.class);
  }

  @Override
  @Nullable
  public ElixirEexBlockList getEexBlockList() {
    return PsiTreeUtil.getChildOfType(this, ElixirEexBlockList.class);
  }

  @Override
  @Nullable
  public ElixirEexStab getEexStab() {
    return PsiTreeUtil.getChildOfType(this, ElixirEexStab.class);
  }

  @Override
  @NotNull
  public List<ElixirEndOfExpression> getEndOfExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEndOfExpression.class);
  }

  @Override
  @Nullable
  public ElixirStab getStab() {
    return PsiTreeUtil.getChildOfType(this, ElixirStab.class);
  }

  @NotNull
  public OtpErlangObject[] quoteArguments() {
    return ElixirPsiImplUtil.quoteArguments(this);
  }

}
