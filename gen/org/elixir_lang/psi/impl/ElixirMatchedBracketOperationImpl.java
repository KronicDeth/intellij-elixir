// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirBracketArguments;
import org.elixir_lang.psi.ElixirMatchedBracketOperation;
import org.elixir_lang.psi.ElixirMatchedExpression;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirMatchedBracketOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedBracketOperation {

  public ElixirMatchedBracketOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitMatchedBracketOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirBracketArguments getBracketArguments() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirBracketArguments.class));
  }

  @Override
  @NotNull
  public ElixirMatchedExpression getMatchedExpression() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMatchedExpression.class));
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
