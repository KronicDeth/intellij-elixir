// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirDoBlock;
import org.elixir_lang.psi.ElixirMatchedUnqualifiedNoArgumentsCall;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirMatchedUnqualifiedNoArgumentsCallImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedUnqualifiedNoArgumentsCall {

  public ElixirMatchedUnqualifiedNoArgumentsCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedUnqualifiedNoArgumentsCall(this);
    else super.accept(visitor);
  }

  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
