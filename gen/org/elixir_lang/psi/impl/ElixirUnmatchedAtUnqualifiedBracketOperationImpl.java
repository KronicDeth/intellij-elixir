// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirUnmatchedAtUnqualifiedBracketOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedAtUnqualifiedBracketOperation {

  public ElixirUnmatchedAtUnqualifiedBracketOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedAtUnqualifiedBracketOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirAtPrefixOperator.class));
  }

  @Override
  @NotNull
  public ElixirBracketArguments getBracketArguments() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirBracketArguments.class));
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
