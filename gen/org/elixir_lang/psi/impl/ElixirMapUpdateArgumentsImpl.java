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

public class ElixirMapUpdateArgumentsImpl extends ASTWrapperPsiElement implements ElixirMapUpdateArguments {

  public ElixirMapUpdateArgumentsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitMapUpdateArguments(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAssociations getAssociations() {
    return PsiTreeUtil.getChildOfType(this, ElixirAssociations.class);
  }

  @Override
  @Nullable
  public ElixirAssociationsBase getAssociationsBase() {
    return PsiTreeUtil.getChildOfType(this, ElixirAssociationsBase.class);
  }

  @Override
  @Nullable
  public ElixirKeywords getKeywords() {
    return PsiTreeUtil.getChildOfType(this, ElixirKeywords.class);
  }

  @Override
  @NotNull
  public ElixirMatchedMatchOperation getMatchedMatchOperation() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMatchedMatchOperation.class));
  }

  @Override
  @NotNull
  public ElixirPipeInfixOperator getPipeInfixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirPipeInfixOperator.class));
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
