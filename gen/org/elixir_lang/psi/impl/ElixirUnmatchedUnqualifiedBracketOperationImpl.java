// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirBracketArguments;
import org.elixir_lang.psi.ElixirIdentifier;
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedBracketOperation;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirUnmatchedUnqualifiedBracketOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedUnqualifiedBracketOperation {

  public ElixirUnmatchedUnqualifiedBracketOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedUnqualifiedBracketOperation(this);
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
  public ElixirIdentifier getIdentifier() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirIdentifier.class));
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
