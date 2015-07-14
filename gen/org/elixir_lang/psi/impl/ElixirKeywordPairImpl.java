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

public class ElixirKeywordPairImpl extends ASTWrapperPsiElement implements ElixirKeywordPair {

  public ElixirKeywordPairImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitKeywordPair(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @NotNull
  public ElixirKeywordKey getKeywordKey() {
    return findNotNullChildByClass(ElixirKeywordKey.class);
  }

  @Override
  @Nullable
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return findChildByClass(ElixirUnmatchedExpression.class);
  }

  public Quotable getKeywordValue() {
    return ElixirPsiImplUtil.getKeywordValue(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
