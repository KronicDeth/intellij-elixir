// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirMatchedExpression;
import org.elixir_lang.psi.ElixirMatchedMultiplicationOperation;
import org.elixir_lang.psi.ElixirMultiplicationInfixOperator;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirMatchedMultiplicationOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedMultiplicationOperation {

  public ElixirMatchedMultiplicationOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedMultiplicationOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirMatchedExpression> getMatchedExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedExpression.class);
  }

  @Override
  @NotNull
  public ElixirMultiplicationInfixOperator getMultiplicationInfixOperator() {
    return findNotNullChildByClass(ElixirMultiplicationInfixOperator.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
