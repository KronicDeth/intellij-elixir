// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirNoParenthesesKeywordPairImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesKeywordPair {

  public ElixirNoParenthesesKeywordPairImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitNoParenthesesKeywordPair(this);
  }

  @Override
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
  public @NotNull Quotable getKeywordValue() {
    return ElixirPsiImplUtil.getKeywordValue(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
