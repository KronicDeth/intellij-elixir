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

public class ElixirStructOperationImpl extends ASTWrapperPsiElement implements ElixirStructOperation {

  public ElixirStructOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitStructOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAlias getAlias() {
    return PsiTreeUtil.getChildOfType(this, ElixirAlias.class);
  }

  @Override
  @Nullable
  public ElixirAtom getAtom() {
    return PsiTreeUtil.getChildOfType(this, ElixirAtom.class);
  }

  @Override
  @NotNull
  public ElixirMapArguments getMapArguments() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMapArguments.class));
  }

  @Override
  @NotNull
  public ElixirMapPrefixOperator getMapPrefixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMapPrefixOperator.class));
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return PsiTreeUtil.getChildOfType(this, ElixirMatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirVariable getVariable() {
    return PsiTreeUtil.getChildOfType(this, ElixirVariable.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
