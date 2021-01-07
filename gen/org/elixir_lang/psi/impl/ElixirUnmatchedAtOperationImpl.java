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
import com.intellij.psi.PsiReference;

public class ElixirUnmatchedAtOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedAtOperation {

  public ElixirUnmatchedAtOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedAtOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirAtPrefixOperator.class));
  }

  @Override
  @Nullable
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return PsiTreeUtil.getChildOfType(this, ElixirUnmatchedExpression.class);
  }

  @Override
  @Nullable
  public PsiReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
  }

  @Override
  @NotNull
  public String moduleAttributeName() {
    return ElixirPsiImplUtil.moduleAttributeName(this);
  }

  @Override
  @Nullable
  public Quotable operand() {
    return ElixirPsiImplUtil.operand(this);
  }

  @Override
  @NotNull
  public Operator operator() {
    return ElixirPsiImplUtil.operator(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
