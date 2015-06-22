// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirBlockExpression;
import org.elixir_lang.psi.ElixirUnaryBlockOperation;
import org.elixir_lang.psi.ElixirUnaryPrefixOperator;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirUnaryBlockOperationImpl extends ASTWrapperPsiElement implements ElixirUnaryBlockOperation {

  public ElixirUnaryBlockOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnaryBlockOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirBlockExpression getBlockExpression() {
    return findNotNullChildByClass(ElixirBlockExpression.class);
  }

  @Override
  @NotNull
  public ElixirUnaryPrefixOperator getUnaryPrefixOperator() {
    return findNotNullChildByClass(ElixirUnaryPrefixOperator.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
