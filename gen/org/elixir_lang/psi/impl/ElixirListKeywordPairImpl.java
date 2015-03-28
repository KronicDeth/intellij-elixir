// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirKeywordKey;
import org.elixir_lang.psi.ElixirKeywordValue;
import org.elixir_lang.psi.ElixirListKeywordPair;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirListKeywordPairImpl extends ASTWrapperPsiElement implements ElixirListKeywordPair {

  public ElixirListKeywordPairImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitListKeywordPair(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirKeywordKey getKeywordKey() {
    return findNotNullChildByClass(ElixirKeywordKey.class);
  }

  @Override
  @NotNull
  public ElixirKeywordValue getKeywordValue() {
    return findNotNullChildByClass(ElixirKeywordValue.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
