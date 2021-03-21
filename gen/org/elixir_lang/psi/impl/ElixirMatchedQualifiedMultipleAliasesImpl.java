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

public class ElixirMatchedQualifiedMultipleAliasesImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedQualifiedMultipleAliases {

  public ElixirMatchedQualifiedMultipleAliasesImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitMatchedQualifiedMultipleAliases(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirDotInfixOperator getDotInfixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDotInfixOperator.class));
  }

  @Override
  @NotNull
  public ElixirMatchedExpression getMatchedExpression() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMatchedExpression.class));
  }

  @Override
  @NotNull
  public ElixirMultipleAliases getMultipleAliases() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMultipleAliases.class));
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
