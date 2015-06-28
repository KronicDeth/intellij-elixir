// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirHatInfixOperator;
import org.elixir_lang.psi.ElixirUnmatchedExpression;
import org.elixir_lang.psi.ElixirUnmatchedHatOperation;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirUnmatchedHatOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedHatOperation {

  public ElixirUnmatchedHatOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedHatOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirHatInfixOperator getHatInfixOperator() {
    return findNotNullChildByClass(ElixirHatInfixOperator.class);
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
