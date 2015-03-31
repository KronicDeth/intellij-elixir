// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirHatInfixOperator;
import org.elixir_lang.psi.ElixirMatchedExpression;
import org.elixir_lang.psi.ElixirMatchedHatOperation;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirMatchedHatOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedHatOperation {

  public ElixirMatchedHatOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedHatOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirHatInfixOperator getHatInfixOperator() {
    return findNotNullChildByClass(ElixirHatInfixOperator.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedExpression> getMatchedExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedExpression.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
