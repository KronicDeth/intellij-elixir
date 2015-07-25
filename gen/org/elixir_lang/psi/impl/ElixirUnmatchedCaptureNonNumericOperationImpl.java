// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirCapturePrefixOperator;
import org.elixir_lang.psi.ElixirUnmatchedCaptureNonNumericOperation;
import org.elixir_lang.psi.ElixirUnmatchedExpression;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnmatchedCaptureNonNumericOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedCaptureNonNumericOperation {

  public ElixirUnmatchedCaptureNonNumericOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedCaptureNonNumericOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirCapturePrefixOperator getCapturePrefixOperator() {
    return findNotNullChildByClass(ElixirCapturePrefixOperator.class);
  }

  @Override
  @Nullable
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return findChildByClass(ElixirUnmatchedExpression.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
