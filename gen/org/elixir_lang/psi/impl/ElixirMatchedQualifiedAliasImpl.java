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

public class ElixirMatchedQualifiedAliasImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedQualifiedAlias {

  public ElixirMatchedQualifiedAliasImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitMatchedQualifiedAlias(this);
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
  public ElixirMatchedExpression getMatchedExpression() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMatchedExpression.class));
  }

  @Override
  @Nullable
  public String fullyQualifiedName() {
    return ElixirPsiImplUtil.fullyQualifiedName(this);
  }

  @Override
  @NotNull
  public String getName() {
    return ElixirPsiImplUtil.getName((QualifiedAlias) this);
  }

  @Override
  @Nullable
  public PsiElement getNameIdentifier() {
    return ElixirPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  @Nullable
  public ItemPresentation getPresentation() {
    return ElixirPsiImplUtil.getPresentation(this);
  }

  @Override
  @Nullable
  public PsiPolyVariantReference getReference() {
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
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  @NotNull
  public PsiElement setName(@NotNull String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
