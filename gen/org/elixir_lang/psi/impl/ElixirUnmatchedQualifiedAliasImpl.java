// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnmatchedQualifiedAliasImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedQualifiedAlias {

  public ElixirUnmatchedQualifiedAliasImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedQualifiedAlias(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAlias getAlias() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirAlias.class));
  }

  @Override
  @NotNull
  public ElixirDotInfixOperator getDotInfixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDotInfixOperator.class));
  }

  @Override
  @NotNull
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirUnmatchedExpression.class));
  }

  @Nullable
  public String fullyQualifiedName() {
    return ElixirPsiImplUtil.fullyQualifiedName(this);
  }

  @NotNull
  public String getName() {
    return ElixirPsiImplUtil.getName(this);
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return ElixirPsiImplUtil.getNameIdentifier(this);
  }

  @Nullable
  public ItemPresentation getPresentation() {
    return ElixirPsiImplUtil.getPresentation(this);
  }

  @Nullable
  public PsiReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
  }

  @Nullable
  public PsiPolyVariantReference getReference(@NotNull PsiElement maxScope) {
    return ElixirPsiImplUtil.getReference(this, maxScope);
  }

  public boolean isModuleName() {
    return ElixirPsiImplUtil.isModuleName(this);
  }

  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @NotNull
  public PsiElement setName(@NotNull String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
