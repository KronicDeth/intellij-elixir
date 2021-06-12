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
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirStructOperationImpl extends ASTWrapperPsiElement implements ElixirStructOperation {

  public ElixirStructOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitStructOperation(this);
  }

  @Override
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

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
