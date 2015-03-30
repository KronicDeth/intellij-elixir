// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirEmptyBlock;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirEmptyBlockImpl extends ElixirMatchedExpressionImpl implements ElixirEmptyBlock {

  public ElixirEmptyBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitEmptyBlock(this);
    else super.accept(visitor);
  }

  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
