// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirMatchedCallArguments;
import org.elixir_lang.psi.ElixirMatchedUnqualifiedCallOperation;
import org.elixir_lang.psi.ElixirVariable;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirMatchedUnqualifiedCallOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedUnqualifiedCallOperation {

  public ElixirMatchedUnqualifiedCallOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedUnqualifiedCallOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMatchedCallArguments getMatchedCallArguments() {
    return findNotNullChildByClass(ElixirMatchedCallArguments.class);
  }

  @Override
  @NotNull
  public ElixirVariable getVariable() {
    return findNotNullChildByClass(ElixirVariable.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
