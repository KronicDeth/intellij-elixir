// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

public class ElixirUnmatchedQualifiedAliasImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedQualifiedAlias {

  public ElixirUnmatchedQualifiedAliasImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedQualifiedAlias(this);
  }

  @Override
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

  @Override
  public @Nullable String fullyQualifiedName() {
    return ElixirPsiImplUtil.fullyQualifiedName(this);
  }

  @Override
  public @NotNull String getName() {
    return ElixirPsiImplUtil.getName(this);
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return ElixirPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public @Nullable ItemPresentation getPresentation() {
    return ElixirPsiImplUtil.getPresentation(this);
  }

  @Override
  public @Nullable PsiPolyVariantReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
  }

  @Override
  public boolean isModuleName() {
    return ElixirPsiImplUtil.isModuleName(this);
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  public @NotNull PsiElement setName(@NotNull String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
