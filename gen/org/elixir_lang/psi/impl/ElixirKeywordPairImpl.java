// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;

public class ElixirKeywordPairImpl extends ASTWrapperPsiElement implements ElixirKeywordPair {

  public ElixirKeywordPairImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitKeywordPair(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findNotNullChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @NotNull
  public ElixirKeywordKey getKeywordKey() {
    return findNotNullChildByClass(ElixirKeywordKey.class);
  }

  public Quotable getKeywordValue() {
    return ElixirPsiImplUtil.getKeywordValue(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
