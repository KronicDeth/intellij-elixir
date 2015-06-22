// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirBlockExpression;
import org.elixir_lang.psi.ElixirCaptureBlockOperation;
import org.elixir_lang.psi.ElixirCapturePrefixOperator;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirCaptureBlockOperationImpl extends ASTWrapperPsiElement implements ElixirCaptureBlockOperation {

  public ElixirCaptureBlockOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCaptureBlockOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirBlockExpression getBlockExpression() {
    return findNotNullChildByClass(ElixirBlockExpression.class);
  }

  @Override
  @NotNull
  public ElixirCapturePrefixOperator getCapturePrefixOperator() {
    return findNotNullChildByClass(ElixirCapturePrefixOperator.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
