// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirComparisonInfixOperator;
import org.elixir_lang.psi.ElixirUnmatchedComparisonOperation;
import org.elixir_lang.psi.ElixirUnmatchedExpression;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirUnmatchedComparisonOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedComparisonOperation {

  public ElixirUnmatchedComparisonOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedComparisonOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirComparisonInfixOperator getComparisonInfixOperator() {
    return findNotNullChildByClass(ElixirComparisonInfixOperator.class);
  }

  @Override
  @NotNull
  public List<ElixirUnmatchedExpression> getUnmatchedExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirUnmatchedExpression.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
