// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirNoParenthesesKeywordPairImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesKeywordPair {

  public ElixirNoParenthesesKeywordPairImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitNoParenthesesKeywordPair(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return PsiTreeUtil.getChildOfType(this, ElixirEmptyParentheses.class);
  }

  @Override
  @NotNull
  public ElixirKeywordKey getKeywordKey() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirKeywordKey.class));
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return PsiTreeUtil.getChildOfType(this, ElixirMatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyStrictNoParenthesesExpression getNoParenthesesManyStrictNoParenthesesExpression() {
    return PsiTreeUtil.getChildOfType(this, ElixirNoParenthesesManyStrictNoParenthesesExpression.class);
  }

  @Override
  @NotNull
  public Quotable getKeywordValue() {
    return ElixirPsiImplUtil.getKeywordValue(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
